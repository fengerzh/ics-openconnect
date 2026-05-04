package app.openconnect.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenVPNTest {

    @Test
    void connectionStatusEnumValues() {
        assertNotNull(OpenVPN.ConnectionStatus.LEVEL_CONNECTED);
        assertNotNull(OpenVPN.ConnectionStatus.LEVEL_VPNPAUSED);
        assertNotNull(OpenVPN.ConnectionStatus.LEVEL_CONNECTING_SERVER_REPLIED);
        assertNotNull(OpenVPN.ConnectionStatus.LEVEL_CONNECTING_NO_SERVER_REPLY_YET);
        assertNotNull(OpenVPN.ConnectionStatus.LEVEL_NONETWORK);
        assertNotNull(OpenVPN.ConnectionStatus.LEVEL_NOTCONNECTED);
        assertNotNull(OpenVPN.ConnectionStatus.LEVEL_AUTH_FAILED);
        assertNotNull(OpenVPN.ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT);
        assertNotNull(OpenVPN.ConnectionStatus.UNKNOWN_LEVEL);
    }

    @Test
    void connectionStatusEnumCount() {
        OpenVPN.ConnectionStatus[] values = OpenVPN.ConnectionStatus.values();
        assertEquals(9, values.length);
    }

    @Test
    void logErrorDoesNotCrash() {
        // logError and logInfo are stub methods (empty bodies)
        OpenVPN.logError("test error");
        OpenVPN.logInfo("test info");
    }

    @Test
    void logErrorWithResourceIdDoesNotCrash() {
        OpenVPN.logError(0, "arg1", "arg2");
        OpenVPN.logInfo(0, "arg1");
    }
}