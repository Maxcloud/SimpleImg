package img;

import img.cache.DirectoryConfiguration;
import img.io.ImgSeekableInputStream;
import img.io.ImgWritableOutputStream;
import img.model.WzImgFile;
import img.util.StringWriter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class WzImgFileWriter {

    private final DirectoryConfiguration configuration;
    private final byte[] secret;

    public WzImgFileWriter(DirectoryConfiguration configuration, byte[] secret) {
        this.configuration = configuration;
        this.secret = secret;
    }

    static Logger log = LoggerFactory.getLogger(WzImgFileWriter.class);

    public void parse(Path inputFileName) throws IOException {
        ByteBuf byteBuf = Unpooled.buffer();

        if (!Files.exists(inputFileName)) {
            log.error("The file {} doesn't exist.", inputFileName.getFileName());
            return;
        }

        Path inputRoot = Path.of(configuration.getOutput());
        Path outputRoot = Path.of(configuration.getNewOutput());
        Path inputFilePath = inputRoot.relativize(inputFileName);
        Path outputFilePath = outputRoot.resolve(inputFilePath);

        Files.createDirectories(outputFilePath.getParent());

        Path outputFileName = outputFilePath.getFileName();

        WzImgFile archive = new WzImgFile();
        try (ImgSeekableInputStream stream = new ImgSeekableInputStream(outputFileName, inputFileName, secret)) {
            archive.parse(stream);
        } catch (Exception e) {
            log.error("An error occurred when parsing {}.", inputFileName.getFileName(), e);
        }

        StringWriter stringWriterPool = new StringWriter();
        try (ImgWritableOutputStream output = new ImgWritableOutputStream(byteBuf, secret)) {
            archive.write(stringWriterPool, "Property", output);

            Files.write(outputFilePath, ByteBufUtil.getBytes(byteBuf));
        } catch (Exception e) {
            log.error("An error occurred when writing to the output stream for {}.", inputFileName.getFileName(), e);
        }

    }


}

