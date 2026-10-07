package wz.property;

import img.crypto.WzStringRegistry;
import img.io.ImgInputStream;
import img.io.ImgWritableOutputStream;
import io.netty.buffer.ByteBuf;
import wz.WzNode;

public class WzStringProperty implements WzProperty {

    private final static byte VT_BSTR = 8;
    private String data;

    WzStringProperty() { }

    @Override
    public void read(WzStringRegistry codec, ImgInputStream stream) {
        this.data = codec.deserialize(stream);
    }

    @Override
    public void write(WzStringRegistry codec, String key,
                      ImgWritableOutputStream stream) {
        ByteBuf out = stream.getByteBuf();

        codec.serialize(stream, key, WzNode.NEW_DIRECTORY, WzNode.EXIST_DIRECTORY);
        out.writeByte(VT_BSTR);
        codec.serialize(stream, this.data, WzNode.NEW_DIRECTORY, WzNode.EXIST_DIRECTORY);
    }
}
