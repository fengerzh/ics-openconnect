package app.openconnect;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import app.openconnect.TestApplication;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35, application = TestApplication.class)
public class FileProviderTest {

    private FileProvider provider;

    @Before
    public void setUp() {
        provider = new FileProvider();
        // Attach the provider to the Robolectric Application context
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        provider.attachInfo(context, null);
    }

    @Test
    public void getTypeReturnsOctetStream() {
        assertEquals("application/octet-stream",
                provider.getType(Uri.parse("content://app.openconnect.files/test.dmp")));
    }

    @Test
    public void getFileFromURIValidDmpFile() throws Exception {
        java.lang.reflect.Method method = FileProvider.class.getDeclaredMethod(
                "getFileFromURI", Uri.class);
        method.setAccessible(true);

        // Create a temp dmp file in cache dir so getFileFromURI finds it
        File cacheDir = provider.getContext().getCacheDir();
        File dmpFile = new File(cacheDir, "1f9563a4-a1f5-2165-255f2219-111823ef.dmp");
        dmpFile.createNewFile();
        dmpFile.deleteOnExit();

        Object result = method.invoke(provider,
                Uri.parse("content://app.openconnect.files/1f9563a4-a1f5-2165-255f2219-111823ef.dmp"));
        assertNotNull(result);
    }

    @Test
    public void getFileFromURIValidDmpLogFile() throws Exception {
        java.lang.reflect.Method method = FileProvider.class.getDeclaredMethod(
                "getFileFromURI", Uri.class);
        method.setAccessible(true);

        File cacheDir = provider.getContext().getCacheDir();
        File logFile = new File(cacheDir, "1f9563a4.dmp.log");
        logFile.createNewFile();
        logFile.deleteOnExit();

        Object result = method.invoke(provider,
                Uri.parse("content://app.openconnect.files/1f9563a4.dmp.log"));
        assertNotNull(result);
    }

    @Test
    public void getFileFromURIInvalidExtension() throws Exception {
        java.lang.reflect.Method method = FileProvider.class.getDeclaredMethod(
                "getFileFromURI", Uri.class);
        method.setAccessible(true);

        try {
            method.invoke(provider,
                    Uri.parse("content://app.openconnect.files/test.txt"));
            fail("Expected FileNotFoundException");
        } catch (java.lang.reflect.InvocationTargetException e) {
            assertTrue(e.getCause() instanceof FileNotFoundException);
        }
    }

    @Test
    public void getFileFromURIUppercaseRejected() throws Exception {
        java.lang.reflect.Method method = FileProvider.class.getDeclaredMethod(
                "getFileFromURI", Uri.class);
        method.setAccessible(true);

        try {
            method.invoke(provider,
                    Uri.parse("content://app.openconnect.files/ABC.dmp"));
            fail("Expected FileNotFoundException");
        } catch (java.lang.reflect.InvocationTargetException e) {
            assertTrue(e.getCause() instanceof FileNotFoundException);
        }
    }

    @Test
    public void getFileFromURINoExtensionRejected() throws Exception {
        java.lang.reflect.Method method = FileProvider.class.getDeclaredMethod(
                "getFileFromURI", Uri.class);
        method.setAccessible(true);

        try {
            method.invoke(provider,
                    Uri.parse("content://app.openconnect.files/somefile"));
            fail("Expected FileNotFoundException");
        } catch (java.lang.reflect.InvocationTargetException e) {
            assertTrue(e.getCause() instanceof FileNotFoundException);
        }
    }

    @Test
    public void insertReturnsNull() {
        assertEquals(null, provider.insert(
                Uri.parse("content://app.openconnect.files/test.dmp"), null));
    }

    @Test
    public void deleteReturnsZero() {
        assertEquals(0, provider.delete(
                Uri.parse("content://app.openconnect.files/test.dmp"), null, null));
    }

    @Test
    public void updateReturnsZero() {
        assertEquals(0, provider.update(
                Uri.parse("content://app.openconnect.files/test.dmp"), null, null, null));
    }
}