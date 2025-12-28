package wz;

import img.io.ImgSeekableInputStream;
import lombok.Getter;

import java.nio.file.Path;

@Getter
public class WzSeekableInputStream extends ImgSeekableInputStream {

    private final int fileStart;

    private final long fileSize;
    private final String fileIdentification;
    private final String fileCopyright;

    private final WzVersion version;

    /**
     * Constructs a new stream by loading the wz file into memory.
     *
     * @param path the path to the binary file
     */
    public WzSeekableInputStream(Path path) {
        super(Path.of(""), path);

        fileIdentification = readAsciiString(4);
        fileSize = readLong();
        fileStart = readInt();
        fileCopyright = readNullTerminatedAsciiString();
        version = new WzVersion(this);
    }

    public String decodeStringBlock(byte type) {
        String result = null;
        switch (type) {
            case 0x02:
                result = decodeStringAtOffsetAndReset(readInt() + getFileStart() + 1); break;
            case 0x03:
            case 0x04:
                result = decodeString(); break;
        }
        return result;
    }

    /**
     * Reads an offset from the stream, decrypts it, and returns the absolute position.
     * The offset is calculated based on the current position and the file start.
     *
     * @return the decrypted offset
     */
    public int readOffset() {

        int currentPosition = (int) getPosition();
        int fileHash = getVersion().getHash();

        // calculate initial offset
        int relativePosition = currentPosition - getFileStart();
        relativePosition = ~relativePosition;

        int initialOffset = relativePosition * fileHash;
        initialOffset -= 0x581C3F6D;

        // rotate left
        initialOffset = rotate_left(initialOffset, (byte) (initialOffset & 0x1F));

        int encryptedOffset = readInt();

        // decrypt offset
        int decryptedOffset = initialOffset ^ encryptedOffset;
        decryptedOffset &= 0xffffffff;

        return (decryptedOffset + (getFileStart()* 2));
    }

    /**
     * Rotates the bits of an integer to the left by a specified number of positions.
     *
     * @param x the integer to rotate
     * @param n the number of positions to rotate
     * @return the rotated integer
     */
    private int rotate_left(int x, byte n) {
        return (x << n) | (x >>> (32 - n));
    }
}
