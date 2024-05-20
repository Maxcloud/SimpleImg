package cdn.simple.img;

import cdn.simple.img.input.ImgSeekableInputStream;
import cdn.simple.img.property.WzProperty;

import java.io.File;

// https://www.techiedelight.com/list-all-subdirectories-present-directory-java/
public class ImgFile extends WzProperty {

	private final File file;

	public ImgFile(String name) {
		String path = System.getProperty("wz.path");
		this.file = new File(path +"/"+ name);
	}

	public File getFile() {
		return file;
	}

	public synchronized MapleData getImageData(String path) {
		File file = new File(this.file, path);
		if (file.exists()) {
			ImgSeekableInputStream stream = new ImgSeekableInputStream(file);

			ImgEntry imgEntry = new ImgEntry(null);
			readImage(imgEntry, stream, 0);

			return imgEntry;
		} else {
			throw new UnsupportedOperationException("Image file not found in that path.");
		}
	}

}
