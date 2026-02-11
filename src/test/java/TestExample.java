import img.crypto.WzCryptography;
import img.io.repository.KeyFileRepository;
import img.model.common.Version;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestExample {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // Combinations
    // v55 - No Aes (aeskey2, iv2)
    // v95 - Aes with IV3
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

    private static final byte[] AES_KEY2 = {
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00
    };

    private static final byte[] IV = {0x4D, 0x23, (byte) 0xC7, 0x2B};
    private static final byte[] IV2 = {(byte) 0xB9, 0x7D, 0x63, (byte) 0xE9};
    private static final byte[] IV3 = {0x00, 0x00, (byte) 0x00, 0x00};

    // public final static char[] MODERN_UNI_KEY = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public final static char[] MODERN_UNI_KEY = new char[] {
            (char) 26027, (char) 1353,  (char) 52583, (char) 2647,
            (char) 31640, (char) 2695,  (char) 26092, (char) 35591,
            (char) 29845, (char) 27702, (char) 22963, (char) 24105,
            (char) 22946, (char) 32259, (char) 32191, (char) 29899,
            (char) 21392, (char) 37926, (char) 28440, (char) 34657,
            (char) 54992, (char) 7801,  (char) 21164, (char) 21225,
            (char) 31362, (char) 59422
    };

    // Modern key as bytes (52 bytes)
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

    // Your original MODERN_UNI_KEY as hex values
    private static final char[] MODERN_UNI_KEY_HEX = {
            0x65AB, 0x0549, 0xCD67, 0x0A57, 0x7B98, 0x0A87, 0x65DC, 0x8B07,
            0x7495, 0x6C36, 0x59B3, 0x5E29, 0x59A2, 0x7E03, 0x7DBF, 0x74CB,
            0x5390, 0x9426, 0x6F18, 0x8761, 0xD6D0, 0x1E79, 0x52AC, 0x52E9,
            0x7A82, 0xE81E
    };

    public final static char[] MODERN_UNI_KEY2 = new char[] {
            (char) 26027, (char) 1353, (char) 52583, (char) 2647,
            (char) 31640, (char) 2695, (char) 26092, (char) 35591,
            (char) 29845, (char) 27702, (char) 22963, (char) 24105,
            (char) 22946, (char) 32259, (char) 32191, (char) 29899,
            (char) 21392, (char) 37926, (char) 28440, (char) 34657,
            (char) 54992, (char) 7801, (char) 21164, (char) 21225,
            (char) 31362, (char) 59422
    };

    // Convert to char array (for backward compatibility)
    private final static char[] MODERN_KEY = new char[MODERN_UNI_KEY_HEX.length * 2];
    static {
        for (int i = 0; i < MODERN_UNI_KEY_HEX.length; i++) {
            MODERN_KEY[i * 2 + 1] = (char) (MODERN_UNI_KEY_HEX[i] >> 8);
            MODERN_KEY[i * 2]= (char) ((MODERN_UNI_KEY_HEX[i]) & 0xFF);
        }
    }

    private static final Path configFile = Path.of("src/main/resources/configuration.json");

    @Mock
    private KeyFileRepository<Version> repository;

    @Test
    void test_version_55_one_off() {

        byte[] aAlphabet = ALPHABET.getBytes();
        byte[] clAlphabet = aAlphabet.clone();
        for (int i = 0; i < aAlphabet.length; i++) {
            clAlphabet[i] ^= aAlphabet[i];
        }

        byte[] peanut = {
                (byte) 0xDE,
                (byte) 0xC4,
                (byte) 0xD5,
                (byte) 0xF9,
                (byte) 0xC1,
                (byte) 0xD8,
                (byte) 0xD5,
                (byte) 0xC3,
                (byte) 0xFB,
                (byte) 0xDD,
                (byte) 0xC7,
                (byte) 0xDC,
                (byte) 0xD2,
                (byte) 0xD2,
                (byte) 0xE9,
                (byte) 0xCC,
                (byte) 0xDF,
                (byte) 0xC8,
                (byte) 0xC8,
                (byte) 0xFF,
                (byte) 0xD1,
                (byte) 0xCC,
                (byte) 0xB3,
                (byte) 0xEF,
                (byte) 0xAB,
                (byte) 0xAE,
                (byte) 0xA3
        };


        byte[] pea = peanut.clone();
        decodeNonUnicodeString(pea, clAlphabet);

        String value = new String(pea);
        assertEquals("toyTowerInsideQuestBoss.img", value, "img file name does not match expected value!");
        System.out.println(value);
    }

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
        System.out.println("\n✅ SUCCESS! Decrypted 우비세트!");
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
        System.out.println("\n✅ SUCCESS! Decrypted Property!");
    }

    @Test
    void test_version_83_unicode() {
        byte[] secret = getSecret(AES_KEY, 83);

        byte[] oPropertyBytes = new byte[]{
                (byte) 0x40,
                (byte) 0xC1,
                (byte) 0xD0,
                (byte) 0xB0,
                (byte) 0xB8,
                (byte) 0x85,
                (byte) 0x50,
                (byte) 0x3A,
                (byte) 0x79,
                (byte) 0x6E,
                (byte) 0xAA,
                (byte) 0x78,
                (byte) 0x7B,
                (byte) 0xDC,
                (byte) 0xE9,
                (byte) 0xC2,
                (byte) 0x59,
                (byte) 0xB6,
                (byte) 0x17,
                (byte) 0xE7,
                (byte) 0x54,
                (byte) 0xFC
        };

        byte[] data = oPropertyBytes.clone();
        decodeUnicodeString(data, secret);

        String value = new String(data);
        assertEquals("毪᩻⼔都쓗툅盋桘ᳫ䶤因", value, "Decrypted name does not match expected value!");
        System.out.println("\n✅ SUCCESS! Decrypted Property!");
    }

    @Test
    void test_version_83_non_unicode() {
        byte[] secret = getSecret(AES_KEY, 83);

        byte[] oPropertyBytes = new byte[]{
                (byte) 0x6C, (byte) 0x77, (byte) 0xFC, (byte) 0x79,
                (byte) 0x83, (byte) 0x27, (byte) 0x19, (byte) 0x58
        };

        byte[] data = oPropertyBytes.clone();
        decodeNonUnicodeString(data, secret);

        String value = new String(data);
        assertEquals("Property", value, "Decrypted name does not match expected value!");
        System.out.println("\n✅ SUCCESS! Decrypted Property!");
    }

    @Test
    void test_version_95() {
        byte[] secret = getSecret(AES_KEY, 95);

        byte[] oPropertyBytes = new byte[]{
                (byte) 0x4F, (byte) 0x68, (byte) 0xF2, (byte) 0x79,
                (byte) 0xC8, (byte) 0x3C, (byte) 0x0, (byte) 0x46
        };

        byte[] data = oPropertyBytes.clone();
        decodeNonUnicodeString(data, secret);

        String value = new String(data);
        assertEquals("smap.img", value, "Decrypted name does not match expected value!");
        System.out.println("\n✅ SUCCESS! Decrypted smap.img!");
    }

    private byte[] getSecret(byte[] secret, int version) {

        when(repository.getVersion()).thenReturn(version);
        when(repository.getSecret()).thenReturn(secret);

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
