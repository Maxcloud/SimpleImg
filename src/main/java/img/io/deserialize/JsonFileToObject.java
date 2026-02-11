package img.io.deserialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import img.model.common.FileImgRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class JsonFileToObject<T> {

    private static final Logger log = LoggerFactory.getLogger(JsonFileToObject.class);

    private final Path path;
    private final Class<T> clazz;

    public JsonFileToObject(Path path, Class<T> clazz) {
        this.path = path;
        this.clazz = clazz;
    }

    public Path getPath() {
        return path;
    }

    protected static Gson createDefaultGson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    public void saveAsJson(FileImgRecord oFileImgRecord) {
        Path oOutputPath = getPath().resolveSibling(getPath().getFileName() + ".json");
        try (var oBufferedWriter = Files.newBufferedWriter(oOutputPath)) {
            createDefaultGson().toJson(oFileImgRecord, oBufferedWriter);
        } catch (NoSuchFileException e) {
            log.error("NoSuchFileException Exception: file not found: {}", e.getMessage());
        } catch (IOException io) {
            log.error("An error occurred when saving the file.", io);
        }
    }

    public T loadFromJson() {
        try (var oBufferedRead = Files.newBufferedReader(getPath())) {
            return createDefaultGson().fromJson(oBufferedRead, clazz);
        } catch (NoSuchFileException e) {
            log.error("NoSuchFileException Exception: file not found: {}", e.getMessage());
            return null;
        } catch (IOException io) {
            log.error("An error occurred while reading the file.", io);
            return null;
        }
    }

}
