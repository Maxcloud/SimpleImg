package img.model.common;

import java.util.Map;

public record WzImgCache(Map<String, Long> offsetCache,
                         Map<Long, String> stringCache,
                         Map<Long, String> uolCache) {
}