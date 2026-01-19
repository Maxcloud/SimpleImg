package app.menu;

import java.util.prefs.Preferences;

public class SimpleImgApplication {
    private static final String LAST_OPENED_DIRECTORY = "lastOpenedDirectory";
    private static final Preferences oPreferences = Preferences.userNodeForPackage(SimpleImgApplication.class);

    public static void saveLastOpenedDirectory(String path) {
        oPreferences.put(LAST_OPENED_DIRECTORY, path);
    }

    public static String getLastOpenedDirectory() {
        return oPreferences.get(LAST_OPENED_DIRECTORY, null);
    }
}
