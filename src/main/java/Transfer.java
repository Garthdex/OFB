import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class Transfer {
    static byte[] intToByte(int[] data) {
        byte[] result = new byte[data.length * 4];
        for (int i = 0, j = 0, shift = 24; i < result.length; i++) {
            result[i] = (byte) (data[j] >> shift);
            if (shift == 0) {
                shift = 24;
                j++;
            } else {
                shift -= 8;
            }
        }
        return result;
    }

    static int[] byteToInt(byte[] data) {
        int[] result = new int[data.length / 4];
        for (int i = 0, j = 0, shift = 24; i < data.length; i++) {
            result[j] |= ((data[i] & 0xFF) << shift);
            if (shift == 0) {
                shift = 24;
                j++;
            } else {
                shift -=8;
            }
        }
        return result;
    }

    static int[] getMd5Digest(String string) {
        byte[] bytes = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(string.getBytes());
            bytes = md5.digest();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return byteToInt(bytes);
    }
}
