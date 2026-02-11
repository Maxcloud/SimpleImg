package img.crypto;

public class WzAlphabet {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private byte[] aEncryptedAlphabet;

    public WzAlphabet() {
        encryptAlphabet();
    }

    private void encryptAlphabet() {
        byte[] aAlphabet = ALPHABET.getBytes();
        aEncryptedAlphabet = aAlphabet.clone();
        for (int i = 0; i < aAlphabet.length; i++) {
            aEncryptedAlphabet[i] ^= aAlphabet[i];
        }
    }

    public byte[] getAlphabet() {
        return aEncryptedAlphabet;
    }
}
