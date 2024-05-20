package cdn.simple.img.property;

import cdn.simple.img.input.ImgSeekableInputStream;

public class WzStringProperty implements IWzProperty {
	private Object data;

	public WzStringProperty(ImgSeekableInputStream stream) {
		parse(stream);
	}

	private void parse(ImgSeekableInputStream stream) {
		byte type = stream.readByte();
		if (type == 0) {
			this.data = stream.readDecodedString();
		} else if (type == 1) {
			this.data = stream.readDecodedStringAtOffsetAndReset(stream.readInt());
		}
	}

	@Override
	public WzPropertyType getType() {
		return WzPropertyType.STRING;
	}

	@Override
	public Object getData() {
		return data;
	}

}
