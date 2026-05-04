package app.openconnect.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import app.openconnect.TestApplication;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35, application = TestApplication.class)
public class ExternalAppDatabaseTest {

    private ExternalAppDatabase db;
    private SharedPreferences prefs;

    @Before
    public void setUp() {
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        db = new ExternalAppDatabase(context);
        prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        // Clear any existing data
        prefs.edit().clear().commit();
    }

    @Test
    public void emptyListIsNotAllowed() {
        assertFalse(db.isAllowed("com.example.app"));
    }

    @Test
    public void addAppAndCheckAllowed() {
        db.addApp("com.example.app");
        assertTrue(db.isAllowed("com.example.app"));
    }

    @Test
    public void addMultipleApps() {
        db.addApp("com.example.app1");
        db.addApp("com.example.app2");
        assertTrue(db.isAllowed("com.example.app1"));
        assertTrue(db.isAllowed("com.example.app2"));
        assertFalse(db.isAllowed("com.example.app3"));
    }

    @Test
    public void removeApp() {
        db.addApp("com.example.app1");
        db.addApp("com.example.app2");
        db.removeApp("com.example.app1");
        assertFalse(db.isAllowed("com.example.app1"));
        assertTrue(db.isAllowed("com.example.app2"));
    }

    @Test
    public void clearAllApiApps() {
        db.addApp("com.example.app1");
        db.addApp("com.example.app2");
        db.clearAllApiApps();
        assertFalse(db.isAllowed("com.example.app1"));
        assertFalse(db.isAllowed("com.example.app2"));
    }

    @Test
    public void getExtAppListEmpty() {
        Set<String> list = db.getExtAppList();
        assertTrue(list.isEmpty());
    }

    @Test
    public void getExtAppListAfterAdd() {
        db.addApp("com.example.app");
        Set<String> list = db.getExtAppList();
        assertTrue(list.contains("com.example.app"));
    }
}