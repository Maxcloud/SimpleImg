# SimpleImg :fire:

A fast, modern `.img` file parser designed for Mushroom data files, built with efficiency and simplicity in mind.

Instead of parsing the entire `.img` structure on every access, this library pre-processes files by dumping string references to compact JSON metadata. At runtime, it resolves property paths using this metadata and seeks directly to the required offsets within the original file—significantly improving access speed.

Now also supports full extraction of .img files, making it easy to inspect.

Update 2026-02-12: Complete refactoring.\
Update 2026-02-13: Added support for older versions, such as version 55 and below.

## :clipboard: Planned Features

- Application interface to enable inspecting`.img` file contents using JSON metadata for quick navigation.
- Editing support for `.img` files to modify properties and save changes back to the original file.
- Filters to assist when rebuilding `.img` files to remove unused data and optimize file size.
- Bin-diff support to quickly find differences between two `.img` files, useful for patching or version comparisons.
 
## :heavy_check_mark: Features

- Parses `.img` files used in mushroom data formats (e.g., `Etc.wz`, `Character.wz`).
- Supports extracting full `.img` file contents to external format for inspection or processing.
- Uses pre-generated `.json` files to map string paths to byte offsets.
- High-performance file access using memory-mapped or byte-buffer-based I/O.
- Auto-closes resources using `AutoCloseable` and try-with-resources.
- Minimal object allocation and low overhead—ideal for high-frequency, real-time access.
- Thread-safe design for concurrent access to multiple `.img` files.


## :heavy_check_mark: Structure

The JSON cache is structured as a flat `Map<String, Long>`, where:

- The **key** is the path to the property (e.g., `1234/ItemId`)
- The **value** is the byte offset into the `.img` file.

## :heavy_check_mark: How to generate JSON files

The code below will generate JSON files for all `.img` files in the specified directory. The generated JSON files will
be stored in the same directory as the `.img` files.

### Example usage of the library to generate JSON files.

```java
public static void main(String[] args) {
    String wzFilePath = System.getProperty("wz.path");
    Path root = Path.of(wzFilePath);

    SimpleImg exportJsonService = new SimpleImg();
    exportJsonService.dumpStringsToJson(root);
}
```

## :heavy_check_mark: Library Usage

### Example usage of the library to read `.img` files.

```java
public class SetItemInfo {

    private static final WzDataFunction<SetItemInfoImg> pfnCommon = (stream, root) -> {
        WzValueReader reader;
        
        SetItemInfoImg setItemInfoImg = new SetItemInfoImg();
        for (String entryId : root.getChildren()) {

            WzPathNavigator child = root.resolve(entryId);
            reader = new WzValueReader(stream, child);

            setItemInfoImg.setItemName(reader.readString("setItemName"));
            setItemInfoImg.setCompleteCount(reader.readInt("completeCount"));

            WzPathNavigator pathItem = child.resolve("ItemID");
            for (String itemId : pathItem.getChildren()) {

            }

            WzPathNavigator pathEffect = child.resolve("Effect");
            for (String effectId : pathEffect.getChildren()) {
                WzPathNavigator effectChild = pathEffect.resolve(effectId);
                reader = new WzValueReader(stream, effectChild);

                setItemInfoImg.setIncPAD(reader.readInt("incPAD"));
                setItemInfoImg.setIncMDD(reader.readInt("incMDD"));
                setItemInfoImg.setIncACC(reader.readInt("incACC"));
                setItemInfoImg.setIncSTR(reader.readInt("incSTR"));
                setItemInfoImg.setIncDEX(reader.readInt("incDEX"));
                setItemInfoImg.setIncINT(reader.readInt("incINT"));
                setItemInfoImg.setIncLUK(reader.readInt("incLUK"));
                setItemInfoImg.setIncMHP(reader.readInt("incMHP"));
                setItemInfoImg.setIncMMP(reader.readInt("incMMP"));
                setItemInfoImg.setIncMHPr(reader.readInt("incMHPr"));
                setItemInfoImg.setIncMMPr(reader.readInt("incMMPr"));
                setItemInfoImg.setIncAllStat(reader.readInt("incAllStat"));
                setItemInfoImg.setIncSpeed(reader.readInt("incSpeed"));
                setItemInfoImg.setIncJump(reader.readInt("incJump"));

                WzPathNavigator pathOption = effectChild.resolve("Option");
                for (String optionId : pathOption.getChildren()) {
                    WzPathNavigator optionChild = pathOption.resolve(optionId);
                    reader = new WzValueReader(stream, optionChild);

                    int level = reader.readInt("level");
                    int option = reader.readInt("option");
                }

            }
        }

        return setItemInfoImg;
    };

    public static void main(String[] args) {
        EtcWzDataRequest imgDataRequest = new EtcWzDataRequest("SetItemInfo.img");

        ReadImgFile<SetItemInfoImg> readImgFile = new ReadImgFile<>();
        readImgFile.fromImg(imgDataRequest, pfnCommon);
    }
}
```

### Credits
[HaRepacker-Resurrected](https://github.com/lastbattle/Harepacker-resurrected/)