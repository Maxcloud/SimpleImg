package img.service;

import img.EnvironmentConfig;
import img.WzConfiguration;
import img.model.image.WzImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;

/**
 * SimpleImg is a utility class that traverses a directory structure,
 * parsing WzImage files and dumping their string data to JSON format.
 *
 */
public class ExportJsonService {

    Logger log = LoggerFactory.getLogger(ExportJsonService.class);

    public static void main(String[] args) {
        WzConfiguration configuration = new WzConfiguration();

        System.out.println("Starting to dump strings to JSON. Please wait, this might take awhile...");
        ExportJsonService exportJsonService = new ExportJsonService();
        exportJsonService.dumpStringsToJson(configuration);
        System.out.println("Dumping strings to JSON completed. Please double check the logs for any errors.");
    }

    public void dumpStringsToJson(WzConfiguration configuration) {
        EnvironmentConfig environment = configuration.getEnvironment();
        Path root = Path.of(environment.get("simple.img.output"));
        Path list = Path.of(environment.get("simple.img.list"));
        int version = environment.getInt("simple.img.version");

        byte[] secret = configuration.getSecret();
        WzImage image = new WzImage(secret, version, list);

        try (var paths = Files.walk(root)) {
            paths.parallel()
                    .filter(this::fnExcluded)
                    .filter(Files::isRegularFile)
                    .forEach(image::parse);
        } catch (IOException e) {
            log.error("An error has occurred while walking the file tree.", e);
        }
    }

    private boolean fnExcluded(Path oPath) {
        String sName = oPath.getFileName().toString();
        return sName.endsWith(".json");
    }
}


