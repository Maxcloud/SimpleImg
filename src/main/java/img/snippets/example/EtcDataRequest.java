package img.snippets.example;

import img.EnvironmentConfig;
import img.WzConfiguration;

import java.nio.file.Path;

public class EtcDataRequest implements WzImplDataRequest {

    private final String imgPath;
    private final Path filePath;

    public EtcDataRequest(final String fileName) {
        WzConfiguration configuration = new WzConfiguration();
        EnvironmentConfig environment = configuration.getEnvironment();

        Path output = Path.of(environment.get("simple.img.output"));

        String imgPath = "Etc.wz/%s".formatted(fileName);

        this.imgPath = output.resolve(imgPath).toString();
        this.filePath = Path.of(this.imgPath + ".json");
    }

    @Override
    public String getImgPath() {
        return imgPath;
    }

    @Override
    public Path getFilePath() {
        return filePath;
    }
}
