package img.property;

import img.Variant;
import img.io.ImgSeekableInputStream;
import img.io.ImgWritableOutputStream;
import img.util.StringWriter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@Slf4j
public class WzPropertyList implements WzProperty {

    private String name = "Property";
    private final Map<String, WzProperty> lWzProperty = new LinkedHashMap<>();

    @Override
    public void read(ImgSeekableInputStream stream) {
        stream.readByte();
        stream.readByte();
        int children = stream.decodeInt();
        for (int i = 0; i < children; i++) {
            parse(stream);
        }
    }

    @Override
    public void parse(ImgSeekableInputStream stream) {
        String property_name = stream.getStringWriter().internalDeserializeString(stream);
        byte variant = stream.readByte();

        WzProperty property = getWzProperty(variant);
        if (property != null) {
            property.read(stream);

            if (property instanceof WzDispatchProperty prop) {
                if (prop.getName().equals("Canvas") ||
                    prop.getName().equals("Shape2D#Convex2D") ||
                    prop.getName().equals("Sound_DX8")) {
                    return;
                }
            }

            lWzProperty.put(property_name, property);
        }
    }

    private WzProperty getWzProperty(byte variant) {
        Variant variant_name = Variant.fromByte(variant);
        return switch (variant_name) {
            case VT_EMPTY           -> new WzNullProperty();
            case VT_I2, VT_BOOL     -> new WzShortProperty();
            case VT_I4              -> new WzIntProperty();
            case VT_I8              -> new WzLongProperty();
            case VT_R4              -> new WzFloatProperty();
            case VT_R8              -> new WzDoubleProperty();
            case VT_BSTR            -> new WzStringProperty();
            case VT_DISPATCH        -> new WzDispatchProperty();
            default                 -> null;
        };
    }

    @Override
    public void write(StringWriter stringWriterPool, String key, ImgWritableOutputStream output) {
        output.writeByte(0);
        output.writeByte(0);
        if (lWzProperty.isEmpty()) {
            output.writeCompressedInt(0);
        } else {
            output.writeCompressedInt(lWzProperty.size());
            for (Map.Entry<String, WzProperty> entry : lWzProperty.entrySet()) {
                String key0 = entry.getKey();
                WzProperty value = entry.getValue();

                // System.out.println("Writing property name: " + key0);
                value.write(stringWriterPool, key0, output);
            }

        }
    }

}
