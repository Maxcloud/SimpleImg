package img.property;

import img.crypto.WzStringCodec;
import img.io.impl.ImgInputStream;
import img.io.impl.ImgWritableOutputStream;

public class WzShortProperty implements WzProperty {

    private final byte VT_I2 = 2;
    private short data;

    WzShortProperty() { }

    @Override
    public void read(WzStringCodec codec, ImgInputStream stream) {
        this.data = stream.readShort();
    }

    @Override
    public void write(WzStringCodec codec, String key,
                      ImgWritableOutputStream stream) {

        codec.serialize(stream, key, (byte) 0x00, (byte) 0x01);
        stream.writeByte(VT_I2);
        stream.writeShort(data);
    }
}
