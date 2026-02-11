package img.model.image;

import img.ListWzFile;
import img.crypto.WzStringHandler;
import img.io.impl.ImgInputStream;
import img.util.Variant;
import img.io.repository.JsonFileRepository;
import img.model.common.WzImgCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class WzImage {

    Logger log = LoggerFactory.getLogger(WzImage.class);

    private final int version;
    private final byte[] secret;

    public WzImage(int version, byte[] secret) {
        this.version = version;
        this.secret = secret;
    }

    public void parse(Path path) {
        if (!Files.exists(path)) {
            log.error("The file {} doesn't exist.", path.getFileName()); return;
        }

        JsonFileRepository<WzImgCache> repository =
                new JsonFileRepository<>(path, WzImgCache.class);

        ListWzFile listWzFile = new ListWzFile(Path.of("E:\\MapleStory_55\\Maplestory\\List.wz"));
        List<String> listWzFileNames = listWzFile.getEntries();

        WzStringHandler handle = new WzStringHandler(version, secret);
        handle.setModernImgFiles(listWzFileNames);

        try (ImgInputStream stream = new ImgInputStream(path, handle, secret)) {
            parse("", path, stream, repository, 0);
            repository.saveAsJson();
        } catch (Exception e) {
            log.error("An error occurred when parsing {}.", path.getFileName(), e);
        }
    }

    private void parse(String filePath, Path path, ImgInputStream stream,
                       JsonFileRepository<WzImgCache> cache, long offset) {

        long position = stream.getPosition();

        String name = stream.getHandle().getCodec().deserialize(stream);
        switch (name) {
            case "Property":
                stream.readByte();
                stream.readByte();
                int children = stream.decodeInt();
                for (int i = 0; i < children; i++) {
                    parse(filePath, path, stream, cache);
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
                stream.seek(offset);
                log.warn("An unhandled property has been found: {}", name);
                break;
        }
    }

    /**
     * <a href="https://learn.microsoft.com/en-us/windows/win32/api/oaidl/ns-oaidl-variant">...</a>
     * Handles the parsing of different property variants.
     *
     */
    private void parse(String filePath, Path path, ImgInputStream stream, JsonFileRepository<WzImgCache> cache) {
        String name = stream.getHandle().getCodec().deserialize(stream);

        filePath = filePath.isEmpty() ? name : filePath + "/" + name;
        // System.out.println("Parsing property: " + filePath);

        long offset = stream.getPosition();
        cache.setNameToOffset(filePath, offset);

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
                String decryptedString = stream.getHandle().getCodec().deserialize(stream);
                cache.setOffsetAndName(offset, decryptedString);
                break;
            case VT_DISPATCH:
                long endOfBytes = stream.readInt() + stream.getPosition();
                parse(filePath, path, stream, cache, endOfBytes);
                break;
            default:
                log.warn("There was a missing property found for  {} ", filePath);
                break;
        }
    }

}
