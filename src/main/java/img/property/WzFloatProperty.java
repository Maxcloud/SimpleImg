package img.property;

import img.crypto.WzStringCodec;
import img.io.impl.ImgInputStream;
import img.io.impl.ImgWritableOutputStream;

public class WzFloatProperty implements WzProperty {

    private final byte VT_R4 = 4;
    private float data;

    WzFloatProperty() { }

    @Override
    public void read(WzStringCodec codec, ImgInputStream stream) {
        this.data = stream.decodeFloat();
    }

    @Override
    public void write(WzStringCodec codec, String key, ImgWritableOutputStream stream) {

        codec.serialize(stream, key, (byte) 0x00, (byte) 0x01);
        stream.writeByte(VT_R4);
        stream.writeCompressedFloat(data);
    }
}
