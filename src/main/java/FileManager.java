import java.io.*;

class FileManager {
    private static final int EOF = -1;

    /**
     *
     * @param path путь
     * @param buffer массив байтов для записи
     * этот метод для записи в файл ключа, здесь не используется.
     */
    static void writeToFile(String path, byte[] buffer) {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(buffer, 0, buffer.length);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    static void writeFile(FileOutputStream fos, byte[] buffer, int size) throws IOException {
        fos.write(buffer, 0, size);
    }

    static void writeFile(FileOutputStream fos, byte[] buffer) throws IOException {
        fos.write(buffer, 0, buffer.length);
    }

    static int readFile(FileInputStream fis, byte[] buffer) throws IOException {
        int size = 0;
        int tempByte;
        for (int i = 0; i < 8; i++) {
            tempByte = fis.read();
            if (tempByte != EOF) {
                buffer[i] = (byte) tempByte;
                size++;
            } else buffer[i] = 0;
        }
        return size;
    }
}
