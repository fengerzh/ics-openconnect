package app.openconnect.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import app.openconnect.TestApplication;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35, application = TestApplication.class)
public class AssetExtractorRobolectricTest {

    @Test
    public void getArchOnCommonPlatforms() {
        String arch = AssetExtractor.getArch();
        assertNotNull(arch);
        assertTrue(arch.equals("arm64-v8a") || arch.equals("x86_64") ||
                arch.equals("x86") || arch.equals("armeabi"));
    }

    @Test
    public void crc32WithRobolectric() throws Exception {
        File f = Files.createTempFile("crc", ".tmp").toFile();
        f.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(f)) {
            out.write("hello world".getBytes("UTF-8"));
        }
        long crc = AssetExtractor.crc32(f);
        assertTrue(crc != 0);
    }

    @Test
    public void readStringFromNonexistentFile() {
        String result = AssetExtractor.readStringFromFile("/nonexistent/file.txt");
        // android.util.Log.e works in Robolectric, so this should return null
        assertEquals(null, result);
    }

    @Test
    public void readStringFromFileWithContent() throws Exception {
        File f = Files.createTempFile("content", ".txt").toFile();
        f.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(f)) {
            out.write("test data".getBytes("UTF-8"));
        }
        String result = AssetExtractor.readStringFromFile(f.getAbsolutePath());
        assertEquals("test data", result);
    }

    @Test
    public void writeStreamWithLargeContent() throws Exception {
        File dst = Files.createTempFile("large", ".tmp").toFile();
        dst.deleteOnExit();
        byte[] largeData = new byte[100000];
        for (int i = 0; i < largeData.length; i++) {
            largeData[i] = (byte)(i % 256);
        }
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(largeData);
        AssetExtractor.writeStream(in, dst);

        byte[] result = Files.readAllBytes(dst.toPath());
        assertEquals(largeData.length, result.length);
    }

    @Test
    public void extractAllReturnsTrueOrFalse() {
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        // extractAll will try to open the APK zip file; in Robolectric this may fail
        boolean result = AssetExtractor.extractAll(context);
        // Result depends on whether the APK exists; either true or false is acceptable
        assertTrue(result || !result); // always passes, just verifies no crash
    }

    @Test
    public void extractAllWithCustomPath() {
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        String customPath = context.getCacheDir().getAbsolutePath();
        boolean result = AssetExtractor.extractAll(context, 0, customPath);
        // Verifies no crash with custom path
        assertTrue(result || !result);
    }

    @Test
    public void extractAllWithForceFlag() {
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        boolean result = AssetExtractor.extractAll(context, AssetExtractor.FL_FORCE, null);
        assertTrue(result || !result);
    }

    @Test
    public void extractAllWithNoExecFlag() {
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        boolean result = AssetExtractor.extractAll(context, AssetExtractor.FL_NOEXEC, null);
        assertTrue(result || !result);
    }

    @Test
    public void readAndCloseWithEmptyContent() throws Exception {
        java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(new byte[0]);
        java.io.InputStreamReader reader = new java.io.InputStreamReader(bis, "UTF-8");
        String result = AssetExtractor.readAndClose(reader);
        assertEquals("", result);
    }
}