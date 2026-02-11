package img.io.impl;

import img.crypto.WzCryptography;
import img.crypto.WzDecodeRecord;
import img.crypto.WzStringHandler;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Custom input stream for reading encoded strings and numbers from a binary file format.
 * Extends {@link ImgRecyclableSeekableStream} to add decryption logic using {@link WzCryptography}.
 */
public class ImgInputStream extends ImgRecyclableSeekableStream {

    private final WzStringHandler handle;
    private final byte[] secret;
    private final boolean isListImg;

    /**
     * Constructs a new stream by loading the img file into memory.
     *
     * @param path the path to the binary file
     */
    public ImgInputStream(Path path, WzStringHandler handle, byte[] secret) {
        super(path);
        this.handle = handle;
        this.secret = secret;
        this.isListImg = handle.isListImg(path);
    }

    public WzStringHandler getHandle() {
        return handle;
    }

    public byte[] getSecret() {
        return secret;
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

        int len = (b == 0x7F || b == -128) ? readInt() : (b >= 0 ? b : -b);
        if (len < 0) {
            throw new IllegalStateException("String length cannot be negative: " + len);
        }

        boolean isUnicode = (b > 0);
        byte[] data = readBytes(isUnicode ? len * 2 : len);

        byte[] clone = data.clone();
        WzDecodeRecord record = handle.decode(isUnicode, clone, 0, isListImg);

        String result = new String(
                record.data(),
                record.charset()
        );

        Objects.requireNonNull(result, "Be sure to provide a valid string decoding implementation.");
        return result;
    }
}

