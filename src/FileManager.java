import java.io.*;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    static long sizeFile = 0; //Размер изменяемого файла
    static long sizeForNIO = 20000; //Размер изменяемого файла от которого будем использовать NIO
    static String filePath;
    static int keyCrypt;
    static boolean encryptFile;

    public static void readFile(String filePath, int keyCrypt, boolean encryptFile) throws IOException {
        FileManager.filePath = filePath;
        FileManager.keyCrypt = keyCrypt;
        FileManager.encryptFile = encryptFile;

        Path path = Paths.get(filePath);
        try {
            sizeFile = Files.size(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (sizeFile < sizeForNIO) {
            System.out.println("Читаем маленький файл");
            readFileIO();
        } else {
            System.out.println("Читаем большой файл");
            processFileChunked();
        }

    }

    public static void readFileIO() throws IOException {
        ArrayList<Character> contentFile = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            int charNumber;
            while ((charNumber = br.read()) != -1) {
                contentFile.add((char) charNumber);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (encryptFile) {
            writeFileIO(Cipher.encrypt(contentFile, keyCrypt)); //Остановился здлесь
        } else {
            writeFileIO(Cipher.decrypt(contentFile, keyCrypt));
        }
        UserMenu.menu();
    }

    public static void writeFileIO(ArrayList<Character> contentFile) throws IOException {
       try(BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
           for (char ch : contentFile) {
               bw.write(ch);
           }
       }catch (IOException e){
           e.printStackTrace();
       }
       UserMenu.menu();
    }

    /*public static void readFileNIO() throws IOException {
        int lastDotIndex = filePath.lastIndexOf(".");
        String temporaryFilePath = null;
        if (lastDotIndex != -1) {
            temporaryFilePath = filePath.substring(0, lastDotIndex) + "_temporary" + filePath.substring(lastDotIndex);
        }
        try (FileChannel inChannel = FileChannel.open(Paths.get(filePath), StandardOpenOption.READ);
             FileChannel outChannel = FileChannel.open(Paths.get(temporaryFilePath),
                     StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
                 List<Character> ALPHABET = Cipher.ALPHABET;
                 //Энкодер/декодер дает возможность работать с чарами
            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
            CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();

            ByteBuffer byteBuffer = ByteBuffer.allocate(8192);

            while (true) {
                int bytesRead = inChannel.read(byteBuffer);
                if (bytesRead == -1) {
                    break;
                }

                byteBuffer.flip();
                CharBuffer charBuffer = CharBuffer.allocate(byteBuffer.remaining());
                CoderResult result = decoder.decode(byteBuffer, charBuffer, false);
                charBuffer.flip();

                for (int i = 0; i < charBuffer.limit(); i++) {
                    char original = charBuffer.get(i);
                    int index = ALPHABET.indexOf(original);
                    if (index != -1) {
                        int newIndex;
                        if (encryptFile) {
                            newIndex = (index + keyCrypt) % ALPHABET.size();
                        } else {
                            newIndex = (index - keyCrypt + ALPHABET.size()) % ALPHABET.size();
                        }
                        char newChar = ALPHABET.get(newIndex);
                        charBuffer.put(i, newChar);
                    }
                }
                ByteBuffer encoderBuffer = ByteBuffer.allocate(8192);
                encoder.encode(charBuffer, encoderBuffer, false);

                encoderBuffer.flip();
                outChannel.write(encoderBuffer);
                byteBuffer.clear();
            }
        }
        try{
            if(Files.exists(Paths.get(filePath))){
                Files.delete(Paths.get(filePath));
            }
            Files.move(Paths.get(temporaryFilePath), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        UserMenu.menu();
    }*/


    public static void processFileChunked() throws IOException {
        int lastDotIndex = filePath.lastIndexOf(".");
        String temporaryFilePath = null;
        if (lastDotIndex != -1) {
            temporaryFilePath = filePath.substring(0, lastDotIndex) + "_temporary" + filePath.substring(lastDotIndex);
        }

        try (FileChannel inChannel = FileChannel.open(Paths.get(filePath), StandardOpenOption.READ);
             FileChannel outChannel = FileChannel.open(Paths.get(temporaryFilePath),
                     StandardOpenOption.CREATE,
                     StandardOpenOption.TRUNCATE_EXISTING,
                     StandardOpenOption.WRITE))
        {
            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
            CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();

            ByteBuffer byteBuffer = ByteBuffer.allocate(8192);
            CharBuffer charBuffer = CharBuffer.allocate(8192);
            ByteBuffer encodeBuffer = ByteBuffer.allocate(8192);

            boolean endOfInput = false;
            ByteBuffer leftoverBytes = null; // Буфер для неполных байтов

            while (!endOfInput) {
                if (leftoverBytes != null) {
                    // Если есть неполные байты, добавляем их в начало нового буфера
                    byteBuffer.clear();
                    byteBuffer.put(leftoverBytes);
                    leftoverBytes = null;
                }

                int bytesRead = inChannel.read(byteBuffer);
                if (bytesRead == -1) {
                    endOfInput = true;
                }
                byteBuffer.flip();

                CoderResult cr;
                while (true) {
                    cr = decoder.decode(byteBuffer, charBuffer, endOfInput);
                    if (cr.isOverflow()) {
                        processDecodedChunkAndWrite(charBuffer, encoder, encodeBuffer, outChannel, keyCrypt, encryptFile);
                        continue;
                    } else if (cr.isUnderflow()) {
                        // Проверяем, есть ли неполные байты
                        if (byteBuffer.hasRemaining()) {
                            leftoverBytes = ByteBuffer.allocate(byteBuffer.remaining());
                            leftoverBytes.put(byteBuffer);
                            leftoverBytes.flip();
                        }
                        break;
                    } else if (cr.isError()) {
                        cr.throwException();
                    }
                }

                byteBuffer.compact();
            }

            flushDecoder(decoder, charBuffer, encoder, encodeBuffer, outChannel, keyCrypt, encryptFile);
            flushEncoder(encoder, encodeBuffer, outChannel);
        }
    }

    /** Обрабатываем накопившиеся в charBuffer символы:
     *  - шифруем (Caesar)
     *  - кодируем в encodeBuffer
     *  - пишем в outChannel
     */
    private static void processDecodedChunkAndWrite(
            CharBuffer charBuffer,
            CharsetEncoder encoder,
            ByteBuffer encodeBuffer,
            FileChannel outChannel,
            int keyCrypt,
            boolean encryptFile) throws IOException {
        System.out.println("Starting processDecodedChunkAndWrite");

        // flip, чтобы перейти в режим чтения из charBuffer
        charBuffer.flip();
        // 1. Шифруем
        encryptInCharBuffer(charBuffer, keyCrypt, encryptFile);
        // 2. Кодируем charBuffer -> encodeBuffer
        //    Это тоже может быть циклом, если encodeBuffer переполнится
        while (charBuffer.hasRemaining()) {
            CoderResult crEnc = encoder.encode(charBuffer, encodeBuffer, false);
            if (crEnc.isOverflow()) {
                // encodeBuffer заполнен — пишем в файл
                writeEncoded(encodeBuffer, outChannel);
            } else if (crEnc.isUnderflow()) {
                // значит всё ok, просто идём дальше
                break;
            } else if (crEnc.isError()) {
                crEnc.throwException();
            }
        }
        // charBuffer полностью прочитан
        charBuffer.clear();
        // Возможно, encodeBuffer ещё остались байты
        writeEncoded(encodeBuffer, outChannel);

        System.out.println("Finished processDecodedChunkAndWrite");
    }

    /** Доп. метод: "запись" encodeBuffer в файл при overflow. */
    private static void writeEncoded(ByteBuffer encodeBuffer, FileChannel outChannel) throws IOException {
        System.out.println("Starting writeEncoded");

        encodeBuffer.flip();
        outChannel.write(encodeBuffer);
        encodeBuffer.clear();

        System.out.println("Finished writeEncoded");
    }

    /** "дошифровка" хвостов, когда endOfInput=true */
    private static void flushDecoder(CharsetDecoder decoder,
                                     CharBuffer charBuffer,
                                     CharsetEncoder encoder,
                                     ByteBuffer encodeBuffer,
                                     FileChannel outChannel,
                                     int keyCrypt, boolean encryptFile) throws IOException {
        System.out.println("Starting flushDecoder");

        // Завершаем декодирование оставшихся данных
        while (true) {
            CoderResult cr = decoder.decode(ByteBuffer.allocate(0), charBuffer, true);
            if (cr.isOverflow()) {
                // charBuffer переполнен, шифруем и пишем
                processDecodedChunkAndWrite(charBuffer, encoder, encodeBuffer, outChannel, keyCrypt, encryptFile);
            } else if (cr.isUnderflow()) {
                // всё декодировано
                break;
            } else if (cr.isError()) {
                cr.throwException();
            }
        }

        // Обрабатываем остатки в charBuffer
        processDecodedChunkAndWrite(charBuffer, encoder, encodeBuffer, outChannel, keyCrypt, encryptFile);

        // А теперь сам flush
        while (true) {
            CoderResult cr = decoder.flush(charBuffer);
            if (cr.isOverflow()) {
                processDecodedChunkAndWrite(charBuffer, encoder, encodeBuffer, outChannel, keyCrypt, encryptFile);
            } else if (cr.isUnderflow()) {
                break;
            } else if (cr.isError()) {
                cr.throwException();
            }
        }

        // Ещё раз обрабатываем остатки в charBuffer
        processDecodedChunkAndWrite(charBuffer, encoder, encodeBuffer, outChannel, keyCrypt, encryptFile);

        System.out.println("Finished flushDecoder");
    }

    /** Аналогичная процедура "довыжать" encoder */
    private static void flushEncoder(CharsetEncoder encoder,
                                     ByteBuffer encodeBuffer,
                                     FileChannel outChannel) throws IOException {
        System.out.println("Starting flushEncoder");

        // Завершаем кодирование оставшихся данных
        while (true) {
            CoderResult cr = encoder.encode(CharBuffer.allocate(0), encodeBuffer, true); // Передаем пустой CharBuffer и endOfInput=true
            if (cr.isOverflow()) {
                // encodeBuffer переполнен — пишем в файл
                writeEncoded(encodeBuffer, outChannel);
            } else if (cr.isUnderflow()) {
                break;
            } else if (cr.isError()) {
                cr.throwException();
            }
        }

        // Теперь можно безопасно вызвать flush
        while (true) {
            CoderResult cr = encoder.flush(encodeBuffer);
            if (cr.isOverflow()) {
                // encodeBuffer переполнен — пишем в файл
                writeEncoded(encodeBuffer, outChannel);
            } else if (cr.isUnderflow()) {
                break;
            } else if (cr.isError()) {
                cr.throwException();
            }
        }

        // Записываем, если остались данные
        encodeBuffer.flip();
        if (encodeBuffer.hasRemaining()) {
            outChannel.write(encodeBuffer);
        }
        encodeBuffer.clear();

        System.out.println("Finished flushEncoder");
    }

    /** Применяем ваш Caesar (или любой другой) шифр к содержимому charBuffer in-place */
    private static void encryptInCharBuffer(CharBuffer charBuffer,
                                            int keyCrypt,
                                            boolean encryptFile)
    {
        List<Character> ALPHABET = Cipher.ALPHABET;
        // Проходимся по символам, меняем их
        for (int i = 0; i < charBuffer.limit(); i++) {
            char original = charBuffer.get(i);
            int index = ALPHABET.indexOf(original);
            if (index != -1) {
                int newIndex;
                if (encryptFile) {
                    newIndex = (index + keyCrypt) % ALPHABET.size();
                } else {
                    newIndex = (index - keyCrypt + ALPHABET.size()) % ALPHABET.size();
                }
                charBuffer.put(i, ALPHABET.get(newIndex));
            }
        }
    }

}
