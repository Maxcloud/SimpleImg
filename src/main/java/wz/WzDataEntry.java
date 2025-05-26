package wz;

import img.io.WzSeekableInputStream;
import lombok.Getter;

@Getter
public class WzDataEntry implements MapleDataEntity {

	private final String name;
	private final int size;
	private final int checksum;
	private final int offset;
	private final MapleDataEntity parent;

	public WzDataEntry(byte type, WzSeekableInputStream stream, MapleDataEntity parent) {
		this.name = stream.decodeStringBlock(type);
		this.size = stream.decodeInt();
		this.checksum = stream.decodeInt();
		this.offset = stream.readOffset();
		this.parent = parent;
	}

	public WzDataEntry(String name) {
		this.name = name;
		this.size = 0;
		this.checksum = 0;
		this.offset = 0;
		this.parent = null;
	}

}
