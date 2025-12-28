package img.model;

import img.io.ImgSeekableInputStream;
import img.io.ImgWritableOutputStream;
import img.property.WzPropertyList;
import img.util.StringWriter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class WzImgFile {

    private WzPropertyList property;

    public void parse(ImgSeekableInputStream stream) {
        stream.getStringWriter().internalDeserializeString(stream);
        this.property = new WzPropertyList();
        this.property.read(stream);
    }

    public void write(StringWriter stringWriterPool, String key, ImgWritableOutputStream stream) {
        stringWriterPool.internalSerializeString(stream, key, (byte) 0x73, (byte) 0x1B);
        property.write(stringWriterPool, key, stream);
    }
}
