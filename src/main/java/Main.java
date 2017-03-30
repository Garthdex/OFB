import java.io.*;

public class Main {
    private static final String KEY_FILE  = "\\key.key";
    private static final int VECTOR_ELEMENT = 0;
    private static final int EOF = -1;

    private static void writeToFile(String path, byte[] buffer) {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(buffer, 0, buffer.length);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void writeFile(FileOutputStream fos, byte[] buffer, int size) throws IOException {
        fos.write(buffer, 0, size);
    }

    private static void writeFile(FileOutputStream fos, byte[] buffer) throws IOException {
        fos.write(buffer, 0, buffer.length);
    }

    private static int readFile(FileInputStream fis, byte[] buffer) throws IOException {
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

    private static void printHelpToConsole() {
        System.out.println("Вы должны ввести параметры в таком порядке:" + "\n"
                + "java -jar tea.jar -e param1 для шифрования" + "\n"
                + "или tea.jar -d param1 для дешифрования" + "\n"
                + "где param1 - полный путь к текстовому файлу для кодирования/декодирования" + "\n"
                + "пример: C:/ИБ/ЛР1/1.txt для кодирования" + "\n"
                + "или C:/ИБ/ЛР1/1-enc.txt для декодирования");
    }

    public static void main(String[] args) throws IOException {
        File f = new File(args[1]);
        if (args.length == 0 || args[0].equals("?")) {
            printHelpToConsole();
            return;
        }


        if (args[0].equals("-e")) {
            String enc = f.getName().substring(0, f.getName().lastIndexOf('.')) + "-enc.txt";
            try (FileOutputStream writer = new FileOutputStream(f.getParent() + "//" + enc);
                FileInputStream reader = new FileInputStream(args[1])) {

                byte[] bufferKey = Tea.generateKey();

                runCycle(f, writer, reader, bufferKey, args[2]);

                writeToFile(f.getParent() + KEY_FILE, bufferKey);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }


        if (args[0].equals("-d")) {
            String dec = f.getName().substring(0, f.getName().lastIndexOf('-')) + "-dec.txt";
            try (FileOutputStream writer = new FileOutputStream(f.getParent() + "//" + dec);
                 FileInputStream reader = new FileInputStream(args[1]);
                 FileInputStream writerKey = new FileInputStream(f.getParent() + KEY_FILE)) {


                byte[] bufferKey = new byte[16];

                writerKey.read(bufferKey, 0, bufferKey.length);

                runCycle(f, writer, reader, bufferKey, args[2]);

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void runCycle(File f, FileOutputStream writer, FileInputStream reader,
                                 byte[] bufferKey, String password) throws IOException {
        int size = (int)f.length()/8;
        int vector[] = initVector();
        int[] hashKey = Transfer.getMd5Digest(password);
        int[] key = Transfer.byteToInt(bufferKey);

        int[] keyEnc = Tea.encryptInParts(key, hashKey);
        writeFile(writer, Transfer.intToByte(keyEnc));

        byte[] bufferValue = new byte[8];


        for (int i = 0; i <= size; i++) {
            int[] value = Transfer.byteToInt(bufferValue);
            Tea.encrypt(vector, key);
            Tea.ofb(value, vector);

            if (i == size) {
                int sizeBlock = readFile(reader, bufferValue);
                writeFile(writer, Transfer.intToByte(value), sizeBlock);
            }
            else writeFile(writer, Transfer.intToByte(value));
        }
    }

    private static int[] initVector() {
        return new int[]{VECTOR_ELEMENT, VECTOR_ELEMENT};
    }

}