package img.property;

import img.crypto.WzStringCodec;
import img.io.impl.ImgInputStream;
import img.io.impl.ImgWritableOutputStream;

public class WzUOLProperty implements WzProperty {

    private byte VT_EMPTY;
    private String data;

    @Override
    public void read(WzStringCodec codec, ImgInputStream stream) {
        VT_EMPTY = stream.readByte();
        this.data = codec.deserialize(stream);
    }

    @Override
    public void write(WzStringCodec codec, String key,
                      ImgWritableOutputStream stream) {

        stream.writeByte(VT_EMPTY);
        codec.serialize(stream, this.data, (byte) 0x00, (byte) 0x01);
    }
}
