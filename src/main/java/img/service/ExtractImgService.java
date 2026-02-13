package img.service;

import img.EnvironmentConfig;
import img.WzConfiguration;
import img.io.deserialize.JsonFileToObject;
import img.io.repository.KeyFileRepository;
import img.configuration.DirectoryConfiguration;
import img.crypto.WzCryptography;
import img.model.common.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wz.WzFile;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Extracts img files from .wz files and merges them into a single,
 * folder structure.
 *
 */
public class ExtractImgService {

    private final Logger log = LoggerFactory.getLogger(ExtractImgService.class);

    private static final Path configFile = Path.of("src/main/resources/configuration.json");

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

    private static Path outputDirectory;
    private static int version;
    private static byte[] secret;

    public static void main(String[] args) throws IOException, InterruptedException {

        WzConfiguration configuration = new WzConfiguration();
        EnvironmentConfig environment = configuration.getEnvironment();

        String path = environment.get("simple.img.input");
        Path input = Path.of(path);

        outputDirectory = Path.of(environment.get("simple.img.output"));
        boolean wants_to_merge_folders = Boolean.parseBoolean(environment.get("simple.img.merge"));

        secret = configuration.getSecret();
        version = environment.getInt("simple.img.version");

        ExtractImgService service = new ExtractImgService();
        service.readWzDirectory(input);

        if (wants_to_merge_folders) {
            System.out.println("Extraction Complete. Merging folders now.");
            Thread.sleep(1000);

            service.MergeFolderContents(outputDirectory);
        } else {
            System.out.println("Extraction Complete.");
        }
    }

    public void readWzDirectory(Path oPath) {
        try (var stream = Files.list(oPath)) {
            stream.filter(this::fnExcluded).forEach(this::walkFileTree);
        } catch (Exception e) {
            log.error("Error reading files: {}", e.getMessage());
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
                new WzFile(oPath, outputDirectory, version, secret);
            } catch (Exception e) {
                log.error("An error occurred when processing {}.", oPath.getFileName(), e);
            }
        }
    }

    private void MergeFolderContents(Path outputPath) throws IOException {
        for (String folderName : BASE_NAMES) {
            Path target = outputPath.resolve(folderName + ".wz");

            if (!Files.exists(target)) {
                Files.createDirectories(target);
            }

            String quote = Pattern.quote(folderName);
            Pattern numberedFolderPattern = Pattern.compile(quote + "\\d+\\.wz");

            try (var stream = Files.newDirectoryStream(outputPath)) {
                for (Path entry : stream) {
                    String path = entry.getFileName().toString();
                    if (Files.isDirectory(entry) && numberedFolderPattern.matcher(path).matches()) {
                        MoveFolderContents(entry, target);
                    }
                }
            }
        }

        System.out.println("Successfully merged all files and folders.");
    }

    private void MoveFolderContents(Path source, Path target) throws IOException {

        SimpleFileVisitor<Path> visitor = new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult visitFile(Path file,
                                             BasicFileAttributes attributes) throws IOException {

                Path destination = target.resolve(source.relativize(file));
                Files.createDirectories(destination.getParent());
                Files.move(file, destination, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path directory,
                                                      IOException exception) throws IOException {

                Files.delete(directory);
                return FileVisitResult.CONTINUE;
            }
        };

        Files.walkFileTree(source, visitor);
    }

}
