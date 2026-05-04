package app.openconnect.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import app.openconnect.TestApplication;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35, application = TestApplication.class)
public class OpenConnectManagementThreadRobolectricTest {

    @Test
    public void rewriteShellReturnsFalseForValidShebang() {
        assertFalse(OpenConnectManagementThread.rewriteShell("#!/bin/sh\necho hello"));
    }

    @Test
    public void rewriteShellReturnsTrueForInvalidShebang() {
        assertTrue(OpenConnectManagementThread.rewriteShell("#!/nonexistent/sh\necho hello"));
    }

    @Test
    public void rewriteShellReturnsFalseForNoShebang() {
        assertFalse(OpenConnectManagementThread.rewriteShell("echo hello"));
    }

    @Test
    public void rewriteShellWithTabAfterShebang() {
        assertFalse(OpenConnectManagementThread.rewriteShell("#!\t/bin/sh\necho hello"));
    }

    @Test
    public void rewriteShellWithNewlineAfterShebang() {
        assertFalse(OpenConnectManagementThread.rewriteShell("#!/bin/sh\n"));
    }
}