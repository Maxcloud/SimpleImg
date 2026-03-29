package img.io.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A recyclable seekable stream built on top of Netty's {@link ByteBuf}.
 * Designed for efficient in-memory binary file reading with support for random access (seeking).
 */
public class ImgRecyclableSeekableStream implements AutoCloseable {

    Logger log = LoggerFactory.getLogger(ImgRecyclableSeekableStream.class);

    private static final Charset ASCII = StandardCharsets.US_ASCII;

    private Path path;
    private ByteBuf readBuf;


    public Path getPath() {
        return path;
    }

    public ByteBuf getReadBuf() {
        return readBuf;
    }

    /**
     * Loads a file into memory and wraps it with a {@link ByteBuf} for binary access.
     *
     * @param path the path to the file to read
     */
    public ImgRecyclableSeekableStream(Path path) {
        this.path = path;
        try {
            byte[] data = Files.readAllBytes(path);
            readBuf = Unpooled.wrappedBuffer(data);
        } catch (Exception e) {
            log.error("An error has occurred while loading the file to memory. ", e);
        }
    }

    public ImgRecyclableSeekableStream(Path path, byte[] data) {
        this.path = path;
        try {
            readBuf = Unpooled.wrappedBuffer(data);
        } catch (Exception e) {
            log.error("An error has occurred while loading the file to memory. ", e);
        }
    }

    public ImgRecyclableSeekableStream(ByteBuf readBuf) {
        this.readBuf = readBuf;
    }

    /**
     * Reads a single byte from the current position.
     *
     * @return the byte read
     */
    public byte readByte() {
        return readBuf.readByte();
    }

    /**
     * Reads a specified number of bytes from the current position.
     *
     * @param length the number of bytes to read
     * @return the byte array read
     */
    public byte[] readBytes(int length) {
        byte[] bytes = new byte[length];
        readBuf.readBytes(bytes);
        return bytes;
    }

    public void readFully(byte[] b) {
        readBuf.readBytes(b);
    }

    /**
     * Gets the number of readable bytes remaining in the stream.
     *
     * @return the number of readable bytes
     */
    public int readableBytes() {
        return readBuf.readableBytes();
    }

    /**
     * Reads a 2-byte short (little-endian) from the current position.
     *
     * @return the short read
     */
    public short readShort() {
        return readBuf.readShortLE();
    }

    /**
     * Reads a 4-byte integer (little-endian) from the current position.
     *
     * @return the integer read
     */
    public int readInt() {
        return readBuf.readIntLE();
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
        return readBuf.readLongLE();
    }

    /**
     * Reads a 4-byte float (little-endian) from the current position.
     *
     * @return the float read
     */
    public float readFloat() {
        return readBuf.readFloatLE();
    }

    /**
     * Reads an 8-byte double (little-endian) from the current position.
     *
     * @return the double read
     */
    public double readDouble() {
        return readBuf.readDoubleLE();
    }

    /**
     * Reads a fixed-length ASCII string from the current position.
     *
     * @param len the number of bytes to read
     * @return the resulting string
     */
    public String readAsciiString(int len) {
        ByteBuf tempBuf = readBuf.readSlice(len);
        return tempBuf.toString(ASCII);
    }

    /**
     * Reads a maple-convention ASCII string from the current position.
     * The string is prefixed with a 2-byte short indicating its length.
     *
     * @return the resulting string
     */
    public final String readMapleAsciiString() {
        short len = readShort();
        return readAsciiString(len);
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
        readBuf.skipBytes(num);
    }

    /**
     * Seeks to an absolute offset in the stream.
     *
     * @param offset the target offset to seek to
     */
    public void seek(long offset) {
        readBuf.readerIndex((int) offset);
    }

    /**
     * Gets the current reader index (position).
     *
     * @return the current offset in the stream
     */
    public long getPosition() {
        return readBuf.readerIndex();
    }

    /**
     * Checks if there are any bytes remaining to read.
     *
     * @return true if readable bytes remain, false otherwise
     */
    public boolean remaining() {
        return readBuf.isReadable();
    }

    /**
     * Resets the buffer by clearing it (reader and writer index to 0).
     */
    public void reset() {
        if (readBuf != null) {
            readBuf.clear();
        }
    }

    /**
     * Releases the underlying {@link ByteBuf} and performs cleanup.
     */
    @Override
    public synchronized void close() {
        if (readBuf != null) {
            try {
                if (readBuf.refCnt() > 0) {
                    readBuf.release();
                }
            } finally {
                readBuf = null;
            }
        }
    }

}