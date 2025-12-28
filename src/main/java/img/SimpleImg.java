package img;

import img.model.WzImage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;

/**
 * SimpleImg is a utility class that traverses a directory structure,
 * parsing WzImage files and dumping their string data to JSON format.
 *
 */
@Slf4j
public class SimpleImg {

    private static final Properties config = Config.getInstance().getProperties();

    public static void main(String[] args) {
        String wzFilePath = config.getProperty("config.re_output_directory");
        Path root = Path.of(wzFilePath);

        System.out.println("Starting to dump strings to JSON. Please wait...");
        SimpleImg simpleImg = new SimpleImg();
        simpleImg.dumpStringsToJson(root);
        System.out.println("Dumping strings to JSON completed. Please double check the logs for any errors.");
    }

    public void dumpStringsToJson(Path root) {
        try {
            Files.walkFileTree(root, new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toString().endsWith(".json")) {
                        return FileVisitResult.CONTINUE;
                    }
                    WzImage image = new WzImage();
                    image.parse(file);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.error("An error has occurred while walking the file tree.", e);
        }
    }
}


