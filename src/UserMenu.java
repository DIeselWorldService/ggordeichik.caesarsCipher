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
            case 1 -> CommandHandler.handlerEncrypt();
            case 2 -> CommandHandler.handlerDecrypt();
            case 3 -> CommandHandler.bruteForce();
            case 0 -> System.exit(0);

        }




    }
}
