package img;

import img.io.RecyclableSeekableStream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

@Getter
@Slf4j
public class WzValueReader {

    private final RecyclableSeekableStream stream;
    private final WzPathNavigator directory;

    public WzValueReader(RecyclableSeekableStream stream, WzPathNavigator directory) {
        this.stream = stream;
        this.directory = directory;
    }

    /**
     * Retrieves a short value from the cache using a property name.
     *
     * @param property The property name.
     * @return The short value, or 0 if an error occurs.
     */
    public short readShort(String property) {
        long offset = getDirectory().getOffset(property);
        stream.seek(offset);
        if (offset == -1) {
            return 0;
        }
        var variant = stream.readByte();

        var b = stream.readByte();
        return stream.readShort();
    }

    /**
     * Retrieves an integer value from the cache using a property name.
     *
     * @param property The property name.
     * @return The integer value, or 0 if an error occurs.
     */
    public int readInt(String property) {
        long offset = getDirectory().getOffset(property);
        if (offset == -1) {
            return 0;
        }
        stream.seek(offset);
        var variant = stream.readByte();

        var variantType = Variant.fromByte(variant);
        if (variantType == Variant.VT_I2) {
            return stream.readShort();
        }

        var b = stream.readByte();
        return (b == -128 ? stream.readInt() : b);
    }

    /**
     * Retrieves a float value from the cache using a property name.
     *
     * @param property The property name.
     * @return The float value, or 0 if an error occurs.
     */
    public float readFloat(String property) {
        long offset = getDirectory().getOffset(property);
        if (offset == -1) {
            return 0;
        }
        stream.seek(offset);
        var variant = stream.readByte();

        byte b = stream.readByte();
        return (b == -128 ? stream.readFloat() : b);
    }

    /**
     * Retrieves a double value from the cache using a property name.
     *
     * @param property The property name.
     * @return The double value, or 0 if an error occurs.
     */
    public double readDouble(String property) {
        long offset = getDirectory().getOffset(property);
        if (offset == -1) {
            return 0;
        }
        stream.seek(offset);
        var variant = stream.readByte();

        return stream.readDouble();
    }

    /**
     * Retrieves a string value from the cache using an attribute name.
     *
     * @param attr The attribute name.
     * @return The string value, or null if an error occurs.
     */
    public String readString(String attr) {
        Map<String, Long> strings = getDirectory().getStrings();
        Objects.requireNonNull(strings, "There is no string cache for this img file.");

        Map<Long, String> offsets = getDirectory().getOffsets();
        Objects.requireNonNull(offsets, "There is no offset cache for this img file.");

        long offset = getDirectory().getOffset(attr);
        String result = getDirectory().getString(offset);
        if (result == null || result.isEmpty()) {
            log.warn("An error might have occurred with returning a offset from the cache.");
        }
        return result;
    }

}
