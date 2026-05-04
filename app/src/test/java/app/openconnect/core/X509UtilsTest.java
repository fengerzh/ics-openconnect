package app.openconnect.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class X509UtilsTest {

    @Test
    void isPrintableCharAsciiLetter() {
        assertTrue(X509Utils.isPrintableChar('A'));
        assertTrue(X509Utils.isPrintableChar('z'));
    }

    @Test
    void isPrintableCharDigit() {
        assertTrue(X509Utils.isPrintableChar('0'));
        assertTrue(X509Utils.isPrintableChar('9'));
    }

    @Test
    void isPrintableCharSpace() {
        assertTrue(X509Utils.isPrintableChar(' '));
    }

    @Test
    void isPrintableCharControlChar() {
        assertFalse(X509Utils.isPrintableChar('\0'));
        assertFalse(X509Utils.isPrintableChar('\n'));
        assertFalse(X509Utils.isPrintableChar('\t'));
    }

    @Test
    void isPrintableCharSpecialsBlock() {
        assertFalse(X509Utils.isPrintableChar('\uFFFF'));
    }

    @Test
    void isPrintableCharChinese() {
        assertTrue(X509Utils.isPrintableChar('中'));
        assertTrue(X509Utils.isPrintableChar('文'));
    }

    @Test
    void isPrintableCharPunctuation() {
        assertTrue(X509Utils.isPrintableChar('.'));
        assertTrue(X509Utils.isPrintableChar('@'));
        assertTrue(X509Utils.isPrintableChar('-'));
    }

    @Test
    void ia5decodeSimpleAscii() {
        // "16" decodes to byte 0x16 = char 0x12 (DC2, non-printable -> ignored at position i==1)
        // But for printable chars: "41" = 'A', "42" = 'B'
        String result = X509Utils.ia5decode("4142");
        assertEquals("AB", result);
    }

    @Test
    void ia5decodeEmailPrefix() {
        // The real usage strips the prefix "1.2.840.113549.1.9.1=#16" before calling ia5decode
        // So ia5decode receives just the IA5String hex portion
        // "4a6f686e" = "John" (each pair is a hex byte)
        String result = X509Utils.ia5decode("4a6f686e");
        assertEquals("John", result);
    }

    @Test
    void ia5decodeEmptyString() {
        assertEquals("", X509Utils.ia5decode(""));
    }

    @Test
    void ia5decodeSingleChar() {
        // "41" = 'A'
        assertEquals("A", X509Utils.ia5decode("41"));
    }

    @Test
    void ia5decodeNonPrintableChar() {
        // "01" = SOH control char -> should be "\\x01"
        String result = X509Utils.ia5decode("01");
        assertEquals("\\x01", result);
    }

    @Test
    void ia5decodeControlCharAtPosition1Ignored() {
        // "12" = DC2 (0x12) - at position i==1 it should be ignored
        // "41" = 'A'
        String result = X509Utils.ia5decode("1241");
        assertEquals("A", result);
    }

    @Test
    void ia5decodeControlCharAtPosition1Hex1b() {
        // "1b" = ESC (0x1b) - also ignored at position i==1
        String result = X509Utils.ia5decode("1b41");
        assertEquals("A", result);
    }
}