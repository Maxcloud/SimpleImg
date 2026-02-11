package img.crypto;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class WzStringHandler {

    private final WzStringDecodeImpl oUnicodeString;
    private final WzStringDecodeImpl oAsciiString;
    private final WzStringCodec oStringCodec;
    private List<String> lListImgFiles = Collections.emptyList();

    public WzStringHandler(int version, byte[] secret) {
        this.oUnicodeString = new WzUnicodeString();
        if (version <= 55) {
            this.oAsciiString = new WzVersion55AsciiString(secret);
        } else {
            this.oAsciiString = new WzVersion83AsciiString(secret);
        }
        this.oStringCodec   = new WzStringCodec();
    }

    public WzStringCodec getCodec() {
        return oStringCodec;
    }

    public boolean isListImg(Path path) {
        String fileName = path.getFileName().toString();
        return lListImgFiles != null && lListImgFiles.contains(fileName);
    }

    public List<String> getListImgFiles() {
        return Collections.unmodifiableList(lListImgFiles);
    }

    public void setModernImgFiles(List<String> lModernImgFiles) {
        this.lListImgFiles = lModernImgFiles != null ?
                List.copyOf(lModernImgFiles)
                : Collections.emptyList();
    }

    public WzDecodeRecord decode(boolean isUnicode, byte[] data, int len, boolean isModernImgFile) {
        WzStringDecodeImpl decoder = isUnicode ? oUnicodeString : oAsciiString;
        decoder.decode(data, len, isModernImgFile);
        return new WzDecodeRecord(isUnicode, data, len);
    }
}
