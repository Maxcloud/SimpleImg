package img.ext;

import img.WzImage;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class ExtractFileVisitor extends FileTreeVisitor {

    private final short version;
    private final byte[] secret;
    private final List<String> llist;

    public ExtractFileVisitor(short version, byte[] secret, List<String> lList) {
        this.version = version;
        this.secret = secret;
        this.llist = lList;
    }

    @Override
    protected FileVisitResult handleFile(Path file,
                                         BasicFileAttributes attrs) throws IOException {
        if (file.toString().endsWith(".json")) {
            return FileVisitResult.CONTINUE;
        }
        WzImage image = new WzImage(secret, version, llist);
        image.parse(file);
        return FileVisitResult.CONTINUE;
    }
}
