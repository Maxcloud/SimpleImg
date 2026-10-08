package img.snippets.example;

import java.nio.file.Path;

public class EtcDataRequest implements WzImplDataRequest {

    private final String imgName;
    private Path imgPath;
    private Path filePath;

    public EtcDataRequest(final String fileName) {

        imgName = fileName.replace(".img", "");
        String imgPath = "Etc.wz/%s".formatted(fileName);

        // this.imgPath = output.resolve(imgPath);
        // this.filePath = Path.of(this.imgPath + ".json");
    }

    @Override
    public String getImgName() {
        return imgName;
    }

    @Override
    public Path getImgPath() {
        return imgPath;
    }

    @Override
    public Path getFilePath() {
        return filePath;
    }
}
