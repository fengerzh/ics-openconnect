package app.openconnect.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CIDRIPTest {

    @Test
    void constructorWithSlashNotation() {
        CIDRIP cidr = new CIDRIP("10.0.0.0/24");
        assertEquals("10.0.0.0/24", cidr.toString());
    }

    @Test
    void constructorWithSlashNotation16() {
        CIDRIP cidr = new CIDRIP("192.168.0.0/16");
        assertEquals("192.168.0.0/16", cidr.toString());
    }

    @Test
    void constructorWithIpAndMask() {
        CIDRIP cidr = new CIDRIP("10.0.0.0", "255.255.255.0");
        assertEquals("10.0.0.0/24", cidr.toString());
    }

    @Test
    void constructorWithIpAndPrefixLen() {
        CIDRIP cidr = new CIDRIP("10.0.0.0", 24);
        assertEquals("10.0.0.0/24", cidr.toString());
    }

    @Test
    void normaliseCorrectsNonNetworkAddress() {
        // 10.0.0.5/24 should normalise to 10.0.0.0/24
        CIDRIP cidr = new CIDRIP("10.0.0.5/24");
        assertEquals("10.0.0.0/24", cidr.toString());
    }

    @Test
    void normaliseReturnsTrueWhenModified() {
        CIDRIP cidr = new CIDRIP("10.0.0.5", 24);
        assertTrue(cidr.normalise());
    }

    @Test
    void normaliseReturnsFalseWhenAlreadyNormal() {
        CIDRIP cidr = new CIDRIP("10.0.0.0", 24);
        assertFalse(cidr.normalise());
    }

    @Test
    void maskToLen2552552550() {
        assertEquals(24, CIDRIP.maskToLen("255.255.255.0"));
    }

    @Test
    void maskToLen25525500() {
        assertEquals(16, CIDRIP.maskToLen("255.255.0.0"));
    }

    @Test
    void maskToLen255000() {
        assertEquals(8, CIDRIP.maskToLen("255.0.0.0"));
    }

    @Test
    void maskToLen255255255255() {
        assertEquals(32, CIDRIP.maskToLen("255.255.255.255"));
    }

    @Test
    void maskToLen0000() {
        assertEquals(0, CIDRIP.maskToLen("0.0.0.0"));
    }

    @Test
    void maskToLenNonContiguousReturns32() {
        // Non-contiguous mask like 255.0.255.0 should return 32
        assertEquals(32, CIDRIP.maskToLen("255.0.255.0"));
    }

    @Test
    void getIntConvertsIpCorrectly() {
        assertEquals(0x0A000001L, CIDRIP.getInt("10.0.0.1"));
    }

    @Test
    void getIntConverts192Network() {
        assertEquals(0xC0A80001L, CIDRIP.getInt("192.168.0.1"));
    }

    @Test
    void prefixLenOver32InCombo() {
        // /40 -> matches [0-9]+ so parseInt gets 40, then clamped to 32
        CIDRIP cidr = new CIDRIP("10.0.0.0/40");
        assertEquals(32, cidr.len);
    }

    @Test
    void prefixLen0() {
        CIDRIP cidr = new CIDRIP("0.0.0.0/0");
        assertEquals(0, cidr.len);
    }

    @Test
    void maskNotationInSlashFormat() {
        // 10.0.0.0/255.255.255.0 should parse mask part
        CIDRIP cidr = new CIDRIP("10.0.0.0/255.255.255.0");
        assertEquals("10.0.0.0/24", cidr.toString());
    }

    @Test
    void getIntInstanceMethod() {
        CIDRIP cidr = new CIDRIP("10.0.0.1", 32);
        assertEquals(0x0A000001L, cidr.getInt());
    }
}