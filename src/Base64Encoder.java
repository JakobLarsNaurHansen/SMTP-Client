import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.io.*;

public class Base64Encoder {
    public static String encode(Path path) throws IOException {
        byte[] imageBytes = Files.readAllBytes(path);
        return encode(imageBytes);
    }

    public static String encode(String string) {
        return encode(string.getBytes());
    }

    private static String encode(byte[] bytes) {
        return new String(Base64.getMimeEncoder().encode(bytes), StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        String result = encode("../dog.jpeg".getBytes());
        System.out.println(result);

    }
}
