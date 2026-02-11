package img.snippets.production;

import img.WzPathNavigator;
import img.io.impl.ImgRecyclableSeekableStream;

@FunctionalInterface
public interface WzDataFunction<T> {
    T apply(ImgRecyclableSeekableStream stream, WzPathNavigator root);
}
