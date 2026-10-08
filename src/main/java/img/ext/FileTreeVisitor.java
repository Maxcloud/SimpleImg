package img.ext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public abstract class FileTreeVisitor extends SimpleFileVisitor<Path> {
    Logger logger = LoggerFactory.getLogger(FileTreeVisitor.class);

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        return handleFile(file, attrs);
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return handlePostDirectory(dir, exc);
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        logger.error("Failed to visit file: {}", file, exc);
        return FileVisitResult.CONTINUE;
    }

    /** Required: what to do with each file. */
    protected abstract FileVisitResult handleFile(Path file, BasicFileAttributes attrs) throws IOException;

    /** Optional: default is to do nothing (super behaviour). */
    protected FileVisitResult handlePostDirectory(Path directory, IOException exc) throws IOException {
        return super.postVisitDirectory(directory, exc);
    }
}
