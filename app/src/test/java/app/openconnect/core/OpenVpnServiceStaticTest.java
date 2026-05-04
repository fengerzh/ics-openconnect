package app.openconnect.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenVpnServiceStaticTest {

    @Test
    void humanReadableByteCountZeroBytes() {
        assertEquals("0 B", OpenVpnService.humanReadableByteCount(0, false));
    }

    @Test
    void humanReadableByteCountZeroBits() {
        assertEquals("0 bit", OpenVpnService.humanReadableByteCount(0, true));
    }

    @Test
    void humanReadableByteCountOneByte() {
        assertEquals("1 B", OpenVpnService.humanReadableByteCount(1, false));
    }

    @Test
    void humanReadableByteCountOneKilobyte() {
        String result = OpenVpnService.humanReadableByteCount(1024, false);
        assertTrue(result.contains("K"));
        assertTrue(result.contains("B"));
    }

    @Test
    void humanReadableByteCountOneMegabyte() {
        String result = OpenVpnService.humanReadableByteCount(1024 * 1024, false);
        assertTrue(result.contains("M"));
        assertTrue(result.contains("B"));
    }

    @Test
    void humanReadableByteCountOneGigabyte() {
        String result = OpenVpnService.humanReadableByteCount(1024L * 1024 * 1024, false);
        assertTrue(result.contains("G"));
        assertTrue(result.contains("B"));
    }

    @Test
    void humanReadableByteCountSmallBits() {
        // 512 bytes * 8 = 4096 bits -> kbit range
        String result = OpenVpnService.humanReadableByteCount(512, true);
        assertTrue(result.contains("kbit") || result.contains("bit"));
    }

    @Test
    void humanReadableByteCountKilobits() {
        // 1000 bytes * 8 = 8000 bits -> kbit
        String result = OpenVpnService.humanReadableByteCount(1000, true);
        assertTrue(result.contains("kbit"));
    }

    @Test
    void humanReadableByteCountMegabits() {
        // 1MB in bits = 8 Mbit
        String result = OpenVpnService.humanReadableByteCount(1024 * 1024, true);
        assertTrue(result.contains("Mbit") || result.contains("bit"));
    }

    @Test
    void formatElapsedTimeZero() {
        // startTime=0 means elapsed time is current time
        String result = OpenVpnService.formatElapsedTime(0);
        assertNotNull(result);
        assertTrue(result.contains(":"));
    }

    @Test
    void formatElapsedTimeSeconds() {
        // Use a time close to now
        long now = System.currentTimeMillis();
        String result = OpenVpnService.formatElapsedTime(now - 5000); // 5 seconds ago
        assertTrue(result.contains(":"));
    }

    @Test
    void formatElapsedTimeMinutes() {
        long now = System.currentTimeMillis();
        String result = OpenVpnService.formatElapsedTime(now - 120000); // 2 minutes ago
        assertTrue(result.contains(":"));
    }

    @Test
    void formatElapsedTimeHours() {
        long now = System.currentTimeMillis();
        String result = OpenVpnService.formatElapsedTime(now - 3600000); // 1 hour ago
        assertTrue(result.contains(":"));
        // Should have hours format like "01:00:00"
        assertTrue(result.length() >= 5);
    }

    @Test
    void formatElapsedTimeDays() {
        long now = System.currentTimeMillis();
        String result = OpenVpnService.formatElapsedTime(now - 86400000L); // 1 day ago
        assertTrue(result.contains(":"));
        // Should have days format like "1:00:00:00"
        assertTrue(result.length() >= 8);
    }

    @Test
    void serviceActionConstants() {
        assertEquals("app.openconnect.START_SERVICE", OpenVpnService.START_SERVICE);
        assertEquals("app.openconnect.START_SERVICE_STICKY", OpenVpnService.START_SERVICE_STICKY);
        assertEquals("app.openconnect.NOTIFICATION_ALWAYS_VISIBLE", OpenVpnService.ALWAYS_SHOW_NOTIFICATION);
        assertEquals("app.openconnect.VPN_STATUS", OpenVpnService.ACTION_VPN_STATUS);
        assertEquals("app.openconnect.connectionState", OpenVpnService.EXTRA_CONNECTION_STATE);
        assertEquals("app.openconnect.UUID", OpenVpnService.EXTRA_UUID);
    }

    @Test
    void tagConstant() {
        assertEquals("OpenConnect", OpenVpnService.TAG);
    }
}