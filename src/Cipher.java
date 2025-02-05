import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Cipher {

    static final List<Character> ALPHABET = List.of(
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

    public static ArrayList<Character> encrypt(ArrayList<Character> originalText, int keyEncrypt) throws IOException {

        ArrayList<Character> originalTextFile = originalText;
        ArrayList<Character> encryptedTextFile = new ArrayList<>();

        for (int i = 0; i < originalTextFile.size(); i++) {
            int indexAlphabet = ALPHABET.indexOf(originalTextFile.get(i)) + keyEncrypt;
            if (indexAlphabet >= ALPHABET.size()) {
                indexAlphabet -= ALPHABET.size();
            }
            encryptedTextFile.add(i, ALPHABET.get(indexAlphabet));
        }
        return encryptedTextFile;

    }

    public static ArrayList<Character> decrypt(ArrayList<Character> encryptText, int keyDecrypt) throws IOException {
        ArrayList<Character> decryptedTextFile = encryptText;
        ArrayList<Character> originalTextFile = new ArrayList<>();

        for (int i = 0; i < decryptedTextFile.size(); i++) {
            int indexApphabet = ALPHABET.indexOf(decryptedTextFile.get(i)) - keyDecrypt;
            if (indexApphabet < 0) {
                indexApphabet += ALPHABET.size();
            }
            originalTextFile.add(i, ALPHABET.get(indexApphabet));
        }
        System.out.println(originalTextFile.toString());
        return originalTextFile;

    }
}
