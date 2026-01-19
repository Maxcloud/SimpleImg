package img.property;

import img.io.impl.ImgReadableInputStream;
import img.io.impl.ImgWritableOutputStream;
import img.util.StringWriter;

public class WzDoubleProperty implements WzProperty {

    private final byte VT_R8 = 5;
    private double data;

    WzDoubleProperty() { }

    @Override
    public void read(ImgReadableInputStream stream) {
        this.data = stream.readDouble();
    }

    @Override
    public void write(StringWriter stringWriterPool, String key, ImgWritableOutputStream stream) {

        stringWriterPool.internalSerializeString(stream, key, (byte) 0x00, (byte) 0x01);
        stream.writeByte(VT_R8);
        stream.writeDouble(data);
    }

}
