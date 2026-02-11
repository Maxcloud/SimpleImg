package img;

import img.configuration.DirectoryConfiguration;
import img.io.deserialize.JsonFileToObject;
import img.io.repository.KeyFileRepository;
import img.model.common.Version;

import java.nio.file.Path;
import java.util.Objects;

public class WzConfiguration {

    private final Path filePath;
    private DirectoryConfiguration configuration;
    private KeyFileRepository<Version> repository;
    private WzTestCryptography cryptography;

    public WzConfiguration(Path filePath) {
        this.filePath = Objects.requireNonNull(filePath, "File path cannot be null.");
    }

    public DirectoryConfiguration getDirectories() {
        if (configuration == null) {
            JsonFileToObject<DirectoryConfiguration> fileToObject =
                    new JsonFileToObject<>(filePath, DirectoryConfiguration.class);
            configuration = fileToObject.loadFromJson();
        }
        return configuration;
    }

    public KeyFileRepository<Version> getRepository() {
        if (repository == null) {
            String version = getDirectories().getVersion();
            repository = new KeyFileRepository<>(Version.class);
            repository.setSecret(version);
        }
        return repository;
    }

    public WzTestCryptography getCryptography() {
        if (cryptography == null) {
            cryptography = new WzTestCryptography(getRepository());
        }
        return cryptography;
    }

    public String getInput() {
        return configuration.getInput();
    }

    public String getOutput() {
        return configuration.getOutput();
    }

    public int getVersion() {
        return configuration.getVersion_();
    }

    public byte[] getSecret() {
        WzTestCryptography cryptography = getCryptography();
        return cryptography.getSecret();
    }
}
