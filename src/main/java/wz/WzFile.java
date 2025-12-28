package wz;

import img.Config;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Getter
@Slf4j
public class WzFile {

    private ExecutorService service;
    private final Properties config = Config.getInstance().getProperties();

    private final Path path;
    private final WzDirectory root;

    public WzFile(Path path) {
        this.path = path;
        this.root = new WzDirectory("");
        init();
    }

    private void init() {
        service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        boolean isTerminated;
        try (WzSeekableInputStream stream = new WzSeekableInputStream(this.path)) {
            parseImg(stream, getRoot());
        } catch (Exception e) {
            System.out.println("An issue occurred. " + e);
        } finally {
            if (service != null) {
                service.shutdown();
                try {
                    isTerminated = service.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
                    if (isTerminated) {
                        System.out.println("Decompressed " + path.getFileName());
                    } else {
                        service.shutdownNow();
                        log.error("An operation failed and the service was shut down");
                    }
                } catch (Exception e) {
                    service.shutdownNow();
                    Thread.currentThread().interrupt();
                    log.error("Thread was interrupted. ", e);
                }
            }
        }
    }

    private void parseImg(WzSeekableInputStream stream, WzDirectory directory) throws IOException {
        ByteBuf buf = stream.getByteBuf();

        String imgFilePath = config.getProperty("config.output_directory");
        Path target = Path.of(imgFilePath).resolve(stream.getPath().getFileName());

        if (!Files.exists(target)) {
            Files.createDirectories(target);
        }

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
                            log.error("An error occurred in the service, attempting to write the file.", e);
                        }
                        slice.release();
                        // System.out.println("Writing " + file_path + "\\" + wzDataEntry.getName());
                        // log.warn("File: ({}), Property: ({}/{}), Size: ({} k/b)", wzDataEntry.getName(),
                        //        count.getAndIncrement(), entries, bytes / 1024);
                    } catch (Exception e) {
                        log.error("An error occurred in the service.", e);
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
            parseImg(stream, dir);
        }

    }

}
