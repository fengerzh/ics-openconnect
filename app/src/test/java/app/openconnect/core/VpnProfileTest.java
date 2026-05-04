package app.openconnect.core;

import app.openconnect.VpnProfile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VpnProfileTest {

    @Test
    void inlineTagConstant() {
        assertEquals("[[INLINE]]", VpnProfile.INLINE_TAG);
    }
}