package img;

import img.cache.JsonFileRepository;
import img.io.RecyclableSeekableStream;
import img.record.ImgCache;
import img.snippets.production.ImplWzDataRequest;
import img.snippets.production.WzDataFunction;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@NoArgsConstructor
@Slf4j
public class ReadImgFile<T> {

    public T fromImg(ImplWzDataRequest imgDataRequest, WzDataFunction<T> bfnRequest) {

        Path filePath = imgDataRequest.getFilePath();
        String imgPath = imgDataRequest.getImgPath();

        JsonFileRepository stringCache = new JsonFileRepository(filePath);
        ImgCache imgCache = stringCache.loadFromFile();

        try (RecyclableSeekableStream stream = new RecyclableSeekableStream(filePath)) {
            WzPathNavigator root = new WzPathNavigator(imgPath, imgCache);
            return bfnRequest.apply(stream, root);
        } catch (Exception e) {
            log.warn("Error reading files: {}", e.getMessage());
            return null;
        }
    }

}
