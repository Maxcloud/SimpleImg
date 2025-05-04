package img.io;

import img.crypto.WzEncrypt;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

/**
 * Custom input stream for reading encoded strings and numbers from a binary file format.
 * Extends {@link RecyclableSeekableStream} to add decryption logic using {@link WzEncrypt}.
 */
@Slf4j
public class CustomSeekableInputStream extends RecyclableSeekableStream {

    private final WzEncrypt encryption = WzEncrypt.getInstance();

    /**
     * Constructs a new stream by loading the file into memory.
     *
     * @param filePath the path to the binary file
     */
    public CustomSeekableInputStream(Path filePath) {
        super(filePath);
    }

    /**
     * Decodes a string from a given offset, then restores the stream position.
     *
     * @param offset the offset in the file where the string is located
     * @return the decoded string
     */
    public String decodeStringAtOffsetAndReset(int offset) {
        long pos = getPosition();
        try {
            seek(offset);
            return decodeString();
        } finally {
            seek(pos);
        }
    }

    /**
     * Decodes a string from the current position.
     * String can be ASCII or Unicode, with custom length encoding.
     *
     * @return the decoded string
     */
    public String decodeString() {
        byte b = readByte();
        if (b == 0x00) return "";

        int len = (b == 0x7F || b == -128) ? readInt() : (b >= 0 ? b : -b);
        if (len < 0) {
            throw new IllegalStateException("String length cannot be negative: " + len);
        }

        char[] str = new char[len];
        if (b >= 0) {
            decodeUnicodeString(str, len);
        } else {
            decodeNonUnicodeString(str, len);
        }

        return String.valueOf(str);
    }

    /**
     * Decodes a string block depending on the block type.
     *
     * @param type the type indicator byte
     * @return the decoded string, or null if the type is unknown
     */
    public String decodeStringBlock(byte type) {
        String result = null;
        switch (type) {
            case 0x00: // subdirectories
            case 0x73:
                result = decodeString();
                break;
            case 0x01: // relative subdirectories
            case 0x1B:
                int num1 = readInt();
                result = decodeStringAtOffsetAndReset(num1);
                break;
            default:
                log.error("An unhandled type ({}) has been found. ", type);
                break;
        }
        return result;
    }

    /**
     * Decodes an integer with custom encoding.
     * If the byte is -128, the next 4 bytes form the integer; otherwise, the byte itself is used.
     *
     * @return the decoded integer
     */
    public int decodeInt() {
        byte b = readByte();
        return (b == -128 ? readInt() : b);
    }

    /**
     * Decodes a long with custom encoding.
     * If the byte is -128, the next 8 bytes form the long; otherwise, the byte itself is used.
     *
     * @return the decoded integer
     */
    public long decodeLong() {
        byte b = readByte();
        return (b == -128) ? readLong() : (b & 0xFF);
    }

    /**
     * Decodes a float with custom encoding.
     * If the byte is -128, the next 4 bytes form the float; otherwise, the byte value is returned as a float.
     *
     * @return the decoded float
     */
    public float decodeFloat() {
        byte b = readByte();
        return (b == -128 ? readFloat() : b);
    }

    /**
     * Decodes a Unicode string using a character XOR mask.
     *
     * @param str the character array to fill
     * @param len the number of characters to decode
     */
    private void decodeUnicodeString(char[] str, int len) {
        char[] mask = encryption.getXorCharArray();
        for (int i = 0; i < len; i++) {
            char chr = readChar();
            str[i] = (char) (chr ^ mask[i]);
        }
    }

    /**
     * Decodes a non-Unicode string using a byte XOR key.
     *
     * @param str the character array to fill
     * @param len the number of characters to decode
     */
    private void decodeNonUnicodeString(char[] str, int len) {
        byte[] xorKey = encryption.getXorKey(len);
        for (int i = 0; i < len; i++) {
            str[i] = (char) ((readByte() ^ xorKey[i]) & 0xFF);
        }
    }
}

