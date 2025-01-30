import java.io.IOException;

public class Cipher {
    private char[] alphabet;
    public Cipher(char[] alphabet) {
        this.alphabet = alphabet;
    }
    public static void encrypt(String text, int keyCrypto) throws IOException {
        String originalText = FileManager.readFile(text);
        System.out.println("Original text: " + originalText);

    }
    public String decrypt(String encryptedText, int shift) {
        // Логика расшифровки
        return null;
    }
}
