import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Cipher {

    private static final List<Character> ALPHABET = List.of(
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

    public static void encrypt(String text, int keyCrypto) throws IOException {

        ArrayList<Character> textFileOriginal = FileManager.readFile(text);
        ArrayList<Character> encryptedTextFile = new ArrayList<>();
        for (int i = 0; i < textFileOriginal.size() - 1; i++) {
            int indexAlphabet = ALPHABET.indexOf(textFileOriginal.get(i)) + keyCrypto;
            if (indexAlphabet >= ALPHABET.size()) {
                indexAlphabet = indexAlphabet - ALPHABET.size();
            }
            encryptedTextFile.add(i, ALPHABET.get(indexAlphabet));
        }

        FileManager.writeFile(encryptedTextFile);

    }
    public String decrypt(String encryptedText, int shift) {
        // Логика расшифровки
        return null;
    }
}
