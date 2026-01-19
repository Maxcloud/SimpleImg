package img.io.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A recyclable seekable stream built on top of Netty's {@link ByteBuf}.
 * Designed for efficient in-memory binary file reading with support for random access (seeking).
 */
public class RecyclableSeekableStream implements AutoCloseable {

    Logger log = LoggerFactory.getLogger(RecyclableSeekableStream.class);

    private final Path path;
    private ByteBuf byteBuf;
    protected byte[] secret;


    public Path getPath() {
        return path;
    }

    public ByteBuf getByteBuf() {
        return byteBuf;
    }

    public byte[] getSecret() {
        return secret;
    }

    /**
     * Loads a file into memory and wraps it with a {@link ByteBuf} for binary access.
     *
     * @param path the path to the file to read
     */
    public RecyclableSeekableStream(Path path, byte[] secret) {
        this.path = path;
        this.secret = secret;

        try {
            byte[] data = Files.readAllBytes(path);
            byteBuf = Unpooled.wrappedBuffer(data);
        } catch (Exception e) {
            log.error("An error has occurred while loading the file to memory. ", e);
        }
    }

    /**
     * Reads a single byte from the current position.
     *
     * @return the byte read
     */
    public byte readByte() {
        return byteBuf.readByte();
    }

    /**
     * Reads a specified number of bytes from the current position.
     *
     * @param length the number of bytes to read
     * @return the byte array read
     */
    public byte[] readBytes(int length) {
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        return bytes;
    }

    /**
     * Reads a 2-byte short (little-endian) from the current position.
     *
     * @return the short read
     */
    public short readShort() {
        return byteBuf.readShortLE();
    }

    /**
     * Reads a 4-byte integer (little-endian) from the current position.
     *
     * @return the integer read
     */
    public int readInt() {
        return byteBuf.readIntLE();
    }

    /**
     * Reads a 2-byte character (little-endian) from the current position.
     *
     * @return the character read
     */
    public char readChar() {
        return (char) readShort();
    }

    /**
     * Reads an 8-byte long (little-endian) from the current position.
     *
     * @return the long read
     */
    public long readLong() {
        return byteBuf.readLongLE();
    }

    /**
     * Reads a 4-byte float (little-endian) from the current position.
     *
     * @return the float read
     */
    public float readFloat() {
        return byteBuf.readFloatLE();
    }

    /**
     * Reads an 8-byte double (little-endian) from the current position.
     *
     * @return the double read
     */
    public double readDouble() {
        return byteBuf.readDoubleLE();
    }

    /**
     * Reads a fixed-length ASCII string from the current position.
     *
     * @param n the number of bytes to read
     * @return the resulting string
     */
    public String readAsciiString(int n) {
        byte[] bytes = readBytes(n);
        return new String(bytes);
    }

    /**
     * Reads an ASCII string from the current position until a null terminator (0x00) is encountered.
     *
     * @return the resulting string (excluding the null terminator)
     */
    public String readNullTerminatedAsciiString() {
        StringBuilder sb = new StringBuilder();
        byte b = 1;
        while (b != 0) {
            b = readByte();
            if (b != 0) {
                sb.append((char) b);
            }
        }
        return sb.toString();
    }

    /**
     * Skips a specified number of bytes from the current position.
     *
     * @param num the number of bytes to skip
     */
    public void skip(int num) {
        byteBuf.skipBytes(num);
    }

    /**
     * Seeks to an absolute offset in the stream.
     *
     * @param offset the target offset to seek to
     */
    public void seek(long offset) {
        byteBuf.readerIndex((int) offset);
    }

    /**
     * Gets the current reader index (position).
     *
     * @return the current offset in the stream
     */
    public long getPosition() {
        return byteBuf.readerIndex();
    }

    /**
     * Checks if there are any bytes remaining to read.
     *
     * @return true if readable bytes remain, false otherwise
     */
    public boolean remaining() {
        return byteBuf.isReadable();
    }

    /**
     * Resets the buffer by clearing it (reader and writer index to 0).
     */
    public void reset() {
        if (byteBuf != null) {
            byteBuf.clear();
        }
    }

    /**
     * Releases the underlying {@link ByteBuf} and performs cleanup.
     */
    @Override
    public synchronized void close() {
        if (byteBuf != null) {
            try {
                if (byteBuf.refCnt() > 0) {
                    byteBuf.release();
                }
            } finally {
                byteBuf = null;
            }
        }
    }

}