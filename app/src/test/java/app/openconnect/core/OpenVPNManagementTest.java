package app.openconnect.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenVPNManagementTest {

    @Test
    void interfaceExists() {
        assertNotNull(OpenVPNManagement.class);
        assertTrue(OpenVPNManagement.class.isInterface());
    }

    @Test
    void pauseReasonEnumValues() {
        assertNotNull(OpenVPNManagement.pauseReason.noNetwork);
        assertNotNull(OpenVPNManagement.pauseReason.userPause);
        assertNotNull(OpenVPNManagement.pauseReason.screenOff);
        assertEquals(3, OpenVPNManagement.pauseReason.values().length);
    }

    @Test
    void bytecountIntervalConstant() {
        assertEquals(2, OpenVPNManagement.mBytecountInterval);
    }
}