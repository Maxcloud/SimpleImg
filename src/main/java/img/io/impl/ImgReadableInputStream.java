package img.io.impl;

import img.crypto.WzCryptography;
import img.util.StringWriter;

import java.nio.file.Path;

/**
 * Custom input stream for reading encoded strings and numbers from a binary file format.
 * Extends {@link RecyclableSeekableStream} to add decryption logic using {@link WzCryptography}.
 */
public class ImgReadableInputStream extends RecyclableSeekableStream {
    private final StringWriter stringWriter = new StringWriter();

    private final Path name;

    private final byte[] secret;

    /**
     * Constructs a new stream by loading the img file into memory.
     *
     * @param path the path to the binary file
     */
    public ImgReadableInputStream(Path name, Path path, byte[] secret) {
        super(path, secret);
        this.name = name;
        this.secret = secret;
    }

    public Path getName() {
        return name;
    }

    public StringWriter getStringWriter() {
        return stringWriter;
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
     * Decodes a string from a given offset, then restores the stream position.
     *
     * @param offset the offset in the file where the string is located
     * @return the decoded string
     */
    public String decodeStringAtOffsetAndReset(long offset) {
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

        /*int len;
        if (b > 0) {
            len = b == Byte.MAX_VALUE ? readInt() : b;
        } else {
            len = b == Byte.MIN_VALUE ? readInt() : -b;
        }

        if (len <= 0) {
            return "";
        }*/

        int len = (b == 0x7F || b == -128) ? readInt() : (b >= 0 ? b : -b);
        if (len < 0) {
            throw new IllegalStateException("String length cannot be negative: " + len);
        }

        char[] str = new char[len];
        if (b > 0) {
            decodeUnicodeString(str);
        } else {
            decodeNonUnicodeString(str);
        }

        return String.valueOf(str);
    }

    /**
     * Decodes a Unicode string using a character XOR mask.
     *
     * @param str the character array to fill
     */
    private void decodeUnicodeString(char[] str) {
        char mask = (char) 0xAAAA;
        int len = str.length;
        for (int i = 0; i < len; i++) {
            char chr = readChar();
            str[i] = (char) (chr ^ mask++);
        }
    }

    /**
     * Decodes a non-Unicode string using a byte XOR key.
     *
     * @param str the character array to fill
     */
    private void decodeNonUnicodeString(char[] str) {
        byte mask = (byte) 0xAA;
        int len = str.length;
        for (int i = 0; i < len; i++, mask++) {
            byte cipherByte = readByte();
            byte keyByte = (byte) (secret[i % secret.length] ^ mask);
            str[i] = (char) ((cipherByte ^ keyByte) & 0xFF);
        }
    }
}

