package img.property;

import img.crypto.WzStringCodec;
import img.io.impl.ImgInputStream;
import img.io.impl.ImgWritableOutputStream;
import io.netty.buffer.ByteBuf;

import java.util.LinkedHashMap;
import java.util.Map;

public class WzDispatchProperty implements WzProperty {

    private String name = "Property";
    private final Map<String, WzProperty> lWzProperty = new LinkedHashMap<>();

    public WzDispatchProperty() { }

    public Map<String, WzProperty> getProperties() {
        return lWzProperty;
    }

    @Override
    public void read(WzStringCodec codec, ImgInputStream stream) {
        long endOfBytes = stream.readInt() + stream.getPosition();

        String name = codec.deserialize(stream);
        setName(name);

        WzProperty property = null;
        switch (name) {
            case "Canvas":
            case "Shape2D#Convex2D":
            case "Sound_DX8":
                stream.seek(endOfBytes);
                break;
            case "Property":
                property = new WzPropertyList();
                break;
            case "Shape2D#Vector2D":
                property = new WzVectorProperty();
                break;
            case "UOL":
                property = new WzUOLProperty();
                break;
            default:
                // log.warn("There was a missing property found.");
                break;
        }
        if (property != null) {
            property.read(codec, stream);
            lWzProperty.put(name, property);
        }
    }

    @Override
    public void write(WzStringCodec codec, String key, ImgWritableOutputStream output) {

    }

    /**
     * Writes a dispatch block to the provided ByteBuf and ImgWritableOutputStream.
     *
     * @param stream The ImgWritableOutputStream to write to.
     */
    private void writeDispatch(WzStringCodec codec, ImgWritableOutputStream stream) {
        int lengthPosition = stream.getPosition();
        stream.writeInt(0); // we're using a placeholder here, for total length.

        int startPosition = stream.getPosition();
        if (lWzProperty.isEmpty()) {

        } else {
            codec.serialize(stream, getName(), (byte) 0x73, (byte) 0x1B);
            for (Map.Entry<String, WzProperty> entry : lWzProperty.entrySet()) {
                String key = entry.getKey();

                WzProperty property = entry.getValue();
                property.write(codec, key, stream);
            }
        }

        // end of dispatch block
        int endPosition = stream.getPosition();
        int totalLength = endPosition - startPosition;

        // log.warn("Writing dispatch: lengthPos={}, startPosition={}, endPosition={}, totalLength={}",
        //         lengthPosition, startPosition, endPosition, totalLength);

        ByteBuf byteBuf = stream.getByteBuf();
        byteBuf.setIntLE(lengthPosition, totalLength);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
