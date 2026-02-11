package img.model.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class FileImgRecord {

    private final Map<String, Long> offsetCache;
    private final Map<Long, String> stringCache;
    private final Map<Long, String> uolCache;
    private byte[] bytes;

    public FileImgRecord(Map<String, Long> offsetCache,
                         Map<Long, String> stringCache,
                         Map<Long, String> uolCache) {
        this.offsetCache = offsetCache;
        this.stringCache = stringCache;
        this.uolCache = uolCache;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(Path oPath) {
        try {
            bytes = Files.readAllBytes(oPath);
        } catch (IOException e) {
            // swallow exception
        }
    }

    public Map<String, Long> getOffsetCache() {
        return offsetCache;
    }

    public Map<Long, String> getStringCache() {
        return stringCache;
    }

    public Map<Long, String> getUolCache() {
        return uolCache;
    }
}