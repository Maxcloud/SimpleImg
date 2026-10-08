package img.service;

import img.ext.MoveFileVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wz.WzFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.regex.Pattern;

public class ExtractImgService {
    private final Logger logger = LoggerFactory.getLogger(ExtractImgService.class);

    private static final List<String> BASE_NAMES = List.of(
        "Character",
        "Effect",
        "Etc",
        "Item",
        "Map",
        "Mob",
        "Npc",
        "Skill",
        "Sound"
    );

    private final Path target;
    private final short version;
    private final byte[] secret;

    public ExtractImgService(Path target, short version, byte[] secret) {
        this.target = target;
        this.version = version;
        this.secret = secret;
    }

    public void readWzDirectory(Path oPath) {
        try (var stream = Files.list(oPath)) {
            stream.filter(this::fnExcluded).forEach(this::walkFileTree);
        } catch (Exception e) {
            logger.error("Error reading files: {}", e.getMessage());
        }
    }

    private boolean fnExcluded(Path oPath) {
        String sName = oPath.getFileName().toString();
        return sName.endsWith(".wz");
    }

    private void walkFileTree(Path oPath) {
        boolean isDirectory     = Files.isDirectory(oPath);
        boolean isRegularFile   = Files.isRegularFile(oPath);

        if (isDirectory) {
            readWzDirectory(oPath);
        } else if (isRegularFile) {
            try {
                new WzFile(oPath, target, version, secret);
            } catch (Exception e) {
                logger.error("An error occurred when processing {}.", oPath.getFileName(), e);
            }
        }
    }

    public void fnMoveFiles(Path outputPath) throws IOException {
        for (String file : BASE_NAMES) {
            var target = outputPath.resolve(file + ".wz");

            if (!Files.exists(target)) {
                Files.createDirectories(target);
            }

            String quote = Pattern.quote(file);
            Pattern pattern = Pattern.compile(quote + "\\d+\\.wz");

            try (var stream = Files.newDirectoryStream(outputPath)) {
                for (Path entry : stream) {
                    var path = entry.getFileName().toString();
                    boolean matches = pattern.matcher(path).matches();
                    if (Files.isDirectory(entry) && matches) {
                        // bound per folder: move entry's contents into the merged target
                        Files.walkFileTree(entry, new MoveFileVisitor(entry, target));
                    }
                }
            }
        }

        logger.warn("Successfully merged all files and folders.");
    }
}
