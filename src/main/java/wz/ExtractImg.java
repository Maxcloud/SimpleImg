package wz;

import img.cache.JsonFileToObject;
import img.cache.KeyFileRepository;
import img.cache.DirectoryConfiguration;
import img.cryptography.WzCryptography;
import img.record.Version;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Extracts img files from .wz files and merges them into a single,
 * folder structure.
 *
 */
public class ExtractImg {

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

    public static void main(String[] args) throws IOException, InterruptedException {

        JsonFileToObject<DirectoryConfiguration> fileToObject =
                new JsonFileToObject<>(configFile, DirectoryConfiguration.class);
        DirectoryConfiguration directoryConfiguration = fileToObject.createObjFromFile();

        Path inputDirectory = Path.of(directoryConfiguration.getInput());
        Path outputDirectory = Path.of(directoryConfiguration.getOutput());
        boolean isMergeMode = directoryConfiguration.isMergeFolders();

        KeyFileRepository<Version> repository = new KeyFileRepository<>(Version.class);
        repository.setSecret(directoryConfiguration.getVersion());

        WzCryptography cryptography = new WzCryptography(repository);
        byte[] secret = cryptography.getSecret();

        Predicate<Path> isWzFile = file -> file.toString().endsWith(".wz");
        try (Stream<Path> stream = Files.list(inputDirectory)) {
            processFile(stream, isWzFile, inputDirectory, outputDirectory, secret);
        }

        if (isMergeMode) {
            System.out.println("Extraction Complete. Merging folders now.");
            Thread.sleep(1000);

            MergeFolderContents(outputDirectory);
        } else {
            System.out.println("Extraction Complete.");
        }
    }

    private static void processFile(Stream<Path> stream, Predicate<Path> isWzFile,
                                    Path input, Path output, byte[] secret) {

        System.out.println(input);
        Function<Path, WzFile> nextFile = wzFile -> new WzFile(wzFile, output, secret);
        stream.filter(Files::isRegularFile).filter(isWzFile).forEach(nextFile::apply);
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
