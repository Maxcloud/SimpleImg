package wz;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import img.crypto.WzCryptography;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListWzFile {

    private Logger log = LoggerFactory.getLogger(ListWzFile.class);

    private final Path configFile = Path.of("src/main/resources/configuration");

    private static final byte[] AES_KEY = {
            0x13, 0x00, 0x00, 0x00,
            0x08, 0x00, 0x00, 0x00,
            0x06, 0x00, 0x00, 0x00,
            (byte) 0xB4, 0x00, 0x00, 0x00,
            0x1B, 0x00, 0x00, 0x00,
            0x0F, 0x00, 0x00, 0x00,
            0x33, 0x00, 0x00, 0x00,
            0x52, 0x00, 0x00, 0x00
    };

    private static final byte[] IV = {(byte) 0xB9, 0x7D, 0x63, (byte) 0xE9}; // maple sea

    private final WzCryptography cipher = new WzCryptography(IV, 0);
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
        try {
            byte[] fileBytes = Files.readAllBytes(path);
            ByteBuffer buf = ByteBuffer.wrap(fileBytes).order(ByteOrder.LITTLE_ENDIAN);

            byte[] secret = cipher.getSecret();

            byte mask = (byte) 0xAA;
            while (buf.remaining() > 0) {
                int len = buf.getInt();

                byte[] chunk = new byte[len * 2];
                for (int i = 0; i < chunk.length; i++, mask++) {
                    byte cipherByte = buf.get();
                    byte keyByte = (byte) (secret[i % secret.length] ^ mask);
                    chunk[i] = (byte) (cipherByte ^ keyByte);
                }
                buf.getShort(); // null-terminator

                final String value = new String(chunk, StandardCharsets.UTF_16LE);

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

    public boolean isModernImgFile(String fileName) {
        return entries.contains(fileName);
    }

}