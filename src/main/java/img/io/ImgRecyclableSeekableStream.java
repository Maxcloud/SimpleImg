package img.io;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * A recyclable seekable stream built on top of Netty's {@link ByteBuf}.
 * Designed for efficient in-memory binary file reading with support for random access (seeking).
 */
public class ImgRecyclableSeekableStream implements AutoCloseable {
    private static final Charset ASCII = StandardCharsets.US_ASCII;

    private final Path path;
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
        this.path = Objects.requireNonNull(path, "Path cannot be null");
        try {
            readBuf = Unpooled.wrappedBuffer(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new UncheckedIOException("An error has occurred while loading the file to memory. " + path, e);
        }
    }

    public ImgRecyclableSeekableStream(Path path, byte[] data) {
        this.path = Objects.requireNonNull(path, "Path cannot be null");
        readBuf = Unpooled.wrappedBuffer(data);
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
        if (length < 0) throw new IllegalArgumentException("negative length: " + length);
        byte[] bytes = new byte[length];
        readBuf.readBytes(bytes);
        return bytes;
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
        int len = readShort() & 0xFFFF;
        return readAsciiString(len);
    }

    /**
     * Reads an ASCII string from the current position until a null terminator (0x00) is encountered.
     *
     * @return the resulting string (excluding the null terminator)
     */
    public String readNullTerminatedAsciiString() {
        StringBuilder sb = new StringBuilder();
        while (readBuf.isReadable()) {
            int b = readByte() & 0xFF;
            if (b == 0) break;
            sb.append((char) b);
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
        if (offset < 0 || offset > readBuf.capacity()) {
            throw new IndexOutOfBoundsException("offset " + offset + " out of [0," + readBuf.capacity() + "]");
        }
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
     * Resets the buffer by setting the reader index to 0.
     */
    public void reset() {
        if (readBuf != null) {
            readBuf.readerIndex(0);
        }
    }

    /**
     * Releases the underlying {@link ByteBuf} and performs cleanup.
     */
    @Override
    public void close() {
        if (readBuf != null && readBuf.refCnt() > 0) {
            readBuf.release();
        }
        readBuf = null;
    }
}