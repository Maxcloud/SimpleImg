package img;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@Slf4j
public class WzPathNavigator {

    private final String context;
    private final Map<String, Long> strings;
    private final Map<Long, String> offsets;

    public WzPathNavigator() {
        this.context = "";
        this.strings = Collections.emptyMap();
        this.offsets = Collections.emptyMap();
    }

    public WzPathNavigator(String context, WzImgCache data) {
        this.context = context;
        this.strings = data.stringCache();
        this.offsets = data.offsetCache();
    }

    private WzPathNavigator(String context, Map<String, Long> strings, Map<Long, String> offsets) {
        this.context = context;
        this.strings = strings;
        this.offsets = offsets;
    }

    public WzPathNavigator resolve(String format, Object... args) {
        String formattedPath = format.formatted(args);
        return resolve(formattedPath);
    }

    public WzPathNavigator resolve(String relativePath) {
        String newContext = context.isEmpty() ? relativePath : context + "/" + relativePath;
        if (!strings.containsKey(newContext)) {
            // only log when in debug mode
            // log.debug("Path not found: {}", newContext);
            return new WzPathNavigator(); // silent fail
        }
        return new WzPathNavigator(newContext, strings, offsets);
    }

    public List<String> getChildren() {
        if (getStrings().isEmpty()) {
            return Collections.emptyList();
        }

        String prefix = context.isEmpty() ? "" : (this.context + "/");
        return getStrings().keySet().stream()
                .filter(key -> key.startsWith(prefix))
                .map(key -> key.substring(prefix.length()))
                .filter(this::isValidChildKey)
                .toList();
    }

    private boolean isValidChildKey(String key) {
        return (!key.isEmpty() && key.indexOf('/') == -1);
    }

    public long getOffset(String attribute) {
        return getStrings().getOrDefault(this.context + "/" + attribute, -1L);
    }

    public String getString(long offset) {
        return getOffsets().getOrDefault(offset, "");
    }

}
