package img;

import img.crypto.WzString;
import img.crypto.WzStringRegistry;
import img.io.ImgInputStream;
import img.io.ImgWritableOutputStream;
import wz.property.WzPropertyList;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class WzImgFileWriter {

    private final Path inPath;
    private final Path outPath;
    private final short version;
    private final byte[] secret;
    private WzPropertyList property;

    public WzImgFileWriter(Path inPath, Path outPath, short version, byte[] secret) {
        this.inPath = inPath;
        this.outPath = outPath;
        this.version = version;
        this.secret = secret;
    }

    static Logger log = LoggerFactory.getLogger(WzImgFileWriter.class);

    public void parse(Path inputFileName) throws IOException {
        ByteBuf byteBuf = Unpooled.buffer();

        if (!Files.exists(inputFileName)) {
            log.error("The file {} doesn't exist.", inputFileName.getFileName());
            return;
        }

        Path inputFilePath = inPath.relativize(inputFileName);
        Path outputFilePath = outPath.resolve(inputFilePath);

        Files.createDirectories(outputFilePath.getParent());

        WzString str = new WzString(version, secret);
        WzStringRegistry registry = str.getRegistry();

        try (var in = new ImgInputStream(inputFileName, str, secret)) {
            registry.deserialize(in);

            this.property = new WzPropertyList();
            this.property.read(registry, in);
        } catch (Exception e) {
            log.error("An error occurred when parsing {}.", inputFileName.getFileName(), e);
        }

        try (var out = new ImgWritableOutputStream(byteBuf, secret)) {
            registry.serialize(out, "Property");
            property.write(registry, "Property", out);

            byte[] bytes = ByteBufUtil.getBytes(byteBuf);
            Files.write(outputFilePath, bytes);
        } catch (Exception e) {
            log.error("An error occurred when writing to the output stream for {}.", inputFileName.getFileName(), e);
        }
    }
}

