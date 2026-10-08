package wz.property;

import img.crypto.WzStringRegistry;
import img.io.ImgInputStream;
import img.io.ImgWritableOutputStream;

public class WzVectorProperty implements WzProperty {

    private int x;
    private int y;

    @Override
    public void read(WzStringRegistry codec, ImgInputStream stream) {
        this.x = stream.decodeInt();
        this.y = stream.decodeInt();
    }

    @Override
    public void write(WzStringRegistry codec, String key,
                      ImgWritableOutputStream output) {

        output.writeCompressedInt(x);
        output.writeCompressedInt(y);
    }
}
