package img.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import img.record.ImgCache;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class JsonFileRepository {

    private static final Gson gson = (new GsonBuilder()).setPrettyPrinting().create();

    private final Map<String, Long> stringToOffset = new LinkedHashMap<>();
    private final Map<Long, String> offsetToString = new LinkedHashMap<>();

    private final Path path;

    public JsonFileRepository(Path path) {
        this.path = path;
    }

    public void toOffset(String name, long offset) {
        this.stringToOffset.put(name, offset);
    }

    public void toString(long offset, String name) {
        this.offsetToString.put(offset, name);
    }

    public void saveToFile() {
        Path outputPath = getPath().resolveSibling(getPath().getFileName() + ".json");
        ImgCache cache = new ImgCache(this.stringToOffset, this.offsetToString);

        try (FileWriter writer = new FileWriter(outputPath.toFile())) {
            gson.toJson(cache, writer);
        } catch (IOException io) {
            log.error("An error occurred when saving the file.", io);
        }
    }

    public ImgCache loadFromFile() {
        try (FileReader reader = new FileReader(getPath().toFile() + ".json")) {
            return gson.fromJson(reader, ImgCache.class);
        } catch (IOException io) {
            log.error("An error occurred when saving the file.", io);
            return new ImgCache(null, null);
        }
    }
}
