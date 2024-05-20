package cdn.simple.img.input;

import java.io.*;

public class SeekableInputStream {
	private RandomAccessByteStream bs;

	public SeekableInputStream(File file) {
		try {
			this.bs = new RandomAccessByteStream(new RandomAccessFile(file, "r"));
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			e.printStackTrace();
		}
	}

	public byte readByte() {
		return (byte) bs.readByte();
	}

	public int readInt() {
		int byte1, byte2, byte3, byte4;

		byte1 = bs.readByte();
		byte2 = bs.readByte();
		byte3 = bs.readByte();
		byte4 = bs.readByte();
		return (byte4 << 24) + (byte3 << 16) + (byte2 << 8) + byte1;
	}

	public short readShort() {
		int byte1, byte2;
		byte1 = bs.readByte();
		byte2 = bs.readByte();
		return (short) ((byte2 << 8) + byte1);
	}

	public char readChar() {
		return (char) readShort();
	}

	public long readLong() {
		long byte1 = bs.readByte();
		long byte2 = bs.readByte();
		long byte3 = bs.readByte();
		long byte4 = bs.readByte();
		long byte5 = bs.readByte();
		long byte6 = bs.readByte();
		long byte7 = bs.readByte();
		long byte8 = bs.readByte();

		return (byte8 << 56) + (byte7 << 48) + (byte6 << 40) + (byte5 << 32) +
			   (byte4 << 24) + (byte3 << 16) + (byte2 << 8) + byte1;
	}

	public float readFloat() {
		return Float.intBitsToFloat(readInt());
	}

	public double readDouble() {
		return Double.longBitsToDouble(readLong());
	}

	public final String readAsciiString(int n) {
		char ret[] = new char[n];
		for (int x = 0; x < n; x++) {
			ret[x] = (char) readByte();
		}
		return String.valueOf(ret);
	}

	public final String readNullTerminatedAsciiString() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte b = 1;
		while (b != 0) {
			b = readByte();
			baos.write(b);
		}
		byte[] buf = baos.toByteArray();
		char[] chrBuf = new char[buf.length];
		for (int x = 0; x < buf.length; x++) {
			chrBuf[x] = (char) buf[x];
		}
		return String.valueOf(chrBuf);
	}

	public void skip(int num) {
		for (int x = 0; x < num; x++) {
			readByte();
		}
	}

	public void seek(long offset) {
		try {
			bs.seek(offset);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public long getPosition() {
		try {
			return bs.getPosition();
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

}
