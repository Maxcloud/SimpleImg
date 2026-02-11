package img;

import img.model.common.FileImgRecord;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

public class ImgFileCache {

    /**
     * A thread-safe cache mapping file paths to their corresponding image records.
     */
    private final ConcurrentHashMap<Path, FileImgRecord> mImgCache = new ConcurrentHashMap<>();


    public ConcurrentHashMap<Path, FileImgRecord> getImgCache() {
        return mImgCache;
    }

    public void insertFileImgRecord(Path path, FileImgRecord record) {
        mImgCache.put(path, record);
    }

}
