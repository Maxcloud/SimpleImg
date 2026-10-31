package img.crypto;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class WzString {

    private final IWzStringDecode oUnicodeString;
    private final IWzStringDecode oAsciiString;
    private final WzStringRegistry oStringRegistry;
    private List<String> lListImgFiles = Collections.emptyList();
    private boolean modernNames;

    /*
     * String encryption eras, selected by client version:
     *  - v55 and below: the plain 0xAA+i / 0xAAAA+i mask for most
     *    strings, PLUS the classic ZLZ key (chained from the v55 IV
     *    B9 7D 63 E9) for img files listed in List.wz (Mob.wz's older
     *    imgs are the known members).
     *  - v56 to v92 (v62 verified): every string additionally XORs
     *    the 64 KiB WzKey from WzKeyGenerator (GMS IV).
     *  - v93 onwards (v95 verified): the modern per-version secret
     *    scheme (the "modern img" branch below).
     */
    public WzString(int version, byte[] secret) {
        if (version <= 55) {
            byte[] wzKey = WzKeyGenerator.generateWzKey(WzKeyGenerator.IV_BMS);
            this.oUnicodeString = new WzUnicodeString(wzKey, true);
            this.oAsciiString = new WzAsciiString(wzKey, new WzAlphabet().getAlphabet());
        } else if (version < 93) {
            byte[] wzKey = WzKeyGenerator.generateWzKey();
            this.oUnicodeString = new WzUnicodeString(wzKey);
            this.oAsciiString = new WzAsciiString(secret, wzKey);
        } else {
            this.oUnicodeString = new WzUnicodeString();
            this.oAsciiString = new WzAsciiString(secret);
        }
        this.oStringRegistry = new WzStringRegistry();
        this.modernNames = version >= 93;
    }

    /**
     * Forces the modern string scheme for every string read through this
     * handler, overriding the version-based era selection.
     */
    public void setModernNames(boolean modernNames) {
        this.modernNames = modernNames;
    }

    public WzStringRegistry getRegistry() {
        return oStringRegistry;
    }

    public boolean isListImg(Path path) {
        if (modernNames) {
            return true;
        }

        String fileName = path.getFileName().toString();
        return lListImgFiles.contains(fileName);
    }

    public List<String> getListImgFiles() {
        return Collections.unmodifiableList(lListImgFiles);
    }

    public void setListFiles(List<String> lModernImgFiles) {
        this.lListImgFiles = lModernImgFiles != null ?
                List.copyOf(lModernImgFiles)
                : Collections.emptyList();
    }

    public WzDecodeRecord decode(boolean isUnicode, byte[] data, int len, boolean isModernImgFile) {
        IWzStringDecode decoder = isUnicode ? oUnicodeString : oAsciiString;
        decoder.decode(data, len, isModernImgFile);
        return new WzDecodeRecord(isUnicode, data, len);
    }
}
