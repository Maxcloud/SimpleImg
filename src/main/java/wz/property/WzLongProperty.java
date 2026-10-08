package wz.property;

import img.crypto.WzStringRegistry;
import img.io.ImgInputStream;
import img.io.ImgWritableOutputStream;
import io.netty.buffer.ByteBuf;
import wz.WzNode;

public class WzLongProperty implements WzProperty {

    private static final byte VT_I8 = 20;
    private long data;

    WzLongProperty() { }

    @Override
    public void read(WzStringRegistry codec, ImgInputStream stream) {
        this.data = stream.decodeLong();
    }

    @Override
    public void write(WzStringRegistry codec, String key,
                      ImgWritableOutputStream stream) {
        ByteBuf out = stream.getByteBuf();

        codec.serialize(stream, key, WzNode.NEW_DIRECTORY, WzNode.EXIST_DIRECTORY);

        out.writeByte(VT_I8);
        stream.writeCompressedLong(data);
    }
}
