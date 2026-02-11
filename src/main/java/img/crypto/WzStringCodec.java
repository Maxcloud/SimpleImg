package img.crypto;

import img.io.impl.ImgInputStream;
import img.io.impl.ImgWritableOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class WzStringCodec {

    Logger log = LoggerFactory.getLogger(WzStringCodec.class);

    public final Map<Long, String> fromArchive = new HashMap<>();
    public final Map<String, Long> toArchive = new HashMap<>();

    public String deserialize(ImgInputStream stream) {
        String result = null;

        byte type = stream.readByte();
        switch (type) {
            case 0x00: // a new directory entry
            case 0x73: // a new string entry
                long index = stream.getPosition();
                result = stream.decodeString();
                fromArchive.put(index, result);
                break;
            case 0x01: // an existing directory entry
            case 0x1B: // an existing string entry
                long num1 = stream.readInt();
                if (fromArchive.get(num1) != null) {
                    result = fromArchive.get(num1);
                } else {
                    result = stream.decodeStringAtOffsetAndReset(num1);
                    fromArchive.put(num1, result);
                }
                break;
            default:
                log.error("An unhandled flag found! Inside ({}) with flag: {}", stream.getPath(), type);
                break;
        }
        return result;
    }

    public void serialize(ImgWritableOutputStream output,
                          String name, byte bNew, byte bExists) {

        boolean isStringNull = toArchive.get(name) == null;
        output.writeByte(isStringNull ? bNew : bExists);
        if (isStringNull) {
            long index = output.getByteBuf().writerIndex();
            toArchive.put(name, index);
            output.writeString(name);
        } else {
            long offset = toArchive.get(name);
            output.writeInt((int) offset);
        }
    }
}
