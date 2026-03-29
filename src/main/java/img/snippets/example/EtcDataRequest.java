package img.snippets.example;

import img.EnvironmentConfig;
import img.WzConfiguration;

import java.nio.file.Path;

public class EtcDataRequest implements WzImplDataRequest {

    private final String imgName;
    private final Path imgPath;
    private final Path filePath;

    public EtcDataRequest(final String fileName) {
        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        WzConfiguration configuration = new WzConfiguration(environmentConfig);
        EnvironmentConfig environment = configuration.getEnvironment();

        Path output = Path.of(environment.get("simple.img.output"));

        imgName = fileName.replace(".img", "");
        String imgPath = "Etc.wz/%s".formatted(fileName);

        this.imgPath = output.resolve(imgPath);
        this.filePath = Path.of(this.imgPath + ".json");
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
