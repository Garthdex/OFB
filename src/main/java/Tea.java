public class Tea {
    private static final int DELTA = 0x9e3779b9;

    public static byte[] generateKey() {
        byte[] k = new byte[16];
        for (int i = 0; i < 16; i++) {
            k[i] = (byte)(Math.random()*100);
        }
        return k;
    }

    public static byte[] encrypt(int[] v, int[] k) {
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
        return Transfer.intToByte(v);
    }

    public static byte[] decrypt (int[]  v, int[] k) {
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
        return Transfer.intToByte(v);
    }

}