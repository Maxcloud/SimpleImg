package img.property;

import img.io.ImgSeekableInputStream;
import img.io.ImgWritableOutputStream;
import img.util.StringWriter;

import java.util.LinkedHashMap;
import java.util.Map;

public class WzDispatchProperty implements WzProperty {

    private String name = "Property";
    private final Map<String, WzProperty> lWzProperty = new LinkedHashMap<>();

    public WzDispatchProperty() { }

    @Override
    public void read(ImgSeekableInputStream stream) {
        long endOfBytes = stream.readInt() + stream.getPosition();

        String name = stream.getStringWriter().internalDeserializeString(stream);
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
            property.read(stream);
            lWzProperty.put(name, property);
        }
    }

    @Override
    public void write(StringWriter stringWriterPool, String key, ImgWritableOutputStream stream) {
        stringWriterPool.internalSerializeString(stream, key, (byte) 0x00, (byte) 0x01);

        byte VT_DISPATCH = 9;
        stream.writeByte(VT_DISPATCH);
        writeDispatch(stringWriterPool, stream);
    }

    /**
     * Writes a dispatch block to the provided ByteBuf and ImgWritableOutputStream.
     *
     * @param stream The ImgWritableOutputStream to write to.
     */
    private void writeDispatch(StringWriter stringWriterPool, ImgWritableOutputStream stream) {
        int lengthPosition = stream.getPosition();
        stream.writeInt(0); // we're using a placeholder here, for total length.

        int startPosition = stream.getPosition();
        if (lWzProperty.isEmpty()) {

        } else {
            stringWriterPool.internalSerializeString(stream, getName(), (byte) 0x73, (byte) 0x1B);
            for (Map.Entry<String, WzProperty> entry : lWzProperty.entrySet()) {
                String key = entry.getKey();
                WzProperty value = entry.getValue();

                value.write(stringWriterPool, key, stream);
            }
        }

        // end of dispatch block
        int endPosition = stream.getPosition();
        int totalLength = endPosition - startPosition;

        // log.warn("Writing dispatch: lengthPos={}, startPosition={}, endPosition={}, totalLength={}",
        //         lengthPosition, startPosition, endPosition, totalLength);

        stream.getByteBuf().setIntLE(lengthPosition, totalLength);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
