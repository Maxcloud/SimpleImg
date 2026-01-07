package img;

import img.cache.DirectoryConfiguration;
import img.cache.JsonFileToObject;
import img.cache.KeyFileRepository;
import img.cryptography.WzCryptography;
import img.model.WzImage;
import img.record.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * SimpleImg is a utility class that traverses a directory structure,
 * parsing WzImage files and dumping their string data to JSON format.
 *
 */
public class SimpleImg {

    Logger log = LoggerFactory.getLogger(SimpleImg.class);

    private static final Path configFile = Path.of("src/main/resources/configuration.json");

    public static void main(String[] args) {

        JsonFileToObject<DirectoryConfiguration> fileToObject =
                new JsonFileToObject<>(configFile, DirectoryConfiguration.class);
        DirectoryConfiguration directoryConfiguration = fileToObject.createObjFromFile();

        Path outputDirectory = Path.of(directoryConfiguration.getOutput());

        KeyFileRepository<Version> repository = new KeyFileRepository<>(Version.class);
        repository.setSecret(directoryConfiguration.getVersion());

        WzCryptography cryptography = new WzCryptography(repository);
        byte[] secret = cryptography.getSecret();

        System.out.println("Starting to dump strings to JSON. Please wait...");
        SimpleImg simpleImg = new SimpleImg();
        simpleImg.dumpStringsToJson(outputDirectory, secret);
        System.out.println("Dumping strings to JSON completed. Please double check the logs for any errors.");
    }

    public void dumpStringsToJson(Path root, byte[] secret) {
        try {
            Files.walkFileTree(root, new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toString().endsWith(".json")) {
                        return FileVisitResult.CONTINUE;
                    }
                    WzImage image = new WzImage(secret);
                    image.parse(file);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.error("An error has occurred while walking the file tree.", e);
        }
    }
}


