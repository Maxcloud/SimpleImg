package img;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class EnvironmentConfig {

    private final Logger log;

    private static final String CONFIG_FILE = "release/config/config-%s.properties";

    private final Properties props;

    public EnvironmentConfig() {
        props = new Properties();
        log = LoggerFactory.getLogger(EnvironmentConfig.class);

        String oEnvironment = System.getProperty("env", "default");
        String oConfigFile  = String.format(CONFIG_FILE, oEnvironment);

        try (InputStream input = new FileInputStream(oConfigFile)) {
            props.load(input);
        } catch (Exception e) {
            log.error("Could not load environment-specific configuration. Using default properties.", e);
        }
    }

    /**
     * Retrieves the value for the given key. Checks environment variables first,
     * then falls back to properties file.
     *
     * @param key The configuration key.
     * @return The configuration value.
     */
    public String get(String key) {
        String envValue = System.getenv(key.replace(".", "_").toUpperCase());
        return envValue != null ? envValue : props.getProperty(key);
    }

    /**
     * Retrieves the integer value for the given key.
     *
     * @param key The configuration key.
     * @return The integer configuration value.
     */
    public int getInt(String key) {
        String value = get(key);
        return Integer.parseInt(value);
    }
}
