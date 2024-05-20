package cdn.simple.img;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class WzEncryption {

    private byte[] iv;
    private byte[] key;

    private static WzEncryption instance;

    private final byte[] secret = new byte[] {
        (byte) 0x13, 0x00, 0x00, 0x00,
        (byte) 0x08, 0x00, 0x00, 0x00,
        (byte) 0x06, 0x00, 0x00, 0x00,
        (byte) 0xB4, 0x00, 0x00, 0x00,
        (byte) 0x1B, 0x00, 0x00, 0x00,
        (byte) 0x0F, 0x00, 0x00, 0x00,
        (byte) 0x33, 0x00, 0x00, 0x00,
        (byte) 0x52, 0x00, 0x00, 0x00};

    private WzEncryption() {
        setInitializationVector();
        setEncryptionKey(this.iv, this.secret);
    }

    public static synchronized WzEncryption getInstance() {
        if (instance == null) {
            instance = new WzEncryption();
        }
        return instance;
    }

    public byte[] getKey() {
        return key;
    }

    private void setInitializationVector() {
        byte[] initial = new byte[] {0x4d, 0x23, (byte) 0xc7, 0x2b};
        byte[] iv = new byte[16];
        for (byte b = 0; b < iv.length; b++) {
            iv[b] = initial[b % 4];
        }
        this.iv = iv;
    }

    private void setEncryptionKey(byte[] iv, byte[] secret) {
        byte[] key = new byte[0x200000];
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secret, "AES"));
            for (int i = 0; i < (0xFFFF / 16); i++) {
                iv = cipher.doFinal(iv);
                System.arraycopy(iv, 0, key, (i * 16), 16);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.key = key;
    }

}
