package img.crypto;

public class WzUnicodeString implements WzStringDecodeImpl {

    public WzUnicodeString() { }

    @Override
    public void decode(byte[] data, int len, boolean isModernImgFile) {
        int mask = 0xAAAA;

        for (int i = 0; i < len; i += 2) {
            byte pos = (byte) (i * 2);

            byte low = data[pos];
            byte high = data[pos + 1];
            int encrypted = (low & 0xFF) | ((high & 0xFF) << 8);
            encrypted ^= mask++;
            data[pos] = (byte) encrypted;
            data[pos + 1] = (byte) (encrypted >> 8);
        }
    }
}
