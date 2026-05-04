package app.openconnect.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VPNLogPureTest {

    private VPNLog log;

    @BeforeEach
    void setUp() {
        log = new VPNLog();
    }

    @Test
    void addIncreasesDumpContent() {
        String empty = log.dump();
        assertEquals("", empty);
        log.add(0, "entry1");
        assertTrue(log.dump().contains("entry1"));
    }

    @Test
    void dumpReturnsAllEntries() {
        log.add(0, "alpha");
        log.add(1, "beta");
        log.add(2, "gamma");
        String dump = log.dump();
        assertTrue(dump.contains("alpha"));
        assertTrue(dump.contains("beta"));
        assertTrue(dump.contains("gamma"));
    }

    @Test
    void clearThenAdd() {
        log.add(0, "old");
        log.clear();
        assertEquals("", log.dump());
        log.add(0, "new");
        assertTrue(log.dump().contains("new"));
        assertFalse(log.dump().contains("old"));
    }

    @Test
    void overflowKeepsRecentEntries() {
        for (int i = 0; i < 600; i++) {
            log.add(0, "entry" + i);
        }
        String dump = log.dump();
        // Old entries should be removed
        assertFalse(dump.contains("entry0"));
        assertFalse(dump.contains("entry87"));
        // Recent entries should be kept
        assertTrue(dump.contains("entry599"));
    }

    @Test
    void dumpLastAfterNewInstance() {
        log.add(0, "instance_entry");
        String dumpLast = VPNLog.dumpLast();
        assertTrue(dumpLast.contains("instance_entry"));
    }

    @Test
    void tagConstant() {
        assertEquals("OpenConnect", VPNLog.TAG);
    }

    @Test
    void defaultTimeFormatConstant() {
        assertEquals("short", VPNLog.DEFAULT_TIME_FORMAT);
    }
}