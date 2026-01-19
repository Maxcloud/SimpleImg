package img.property;

import img.io.impl.ImgReadableInputStream;
import img.io.impl.ImgWritableOutputStream;
import img.util.StringWriter;

public interface WzProperty {
    void read(ImgReadableInputStream stream);
    void write(StringWriter wzStringPool, String key, ImgWritableOutputStream output);
    default void parse(ImgReadableInputStream stream) { }
}
