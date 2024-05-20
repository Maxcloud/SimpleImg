
package cdn.simple.img;


import cdn.simple.img.property.WzPropertyType;

import java.awt.*;

public class DataTool {

	private DataTool() {}

	public static int getInt(MapleData data) {
		return data.<Integer>getData();
	}

	public static double getDouble(MapleData data) {
		return data.<Double>getData();
	}

	public static Point getPoint(MapleData data) {
		return data.getData();
	}

	public static String getString(MapleData data) {
		return data.getData();
	}

	public static int getInt(String path, MapleData data) {
		return getInt(data.getChildByPath(path));
	}

	public static int getInt(MapleData data, int def) {
		if (data == null || data.getData() == null) {
			return def;
		} else {
			return data.<Integer>getData();
		}
	}

	public static int getInt(String path, MapleData data, int def) {
		return getInt(data.getChildByPath(path), def);
	}

	public static Point getPoint(String path, MapleData data) {
		return getPoint(data.getChildByPath(path));
	}

	public static String getString(String path, MapleData data) {
		return getString(data.getChildByPath(path));
	}

	public static String getString(String path, MapleData data, String def) {
		return getString(data.getChildByPath(path), def);
	}

	public static String getString(MapleData data, String def) {
		if (data == null || data.getData() == null) {
			return def;
		} else {
			return data.getData();
		}
	}

	public static int getIntConvert(MapleData data) {
		if (data.getType() == WzPropertyType.STRING) {
			return Integer.parseInt(getString(data));
		} else {
			return getInt(data);
		}
	}

	public static int getIntConvert(String path, MapleData data) {
		MapleData d = data.getChildByPath(path);
		return getIntConvert(d);
	}

	public static int getIntConvert(String path, MapleData data, int def) {
		MapleData d = data.getChildByPath(path);
		if (d == null) {
			return def;
		}
		if (d.getType() == WzPropertyType.STRING) {
			try {
				return Integer.parseInt(getString(d));
			} catch (NumberFormatException nfe) {
				return def;
			}
		} else {
			return getInt(d, def);
		}
	}

	public static Point getPoint(String path, MapleData data, Point def) {
		final MapleData pointData = data.getChildByPath(path);
		if (pointData == null) {
			return def;
		}
		return getPoint(pointData);
	}
	
	public static String getFullDataPath(MapleData data) {
		String path = "";
		MapleDataEntity myData = data;
		while (myData != null) {
			path = myData.getName() + "/" + path;
			myData = myData.getParent();
		}
		return path.substring(0, path.length() - 1);
	}

}
