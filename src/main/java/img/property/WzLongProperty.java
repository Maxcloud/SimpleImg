package img.property;

import img.crypto.WzStringCodec;
import img.io.impl.ImgInputStream;
import img.io.impl.ImgWritableOutputStream;
import img.util.StringWriter;

public class WzLongProperty implements WzProperty {

    WzLongProperty() { }

    private long data;

    @Override
    public void read(WzStringCodec codec, ImgInputStream stream) {
        this.data = stream.decodeLong();
    }

    @Override
    public void write(WzStringCodec codec, String key,
                      ImgWritableOutputStream stream) {

        codec.serialize(stream, key, (byte) 0x00, (byte) 0x01);

        byte VT_I8 = 20;
        stream.writeByte(VT_I8);
        stream.writeCompressedLong(data);
    }
}
