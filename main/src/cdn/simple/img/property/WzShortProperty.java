package cdn.simple.img.property;

import cdn.simple.img.input.ImgSeekableInputStream;

public class WzShortProperty implements IWzProperty {

	private Object data;

	public WzShortProperty(ImgSeekableInputStream stream) {
		this.data = stream.readShort();
	}

	@Override
	public WzPropertyType getType() {
		return WzPropertyType.SHORT;
	}

	@Override
	public Object getData() {
		return data;
	}

}
