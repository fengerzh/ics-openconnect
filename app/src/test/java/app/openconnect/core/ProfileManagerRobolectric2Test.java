package app.openconnect.core;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import app.openconnect.TestApplication;
import app.openconnect.VpnProfile;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35, application = TestApplication.class)
public class ProfileManagerRobolectric2Test {

    private Context context;

    @Before
    public void setUp() {
        context = org.robolectric.RuntimeEnvironment.getApplication();
        ProfileManager.init(context);
    }

    @Test
    public void getPrefsNameReturnsPrefixPlusUuid() {
        assertEquals("profile-test-uuid", ProfileManager.getPrefsName("test-uuid"));
    }

    @Test
    public void getReturnsNullForNullKey() {
        assertNull(ProfileManager.get(null));
    }

    @Test
    public void getReturnsNullForUnknownKey() {
        assertNull(ProfileManager.get("nonexistent-uuid"));
    }

    @Test
    public void createProfileAndRetrieve() {
        VpnProfile profile = ProfileManager.create("vpn.example.com");
        assertNotNull(profile);
        assertNotNull(profile.getUUIDString());
        assertTrue(profile.getName().length() > 0);

        // Retrieve by UUID
        VpnProfile retrieved = ProfileManager.get(profile.getUUIDString());
        assertEquals(profile.getUUIDString(), retrieved.getUUIDString());
    }

    @Test
    public void getProfilesReturnsCreatedProfiles() {
        VpnProfile p1 = ProfileManager.create("vpn1.example.com");
        VpnProfile p2 = ProfileManager.create("vpn2.example.com");

        Collection<VpnProfile> profiles = ProfileManager.getProfiles();
        assertTrue(profiles.size() >= 2);
    }

    @Test
    public void getProfileByName() {
        VpnProfile profile = ProfileManager.create("vpn.example.com");
        VpnProfile found = ProfileManager.getProfileByName(profile.getName());
        assertNotNull(found);
        assertEquals(profile.getUUIDString(), found.getUUIDString());
    }

    @Test
    public void getProfileByNameNotFound() {
        assertNull(ProfileManager.getProfileByName("Nonexistent Name"));
    }

    @Test
    public void createProfileWithDuplicateNameGetsSuffix() {
        VpnProfile p1 = ProfileManager.create("vpn.example.com");
        VpnProfile p2 = ProfileManager.create("vpn.example.com");

        // Second profile should have "(1)" suffix
        assertTrue(p2.getName().contains("(1)"));
        assertTrue(!p1.getUUIDString().equals(p2.getUUIDString()));
    }

    @Test
    public void deleteProfile() {
        VpnProfile profile = ProfileManager.create("vpn3.example.com");
        String uuid = profile.getUUIDString();
        boolean result = ProfileManager.delete(uuid);
        assertTrue(result);
        assertNull(ProfileManager.get(uuid));
    }
}