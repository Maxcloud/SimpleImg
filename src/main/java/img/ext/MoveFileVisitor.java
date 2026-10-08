package img.ext;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

/** Moves everything under a source tree into a target tree, then deletes the (now empty) source tree. */
public class MoveFileVisitor extends FileTreeVisitor {

    private final Path source;
    private final Path target;

    public MoveFileVisitor(Path source, Path target) {
        this.source = source;
        this.target = target;
    }

    public Path getSource() {
        return source;
    }

    public Path getTarget() {
        return target;
    }

    @Override
    protected FileVisitResult handleFile(Path file, BasicFileAttributes attrs) throws IOException {
        Path destination = target.resolve(source.relativize(file));
        Files.createDirectories(destination.getParent());
        Files.move(file, destination, StandardCopyOption.REPLACE_EXISTING);
        return FileVisitResult.CONTINUE;
    }

    @Override
    protected FileVisitResult handlePostDirectory(Path directory, IOException exc) throws IOException {
        Files.delete(directory);
        return FileVisitResult.CONTINUE;
    }
}
