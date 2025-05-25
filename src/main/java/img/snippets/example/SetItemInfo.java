package img.snippets.example;

import img.ReadImgFile;
import img.WzPathNavigator;
import img.WzValueReader;
import img.snippets.production.EtcWzDataRequest;
import img.snippets.production.WzDataFunction;

public class SetItemInfo {

    private static final WzDataFunction<SetItemInfoImg> pfnCommon =
            (stream, root) -> {
        WzValueReader reader;
            SetItemInfoImg setItemInfoImg = new SetItemInfoImg();

        for (String entryId : root.getChildren()) {

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
