package img.crypto;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public record WzDecodeRecord(boolean isUnicode, byte[] data, int pos) {

    public Charset charset() {
        return isUnicode() ?
                StandardCharsets.UTF_16LE :
                Charset.defaultCharset();
    }

}
