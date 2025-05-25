package img.snippets.production;

import img.snippets.ImgDataRequest;

import java.nio.file.Path;

public class EtcWzDataRequest implements ImgDataRequest {

    private final Path filePath;

    public EtcWzDataRequest(String fileName) {
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
