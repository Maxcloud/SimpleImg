package app;

import img.model.common.WzImgCache;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class TmpFileCache {

    private final Map<Path, WzImgCache> mPathToImgCache;
    private final Map<Path, byte[]> mPathToRawDataCache;

    public TmpFileCache() {
        mPathToImgCache = new LinkedHashMap<>();
        mPathToRawDataCache = new LinkedHashMap<>();
    }

    public Map<Path, WzImgCache> getPathToImgCache() {
        return mPathToImgCache;
    }

    public void addImgCache(Path path, WzImgCache imgCache) {
        mPathToImgCache.put(path, imgCache);
    }

    public Map<Path, byte[]> getRawDataCache() {
        return mPathToRawDataCache;
    }

    public void addRawDataCache(Path path, byte[] rawData) {
        mPathToRawDataCache.put(path, rawData);
    }

}
