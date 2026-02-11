package img.property;

import img.crypto.WzStringCodec;
import img.io.impl.ImgInputStream;
import img.io.impl.ImgWritableOutputStream;

public interface WzProperty {
    void read(WzStringCodec codec, ImgInputStream stream);
    void write(WzStringCodec codec, String key, ImgWritableOutputStream output);
}
