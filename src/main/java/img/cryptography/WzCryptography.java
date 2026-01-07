package img.cryptography;

import img.cache.KeyFileRepository;
import img.record.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class WzCryptography {

    Logger log = LoggerFactory.getLogger(WzCryptography.class);

    private static WzCryptography instance;

    private WzCryptography() { }

    private byte[] iv;
    private byte[] secret;
    private int version;

    public WzCryptography(KeyFileRepository<Version> repository) {
        this.version = repository.getVersion();
        this.secret = repository.getSecret();

        setInitializationVector();
        setEncryptionKey(this.iv);
    }

    public byte[] getSecret() {
        return secret;
    }

    public static synchronized WzCryptography getInstance() {
        if (instance == null) {
            instance = new WzCryptography();
        }
        return instance;
    }

    public void setInitializationVector() {
        byte[] initial;
        if ((version <= 55 || version >= 117)) {
            initial = new byte[] {0x00, 0x00, (byte) 0x00, 0x00};
        } else {
            initial = new byte[] {0x4D, 0x23, (byte) 0xC7, 0x2B};
        }
        byte[] iv = new byte[16];
        for (byte b = 0; b < iv.length; b++) {
            iv[b] = initial[b % 4];
        }
        this.iv = iv;
    }

    public void setEncryptionKey(byte[] iv) {
        if ((version <= 55 || version >= 117)) {
            this.secret = this.iv;
        } else {
            byte[] key = new byte[0x200000];
            try {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secret, "AES"));
                for (int i = 0; i < (0xFFFF / 16); i++) {
                    iv = cipher.doFinal(iv);
                    System.arraycopy(iv, 0, key, (i * 16), 16);
                }
            } catch (Exception e) {
                log.warn("An error occurred while setting the encryption key: {}", e.getMessage());
            }
            this.secret = key;
        }
    }

}
