import java.io.*;

public class Main {
    private static final String KEY_FILE  = "//key.key";

    private static void writeToFile(String path, byte[] buffer) {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(buffer, 0, buffer.length);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void printHelpToConsole() {
        System.out.println("Вы должны ввести параметры в таком порядке:" + "\n"
                + "java -jar tea.jar -e param1 для шифрования" + "\n"
                + "или tea.jar -d param1 для дешифрования" + "\n"
                + "где param1 - полный путь к текстовому файлу для кодирования/декодирования" + "\n"
                + "пример: C:/ИБ/ЛР1/1.txt для кодирования" + "\n"
                + "или C:/ИБ/ЛР1/1.enc.txt для декодирования");
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0 || args[0].equals("?")) {
            printHelpToConsole();
            return;
        }

        if (args[0].equals("-e")) {
            FileInputStream fin = new FileInputStream(args[1]);
            try {
                File file = new File(args[1].substring(0, args[1].lastIndexOf('.')));
                byte[] bufferValue = new byte[8];
                fin.read(bufferValue, 0, bufferValue.length);

                byte[] key = Tea.generateKey();
                int[] value = Transfer.byteToInt(bufferValue);

                writeToFile(file.getParent() + KEY_FILE, key);
                writeToFile(file.getParent() + "//" + file.getName() + "-enc.txt", Tea.encrypt(value, Transfer.byteToInt(key)));

            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                fin.close();
            }
        }
        if (args[0].equals("-d")) {
            FileInputStream fin1 = new FileInputStream(args[1]);
            try {
                File file = new File(args[1].substring(0, args[1].lastIndexOf('-')));
                FileInputStream fin2 = new FileInputStream(file.getParent() + KEY_FILE);

                byte[] bufferValue = new byte[8];
                byte[] bufferKey = new byte[16];

                fin1.read(bufferValue, 0, bufferValue.length);
                int[] value = Transfer.byteToInt(bufferValue);

                fin2.read(bufferKey, 0, bufferKey.length);
                int[] key = Transfer.byteToInt(bufferKey);

                writeToFile(file.getParent() + "//" + file.getName() + "-dec.txt", Tea.decrypt(value, key));
                fin2.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                fin1.close();
            }
        }
    }
}