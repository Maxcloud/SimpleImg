package img.crypto;
public class WzVersion55AsciiString implements WzStringDecodeImpl {

    private final byte[] secret;
    private final byte[] aAlphabet;

    public WzVersion55AsciiString(byte[] secret) {
        this.secret = secret;
        WzAlphabet alphabet = new WzAlphabet();
        aAlphabet = alphabet.getAlphabet();
    }

    @Override
    public void decode(byte[] data, int len, boolean isListFile) {
        byte mask = (byte) 0xAA;
        byte[] keyArray = isListFile ? secret : aAlphabet;
        for (int i = 0; i < data.length; i++) {
            data[i] ^= (byte) (keyArray[i % keyArray.length] ^ mask++);
        }
    }
}
