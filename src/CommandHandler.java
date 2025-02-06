import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.isRegularFile;


public class CommandHandler {
    public static void handlerEncrypt() throws IOException {
        String filePath;
        int keyCrypt = 0;
        System.out.println("Введите адрес текстового " +
                "файла для шифрования.");
        do{
            filePath = ScannerUtil.getScanner().nextLine();
            if (!isRegularFile(Path.of(filePath))){
                System.out.println("Файл не найден, попробуйте ввести адрес повторно.");
            }

        } while (!isRegularFile(Path.of(filePath)));

        System.out.println("Введите ключь для шифрования файла.");
        do {
            if (!ScannerUtil.getScanner().hasNextInt()){
                System.out.println("Неверный формат данных! Введите целое число.");
                ScannerUtil.getScanner().nextLine();
            } else {
                keyCrypt = ScannerUtil.getScanner().nextInt();
                if (keyCrypt < 1 || keyCrypt > 92) {
                    System.out.println("Нет такого пункта меню! Введите целое число [1..92].");
                }
            }
        } while (keyCrypt < 1 || keyCrypt > 92);

        ScannerUtil.getScanner().nextLine(); //Устраняю остаток строки \n оставшийся от nextInt

        System.out.println("Вы ввели адрес:\n" + filePath + "\nВы ввели ключь шифрования:\n" + keyCrypt);


        FileManager.readFile(filePath, keyCrypt);

    }

    public static void handlerDecrypt() throws IOException {
        String filePath;
        int keyCrypt = 0;
        System.out.println("Введите адрес текстового файла для дешифрования.");
        do{
            filePath = ScannerUtil.getScanner().nextLine();
            if (!isRegularFile(Path.of(filePath))){
                System.out.println("Файл не найден, попробуйте ввести адрес повторно.");
            }

        } while (!isRegularFile(Path.of(filePath)));

        System.out.println("Введите ключь для дешифрования файла.");
        do {
            if (!ScannerUtil.getScanner().hasNextInt()){
                System.out.println("Неверный формат данных! Введите целое число.");
                ScannerUtil.getScanner().nextLine();
            } else {
                keyCrypt = ScannerUtil.getScanner().nextInt();
                if (keyCrypt < 1 || keyCrypt > 92) {
                    System.out.println("Нет такого пункта меню! Введите целое число [1..92].");
                }
            }
        } while (keyCrypt < 0 || keyCrypt > 92);

        ScannerUtil.getScanner().nextLine(); //Устраняю остаток строки \n оставшийся от nextInt

        System.out.println("Вы ввели адрес:\n" + filePath + "\nВы ввели ключь шифрования:\n" + keyCrypt);

        FileManager.readFile(filePath, keyCrypt);
    }

    public static void bruteForce() throws IOException {
        String filePath;
        System.out.println("Введите адрес текстового файла для которого требуется взлом шифра.");
        do {
            filePath = ScannerUtil.getScanner().nextLine();
            if (!isRegularFile(Path.of(filePath))){
                System.out.println("Файл не найден, попробуйте ввести адрес повторно.");
            }
        } while (!isRegularFile(Path.of(filePath)));
        System.out.println("Вы ввели адрес:\n" + filePath);
        System.out.println("Начинаю взлом шифра.");
        BruteForce.keySelection(filePath);
    }


}
