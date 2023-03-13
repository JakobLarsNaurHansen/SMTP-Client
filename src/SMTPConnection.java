import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Open an SMTP connection to a mailserver and send one mail.
 *
 */
public class SMTPConnection {
    /* The socket to the server */
    private Socket connection;

    /* Streams for reading and writing the socket */
    private BufferedReader fromServer;
    private DataOutputStream toServer;

    private static final int SMTP_PORT = 2526;
    private static final String CRLF = "\r\n";

    /* Are we connected? Used in close() to determine what to do. */
    private boolean isConnected = false;

    /* Create an SMTPConnection object. Create the socket and the
       associated streams. Initialize SMTP connection. */
    public SMTPConnection(Envelope envelope) throws IOException {
        /* Create a socket */
        Socket socket = new Socket("smtp.gmail.com", 587);

        /* Set up input and output streams */
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        toServer = new DataOutputStream(socket.getOutputStream());

        /* Wait for the server to send its banner message */
        String reply = fromServer.readLine();

        if (parseReply(reply) != 220) {
            throw new IOException("Connection refused: " + reply);
        }

        /* Send the EHLO command */
        //String localhost = InetAddress.getLocalHost().getHostName();
        sendCommand("EHLO localhost", 250);


        /* Start TLS session */
        sendCommand("STARTTLS", 250);

        /* Create a socket using SSL */
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        connection = sslSocketFactory.createSocket(socket, envelope.DestHost, 587, true);

        /* Set up input and output streams with TLS */
        fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        toServer = new DataOutputStream(connection.getOutputStream());


       //SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(socket, "smtp.gmail.com", 587, true);
        //sslSocket.startHandshake();
        /* Authenticate using username and password */
        sendCommand("AUTH LOGIN", 334);
        sendCommand(Base64.getEncoder().encodeToString(envelope.Username.getBytes()), 334);
        sendCommand(Base64.getEncoder().encodeToString(envelope.Password.getBytes()), 334);

        isConnected = true;
    }

    /* Send the message. Write the correct SMTP-commands in the
       correct order. No checking for errors, just throw them to the
       caller. */
    public void send(Envelope envelope) throws IOException {
        sendCommand("MAIL FROM: <" + envelope.Sender + ">", 250);
        sendCommand("RCPT TO: <" + envelope.Recipient + ">", 250);
        sendCommand("DATA", 354);
        toServer.writeBytes(envelope.Message.toString() + CRLF);
        sendCommand(".", 250);
    }


    /* Close the connection. First, terminate on SMTP level, then
       close the socket. */
    public void close() {
        isConnected = false;
        try {
            sendCommand("QUIT", 221);
            connection.close();
        } catch (IOException e) {
            System.out.println("Unable to close connection: " + e);
            isConnected = true;
        }
    }

    /* Send an SMTP command to the server. Check that the reply code is
       what is is supposed to be according to RFC 821. */
    private void sendCommand(String command, int rc) throws IOException {
        toServer.writeBytes(command + CRLF);
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
        if(isConnected) {
            close();
        }
        super.finalize();
    }
}