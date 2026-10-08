package img.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Generates the 64 KiB WZ string key used by the middle-era wz files
 * (roughly v62 to v92): strings are decrypted as
 * {@code data[i] ^ (0xAA + i) ^ wzKey[i]} for ASCII and
 * {@code char[i] ^ (0xAAAA + i) ^ (wzKey[2i] | wzKey[2i+1] << 8)} for
 * unicode. The key is an iterated AES-256-ECB chain seeded with the
 * client's IV; the constants below are the well-known GMS values (the
 * same IV is embedded in ZLZ.dll at 0x10040, the user key at 0x10060).
 *
 * Earlier versions (v55 and below) predate this scheme: their key is
 * all zeros, which degenerates to the plain 0xAA+i mask handled by
 * {@link WzAlphabet}. Later versions (v93 onwards) moved to the modern
 * per-version secret scheme handled by {@link WzAsciiString}.
 */
public class WzKeyGenerator {

    /** Spaced 32-byte AES user key embedded in ZLZ.dll (GMS). */
    private static final byte[] ZLZ_USER_KEY = {
            0x13, 0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00,
            0x06, 0x00, 0x00, 0x00, (byte) 0xB4, 0x00, 0x00, 0x00,
            0x1B, 0x00, 0x00, 0x00, 0x0F, 0x00, 0x00, 0x00,
            0x33, 0x00, 0x00, 0x00, 0x52, 0x00, 0x00, 0x00
    };

    /** GMS IV (CAESCipher::s_BasicKey), little-endian in the chain input. */
    private static final byte[] IV_GMS = {0x4D, 0x23, (byte) 0xC7, 0x2B};

    private static final int KEY_SIZE = 65536;

    private static volatile byte[] oWzKey;

    private WzKeyGenerator() {
    }

    /**
     * Returns the 64 KiB WZ string key, generating it on first use.
     * Block 0 encrypts the IV repeated as a 4-byte pattern; every
     * following block encrypts the previous one.
     */
    public static byte[] generateWzKey() {
        byte[] result = oWzKey;
        if (result != null) {
            return result;
        }

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(ZLZ_USER_KEY, "AES"));

            byte[] block = new byte[16];
            for (int j = 0; j < 16; j++) {
                block[j] = IV_GMS[j % 4];
            }

            byte[] key = new byte[KEY_SIZE];
            for (int i = 0; i < KEY_SIZE; i += 16) {
                block = cipher.doFinal(block);
                System.arraycopy(block, 0, key, i, 16);
            }

            oWzKey = key;
            return key;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate the WZ string key.", e);
        }
    }
}
