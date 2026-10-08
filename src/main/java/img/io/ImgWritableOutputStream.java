package img.io;

import io.netty.buffer.ByteBuf;

/**
 * Custom encryption to write img files for MapleStory.
 */
public class ImgWritableOutputStream implements AutoCloseable {

    private ByteBuf out;
    protected final byte[] secret;

    public ImgWritableOutputStream(ByteBuf out, byte[] secret) {
        this.out = out;
        this.secret = secret;
    }

    public ByteBuf getByteBuf() {
        return out;
    }

    public void writeCompressedInt(int value) {
        if (value >= Byte.MAX_VALUE || value <= Byte.MIN_VALUE) {
            out.writeByte(Byte.MIN_VALUE);
            out.writeIntLE(value);
        } else {
            out.writeByte(value);
        }
    }

    public void writeCompressedLong(long value) {
        if (value >= Byte.MAX_VALUE || value <= Byte.MIN_VALUE) {
            out.writeByte(Byte.MIN_VALUE);
            out.writeLongLE(value);
        } else {
            out.writeByte((int) value);
        }
    }

    public void writeCompressedFloat(float value) {
        if (value >= Byte.MAX_VALUE || value <= Byte.MIN_VALUE) {
            out.writeByte(Byte.MIN_VALUE);
            out.writeFloatLE(value);
        } else {
            out.writeByte((byte) value);
        }
    }

    public void writeString(String str) {
        if (str == null || str.isEmpty()) {
            out.writeByte(0x00); // empty string
            return;
        }

        boolean isUnicode = isUnicodeString(str);

        int len = str.length();

        if (isUnicode) {
            if (len >= 127) {
                out.writeByte(0x7F); // signals 4-byte length
                out.writeIntLE(len);
            } else {
                out.writeByte(len); // 1-byte length
            }
            writeUnicodeString(str);
        } else {
            if (len > 127) {
                out.writeByte((byte) 0x80); // -128 signed, means 4-byte length
                out.writeIntLE(len);
            } else {
                out.writeByte((byte) -len); // signed byte, negative
            }
            writeNonUnicodeString(str);
        }
    }

    private void writeUnicodeString(String str) {
        char mask = (char) 0xAAAA;
        int len = str.length();

        for (int i = 0; i < len; i++) {
            char c = (char) (str.charAt(i) ^ mask++);
            out.writeShortLE(c);
        }
    }

    private void writeNonUnicodeString(String str) {
        byte mask = (byte) 0xAA;
        int len = str.length();

        for (int i = 0; i < len; i++, mask++) {
            char cipherByte = (str.charAt(i));
            byte keyByte = (byte) (secret[i % secret.length] ^ mask);
            byte b = (byte) (cipherByte ^ keyByte);
            out.writeByte(b);
        }
    }

    private boolean isUnicodeString(String str) {
        for (char c : str.toCharArray()) {
            if (c > 0x7F) return true;
        }
        return false;
    }

    @Override
    public synchronized void close() {
        if (out != null) {
            try {
                if (out.refCnt() > 0) {
                    out.release();
                }
            } finally {
                out = null;
            }
        }
    }
}
