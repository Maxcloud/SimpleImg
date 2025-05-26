package wz;

import java.util.List;

public interface MapleData extends MapleDataEntity {
	String getName();
	List<MapleData> getChildren();
	<T> T getData();
}
