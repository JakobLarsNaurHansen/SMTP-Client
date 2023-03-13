import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.text.*;

/* $Id: Message.java,v 1.5 1999/07/22 12:10:57 kangasha Exp $ */

/**
 * Mail message.
 *
 * @author Jussi Kangasharju
 */
public class Message {
    /* The headers and the body of the message. */
    public String Headers;
    public String Body = "";

    /* Sender and recipient. With these, we don't need to extract them
       from the headers. */
    private String From;
    private String To;

    /* To make it look nicer */
    private static final String CRLF = "\r\n";

    private static final String Boundary = "X-=-=-=-boundary";

    /* Create the message object by inserting the required headers from
       RFC 822 (From, To, Date). */
    public Message(String from, String to, String subject, String text) {
        /* Remove whitespace */
        this(from, to, subject, text, null);
    }

    public Message(String from, String to, String subject, String text, Path imagePath) {
        From = from.trim();
        To = to.trim();
        Headers = "From: " + From + CRLF;
        Headers += "To: " + To + CRLF;
        Headers += "Subject: " + subject.trim() + CRLF;

	/* A close approximation of the required format. Unfortunately
	   only GMT. */
        SimpleDateFormat format =
                new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'");
        String dateString = format.format(new Date());
        Headers += "Date: " + dateString + CRLF;

        // To enable attachments
        Headers += "MIME-Version: 1.0" + CRLF;
        Headers += "Content-Type: multipart/mixed; boundary=\"" + Boundary + "\"" + CRLF;

        Body += "--X-=-=-=-boundary" + CRLF;
        Body += "Content-Type: text/plain; charset=\"UTF-8\"" + CRLF + CRLF;
//        Body += "Content-Type: text/plain" + CRLF + CRLF;

        Body += text + CRLF + CRLF;
        if (imagePath != null) {
            try {
                String filename = String.valueOf(imagePath.getFileName());
                String encodedImage = Base64Encoder.encodeBase64(imagePath.toString());

                Body += Boundary + CRLF;
                Body += "Content-Type: image/jpeg; name=" + filename + CRLF;
                Body += "Content-Transfer-Encoding: base64" + CRLF;
                Body += "Content-Disposition: attachment; filename=" + filename + CRLF + CRLF;
                Body += encodedImage + CRLF + CRLF;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // -- indicates last boundary
        Body += Boundary + "--" + CRLF;
    }

    /* Two functions to access the sender and recipient. */
    public String getFrom() {
        return From;
    }

    public String getTo() {
        return To;
    }

    /* Check whether the message is valid. In other words, check that
       both sender and recipient contain only one @-sign. */
    public boolean isValid() {
        int fromat = From.indexOf('@');
        int toat = To.indexOf('@');

        if (fromat < 1 || (From.length() - fromat) <= 1) {
            System.out.println("Sender address is invalid");
            return false;
        }
        if (toat < 1 || (To.length() - toat) <= 1) {
            System.out.println("Recipient address is invalid");
            return false;
        }
        if (fromat != From.lastIndexOf('@')) {
            System.out.println("Sender address is invalid");
            return false;
        }
        if (toat != To.lastIndexOf('@')) {
            System.out.println("Recipient address is invalid");
            return false;
        }
        return true;
    }

    /* For printing the message. */
    public String toString() {
        String res;

        res = Headers + CRLF;
        res += Body;
        System.out.println(res);
        return res;
    }

    public static void main(String[] args) {
        var m = new Message("hank@example.com", "reciever@other.place", "This is the subject", "this is the text", Path.of("../dog.jpeg"));
        System.out.println(m);
    }
}
