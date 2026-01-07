package img.property;

import img.io.ImgSeekableInputStream;
import img.io.ImgWritableOutputStream;
import img.util.StringWriter;

public class WzShortProperty implements WzProperty {

    private final byte VT_I2 = 2;
    private short data;

    WzShortProperty() { }

    @Override
    public void read(ImgSeekableInputStream stream) {
        this.data = stream.readShort();
    }

    @Override
    public void write(StringWriter stringWriterPool, String key,
                      ImgWritableOutputStream stream) {

        stringWriterPool.internalSerializeString(stream, key, (byte) 0x00, (byte) 0x01);
        stream.writeByte(VT_I2);
        stream.writeShort(data);
    }
}
