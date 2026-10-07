package wz.property;

import img.crypto.WzStringRegistry;
import img.io.ImgInputStream;
import img.io.ImgWritableOutputStream;

public interface WzProperty {
    void read(WzStringRegistry codec, ImgInputStream stream);
    void write(WzStringRegistry codec, String key, ImgWritableOutputStream output);
}
