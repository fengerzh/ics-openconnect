package app.openconnect.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35, application = app.openconnect.TestApplication.class)
public class ProfileManagerRobolectricTest {

    @Test
    public void makeProfNameHttpsUrl() {
        assertEquals("Example", ProfileManager.makeProfName("https://vpn.example.com", 0));
    }

    @Test
    public void makeProfNameUrlWithPath() {
        assertEquals("Example", ProfileManager.makeProfName("vpn.example.com/path", 0));
    }

    @Test
    public void makeProfNameUrlWithoutScheme() {
        assertEquals("Example", ProfileManager.makeProfName("vpn.example.com/group", 0));
    }

    @Test
    public void makeProfNameInvalidUrlReturnsOriginal() {
        assertEquals("https:///", ProfileManager.makeProfName("https:///", 0));
    }

    @Test
    public void makeProfNameHttpUrlReturnsHttp() {
        // "http://vpn.example.com" -> doesn't match "https://.*"
        // so prepends "https://" -> "https://http://vpn.example.com"
        // Uri.parse extracts host "http" (scheme is https, authority is "http")
        // capitalize("http") = "HTTP" (4 chars -> uppercase)
        assertEquals("HTTP", ProfileManager.makeProfName("http://vpn.example.com", 0));
    }
}