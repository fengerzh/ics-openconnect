package app.openconnect;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import app.openconnect.TestApplication;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35, application = TestApplication.class)
public class VpnProfileRobolectric2Test {

    private Context context;
    private SharedPreferences prefs;

    @Before
    public void setUp() {
        context = org.robolectric.RuntimeEnvironment.getApplication();
        prefs = context.getSharedPreferences("test_profile", Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }

    @Test
    public void constructorWithPrefsUuidName() {
        String uuid = UUID.randomUUID().toString();
        VpnProfile profile = new VpnProfile(prefs, uuid, "MyVPN");
        assertEquals("MyVPN", profile.getName());
        assertEquals(uuid, profile.getUUIDString());
        assertTrue(profile.isValid());
    }

    @Test
    public void constructorWithPrefsOnly() {
        String uuid = UUID.randomUUID().toString();
        prefs.edit()
            .putString("profile_uuid", uuid)
            .putString("profile_name", "SavedVPN")
            .commit();
        VpnProfile profile = new VpnProfile(prefs);
        assertEquals("SavedVPN", profile.getName());
        assertEquals(uuid, profile.getUUIDString());
        assertTrue(profile.isValid());
    }

    @Test
    public void constructorWithPrefsOnlyInvalid() {
        // No UUID or name in prefs
        VpnProfile profile = new VpnProfile(prefs);
        assertFalse(profile.isValid());
    }

    @Test
    public void constructorWithNameAndUuid() {
        String uuid = UUID.randomUUID().toString();
        VpnProfile profile = new VpnProfile("TestVPN", uuid);
        assertEquals("TestVPN", profile.getName());
        assertEquals(uuid, profile.getUUIDString());
        assertTrue(profile.isValid());
    }

    @Test
    public void toStringReturnsName() {
        String uuid = UUID.randomUUID().toString();
        VpnProfile profile = new VpnProfile("MyVPN", uuid);
        assertEquals("MyVPN", profile.toString());
    }

    @Test
    public void compareToOrdersByName() {
        String uuid1 = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        VpnProfile p1 = new VpnProfile("Alpha", uuid1);
        VpnProfile p2 = new VpnProfile("Beta", uuid2);
        assertTrue(p1.compareTo(p2) < 0);
        assertTrue(p2.compareTo(p1) > 0);
    }

    @Test
    public void compareToEqualNames() {
        String uuid1 = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        VpnProfile p1 = new VpnProfile("Same", uuid1);
        VpnProfile p2 = new VpnProfile("Same", uuid2);
        assertEquals(0, p1.compareTo(p2));
    }

    @Test
    public void inlineTagConstant() {
        assertEquals("[[INLINE]]", VpnProfile.INLINE_TAG);
    }

    @Test
    public void isValidWithNullName() {
        // Profile with UUID but no name
        String uuid = UUID.randomUUID().toString();
        prefs.edit().putString("profile_uuid", uuid).commit();
        VpnProfile profile = new VpnProfile(prefs);
        assertFalse(profile.isValid());
    }

    @Test
    public void isValidWithNullUuid() {
        // Profile with name but no UUID
        prefs.edit().putString("profile_name", "Test").commit();
        VpnProfile profile = new VpnProfile(prefs);
        assertFalse(profile.isValid());
    }

    @Test
    public void getUUIDReturnsCorrectUUID() {
        String uuid = UUID.randomUUID().toString();
        VpnProfile profile = new VpnProfile("Test", uuid);
        assertNotNull(profile.getUUID());
        assertEquals(UUID.fromString(uuid), profile.getUUID());
    }

    @Test
    public void mPrefsIsSet() {
        String uuid = UUID.randomUUID().toString();
        VpnProfile profile = new VpnProfile(prefs, uuid, "Test");
        assertEquals(prefs, profile.mPrefs);
    }
}