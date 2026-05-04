package app.openconnect.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityRemoteExceptionTest {

    @Test
    void constructorCreatesException() {
        app.openconnect.api.SecurityRemoteException ex = new app.openconnect.api.SecurityRemoteException();
        assertNotNull(ex);
    }

    @Test
    void isRemoteExceptionSubclass() {
        assertTrue(android.os.RemoteException.class.isAssignableFrom(app.openconnect.api.SecurityRemoteException.class));
    }
}