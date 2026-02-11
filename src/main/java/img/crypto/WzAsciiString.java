package img.crypto;
public class WzAsciiString implements WzStringDecodeImpl {

    private final byte[] secret;
    private final byte[] aAlphabet;

    public WzAsciiString(byte[] secret) {
        this.secret = secret;
        WzAlphabet alphabet = new WzAlphabet();
        aAlphabet = alphabet.getAlphabet();
    }

    @Override
    public void decode(byte[] data, int len, boolean isListFile) {
        /*byte mask = (byte) 0xAA;

        byte[] keyArray = isListFile ? secret : aAlphabet;
        for (int i = 0; i < data.length; i++) {
            data[i] ^= (byte) (keyArray[i % keyArray.length] ^ mask++);
        }*/

        if (isListFile) { // modern img file is using Aes ;)
            byte mask = (byte) 0xAA;
            for (int i = 0; i < data.length; i++) {
                byte keyByte = (byte) (secret[i % secret.length] ^ mask);
                data[i] = (byte) ((data[i] ^ keyByte) & 0xFF);
                mask++;
            }
        } else {
            /*byte mask = (byte) 0xAA;
            for (int i = 0; i < data.length; i++) {
                byte keyByte = (byte) (secret[i % secret.length] ^ mask);
                data[i] = (byte) ((data[i] ^ keyByte) & 0xFF);
                mask++;
            }*/
            byte mask = (byte) 0xAA;
            for (int i = 0; i < data.length; i++) {
                data[i] ^= (byte)(mask++ ^ aAlphabet[i % aAlphabet.length]);
            }
        }
    }
}
