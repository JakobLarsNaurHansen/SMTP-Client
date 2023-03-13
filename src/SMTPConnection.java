import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.net.*;
import java.io.*;

/**
 * Open an SMTP connection to a mailserver and send one mail.
 */
public class SMTPConnection {
    /* The socket to the server */
    private Socket socket;

    /* Streams for reading and writing the socket */
    private BufferedReader fromServer;
    private OutputStream toServer;


    private static final String CRLF = "\r\n";

    /* Are we connected? Used in close() to determine what to do. */
    private boolean isConnected = false;

    /* Create an SMTPConnection object. Create the socket and the
       associated streams. Initialize SMTP connection. */
    public SMTPConnection(Envelope envelope) throws IOException {

        SocketFactory socketFactory = SSLSocketFactory.getDefault();
        this.socket = socketFactory.createSocket(envelope.DestHost, envelope.Port);

        this.fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.toServer = socket.getOutputStream();


//        fromServer = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
//        toServer = new DataOutputStream(sslSocket.getOutputStream());
        String reply = fromServer.readLine();
        if (parseReply(reply) != 220) {
            throw new IOException("Connection refused: " + reply);
        }
        sendCommand("EHLO " + envelope.DestHost, 250);
        sendAUTH("AUTH LOGIN", 334);
        sendCommand(Base64Encoder.encode(envelope.Username), 334);
        sendCommand(Base64Encoder.encode(envelope.Password), 235);
    }

    /* Send the message. Write the correct SMTP-commands in the
       correct order. No checking for errors, just throw them to the
       caller. */
    public void send(Envelope envelope) throws IOException {
        sendCommand("MAIL FROM: <" + envelope.Sender + ">", 250);
        sendCommand("RCPT TO: <" + envelope.Recipient + ">", 250);
        sendCommand("DATA", 354);
        toServer.write((envelope.Message.toString() + CRLF).getBytes());
        sendCommand(".", 250);
    }


    /* Close the connection. First, terminate on SMTP level, then
       close the socket. */
    public void close() {
        isConnected = false;
        try {
            sendCommand("QUIT", 221);
            socket.close();
        } catch (IOException e) {
            System.out.println("Unable to close connection: " + e);
            isConnected = true;
        }
    }

    /* Send an SMTP command to the server. Check that the reply code is
       what is is supposed to be according to RFC 821. */
    private void sendCommand(String command, int rc) throws IOException {
        toServer.write((command + CRLF).getBytes());
        String reply = fromServer.readLine();
        if (parseReply(reply) != rc) {
            throw new IOException(reply);
        }
    }

    private void sendAUTH(String command, int rc) throws IOException {
        toServer.write((command + CRLF).getBytes());

        String authHeaders = "";
        for (int i = 0; i <= 6; i++) {
            authHeaders += fromServer.readLine() + "\n";
        }
        String reply = fromServer.readLine();
        if (parseReply(reply) != rc) {
            throw new IOException(reply);
        }
    }


    /* Parse the reply line from the server. Returns the reply code. */
    private int parseReply(String reply) {
        return Integer.parseInt(reply.substring(0, 3));
    }


    /* Destructor. Closes the connection if something bad happens. */
    protected void finalize() throws Throwable {
        if (isConnected) {
            close();
        }
        super.finalize();
    }
}