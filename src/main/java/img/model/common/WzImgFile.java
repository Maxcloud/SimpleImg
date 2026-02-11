package img.model.common;

import img.crypto.WzStringCodec;
import img.io.impl.ImgInputStream;
import wz.WzNode;
import img.io.impl.ImgWritableOutputStream;
import img.property.WzPropertyList;

public class WzImgFile {

    private WzPropertyList property;
    private final WzStringCodec codec;

    public WzImgFile(WzStringCodec codec) {
        this.codec = codec;
    }

    public void parse(ImgInputStream stream) {
        this.codec.deserialize(stream);

        this.property = new WzPropertyList();
        this.property.read(codec, stream);
    }

    public void write(String key, ImgWritableOutputStream stream) {
        codec.serialize(stream, key, WzNode.NEW_ARCHIVE, WzNode.EXIST_ARCHIVE);
        property.write(codec, key, stream);
    }
}
