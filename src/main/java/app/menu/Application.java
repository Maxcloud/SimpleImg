package app.menu;

import java.util.prefs.Preferences;

public class Application {

    private static final String LAST_OPEN_DIRECTORY = "lastOpenedDirectory";
    private static final String LAST_BROWSE_DIRECTORY = "lastOpenedBrowseDir";

    private static final Preferences oPreferences = Preferences.userNodeForPackage(Application.class);

    public static void OnSaveLastOpenDirectory(String path) {
        oPreferences.put(LAST_OPEN_DIRECTORY, path);
    }

    public static String OnGetLastOpenDirectory() {
        return oPreferences.get(LAST_OPEN_DIRECTORY, null);
    }

    public static void OnSaveLastBrowseDirectory(String path) {
        oPreferences.put(LAST_BROWSE_DIRECTORY, path);
    }

    public static String OnGetLastBrowseDirectory() {
        return oPreferences.get(LAST_BROWSE_DIRECTORY, null);
    }
}
