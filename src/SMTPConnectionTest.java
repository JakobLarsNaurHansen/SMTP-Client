import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SMTPConnectionTest {

    @Test
    void sendMail() {
        try {
            Message message = new Message("testing@hank.com", "reciever@get-mail.com", "test-subject", "this is test content");
            Envelope envelope = new Envelope(message, "localhost");
            SMTPConnection smtpConnection = new SMTPConnection(envelope);
            smtpConnection.send(envelope);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // If message is sent, there should be no throwing of error.
        assertTrue(true);
    }

    @Test
    void sendImageMail() {
        try {
            Path p = Path.of("../dog.jpeg");
//            Message message = new Message("testing@hank.com", "reciever@get-mail.com", "test-subject", "this is test content", p);
//            Envelope envelope = new Envelope(message, "localhost");
            Message message = new Message("info@comit.dev", "s224281@student.dtu.dk", "test-subject", "this is test content", p);
            Envelope envelope = new Envelope(message, "datacomm.bhsi.xyz");
            SMTPConnection smtpConnection = new SMTPConnection(envelope);
            smtpConnection.send(envelope);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // If message is sent, there should be no throwing of error.
        assertTrue(true);
    }
}
// cat attachment.zip | base64 > zip.txt
// cat attachment.pdf | base64 > pdf.txt
//
// # Content-Type: text/csv; name="$FILE"                        # for CSV files
// # Content-Type: application/x-msdownload; name="$FILE"    # for executable
// # Content-Type: text/xml; name="$FILE"                        # for xml files or try application/xml
//
// telnet smtp.server.dom 25
//
// HELO
// MAIL FROM: email@server.com
// RCPT TO: email@server.com
// DATA
// Subject: Test email
// From: email@server.com
// To: email@server.com
// MIME-Version: 1.0
// Content-Type: multipart/mixed; boundary="X-=-=-=-text boundary"
//
// --X-=-=-=-text boundary
// Content-Type: text/plain
//
// Put your message here...
//
// --X-=-=-=-text boundary
// Content-Type: application/zip; name="file.zip"
// Content-Transfer-Encoding: base64
// Content-Disposition: attachment; filename="file.zip"
//
// UEsDBBQAAAAIAG1+zEoQa.... copy/paste zip.txt
//
// --X-=-=-=-text boundary
// Content-Type: text/pdf; name="file.pdf"
// Content-Transfer-Encoding: base64
// Content-Disposition: attachment; filename="file.pdf"
//
// UEsDBBQAAAAIAG1+zEoQa.... copy/paste pdf.txt
//
// --X-=-=-=-text boundary
// .
//
// QUIT