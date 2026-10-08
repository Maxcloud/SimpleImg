package img.crypto;

import img.json.KeyFileRepository;
import img.WzVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class WzCryptography {
    private final Logger logger = LoggerFactory.getLogger(WzCryptography.class);

    private byte[] iv;
    private byte[] secret;
    private final int version;

    public WzCryptography(KeyFileRepository<WzVersion> repository) {
        this.version = repository.getVersion();
        this.secret = repository.getSecret();

        setInitializationVector();
        setEncryptionKey(this.iv);
    }

    public byte[] getSecret() {
        return secret;
    }

    public void setInitializationVector() {
        byte[] initial;
        if (version <= 55) {
            // BMS classic era: zero IV. The derived key is all zeros, so
            // strings carry nothing but the 0xAA+i / 0xAAAA+i masks.
            initial = new byte[4];
        } else if (version >= 117) {
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
        /*boolean zeroIv = true;
        for (byte b : iv) {
            if (b != 0) {
                zeroIv = false;
                break;
            }
        }*/

        byte[] key = new byte[0x200000];
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secret, "AES"));
            for (int i = 0; i < (0xFFFF / 16); i++) {
                iv = cipher.doFinal(iv);
                System.arraycopy(iv, 0, key, (i * 16), 16);
            }
        } catch (Exception e) {
            logger.warn("An error occurred while setting the encryption key: {}", e.getMessage());
        }
        this.secret = key;
    }

}
