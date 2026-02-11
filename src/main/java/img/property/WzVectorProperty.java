package img.property;

import img.crypto.WzStringCodec;
import img.io.impl.ImgInputStream;
import img.io.impl.ImgWritableOutputStream;
import img.util.StringWriter;

public class WzVectorProperty implements WzProperty {

    private int x;
    private int y;

    @Override
    public void read(WzStringCodec codec, ImgInputStream stream) {
        this.x = stream.decodeInt();
        this.y = stream.decodeInt();
    }

    @Override
    public void write(WzStringCodec codec, String key,
                      ImgWritableOutputStream output) {

        output.writeCompressedInt(x);
        output.writeCompressedInt(y);
    }
}
