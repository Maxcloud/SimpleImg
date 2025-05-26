package img.extract;

import img.io.ImgSeekableInputStream;

public class WzVersion {

	private int hash;

	public WzVersion(ImgSeekableInputStream stream) {
		this.hash = stream.readShort();
		CheckAndGetVersionHash(hash, 111);
	}

	public int getHash() {
		return hash;
	}

	// Credits: https://github.com/lastbattle/Harepacker-resurrected/
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
