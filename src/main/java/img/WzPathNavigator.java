package img;

import img.model.common.WzImgCache;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class WzPathNavigator {

    private final String context;
    private final Map<String, Long> offsets;
    private final Map<Long, String> strings;
    // private final Map<Long, String> uolStrings;

    public String getContext() {
        return context;
    }

    public Map<String, Long> getOffsets() {
        return offsets;
    }

    public Map<Long, String> getStrings() {
        return strings;
    }

    public WzPathNavigator() {
        this.context = "";
        this.offsets = Collections.emptyMap();
        this.strings = Collections.emptyMap();
        // this.uolStrings = Collections.emptyMap();
    }

    public WzPathNavigator(String context, WzImgCache data) {
        this.context = context;
        this.offsets = data.offsetCache();
        this.strings = data.stringCache();
        // this.uolStrings = data.uolCache();
    }

    private WzPathNavigator(String context, Map<String, Long> offsets, Map<Long, String> strings/*, Map<Long, String> uolStrings*/) {
        this.context = context;
        this.offsets = offsets;
        this.strings = strings;
        // this.uolStrings = uolStrings;
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
        return new WzPathNavigator(newContext, offsets, strings);
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

    /*public String getUolString(long offset) {
        return getUolStrings().getOrDefault(offset, "");
    }*/

}
