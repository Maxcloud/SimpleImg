package wz;

import img.ListWzFile;
import img.crypto.WzStringHandler;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WzFile {

    Logger log = LoggerFactory.getLogger(WzFile.class);

    private ExecutorService service;

    private final Path imgInputPath;
    private final Path imgOutputPath;
    private final int version;
    private final byte[] secret;
    private final WzDirectory root;

    public WzFile(Path imgInputPath, Path imgOutputPath, int version, byte[] secret) {
        this.imgInputPath = imgInputPath;
        this.imgOutputPath = imgOutputPath;
        this.version = version;
        this.secret = secret;
        this.root = new WzDirectory("");
        init();
    }

    public WzDirectory getRoot() {
        return root;
    }

    private void init() {
        Path target = imgOutputPath.resolve(imgInputPath.getFileName());

        service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        boolean isTerminated;

        ListWzFile listWzFile = new ListWzFile(Path.of("E:\\MapleStory_83\\Maplestory\\List.wz"));
        List<String> entries = listWzFile.getEntries();

        WzStringHandler handle = new WzStringHandler(version, secret);
        handle.setModernImgFiles(entries);

        try (WzSeekableInputStream stream = new WzSeekableInputStream(imgInputPath, handle, secret)) {
            parseImg(stream, target, getRoot());
        } catch (Exception e) {
            System.out.println("An issue occurred while parsing. " + e);
        } finally {
            if (service != null) {
                service.shutdown();
                try {
                    isTerminated = service.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
                    if (isTerminated) {
                        System.out.println("Decompressed " + imgInputPath.getFileName());
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

    private void parseImg(WzSeekableInputStream stream, Path target, WzDirectory directory){

        try {
            if (!Files.exists(target)) {
                Files.createDirectories(target);
            }

            ByteBuf buf = stream.getByteBuf();
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
                parseImg(stream, target, dir);
            }
        } catch (Exception e) {
            System.out.println("An issue occurred with an exception. " + e);
        }

    }

}
