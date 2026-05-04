package app.openconnect.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import android.content.SharedPreferences;

import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class ErrorDialogTest {

    private SharedPreferences mockPrefs;

    @BeforeEach
    void setUp() {
        mockPrefs = Mockito.mock(SharedPreferences.class);
        Mockito.when(mockPrefs.getString(Mockito.anyString(), Mockito.anyString())).thenReturn("");
        Mockito.when(mockPrefs.getBoolean(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(false);
        UserDialog.clearDeferredPrefs();
    }

    @Test
    void constructorSetsTitleAndMessage() {
        ErrorDialog dialog = new ErrorDialog(mockPrefs, "Error Title", "Error Message");
        assertEquals("Error Title", dialog.mTitle);
        assertEquals("Error Message", dialog.mMessage);
    }

    @Test
    void constructorWithNullTitle() {
        ErrorDialog dialog = new ErrorDialog(mockPrefs, null, "msg");
        assertNull(dialog.mTitle);
        assertEquals("msg", dialog.mMessage);
    }

    @Test
    void constructorWithNullMessage() {
        ErrorDialog dialog = new ErrorDialog(mockPrefs, "title", null);
        assertEquals("title", dialog.mTitle);
        assertNull(dialog.mMessage);
    }

    @Test
    void onClickDoesNotCrash() {
        ErrorDialog dialog = new ErrorDialog(mockPrefs, "title", "msg");
        // onClick is empty implementation - just verify no crash
        dialog.onClick(null, 0);
    }
}