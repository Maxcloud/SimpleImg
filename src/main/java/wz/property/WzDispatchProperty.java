package wz.property;

import img.crypto.WzStringRegistry;
import img.io.ImgInputStream;
import img.io.ImgWritableOutputStream;
import io.netty.buffer.ByteBuf;

import java.util.LinkedHashMap;
import java.util.Map;

public class WzDispatchProperty implements WzProperty {

    private static final byte VT_DISPATCH = 9;
    private String name = "Property";
    private final Map<String, WzProperty> lWzProperty = new LinkedHashMap<>();

    public WzDispatchProperty() { }

    public Map<String, WzProperty> getProperties() {
        return lWzProperty;
    }

    /**
     * Whether this dispatch carries no usable content. A "Property"
     * dispatch always stores its inner list, so emptiness must be
     * decided on that list, not on this wrapper's map.
     */
    public boolean isEmpty() {
        if (lWzProperty.isEmpty()) {
            return true;
        }

        WzProperty property = lWzProperty.values().iterator().next();
        return property instanceof WzPropertyList list && list.getProperties().isEmpty();
    }

    @Override
    public void read(WzStringRegistry codec, ImgInputStream stream) {
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
    public void write(WzStringRegistry codec, String key, ImgWritableOutputStream output) {
        ByteBuf out = output.getByteBuf();

        codec.serialize(output, key, (byte) 0x00, (byte) 0x01);
        out.writeByte(VT_DISPATCH);
        writeDispatch(codec, output);
    }

    /**
     * Writes a dispatch block to the provided ByteBuf and ImgWritableOutputStream.
     *
     * @param stream The ImgWritableOutputStream to write to.
     */
    private void writeDispatch(WzStringRegistry codec, ImgWritableOutputStream stream) {
        ByteBuf out = stream.getByteBuf();

        int lengthPosition = stream.getByteBuf().writerIndex();
        out.writeIntLE(0); // we're using a placeholder here, for total length.

        int startPosition = out.writerIndex();
        codec.serialize(stream, getName(), (byte) 0x73, (byte) 0x1B);
        for (Map.Entry<String, WzProperty> entry : lWzProperty.entrySet()) {
            String key = entry.getKey();

            WzProperty property = entry.getValue();
            property.write(codec, key, stream);
        }

        // end of dispatch block
        int endPosition = out.writerIndex();
        int totalLength = endPosition - startPosition;

        // log.warn("Writing dispatch: lengthPos={}, startPosition={}, endPosition={}, totalLength={}",
        //         lengthPosition, startPosition, endPosition, totalLength);

        out.setIntLE(lengthPosition, totalLength);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
