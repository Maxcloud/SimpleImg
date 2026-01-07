package img.util;

import img.io.ImgSeekableInputStream;
import img.io.ImgWritableOutputStream;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class StringWriter {

    public final Map<Long, String> fromArchive = new HashMap<>();
    public final Map<String, Long> toArchive = new HashMap<>();

    public String internalDeserializeString(ImgSeekableInputStream stream) {
        String result = null;

        byte type = stream.readByte();
        switch (type) {
            case 0x00:
            case 0x73:
                long index = stream.getPosition();
                result = stream.decodeString();
                fromArchive.put(index, result);
                break;
            case 0x01:
            case 0x1B:
                long num1 = stream.readInt();
                if (fromArchive.get(num1) != null) {
                    result = fromArchive.get(num1);
                } else {
                    result = stream.decodeStringAtOffsetAndReset(num1);
                    fromArchive.put(num1, result);
                }
                break;
            default:
                System.out.println("Unhandled string type: (" + stream.getPath() + ")");
                break;
        }
        return result;
    }

    public void internalSerializeString(ImgWritableOutputStream output,
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
