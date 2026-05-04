package app.openconnect.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FragCacheTest {

    @BeforeEach
    void setUp() {
        FragCache.init();
    }

    @Test
    void putAndGetWithUuid() {
        FragCache.put("uuid1", "key1", "value1");
        assertEquals("value1", FragCache.get("uuid1", "key1"));
    }

    @Test
    void putAndGetWithoutUuid() {
        FragCache.put("key2", "value2");
        assertEquals("value2", FragCache.get("key2"));
    }

    @Test
    void getNonexistentKeyReturnsNull() {
        assertNull(FragCache.get("nonexistent"));
    }

    @Test
    void overwriteValue() {
        FragCache.put("key1", "old");
        FragCache.put("key1", "new");
        assertEquals("new", FragCache.get("key1"));
    }

    @Test
    void differentUuidDifferentKey() {
        FragCache.put("uuid1", "key1", "val1");
        FragCache.put("uuid2", "key1", "val2");
        assertEquals("val1", FragCache.get("uuid1", "key1"));
        assertEquals("val2", FragCache.get("uuid2", "key1"));
    }

    @Test
    void nullUuidStillWorks() {
        FragCache.put(null, "key3", "value3");
        assertEquals("value3", FragCache.get(null, "key3"));
    }

    @Test
    void initClearsCache() {
        FragCache.put("key1", "value1");
        FragCache.init();
        assertNull(FragCache.get("key1"));
    }
}