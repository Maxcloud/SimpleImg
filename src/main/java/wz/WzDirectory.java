package wz;

import img.io.WzSeekableInputStream;

import java.util.*;

/**
 * @version 1.0
 * @since 8/6/2022
 */
public class WzDirectory extends WzDataEntry {

	/* A directory has subdirectories */
	private final List<WzDirectory> subdirectories = new ArrayList<>();

	/* A directory has img files */
	private final List<WzDataEntry> files = new ArrayList<>();

	/* A directory has multiple entries (directories, images) */
	private final Map<String, WzDataEntry> entries = new HashMap<>();

	public WzDirectory(String name) {
		super(name);
	}

	public WzDirectory(byte type, WzSeekableInputStream stream, MapleDataEntity parent) {
		super(type, stream, parent);
	}

	public void addDirectory(WzDirectory dir) {
		subdirectories.add(dir);
		entries.put(dir.getName(), dir);
	}

	public void addFile(WzDataEntry fileEntry) {
		files.add(fileEntry);
		entries.put(fileEntry.getName(), fileEntry);
	}

	public List<WzDirectory> getSubdirectories() {
		return Collections.unmodifiableList(subdirectories);
	}
}
