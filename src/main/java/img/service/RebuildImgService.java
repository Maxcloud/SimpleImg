package img.service;

import img.WzImgFileWriter;
import img.configuration.DirectoryConfiguration;
import img.io.deserialize.JsonFileToObject;
import img.io.repository.KeyFileRepository;
import img.crypto.WzCryptography;
import img.model.common.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * SimpleImgNoCanvas is a utility class that traverses a directory,
 * structure and processes each img file, and writes the output
 * without canvas properties.
 */
public class RebuildImgService {

    Logger log = LoggerFactory.getLogger(RebuildImgService.class);

    private static final Path configFile = Path.of("src/main/resources/configuration.json");

    private static DirectoryConfiguration configuration;
    private static String outputPath;
    private static byte[] secret;
    /**
     * Main method to start the re-dumping process of img files without canvas properties.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {

        JsonFileToObject<DirectoryConfiguration> fileToObject =
                new JsonFileToObject<>(configFile, DirectoryConfiguration.class);
        configuration = fileToObject.createObjFromFile();
        outputPath = configuration.getOutput();

        KeyFileRepository<Version> repository =
                new KeyFileRepository<>(Version.class);
        repository.setSecret(configuration.getVersion());

        WzCryptography cryptography = new WzCryptography(repository);
        secret = cryptography.getSecret();

        System.out.println("Starting to re-write img files without canvas properties. Please wait...");
        RebuildImgService rebuildImgService = new RebuildImgService();
        rebuildImgService.writeImgFile();
        System.out.println("Operation completed. Please double check the logs for any errors.");
    }

    /**
     * Rewrites img files without canvas properties.
     *
     */
    public void writeImgFile() {
        try {
            Files.walkFileTree(Path.of(outputPath), new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toString().endsWith(".json")) {
                        return FileVisitResult.CONTINUE;
                    }
                    WzImgFileWriter image = new WzImgFileWriter(configuration, secret);
                    image.parse(file);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.error("An error has occurred while walking the file tree.", e);
        }
    }
}
