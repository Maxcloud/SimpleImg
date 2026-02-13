package img;

import img.io.repository.KeyFileRepository;
import img.model.common.Version;

public class WzConfiguration {

    private WzTestCryptography cryptography;
    private final EnvironmentConfig environment;

    public WzConfiguration() {
        this.environment = new EnvironmentConfig();
    }

    public EnvironmentConfig getEnvironment() {
        return environment;
    }

    public KeyFileRepository<Version> getRepository() {
        String version = environment.get("simple.img.version.string");

        KeyFileRepository<Version> repository = new KeyFileRepository<>(Version.class);
        repository.setSecret(version);

        return repository;
    }

    public WzTestCryptography getCryptography() {
        if (cryptography == null) {
            cryptography = new WzTestCryptography(getRepository());
        }
        return cryptography;
    }

    public byte[] getSecret() {
        WzTestCryptography cryptography = getCryptography();
        return cryptography.getSecret();
    }
}
