package img.model;

import img.Variant;
import img.cache.JsonFileRepository;
import img.io.ImgSeekableInputStream;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;

@Getter
@Setter
@Slf4j
public class WzImage {

    public void parse(Path path) {
        if (Files.exists(path)) {
            JsonFileRepository cache = new JsonFileRepository(path);
            try (ImgSeekableInputStream stream = new ImgSeekableInputStream(path)) {
                parse("", stream, cache, 0);
                cache.saveToFile();
            } catch (Exception e) {
                log.error("An error occurred when parsing {}.", path.getFileName(), e);
            }
        } else {
            log.error("The file {} doesn't exist.", path.getFileName());
        }
    }

    private void parse(String filePath, ImgSeekableInputStream stream, JsonFileRepository cache, long offset) {
        String name = stream.decodeStringBlock(stream.readByte());

        switch (name) {
            case "Property":
                stream.skip(1);
                stream.skip(1);
                int children = stream.decodeInt();
                for (int i = 0; i < children; i++) {
                    parse(filePath, stream, cache);
                }
                break;
            case "Canvas":
            case "Shape2D#Convex2D":
            case "Shape2D#Vector2D":
            case "Sound_DX8":
            case "UOL":
                stream.seek(offset); break;
            default:
                log.warn("An unhandled property has been found: {}", name);
                break;
        }
    }

    private void parse(String filePath, ImgSeekableInputStream stream, JsonFileRepository cache) {
        byte type = stream.readByte();
        String name = stream.decodeStringBlock(type);

        filePath = filePath.isEmpty() ? name : filePath + "/" + name;

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
                byte stringType = stream.readByte();
                String decryptedString = stream.decodeStringBlock(stringType);
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
