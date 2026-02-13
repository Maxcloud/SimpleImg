import img.crypto.WzCryptography;
import img.io.repository.KeyFileRepository;
import img.model.common.Version;
import org.junit.jupiter.api.*;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked, unused"})
public class TestWzTheorem {

    /**
     * This is a test class to verify the decryption logic for different versions of the Wz file format.
     * It includes tests for both Unicode and non-Unicode strings, as well as tests for specific versions
     * that utilize AES encryption with different keys and initialization vectors.
     */
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // Combinations
    // v55      - Aes (No AES_KEY, IV);
    // v55 List - Aes (No AES_KEY, IV2);
    // v95      - Aes with IV3
    private static final byte[] AES_KEY = {
            0x13, 0x00, 0x00, 0x00,
            0x08, 0x00, 0x00, 0x00,
            0x06, 0x00, 0x00, 0x00,
            (byte) 0xB4, 0x00, 0x00, 0x00,
            0x1B, 0x00, 0x00, 0x00,
            0x0F, 0x00, 0x00, 0x00,
            0x33, 0x00, 0x00, 0x00,
            0x52, 0x00, 0x00, 0x00
    };

    private static final byte[] IV  = { (byte) 0x4D, (byte) 0x23, (byte) 0xC7, (byte) 0x2B};
    private static final byte[] IV2 = { (byte) 0xB9, (byte) 0x7D, (byte) 0x63, (byte) 0xE9};
    private static final byte[] IV3 = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

    /**
     * This is the actual key used in the AES implementation for version 55, represented as char values.
     * Each char corresponds to a byte in the AES key, but since Java chars are 16-bit, we need to ensure
     * that the values are correctly represented as bytes when used in the AES algorithm.
     */
    public final static char[] MODERN_UNI_KEY = new char[] {
            (char) 26027, (char) 1353,  (char) 52583, (char) 2647,
            (char) 31640, (char) 2695,  (char) 26092, (char) 35591,
            (char) 29845, (char) 27702, (char) 22963, (char) 24105,
            (char) 22946, (char) 32259, (char) 32191, (char) 29899,
            (char) 21392, (char) 37926, (char) 28440, (char) 34657,
            (char) 54992, (char) 7801,  (char) 21164, (char) 21225,
            (char) 31362, (char) 59422
    };

    /**
     * This is the actual key used in the AES implementation for version 55, represented as byte values.
     * Each byte corresponds to a char in the original MODERN_UNI_KEY, but since Java chars are 16-bit,
     * we need to ensure that the values are correctly represented as bytes when used in the AES algorithm.
     */
    public static final byte[] MODERN_KEY_BYTES = new byte[] {
            (byte)0xAB, (byte)0x65, (byte)0x49, (byte)0x05,
            (byte)0x67, (byte)0xCD, (byte)0x57, (byte)0x0A,
            (byte)0x98, (byte)0x7B, (byte)0x87, (byte)0x0A,
            (byte)0xEC, (byte)0x65, (byte)0x07, (byte)0x8B,
            (byte)0x95, (byte)0x74, (byte)0x36, (byte)0x6C,
            (byte)0xB3, (byte)0x59, (byte)0x29, (byte)0x5E,
            (byte)0xA2, (byte)0x59, (byte)0x03, (byte)0x7E,
            (byte)0xBF, (byte)0x7D, (byte)0xCB, (byte)0x74,
            (byte)0x90, (byte)0x53, (byte)0x26, (byte)0x94,
            (byte)0x18, (byte)0x6F, (byte)0x61, (byte)0x87,
            (byte)0xD0, (byte)0xD6, (byte)0x79, (byte)0x1E,
            (byte)0xAC, (byte)0x52, (byte)0xE9, (byte)0x52,
            (byte)0x82, (byte)0x7A, (byte)0x1E, (byte)0xE8
    };

    /**
     * This is the actual key used in the AES implementation for version 55, represented as char
     * values in hexadecimal. Each char corresponds to a byte in the AES key, but since Java chars
     * are 16-bit, we need to ensure that the values are correctly represented as bytes when used
     * in the AES algorithm.
     */
    private static final char[] MODERN_UNI_KEY_HEX = {
            0x65AB, 0x0549, 0xCD67, 0x0A57, 0x7B98, 0x0A87, 0x65DC, 0x8B07,
            0x7495, 0x6C36, 0x59B3, 0x5E29, 0x59A2, 0x7E03, 0x7DBF, 0x74CB,
            0x5390, 0x9426, 0x6F18, 0x8761, 0xD6D0, 0x1E79, 0x52AC, 0x52E9,
            0x7A82, 0xE81E
    };

    @Test
    void test_version_55_unicode() {

        byte[] aAlphabet = ALPHABET.getBytes();
        byte[] clAlphabet = aAlphabet.clone();
        for (int i = 0; i < aAlphabet.length; i++) {
            clAlphabet[i] ^= aAlphabet[i];
        }

        byte[] oPropertyBytes = new byte[]{
                (byte) 0x1A,
                (byte) 0x6C,
                (byte) 0xEF,
                (byte) 0x14,
                (byte) 0x94,
                (byte) 0x6B,
                (byte) 0x15,
                (byte) 0x78
        };

        byte[] data = oPropertyBytes.clone();
        decodeUnicodeString(data, clAlphabet);

        String value = new String(data, StandardCharsets.UTF_16LE);
        assertEquals("우비세트", value, "Decrypted name does not match expected value!");
        System.out.println("✅ SUCCESS! Decrypted 우비세트!");
    }

    @Test
    void test_version_55_non_unicode() {

        byte[] aAlphabet = ALPHABET.getBytes();
        byte[] clAlphabet = aAlphabet.clone();
        for (int i = 0; i < aAlphabet.length; i++) {
            clAlphabet[i] ^= aAlphabet[i];
        }

        byte[] oPropertyBytes = new byte[]{
                (byte) 0xD0, (byte) 0xC6, (byte) 0xCD, (byte) 0xDD,
                (byte) 0x80, (byte) 0xC6, (byte) 0xDD, (byte) 0xD6
        };

        byte[] data = oPropertyBytes.clone();
        decodeNonUnicodeString(data, clAlphabet);

        String value = new String(data);
        assertEquals("zmap.img", value, "Decrypted name does not match expected value!");
        System.out.println("✅ SUCCESS! Decrypted Property!");
    }

    @Test
    void test_version_83_non_unicode() {
        byte[] secret = getSecret(83);

        byte[] oPropertyBytes = new byte[]{
                (byte) 0x6C, (byte) 0x77, (byte) 0xFC, (byte) 0x79,
                (byte) 0x83, (byte) 0x27, (byte) 0x19, (byte) 0x58
        };

        byte[] data = oPropertyBytes.clone();
        decodeNonUnicodeString(data, secret);

        String value = new String(data);
        assertEquals("Property", value, "Decrypted name does not match expected value!");
        System.out.println("✅ SUCCESS! Decrypted Property!");
    }

    @Test
    void test_version_95() {
        byte[] secret = getSecret(95);

        byte[] oPropertyBytes = new byte[]{
                (byte) 0x4F, (byte) 0x68, (byte) 0xF2, (byte) 0x79,
                (byte) 0xC8, (byte) 0x3C, (byte) 0x0, (byte) 0x46
        };

        byte[] data = oPropertyBytes.clone();
        decodeNonUnicodeString(data, secret);

        String value = new String(data);
        assertEquals("smap.img", value, "Decrypted name does not match expected value!");
        System.out.println("✅ SUCCESS! Decrypted smap.img!");
    }

    private byte[] getSecret(int version) {
        KeyFileRepository<Version> repository = mock(KeyFileRepository.class);

        when(repository.getVersion()).thenReturn(version);
        when(repository.getSecret()).thenReturn(TestWzTheorem.AES_KEY);

        WzCryptography cryptography = new WzCryptography(repository);
        return cryptography.getSecret();
    }

    private void decodeNonUnicodeString(byte[] data, byte[] secret) {
        byte mask = (byte) 0xAA;

        for (int i = 0; i < data.length; i++) {
            data[i] = (byte)(data[i] ^ mask);
            mask++;
        }

        for (int i = 0; i < data.length; i++) {
            data[i] ^= secret[i % secret.length];
        }
    }

    private void decodeUnicodeString(byte[] data, byte[] secret) {
        int mask = 0xAAAA;

        for (int i = 0; i < data.length / 2; i++) {
            byte pos = (byte) (i * 2);

            byte low = data[pos];
            byte high = data[pos + 1];
            int encrypted = (low & 0xFF) | ((high & 0xFF) << 8);
            encrypted ^= mask++;
            data[pos] = (byte) encrypted;
            data[pos + 1] = (byte) (encrypted >> 8);
        }

        /*for (int i = 0; i < data.length; i++) {
            data[i] ^= secret[i % secret.length];
        }*/
    }

}
