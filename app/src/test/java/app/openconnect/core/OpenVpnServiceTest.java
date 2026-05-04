package app.openconnect.core;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenVpnServiceTest {

    @Test
    void humanReadableByteCountZeroBytes() {
        assertEquals("0 B", OpenVpnService.humanReadableByteCount(0, false));
    }

    @Test
    void humanReadableByteCountZeroBits() {
        assertEquals("0 bit", OpenVpnService.humanReadableByteCount(0, true));
    }

    @Test
    void humanReadableByteCountSmallBytes() {
        assertEquals("512 B", OpenVpnService.humanReadableByteCount(512, false));
    }

    @Test
    void humanReadableByteCountSmallBits() {
        // 512 * 8 = 4096, but 4096 >= 1000 (unit for mbit), so it formats as kbit
        String result = OpenVpnService.humanReadableByteCount(512, true);
        assertTrue(result.contains("kbit"));
    }

    @Test
    void humanReadableByteCountKilobytes() {
        String result = OpenVpnService.humanReadableByteCount(2048, false);
        assertTrue(result.contains("KB") || result.contains("K"));
        assertTrue(result.startsWith("2"));
    }

    @Test
    void humanReadableByteCountMegabytes() {
        String result = OpenVpnService.humanReadableByteCount(2 * 1024 * 1024, false);
        assertTrue(result.contains("MB") || result.contains("M"));
    }

    @Test
    void humanReadableByteCountGigabytes() {
        String result = OpenVpnService.humanReadableByteCount(3L * 1024 * 1024 * 1024, false);
        assertTrue(result.contains("GB") || result.contains("G"));
    }

    @Test
    void humanReadableByteCountKilobits() {
        String result = OpenVpnService.humanReadableByteCount(125, true);
        // 125 * 8 = 1000 bits = 1.0 kbit
        assertTrue(result.contains("kbit"));
    }

    @Test
    void humanReadableByteCountMegabits() {
        String result = OpenVpnService.humanReadableByteCount(125000, true);
        // 125000 * 8 = 1000000 bits = 1.0 Mbit
        assertTrue(result.contains("Mbit"));
    }

    @Test
    void humanReadableByteCountBitSuffix() {
        String result = OpenVpnService.humanReadableByteCount(100, true);
        assertTrue(result.endsWith("bit"));
        assertTrue(!result.contains("kbit") && !result.contains("Mbit"));
    }

    @Test
    void humanReadableByteCountByteSuffix() {
        String result = OpenVpnService.humanReadableByteCount(100, false);
        assertTrue(result.endsWith("B"));
        assertTrue(!result.contains("KB") && !result.contains("MB"));
    }

    @Test
    void formatElapsedTimeZero() {
        String result = OpenVpnService.formatElapsedTime(System.currentTimeMillis());
        assertEquals("00:00", result);
    }

    @Test
    void formatElapsedTimeSecondsOnly() {
        long tenSecondsAgo = System.currentTimeMillis() - 10 * 1000;
        String result = OpenVpnService.formatElapsedTime(tenSecondsAgo);
        assertEquals("00:10", result);
    }

    @Test
    void formatElapsedTimeMinutesAndSeconds() {
        long fiveMinAgo = System.currentTimeMillis() - (5 * 60 + 30) * 1000;
        String result = OpenVpnService.formatElapsedTime(fiveMinAgo);
        assertEquals("05:30", result);
    }

    @Test
    void formatElapsedTimeHoursMinutesSeconds() {
        long twoHoursAgo = System.currentTimeMillis() - (2 * 3600 + 5 * 60 + 30) * 1000;
        String result = OpenVpnService.formatElapsedTime(twoHoursAgo);
        assertEquals("02:05:30", result);
    }

    @Test
    void formatElapsedTimeDaysHoursMinutesSeconds() {
        long threeDaysAgo = System.currentTimeMillis() -
                (3 * 86400 + 2 * 3600 + 5 * 60 + 30) * 1000;
        String result = OpenVpnService.formatElapsedTime(threeDaysAgo);
        assertTrue(result.startsWith("3:"));
        assertTrue(result.contains("02:05:30"));
    }

    @Test
    void formatElapsedTimeOneHour() {
        long oneHourAgo = System.currentTimeMillis() - 3600 * 1000;
        String result = OpenVpnService.formatElapsedTime(oneHourAgo);
        assertEquals("01:00:00", result);
    }
}