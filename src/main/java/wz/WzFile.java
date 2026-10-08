package wz;

import img.crypto.WzString;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WzFile {
    private final Logger logger = LoggerFactory.getLogger(WzFile.class);

    private ExecutorService service;

    private final Path inPath;
    private final Path outPath;
    private final int version;
    private final byte[] secret;
    @Getter
    private final WzDirectory root;

    public WzFile(Path inPath, Path outPath, int version, byte[] secret) {
        this.inPath = inPath;
        this.outPath = outPath;
        this.version = version;
        this.secret = secret;
        this.root = new WzDirectory("");
        init();
    }

    private void init() {
        var target = outPath.resolve(inPath.getFileName());

        service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        boolean isTerminated;

        var entries = readListWz(inPath.resolveSibling("List.wz"));
        var handle = new WzString(version, secret);
        handle.setListFiles(entries);

        try (var stream = new WzSeekableInputStream(inPath, handle, secret, version)) {
            parseImg(stream, target, getRoot());
        } catch (Exception e) {
            logger.error("An issue occurred while parsing. {}", e.getMessage());
        } finally {
            if (service != null) {
                service.shutdown();
                try {
                    isTerminated = service.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
                    if (isTerminated) {
                        System.out.println("Decompressed " + inPath.getFileName());
                    } else {
                        service.shutdownNow();
                        logger.error("An operation failed and the service was shut down");
                    }
                } catch (Exception e) {
                    service.shutdownNow();
                    Thread.currentThread().interrupt();
                    logger.error("Thread was interrupted. ", e);
                }
            }
        }
    }

    /**
     * Reads the img file names from List.wz; a missing or unreadable list
     * must not abort extraction, so anything but success returns an empty list.
     */
    private List<String> readListWz(Path listWz) {
        if (!Files.isRegularFile(listWz)) {
            return List.of();
        }
        try {
            return new ListWzFile(listWz, secret).entries();
        } catch (Exception e) {
            logger.warn("Could not read {}. Strings in list-encoded files may not decode.", listWz, e);
            return List.of();
        }
    }

    private void parseImg(WzSeekableInputStream stream, Path target, WzDirectory directory){

        try {
            if (!Files.exists(target)) {
                Files.createDirectories(target);
            }

            ByteBuf buf = stream.getReadBuf();
            int entries = stream.decodeInt();
            for (int i = 0; i < entries; i++) {
                byte type = stream.readByte();
                if (type == 2 || type == 4) {
                    WzDataEntry wzDataEntry = new WzDataEntry(type, stream, directory);

                    int offset = wzDataEntry.getOffset();
                    int len = wzDataEntry.getSize();

                    ByteBuf slice = buf.slice(offset, len).retain();
                    service.submit(() -> {
                        int bytes = 0;
                        try {
                            Path output = target.resolve(directory.getName()).resolve(wzDataEntry.getName());
                            try (FileOutputStream fos = new FileOutputStream(output.toFile())) {
                                FileChannel channel = fos.getChannel();
                                bytes = channel.write(slice.nioBuffer());
                            } catch (Exception e) {
                                logger.error("An error occurred in the service, attempting to write the file.", e);
                            }
                            slice.release();
                        } catch (Exception e) {
                            logger.error("An error occurred in the service.", e);
                        }
                    });

                    directory.addFile(wzDataEntry);
                } else if (type == 3) {
                    WzDirectory wz_directory = new WzDirectory(type, stream, directory);

                    Path path = target.resolve(wz_directory.getName());
                    if (!Files.exists(path)) {
                        Files.createDirectories(path);
                    }

                    directory.addDirectory(wz_directory);
                }
            }

            for (WzDirectory dir : directory.getSubdirectories()) {
                parseImg(stream, target, dir);
            }
        } catch (Exception e) {
            logger.error("An issue occurred with an exception. {}", String.valueOf(e));
        }

    }

}
