package cdn.simple.img.property;

import cdn.simple.img.ImgEntry;
import cdn.simple.img.input.ImgSeekableInputStream;

public class WzProperty implements IWzProperty {

	public WzProperty() {}

	public WzProperty(ImgEntry root, ImgSeekableInputStream stream, long endOfBytes) {
		readImage(root, stream, endOfBytes);
	}

	public void readImage(ImgEntry entry, ImgSeekableInputStream stream, long endOfBytes) {
		try {
			byte b = stream.readByte();
			String type = stream.readDecodedStringBlock(b);

			if (type.equals("Property")) {
				stream.skip(1);
				stream.skip(1);
				int children = stream.readCompressedInt();
				for (int i = 0; i < children; i++) {
					ImgEntry image = new ImgEntry(entry);
					parse(image, stream);
					image.finish();
					entry.addChild(image);
				}
			} else if (type.equals("Canvas")) {
				stream.seek(endOfBytes);
			} else if (type.equals("Shape2D#Convex2D")) {
				stream.seek(endOfBytes);
			} else if (type.equals("Shape2D#Vector2D")) {
				stream.seek(endOfBytes);
			} else if (type.equals("Sound_DX8")) {
				stream.seek(endOfBytes);
			} else if (type.equals("UOL")) {
				stream.seek(endOfBytes);
			} else {
				System.out.println("Unhandled WzExtended Type: "+type);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public void parse(ImgEntry entry, ImgSeekableInputStream stream) {
		byte type = stream.readByte();
		String name = stream.readDecodedStringBlock(type);

		IWzProperty property = null;
		byte marker = stream.readByte();
		switch (marker) {
			case 2:
			case 11:
				property = new WzShortProperty(stream);
				break;
			case 3:
				property = new WzIntProperty(stream);
				break;
			case 4:
				property = new WzFloatProperty(stream);
				break;
			case 5:
				property = new WzDoubleProperty(stream);
				break;
			case 8:
				property = new WzStringProperty(stream);
				break;
			case 9:
				long endOfBytes = stream.readInt() + stream.getPosition();
				property = new WzProperty(entry, stream, endOfBytes);
				break;
			default:
				System.out.printf("Missing property found. (%s)%n" + marker);
				break;
		}

		entry.setName(name);
		entry.setType(property.getType());
		entry.setData(property.getData());
	}

	@Override
	public WzPropertyType getType() {
		return WzPropertyType.PROPERTY;
	}

	@Override
	public Object getData() {
		return 0;
	}
}
