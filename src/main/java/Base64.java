public class Base64 {
    private static byte[] encodeData;
    private static String charSet =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    static {
        encodeData = new byte[64];
        for (int i = 0; i < 64; i++) {
            byte c = (byte) charSet.charAt(i);
            encodeData[i] = c;
        }
    }

    private Base64() {
    }

    public static byte[] encode(byte[] src) {
        return encode(src, 0, src.length);
    }

    public static byte[] encode(byte[] src, int start, int length) {
        byte[] dst = new byte[(length + 2) / 3 * 4];
        int x;
        int dstIndex = 0;
        int state = 0;
        int old = 0;
        int max = length + start;
        for (int srcIndex = start; srcIndex < max; srcIndex++) {
            x = src[srcIndex];
            switch (++state) {
                case 1:
                    dst[dstIndex++] = encodeData[(x >> 2) & 0x3f];
                    break;
                case 2:
                    dst[dstIndex++] = encodeData[((old << 4) & 0x30)
                            | ((x >> 4) & 0xf)];
                    break;
                case 3:
                    dst[dstIndex++] = encodeData[((old << 2) & 0x3C)
                            | ((x >> 6) & 0x3)];
                    dst[dstIndex++] = encodeData[x & 0x3F];
                    state = 0;
                    break;
            }
            old = x;
        }

        switch (state) {
            case 1:
                dst[dstIndex++] = encodeData[(old << 4) & 0x30];
                dst[dstIndex++] = (byte) '=';
                dst[dstIndex++] = (byte) '=';
                break;
            case 2:
                dst[dstIndex++] = encodeData[(old << 2) & 0x3c];
                dst[dstIndex++] = (byte) '=';
                break;
        }
        return dst;
    }

    public static byte[] decode(byte[] bytes) {
        int end = 0;
        if (bytes[bytes.length - 1] == "=".getBytes()[0]) {
            end++;
        }
        if (bytes[bytes.length - 2] == "=".getBytes()[0]) {
            end++;
        }
        int len = (bytes.length + 3) / 4 * 3 - end;
        byte[] result = new byte[len];
        int dst = 0;
        try {
            for (int src = 0; src < bytes.length; src++) {
                int code = charSet.indexOf(bytes[src] & 0xFF);
                if (code == -1) {
                    break;
                }
                switch (src % 4) {
                    case 0:
                        result[dst] = (byte) (code << 2);
                        break;
                    case 1:
                        result[dst++] |= (byte) ((code >> 4) & 0x3);
                        result[dst] = (byte) (code << 4);
                        break;
                    case 2:
                        result[dst++] |= (byte) ((code >> 2) & 0xf);
                        result[dst] = (byte) (code << 6);
                        break;
                    case 3:
                        result[dst++] |= (byte) (code & 0x3f);
                        break;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        return result;
    }
}
