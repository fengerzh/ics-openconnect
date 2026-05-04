package app.openconnect.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenConnectManagementThreadTest {

    @Test
    void rewriteShellReturnsFalseWhenShebangExists() {
        // /bin/sh always exists on Unix/macOS
        assertFalse(OpenConnectManagementThread.rewriteShell("#!/bin/sh\necho hello"));
    }

    @Test
    void rewriteShellReturnsTrueWhenShebangNonexistent() {
        assertTrue(OpenConnectManagementThread.rewriteShell("#!/nonexistent/path/to/sh\necho hello"));
    }

    @Test
    void rewriteShellReturnsFalseWhenNoShebang() {
        assertFalse(OpenConnectManagementThread.rewriteShell("echo hello"));
    }

    @Test
    void rewriteShellWithSpaces() {
        assertFalse(OpenConnectManagementThread.rewriteShell("#!  /bin/sh  \necho hello"));
    }
}