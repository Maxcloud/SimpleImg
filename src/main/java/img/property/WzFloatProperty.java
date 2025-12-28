package img.property;

import img.io.ImgSeekableInputStream;
import img.io.ImgWritableOutputStream;
import img.util.StringWriter;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class WzFloatProperty implements WzProperty {

    private final byte VT_R4 = 4;
    private float data;

    @Override
    public void read(ImgSeekableInputStream stream) {
        this.data = stream.decodeFloat();
    }

    @Override
    public void write(StringWriter stringWriterPool, String key, ImgWritableOutputStream stream) {

        stringWriterPool.internalSerializeString(stream, key, (byte) 0x00, (byte) 0x01);
        stream.writeByte(VT_R4);
        stream.writeCompressedFloat(data);
    }
}
