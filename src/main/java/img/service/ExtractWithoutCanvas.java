package img.service;

import img.WzImgFileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class ExtractWithoutCanvas {
    private final Logger logger = LoggerFactory.getLogger(ExtractWithoutCanvas.class);

    private final Path outPath;
    private final Path inPath;
    private final short version;
    private final byte[] secret;

    public ExtractWithoutCanvas(Path outPath, Path inPath, short version, byte[] secret) {
        this.outPath = outPath;
        this.inPath = inPath;
        this.version = version;
        this.secret = secret;
    }

    private boolean fnExcluded(Path output) {
        String sName = output.getFileName().toString();
        return !sName.endsWith(".json");
    }

    private void walkFileTree(Path oPath) {
        if (Files.isDirectory(oPath)) {
            writeImgFile(oPath);
        } else if (Files.isRegularFile(oPath)) {
            try {
                WzImgFileWriter writeImgFile = new WzImgFileWriter(
                        inPath, outPath, version, secret);
                writeImgFile.parse(oPath);
            } catch (Exception e) {
                logger.error("An error occurred when processing {}.", oPath.getFileName(), e);
            }
        }
    }

    public void writeImgFile(Path oPath) {
        try (Stream<Path> stream = Files.list(oPath)) {
            stream.filter(this::fnExcluded).forEach(this::walkFileTree);
        } catch (IOException e) {
            logger.error("An error has occurred while listing files in the output directory.", e);
        }
    }
}
