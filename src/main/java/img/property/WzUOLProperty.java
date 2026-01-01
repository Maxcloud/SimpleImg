package img.property;

import img.io.ImgSeekableInputStream;
import img.io.ImgWritableOutputStream;
import img.util.StringWriter;

public class WzUOLProperty implements WzProperty {

    private final byte VT_EMPTY = 0;
    private String data;

    @Override
    public void read(ImgSeekableInputStream stream) {
        byte b = stream.readByte();
        this.data = stream.getStringWriter().internalDeserializeString(stream);
    }

    @Override
    public void write(StringWriter stringWriterPool, String key, ImgWritableOutputStream stream) {
        // stringWriterPool.internalSerializeString(stream, key, (byte) 0x00, (byte) 0x01);
        stream.writeByte(VT_EMPTY);
        stringWriterPool.internalSerializeString(stream, this.data, (byte) 0x00, (byte) 0x01);
    }
}
