package img.property;

import img.crypto.WzStringCodec;
import img.util.Variant;
import img.io.impl.ImgInputStream;
import img.io.impl.ImgWritableOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class WzPropertyList implements WzProperty {

    Logger log = LoggerFactory.getLogger(WzPropertyList.class);

    private final String name = "Property";
    private final Map<String, WzProperty> lWzProperty = new LinkedHashMap<>();

    @Override
    public void read(WzStringCodec codec, ImgInputStream stream) {
        stream.readByte();
        stream.readByte();
        int children = stream.decodeInt();
        for (int i = 0; i < children; i++) {
            parse(codec, stream);
        }
    }

    public String getName() {
        return name;
    }

    public Map<String, WzProperty> getProperties() {
        return lWzProperty;
    }

    public void parse(WzStringCodec codec, ImgInputStream stream) {
        String property_name = codec.deserialize(stream);
        byte variant = stream.readByte();

        WzProperty property = getWzProperty(variant);
        if (property == null) {
            return;
        }

        property.read(codec, stream);

        if (property instanceof WzDispatchProperty prop) {
            if (prop.getName().equals("Canvas") ||
                prop.getName().equals("Shape2D#Convex2D") ||
                prop.getName().equals("Sound_DX8")) {
                return;
            }

            if (prop.getProperties().isEmpty()) return;
        }

        lWzProperty.put(property_name, property);
    }

    private WzProperty getWzProperty(byte variant) {
        Variant variant_name = null;

        try {
            variant_name = Variant.fromByte(variant);
        } catch (Exception e) {
            log.error("Skipping file, property variant not found: {}", variant);
        }

        return switch (variant_name) {
            case VT_EMPTY           -> new WzNullProperty();
            case VT_I2, VT_BOOL     -> new WzShortProperty();
            case VT_I4, VT_UI4      -> new WzIntProperty();
            case VT_I8              -> new WzLongProperty();
            case VT_R4              -> new WzFloatProperty();
            case VT_R8              -> new WzDoubleProperty();
            case VT_BSTR            -> new WzStringProperty();
            case VT_DISPATCH        -> new WzDispatchProperty();
            default                 -> null;
        };
    }

    @Override
    public void write(WzStringCodec codec, String key, ImgWritableOutputStream output) {
        output.writeByte(0);
        output.writeByte(0);

        boolean isEmpty = lWzProperty.isEmpty();
        output.writeCompressedInt(isEmpty ? 0 : lWzProperty.size());
        if (isEmpty) return;

        for (Map.Entry<String, WzProperty> entry : lWzProperty.entrySet()) {
            String key0 = entry.getKey();

            WzProperty property = entry.getValue();
            property.write(codec, key0, output);
        }
    }
}
