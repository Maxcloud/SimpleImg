package img;

import img.cache.JsonFileRepository;
import img.record.WzImgCache;
import img.io.RecyclableSeekableStream;
import img.snippets.production.WzImplDataRequest;
import img.snippets.production.WzDataConsumer;
import img.snippets.production.WzDataFunction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
@Slf4j
public class ReadImgFile {

    @Getter
    private static final ReadImgFile instance = new ReadImgFile();
    private final ConcurrentHashMap<Path, WzImgCache> imgCacheMap = new ConcurrentHashMap<>();

    public <T> T apply(WzImplDataRequest imgDataRequest, WzDataFunction<T> fnRequest) {
        Path filePath = imgDataRequest.getFilePath();
        String imgPath = imgDataRequest.getImgPath();

        WzImgCache wzImgCache = imgCacheMap.computeIfAbsent(filePath, path -> {
            JsonFileRepository stringCache = new JsonFileRepository(path);
            return stringCache.loadFromFile();
        });

        try (RecyclableSeekableStream stream = new RecyclableSeekableStream(filePath)) {
            WzPathNavigator root = new WzPathNavigator(imgPath, wzImgCache);
            return fnRequest.apply(stream, root);
        } catch (Exception e) {
            System.out.println(filePath.toAbsolutePath().toString());
            log.warn("Error reading files.", e);
            return null;
        }
    }

    public void accept(WzImplDataRequest imgDataRequest, WzDataConsumer fnRequest) {
        Path filePath = imgDataRequest.getFilePath();
        String imgPath = imgDataRequest.getImgPath();

        WzImgCache wzImgCache = imgCacheMap.computeIfAbsent(filePath, path -> {
            JsonFileRepository stringCache = new JsonFileRepository(path);
            return stringCache.loadFromFile();
        });

        try (RecyclableSeekableStream stream = new RecyclableSeekableStream(filePath)) {
            WzPathNavigator root = new WzPathNavigator(imgPath, wzImgCache);
            fnRequest.accept(stream, root);
        } catch (Exception e) {
            System.out.println(filePath.toAbsolutePath().toString());
            log.error("Error reading files.", e);
        }
    }

    public WzPathNavigator getRoot(WzImplDataRequest implDataRequest) {
        Path filePath = implDataRequest.getFilePath();

        WzImgCache wzImgCache = imgCacheMap.computeIfAbsent(filePath, path -> {
            JsonFileRepository stringCache = new JsonFileRepository(path);
            return stringCache.loadFromFile();
        });

        return new WzPathNavigator("", wzImgCache);
    }
}
