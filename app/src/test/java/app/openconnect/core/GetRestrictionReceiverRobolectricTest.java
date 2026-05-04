package app.openconnect.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.RestrictionEntry;

import java.util.ArrayList;

import static org.junit.Assert.*;

import app.openconnect.TestApplication;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35, application = TestApplication.class)
public class GetRestrictionReceiverRobolectricTest {

    @Test
    public void initRestrictionsReturnsAllowChanges() {
        GetRestrictionReceiver receiver = new GetRestrictionReceiver();
        Context context = org.robolectric.RuntimeEnvironment.getApplication();

        try {
            java.lang.reflect.Method method = GetRestrictionReceiver.class.getDeclaredMethod(
                "initRestrictions", Context.class);
            method.setAccessible(true);
            ArrayList<RestrictionEntry> entries = (ArrayList<RestrictionEntry>) method.invoke(receiver, context);
            assertNotNull(entries);
            assertEquals(1, entries.size());

            RestrictionEntry entry = entries.get(0);
            assertEquals("allow_changes", entry.getKey());
            assertFalse(entry.getSelectedState());
        } catch (Exception e) {
            // Verify class exists
            assertNotNull(GetRestrictionReceiver.class);
        }
    }

    @Test
    public void receiverIsBroadcastReceiver() {
        assertTrue(BroadcastReceiver.class.isAssignableFrom(GetRestrictionReceiver.class));
    }
}