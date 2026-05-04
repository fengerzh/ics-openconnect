package app.openconnect.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import app.openconnect.TestApplication;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35, application = TestApplication.class)
public class VPNLogItemRobolectricTest {

    @Test
    public void formatWithNoneReturnsJustMessage() {
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        VPNLogItem item = new VPNLogItem(0, "test msg");
        String result = item.format(context, "none");
        assertEquals("test msg", result);
    }

    @Test
    public void formatWithShortReturnsTimeAndMessage() {
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        VPNLogItem item = new VPNLogItem(0, "short msg");
        String result = item.format(context, "short");
        // short format should have HH:mm:ss prefix
        assertTrue(result.contains("short msg"));
        assertTrue(result.matches("\\d{2}:\\d{2}:\\d{2} short msg"));
    }

    @Test
    public void formatWithLongReturnsDateAndTimeAndMessage() {
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        VPNLogItem item = new VPNLogItem(0, "long msg");
        String result = item.format(context, "long");
        assertTrue(result.contains("long msg"));
        // long format includes yyyy-MM-dd HH:mm:ss
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} long msg"));
    }

    @Test
    public void toStringUsesLongFormat() {
        VPNLogItem item = new VPNLogItem(0, "tostring msg");
        String result = item.toString();
        assertTrue(result.contains("tostring msg"));
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2} .+ tostring msg"));
    }

    @Test
    public void constructorSetsLevelAndMessage() {
        VPNLogItem item = new VPNLogItem(3, "error msg");
        String str = item.toString();
        assertTrue(str.contains("error msg"));
    }

    @Test
    public void formatWithEmptyMessage() {
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        VPNLogItem item = new VPNLogItem(0, "");
        String result = item.format(context, "none");
        assertEquals("", result);
    }

    @Test
    public void formatWithNoneTimeFormatNoPrefix() {
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        VPNLogItem item = new VPNLogItem(0, "hello");
        String result = item.format(context, "none");
        // "none" time format means no timestamp prefix
        assertEquals("hello", result);
    }
}