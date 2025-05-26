package img;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SimpleImg {

    private static ExecutorService service;
    private static final Properties config = Config.getInstance().getProperties();

    public static void main(String[] args) {
        String wzFilePath = config.getProperty("config.output_directory");
        Path root = Path.of(wzFilePath);

        System.out.println("Starting to dump strings to JSON. Please wait...");
        SimpleImg simpleImg = new SimpleImg();
        simpleImg.dumpStringsToJson(root);
        System.out.println("Dumping strings to JSON completed. Please double check the logs for any errors.");
    }

    public void dumpStringsToJson(Path root) {
        try {
            service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            Files.walkFileTree(root, new ImgFileVisitor(service));
        } catch (IOException e) {
            log.error("An error has occurred while walking the file tree.", e);
        } finally {
            service.shutdown();
            try {
                if (!service.awaitTermination(180L, TimeUnit.SECONDS))
                    service.shutdownNow();
            } catch (InterruptedException e) {
                service.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

}
