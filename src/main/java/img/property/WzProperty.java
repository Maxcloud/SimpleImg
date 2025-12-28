package img.property;

import img.io.ImgSeekableInputStream;
import img.io.ImgWritableOutputStream;
import img.util.StringWriter;

public interface WzProperty {
    void read(ImgSeekableInputStream stream);
    void write(StringWriter wzStringPool, String key, ImgWritableOutputStream output);
    default void parse(ImgSeekableInputStream stream) { }
}
