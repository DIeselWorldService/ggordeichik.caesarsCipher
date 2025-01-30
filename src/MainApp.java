import java.io.IOException;

public class MainApp {
    public static void main(String[] args) throws IOException {
        System.out.println("Привет! Я шифровщик текстовых файлов, чем я могу тебе помочь?");
        System.out.println("Введите номер одного из предложенных ниже действий:");
        UserMenu.menu();

    }
}