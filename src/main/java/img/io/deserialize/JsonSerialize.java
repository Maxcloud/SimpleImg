package img.io.deserialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;

public class JsonSerialize {

    Logger log = LoggerFactory.getLogger(JsonSerialize.class);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public <T> void serialize(T object, Path path) {
        try (FileWriter writer = new FileWriter(path.toFile());
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            GSON.toJson(object, bufferedWriter);
        } catch (IOException io) {
            log.error("An error occurred when saving the file.", io);
        }
    }

    public <T> T deserialize(Path path, Class<T> clazz) {
        try (FileReader reader = new FileReader(path.toFile());
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            return GSON.fromJson(bufferedReader, clazz);
        } catch (FileNotFoundException e) {
            log.error("FileNotFound Exception: file not found: {}", e.getMessage());
            return null;
        } catch (IOException io) {
            log.error("An error occurred while reading the file.", io);
            return null;
        }
    }
}
