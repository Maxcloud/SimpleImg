package img.service;

import img.ext.ExtractFileVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

/**
 * SimpleImg is a utility class that traverses a directory structure,
 * parsing WzImage files and dumping their string data to JSON format.
 *
 */
public class ExtractJsonService {
    private final Logger logger = LoggerFactory.getLogger(ExtractJsonService.class);

    private final ExtractFileVisitor extractFileVisitor;

    public ExtractJsonService(short version, byte[] secret, List<String> strings) {
        this.extractFileVisitor = new ExtractFileVisitor(version, secret, strings);
    }

    public void dump(String outPath) {
        try {
            Files.walkFileTree(Path.of(outPath), this.extractFileVisitor);
        } catch (IOException e) {
            logger.error("An error has occurred while walking the file tree.", e);
        }
    }
}


