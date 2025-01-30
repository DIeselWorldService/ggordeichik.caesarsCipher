import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileManager {
    public static String readFile(String filePath) throws IOException {
        long sizeFile = 0; //Размер изменяемого файла
        long sizeForNIO = 20000; //Размер изменяемого файла от которого будем использовать NIO
        String contentFile = null;


        Path path = Paths.get(filePath);
        try {
            sizeFile = Files.size(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (sizeFile < sizeForNIO) {
            System.out.println("Маленький файл");
            contentFile = readFileIO(filePath);
        } else {
            System.out.println("Большой файл");
            contentFile = readFileNIO(filePath);
        }
        return contentFile;

    }

    public static String readFileIO(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String readFileNIO(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines.forEach(line -> {
                sb.append(line);
                sb.append("\n");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    public void writeFile(String content, String filePath) {
        // Логика записи файла
    }
}
