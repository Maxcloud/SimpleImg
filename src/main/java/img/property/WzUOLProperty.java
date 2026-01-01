package img.property;

import img.io.ImgSeekableInputStream;
import img.io.ImgWritableOutputStream;
import img.util.StringWriter;

public class WzUOLProperty implements WzProperty {

    private byte VT_EMPTY;
    private String data;

    @Override
    public void read(ImgSeekableInputStream stream) {
        VT_EMPTY = stream.readByte();
        this.data = stream.getStringWriter().internalDeserializeString(stream);
    }

    @Override
    public void write(StringWriter stringWriterPool, String key,
                      ImgWritableOutputStream stream) {

        stream.writeByte(VT_EMPTY);
        stringWriterPool.internalSerializeString(stream, this.data, (byte) 0x00, (byte) 0x01);
    }
}
