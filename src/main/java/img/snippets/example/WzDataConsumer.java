package img.snippets.example;

import img.WzPathNavigator;
import img.io.impl.ImgRecyclableSeekableStream;

@FunctionalInterface
public interface WzDataConsumer {
    void accept(ImgRecyclableSeekableStream stream, WzPathNavigator root);
}
