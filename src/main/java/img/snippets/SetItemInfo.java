package img.snippets;

import img.WzPathNavigator;
import img.WzValueReader;
import img.cache.ImgCacheRepository;
import img.io.RecyclableSeekableStream;
import img.records.ImgCache;

import java.nio.file.Path;

public class SetItemInfo {

    public static void main(String[] args) {
        String wzFilePath = System.getProperty("wz.path");

        Path path = Path.of(wzFilePath);
        Path imgFilePath = path.resolve("Etc.wz/SetItemInfo.img");

        ImgCacheRepository stringCache = new ImgCacheRepository(imgFilePath);
        ImgCache imgCache = stringCache.loadFromFile();

        WzPathNavigator root = new WzPathNavigator("", imgCache);

        try (RecyclableSeekableStream stream = new RecyclableSeekableStream(imgFilePath)) {
            for (String entryId : root.getChildren()) {
                WzValueReader reader;

                WzPathNavigator child = root.resolve(entryId);
                reader = new WzValueReader(stream, child);

                String setItemName = reader.readString("setItemName");
                int completeCount = reader.readInt("completeCount");

                WzPathNavigator pathItem = child.resolve("ItemID");
                for (String itemId : pathItem.getChildren()) {

                }

                WzPathNavigator pathEffect = child.resolve("Effect");
                for (String effectId : pathEffect.getChildren()) {
                    WzPathNavigator effectChild = pathEffect.resolve(effectId);
                    reader = new WzValueReader(stream, effectChild);

                    int incPDD      = reader.readInt("incPDD");
                    int incMDD      = reader.readInt("incMDD");
                    int incACC      = reader.readInt("incACC");
                    int incSTR      = reader.readInt("incSTR");
                    int incDEX      = reader.readInt("incDEX");
                    int incINT      = reader.readInt("incINT");
                    int incLUK      = reader.readInt("incLUK");
                    int incPAD      = reader.readInt("incPAD");
                    int incMAD      = reader.readInt("incMAD");
                    int incMHP      = reader.readInt("incMHP");
                    int incMMP      = reader.readInt("incMMP");
                    int incMHPr     = reader.readInt("incMHPr");
                    int incMMPr     = reader.readInt("incMMPr");
                    int incAllStat  = reader.readInt("incAllStat");
                    int incSpeed    = reader.readInt("incSpeed");
                    int incJump     = reader.readInt("incJump");

                    WzPathNavigator pathOption = effectChild.resolve("Option");
                    for (String optionId : pathOption.getChildren()) {
                        WzPathNavigator optionChild = pathOption.resolve(optionId);
                        reader = new WzValueReader(stream, optionChild);

                        int level = reader.readInt("level");
                        int option = reader.readInt("option");
                    }

                }
            }

        }
    }
}
