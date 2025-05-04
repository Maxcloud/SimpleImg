package img;

import img.model.WzImage;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ExecutorService;

@Slf4j
public class ImgFileVisitor extends SimpleFileVisitor<Path> {

    private final ExecutorService service;

    public ImgFileVisitor(ExecutorService service) {
        this.service = service;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        if (file.toString().endsWith(".json")) {
            return FileVisitResult.CONTINUE;
        }

        service.submit(() -> {
            WzImage image = new WzImage();
            image.parse(file);
        });

        return FileVisitResult.CONTINUE;
    }

}

