package img.model.common;

import wz.WzNode;
import img.io.impl.ImgReadableInputStream;
import img.io.impl.ImgWritableOutputStream;
import img.property.WzPropertyList;
import img.util.StringWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WzImgFile {

    Logger log = LoggerFactory.getLogger(WzImgFile.class);

    private WzPropertyList property;

    public WzImgFile() { }

    public void parse(ImgReadableInputStream stream) {
        stream.getStringWriter().internalDeserializeString(stream);
        this.property = new WzPropertyList();
        this.property.read(stream);
    }

    public void write(StringWriter stringWriterPool, String key, ImgWritableOutputStream stream) {
        /*if (property.getProperties().isEmpty()) {
            System.out.printf("%s has no properties to write. Skipping...\r\n", key);
            return;
        }*/
        stringWriterPool.internalSerializeString(stream, key, WzNode.NEW_ARCHIVE, WzNode.ALREADY_EXISTS);
        property.write(stringWriterPool, key, stream);
    }
}
