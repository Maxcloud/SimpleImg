package img.model.image;

import img.util.Variant;
import img.io.repository.JsonFileRepository;
import img.io.impl.ImgReadableInputStream;
import img.model.common.WzImgCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

public class WzImage {

    Logger log = LoggerFactory.getLogger(WzImage.class);

    private final byte[] secret;

    public WzImage(byte[] secret) {
        this.secret = secret;
    }

    public void parse(Path path) {
        if (!Files.exists(path)) {
            log.error("The file {} doesn't exist.", path.getFileName()); return;
        }

        JsonFileRepository<WzImgCache> repository =
                new JsonFileRepository<>(path, WzImgCache.class);

        try (ImgReadableInputStream stream = new ImgReadableInputStream(path.getFileName(), path, secret)) {
            parse("", stream, repository, 0);
            repository.saveAsJson();
        } catch (Exception e) {
            log.error("An error occurred when parsing {}.", path.getFileName(), e);
        }
    }

    private void parse(String filePath, ImgReadableInputStream stream, JsonFileRepository<WzImgCache> cache, long offset) {
        long position = stream.getPosition();

        String name = stream.getStringWriter().internalDeserializeString(stream);
        switch (name) {
            case "Property":
                stream.skip(1);
                stream.skip(1);
                int children = stream.decodeInt();
                for (int i = 0; i < children; i++) {
                    parse(filePath, stream, cache);
                }
                break;
            case "Shape2D#Vector2D":
                /*long vector_position = stream.getPosition();
                cache.toOffset(filePath, vector_position);
                stream.decodeInt();
                stream.decodeInt();
                break;*/
            case "UOL":
                /*long uol_position = position - 5; // adjust 5 bytes for the dispatch pointer and the variant type
                stream.skip(1); // variant type
                String uol = stream.getStringWriter().internalDeserializeString(stream);
                cache.getUolToString().put(uol_position, uol);
                break;*/
            case "Canvas":
            case "Shape2D#Convex2D":
            case "Sound_DX8":
                stream.seek(offset);
                break;
            default:
                log.warn("An unhandled property has been found: {}", name);
                break;
        }
    }

    /**
     * <a href="https://learn.microsoft.com/en-us/windows/win32/api/oaidl/ns-oaidl-variant">...</a>
     * Handles the parsing of different property variants.
     * @param filePath
     * @param stream
     * @param cache
     */
    private void parse(String filePath, ImgReadableInputStream stream, JsonFileRepository<WzImgCache> cache) {
        String name = stream.getStringWriter().internalDeserializeString(stream);

        filePath = filePath.isEmpty() ? name : filePath + "/" + name;
        // System.out.println("Parsing property: " + filePath);

        long offset = stream.getPosition();
        cache.toOffset(filePath, offset);

        Variant variant = Variant.values()[stream.readByte()];
        switch (variant) {
            case VT_EMPTY:
                break;
            case VT_I2:
            case VT_BOOL:
                stream.readShort();
                break;
            case VT_I4:
            case VT_UI4:
                stream.decodeInt();
                break;
            case VT_I8:
                stream.decodeLong();
                break;
            case VT_R4:
                stream.decodeFloat();
                break;
            case VT_R8:
                stream.readDouble();
                break;
            case VT_BSTR:
                String decryptedString = stream.getStringWriter().internalDeserializeString(stream);
                cache.toString(offset, decryptedString);
                break;
            case VT_DISPATCH:
                long endOfBytes = stream.readInt() + stream.getPosition();
                parse(filePath, stream, cache, endOfBytes);
                break;
            default:
                log.warn("There was a missing property found for  {} ", filePath);
                break;
        }
    }

}
