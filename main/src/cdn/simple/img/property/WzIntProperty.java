package cdn.simple.img.property;

import cdn.simple.img.input.ImgSeekableInputStream;

public class WzIntProperty implements IWzProperty {

	private final Object data;

	public WzIntProperty(ImgSeekableInputStream stream) {
		this.data = stream.readCompressedInt();
	}

	@Override
	public WzPropertyType getType() {
		return WzPropertyType.INT;
	}

	@Override
	public Object getData() {
		return data;
	}

}
