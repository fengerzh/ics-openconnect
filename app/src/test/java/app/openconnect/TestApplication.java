package app.openconnect;

import android.app.Application;

public class TestApplication extends Application {
    @Override
    public void onCreate() {
        // Skip native library loading (System.loadLibrary fails in Robolectric)
        // We just need the SharedPreferences and other Android framework features
        app.openconnect.core.FragCache.init();
    }
}