import java.util.Random;

class Tea {
    private static final int DELTA = 0x9e3779b9;
    private static final int VECTOR_ELEMENT = 0;

    static byte[] generateKey() {
        byte[] k = new byte[16];
        Random random = new Random();
        for (int i = 0; i < 16; i++) {
            k[i] = (byte)(random.nextInt(255));
        }
        return k;
    }

    static void ofb(int[] value, int[] vector) {
        value[0] ^= vector[0];
        value[1] ^= vector[1];
    }

    static int[] encrypt(int[] v, int[] k) {
        int v0 = v[0];
        int v1 = v[1];
        int sum = 0;

        int k0 = k[0];
        int k1 = k[1];
        int k2 = k[2];
        int k3 = k[3];

        for (int i = 0; i < 32; i++) {
            sum += DELTA;
            v0 += ((v1 << 4) + k0) ^ (v1 + sum) ^ ((v1 >> 5) + k1);
            v1 += ((v0 << 4) + k2) ^ (v0 + sum) ^ ((v0 >> 5) + k3);
        }

        v[0] = v0;
        v[1] = v1;
        return v;
    }

    static int[] encryptInParts(int[] key, int[] hashKey) {
        int[] part1 = {key[0], key[1]};
        int[] part2 = {key[2], key[3]};

        encrypt(part1, hashKey);
        encrypt(part2, hashKey);


        return new int[]{part1[0], part1[1], part2[0], part2[1]};
    }

    static int[] decryptInParts(int[] key, int[] hashKey) {
        int[] part1 = {key[0], key[1]};
        int[] part2 = {key[2], key[3]};

        decrypt(part1, hashKey);
        decrypt(part2, hashKey);


        return new int[]{part1[0], part1[1], part2[0], part2[1]};
    }

    /**
     *
     * @param v значение
     * @param k ключ
     * @return
     * Декрипт для шифрования, здесь не используется
     */
    static int[] decrypt (int[]  v, int[] k) {
        int v0 = v[0];
        int v1 = v[1];
        int sum = 0xC6EF3720;

        int k0 = k[0];
        int k1 = k[1];
        int k2 = k[2];
        int k3 = k[3];

        for (int i = 0; i < 32; i++) {
            v1 -= ((v0 << 4) + k2) ^ (v0 + sum) ^ ((v0 >> 5) + k3);
            v0 -= ((v1 << 4) + k0) ^ (v1 + sum) ^ ((v1 >> 5) + k1);

            sum -= DELTA;
        }

        v[0] = v0;
        v[1] = v1;
        return v;
    }

    static int[] initVector() {
        return new int[]{VECTOR_ELEMENT, VECTOR_ELEMENT};
    }

}