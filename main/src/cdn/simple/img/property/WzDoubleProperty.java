package cdn.simple.img.property;

import cdn.simple.img.input.ImgSeekableInputStream;

public class WzDoubleProperty implements IWzProperty {

	private final Object data;

	public WzDoubleProperty(ImgSeekableInputStream stream) {
		this.data = stream.readDouble();
	}

	@Override
	public WzPropertyType getType() {
		return WzPropertyType.DOUBLE;
	}

	@Override
	public Object getData() {
		return data;
	}

}
