import java.io.*;

public class Main {
    private static void printHelpToConsole() {
        System.out.println("Вы должны ввести параметры в таком порядке:" + "\n"
                + "java -jar ofb.jar -e param1 param2 для шифрования" + "\n"
                + "или ofb.jar -d param1 param2 для дешифрования" + "\n"
                + "где param1 - полный путь к текстовому файлу для кодирования/декодирования" + "\n"
                + "пример: C:/IB/1.txt для кодирования" + "\n"
                + "или C:/IB/1-enc.txt для декодирования" + "\n"
                + "где param2 - пароль шифрования" + "\n"
                + "пример: 123");
    }

    public static void main(String[] args) throws IOException {
        File f = new File(args[1]);
        int size = (int)f.length()/8;
        String password = args[2];
        int[] hashKey = Transfer.getMd5Digest(password);
        if (args.length == 0 || args[0].equals("?")) {
            printHelpToConsole();
            return;
        }

        if (args[0].equals("-e")) {
            String enc = f.getName().substring(0, f.getName().lastIndexOf('.')) + "-enc.txt";
            try (FileOutputStream writer = new FileOutputStream(f.getParent() + "//" + enc);
                FileInputStream reader = new FileInputStream(args[1])) {
                byte[] bufferKey = Tea.generateKey();
                int[] key = Transfer.byteToInt(bufferKey);

                int[] keyEnc = Tea.encryptInParts(key, hashKey);
                FileManager.writeFile(writer, Transfer.intToByte(keyEnc));

                runCycle(size, writer, reader, key);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        if (args[0].equals("-d")) {
            String dec = f.getName().substring(0, f.getName().lastIndexOf('-')) + "-dec.txt";
            try (FileOutputStream writer = new FileOutputStream(f.getParent() + "//" + dec);
                 FileInputStream reader = new FileInputStream(args[1])) {

                byte[] bufferKey = new byte[16];
                reader.read(bufferKey, 0, bufferKey.length);

                int[] key = Transfer.byteToInt(bufferKey);
                int[] keyDec = Tea.decryptInParts(key, hashKey);

                runCycle(size - 2, writer, reader, keyDec);

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void runCycle(int size, FileOutputStream writer, FileInputStream reader,
                                 int[] key) throws IOException {
        int vector[] = Tea.initVector();
        byte[] bufferValue = new byte[8];
        int[] value;

        for (int i = 0; i <= size; i++) {
            if (i != size) reader.read(bufferValue, 0, bufferValue.length);
            value = Transfer.byteToInt(bufferValue);
            Tea.encrypt(vector, key);
            Tea.ofb(value, vector);
            if (i == size) {
                int sizeBlock = FileManager.readFile(reader, bufferValue);
                value = Transfer.byteToInt(bufferValue);
                FileManager.writeFile(writer, Transfer.intToByte(value), sizeBlock);
            }
            else FileManager.writeFile(writer, Transfer.intToByte(value));
        }
    }
}