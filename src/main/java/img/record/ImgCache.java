package img.record;

import java.util.Map;

public record ImgCache(Map<String, Long> stringCache, Map<Long, String> offsetCache) {}