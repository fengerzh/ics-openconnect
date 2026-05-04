package app.openconnect.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Context;
import android.content.SharedPreferences;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import app.openconnect.TestApplication;
import app.openconnect.VpnProfile;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35, application = TestApplication.class)
public class OpenConnectManagementThreadDecodeBase64Test {

    private OpenConnectManagementThread createThread() {
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        SharedPreferences prefs = context.getSharedPreferences("test", Context.MODE_PRIVATE);
        // VpnProfile constructor reads UUID and name from prefs, so set them
        prefs.edit()
            .putString("profile_uuid", "00000000-0000-0000-0000-000000000001")
            .putString("profile_name", "Test")
            .commit();
        VpnProfile profile = new VpnProfile(prefs);
        return new OpenConnectManagementThread(context, profile, null);
    }

    @Test
    public void decodeBase64ValidInput() {
        byte[] result = createThread().decodeBase64("SGVsbG8=");
        assertEquals("Hello", new String(result));
    }

    @Test
    public void decodeBase64EmptyString() {
        try {
            createThread().decodeBase64("");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected - empty string doesn't match regex
        }
    }

    @Test
    public void decodeBase64InvalidChars() {
        try {
            createThread().decodeBase64("invalid!chars#here");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("invalid chars", e.getMessage());
        }
    }

    @Test
    public void decodeBase64WithSpacesInInput() {
        // Space character is not valid in base64 encoding
        try {
            createThread().decodeBase64("SGVs bG8=");
            fail("Expected IllegalArgumentException for space character");
        } catch (IllegalArgumentException e) {
            assertEquals("invalid chars", e.getMessage());
        }
    }

    @Test
    public void decodeBase64WithNewlines() {
        byte[] result = createThread().decodeBase64("SGVsbG8=\n");
        assertEquals("Hello", new String(result));
    }

    @Test
    public void decodeBase64WithPemHeader() {
        try {
            createThread().decodeBase64("-----BEGIN CERT-----");
            fail("Expected IllegalArgumentException for PEM header");
        } catch (IllegalArgumentException e) {
            assertEquals("invalid chars", e.getMessage());
        }
    }

    @Test
    public void decodeBase64SimpleBase64() {
        byte[] result = createThread().decodeBase64("AQID");
        assertArrayEquals(new byte[]{1, 2, 3}, result);
    }

    @Test
    public void decodeBase64WithPadding() {
        byte[] result = createThread().decodeBase64("AA==");
        assertArrayEquals(new byte[]{0}, result);
    }

    @Test
    public void decodeBase64WithSlashAndPlus() {
        byte[] result = createThread().decodeBase64("A+B/");
        assertEquals(3, result.length);
    }
}