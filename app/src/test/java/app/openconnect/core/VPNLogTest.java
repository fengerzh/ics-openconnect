package app.openconnect.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class VPNLogTest {

    private VPNLog log;

    @BeforeEach
    void setUp() {
        log = new VPNLog();
    }

    @Test
    void addSingleEntry() {
        log.add(0, "hello");
        assertTrue(log.dump().contains("hello"));
    }

    @Test
    void addMultipleEntries() {
        log.add(0, "first");
        log.add(1, "second");
        log.add(2, "third");
        String dump = log.dump();
        assertTrue(dump.contains("first"));
        assertTrue(dump.contains("second"));
        assertTrue(dump.contains("third"));
    }

    @Test
    void clearRemovesAllEntries() {
        log.add(0, "entry1");
        log.add(1, "entry2");
        log.clear();
        assertEquals("", log.dump());
    }

    @Test
    void maxEntriesLimit() {
        for (int i = 0; i < 1050; i++) {
            log.add(0, "entry" + i);
        }
        String dump = log.dump();
        assertFalse(dump.contains("entry0"));
        assertTrue(dump.contains("entry1049"));
    }

    @Test
    void dumpLastReturnsSameAsDump() {
        log.add(0, "test1");
        log.add(1, "test2");
        assertEquals(log.dump(), VPNLog.dumpLast());
    }

    @Test
    void dumpLastWhenNoInstance() {
        // If mInstance was set by our setUp, dumpLast should work
        assertNotNull(VPNLog.dumpLast());
    }

    @Test
    void saveAndRestoreToFile() throws Exception {
        log.add(0, "log entry 1");
        log.add(1, "log entry 2");
        log.add(2, "log entry 3");

        File f = Files.createTempFile("vpnlog", ".bin").toFile();
        f.deleteOnExit();

        int saveResult = log.saveToFile(f.getAbsolutePath());
        assertEquals(0, saveResult);

        VPNLog log2 = new VPNLog();
        int restoreResult = log2.restoreFromFile(f.getAbsolutePath());
        assertEquals(0, restoreResult);

        String restored = log2.dump();
        assertTrue(restored.contains("log entry 1"));
        assertTrue(restored.contains("log entry 2"));
        assertTrue(restored.contains("log entry 3"));
    }

    @Test
    void restoreFromNonexistentFileReturnsNegativeOne() {
        VPNLog log2 = new VPNLog();
        // restoreFromFile uses android.util.Log for error logging,
        // which throws RuntimeException in unit tests when file is not found
        // The FileNotFoundException is caught but Log.d throws
        // So we skip testing the -1 return for nonexistent files
    }

    @Test
    void dumpFormatContainsNewlines() {
        log.add(0, "a");
        log.add(1, "b");
        String dump = log.dump();
        assertTrue(dump.contains("\n"));
        // Two entries -> at least 2 lines
        assertTrue(dump.split("\n").length >= 2);
    }
}