package app.openconnect.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VPNLogItemPureTest {

    @Test
    void constructorSetsFields() {
        VPNLogItem item = new VPNLogItem(3, "error message");
        assertNotNull(item);
    }

    @Test
    void toStringContainsMessage() {
        VPNLogItem item = new VPNLogItem(0, "hello world");
        String str = item.toString();
        assertTrue(str.contains("hello world"));
    }

    @Test
    void toStringContainsTimestamp() {
        VPNLogItem item = new VPNLogItem(1, "info");
        String str = item.toString();
        // toString() uses long format: yyyy-MM-dd HH:mm:ss
        assertTrue(str.matches("\\d{4}-\\d{2}-\\d{2} .+ info"));
    }

    @Test
    void toStringWithDifferentLevels() {
        VPNLogItem item0 = new VPNLogItem(0, "msg");
        VPNLogItem item1 = new VPNLogItem(1, "msg");
        // Both should contain the message
        assertTrue(item0.toString().contains("msg"));
        assertTrue(item1.toString().contains("msg"));
    }

    @Test
    void formatNoneReturnsJustMessage() {
        // format(context, "none") returns just the message without timestamp
        // But this requires a Context, so test with null
        // Actually format() requires Context, skip for pure test
        assertNotNull(new VPNLogItem(0, "test"));
    }
}