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
public class VPNLogArrayAdapterTest {

    @Test
    public void adapterCountMatchesLogEntries() {
        VPNLog log = new VPNLog();
        Context context = org.robolectric.RuntimeEnvironment.getApplication();

        log.add(0, "entry1");
        log.add(1, "entry2");
        log.add(2, "entry3");

        VPNLog.LogArrayAdapter adapter = log.getArrayAdapter(context);
        assertEquals(3, adapter.getCount());
    }

    @Test
    public void adapterGetItemId() {
        VPNLog log = new VPNLog();
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        log.add(0, "entry");

        VPNLog.LogArrayAdapter adapter = log.getArrayAdapter(context);
        assertEquals(0, adapter.getItemId(0));
    }

    @Test
    public void adapterSetTimeFormat() {
        VPNLog log = new VPNLog();
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        log.add(0, "entry");

        VPNLog.LogArrayAdapter adapter = log.getArrayAdapter(context);
        adapter.setTimeFormat("long");
        // No crash, just verifies the method works
    }

    @Test
    public void adapterGetItemReturnsVPNLogItem() {
        VPNLog log = new VPNLog();
        Context context = org.robolectric.RuntimeEnvironment.getApplication();
        log.add(0, "test message");

        VPNLog.LogArrayAdapter adapter = log.getArrayAdapter(context);
        Object item = adapter.getItem(0);
        assertNotNull(item);
        assertTrue(item instanceof VPNLogItem);
    }
}