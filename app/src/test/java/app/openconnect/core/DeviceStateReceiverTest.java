package app.openconnect.core;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Intent;
import android.content.SharedPreferences;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import app.openconnect.TestApplication;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35, application = TestApplication.class)
public class DeviceStateReceiverTest {

    private OpenVPNManagement mockManagement;
    private SharedPreferences mockPrefs;
    private DeviceStateReceiver receiver;

    @Before
    public void setUp() {
        mockManagement = Mockito.mock(OpenVPNManagement.class);
        mockPrefs = Mockito.mock(SharedPreferences.class);
        // Default: screen off pause disabled, netchange reconnect enabled
        Mockito.when(mockPrefs.getBoolean("screenoff", false)).thenReturn(false);
        Mockito.when(mockPrefs.getBoolean("netchangereconnect", true)).thenReturn(true);
        receiver = new DeviceStateReceiver(mockManagement, mockPrefs);
    }

    @Test
    public void setKeepaliveActiveDoesNotPauseWhenScreenOn() {
        receiver.setKeepalive(true);
        verify(mockManagement, never()).pause();
    }

    @Test
    public void setKeepaliveInactiveDoesNotPauseWhenScreenOn() {
        receiver.setKeepalive(false);
        verify(mockManagement, never()).pause();
    }

    @Test
    public void screenOffTriggersPauseWhenEnabled() {
        // Enable pause on screen off
        Mockito.when(mockPrefs.getBoolean("screenoff", false)).thenReturn(true);
        receiver = new DeviceStateReceiver(mockManagement, mockPrefs);

        Intent intent = new Intent(Intent.ACTION_SCREEN_OFF);
        receiver.onReceive(null, intent);

        verify(mockManagement).pause();
    }

    @Test
    public void screenOnResumesAfterScreenOffPause() {
        // Enable pause on screen off
        Mockito.when(mockPrefs.getBoolean("screenoff", false)).thenReturn(true);
        receiver = new DeviceStateReceiver(mockManagement, mockPrefs);

        // First: screen off -> pause
        Intent offIntent = new Intent(Intent.ACTION_SCREEN_OFF);
        receiver.onReceive(null, offIntent);
        verify(mockManagement).pause();

        // Then: screen on -> resume
        Intent onIntent = new Intent(Intent.ACTION_SCREEN_ON);
        receiver.onReceive(null, onIntent);
        verify(mockManagement).resume();
    }

    @Test
    public void screenOffDoesNotPauseWhenKeepaliveActive() {
        // Enable pause on screen off
        Mockito.when(mockPrefs.getBoolean("screenoff", false)).thenReturn(true);
        receiver = new DeviceStateReceiver(mockManagement, mockPrefs);

        // Set keepalive active
        receiver.setKeepalive(true);

        // Screen off should NOT pause because keepalive is active
        Intent intent = new Intent(Intent.ACTION_SCREEN_OFF);
        receiver.onReceive(null, intent);

        verify(mockManagement, never()).pause();
    }

    @Test
    public void keepaliveDeactivatesAllowsPause() {
        // Enable pause on screen off
        Mockito.when(mockPrefs.getBoolean("screenoff", false)).thenReturn(true);
        receiver = new DeviceStateReceiver(mockManagement, mockPrefs);

        // First: keepalive active, screen off -> no pause
        receiver.setKeepalive(true);
        Intent offIntent = new Intent(Intent.ACTION_SCREEN_OFF);
        receiver.onReceive(null, offIntent);
        verify(mockManagement, never()).pause();

        // Then: keepalive deactivated -> should pause
        receiver.setKeepalive(false);
        verify(mockManagement).pause();
    }
}