package wz.property;

import img.crypto.WzStringRegistry;
import img.io.ImgInputStream;
import img.io.ImgWritableOutputStream;
import io.netty.buffer.ByteBuf;
import wz.WzNode;

public class WzShortProperty implements WzProperty {

    private final byte VT_I2 = 2;
    private short data;

    WzShortProperty() { }

    @Override
    public void read(WzStringRegistry codec, ImgInputStream stream) {
        this.data = stream.readShort();
    }

    @Override
    public void write(WzStringRegistry codec, String key,
                      ImgWritableOutputStream stream) {
        ByteBuf out = stream.getByteBuf();

        codec.serialize(stream, key, WzNode.NEW_DIRECTORY, WzNode.EXIST_DIRECTORY);
        out.writeByte(VT_I2);
        out.writeShortLE(data);
    }
}
