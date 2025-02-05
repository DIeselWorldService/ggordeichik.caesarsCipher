import state.State;
import state.UserState;

import java.io.*;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.ArrayList;

public class FileManager {
    static long sizeFile = 0;        //Размер изменяемого файла
    static long sizeForNIO = 25000; //Размер изменяемого файла от которого будем использовать NIO
    static String filePath;        //Адрес файла
    static String temporaryFilePath;
    static int keyCrypt;          //Ключь шифрования/дешифрования


    /*
    readFile - определяем размер файла, сравнив с переменной sizeForNIO и в зависимости от размера отправляемся далее по одному из двух путей
     */
    public static void readFile(String filePath, int keyCrypt) throws IOException {
        FileManager.filePath = filePath;
        FileManager.keyCrypt = keyCrypt;
        Path path = Paths.get(filePath);
        int lastDotIndex = filePath.lastIndexOf(".");
        if (lastDotIndex != -1) {
            temporaryFilePath = filePath.substring(0, lastDotIndex) + "_temporary" + filePath.substring(lastDotIndex);
        } else {
            temporaryFilePath = filePath + "_temporary";
        }
        try {
            sizeFile = Files.size(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (sizeFile < sizeForNIO) {
            readFileIO();
        } else {
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
        if (UserState.getCurrentState() == State.ENCRYPTED) {
            writeFileIO(Cipher.encrypt(contentFile, keyCrypt));
        } else if (UserState.getCurrentState() == State.DECRYPTED) {
            writeFileIO(Cipher.decrypt(contentFile, keyCrypt));
        } else if (UserState.getCurrentState() == State.BRUTE_FORCED) {
            BruteForce.decryptedTextByBruteForce(Cipher.decrypt(contentFile, keyCrypt));
        } else if (UserState.getCurrentState() == State.BRUTE_FORCED_DECRYPTED) {
            writeFileIO(Cipher.decrypt(contentFile, keyCrypt));
        }
        UserMenu.menu();
    }

    public static void writeFileIO(ArrayList<Character> contentFile) throws IOException {
        if (UserState.getCurrentState() == State.ENCRYPTED || UserState.getCurrentState() == State.DECRYPTED) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
                for (char ch : contentFile) {
                    bw.write(ch);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (UserState.getCurrentState() == State.BRUTE_FORCED_DECRYPTED) {
            int lastDotIndex = filePath.lastIndexOf(".");
            String newDecryptedFilePath = null;
            if (lastDotIndex != -1) {
                newDecryptedFilePath = filePath.substring(0, lastDotIndex) + "_decrypted" + filePath.substring(lastDotIndex);
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(newDecryptedFilePath))) {
                for (char ch : contentFile) {
                    bw.write(ch);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Создан новый файл с расшифрованным текстом: " + newDecryptedFilePath);
        }
        UserMenu.menu();
    }


    public static void processFileChunked() throws IOException {
        // Ниже создаем путь для временного файла
        // открываем каналы на чтение/запись файлов
        try (FileChannel inChannel = FileChannel.open(Paths.get(filePath), StandardOpenOption.READ);
             FileChannel outChannel = FileChannel.open(Paths.get(temporaryFilePath),
                     StandardOpenOption.CREATE,
                     StandardOpenOption.TRUNCATE_EXISTING,
                     StandardOpenOption.WRITE)) {
            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
            CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) sizeForNIO);
            CharBuffer charBuffer = CharBuffer.allocate((int) sizeForNIO);
            ByteBuffer encodeBuffer = ByteBuffer.allocate((int) sizeForNIO);
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
                        processDecodedChunkAndWrite(charBuffer, encoder, encodeBuffer, outChannel, keyCrypt);
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
            flushDecoder(decoder, charBuffer, encoder, encodeBuffer, outChannel);
            flushEncoder(encoder, encodeBuffer, outChannel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (UserState.getCurrentState() == State.ENCRYPTED || UserState.getCurrentState() == State.DECRYPTED) {
                // Выводим сообщение о завершении работы
                System.out.println("Работа с файлом завершена.");
                // Удаляем оригинальный файл
                Files.delete(Paths.get(filePath));
                // Переименовываем временный файл в имя оригинального файла
                Files.move(Paths.get(temporaryFilePath), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Оригинальный файл удален, временный файл переименован.");
                UserMenu.menu();
            } else if (UserState.getCurrentState() == State.BRUTE_FORCED_DECRYPTED) {
                // Выводим сообщение о завершении работы
                System.out.println("Работа с файлом завершена.");
                int lastDotIndex = filePath.lastIndexOf(".");
                String newDecryptedFilePath = null;
                if (lastDotIndex != -1) {
                    newDecryptedFilePath = filePath.substring(0, lastDotIndex) + "_decrypted" + filePath.substring(lastDotIndex);
                }
                // Переименовываем временный файл в новый файл с суффиксом _decrypted
                Files.move(Paths.get(temporaryFilePath), Paths.get(newDecryptedFilePath), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Создан новый файл с расшифрованным текстом: " + newDecryptedFilePath);
                UserMenu.menu();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Ошибка при завершении работы с файлом: " + e.getMessage());
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
            int keyCrypt) throws IOException {

        // flip, чтобы перейти в режим чтения из charBuffer
        charBuffer.flip();
        // 1. Шифруем
        if (UserState.getCurrentState() == State.ENCRYPTED || UserState.getCurrentState() == State.DECRYPTED) {
            Cipher.encryptInCharBuffer(charBuffer, keyCrypt);
        } else if (UserState.getCurrentState() == State.BRUTE_FORCED) {
            ArrayList<Character> charList = new ArrayList<>();
            while (charBuffer.hasRemaining()) {
                charList.add(charBuffer.get());
            }
            BruteForce.decryptedTextByBruteForce(Cipher.decrypt(charList, keyCrypt));
        }
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

    }

    /** Доп. метод: "запись" encodeBuffer в файл при overflow. */
    private static void writeEncoded(ByteBuffer encodeBuffer, FileChannel outChannel) throws IOException {

        encodeBuffer.flip();
        outChannel.write(encodeBuffer);
        encodeBuffer.clear();

    }

    /** "дошифровка" хвостов, когда endOfInput=true */
    private static void flushDecoder(CharsetDecoder decoder,
                                     CharBuffer charBuffer,
                                     CharsetEncoder encoder,
                                     ByteBuffer encodeBuffer,
                                     FileChannel outChannel) throws IOException {

        // Завершаем декодирование оставшихся данных
        while (true) {
            CoderResult cr = decoder.decode(ByteBuffer.allocate(0), charBuffer, true);
            if (cr.isOverflow()) {
                // charBuffer переполнен, шифруем и пишем
                processDecodedChunkAndWrite(charBuffer, encoder, encodeBuffer, outChannel, keyCrypt);
            } else if (cr.isUnderflow()) {
                // всё декодировано
                break;
            } else if (cr.isError()) {
                cr.throwException();
            }
        }

        // Обрабатываем остатки в charBuffer
        processDecodedChunkAndWrite(charBuffer, encoder, encodeBuffer, outChannel, keyCrypt);

        // А теперь сам flush
        while (true) {
            CoderResult cr = decoder.flush(charBuffer);
            if (cr.isOverflow()) {
                processDecodedChunkAndWrite(charBuffer, encoder, encodeBuffer, outChannel, keyCrypt);
            } else if (cr.isUnderflow()) {
                break;
            } else if (cr.isError()) {
                cr.throwException();
            }
        }

        // Ещё раз обрабатываем остатки в charBuffer
        processDecodedChunkAndWrite(charBuffer, encoder, encodeBuffer, outChannel, keyCrypt);

    }

    /** Аналогичная процедура "довыжать" encoder */
    private static void flushEncoder(CharsetEncoder encoder,
                                     ByteBuffer encodeBuffer,
                                     FileChannel outChannel) throws IOException {

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

    }



}
