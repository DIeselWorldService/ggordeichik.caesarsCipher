import java.io.IOException;
import java.util.*;

public class BruteForce {
    static String filePath;
    static String checkedText;
    static int selectedKey = 0;
    static int mathCharsInFile;
    static HashMap<Integer, Integer> coincidenceCount = new HashMap<Integer, Integer>();
    static Set<Map.Entry<Integer, Integer>> entrySetCoincidenceCount;
    static int count = 0; //Счетчик для отладки
    static int zzzCount = 0;// отладка

    private static final List<String> COMMON_RUSSIAN_WORDS = Arrays.asList(
            " и ", " в ", " не ", " на ", " я ", " что ", " он ", " с ", " а ", " как ", " то ", " это ", " все ", " но ", " они ", " к ", " у ", " мы ", " за ", " вы ", " или "
    );

    public static void keySelection(String Path) throws IOException {
        selectedKey++;
        filePath = Path;
        if (selectedKey < Cipher.ALPHABET.size()) {
            FileManager.readFile(filePath, selectedKey);
        } else {
            int[] result = maxValueInHashMap();
            System.out.println("Перебор ключей выполнен. Возможных ключей - " + count);
            System.out.println("Чаров в строке: " + mathCharsInFile);
            UserMenu.menuBruteForce(result);
        }

    }
    /*
    В методе выше (keySelection) подбирая ключь если не вылезаем за пределы алфавита, то отправляемся в файл менеджер и от туда возвращаемся
    к методу ниже (decryptedTextByBruteForce) передав коллекцию чаров.
     */
    public static void decryptedTextByBruteForce(ArrayList<Character> checkedList) throws IOException {

        StringBuilder sb = new StringBuilder();
        for (Character ch : checkedList) {
            sb.append(ch);
        }
        checkedText = sb.toString();
        mathCharsInFile = checkedText.length();
        checkedString();
        keySelection(filePath);

    }

    public static boolean checkedString(){
        boolean result;
        String checkedTextLowerCase = checkedText.toLowerCase();
        int mathCount = 0;
        for (String word : COMMON_RUSSIAN_WORDS) {
            if (checkedTextLowerCase.contains(word.toLowerCase())) {
                mathCount++;
            }
        }

        int threshold = mathCharsInFile / 2500;
        if (mathCount >= threshold) {
            result = true;
            count++;
        } else {
            result = false;
        }
        //Все ключи с найденными совпадениями заносим в мапу для дальнейшей сортировки по значениям
        if (mathCount != 0) {
            coincidenceCount.put(selectedKey, mathCount);
            System.out.println("Для ключа: " + selectedKey + "Совпадений: " + mathCount);
        }
        return result;
    }

    public static int[] maxValueInHashMap() {
        int firstMaxValue = 0;
        int keyFirstMaxValue = 0;
        int secondMaxValue = 0;
        int keySecondMaxValue = 0;
        int thirdMaxValue = 0;
        int keyThirdMaxValue = 0;
        entrySetCoincidenceCount = coincidenceCount.entrySet();
        for (Map.Entry<Integer, Integer> entry : entrySetCoincidenceCount) {
                int key = entry.getKey();
                int value = entry.getValue();
                if (value > firstMaxValue) {
                 thirdMaxValue = secondMaxValue;
                    keyThirdMaxValue = keySecondMaxValue;
                    secondMaxValue = firstMaxValue;
                    keySecondMaxValue = keyFirstMaxValue;
                    firstMaxValue = value;
                    keyFirstMaxValue = key;
                } else if (value > secondMaxValue) {
                    thirdMaxValue = secondMaxValue;
                    keyThirdMaxValue = keySecondMaxValue;
                    secondMaxValue = value;
                    keySecondMaxValue = key;
                } else if (value > thirdMaxValue) {
                    thirdMaxValue = value;
                    keyThirdMaxValue = key;
                }
            }
        return new int[]{keyFirstMaxValue, keySecondMaxValue, keyThirdMaxValue};
        }

        public static void createdDecryptedFile(int selectedKey) throws IOException {
            System.out.println("KEY% " + selectedKey);
        FileManager.readFile(filePath, selectedKey);
        }

        public static void threePossibleKeys(int[] threeKeys) throws IOException {
        int keyFirstMaxValue = threeKeys[0];
        int keySecondMaxValue = threeKeys[1];
        int keyThirdMaxValue = threeKeys[2];
            System.out.println("Ключ: " +  keyFirstMaxValue + " | Количество совпадений: " + coincidenceCount.get(keyFirstMaxValue));
            System.out.println("Ключ: " + keySecondMaxValue + " | Количество совпадений: " + coincidenceCount.get(keySecondMaxValue));
            System.out.println("Ключ: " + keyThirdMaxValue + " | Количество совпадений: " + coincidenceCount.get(keyThirdMaxValue));
            UserMenu.menuBruteForce(threeKeys);
        }

        public static void likelyKeys() throws IOException {
            for (Map.Entry<Integer, Integer> entry : entrySetCoincidenceCount){
                System.out.println("Ключ: " + entry.getKey() + " | Количество совпадений: " + entry.getValue());
            }
            UserMenu.menu();
        }

    }
