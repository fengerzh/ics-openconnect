package app.openconnect.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenConnectManagementThreadRewriteShellTest {

    @Test
    void rewriteShellWithNonexistentShebangReturnsTrue() {
        // Shebang pointing to a nonexistent path should return true
        // (meaning the shell line should be rewritten)
        String input = "#!/path/to/nonexistent/file\nsome script";
        assertTrue(OpenConnectManagementThread.rewriteShell(input));
    }

    @Test
    void rewriteShellWithExistingShebangReturnsFalse() {
        // Shebang pointing to an existing path should return false
        // /bin/sh exists on most systems including macOS and Linux
        String input = "#!/bin/sh\nsome script";
        assertFalse(OpenConnectManagementThread.rewriteShell(input));
    }

    @Test
    void rewriteShellWithNoShebangReturnsFalse() {
        // No shebang line at all
        String input = "just a regular script\nwith no shebang";
        assertFalse(OpenConnectManagementThread.rewriteShell(input));
    }

    @Test
    void rewriteShellWithBlankShebangReturnsFalse() {
        // Shebang with no path (just "#!")
        String input = "#!\nscript content";
        assertFalse(OpenConnectManagementThread.rewriteShell(input));
    }

    @Test
    void rewriteShellWithSpacesInShebang() {
        // Shebang with spaces after path
        String input = "#!/path/to/nonexistent/file  \nscript";
        assertTrue(OpenConnectManagementThread.rewriteShell(input));
    }

    @Test
    void rewriteShellWithTabInShebang() {
        // Shebang with tabs after path
        String input = "#!/path/to/nonexistent/file\t\nscript";
        assertTrue(OpenConnectManagementThread.rewriteShell(input));
    }

    @Test
    void stateConstants() {
        assertEquals(1, OpenConnectManagementThread.STATE_AUTHENTICATING);
        assertEquals(2, OpenConnectManagementThread.STATE_USER_PROMPT);
        assertEquals(3, OpenConnectManagementThread.STATE_AUTHENTICATED);
        assertEquals(4, OpenConnectManagementThread.STATE_CONNECTING);
        assertEquals(5, OpenConnectManagementThread.STATE_CONNECTED);
        assertEquals(6, OpenConnectManagementThread.STATE_DISCONNECTED);
    }

    @Test
    void tagConstant() {
        assertEquals("OpenConnect", OpenConnectManagementThread.TAG);
    }
}