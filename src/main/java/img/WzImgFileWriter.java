package img;

import img.io.ImgSeekableInputStream;
import img.io.ImgWritableOutputStream;
import img.model.WzImgFile;
import img.util.StringWriter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public record WzImgFileWriter(Path inputRoot, Path outputRoot) {

    public void parse(Path path) throws IOException {
        ByteBuf byteBuf = Unpooled.buffer();

        if (Files.exists(path)) {
            Path relativePath = inputRoot.relativize(path);
            Path outputFile = outputRoot.resolve(relativePath);

            Files.createDirectories(outputFile.getParent());

            WzImgFile archive = new WzImgFile();
            try (ImgSeekableInputStream stream = new ImgSeekableInputStream(outputFile.getFileName(), path)) {
                archive.parse(stream);
            } catch (Exception e) {
                log.error("An error occurred when parsing {}.", path.getFileName(), e);
            }

            StringWriter stringWriterPool = new StringWriter();
            try (ImgWritableOutputStream output = new ImgWritableOutputStream(byteBuf)) {
                archive.write(stringWriterPool, "Property", output);

                Files.write(outputFile, ByteBufUtil.getBytes(byteBuf));
            } catch (Exception e) {
                log.error("An error occurred when writing to the output stream for {}.", path.getFileName(), e);
            }

        } else {
            log.error("The file {} doesn't exist.", path.getFileName());
        }
    }
}

