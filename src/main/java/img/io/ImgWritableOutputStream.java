package img.io;

import img.cryptography.WzCryptography;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

@Getter
public class ImgWritableOutputStream extends RecyclableWritableStream {
    private final WzCryptography imgFileEncryption = WzCryptography.getInstance();

    public ImgWritableOutputStream(ByteBuf byteBuf) {
        super(byteBuf);
    }

    public void writeCompressedInt(int value) {
        if (value >= Byte.MAX_VALUE || value <= Byte.MIN_VALUE) {
            writeByte(Byte.MIN_VALUE);
            writeInt(value);
        } else {
            writeByte(value);
        }
    }

    public void writeCompressedLong(long value) {
        if (value >= Byte.MAX_VALUE || value <= Byte.MIN_VALUE) {
            writeByte(Byte.MIN_VALUE);
            writeLong(value);
        } else {
            writeByte((int) value);
        }
    }

    public void writeCompressedFloat(float value) {
        if (value >= Byte.MAX_VALUE || value <= Byte.MIN_VALUE) {
            writeByte(Byte.MIN_VALUE);
            writeFloat(value);
        } else {
            writeByte((byte) value);
        }
    }

    public void writeString(String str) {
        if (str == null || str.isEmpty()) {
            writeByte(0x00); // empty string
            return;
        }

        boolean isUnicode = isUnicodeString(str);

        int len = str.length();

        if (isUnicode) {
            if (len >= 127) {
                writeByte(0x7F); // signals 4-byte length
                writeInt(len);
            } else {
                writeByte(len); // 1-byte length
            }
            writeUnicodeString(str);
        } else {
            if (len > 127) {
                writeByte((byte) 0x80); // -128 signed, means 4-byte length
                writeInt(len);
            } else {
                writeByte((byte) -len); // signed byte, negative
            }
            writeNonUnicodeString(str);
        }
    }


    private void writeUnicodeString(String str) {
        char[] mask = imgFileEncryption.getXorCharArray();
        for (int i = 0; i < str.length(); i++) {
            char c = (char) (str.charAt(i) ^ mask[i]);
            writeChar(c);
        }
    }

    private void writeNonUnicodeString(String str) {
        byte[] xorKey = imgFileEncryption.getXorKey(str.length());
        for (int i = 0; i < str.length(); i++) {
            byte b = (byte) (str.charAt(i) ^ xorKey[i]);
            writeByte(b);
        }
    }

    private boolean isUnicodeString(String str) {
        for (char c : str.toCharArray()) {
            if (c > 0x7F) return true;
        }
        return false;
    }

}
