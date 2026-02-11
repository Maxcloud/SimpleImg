package img.property;

import img.crypto.WzStringCodec;
import img.io.impl.ImgInputStream;
import img.io.impl.ImgWritableOutputStream;
import img.util.StringWriter;

public class WzDoubleProperty implements WzProperty {

    private final byte VT_R8 = 5;
    private double data;

    WzDoubleProperty() { }

    @Override
    public void read(WzStringCodec codec, ImgInputStream stream) {
        this.data = stream.readDouble();
    }

    @Override
    public void write(WzStringCodec codec, String key, ImgWritableOutputStream stream) {

        codec.serialize(stream, key, (byte) 0x00, (byte) 0x01);
        stream.writeByte(VT_R8);
        stream.writeDouble(data);
    }

}
