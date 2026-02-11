package img.property;

import img.crypto.WzStringCodec;
import img.io.impl.ImgInputStream;
import img.io.impl.ImgWritableOutputStream;
import img.util.StringWriter;

public class WzNullProperty implements WzProperty {

    WzNullProperty() { }

    @Override
    public void read(WzStringCodec codec, ImgInputStream stream) { }

    @Override
    public void write(WzStringCodec codec, String key,
                      ImgWritableOutputStream stream) {

        codec.serialize(stream, key, (byte) 0x00, (byte) 0x01);
        stream.writeByte(0); // VT_EMPTY
    }
}
