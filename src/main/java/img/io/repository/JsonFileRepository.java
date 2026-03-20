package img.io.repository;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import img.io.deserialize.JsonFileToObject;
import img.model.common.FileImgRecord;

public class JsonFileRepository<T> extends JsonFileToObject<T> {

    /** path and offset data */
    private final Map<String, Long> mNameToOffset
            = new LinkedHashMap<>();
    /** offset and path data **/
    private final Map<Long, String> mOffsetToName
            = new LinkedHashMap<>();
    /** offset and vec data **/
    private final Map<Long, String> mOffsetToVec
            = new LinkedHashMap<>();
    /** offset and uol data **/
    private final Map<Long, String> mUolToString
            = new LinkedHashMap<>();

    public JsonFileRepository(Path path, Class<T> clazz) {
        super(path, clazz);
    }

    public void setNameToOffset(String name, long offset) {
        mNameToOffset.put(name, offset);
    }

    public void setOffsetAndName(long offset, String name) {
        mOffsetToName.put(offset, name);
    }

    public void setOffsetToVec(long offset, String name) {
        mOffsetToVec.put(offset, name);
    }

    public void setUolToString(long offset, String uol) {
        mUolToString.put(offset, uol);
    }

    public void saveAsJson() {
        FileImgRecord oFileImgRecord = new FileImgRecord(
                mNameToOffset,
                mOffsetToName,
                mOffsetToVec,
                mUolToString
        );
        saveAsJson(oFileImgRecord);
    }

}
