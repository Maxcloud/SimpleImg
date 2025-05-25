package img.snippets.example;

import img.ReadImgFile;
import img.WzPathNavigator;
import img.WzValueReader;
import img.snippets.production.EtcWzDataRequest;
import img.snippets.production.SkillDataFunction;
import img.snippets.production.SkillDataCommon;

public class SetItemInfo {

    private static final SkillDataFunction<Object> pfnCommon =
            (stream, root) -> {
        WzValueReader reader;
        SkillDataCommon common = new SkillDataCommon();

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

                int incPDD = reader.readInt("incPDD");
                int incMDD = reader.readInt("incMDD");
                int incACC = reader.readInt("incACC");
                int incSTR = reader.readInt("incSTR");
                int incDEX = reader.readInt("incDEX");
                int incINT = reader.readInt("incINT");
                int incLUK = reader.readInt("incLUK");
                int incPAD = reader.readInt("incPAD");
                int incMAD = reader.readInt("incMAD");
                int incMHP = reader.readInt("incMHP");
                int incMMP = reader.readInt("incMMP");
                int incMHPr = reader.readInt("incMHPr");
                int incMMPr = reader.readInt("incMMPr");
                int incAllStat = reader.readInt("incAllStat");
                int incSpeed = reader.readInt("incSpeed");
                int incJump = reader.readInt("incJump");

                WzPathNavigator pathOption = effectChild.resolve("Option");
                for (String optionId : pathOption.getChildren()) {
                    WzPathNavigator optionChild = pathOption.resolve(optionId);
                    reader = new WzValueReader(stream, optionChild);

                    int level = reader.readInt("level");
                    int option = reader.readInt("option");
                }

            }
        }

        return common;
    };

    public static void main(String[] args) {
        EtcWzDataRequest imgDataRequest = new EtcWzDataRequest("SetItemInfo.img");

        ReadImgFile<Object> readImgFile = new ReadImgFile<>();
        readImgFile.fromImg(imgDataRequest, pfnCommon);
    }
}
