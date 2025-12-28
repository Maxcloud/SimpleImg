package img;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;

/**
 * SimpleImgNoCanvas is a utility class that traverses a directory,
 * structure and processes each img file, and writes the output
 * without canvas properties.
 */
@Slf4j
public class SimpleImgNoCanvas {

    private static final Properties config = Config.getInstance().getProperties();

    /**
     * Main method to start the re-dumping process of img files without canvas properties.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        String wzFileInPath = config.getProperty("config.output_directory");
        String wzFileOutPath = config.getProperty("config.re_output_directory");
        Path inputRoot = Path.of(wzFileInPath);
        Path outputRoot = Path.of(wzFileOutPath);

        System.out.println("Starting to re-write img files without canvas properties. Please wait...");
        SimpleImgNoCanvas simpleImgNoCanvas = new SimpleImgNoCanvas();
        simpleImgNoCanvas.rewriteImgFileWithNoCanvas(inputRoot, outputRoot);
        System.out.println("Operation completed. Please double check the logs for any errors.");
    }

    /**
     * Rewrites img files without canvas properties.
     *
     * @param inputRoot  the root directory of the input files
     * @param outputRoot the root directory for the output files
     */
    public void rewriteImgFileWithNoCanvas(Path inputRoot, Path outputRoot) {
        try {
            Files.walkFileTree(inputRoot, new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toString().endsWith(".json")) {
                        return FileVisitResult.CONTINUE;
                    }
                    WzImgFileWriter image = new WzImgFileWriter(inputRoot, outputRoot);
                    image.parse(file);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.error("An error has occurred while walking the file tree.", e);
        }
    }
}
