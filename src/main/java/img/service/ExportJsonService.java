package img.service;

import img.EnvironmentConfig;
import img.ListWzFile;
import img.WzConfiguration;
import img.crypto.WzStringHandler;
import img.model.image.WzImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

/**
 * SimpleImg is a utility class that traverses a directory structure,
 * parsing WzImage files and dumping their string data to JSON format.
 *
 */
public class ExportJsonService {

    Logger log = LoggerFactory.getLogger(ExportJsonService.class);

    public static void main(String[] args) {

        WzConfiguration configuration = new WzConfiguration();
        EnvironmentConfig environment = configuration.getEnvironment();

        Path outputDirectory = Path.of(environment.get("simple.img.output"));

        int version = environment.getInt("simple.img.version");
        byte[] secret = configuration.getSecret();

        System.out.println("Starting to dump strings to JSON. Please wait, this might take awhile...");
        ExportJsonService exportJsonService = new ExportJsonService();
        exportJsonService.dumpStringsToJson(outputDirectory, version, secret);
        System.out.println("Dumping strings to JSON completed. Please double check the logs for any errors.");
    }

    public void dumpStringsToJson(Path root, int version, byte[] secret) {
        WzImage wzImage = new WzImage(version, secret);

        ListWzFile listWzFile = new ListWzFile(Path.of("E:\\MapleStory_83\\Maplestory\\List.wz"));
        List<String> listWzFileNames = listWzFile.getEntries();

        WzStringHandler handle = new WzStringHandler(version, secret);
        handle.setModernImgFiles(listWzFileNames);

        try {
            Files.walkFileTree(root, new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toString().endsWith(".json")) {
                        return FileVisitResult.CONTINUE;
                    }
                    wzImage.parse(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    log.error("Failed to visit file: {}", file, exc);
                    return FileVisitResult.CONTINUE;
                }

            });

        } catch (IOException e) {
            log.error("An error has occurred while walking the file tree.", e);
        }
    }
}


