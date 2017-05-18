import java.io.*;
import java.nio.file.Files;

public class Main {
    private static final String KEY_FILE = "//key.key";
    private static void printHelpToConsole() {
        System.out.println("Вы должны ввести параметры в таком порядке:" + "\n"
                + "java -jar ofb.jar -e param1 param2 для шифрования" + "\n"
                + "или ofb.jar -d param1 param2 для дешифрования" + "\n"
                + "где param1 - полный путь к текстовому файлу для кодирования/декодирования" + "\n"
                + "пример: C:/IB/1.txt для кодирования" + "\n"
                + "или C:/IB/1-enc.txt для декодирования");
    }

    public static void main(String[] args) throws IOException, IndexOutOfBoundsException {
        File f = new File(args[1]);
        String dot = f.getName().substring(f.getName().lastIndexOf("."), f.getName().length());
        int size = (int)f.length()/8;

        if (args.length == 0 || args[0].equals("?")) {
            printHelpToConsole();
            return;
        }

        System.out.println("Would you use session password? y/n");
        Console cons = System.console();
        String answer = cons.readLine();
        int[] hashKey = null;
        if (answer.equals("y")) {
            String password = String.valueOf(cons.readPassword("Enter Password: "));
            hashKey = Transfer.getMd5Digest(password);
        }

        if (args[0].equals("-e")) {

            System.out.println("Would you archive?y/n");
            String answer2 = cons.readLine();
            String fileName = "";
            File fArch = null;
            if (answer2.equals("y")) {
                byte[] value;
                try {
                    value = FileManager.readFile(f);
                } catch (FileNotFoundException e){
                    System.out.println("Файл не найден");
                    return;
                }
                byte[] compressed = Archiver.compressed(value);
                fileName = f.getName().substring(0, f.getName().lastIndexOf('.')) + "-ZIP" + dot;
                fArch = new File(f.getParent() + "//" + fileName);
                size = (int)fArch.length()/8;
                FileManager.writeFile(f.getParent()+ "//" + fileName, compressed);
            }

            String enc = f.getName().substring(0, f.getName().lastIndexOf('.')) + "-enc" + dot;
            FileInputStream reader;
            if (answer2.equals("y")) reader = new FileInputStream(f.getParent() + "//" + fileName);
            else reader = new FileInputStream(args[1]);
            try (FileOutputStream writer = new FileOutputStream(f.getParent() + "//" + enc)) {
                byte[] bufferKey = Tea.generateKey();
                int[] key = Transfer.byteToInt(bufferKey);

                if (hashKey != null) {
                    int[] keyEnc = Tea.encryptInParts(key, hashKey);
                    FileManager.writeFile(writer, Transfer.intToByte(keyEnc));
                }
                else {
                    System.out.println(new String(Transfer.intToByte(key)));
                    FileManager.writeToFile(f.getParent() + KEY_FILE, Transfer.intToByte(key));
                }

                runCycle(size, writer, reader, key);

            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                reader.close();
            }
            if (fArch != null) {
                Files.delete(fArch.toPath());
            }
            System.out.println("Would you enc base64?y/n");
            String answer3 = cons.readLine();
            if (answer3.equals("y")) {
                String str;
                File file = new File(f.getParent() + "//" + enc);
                String dotBase64 = file.getName().substring(file.getName().lastIndexOf("."), file.getName().length());
                String fileName2;
                try {
                    str = new String(FileManager.readFile(file));
                } catch (FileNotFoundException e) {
                    System.out.println("Файл не найден");
                    return;
                }
                byte[] encodeByte = Base64.encode(str.getBytes());

                fileName2 = file.getName().substring(0, file.getName().lastIndexOf(".")) + "-Base64" + dotBase64;
                FileManager.writeFile(f.getParent() + "//" + fileName2, encodeByte);
                File del = new File(f.getParent() + "//" + enc);
                Files.delete(del.toPath());
            }
        }

        String answer3;
        if (args[0].equals("-d")) {

            System.out.println("Would you dec base64?y/n");
            answer3 = cons.readLine();
            byte[] str;
            String fileName = "";
            if (answer3.equals("y")) {
                try {
                    str = FileManager.readFile(f);
                } catch (FileNotFoundException e) {
                    System.out.println("Файл не найден");
                    return;
                }
                byte[] result = Base64.decode(str);
                fileName = f.getName().substring(0, f.getName().indexOf("-")) + "-unBase64" + dot;
                FileManager.writeFile(f.getParent() + "//" + fileName, result);
            }

            String dec = "";
            FileInputStream reader;
            if (answer3.equals("y")) {
                dec = f.getName().substring(0, f.getName().indexOf("-")) + "-unBase64" + "-dec" + dot;
                reader = new FileInputStream(f.getParentFile() + "//" + fileName);
            }
            else {
                dec = f.getName().substring(0, f.getName().indexOf('-')) + "-dec" + dot;
                reader = new FileInputStream(args[1]);
            }
            try (FileOutputStream writer = new FileOutputStream(f.getParent() + "//" + dec)) {

                if (answer3.equals("y")) {
                    File fSize = new File(f.getParentFile() + "//" + fileName);
                    size = (int)fSize.length()/8;
                }
                int[] keyDec = null;
                byte[] bufferKey = new byte[16];
                if (hashKey != null) {
                    reader.read(bufferKey, 0, bufferKey.length);
                    int[] key = Transfer.byteToInt(bufferKey);
                    keyDec = Tea.decryptInParts(key, hashKey);
                }
                else {
                    FileInputStream fin2 = new FileInputStream(f.getParent() + KEY_FILE);
                    fin2.read(bufferKey, 0, bufferKey.length);
                }
                System.out.println(new String(bufferKey));
                if (hashKey != null) runCycle(size - 2, writer, reader, keyDec);
                else runCycle(size, writer, reader, Transfer.byteToInt(bufferKey));

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            File fUnArch;
            if (answer3.equals("y")) fUnArch = new File(f.getParent() + "//" + fileName);
            else fUnArch = new File(f.getParent() + "//" + dec);
            System.out.println("Would you unarchive?y/n");
            String answer2 = cons.readLine();
            if (answer2.equals("y")) {
                byte[] value;
                try {
                    value = FileManager.readFile(fUnArch);
                } catch (FileNotFoundException e){
                    System.out.println("Файл не найден");
                    return;
                }
                byte[] decompressed = Archiver.deCompressed(value);
                FileManager.writeFile(f.getParent() + "//" + dec, decompressed);
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
                Tea.encrypt(vector, key);
                Tea.ofb(value, vector);
                FileManager.writeFile(writer, Transfer.intToByte(value), sizeBlock);
            }
            else FileManager.writeFile(writer, Transfer.intToByte(value));
        }
    }
}