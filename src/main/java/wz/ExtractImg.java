package wz;

import img.Config;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Extracts img files from .wz files and merges them into a single,
 * folder structure.
 *
 */
public class ExtractImg {

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

    public static void main(String[] args) throws IOException, InterruptedException {
        Properties config   = Config.getInstance().getProperties();

        String fileInput    = config.getProperty("config.input_directory");
        String fileOutput   = config.getProperty("config.output_directory");
        String fileMerge    = config.getProperty("config.merge_folders", "false");

        Predicate<Path> isWzFile = file -> file.toString().endsWith(".wz");
        try (Stream<Path> stream = Files.list(Path.of(fileInput))) {
            stream.filter(Files::isRegularFile).filter(isWzFile).forEach(WzFile::new);
        }

        boolean isMergeMode = fileMerge.equalsIgnoreCase("true");
        if (isMergeMode) {
            System.out.println("Extraction Complete. Merging folders now.");
            Thread.sleep(1000);

            MergeFolderContents(Path.of(fileOutput));
        } else {
            System.out.println("Extraction Complete.");
        }
    }

    private static void MergeFolderContents(Path outputPath) throws IOException {
        for (String folderName : BASE_NAMES) {
            Path target = outputPath.resolve(folderName + ".wz");

            if (!Files.exists(target)) {
                Files.createDirectories(target);
            }

            Pattern numberedFolderPattern = Pattern.compile(Pattern.quote(folderName) + "\\d+\\.wz");

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(outputPath)) {
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

    private static void MoveFolderContents(Path source, Path target) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<>() {
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
        });
    }

}
