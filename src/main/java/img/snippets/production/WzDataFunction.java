package img.snippets.production;

import img.WzPathNavigator;
import img.io.RecyclableSeekableStream;

@FunctionalInterface
public interface WzDataFunction<T> {
    T apply(RecyclableSeekableStream stream, WzPathNavigator root);
}
