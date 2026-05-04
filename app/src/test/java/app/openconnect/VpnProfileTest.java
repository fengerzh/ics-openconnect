package app.openconnect;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class VpnProfileTest {

    @Test
    void inlineTagConstant() {
        assertEquals("[[INLINE]]", VpnProfile.INLINE_TAG);
    }

    @Test
    void constructorWithNameAndUuid() {
        VpnProfile profile = new VpnProfile("TestVPN", "550e8400-e29b-41d4-a716-446655440000");
        assertEquals("TestVPN", profile.getName());
        assertEquals("550e8400-e29b-41d4-a716-446655440000", profile.getUUIDString());
    }

    @Test
    void isValidWithValidData() {
        VpnProfile profile = new VpnProfile("TestVPN", "550e8400-e29b-41d4-a716-446655440000");
        assertTrue(profile.isValid());
    }

    @Test
    void toStringReturnsName() {
        VpnProfile profile = new VpnProfile("MyProfile", "550e8400-e29b-41d4-a716-446655440000");
        assertEquals("MyProfile", profile.toString());
    }

    @Test
    void compareToOrdersAlphabetically() {
        VpnProfile a = new VpnProfile("alpha", "550e8400-e29b-41d4-a716-446655440001");
        VpnProfile b = new VpnProfile("beta", "550e8400-e29b-41d4-a716-446655440002");
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
    }

    @Test
    void compareToIsCaseInsensitive() {
        VpnProfile a = new VpnProfile("alpha", "550e8400-e29b-41d4-a716-446655440001");
        VpnProfile b = new VpnProfile("ALPHA", "550e8400-e29b-41d4-a716-446655440002");
        assertEquals(0, a.compareTo(b));
    }

    @Test
    void getUUIDReturnsCorrectUUID() {
        VpnProfile profile = new VpnProfile("Test", "550e8400-e29b-41d4-a716-446655440000");
        assertEquals(java.util.UUID.fromString("550e8400-e29b-41d4-a716-446655440000"), profile.getUUID());
    }
}