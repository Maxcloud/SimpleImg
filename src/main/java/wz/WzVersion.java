package wz;

import img.cache.JsonFileToObject;
import img.cache.DirectoryConfiguration;
import img.io.RecyclableSeekableStream;

import java.nio.file.Path;

public class WzVersion {

	private static final Path configFile = Path.of("src/main/resources/configuration.json");

	private int hash;

    /**
	 * Constructs a WzVersion object from a RecyclableSeekableStream.
	 *
	 * @param stream The stream to read the version hash from.
	 */
	public WzVersion(RecyclableSeekableStream stream) {
		JsonFileToObject<DirectoryConfiguration> fileToObject =
				new JsonFileToObject<>(configFile, DirectoryConfiguration.class);
		DirectoryConfiguration directoryConfiguration = fileToObject.createObjFromFile();
		String[] splits = directoryConfiguration.getVersion().split(":", 3);

		this.hash = stream.readShort();
		CheckAndGetVersionHash(hash, Integer.parseInt(splits[1]));
	}

	public int getHash() {
		return hash;
	}

	/**
	 * Checks the version hash and updates it if the version matches.
	 *
	 * @param version      The expected version number.
	 * @param patchVersion The patch version to check against.
	 */
	public void CheckAndGetVersionHash(int version, int patchVersion) {
		int hash = 0;
		this.hash = hash;
		String versionNumberStr = String.valueOf(patchVersion);

		int l = versionNumberStr.length();
		for (int i = 0; i < l; i++) {
			hash = (32 * hash) + versionNumberStr.charAt(i) + 1;
		}

		int a = (hash >> 24) & 0xFF;
		int b = (hash >> 16) & 0xFF;
		int c = (hash >> 8) & 0xFF;
		int d = hash & 0xFF;
		int decryptedVersionNum = (0xFF ^ a ^ b ^ c ^ d);
		if (version == decryptedVersionNum) {
			this.hash = hash;
		}
	}

}
