package app.openconnect.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import android.content.SharedPreferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDialogTest {

    private SharedPreferences mockPrefs;
    private SharedPreferences.Editor mockEditor;
    private UserDialog dialog;

    @BeforeEach
    void setUp() {
        mockPrefs = Mockito.mock(SharedPreferences.class);
        mockEditor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(mockPrefs.edit()).thenReturn(mockEditor);
        Mockito.when(mockEditor.putString(Mockito.anyString(), Mockito.anyString())).thenReturn(mockEditor);
        Mockito.when(mockEditor.putBoolean(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(mockEditor);

        // Create a minimal concrete subclass for testing
        dialog = new UserDialog(mockPrefs) {
            @Override
            public Object earlyReturn() {
                return null;
            }
        };

        // Clear static state before each test
        UserDialog.clearDeferredPrefs();
    }

    @Test
    void setAndGetStringPref() {
        dialog.setStringPref("key1", "value1");
        assertEquals("value1", dialog.getStringPref("key1"));
    }

    @Test
    void getStringPrefFallsBackToSharedPreferences() {
        Mockito.when(mockPrefs.getString("missing", "")).thenReturn("fallback");
        assertEquals("fallback", dialog.getStringPref("missing"));
    }

    @Test
    void setAndGetBooleanPref() {
        dialog.setBooleanPref("flag1", true);
        assertTrue(dialog.getBooleanPref("flag1"));
        dialog.setBooleanPref("flag2", false);
        assertFalse(dialog.getBooleanPref("flag2"));
    }

    @Test
    void getBooleanPrefFallsBackToSharedPreferences() {
        Mockito.when(mockPrefs.getBoolean("missing", false)).thenReturn(true);
        assertTrue(dialog.getBooleanPref("missing"));
    }

    @Test
    void setStringPrefOverridesDeferred() {
        dialog.setStringPref("key", "first");
        dialog.setStringPref("key", "second");
        assertEquals("second", dialog.getStringPref("key"));
    }

    @Test
    void clearDeferredPrefs() {
        dialog.setStringPref("key1", "value1");
        dialog.setBooleanPref("key2", true);
        UserDialog.clearDeferredPrefs();
        // After clearing, should fall back to SharedPreferences
        Mockito.when(mockPrefs.getString("key1", "")).thenReturn("");
        assertEquals("", dialog.getStringPref("key1"));
    }

    @Test
    void writeDeferredPrefsCommitsStrings() {
        dialog.setStringPref("key1", "value1");
        UserDialog.writeDeferredPrefs();
        Mockito.verify(mockEditor).putString("key1", "value1");
        Mockito.verify(mockEditor).commit();
    }

    @Test
    void writeDeferredPrefsCommitsBooleans() {
        dialog.setBooleanPref("flag1", true);
        UserDialog.writeDeferredPrefs();
        Mockito.verify(mockEditor).putBoolean("flag1", true);
        Mockito.verify(mockEditor).commit();
    }

    @Test
    void writeDeferredPrefsClearsMap() {
        dialog.setStringPref("key1", "value1");
        UserDialog.writeDeferredPrefs();
        // After write, deferred prefs are cleared, so falls back to SharedPreferences
        Mockito.when(mockPrefs.getString("key1", "")).thenReturn("fromprefs");
        assertEquals("fromprefs", dialog.getStringPref("key1"));
    }

    @Test
    void getBooleanPrefWithClassCastException() {
        // Put a string pref with the same key, then try to get boolean
        dialog.setStringPref("sharedKey", "stringValue");
        // This should throw ClassCastException internally and fall back to SharedPreferences
        Mockito.when(mockPrefs.getBoolean("sharedKey", false)).thenReturn(false);
        assertFalse(dialog.getBooleanPref("sharedKey"));
    }

    @Test
    void getStringPrefWithClassCastException() {
        // Put a boolean pref with the same key, then try to get string
        dialog.setBooleanPref("sharedKey", true);
        // This should throw ClassCastException internally and fall back to SharedPreferences
        Mockito.when(mockPrefs.getString("sharedKey", "")).thenReturn("fallback");
        assertEquals("fallback", dialog.getStringPref("sharedKey"));
    }
}