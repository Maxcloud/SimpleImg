package img.io.repository;

import java.nio.file.Path;
import java.util.Map;

import img.io.deserialize.JsonFileToObject;
import img.model.common.Version;

public class KeyFileRepository<T> extends JsonFileToObject<T> {

    private static final String DEFAULT = "8:1:1";

    private int region;
    private int version;
    private int revision;
    private byte[] secret;

    public Version wzVersion;

    public KeyFileRepository(Class<T> clazz) {
        super(Path.of("release/config/version.json"), clazz);
        wzVersion = (Version) loadFromJson();
        setSecret(DEFAULT);
    }

    public byte[] getSecret() {
        return secret.clone();
    }

    public void setSecret(String regionVersionAndRevision) {
        Map<String, String> lEncryptionKeys = wzVersion.lEncryptionKeys();

        String[] parts = regionVersionAndRevision.split(":", 3);
        String encryptionKey = lEncryptionKeys.get(regionVersionAndRevision);
        if (encryptionKey == null) {
            parts = DEFAULT.split(":", 3);
            encryptionKey = lEncryptionKeys.get(DEFAULT);
        }

        if (parts.length != 3) {
            throw new IllegalArgumentException("Input must contain exactly two hexadecimal numbers separated by space.");
        }

        region = Integer.parseInt(parts[0]);
        version = Integer.parseInt(parts[1]);
        revision = Integer.parseInt(parts[2]);

        byte[] allBytes = hexStringToByteArray(encryptionKey);

        int len = (int) Math.ceil(allBytes.length / 4.0) * 4;
        secret = new byte[len];

        int resultIndex = 0;
        for (int i = 0; i < allBytes.length; i += 4) {
            secret[resultIndex++] = allBytes[i];
            for (int j = 0; j < 3; j++) {
                secret[resultIndex++] = 0x00;
            }
        }
    }

    private byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    public int getRegion() {
        return region;
    }

    public int getVersion() {
        return version;
    }

    public int getRevision() {
        return revision;
    }
}
