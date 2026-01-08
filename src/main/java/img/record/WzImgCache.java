package img.record;

import java.util.Map;

public record WzImgCache(Map<String, Long> stringCache,
                         Map<Long, String> offsetCache,
                         Map<Long, String> uolCache) {
}