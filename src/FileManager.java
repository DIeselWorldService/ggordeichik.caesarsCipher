import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class FileManager {
    static long sizeFile = 0; //Размер изменяемого файла
    static long sizeForNIO = 20000; //Размер изменяемого файла от которого будем использовать NIO
    static String filePath;

    public static ArrayList<Character> readFile(String filePath) throws IOException {
        ArrayList<Character> contentFile = new ArrayList<>();
        FileManager.filePath = filePath;
        Path path = Paths.get(filePath);
        try {
            sizeFile = Files.size(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (sizeFile < sizeForNIO) {
            System.out.println("Читаем маленький файл");
            readFileIO(filePath);
        } else {
            System.out.println("Читаем большой файл");
            readFileNIO(filePath);
        }
        return contentFile;

    }

    public static String readFileIO(String filePath) throws IOException {
        ArrayList<Character> contentFile = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            int charNumber;
            while ((charNumber = br.read()) != -1) {
                contentFile.add((char) charNumber);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String readFileNIO(String filePath) throws IOException {
        ArrayList<Character> contentFile = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines.forEach(line -> {
                for (char c : line.toCharArray()) {
                    contentFile.add(c);
                }
                contentFile.add('\n');
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    public static void writeFile(ArrayList<Character> contentFile) throws IOException {
        if (contentFile.size() < sizeForNIO / 2) { //Определяем размер будующего файла для выбора варианта записи
            System.out.println("Сохраняем маленький файл");
            writeFileIO(contentFile);
        } else {
            System.out.println("Сохранчем большой файл");
            writeFileNIO(contentFile);
        }
    }

    public static void writeFileIO(ArrayList<Character> contentFile) throws IOException {
       try(BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
           for (char ch : contentFile) {
               bw.write(ch);
           }
       }catch (IOException e){
           e.printStackTrace();
       }
    }

    public static void writeFileNIO(ArrayList<Character> contentFile) throws IOException {


    }
}
