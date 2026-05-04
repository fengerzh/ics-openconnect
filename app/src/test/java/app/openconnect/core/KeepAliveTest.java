package app.openconnect.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KeepAliveTest {

    private KeepAlive keepAlive;

    @BeforeEach
    void setUp() {
        keepAlive = new KeepAlive(30, "8.8.8.8", null);
    }

    @Test
    void buildDNSQueryProducesNonNullResult() throws Exception {
        byte[] transID = {0x12, 0x34};
        byte[] result = invokeBuildDNSQuery(transID, "www.example.com");
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void buildDNSQueryStartsWithTransactionId() throws Exception {
        byte[] transID = {(byte)0xAB, (byte)0xCD};
        byte[] result = invokeBuildDNSQuery(transID, "test.com");
        assertEquals((byte)0xAB, result[0]);
        assertEquals((byte)0xCD, result[1]);
    }

    @Test
    void buildDNSQueryContainsHostname() throws Exception {
        byte[] transID = {0x00, 0x01};
        byte[] result = invokeBuildDNSQuery(transID, "abc.com");
        // The hostname should be encoded as: 3,'a','b','c',3,'c','o','m',0
        // After transID (2 bytes) + prefix (10 bytes), the name starts
        int nameStart = 2 + 10; // transID.length + prefix.length
        assertEquals(3, result[nameStart]);     // length of "abc"
        assertEquals('a', result[nameStart + 1]);
        assertEquals('b', result[nameStart + 2]);
        assertEquals('c', result[nameStart + 3]);
        assertEquals(3, result[nameStart + 4]); // length of "com"
    }

    @Test
    void buildDNSQueryContainsTypeA() throws Exception {
        byte[] transID = {0x00, 0x01};
        byte[] result = invokeBuildDNSQuery(transID, "test.com");
        // Last 4 bytes should be: 0x00, 0x01 (Type A), 0x00, 0x01 (Class IN)
        int len = result.length;
        assertEquals(0x00, result[len - 4]);
        assertEquals(0x01, result[len - 3]);
        assertEquals(0x00, result[len - 2]);
        assertEquals(0x01, result[len - 1]);
    }

    @Test
    void buildDNSQuerySingleLabelHostname() throws Exception {
        byte[] transID = {0x00, 0x01};
        byte[] result = invokeBuildDNSQuery(transID, "localhost");
        int nameStart = 2 + 10;
        assertEquals(9, result[nameStart]); // length of "localhost"
    }

    private byte[] invokeBuildDNSQuery(byte[] transID, String hostname) throws Exception {
        java.lang.reflect.Method method = KeepAlive.class.getDeclaredMethod("buildDNSQuery", byte[].class, String.class);
        method.setAccessible(true);
        return (byte[]) method.invoke(keepAlive, transID, hostname);
    }
}