package img;

import img.crypto.WzCryptography;
import img.io.repository.KeyFileRepository;
import img.model.common.Version;

public class WzConfiguration {

    private WzCryptography cryptography;
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

    public WzCryptography getCryptography() {
        if (cryptography == null) {
            cryptography = new WzCryptography(getRepository());
        }
        return cryptography;
    }

    public byte[] getSecret() {
        WzCryptography cryptography = getCryptography();
        return cryptography.getSecret();
    }
}
