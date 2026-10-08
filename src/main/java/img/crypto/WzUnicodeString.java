package img.crypto;

public class WzUnicodeString implements IWzStringDecode {

    private final byte[] aWzKey;

    public WzUnicodeString() {
        this(null);
    }

    /**
     * Middle-era wz files (v62 to v92) decrypt every character with
     * the 64 KiB key from {@link WzKeyGenerator} on top of the
     * 0xAAAA mask; earlier versions use the mask alone.
     */
    public WzUnicodeString(byte[] aWzKey) {
        this.aWzKey = aWzKey;
    }

    @Override
    public void decode(byte[] data, int len, boolean isModernImgFile) {
        int mask = 0xAAAA;

        for (int i = 0; i < data.length; i += 2) {
            byte low = data[i];
            byte high = data[i + 1];
            int encrypted = (low & 0xFF) | ((high & 0xFF) << 8);
            encrypted ^= mask++;
            if (aWzKey != null) {
                encrypted ^= (aWzKey[i] & 0xFF) | ((aWzKey[i + 1] & 0xFF) << 8);
            }
            data[i] = (byte) encrypted;
            data[i + 1] = (byte) (encrypted >> 8);
        }
    }
}
