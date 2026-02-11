package img;

import img.io.repository.KeyFileRepository;
import img.model.common.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class WzTestCryptography {

    Logger log = LoggerFactory.getLogger(WzTestCryptography.class);

    private static WzTestCryptography instance;

    private WzTestCryptography() { }

    private byte[] iv;
    private byte[] secret;
    private int version;

    public WzTestCryptography(KeyFileRepository<Version> repository) {
        this.version = repository.getVersion();
        this.secret = repository.getSecret();

        setInitializationVector();
        setEncryptionKey(this.iv);
    }

    public WzTestCryptography(byte[] secret, int version) {
        this.secret = secret;
        this.version = version;

        setInitializationVector();
        setEncryptionKey(this.iv);
    }

    public byte[] getSecret() {
        return secret;
    }

    public static synchronized WzTestCryptography getInstance() {
        if (instance == null) {
            instance = new WzTestCryptography();
        }
        return instance;
    }

    public void setInitializationVector() {
        byte[] initial;
        if ((version <= 55)) { // 117
            initial = new byte[] {(byte) 0xB9, 0x7D, 0x63, (byte) 0xE9};
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
