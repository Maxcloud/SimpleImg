# SimpleImg

A fast, modern `.img` file parser designed for Mushroom data files, built with efficiency and simplicity in mind.

Instead of parsing the entire `.img` structure every time, this library pre-processes `.img` files by dumping string references to JSON files. At runtime, it reads the JSON to resolve property paths and uses the offset to seek directly into the file, significantly improving access speed.

## Features

- Parses `.img` files used in Mushroom data formats (e.g., `Etc.wz`, `Character.wz`).
- Uses pre-generated `.json` files to map string paths to byte offsets.
- High-performance file access using memory-mapped or byte-buffer-based I/O.
- Auto-closes resources using `AutoCloseable` and try-with-resources.
- Minimal allocations and overhead for quick repeated access.

## Structure

The JSON cache is structured as a flat `Map<String, Long>`, where:
- The **key** is the path to the property (e.g., `1234/ItemId`)
- The **value** is the byte offset into the `.img` file.

## How to Use
### Example usage of the library to read `.img` files.
```java
String wzFilePath = System.getProperty("wz.path");

Path path = Path.of(wzFilePath);
Path imgFilePath = path.resolve("Etc.wz/SetItemInfo.img");

ImgCacheBuilder stringCache = new ImgCacheBuilder(imgFilePath);
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
```