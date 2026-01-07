package img;

import img.cache.JsonFileRepository;
import img.cryptography.WzCryptography;
import img.record.WzImgCache;
import img.io.RecyclableSeekableStream;
import img.snippets.production.WzImplDataRequest;
import img.snippets.production.WzDataConsumer;
import img.snippets.production.WzDataFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

@Deprecated
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
        Path filePath = imgDataRequest.getFilePath();
        String imgPath = imgDataRequest.getImgPath();

        WzImgCache wzImgCache = imgCacheMap.computeIfAbsent(filePath, path -> {
            JsonFileRepository stringCache = new JsonFileRepository(path);
            return stringCache.loadFromFile();
        });

        WzCryptography cryptography = WzCryptography.getInstance();
        byte[] secret = cryptography.getSecret();

        try (RecyclableSeekableStream stream = new RecyclableSeekableStream(filePath, secret)) {
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

        WzCryptography cryptography = WzCryptography.getInstance();
        byte[] secret = cryptography.getSecret();

        try (RecyclableSeekableStream stream = new RecyclableSeekableStream(filePath, secret)) {
            WzPathNavigator root = new WzPathNavigator(imgPath, wzImgCache);
            fnRequest.accept(stream, root);
        } catch (Exception e) {
            System.out.println(filePath.toAbsolutePath());
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
