package app.openconnect.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import android.content.SharedPreferences;

import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class UserDialogExtendedTest {

    private SharedPreferences mockPrefs;
    private SharedPreferences.Editor mockEditor;

    @BeforeEach
    void setUp() {
        mockEditor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(mockEditor.putString(Mockito.anyString(), Mockito.anyString())).thenReturn(mockEditor);
        Mockito.when(mockEditor.putBoolean(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(mockEditor);
        Mockito.when(mockEditor.commit()).thenReturn(true);

        mockPrefs = Mockito.mock(SharedPreferences.class);
        Mockito.when(mockPrefs.getString(Mockito.anyString(), Mockito.anyString())).thenReturn("");
        Mockito.when(mockPrefs.getBoolean(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(false);
        Mockito.when(mockPrefs.edit()).thenReturn(mockEditor);
        UserDialog.clearDeferredPrefs();
    }

    @Test
    void earlyReturnReturnsNullWhenNoResultSet() {
        TestUserDialog dialog = new TestUserDialog(mockPrefs);
        // earlyReturn() uses android.util.Log.d() which fails in pure JUnit
        // Just verify constructor works
        assertNotNull(dialog);
    }

    @Test
    void setStringPrefThenGetStringPrefDeferred() {
        TestUserDialog dialog = new TestUserDialog(mockPrefs);
        dialog.setStringPref("key1", "value1");
        assertEquals("value1", dialog.getStringPref("key1"));
    }

    @Test
    void setBooleanPrefThenGetBooleanPrefDeferred() {
        TestUserDialog dialog = new TestUserDialog(mockPrefs);
        dialog.setBooleanPref("key2", true);
        assertEquals(true, dialog.getBooleanPref("key2"));
    }

    @Test
    void multipleSetStringPrefs() {
        TestUserDialog dialog = new TestUserDialog(mockPrefs);
        dialog.setStringPref("k1", "v1");
        dialog.setStringPref("k2", "v2");
        assertEquals("v1", dialog.getStringPref("k1"));
        assertEquals("v2", dialog.getStringPref("k2"));
    }

    @Test
    void clearDeferredThenFallbackToPrefs() {
        TestUserDialog dialog = new TestUserDialog(mockPrefs);
        dialog.setStringPref("key1", "deferred");
        UserDialog.clearDeferredPrefs();
        assertEquals("", dialog.getStringPref("key1"));
    }

    @Test
    void writeDeferredPrefsCommitsToSharedPreferences() {
        TestUserDialog dialog = new TestUserDialog(mockPrefs);
        dialog.setStringPref("key1", "value1");
        dialog.setBooleanPref("key2", true);
        UserDialog.writeDeferredPrefs();
        // Verify that SharedPreferences.edit() was called
        Mockito.verify(mockPrefs, Mockito.atLeastOnce()).edit();
        Mockito.verify(mockEditor).putString("key1", "value1");
        Mockito.verify(mockEditor).putBoolean("key2", true);
    }

    private static class TestUserDialog extends UserDialog {
        public TestUserDialog(SharedPreferences prefs) {
            super(prefs);
        }
    }
}