package img.model;

import img.cryptography.WzNode;
import img.io.ImgSeekableInputStream;
import img.io.ImgWritableOutputStream;
import img.property.WzPropertyList;
import img.util.StringWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WzImgFile {

    Logger log = LoggerFactory.getLogger(WzImgFile.class);

    private WzPropertyList property;

    public WzImgFile() { }

    public void parse(ImgSeekableInputStream stream) {
        stream.getStringWriter().internalDeserializeString(stream);
        this.property = new WzPropertyList();
        this.property.read(stream);
    }

    public void write(StringWriter stringWriterPool, String key, ImgWritableOutputStream stream) {
        stringWriterPool.internalSerializeString(stream, key, WzNode.NEW_ARCHIVE, WzNode.ALREADY_EXISTS);
        property.write(stringWriterPool, key, stream);
    }
}
