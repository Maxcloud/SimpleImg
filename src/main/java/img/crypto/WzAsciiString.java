package img.crypto;
public class WzAsciiString implements IWzStringDecode {

    private final byte[] secret;
    private final byte[] aAlphabet;

    public WzAsciiString(byte[] secret) {
        this.secret = secret;
        WzAlphabet alphabet = new WzAlphabet();
        aAlphabet = alphabet.getAlphabet();
    }

    /**
     * Classic decryption with an explicit key table. The middle-era
     * wz files (v62 to v92) pass the 64 KiB key from
     * {@link WzKeyGenerator}; v55-era files keep the default
     * all-zero alphabet, degenerating to the plain 0xAA+i mask.
     */
    public WzAsciiString(byte[] secret, byte[] alphabet) {
        this.secret = secret;
        this.aAlphabet = alphabet;
    }

    @Override
    public void decode(byte[] data, int len, boolean isListFile) {
        if (isListFile) { // modern img file is using Aes
            byte mask = (byte) 0xAA;
            for (int i = 0; i < data.length; i++) {
                byte keyByte = (byte) (secret[i % secret.length] ^ mask);
                data[i] = (byte) ((data[i] ^ keyByte) & 0xFF);
                mask++;
            }
        } else {
            byte mask = (byte) 0xAA;
            for (int i = 0; i < data.length; i++) {
                data[i] ^= (byte)(mask++ ^ aAlphabet[i % aAlphabet.length]);
            }
        }
    }
}
