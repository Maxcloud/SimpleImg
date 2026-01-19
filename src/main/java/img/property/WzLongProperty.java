package img.property;

import img.io.impl.ImgReadableInputStream;
import img.io.impl.ImgWritableOutputStream;
import img.util.StringWriter;

public class WzLongProperty implements WzProperty {

    WzLongProperty() { }

    private long data;

    @Override
    public void read(ImgReadableInputStream stream) {
        this.data = stream.decodeLong();
    }

    @Override
    public void write(StringWriter stringWriterPool, String key,
                      ImgWritableOutputStream stream) {

        stringWriterPool.internalSerializeString(stream, key, (byte) 0x00, (byte) 0x01);

        byte VT_I8 = 20;
        stream.writeByte(VT_I8);
        stream.writeCompressedLong(data);
    }
}
