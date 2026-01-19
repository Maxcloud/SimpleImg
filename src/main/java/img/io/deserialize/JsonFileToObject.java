package img.io.deserialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class JsonFileToObject<T> {

    Logger log = LoggerFactory.getLogger(JsonFileToObject.class);

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

    public T createObjFromFile() {
        try (FileReader reader = new FileReader(getPath().toFile());
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            return createDefaultGson().fromJson(bufferedReader, clazz);
        } catch (FileNotFoundException e) {
            log.error("FileNotFound Exception: file not found: {}", e.getMessage());
            return null;
        } catch (IOException io) {
            log.error("An error occurred while reading the file.", io);
            return null;
        }
    }

}
