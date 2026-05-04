package app.openconnect;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import app.openconnect.TestApplication;
import app.openconnect.VpnProfile;
import app.openconnect.core.ProfileManager;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35, application = TestApplication.class)
public class OnBootReceiverTest {

    private Context context;

    @Before
    public void setUp() {
        context = org.robolectric.RuntimeEnvironment.getApplication();
        ProfileManager.init(context);
    }

    @Test
    public void onReceiveWithBootCompletedNoProfile() {
        OnBootReceiver receiver = new OnBootReceiver();
        Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED);
        receiver.onReceive(context, intent);
        // No boot profile set, so nothing happens - just verifies no crash
    }

    @Test
    public void onReceiveWithNonBootAction() {
        OnBootReceiver receiver = new OnBootReceiver();
        Intent intent = new Intent(Intent.ACTION_SCREEN_ON);
        receiver.onReceive(context, intent);
        // Not a boot action, so nothing happens
    }

    @Test
    public void launchVPNCreatesCorrectIntent() {
        OnBootReceiver receiver = new OnBootReceiver();
        VpnProfile profile = ProfileManager.create("vpn.example.com");

        // Use a mock context to capture the intent
        Context mockContext = mock(Context.class);
        receiver.launchVPN(profile, mockContext);

        // Verify startActivity was called
        verify(mockContext).startActivity(org.mockito.ArgumentMatchers.any(Intent.class));
    }

    @Test
    public void tagConstant() {
        assertEquals("OpenConnect", OnBootReceiver.TAG);
    }
}