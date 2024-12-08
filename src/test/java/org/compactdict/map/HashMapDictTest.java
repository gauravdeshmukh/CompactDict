package org.compactdict.map;

import org.compactdict.BytesRef;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link HashMapDict}.
 */
public class HashMapDictTest {
    @Test
    public void testGetExistingAndNonExistingKeys() {
        HashMapDict dict = new HashMapDict();
        BytesRef key1 = new BytesRef("key1");
        BytesRef value1 = new BytesRef("value1");
        BytesRef key2 = new BytesRef("key2");

        dict.put(key1, value1);

        assertEquals(value1, dict.get(key1), "Should return the correct value for an existing key");
        assertNull(dict.get(key2), "Should return null for a non-existing key");
    }

    @Test
    public void testGetWithNullKey() {
        HashMapDict dict = new HashMapDict();

        assertThrows(AssertionError.class, () -> dict.get(null), "Getting a null key is not allowed");
    }

    @Test
    public void testGetWithEmptyKey() {
        HashMapDict dict = new HashMapDict();
        BytesRef emptyKey = new BytesRef(new byte[0]);

        assertNull(dict.get(emptyKey), "Getting an empty key should return null");
    }

    @Test
    public void testGetWithNonExistentKey() {
        HashMapDict dict = new HashMapDict();
        BytesRef nonExistentKey = new BytesRef("non-existent".getBytes());

        assertNull(dict.get(nonExistentKey), "Getting a non-existent key should return null");
    }

    @Test
    public void testGetAfterRemoval() {
        HashMapDict dict = new HashMapDict();
        BytesRef key = new BytesRef("key".getBytes());
        BytesRef value = new BytesRef("value".getBytes());

        dict.put(key, value);
        dict.put(key, null);

        assertNull(dict.get(key), "Getting a removed key should return null");
    }

    @Test
    public void testGetEntriesReturnsNonEmptyCollection() {
        HashMapDict dict = new HashMapDict();
        BytesRef key1 = new BytesRef("key1");
        BytesRef value1 = new BytesRef("value1");
        BytesRef key2 = new BytesRef("key2");
        BytesRef value2 = new BytesRef("value2");
        dict.put(key1, value1);
        dict.put(key2, value2);

        Collection<Map.Entry<BytesRef, BytesRef>> entries = dict.getEntries();

        assertNotNull(entries);
        assertFalse(entries.isEmpty());
        assertEquals(2, entries.size());

        boolean foundEntry1 = false;
        boolean foundEntry2 = false;
        for (Map.Entry<BytesRef, BytesRef> entry : entries) {
            if (entry.getKey().equals(key1) && entry.getValue().equals(value1)) {
                foundEntry1 = true;
            } else if (entry.getKey().equals(key2) && entry.getValue().equals(value2)) {
                foundEntry2 = true;
            }
        }

        assertTrue(foundEntry1);
        assertTrue(foundEntry2);
    }

    @Test
    public void testGetEntriesEmptyDictionary() {
        HashMapDict emptyDict = new HashMapDict();

        Collection<Map.Entry<BytesRef, BytesRef>> entries = emptyDict.getEntries();

        assertNotNull(entries, "Entries collection should not be null");
        assertTrue(entries.isEmpty(), "Entries collection should be empty");
    }

    @Test
    public void testGetEntriesReflectsChanges() {
        HashMapDict dict = new HashMapDict();
        BytesRef key = new BytesRef("key");
        BytesRef value = new BytesRef("value");

        dict.put(key, value);
        Collection<Map.Entry<BytesRef, BytesRef>> entries = dict.getEntries();

        assertEquals(1, entries.size(), "Entries should contain one element");

        dict.put(new BytesRef("key2"), new BytesRef("value2"));
        assertEquals(2, entries.size(), "Entries should reflect the new addition");
    }

    @Test
    public void testPutAddsKeyValuePair() {
        HashMapDict dict = new HashMapDict();
        BytesRef key = new BytesRef("testKey");
        BytesRef value = new BytesRef("testValue");

        dict.put(key, value);
        BytesRef retrievedValue = dict.get(key);

        assertNotNull(retrievedValue, "Retrieved value should not be null");
        assertEquals(value, retrievedValue, "Retrieved value should match the inserted value");
    }

    @Test
    public void testPutNullKey() {
        HashMapDict dict = new HashMapDict();
        BytesRef value = new BytesRef("value");

        assertThrows(AssertionError.class, () -> dict.put(null, value));
    }

    @Test
    public void testPutNullValue() {
        HashMapDict dict = new HashMapDict();
        BytesRef key = new BytesRef("key");

        dict.put(key, null);

        assertNull(dict.get(key));
    }

    @Test
    public void testPutEmptyValue() {
        HashMapDict dict = new HashMapDict();
        BytesRef key = new BytesRef("key");
        BytesRef emptyValue = new BytesRef("");

        dict.put(key, emptyValue);

        assertEquals(emptyValue, dict.get(key));
    }

    @Test
    public void testPutDuplicateKey() {
        HashMapDict dict = new HashMapDict();
        BytesRef key = new BytesRef("key");
        BytesRef value1 = new BytesRef("value1");
        BytesRef value2 = new BytesRef("value2");

        dict.put(key, value1);
        dict.put(key, value2);

        assertEquals(value2, dict.get(key));
    }

    @Test
    public void testPutLargeKeyValue() {
        HashMapDict dict = new HashMapDict();
        // 1MB array
        byte[] largeArray = new byte[1024 * 1024];
        BytesRef largeKey = new BytesRef(largeArray);
        BytesRef largeValue = new BytesRef(largeArray);

        dict.put(largeKey, largeValue);

        assertEquals(largeValue, dict.get(largeKey));
    }
}
