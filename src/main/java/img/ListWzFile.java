package img;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListWzFile {

    private final Logger log = LoggerFactory.getLogger(ListWzFile.class);

    private List<String> entries = new ArrayList<>();

    public ListWzFile(Path path) {
        read(path);
    }

    /**
     * Reads the List.wz file from the specified path and populates the entries list.
     *
     * @param path the path to the List.wz file
     */
    private void read(Path path) {

        WzConfiguration config = new WzConfiguration();
        try {
            byte[] fileBytes = Files.readAllBytes(path);
            ByteBuffer buf = ByteBuffer.wrap(fileBytes).order(ByteOrder.LITTLE_ENDIAN);

            byte[] secret = config.getSecret();
            while (buf.remaining() > 0) {
                int len = buf.getInt();

                byte[] ret = new byte[len * 2];
                for (int i = 0; i < ret.length; i += 2) {
                    byte low = buf.get();
                    byte high = buf.get();

                    ret[i] = (byte) (low ^ secret[i]);
                    ret[i + 1] = (byte) (high ^ secret[i + 1]);
                }
                buf.getShort(); // null-terminator

                final String value = new String(ret, StandardCharsets.UTF_16LE);

                Path oPath = Path.of(value);
                String sFileName = oPath.getFileName().toString();
                // System.out.println(sFileName);
                entries.add(sFileName);
            }

        } catch (IOException e) {
            log.error("An error occurred when reading List.wz file.", e);
        }
        entries = Collections.unmodifiableList(entries);
    }

    /**
     * Gets the list of entries read from the List.wz file.
     *
     * @return the list of entries
     */
    public List<String> getEntries() {
        return entries;
    }

}