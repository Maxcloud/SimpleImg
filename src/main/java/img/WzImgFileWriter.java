package img;

import img.configuration.DirectoryConfiguration;
import img.crypto.WzStringCodec;
import img.crypto.WzStringHandler;
import img.io.impl.ImgInputStream;
import img.io.impl.ImgWritableOutputStream;
import img.model.common.WzImgFile;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class WzImgFileWriter {

    private final WzConfiguration configuration;
    private final byte[] secret;

    public WzImgFileWriter(WzConfiguration configuration) {
        this.configuration = configuration;
        secret = configuration.getSecret();
    }

    static Logger log = LoggerFactory.getLogger(WzImgFileWriter.class);

    public void parse(Path inputFileName) throws IOException {
        ByteBuf byteBuf = Unpooled.buffer();

        if (!Files.exists(inputFileName)) {
            log.error("The file {} doesn't exist.", inputFileName.getFileName());
            return;
        }

        DirectoryConfiguration directories = configuration.getDirectories();

        Path inputFileRoot = Path.of(configuration.getOutput());
        Path outputFileRoot = Path.of(directories.getNewOutput());
        Path inputFilePath = inputFileRoot.relativize(inputFileName);
        Path outputFilePath = outputFileRoot.resolve(inputFilePath);

        Files.createDirectories(outputFilePath.getParent());

        Path outputFileName = outputFilePath.getFileName();
        int version = configuration.getVersion();

        WzStringHandler handler = new WzStringHandler(version, secret);
        WzStringCodec stringCodec = handler.getCodec();

        WzImgFile imgFile = new WzImgFile(stringCodec);
        try (ImgInputStream stream = new ImgInputStream(inputFileName, handler, secret)) {
            imgFile.parse(stream);
        } catch (Exception e) {
            log.error("An error occurred when parsing {}.", inputFileName.getFileName(), e);
        }

        try (ImgWritableOutputStream output = new ImgWritableOutputStream(byteBuf, secret)) {
            imgFile.write("Property", output);

            byte[] bytes = ByteBufUtil.getBytes(byteBuf);
            Files.write(outputFilePath, bytes);
        } catch (Exception e) {
            log.error("An error occurred when writing to the output stream for {}.", inputFileName.getFileName(), e);
        }
    }
}

