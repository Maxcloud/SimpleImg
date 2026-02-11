package img;

import img.io.impl.ImgRecyclableSeekableStream;
import img.io.repository.JsonFileRepository;
import img.model.common.FileImgRecord;
import img.snippets.production.WzDataConsumer;
import img.snippets.production.WzDataFunction;
import img.snippets.production.WzImplDataRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class ReadImgFile {

    Logger log = LoggerFactory.getLogger(ReadImgFile.class);

    private final ImgFileCache oImgFileCache;

    public ReadImgFile(ImgFileCache oImgFileCache) {
        this.oImgFileCache = oImgFileCache;
    }

    /**
     * Applies a function to read data from an IMG file based on the provided data request.
     *
     * @param oImgDataRequest The data request containing file path and image path information.
     * @param oConsumeRequest The function to apply for reading data from the IMG file.
     * @param <T>             The type of the result produced by the function.
     * @return The result produced by the function, or null if an error occurs.
     */
    public <T> T apply(WzImplDataRequest oImgDataRequest, WzDataFunction<T> oConsumeRequest) {
        var oResult = getImgRecord(oImgDataRequest);

        var oFilePath = oResult.filePath();
        var oImgPath = oResult.imgPath();
        var oFileImgRecord = oResult.fileImgRecord();

        try (var oStream = new ImgRecyclableSeekableStream(oFilePath)) {
            var oRoot = new WzPathNavigator(oImgPath, oFileImgRecord);
            return oConsumeRequest.apply(oStream, oRoot);
        } catch (Exception e) {
            System.out.println(oResult.filePath().toAbsolutePath());
            log.warn("Error reading files.", e);
            return null;
        }
    }

    /**
     * Accepts a consumer to read data from an IMG file based on the provided data request.
     *
     * @param oImgDataRequest The data request containing file path and image path information.
     * @param oConsumeRequest The consumer to accept for reading data from the IMG file.
     */
    public void accept(WzImplDataRequest oImgDataRequest, WzDataConsumer oConsumeRequest) {
        var oResult = getImgRecord(oImgDataRequest);

        var oFilePath = oResult.filePath();
        var oImgPath = oResult.imgPath();
        var oFileImgRecord = oResult.fileImgRecord();

        try(var oStream = new ImgRecyclableSeekableStream(oFilePath)) {
            var oRoot = new WzPathNavigator(oImgPath, oFileImgRecord);
            oConsumeRequest.accept(oStream, oRoot);
        } catch (Exception e) {
            log.error("Error reading files: ", e);
        }
    }

    private ImgRecord getImgRecord(WzImplDataRequest oImgDataRequest) {

        var oImgCache = oImgFileCache.getImgCache();
        var oFilePath = oImgDataRequest.getFilePath();
        var oImgPath = oImgDataRequest.getImgPath();
        System.out.println("Img Cache: " + oImgCache.keySet() + ", Requesting: " + oFilePath + " | " + oImgPath);

        var oRepository = new JsonFileRepository<>(oFilePath, FileImgRecord.class);
        var oFileImgRecord = oImgCache.computeIfAbsent(
                oFilePath, oPath -> oRepository.loadFromJson()
        );

        return new ImgRecord(oFilePath, oImgPath, oFileImgRecord);
    }

    private record ImgRecord(Path filePath, String imgPath, FileImgRecord fileImgRecord) { }

}
