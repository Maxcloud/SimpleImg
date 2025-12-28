package img.property;

import img.io.ImgSeekableInputStream;
import img.io.ImgWritableOutputStream;
import img.util.StringWriter;

public class WzVectorProperty implements WzProperty {

    private int x;
    private int y;

    @Override
    public void read(ImgSeekableInputStream stream) {
        this.x = stream.decodeInt();
        this.y = stream.decodeInt();
    }

    @Override
    public void write(StringWriter wzStringPool, String key, ImgWritableOutputStream output) {
        output.writeCompressedInt(x);
        output.writeCompressedInt(y);
    }
}
