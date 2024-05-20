package cdn.simple.img.property;

import cdn.simple.img.input.ImgSeekableInputStream;

public class WzFloatProperty implements IWzProperty {

	private final Object data;

	public WzFloatProperty(ImgSeekableInputStream stream) {
		this.data = stream.readCompressedFloat();
	}

	@Override
	public WzPropertyType getType() {
		return WzPropertyType.FLOAT;
	}

	@Override
	public Object getData() {
		return data;
	}

}
