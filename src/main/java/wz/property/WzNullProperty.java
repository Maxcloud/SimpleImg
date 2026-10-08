package wz.property;

import img.crypto.WzStringRegistry;
import img.io.ImgInputStream;
import img.io.ImgWritableOutputStream;
import io.netty.buffer.ByteBuf;

public class WzNullProperty implements WzProperty {

    WzNullProperty() { }

    @Override
    public void read(WzStringRegistry codec, ImgInputStream stream) { }

    @Override
    public void write(WzStringRegistry codec, String key,
                      ImgWritableOutputStream stream) {
        ByteBuf out = stream.getByteBuf();

        codec.serialize(stream, key, (byte) 0x00, (byte) 0x01);
        out.writeByte(0); // VT_EMPTY
    }
}
