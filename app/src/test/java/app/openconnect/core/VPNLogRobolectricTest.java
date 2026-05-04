package app.openconnect.core;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import app.openconnect.TestApplication;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35, application = TestApplication.class)
public class VPNLogRobolectricTest {

    private VPNLog log;

    @Before
    public void setUp() {
        log = new VPNLog();
    }

    @Test
    public void saveAndRestoreToFile() throws Exception {
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
    public void restoreFromNonexistentFile() {
        VPNLog log2 = new VPNLog();
        int result = log2.restoreFromFile("/nonexistent/path/file.bin");
        assertEquals(-1, result);
    }

    @Test
    public void saveToInvalidPath() {
        log.add(0, "test");
        int result = log.saveToFile("/nonexistent/dir/file.bin");
        assertEquals(-1, result);
    }
}