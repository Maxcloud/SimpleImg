package img.crypto;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

@Getter
@Slf4j
public class WzEncrypt {

    private static final int ALLOC_SIZE = 4096;

    private byte[] iv;
    private byte[] key;
    private final char[] xorCharArray;

    private static WzEncrypt instance;

    private final byte[] secret = new byte[] {
        (byte) 0x13, 0x00, 0x00, 0x00,
        (byte) 0x08, 0x00, 0x00, 0x00,
        (byte) 0x06, 0x00, 0x00, 0x00,
        (byte) 0xB4, 0x00, 0x00, 0x00,
        (byte) 0x1B, 0x00, 0x00, 0x00,
        (byte) 0x0F, 0x00, 0x00, 0x00,
        (byte) 0x33, 0x00, 0x00, 0x00,
        (byte) 0x52, 0x00, 0x00, 0x00};

    private WzEncrypt() {
        setInitializationVector();
        setEncryptionKey(this.iv, this.secret);
        xorCharArray = new char[ALLOC_SIZE];
        char mask = (char) 0xAAAA;
        for (int i = 0; i < xorCharArray.length; i++) {
            xorCharArray[i] = mask++;
        }
    }

    public static synchronized WzEncrypt getInstance() {
        if (instance == null) {
            instance = new WzEncrypt();
        }
        return instance;
    }

    public byte[] getXorKey(int length) {
        byte[] xorKey = new byte[length];
        byte mask = (byte) 0xAA;
        for (int i = 0; i < length; i++) {
            xorKey[i] = (byte) (key[i] ^ mask++);
        }
        return xorKey;
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
            log.error("An error occurred while trying to set the encryption key.");
        }
        this.key = key;
    }

}
