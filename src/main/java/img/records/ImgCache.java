package img.records;

import java.util.Map;

public record ImgCache(Map<String, Long> stringCache, Map<Long, String> offsetCache) {}