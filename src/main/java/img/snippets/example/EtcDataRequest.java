package img.snippets.example;

import java.nio.file.Path;

public class EtcDataRequest implements WzImplDataRequest {

    private final Path filePath;

    public EtcDataRequest(final String fileName) {
        String wzFilePath = System.getProperty("wz.path");
        Path imgFilePath = Path.of(wzFilePath);

        String imgPath = "Etc.wz/%s".formatted(fileName);
        this.filePath = imgFilePath.resolve(imgPath);
    }

    @Override
    public String getImgPath() {
        return "";
    }

    @Override
    public Path getFilePath() {
        return filePath;
    }
}
