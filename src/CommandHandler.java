import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.isRegularFile;


public class CommandHandler {
    static boolean encryptFile;
    public static void handlerEncrypt() throws IOException {
        encryptFile = true;
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
            }
        } while (keyCrypt == 0);

        ScannerUtil.getScanner().nextLine(); //Устраняю остаток строки \n оставшийся от nextInt

        System.out.println("Вы ввели адрес:\n" + filePath + "\nВы ввели ключь шифрования:\n" + keyCrypt);


        FileManager.readFile(filePath, keyCrypt, encryptFile);

    }

    public static void handlerDecrypt() throws IOException {
        encryptFile = false;
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
            }
        } while (keyCrypt == 0);

        ScannerUtil.getScanner().nextLine(); //Устраняю остаток строки \n оставшийся от nextInt

        System.out.println("Вы ввели адрес:\n" + filePath + "\nВы ввели ключь шифрования:\n" + keyCrypt);

        FileManager.readFile(filePath, keyCrypt, encryptFile);
    }

    public static void bruteForce() {
        //В процессе
    }


}
