package app.openconnect.fragments;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import app.openconnect.TestApplication;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35, application = TestApplication.class)
public class SendDumpFragmentTest {

    private Context context;
    private File cacheDir;

    @Before
    public void setUp() {
        context = org.robolectric.RuntimeEnvironment.getApplication();
        cacheDir = context.getCacheDir();
        // Clean up any existing .dmp files
        for (File f : cacheDir.listFiles()) {
            if (f.getName().endsWith(".dmp")) {
                f.delete();
            }
        }
    }

    @Test
    public void getLatestDumpReturnsNullWhenNoDumps() {
        // No .dmp files exist
        File result = SendDumpFragment.getLastestDump(context);
        assertNull(result);
    }

    @Test
    public void getLatestDumpReturnsDumpFile() throws Exception {
        // Create a .dmp file
        File dmp = new File(cacheDir, "1f9563a4-a1f5-2165-255f2219-111823ef.dmp");
        try (FileOutputStream out = new FileOutputStream(dmp)) {
            out.write("crash data".getBytes());
        }
        dmp.deleteOnExit();

        File result = SendDumpFragment.getLastestDump(context);
        assertNotNull(result);
        assertEquals(dmp.getName(), result.getName());
    }

    @Test
    public void getLatestDumpReturnsMostRecent() throws Exception {
        // Create two .dmp files, one newer than the other
        File oldDmp = new File(cacheDir, "old.dmp");
        try (FileOutputStream out = new FileOutputStream(oldDmp)) {
            out.write("old crash".getBytes());
        }
        oldDmp.setLastModified(System.currentTimeMillis() - 100000);
        oldDmp.deleteOnExit();

        File newDmp = new File(cacheDir, "new.dmp");
        try (FileOutputStream out = new FileOutputStream(newDmp)) {
            out.write("new crash".getBytes());
        }
        newDmp.deleteOnExit();

        File result = SendDumpFragment.getLastestDump(context);
        assertNotNull(result);
        assertEquals("new.dmp", result.getName());
    }

    @Test
    public void getLatestDumpIgnoresNonDumpFiles() throws Exception {
        // Create a .txt file (not .dmp)
        File txt = new File(cacheDir, "test.txt");
        try (FileOutputStream out = new FileOutputStream(txt)) {
            out.write("text".getBytes());
        }
        txt.deleteOnExit();

        File result = SendDumpFragment.getLastestDump(context);
        assertNull(result);
    }
}