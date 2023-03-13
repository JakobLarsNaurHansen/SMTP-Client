import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;

public class TestConnection {
    public TestConnection() throws IOException {
//        datacomm.bhsi.xyz
//        2526
        // 587

        // Connect to the server on port 587 and initiate the SSL handshake
        String host = "datacomm.bhsi.xyz";
        Socket socket = new Socket(host, 25);
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(socket, host, 587, true);

        // Start the conversation with the server
        var outputStream = new DataOutputStream(sslSocket.getOutputStream());
        var inputStream = new DataInputStream(sslSocket.getInputStream());

        sslSocket.startHandshake();
        byte[] buffer = new byte[1024];
        int bytesRead = inputStream.read(buffer);
        System.out.println(new String(buffer, 0, bytesRead));

        // Send the EHLO command to identify the client and retrieve a list of supported commands
        outputStream.write("EHLO example.com\r\n".getBytes());
        outputStream.flush();
        bytesRead = inputStream.read(buffer);
        System.out.println(new String(buffer, 0, bytesRead));

        // Send the MAIL FROM command to specify the email sender
        outputStream.write("MAIL FROM:<sender@example.com>\r\n".getBytes());
        outputStream.flush();
        bytesRead = inputStream.read(buffer);
        System.out.println(new String(buffer, 0, bytesRead));

        // Send one or more RCPT TO commands to specify the email recipients
        outputStream.write("RCPT TO:<recipient1@example.com>\r\n".getBytes());
        outputStream.flush();
        bytesRead = inputStream.read(buffer);
        System.out.println(new String(buffer, 0, bytesRead));

        outputStream.write("RCPT TO:<recipient2@example.com>\r\n".getBytes());
        outputStream.flush();
        bytesRead = inputStream.read(buffer);
        System.out.println(new String(buffer, 0, bytesRead));

        // Send the DATA command to indicate the start of the email message
        outputStream.write("DATA\r\n".getBytes());
        outputStream.flush();
        bytesRead = inputStream.read(buffer);
        System.out.println(new String(buffer, 0, bytesRead));

        // Send the email message itself, including headers and body
        String emailMessage = "Subject: Test email\r\n" +
                "From: sender@example.com\r\n" +
                "To: recipient1@example.com, recipient2@example.com\r\n" +
                "\r\n" +
                "This is a test email message.";
        outputStream.write(emailMessage.getBytes());
        outputStream.write(".\r\n".getBytes());
        outputStream.flush();
        bytesRead = inputStream.read(buffer);
        System.out.println(new String(buffer, 0, bytesRead));

        // Terminate the conversation with the server
        outputStream.write("QUIT\r\n".getBytes());
        outputStream.flush();
        bytesRead = inputStream.read(buffer);
        System.out.println(new String(buffer, 0, bytesRead));

        sslSocket.close();
    }

    public static void main(String[] args) {
        try {
            var a = new TestConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
