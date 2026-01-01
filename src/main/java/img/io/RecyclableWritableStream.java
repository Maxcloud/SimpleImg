package img.io;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class RecyclableWritableStream implements AutoCloseable {

    private ByteBuf byteBuf;

    public RecyclableWritableStream(ByteBuf byteBuf) {
        try {
            this.byteBuf = byteBuf;
        } catch (Exception e) {
            log.error("An error has occurred while loading the file to memory. ", e);
        }
    }

    public void writeByte(int value) {
        byteBuf.writeByte(value);
    }

    public void writeShort(int value) {
        byteBuf.writeShortLE(value);
    }

    public void writeInt(int value) {
        byteBuf.writeIntLE(value);
    }

    public void writeChar(char value) {
        byteBuf.writeChar(value);
    }

    public void writeLong(long value) {
        byteBuf.writeLongLE(value);
    }

    public void writeFloat(float value) {
        byteBuf.writeFloatLE(value);
    }

    public void writeDouble(double value) {
        byteBuf.writeDoubleLE(value);
    }

    public void reset() {
        if (byteBuf != null) {
            byteBuf.clear();
        }
    }

    public void seek(int offset) {
        byteBuf.readerIndex(offset);
    }

    public int getPosition() {
        return byteBuf.writerIndex();
    }

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
