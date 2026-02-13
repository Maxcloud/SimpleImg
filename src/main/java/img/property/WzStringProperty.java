package img.property;

import img.crypto.WzStringCodec;
import img.io.impl.ImgInputStream;
import img.io.impl.ImgWritableOutputStream;

public class WzStringProperty implements WzProperty {

    private final byte VT_BSTR = 8;
    private String data;

    WzStringProperty() { }

    @Override
    public void read(WzStringCodec codec, ImgInputStream stream) {
        this.data = codec.deserialize(stream);
    }

    @Override
    public void write(WzStringCodec codec, String key,
                      ImgWritableOutputStream stream) {

        codec.serialize(stream, key, (byte) 0x00, (byte) 0x01);
        stream.writeByte(VT_BSTR);
        codec.serialize(stream, this.data, (byte) 0x00, (byte) 0x01);
    }
}
