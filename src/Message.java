import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.text.*;

public class Message {
    /* The headers and the body of the message. */
    public String Headers = "";
    public String Body = "";

    /* Sender and recipient. With these, we don't need to extract them
       from the headers. */
    private String From = "";
    private String To = "";

    /* To make it look nicer */
    private static final String CRLF = "\r\n";
    private static final String Boundary = "X-X-X-myboundary-X-X-X";
    private static final String BoundaryDelimiter = "--";

    /* Create the message object by inserting the required headers from
       RFC 822 (From, To, Date). */
    public Message(String from, String to, String subject, String text) {
        this(from, to, subject, text, null);
    }

    public Message(String from, String to, String subject, String text, Path imagePath) {
        /* Remove whitespace */
        From = from.trim();
        To = to.trim();
        Headers = "From: " + From + CRLF;
        Headers += "To: " + To + CRLF;
        Headers += "Subject: " + subject.trim() + CRLF;

        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'");
        String dateString = format.format(new Date());
        Headers += "Date: " + dateString + CRLF;

        // To enable attachments
        Headers += "MIME-Version: 1.0" + CRLF;
        Headers += "Content-Type: multipart/mixed; boundary=\"" + Boundary + "\"" + CRLF;

        Body += BoundaryDelimiter + Boundary + CRLF;
        Body += "Content-Type: text/plain" + CRLF + CRLF;

        Body += text + CRLF + CRLF;
        if (imagePath != null) {
            try {
                String filename = String.valueOf(imagePath.getFileName());
                String encodedImage = Base64Encoder.encode(imagePath);

                Body += BoundaryDelimiter + Boundary + CRLF;
                Body += "Content-Type: image/jpeg; name=" + filename + CRLF;
                Body += "Content-Transfer-Encoding: base64" + CRLF;
                Body += "Content-Disposition: attachment; filename=" + filename + CRLF + CRLF;

                Body += encodedImage + CRLF + CRLF;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Body += BoundaryDelimiter + Boundary + BoundaryDelimiter + CRLF;

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
        return res;
    }

    public static void main(String[] args) {
        var m = new Message("hank@example.com", "reciever@other.place", "this is subject", "this is content", Path.of("../dog.jpeg"));
        System.out.println(m);
    }
}
