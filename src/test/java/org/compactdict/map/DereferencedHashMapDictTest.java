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
 * Unit tests for {@link DereferencedHashMapDict}.
 */
public class DereferencedHashMapDictTest {
    @Test
    public void testDefaultConstructor() {
        DereferencedHashMapDict dict = new DereferencedHashMapDict();
        // Verify that the dictionary is empty
        assertTrue(dict.getEntries().isEmpty());
        // Verify that we can add and retrieve entries
        BytesRef key = new BytesRef("key");
        BytesRef value = new BytesRef("value");

        dict.put(key, value);
        BytesRef retrievedValue = dict.get(key);

        assertNotNull(retrievedValue);
        assertEquals(value, retrievedValue);
        // Verify that the dictionary now contains one entry
        assertEquals(1, dict.getEntries().size());
        // Verify that the entry in the dictionary matches what we put in
        for (Map.Entry<BytesRef, BytesRef> entry : dict.getEntries()) {
            assertEquals(key, entry.getKey());
            assertEquals(value, entry.getValue());
        }
    }

    @Test
    public void testDereferencedHashMapDictWithNegativeInitialCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new DereferencedHashMapDict(-1));
    }

    @Test
    public void testDereferencedHashMapDictWithZeroInitialCapacity() {
        DereferencedHashMapDict dict = new DereferencedHashMapDict(0);
        assertTrue(dict.getEntries().isEmpty());
    }

    @Test
    public void testDereferencedHashMapDictWithMaxIntegerPlusOneInitialCapacity() {
        assertThrows(RuntimeException.class, () -> new DereferencedHashMapDict(Integer.MAX_VALUE + 1));
    }

    @Test
    public void testGetExistingAndNonExistingKeys() {
        DereferencedHashMapDict dict = new DereferencedHashMapDict();
        BytesRef key1 = new BytesRef("key1");
        BytesRef value1 = new BytesRef("value1");
        BytesRef key2 = new BytesRef("key2");

        dict.put(key1, value1);

        assertEquals(value1, dict.get(key1), "Should return the correct value for an existing key");
        assertNull(dict.get(key2), "Should return null for a non-existing key");
    }

    @Test
    public void testGetWithNullKey() {
        DereferencedHashMapDict dict = new DereferencedHashMapDict();
        assertThrows(AssertionError.class, () -> dict.get(null), "Getting a null key is not allowed");
    }

    @Test
    public void testGetWithEmptyKey() {
        DereferencedHashMapDict dict = new DereferencedHashMapDict();
        BytesRef emptyKey = new BytesRef(new byte[0]);

        assertNull(dict.get(emptyKey), "Getting an empty key should return null");
    }

    @Test
    public void testGetWithNonExistentKey() {
        DereferencedHashMapDict dict = new DereferencedHashMapDict();
        BytesRef nonExistentKey = new BytesRef("non-existent".getBytes());

        assertNull(dict.get(nonExistentKey), "Getting a non-existent key should return null");
    }

    @Test
    public void testGetAfterRemoval() {
        DereferencedHashMapDict dict = new DereferencedHashMapDict();
        BytesRef key = new BytesRef("key".getBytes());
        BytesRef value = new BytesRef("value".getBytes());
        dict.put(key, value);
        // Simulating removal
        dict.put(key, null);

        assertNull(dict.get(key), "Getting a removed key should return null");
    }

    @Test
    public void testGetEntriesReturnsNonEmptyCollection() {
        DereferencedHashMapDict dict = new DereferencedHashMapDict();
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
        DereferencedHashMapDict emptyDict = new DereferencedHashMapDict();
        Collection<Map.Entry<BytesRef, BytesRef>> entries = emptyDict.getEntries();

        assertNotNull(entries, "Entries collection should not be null");
        assertTrue(entries.isEmpty(), "Entries collection should be empty");
    }

    @Test
    public void testGetEntriesReflectsChanges() {
        DereferencedHashMapDict dict = new DereferencedHashMapDict();
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
        DereferencedHashMapDict dict = new DereferencedHashMapDict();
        BytesRef key = new BytesRef("testKey");
        BytesRef value = new BytesRef("testValue");

        dict.put(key, value);
        BytesRef retrievedValue = dict.get(key);

        assertNotNull(retrievedValue, "Retrieved value should not be null");
        assertEquals(value, retrievedValue, "Retrieved value should match the inserted value");
    }

    @Test
    public void testPutNullKey() {
        DereferencedHashMapDict dict = new DereferencedHashMapDict();
        BytesRef value = new BytesRef("value");
        assertThrows(AssertionError.class, () -> dict.put(null, value));
    }

    @Test
    public void testPutNullValue() {
        DereferencedHashMapDict dict = new DereferencedHashMapDict();
        BytesRef key = new BytesRef("key");

        dict.put(key, null);

        assertNull(dict.get(key));
    }

    @Test
    public void testPutEmptyValue() {
       DereferencedHashMapDict dict = new DereferencedHashMapDict();
        BytesRef key = new BytesRef("key");
        BytesRef emptyValue = new BytesRef("");

        dict.put(key, emptyValue);

        assertEquals(emptyValue, dict.get(key));
    }

    @Test
    public void testPutDuplicateKey() {
        DereferencedHashMapDict dict = new DereferencedHashMapDict();
        BytesRef key = new BytesRef("key");
        BytesRef value1 = new BytesRef("value1");
        BytesRef value2 = new BytesRef("value2");

        dict.put(key, value1);
        dict.put(key, value2);

        assertEquals(value2, dict.get(key));
    }

    @Test
    public void testPutLargeKeyValue() {
        DereferencedHashMapDict dict = new DereferencedHashMapDict();
        // 1MB array
        byte[] largeArray = new byte[1024 * 1024];
        BytesRef largeKey = new BytesRef(largeArray);
        BytesRef largeValue = new BytesRef(largeArray);

        dict.put(largeKey, largeValue);

        assertEquals(largeValue, dict.get(largeKey));
    }
}