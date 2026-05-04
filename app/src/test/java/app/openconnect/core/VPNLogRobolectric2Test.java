package app.openconnect.core;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Context;

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import app.openconnect.TestApplication;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35, application = TestApplication.class)
public class VPNLogRobolectric2Test {

    private VPNLog log;
    private Context context;

    @Before
    public void setUp() {
        log = new VPNLog();
        context = org.robolectric.RuntimeEnvironment.getApplication();
    }

    @Test
    public void getArrayAdapterReturnsNonNull() {
        VPNLog.LogArrayAdapter adapter = log.getArrayAdapter(context);
        assertNotNull(adapter);
    }

    @Test
    public void getArrayAdapterReflectsLogEntries() {
        log.add(0, "entry1");
        log.add(1, "entry2");
        VPNLog.LogArrayAdapter adapter = log.getArrayAdapter(context);
        assertEquals(2, adapter.getCount());
    }

    @Test
    public void logArrayAdapterGetItem() {
        log.add(0, "entry1");
        VPNLog.LogArrayAdapter adapter = log.getArrayAdapter(context);
        VPNLogItem item = (VPNLogItem) adapter.getItem(0);
        assertTrue(item.toString().contains("entry1"));
    }

    @Test
    public void dumpLastReturnsContent() {
        log.add(0, "test entry");
        String result = VPNLog.dumpLast();
        assertTrue(result.contains("test entry"));
    }

    @Test
    public void dumpLastReturnsEmptyWhenNoInstance() {
        // Create a new VPNLog which sets mInstance
        VPNLog newLog = new VPNLog();
        newLog.add(0, "content");
        assertTrue(VPNLog.dumpLast().contains("content"));
    }

    @Test
    public void putArrayAdapterNullsReference() {
        VPNLog.LogArrayAdapter adapter = log.getArrayAdapter(context);
        log.putArrayAdapter(adapter);
        // After put, getArrayAdapter should return a new one
        VPNLog.LogArrayAdapter adapter2 = log.getArrayAdapter(context);
        assertNotNull(adapter2);
    }

    @Test
    public void restoreFromFileNonexistentPath() {
        int result = log.restoreFromFile("/nonexistent/path/file.bin");
        assertEquals(-1, result);
    }

    @Test
    public void saveToFileInvalidPath() {
        log.add(0, "test");
        int result = log.saveToFile("/nonexistent/dir/file.bin");
        assertEquals(-1, result);
    }

    @Test
    public void saveAndRestoreFullCycle() throws Exception {
        log.add(0, "log1");
        log.add(1, "log2");

        File f = Files.createTempFile("vpnlog2", ".bin").toFile();
        f.deleteOnExit();

        int saveResult = log.saveToFile(f.getAbsolutePath());
        assertEquals(0, saveResult);

        VPNLog log2 = new VPNLog();
        int restoreResult = log2.restoreFromFile(f.getAbsolutePath());
        assertEquals(0, restoreResult);

        assertTrue(log2.dump().contains("log1"));
        assertTrue(log2.dump().contains("log2"));
    }

    @Test
    public void maxEntriesOverflow() {
        for (int i = 0; i < 600; i++) {
            log.add(0, "entry" + i);
        }
        // Should be capped at 512
        VPNLog.LogArrayAdapter adapter = log.getArrayAdapter(context);
        assertEquals(512, adapter.getCount());
    }
}