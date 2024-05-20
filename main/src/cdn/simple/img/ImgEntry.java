package cdn.simple.img;

import cdn.simple.img.property.WzPropertyType;

import java.util.*;

public class ImgEntry implements MapleData {

	private String name;
	private Object data;
	private WzPropertyType type;
	private MapleDataEntity parent;

	private final List<MapleData> children = new ArrayList<>();

	public ImgEntry(MapleDataEntity parent) {
		this.parent = parent;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public MapleDataEntity getParent() {
		return parent;
	}

	@Override
	public WzPropertyType getType() {
		return type;
	}

	public void setType(WzPropertyType type) {
		this.type = type;
	}

	@Override
	public List<MapleData> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public void addChild(ImgEntry entry) {
		children.add(entry);
	}

	@Override
	public MapleData getChildByPath(String path) {
		String[] segments = path.split("/");
		if (segments[0].equals("..")) {
			return ((MapleData) getParent()).getChildByPath(path.substring(path.indexOf("/") + 1));
		}

		MapleData ret = this;
		for (int x = 0; x < segments.length; x++) {
			boolean foundChild = false;
			for (MapleData child : ret.getChildren()) {
				if (child.getName().equals(segments[x])) {
					ret = child;
					foundChild = true;
					break;
				}
			}
			if (!foundChild) {
				return null;
			}
		}

		return ret;
	}

	@Override
	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public Iterator<MapleData> iterator() {
		return null;
	}

	public void finish() {
		((ArrayList<MapleData>) children).trimToSize();
	}

}
