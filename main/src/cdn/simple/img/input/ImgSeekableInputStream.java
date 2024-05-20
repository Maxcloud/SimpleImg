package cdn.simple.img.input;

import cdn.simple.img.WzEncryption;

import java.io.File;

/**
 * @version 1.0
 * @since 8/6/2022
 */
public class ImgSeekableInputStream extends SeekableInputStream {

	byte[] encryptionKey = WzEncryption.getInstance().getKey();

	public ImgSeekableInputStream(File file)  {
		super(file);
	}

	public String readDecodedStringAtOffsetAndReset(int offset) {
		long pos = 0;
		pos = getPosition();
		seek(offset);
		String ret = readDecodedString();
		seek(pos);
		return ret;
	}

	public String readDecodedString() {
		int strLength;
		byte b = readByte();

		if (b == 0x00) return "";
		if (b >= 0) {

			// do unicode
			short umask = (short) 0xAAAA;

			strLength = (b == 0x7F ? readInt() : b);
			if (strLength < 0)
				return "";

			char str[] = new char[strLength];
			for (int i = 0; i < strLength; i++) {
				char chr = readChar();
				chr ^= umask++;
				str[i] = chr;
			}

			return String.valueOf(str);
		} else {

			// do non-unicode
			byte mask = (byte) 0xAA;

			strLength = (b == -128 ? readInt() : -b);
			assert strLength >= 0 : "can never be < 0 (?)";

			char str[] = new char[strLength];
			for (int i = 0; i < strLength; i++) {
				byte b2 = readByte();
				b2 ^= mask++;
				b2 ^= encryptionKey[i];
				str[i] = (char) (b2 & 0xFF);
			}

			return String.valueOf(str);
		}
	}

	public String readDecodedStringBlock(byte type) {
		switch(type) {
			case 0:
			case 3:
			case 4:
			case 0x73: // image head without offset
				return readDecodedString();
			case 1:
			case 0x1B: // image head with offset
				return readDecodedStringAtOffsetAndReset(readInt());
			case 2:
				return readDecodedStringAtOffsetAndReset(readInt() + 1);
			default:
				System.out.println(String.format("Missing Type(%s)", type));
				return "";
		}
	}

	public int readCompressedInt() {
		byte b = readByte();
		return (b == -128 ? readInt() : b);
	}

	public float readCompressedFloat() {
		byte b = readByte();
		return (b == -128 ? readFloat() : b);
	}

}
