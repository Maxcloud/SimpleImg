package img;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.util.Properties;

@Getter
@Slf4j
public class Config {

    @Getter
    public static Config instance = new Config();

    private final Properties properties = new Properties();

    public Config() {
        try (FileInputStream input = new FileInputStream("config/config.ini")) {
            properties.load(input);
        } catch (Exception e) {
            log.error("An error occurred while loading the config file.", e);
        }
    }

}
