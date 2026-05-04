package app.openconnect;

import org.junit.jupiter.api.Test;

import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.*;

class AuthFormHandlerTest {

    /**
     * Tests the same MD5 digest logic used by AuthFormHandler.digest().
     * The method is private and requires Android dependencies to construct an instance,
     * so we replicate the pure logic here.
     */
    private String digest(String s) {
        if (s == null) {
            s = "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            StringBuilder sb = new StringBuilder();
            byte[] d = digest.digest(s.getBytes("UTF-8"));
            for (byte dd : d) {
                sb.append(String.format("%02x", dd));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    @Test
    void digestEmptyString() {
        // MD5 of empty string is well-known
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", digest(""));
    }

    @Test
    void digestConsistentResults() {
        assertEquals(digest("hello"), digest("hello"));
    }

    @Test
    void digestDifferentInputsProduceDifferentOutputs() {
        assertNotEquals(digest("hello"), digest("world"));
    }

    @Test
    void digestNullSameAsEmpty() {
        assertEquals(digest(null), digest(""));
    }

    @Test
    void digestOutputIs32HexChars() {
        String result = digest("test input");
        assertEquals(32, result.length());
        assertTrue(result.matches("[0-9a-f]+"));
    }

    @Test
    void digestKnownValue() {
        // MD5("hello") = 5d41402abc4b2a76b9719d911017c592
        assertEquals("5d41402abc4b2a76b9719d911017c592", digest("hello"));
    }
}