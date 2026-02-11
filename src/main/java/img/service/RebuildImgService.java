package img.service;

import img.WzConfiguration;
import img.WzImgFileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * SimpleImgNoCanvas is a utility class that traverses a directory,
 * structure and processes each img file, and writes the output
 * without canvas properties.
 */
public class RebuildImgService {

    Logger log = LoggerFactory.getLogger(RebuildImgService.class);

    private static final Path configFile = Path.of("src/main/resources/configuration.json");

    private final WzConfiguration configuration;

    /**
     * Main method to start the re-dumping process of img files without canvas properties.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        WzConfiguration configuration = new WzConfiguration(configFile);

        System.out.println("Starting to re-write img files without canvas properties. Please wait...");
        var service = new RebuildImgService(configuration);
        var output = configuration.getOutput();

        service.writeImgFile(output);
        System.out.println("Operation completed. Please double check the logs for any errors.");
    }

    public RebuildImgService(WzConfiguration configuration) {
        this.configuration = configuration;
    }

    private boolean fnExcluded(Path oPath) {
        String sName = oPath.getFileName().toString();
        return !sName.endsWith(".json");
    }

    private void walkFileTree(Path oPath) {
        if (Files.isDirectory(oPath)) {
            writeImgFile(oPath);
        } else if (Files.isRegularFile(oPath)) {
            try {
                WzImgFileWriter writeImgFile = new WzImgFileWriter(configuration);
                writeImgFile.parse(oPath);
            } catch (Exception e) {
                log.error("An error occurred when processing {}.", oPath.getFileName(), e);
            }
        }
    }

    public void writeImgFile(Path oPath) {
        try (Stream<Path> stream = Files.list(oPath)) {
            stream.filter(this::fnExcluded).forEach(this::walkFileTree);
        } catch (IOException e) {
            log.error("An error has occurred while listing files in the output directory.", e);
        }
    }
}
