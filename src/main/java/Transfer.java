public class Transfer {
    public static byte[] intToByte(int[] data) {
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
//        byte[] bytes = new byte[8];
//        bytes[0] = (byte) (data[0] >> 24);
//        bytes[1] = (byte) (data[0] >> 16);
//        bytes[2] = (byte) (data[0] >> 8);
//        bytes[3] = (byte) (data[0]);
//        bytes[4] = (byte) (data[1] >> 24);
//        bytes[5] = (byte) (data[1] >> 16);
//        bytes[6] = (byte) (data[1] >> 8);
//        bytes[7] = (byte) (data[1]);
//        return bytes;

    public static int[] byteToInt(byte[] data) {
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
}
