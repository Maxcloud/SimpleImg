package img.crypto;

public class WzUnicodeString implements IWzStringDecode {

    private final byte[] aWzKey;
    private final boolean keyForListOnly;

    public WzUnicodeString() {
        this(null);
    }

    public WzUnicodeString(byte[] aWzKey) {
        this(aWzKey, false);
    }

    /**
     * Middle-era wz files (v62 to v92) decrypt every character with
     * the 64 KiB key from {@link WzKeyGenerator} on top of the
     * 0xAAAA mask; earlier versions use the mask alone.
     *
     * @param keyForListOnly when true (v55 and below), the key applies
     *                       only to img files listed in List.wz; other
     *                       files carry nothing but the mask
     */
    public WzUnicodeString(byte[] aWzKey, boolean keyForListOnly) {
        this.aWzKey = aWzKey;
        this.keyForListOnly = keyForListOnly;
    }

    @Override
    public void decode(byte[] data, int len, boolean isListFile) {
        int mask = 0xAAAA;

        for (int i = 0; i < data.length; i += 2) {
            byte low = data[i];
            byte high = data[i + 1];
            int encrypted = (low & 0xFF) | ((high & 0xFF) << 8);
            encrypted ^= mask++;
            if (aWzKey != null && (!keyForListOnly || isListFile)) {
                encrypted ^= (aWzKey[i] & 0xFF) | ((aWzKey[i + 1] & 0xFF) << 8);
            }
            data[i] = (byte) encrypted;
            data[i + 1] = (byte) (encrypted >> 8);
        }
    }
}
