package app.openconnect;

import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35, application = app.openconnect.TestApplication.class)
public class VpnProfileRobolectricTest {

    private SharedPreferences prefs;

    @Before
    public void setUp() {
        prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(
                org.robolectric.RuntimeEnvironment.getApplication());
    }

    @Test
    public void constructorWithPrefsUuidAndName() {
        VpnProfile profile = new VpnProfile(prefs, "550e8400-e29b-41d4-a716-446655440000", "TestVPN");
        assertEquals("TestVPN", profile.getName());
        assertEquals("550e8400-e29b-41d4-a716-446655440000", profile.getUUIDString());
        assertTrue(profile.isValid());
    }

    @Test
    public void constructorWithPrefsOnly() {
        VpnProfile profile1 = new VpnProfile(prefs, "550e8400-e29b-41d4-a716-446655440001", "AnotherVPN");
        VpnProfile profile2 = new VpnProfile(prefs);
        assertEquals("AnotherVPN", profile2.getName());
        assertEquals("550e8400-e29b-41d4-a716-446655440001", profile2.getUUIDString());
    }

    @Test
    public void invalidWhenNameIsNull() {
        prefs.edit().clear().commit();
        prefs.edit().putString("profile_uuid", "550e8400-e29b-41d4-a716-446655440000").commit();
        VpnProfile profile = new VpnProfile(prefs);
        assertFalse(profile.isValid());
    }
}