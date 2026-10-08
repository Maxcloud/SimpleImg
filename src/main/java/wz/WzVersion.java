package wz;

import img.io.ImgRecyclableSeekableStream;

public class WzVersion {

	private int hash;

    /**
	 * Constructs a WzVersion object from a RecyclableSeekableStream.
	 *
	 * @param in The stream to read the version hash from.
	 * @param version The game version the file belongs to (e.g. 95).
	 */
	public WzVersion(ImgRecyclableSeekableStream in, int version) {
		this.hash = in.readShort();
		CheckAndGetVersionHash(hash, version);
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
