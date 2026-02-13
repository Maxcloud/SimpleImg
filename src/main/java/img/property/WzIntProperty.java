package img.property;

import img.crypto.WzStringCodec;
import img.io.impl.ImgInputStream;
import img.io.impl.ImgWritableOutputStream;

public class WzIntProperty implements WzProperty {

    private final byte VT_I4 = 3;
    private int data;

    WzIntProperty() { }

    @Override
    public void read(WzStringCodec codec, ImgInputStream stream) {
        this.data = stream.decodeInt();
    }

    @Override
    public void write(WzStringCodec codec, String key, ImgWritableOutputStream stream) {

        codec.serialize(stream, key, (byte) 0x00, (byte) 0x01);
        stream.writeByte(VT_I4);
        stream.writeCompressedInt(data);
    }
}
