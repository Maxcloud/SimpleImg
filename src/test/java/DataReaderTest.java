import img.cache.StringCache;
import img.io.ImgSeekableInputStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

@Slf4j
public class DataReaderTest {

    private long loadJsonToStringCacheAndMeasureTime(String filePath) {
        long start = System.nanoTime();

        File file = new File(filePath + ".json");
        assumeTrue("JSON file doesn't exist, skipping test.", filePath != null);

        StringCache cache = new StringCache(file);
        long end = System.nanoTime();
        return end - start;
    }

    @Test
    public void TestJsonLoadingTime() {
        String path = System.getProperty("wz.path");
        assumeTrue("Path is null, skipping test.", path != null);

        Path filePath = Paths.get(path, "Etc.wz", "Commodity.img");

        long time = loadJsonToStringCacheAndMeasureTime(filePath + ".json");

        assertTrue("JSON loading took too long.", time < 100_000_000);
    }

    @Test
    public void TestOperationComplete() {
        long start = System.nanoTime();

        String path = System.getProperty("wz.path");
        assumeTrue("Path is null, skipping test.", path != null);

        Path filePath = Paths.get(path, "Etc.wz", "Commodity.img");

        File file = new File (filePath + ".json");
        assertNotNull("Skipping test: JSON file does not exist.", file);

        StringCache cache = new StringCache(file);
        try (ImgSeekableInputStream stream = new ImgSeekableInputStream(filePath.toString(), cache)) {

            int enumOfEntries = 14518;

            for (String name : stream.getChildren()) {
                String child = name + "/";
                stream.getInt(child + "Bonus");
                stream.getInt(child + "Count");
                stream.getInt(child + "Gender");
                stream.getInt(child + "ItemId");
                stream.getInt(child + "OnSale");
                stream.getInt(child + "Period");
                stream.getInt(child + "Price");
                stream.getInt(child + "Priority");
                stream.getInt(child + "SN");
            }
        } catch (Exception e) {
            log.error("An error occurred during parsing an image file. " , e);
        }

        long end = System.nanoTime();
        long time = end - start;

        assertTrue("Full operation took too long!", time < 100_000_000);
    }

    @Test
    public void TestOperationWarnUp() {
        long start = System.nanoTime();

        String path = System.getProperty("wz.path");
        assumeTrue("Path is null, skipping test.", path != null);

        Path filePath = Paths.get(path, "Etc.wz", "Commodity.img");

        File file = new File (filePath + ".json");
        assertNotNull("Skipping test: JSON file does not exist.", file);

        StringCache cache = new StringCache(file);
        try (ImgSeekableInputStream stream = new ImgSeekableInputStream(filePath.toString(), cache)) {
            // warm-up phase to help avoid "cold start" issues
            for (int i = 0; i < 10; i++) {
                stream.getInt(i + "/Bonus");
                stream.getInt(i + "/Count");
                stream.getInt(i + "/Gender");
                stream.getInt(i + "/ItemId");
                stream.getInt(i + "/OnSale");
                stream.getInt(i + "/Period");
                stream.getInt(i + "/Price");
                stream.getInt(i + "/Priority");
                stream.getInt(i + "/SN");
            }
        } catch (Exception e) {
            log.error("An error occurred during parsing an image file. " , e);
        }

        long end = System.nanoTime();
        long time = end - start;

        assertTrue("Full operation took too long!", time < 1_000_000_000);
    }

}
