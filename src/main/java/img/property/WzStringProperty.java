package img.property;

import img.io.ImgSeekableInputStream;
import img.io.ImgWritableOutputStream;
import img.util.StringWriter;

public class WzStringProperty implements WzProperty {

    private final byte VT_BSTR = 8;
    private String data;

    WzStringProperty() { }

    @Override
    public void read(ImgSeekableInputStream stream) {
        this.data = stream.getStringWriter().internalDeserializeString(stream);
    }

    @Override
    public void write(StringWriter stringWriterPool, String key,
                      ImgWritableOutputStream stream) {

        stringWriterPool.internalSerializeString(stream, key, (byte) 0x00, (byte) 0x01);
        stream.writeByte(VT_BSTR);
        stringWriterPool.internalSerializeString(stream, this.data, (byte) 0x00, (byte) 0x01);
    }
}
