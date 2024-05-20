import cdn.simple.img.DataTool;
import cdn.simple.img.ImgFile;
import cdn.simple.img.MapleData;

import java.io.File;
import java.util.Objects;

public class WzImgMain {

	public static void main(String[] args) {
		long start = System.nanoTime();

		ImgFile mapFile = new ImgFile("Map.wz");

		/** Get the directories and return the name of each one **/
		File[] directories = mapFile.getFile().listFiles(File::isDirectory);
		Objects.requireNonNull(directories, "There are no directories at that location.");
		for(File file : directories) {
			System.out.println(file.getName());
		}

		/** Get the files and return the name of each one **/
		File[] files = mapFile.getFile().listFiles(File::isFile);
		Objects.requireNonNull(files, "There are no files at that location.");
		for(File file : files) {
			System.out.println(file.getName());
		}

		ImgFile itemFile = new ImgFile("Item.wz");

		/** Transverse through an image to retrieve itemids **/
		/*MapleData mapleData = itemFile.getImageData("Cash/0501.img");
		for (MapleData md : mapleData.getChildren()) {
			System.out.println(md.getName());
		}*/

		String[] paths = {
			"Map/Map0",
			"Map/Map1",
			"Map/Map2",
			"Map/Map3",
			"Map/Map5",
			"Map/Map6",
			"Map/Map7",
			"Map/Map8",
			"Map/Map9"
		};

		for(String path : paths) {
			File[] filess = new File(mapFile.getFile(), path).listFiles(File::isFile);
			for(File fil : filess) {
				String name = fil.getName();
				//System.out.println(fil.getName());
				MapleData itemOptionData = mapFile.getImageData(path+"/"+name);
				for (MapleData md : itemOptionData.getChildren()) {
					System.out.println(md.getName());
				}
			}
		}

		/*MapleData itemOptionData = itemFile.getImageData("ItemOption.img");
		for (MapleData md : itemOptionData.getChildren()) {
			MapleData m = md.getChildByPath("info");


			// String str = DataTool.getData<String>("string", m, "unk");
			String str = DataTool.getString("string", m, "unk");
			int weight = DataTool.getInt("weight", m, 0);

			System.out.println(String.format("Name(%s) - String(%s), Weight(%s)", md.getName(), str, weight));
		}*/

		long nanoDuration = System.nanoTime() - start;

		long ms = (long) (((nanoDuration % (1e9 * 60)) % 1e9) / 1e6);
		long sec = (long) ((nanoDuration % (1e9 * 60)) / 1e9);
		long min = nanoDuration / ((long) 1e9 * 60);
		System.out.println("Completed in " + min + " min " + sec + " sec " + ms + " ms.");

	}

}
