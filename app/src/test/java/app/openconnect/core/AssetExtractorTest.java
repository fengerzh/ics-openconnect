package app.openconnect.core;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class AssetExtractorTest {

    @Test
    void crc32OfEmptyFile() throws Exception {
        File f = Files.createTempFile("test", ".tmp").toFile();
        f.deleteOnExit();
        long crc = AssetExtractor.crc32(f);
        assertEquals(0, crc);
    }

    @Test
    void crc32OfKnownContent() throws Exception {
        File f = Files.createTempFile("test", ".tmp").toFile();
        f.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(f)) {
            out.write("hello".getBytes("UTF-8"));
        }
        long crc = AssetExtractor.crc32(f);
        assertEquals(0x3610A686L, crc);
    }

    @Test
    void writeStreamWritesCorrectContent() throws Exception {
        File dst = Files.createTempFile("dst", ".tmp").toFile();
        dst.deleteOnExit();

        String content = "test content for writeStream";
        ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
        AssetExtractor.writeStream(in, dst);

        byte[] result = Files.readAllBytes(dst.toPath());
        assertEquals(content, new String(result, "UTF-8"));
    }

    @Test
    void getArchReturnsNonNull() {
        String arch = AssetExtractor.getArch();
        assertNotNull(arch);
        assertFalse(arch.isEmpty());
        // Should be one of the known ABIs
        assertTrue(arch.equals("arm64-v8a") || arch.equals("x86_64") ||
                arch.equals("x86") || arch.equals("armeabi"));
    }

    @Test
    void readAndCloseReturnsContent() throws Exception {
        String content = "read me please";
        ByteArrayInputStream bis = new ByteArrayInputStream(content.getBytes("UTF-8"));
        InputStreamReader reader = new InputStreamReader(bis, "UTF-8");
        String result = AssetExtractor.readAndClose(reader);
        assertEquals(content, result);
    }

    @Test
    void readStringFromFile() throws Exception {
        File f = Files.createTempFile("test", ".txt").toFile();
        f.deleteOnExit();
        String content = "file content";
        try (FileOutputStream out = new FileOutputStream(f)) {
            out.write(content.getBytes("UTF-8"));
        }
        String result = AssetExtractor.readStringFromFile(f.getAbsolutePath());
        assertEquals(content, result);
    }

    // readStringFromNonexistentFile cannot be tested in pure JUnit
    // because it uses android.util.Log.e which throws RuntimeException in unit tests
}