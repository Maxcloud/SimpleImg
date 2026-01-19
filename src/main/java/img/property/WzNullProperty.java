package img.property;

import img.io.impl.ImgReadableInputStream;
import img.io.impl.ImgWritableOutputStream;
import img.util.StringWriter;

public class WzNullProperty implements WzProperty {

    WzNullProperty() { }

    @Override
    public void read(ImgReadableInputStream stream) { }

    @Override
    public void write(StringWriter stringWriterPool, String key,
                      ImgWritableOutputStream stream) {

        stringWriterPool.internalSerializeString(stream, key, (byte) 0x00, (byte) 0x01);
        stream.writeByte(0); // VT_EMPTY
    }
}
