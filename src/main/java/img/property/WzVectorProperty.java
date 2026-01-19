package img.property;

import img.io.impl.ImgReadableInputStream;
import img.io.impl.ImgWritableOutputStream;
import img.util.StringWriter;

public class WzVectorProperty implements WzProperty {

    private int x;
    private int y;

    @Override
    public void read(ImgReadableInputStream stream) {
        this.x = stream.decodeInt();
        this.y = stream.decodeInt();
    }

    @Override
    public void write(StringWriter wzStringPool, String key,
                      ImgWritableOutputStream output) {

        output.writeCompressedInt(x);
        output.writeCompressedInt(y);
    }
}
