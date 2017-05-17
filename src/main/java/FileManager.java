import java.io.*;

class FileManager {
    private static final int EOF = -1;

    public static byte[] readFile(File f) throws FileNotFoundException {
        byte[] buffer = new byte[(int) f.length()];
        try (BufferedInputStream reader = new BufferedInputStream(new FileInputStream(f))) {
            reader.read(buffer);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("файл не найден");
        } catch (IOException e) {
            throw new FileNotFoundException("ошибка ввода/вывода");
        }
        return buffer;
    }

    public static void writeFile(String pathFile, byte[] data) {
        try (BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(pathFile))) {
            writer.write(data);
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден");
        } catch (IOException e) {
            System.out.println("Ошибка ввода\\вывода");
        }
    }

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
