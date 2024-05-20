package cdn.simple.img.input;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccessByteStream {
	private final RandomAccessFile raf;
	private long read = 0;

	public RandomAccessByteStream(RandomAccessFile raf) {
		this.raf = raf;
	}

	public int readByte() {
		int temp;
		try {
			temp = raf.read();
			if (temp == -1)
				throw new RuntimeException("EOF");
			read++;
			return temp;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void seek(long offset) throws IOException {
		raf.seek(offset);
	}

	public long getPosition() throws IOException {
		return raf.getFilePointer();
	}

}
