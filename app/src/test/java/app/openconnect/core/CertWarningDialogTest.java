package app.openconnect.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CertWarningDialogTest {

    private android.content.SharedPreferences mockPrefs;

    @BeforeEach
    void setUp() {
        mockPrefs = org.mockito.Mockito.mock(android.content.SharedPreferences.class);
        org.mockito.Mockito.when(mockPrefs.getString(org.mockito.Mockito.anyString(), org.mockito.Mockito.anyString()))
            .thenReturn("");
        org.mockito.Mockito.when(mockPrefs.getBoolean(org.mockito.Mockito.anyString(), org.mockito.Mockito.anyBoolean()))
            .thenReturn(false);
        UserDialog.clearDeferredPrefs();
    }

    @Test
    void resultConstants() {
        assertEquals(0, CertWarningDialog.RESULT_NO);
        assertEquals(1, CertWarningDialog.RESULT_ONCE);
        assertEquals(2, CertWarningDialog.RESULT_ALWAYS);
    }

    @Test
    void constructorSetsFields() {
        CertWarningDialog dialog = new CertWarningDialog(mockPrefs, "host.example.com", "abc123", "cert mismatch");
        assertEquals("host.example.com", dialog.mHostname);
        assertEquals("abc123", dialog.mCertSHA1);
        assertEquals("cert mismatch", dialog.mReason);
    }

    @Test
    void constructorWithNullReason() {
        CertWarningDialog dialog = new CertWarningDialog(mockPrefs, "host", "hash", null);
        assertEquals("host", dialog.mHostname);
        assertEquals("hash", dialog.mCertSHA1);
        assertNull(dialog.mReason);
    }

    @Test
    void earlyReturnReturnsNull() {
        CertWarningDialog dialog = new CertWarningDialog(mockPrefs, "host", "hash", "reason");
        assertNull(dialog.earlyReturn());
    }
}