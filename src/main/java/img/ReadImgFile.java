package img;

import img.io.repository.JsonFileRepository;
import img.crypto.WzCryptography;
import img.model.common.WzImgCache;
import img.io.impl.RecyclableSeekableStream;
import img.snippets.production.WzImplDataRequest;
import img.snippets.production.WzDataConsumer;
import img.snippets.production.WzDataFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

public class ReadImgFile {

    Logger log = LoggerFactory.getLogger(ReadImgFile.class);

    private static ReadImgFile instance;
    private final ConcurrentHashMap<Path, WzImgCache> imgCacheMap = new ConcurrentHashMap<>();

    public static ReadImgFile getInstance() {
        if (instance == null) {
            instance = new ReadImgFile();
        }
        return instance;
    }

    public <T> T apply(WzImplDataRequest imgDataRequest, WzDataFunction<T> fnRequest) {
        Result result = getResult(imgDataRequest);

        try (RecyclableSeekableStream stream = new RecyclableSeekableStream(result.filePath(), result.secret())) {
            WzPathNavigator root = new WzPathNavigator(result.imgPath(), result.wzImgCache());
            return fnRequest.apply(stream, root);
        } catch (Exception e) {
            System.out.println(result.filePath().toAbsolutePath());
            log.warn("Error reading files.", e);
            return null;
        }
    }

    public void accept(WzImplDataRequest imgDataRequest, WzDataConsumer fnRequest) {
        Result result = getResult(imgDataRequest);
        try (RecyclableSeekableStream stream = new RecyclableSeekableStream(result.filePath(), result.secret())) {
            WzPathNavigator root = new WzPathNavigator(result.imgPath(), result.wzImgCache());
            fnRequest.accept(stream, root);
        } catch (Exception e) {
            System.out.println(result.filePath().toAbsolutePath());
            log.error("Error reading files.", e);
        }
    }

    private Result getResult(WzImplDataRequest imgDataRequest) {
        Path filePath = imgDataRequest.getFilePath();
        String imgPath = imgDataRequest.getImgPath();

        JsonFileRepository<WzImgCache> repository =
                new JsonFileRepository<>(filePath, WzImgCache.class);

        WzImgCache wzImgCache = imgCacheMap.computeIfAbsent(filePath, path ->
            repository.createObjFromFile()
        );

        WzCryptography cryptography = WzCryptography.getInstance();
        byte[] secret = cryptography.getSecret();
        return new Result(filePath, imgPath, wzImgCache, secret);
    }

    private record Result(Path filePath, String imgPath, WzImgCache wzImgCache, byte[] secret) { }
}
