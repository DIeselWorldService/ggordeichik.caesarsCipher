import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

public class Cipher {
    public static final List<Character> ALPHABET = List.of(
            // Заглавные буквы
            'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т',
            'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я',
            // Строчные буквы
            'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т',
            'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я',
            // Знаки препинания
            '.', ',', '!', '?', ';', ':', '—', '-', '(', ')', '[', ']', '{', '}', '"', '«', '»', '‘', '’', '“', '”', '…', '_',
            // Пробельные символы
            ' ', '\t', '\n', '\r'
    );

    public static ArrayList<Character> encrypt(ArrayList<Character> contentFile, int keyCrypt) {
        ArrayList<Character> encryptedContent = new ArrayList<>();
        for (char ch : contentFile) {
            int index = ALPHABET.indexOf(ch);
            if (index != -1) {
                int newIndex = (index + keyCrypt) % ALPHABET.size();
                encryptedContent.add(ALPHABET.get(newIndex));
            } else {
                encryptedContent.add(ch);
            }
        }
        return encryptedContent;
    }

    public static ArrayList<Character> decrypt(ArrayList<Character> contentFile, int keyCrypt) {
        ArrayList<Character> decryptedContent = new ArrayList<>();
        for (char ch : contentFile) {
            int index = ALPHABET.indexOf(ch);
            if (index != -1) {
                int newIndex = (index - keyCrypt + ALPHABET.size()) % ALPHABET.size();
                decryptedContent.add(ALPHABET.get(newIndex));
            } else {
                decryptedContent.add(ch);
            }
        }
        return decryptedContent;
    }

    public static void encryptInCharBuffer(CharBuffer charBuffer, int keyCrypt) {
        for (int i = 0; i < charBuffer.limit(); i++) {
            char original = charBuffer.get(i);
            int index = ALPHABET.indexOf(original);
            if (index != -1) {
                int newIndex = (index + keyCrypt) % ALPHABET.size();
                charBuffer.put(i, ALPHABET.get(newIndex));
            }
            // Символы, не входящие в ALPHABET, остаются без изменений
        }
    }

    public static void decryptInCharBuffer(CharBuffer charBuffer, int keyCrypt) {
        for (int i = 0; i < charBuffer.limit(); i++) {
            char original = charBuffer.get(i);
            int index = ALPHABET.indexOf(original);
            if (index != -1) {
                int newIndex = (index - keyCrypt + ALPHABET.size()) % ALPHABET.size();
                charBuffer.put(i, ALPHABET.get(newIndex));
            }
            // Символы, не входящие в ALPHABET, остаются без изменений
        }
    }
}