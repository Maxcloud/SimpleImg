package img.crypto;

import img.io.ImgInputStream;
import img.io.ImgWritableOutputStream;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wz.WzNode;

import java.util.HashMap;
import java.util.Map;

public class WzStringRegistry {
    Logger log = LoggerFactory.getLogger(WzStringRegistry.class);

    public final Map<Long, String> fromArchive = new HashMap<>();
    public final Map<String, Long> toArchive = new HashMap<>();

    public String deserialize(ImgInputStream in) {
        String result = null;

        byte type = in.readByte();
        switch (type) {
            case 0x00: // a new directory entry
            case 0x73: // a new string entry
                long index = in.getPosition();
                result = in.decodeString();
                fromArchive.put(index, result);
                break;
            case 0x01: // an existing directory entry
            case 0x1B: // an existing string entry
                long num1 = in.readInt();
                if (fromArchive.get(num1) != null) {
                    result = fromArchive.get(num1);
                } else {
                    result = in.decodeStringAtOffsetAndReset(num1);
                    fromArchive.put(num1, result);
                }
                break;
            default:
                log.error("An unhandled flag found! Inside ({}) with flag: {}", in.getPath(), type);
                break;
        }
        return result;
    }

    public void serialize(ImgWritableOutputStream out, String name) {
        serialize(out, name, WzNode.NEW_ARCHIVE, WzNode.EXIST_ARCHIVE);
    }

    public void serialize(ImgWritableOutputStream out,
                          String name, byte bNew, byte bExists) {

        boolean isStringNull = toArchive.get(name) == null;
        ByteBuf bb = out.getByteBuf();

        bb.writeByte(isStringNull ? bNew : bExists);
        if (isStringNull) {
            long index = bb.writerIndex();
            toArchive.put(name, index);
            out.writeString(name);
        } else {
            long offset = toArchive.get(name);
            bb.writeIntLE((int) offset);
        }
    }
}
