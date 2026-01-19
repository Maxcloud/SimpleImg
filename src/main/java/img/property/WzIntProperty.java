package img.property;

import img.io.impl.ImgReadableInputStream;
import img.io.impl.ImgWritableOutputStream;
import img.util.StringWriter;

public class WzIntProperty implements WzProperty {

    private final byte VT_I4 = 3;
    private int data;

    WzIntProperty() { }

    @Override
    public void read(ImgReadableInputStream stream) {
        this.data = stream.decodeInt();
    }

    @Override
    public void write(StringWriter stringWriterPool, String key, ImgWritableOutputStream stream) {

        stringWriterPool.internalSerializeString(stream, key, (byte) 0x00, (byte) 0x01);
        stream.writeByte(VT_I4);
        stream.writeCompressedInt(data);
    }
}
