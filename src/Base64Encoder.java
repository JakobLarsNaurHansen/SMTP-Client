import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.io.*;

public class Base64Encoder {
    public static String encodeBase64(String path) throws IOException {
        byte[] imageBytes = Files.readAllBytes(new File(path).toPath());
        return new String(Base64.getMimeEncoder().encode(imageBytes),StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        try {
            String result = encodeBase64("../dog.jpeg");
            System.out.println(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
