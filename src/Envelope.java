import java.net.*;
import java.util.*;

public class Envelope {
    /* SMTP-sender of the message (in this case, contents of From-header). */
    public String Sender;

    /* SMTP-recipient, or contents of To-header. */
    public String Recipient;

    public String Username;

    public String Password;

    /* Target MX-host */
    public String DestHost;
    public int Port;
    public InetAddress DestAddr;

    /* The actual message */
    public Message Message;

    /* Create the envelope. */
    public Envelope(Message message, String localServer, int port, String username, String password) throws UnknownHostException {
        /* Get sender and recipient. */
        String email = username;
        Username = email.substring(0, email .indexOf("@"));


        Password = password;
        Sender = message.getFrom();
        Recipient = message.getTo();


	/* Get message. We must escape the message to make sure that
	   there are no single periods on a line. This would mess up
	   sending the mail. */
        Message = escapeMessage(message);

        /* Take the name of the local mailserver and map it into an InetAddress */
        DestHost = localServer;
        this.Port = port;
        try {
            DestAddr = InetAddress.getByName(DestHost);
        } catch (UnknownHostException e) {
            System.out.println("Unknown host: " + DestHost);
            throw e;
        }
    }

    /* Escape the message by doubling all periods at the beginning of a line. */
    private Message escapeMessage(Message message) {
        StringBuilder escapedBody = new StringBuilder();
        String token;
        StringTokenizer parser = new StringTokenizer(message.Body, "\n", true);

        while (parser.hasMoreTokens()) {
            token = parser.nextToken();
            if (token.startsWith(".")) {
                token = "." + token;
            }
            escapedBody.append(token);
        }
        message.Body = escapedBody.toString();
        return message;
    }

    /* For printing the envelope. Only for debug. */
    public String toString() {
        String res = "Sender: " + Sender + '\n';
        res += "Recipient: " + Recipient + '\n';
        res += "MX-host: " + DestHost + ", address: " + DestAddr + '\n';
        res += "Message:" + '\n';
        res += Message.toString();

        return res;
    }
}
