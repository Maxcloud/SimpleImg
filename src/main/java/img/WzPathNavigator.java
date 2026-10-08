package img;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class WzPathNavigator {

    private final String context;
    private final Map<String, Long> offsets;
    private final Map<Long, String> strings;
    private final Map<Long, String> vectorStrings;
    private final Map<Long, String> uolStrings;

    public String getContext() {
        return context;
    }

    public Map<String, Long> getOffsets() {
        return offsets;
    }

    public Map<Long, String> getStrings() {
        return strings;
    }

    public Map<Long, String> getVectorStrings() {
        return vectorStrings;
    }

    public Map<Long, String> getUolStrings() {
        return uolStrings;
    }

    public WzPathNavigator() {
        this.context = "";
        this.offsets = Collections.emptyMap();
        this.strings = Collections.emptyMap();
        this.vectorStrings = Collections.emptyMap();
        this.uolStrings = Collections.emptyMap();
    }

    public WzPathNavigator(String context, FileImgRecord data) {
        this.context = context;
        this.offsets = data.getOffsetCache();
        this.strings = data.getStringCache();
        this.vectorStrings = data.getVectorCache();
        this.uolStrings = data.getUolCache();
    }

    private WzPathNavigator(String context, Map<String, Long> offsets, Map<Long, String> strings,
                            Map<Long, String> vectorStrings, Map<Long, String> uolStrings) {
        this.context = context;
        this.offsets = offsets;
        this.strings = strings;
        this.vectorStrings = vectorStrings;
        this.uolStrings = uolStrings;
    }

    public WzPathNavigator resolve(String format, Object... args) {
        String formattedPath = format.formatted(args);
        return resolve(formattedPath);
    }

    public WzPathNavigator resolve(String relativePath) {
        String newContext = context.isEmpty() ? relativePath : context + "/" + relativePath;
        if (!offsets.containsKey(newContext)) {
            // only log when in debug mode
            // log.debug("Path not found: {}", newContext);
            return new WzPathNavigator(); // silent fail
        }
        return new WzPathNavigator(newContext, offsets, strings, vectorStrings, uolStrings);
    }

    public List<String> getChildren() {
        if (getOffsets().isEmpty()) {
            return Collections.emptyList();
        }

        String prefix = context.isEmpty() ? "" : (this.context + "/");
        return getOffsets().keySet().stream()
                .filter(key -> key.startsWith(prefix))
                .map(key -> key.substring(prefix.length()))
                .filter(this::isValidChildKey)
                .toList();
    }

    private boolean isValidChildKey(String key) {
        return (!key.isEmpty() && key.indexOf('/') == -1);
    }

    public long getOffset(String attribute) {
        return getOffsets().getOrDefault(this.context + "/" + attribute, -1L);
    }

    public String getString(long offset) {
        return getStrings().getOrDefault(offset, "");
    }

}
