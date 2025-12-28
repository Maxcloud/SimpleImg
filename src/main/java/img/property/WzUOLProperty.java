package img.property;

import img.io.ImgSeekableInputStream;
import img.io.ImgWritableOutputStream;
import img.util.StringWriter;

public class WzUOLProperty implements WzProperty {

    private String name = "UOL";
    private String uol;

    @Override
    public void read(ImgSeekableInputStream stream) {
        stream.readByte();
        this.uol = stream.getStringWriter().internalDeserializeString(stream);
    }

    @Override
    public void write(StringWriter stringWriterPool, String key, ImgWritableOutputStream stream) {
        stringWriterPool.internalSerializeString(stream, this.uol, (byte) 0x00, (byte) 0x01);
    }
}
