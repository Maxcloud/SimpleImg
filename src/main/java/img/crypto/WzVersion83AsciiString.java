package img.crypto;
public class WzVersion83AsciiString implements WzStringDecodeImpl {

    private final byte[] secret;

    public WzVersion83AsciiString(byte[] secret) {
        this.secret = secret;
    }

    @Override
    public void decode(byte[] data, int len, boolean isListFile) {
        byte mask = (byte) 0xAA;
        for (int i = 0; i < data.length; i++) {
            byte keyByte = (byte) (secret[i % secret.length] ^ mask);
            data[i] = (byte) ((data[i] ^ keyByte) & 0xFF);
            mask++;
        }
    }
}
