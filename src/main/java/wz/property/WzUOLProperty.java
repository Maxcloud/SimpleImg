package wz.property;

import img.crypto.WzStringRegistry;
import img.io.ImgInputStream;
import img.io.ImgWritableOutputStream;
import io.netty.buffer.ByteBuf;
import wz.WzNode;

public class WzUOLProperty implements WzProperty {

    private byte VT_EMPTY;
    private String data;

    @Override
    public void read(WzStringRegistry codec, ImgInputStream stream) {
        VT_EMPTY = stream.readByte();
        this.data = codec.deserialize(stream);
    }

    @Override
    public void write(WzStringRegistry codec, String key,
                      ImgWritableOutputStream stream) {
        ByteBuf out = stream.getByteBuf();

        out.writeByte(VT_EMPTY);
        codec.serialize(stream, this.data, WzNode.NEW_DIRECTORY, WzNode.EXIST_DIRECTORY);
    }
}
