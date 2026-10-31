package img.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Generates the 64 KiB WZ string key shared by the classic wz eras:
 * strings are decrypted as
 * {@code data[i] ^ (0xAA + i) ^ wzKey[i]} for ASCII and
 * {@code char[i] ^ (0xAAAA + i) ^ (wzKey[2i] | wzKey[2i+1] << 8)} for
 * unicode. The key is an iterated AES-256-ECB chain over the classic
 * spaced user key (the "13 08 06 B4 1B 0F 33 52" ZLZ.dll key) seeded
 * with the client's IV.
 *
 * <p>The chain seed differs per era: GMS v56 to v92 use the well-known
 * {@code 4D 23 C7 2B} IV, while v55 and below use {@code B9 7D 63 E9}
 * (read from the v55 client's ZLZ.dll at 0x10040; verified against its
 * List.wz and Mob.wz). The 32-byte table embedded in ZLZ.dll at 0x10060
 * — the key packed in version.json as "8:55:1" — is a different,
 * non-string-cipher table and must not be used to generate this key.
 *
 * Later versions (v93 onwards) moved to the modern per-version secret
 * scheme handled by {@link WzAsciiString}.
 */
public class WzKeyGenerator {

    /** Spaced 32-byte AES user key of the classic ZLZ.dll string cipher. */
    private static final byte[] ZLZ_USER_KEY = {
            0x13, 0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00,
            0x06, 0x00, 0x00, 0x00, (byte) 0xB4, 0x00, 0x00, 0x00,
            0x1B, 0x00, 0x00, 0x00, 0x0F, 0x00, 0x00, 0x00,
            0x33, 0x00, 0x00, 0x00, 0x52, 0x00, 0x00, 0x00
    };

    /** GMS IV (CAESCipher::s_BasicKey), little-endian in the chain input. */
    private static final byte[] IV_GMS = {0x4D, 0x23, (byte) 0xC7, 0x2B};

    /** BMS IV embedded in the v55 client's ZLZ.dll at 0x10040. */
    public static final byte[] IV_BMS = {(byte) 0xB9, 0x7D, 0x63, (byte) 0xE9};

    private static final int KEY_SIZE = 65536;

    private static volatile byte[] oWzKeyGms;
    private static volatile byte[] oWzKeyBms;

    private WzKeyGenerator() {
    }

    /**
     * Returns the 64 KiB WZ string key for the GMS IV (v56 to v92),
     * generating it on first use.
     */
    public static byte[] generateWzKey() {
        return generateWzKey(IV_GMS);
    }

    /**
     * Returns the 64 KiB WZ string key chained from the given 4-byte IV.
     * Block 0 encrypts the IV repeated as a 4-byte pattern; every
     * following block encrypts the previous one.
     */
    public static byte[] generateWzKey(byte[] iv) {
        if (java.util.Arrays.equals(iv, IV_GMS)) {
            byte[] result = oWzKeyGms;
            if (result != null) {
                return result;
            }
            result = chain(iv);
            oWzKeyGms = result;
            return result;
        }
        if (java.util.Arrays.equals(iv, IV_BMS)) {
            byte[] result = oWzKeyBms;
            if (result != null) {
                return result;
            }
            result = chain(iv);
            oWzKeyBms = result;
            return result;
        }
        return chain(iv);
    }

    private static byte[] chain(byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(ZLZ_USER_KEY, "AES"));

            byte[] block = new byte[16];
            for (int j = 0; j < 16; j++) {
                block[j] = iv[j % 4];
            }

            byte[] key = new byte[KEY_SIZE];
            for (int i = 0; i < KEY_SIZE; i += 16) {
                block = cipher.doFinal(block);
                System.arraycopy(block, 0, key, i, 16);
            }
            return key;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate the WZ string key.", e);
        }
    }
}
