package org.compactdict.trie;

import org.compactdict.BytesRef;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit test class for {@link BasicTrieDict}
 */
public class BasicTrieDictTest {

    /**
     * Test that a newly constructed BasicTrieDict is empty
     */
    @Test
    public void testNewBasicTrieDictIsEmpty() {
        BasicTrieDict dict = new BasicTrieDict();
        // Verify that the dictionary is empty
        BytesRef key = new BytesRef("test");
        assertNull(dict.get(key), "Newly constructed BasicTrieDict should not contain any key-value pairs");
        // Verify that we can add and retrieve a key-value pair
        BytesRef value = new BytesRef("value");
        dict.put(key, value);
        assertEquals(value, dict.get(key), "After adding a key-value pair, it should be retrievable");
    }

    /**
     * Test that a newly constructed BasicTrieDict is empty.
     */
    @Test
    public void testBasicTrieDictInitiallyEmpty() {
        BasicTrieDict dict = new BasicTrieDict();
        assertNull(dict.get(new BytesRef("anyKey")), "Newly constructed BasicTrieDict should not contain any keys");
    }

    /**
     * Test that a newly constructed BasicTrieDict returns null for getEntries().
     */
    @Test
    public void testBasicTrieDictGetEntriesReturnsNull() {
        BasicTrieDict dict = new BasicTrieDict();
        assertNull(dict.getEntries(), "getEntries() should return null for a newly constructed BasicTrieDict");
    }

    /**
     * Test that a newly constructed BasicTrieDict allows putting and getting a key-value pair.
     */
    @Test
    public void testBasicTrieDictAllowsPutAndGet() {
        BasicTrieDict dict = new BasicTrieDict();
        BytesRef key = new BytesRef("testKey");
        BytesRef value = new BytesRef("testValue");
        dict.put(key, value);
        BytesRef retrievedValue = dict.get(key);
        assertNotNull(retrievedValue, "Retrieved value should not be null after putting a key-value pair");
        assertEquals(value, retrievedValue, "Retrieved value should match the put value");
    }

    /**
     * Test that get() returns the correct value for an existing key
     * and null for a non-existing key.
     */
    @Test
    public void testGetExistingAndNonExistingKeys() {
        BasicTrieDict dict = new BasicTrieDict();
        BytesRef key1 = new BytesRef("key1");
        BytesRef value1 = new BytesRef("value1");
        BytesRef key2 = new BytesRef("key2");
        dict.put(key1, value1);
        assertEquals(value1, dict.get(key1), "Should return the correct value for an existing key");
        assertNull(dict.get(key2), "Should return null for a non-existing key");
    }

    /**
     * Test get method with null key
     */
    @Test
    public void testGetWithNullKey() {
        BasicTrieDict dict = new BasicTrieDict();
        assertThrows(AssertionError.class, () -> dict.get(null), "Getting a null key is not allowed");
    }

    /**
     * Test get method with empty key
     */
    @Test
    public void testGetWithEmptyKey() {
        BasicTrieDict dict = new BasicTrieDict();
        BytesRef emptyKey = new BytesRef(new byte[0]);
        assertNull(dict.get(emptyKey), "Getting an empty key should return null");
    }

    /**
     * Test get method with non-existent key
     */
    @Test
    public void testGetWithNonExistentKey() {
        BasicTrieDict dict = new BasicTrieDict();
        BytesRef nonExistentKey = new BytesRef("non-existent".getBytes());
        assertNull(dict.get(nonExistentKey), "Getting a non-existent key should return null");
    }

    /**
     * Test case for put method of BasicTrieDict
     * Verifies that a key-value pair can be successfully added to the dictionary
     */
    @Test
    public void testPutAddsKeyValuePair() {
        BasicTrieDict dict = new BasicTrieDict();
        BytesRef key = new BytesRef("testKey");
        BytesRef value = new BytesRef("testValue");
        dict.put(key, value);
        BytesRef retrievedValue = dict.get(key);
        assertNotNull(retrievedValue, "Retrieved value should not be null");
        assertEquals(value, retrievedValue, "Retrieved value should match the inserted value");
    }

    @Test
    public void testPutNullKey() {
        /**
         * Test putting a null key into the dictionary.
         * This tests the scenario where the input is invalid (null key).
         */
        BasicTrieDict dict = new BasicTrieDict();
        BytesRef value = new BytesRef("value");
        assertThrows(AssertionError.class, () -> dict.put(null, value));
    }

    @Test
    public void testPutNullValue() {
        /**
         * Test putting a null value into the dictionary.
         * This tests the scenario where the input is invalid (null value).
         */
        BasicTrieDict dict = new BasicTrieDict();
        BytesRef key = new BytesRef("key");
        assertThrows(AssertionError.class, () -> dict.put(key, null));
    }

    @Test
    public void testPutEmptyValue() {
        /**
         * Test putting an empty value into the dictionary.
         * This tests the scenario where the input is empty.
         */
        BasicTrieDict dict = new BasicTrieDict();
        BytesRef key = new BytesRef("key");
        BytesRef emptyValue = new BytesRef("");
        dict.put(key, emptyValue);
        assertEquals(emptyValue, dict.get(key));
    }

    @Test
    public void testPutDuplicateKey() {
        /**
         * Test putting a duplicate key into the dictionary.
         * This tests the scenario where we're overwriting an existing key.
         */
        BasicTrieDict dict = new BasicTrieDict();
        BytesRef key = new BytesRef("key");
        BytesRef value1 = new BytesRef("value1");
        BytesRef value2 = new BytesRef("value2");
        dict.put(key, value1);
        dict.put(key, value2);
        assertEquals(value2, dict.get(key));
    }

    @Test
    public void testPutLargeKeyValue() {
        /**
         * Test putting a very large key and value into the dictionary.
         * This tests the scenario where the input is at the upper bounds of acceptable size.
         */
        BasicTrieDict dict = new BasicTrieDict();
        // 1MB array
        byte[] largeArray = new byte[1024 * 1024];
        BytesRef largeKey = new BytesRef(largeArray);
        BytesRef largeValue = new BytesRef(largeArray);
        dict.put(largeKey, largeValue);
        assertEquals(largeValue, dict.get(largeKey));
    }
    
    @Test
    public void testPrefixMatching() {
        BasicTrieDict dict = new BasicTrieDict();
        dict.put(new BytesRef("key"), new BytesRef("value"), true);
        dict.put(new BytesRef("key1"), new BytesRef("value1"), true);
        dict.put(new BytesRef("key12"), new BytesRef("value12"), false);
        dict.put(new BytesRef("key123"), new BytesRef("value123"), false);

        assertEquals(new BytesRef("value"), dict.get(new BytesRef("key")));
        assertEquals(new BytesRef("value1"), dict.get(new BytesRef("key1")));
        assertEquals(new BytesRef("value12"), dict.get(new BytesRef("key12")));
        assertEquals(new BytesRef("value123"), dict.get(new BytesRef("key123")));
        assertEquals(new BytesRef("value1"), dict.get(new BytesRef("key111")));
        assertEquals(new BytesRef("value1"), dict.get(new BytesRef("key121")));
        assertEquals(new BytesRef("value"), dict.get(new BytesRef("key21")));
        assertNull(dict.get(new BytesRef("ke1y")));
    }
}
