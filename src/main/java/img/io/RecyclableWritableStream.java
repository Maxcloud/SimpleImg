package img.io;

import io.netty.buffer.ByteBuf;

public class RecyclableWritableStream implements AutoCloseable {

    private ByteBuf byteBuf;
    protected byte[] secret;

    public RecyclableWritableStream(ByteBuf byteBuf, byte[] secret) {
        try {
            this.byteBuf = byteBuf;
            this.secret = secret;
        } catch (Exception e) {
            // log.error("An error has occurred while loading the file to memory. ", e);
        }
    }

    public ByteBuf getByteBuf() {
        return byteBuf;
    }

    public byte[] getSecret() {
        return secret;
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
