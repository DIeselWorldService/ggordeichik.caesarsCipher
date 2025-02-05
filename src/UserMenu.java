import org.w3c.dom.ls.LSOutput;
import state.UserState;
import state.State;

import java.io.IOException;

public class UserMenu {
    public static void menu() throws IOException {
        System.out.println("1 - Шифровать текстовый файл");
        System.out.println("2 - Дешифровать текстовый файл");
        System.out.println("3 - Brute Force");
        System.out.println("0 - Выход из программы");

        int numberMenu = -1;

        do {
            if (!ScannerUtil.getScanner().hasNextInt()) {
                System.out.println("Формат данных неверный. Введите целое число [0..3].");
                ScannerUtil.getScanner().nextLine();
            } else {
                numberMenu = ScannerUtil.getScanner().nextInt();
                if (numberMenu < 0 || numberMenu > 3) {
                    System.out.println("Нет такого пункта меню! Введите целое число [0..3].");
                }
            }
        } while (numberMenu < 0 || numberMenu > 3);

        ScannerUtil.getScanner().nextLine(); //Устраняю остаток строки \n оставшийся от nextInt

        System.out.println("Вы ввели: " + numberMenu);
        switch (numberMenu) {
            case 1 -> {
                UserState.setCurrentState(State.ENCRYPTED);
                CommandHandler.handlerEncrypt();
            }
            case 2 -> {
                UserState.setCurrentState(State.DECRYPTED);
                CommandHandler.handlerDecrypt();
            }
            case 3 -> {
                UserState.setCurrentState(State.BRUTE_FORCED);
                CommandHandler.bruteForce();
            }
            case 0 -> System.exit(0);

        }
    }
    public static void menuBruteForce(int[] threeKey) throws IOException {
        int numberMenu = 0;
        System.out.println("Выберете дальнейшее действие: ");
        System.out.println("1 - Создать наиболее вероятную расшифрованную копию файла.");
        System.out.println("2 - Узнать номера и количество совпадений по трем наиболее вероятным ключам.");
        System.out.println("3 - Просмотреть весь список ключей по которым найдены совпадения.");
        do {
            if (!ScannerUtil.getScanner().hasNextInt()) {
                System.out.println("Формат данных неверный. Введите целое число [1..3].");
                ScannerUtil.getScanner().nextLine();
            } else {
                numberMenu = ScannerUtil.getScanner().nextInt();
                if (numberMenu < 0 || numberMenu > 3) {
                    System.out.println("Нет такого пункта меню! Введите целое число [1..3].");
                }
            }
        } while (numberMenu < 1 || numberMenu > 3);
            switch (numberMenu){
                case 1 -> {UserState.setCurrentState(State.BRUTE_FORCED_DECRYPTED);
                    BruteForce.createdDecryptedFile(threeKey[0]);}
                case 2 -> {BruteForce.threePossibleKeys(threeKey);}
                case 3 -> {BruteForce.likelyKeys();}
            }
    }
}
