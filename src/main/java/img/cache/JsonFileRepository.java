package img.cache;

import java.io.*;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import img.record.WzImgCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonFileRepository<T> extends JsonFileToObject<T> {

    Logger log = LoggerFactory.getLogger(JsonFileRepository.class);

    private final Map<String, Long> stringToOffset  = new LinkedHashMap<>();
    private final Map<Long, String> offsetToString  = new LinkedHashMap<>();
    private final Map<Long, String> uolToString     = new LinkedHashMap<>();

    public JsonFileRepository(Path path, Class<T> clazz) {
        super(path, clazz);
    }

    public void toOffset(String name, long offset) {
        this.stringToOffset.put(name, offset);
    }

    public void toString(long offset, String name) {
        this.offsetToString.put(offset, name);
    }

    public void toUOL(long offset, String uol) { this.uolToString.put(offset, uol); }

    public void saveToFile() {
        Path outputPath = getPath().resolveSibling(getPath().getFileName() + ".json");
        WzImgCache cache = new WzImgCache(this.stringToOffset, this.offsetToString, this.uolToString);

        try (FileWriter writer = new FileWriter(outputPath.toFile());
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {

            createDefaultGson().toJson(cache, bufferedWriter);
        } catch (IOException io) {
            log.error("An error occurred when saving the file.", io);
        }
    }

    /*public WzImgCache loadFromFile() {
        try (FileReader reader = new FileReader(getPath().toFile() + ".json");
             BufferedReader bufferedReader = new BufferedReader(reader)) {

            return gson.fromJson(bufferedReader, WzImgCache.class);
        } catch (IOException io) {
            // log.error("An error occurred when saving the file.", io);
            return new WzImgCache(null, null, null);
        }
    }*/
}
