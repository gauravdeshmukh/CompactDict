package org.compactdict.trie;

import org.compactdict.BytesRef;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link DereferencedTrieDict}
 */
public class DereferencedTrieDictTest {

    /**
     * Test the constructor of DereferencedTrieDict
     * Verifies that a newly created DereferencedTrieDict is empty
     */
    @Test
    public void testNewDereferencedTrieDictIsEmpty() {
        DereferencedTrieDict dict = new DereferencedTrieDict();
        // Verify that the dictionary is empty by checking that a get operation returns null
        BytesRef key = new BytesRef("test");
        assertNull(dict.get(key), "Newly created DereferencedTrieDict should not contain any entries");
        // Verify that the getEntries method returns null as per the current implementation
        assertNull(dict.getEntries(), "getEntries should return null for a new DereferencedTrieDict");
    }

    /**
     * Test that the DereferencedTrieDict constructor initializes an empty dictionary.
     */
    @Test
    public void testDereferencedTrieDictConstructor() {
        DereferencedTrieDict dict = new DereferencedTrieDict();
        // Verify that the dictionary is empty
        assertNull(dict.get(new BytesRef("anyKey")));
        assertNull(dict.getEntries());
    }

    /**
     * Test that the DereferencedTrieDict constructor does not throw exceptions.
     */
    @Test
    public void testDereferencedTrieDictConstructorNoExceptions() {
        assertDoesNotThrow(() -> new DereferencedTrieDict());
    }

    /**
     * Test case for get method when a key exists in the dictionary
     */
    @Test
    public void testGetExistingKey() {
        DereferencedTrieDict dict = new DereferencedTrieDict();
        BytesRef key = new BytesRef("hello");
        BytesRef value = new BytesRef("world");
        dict.put(key, value);
        BytesRef result = dict.get(key);
        assertNotNull(result);
        assertEquals(value, result);
    }

    @Test
    public void testGetWithEmptyKey() {
        /**
         * Test the get method with an empty key.
         * This tests the scenario where the input is empty.
         */
        DereferencedTrieDict dict = new DereferencedTrieDict();
        BytesRef emptyKey = new BytesRef(new byte[0]);
        assertNull(dict.get(emptyKey), "Get with empty key should return null");
    }

    @Test
    public void testGetWithNullKey() {
        /**
         * Test the get method with a null key.
         * This tests the scenario where the input is invalid (null).
         */
        DereferencedTrieDict dict = new DereferencedTrieDict();
        assertThrows(NullPointerException.class, () -> dict.get(null), "Get with null key should throw NullPointerException");
    }

    @Test
    public void testGetWithNonExistentKey() {
        /**
         * Test the get method with a key that doesn't exist in the dictionary.
         * This tests an edge case for the get method.
         */
        DereferencedTrieDict dict = new DereferencedTrieDict();
        BytesRef nonExistentKey = new BytesRef(new byte[] { 1, 2, 3 });
        assertNull(dict.get(nonExistentKey), "Get with non-existent key should return null");
    }

    @Test
    public void testGetWithLargeKey() {
        /**
         * Test the get method with a very large key.
         * This tests the scenario where the input might be outside accepted bounds.
         */
        // 1 million bytes
        DereferencedTrieDict dict = new DereferencedTrieDict();
        byte[] largeKeyBytes = new byte[1000000];
        BytesRef largeKey = new BytesRef(largeKeyBytes);
        assertNull(dict.get(largeKey), "Get with large key should return null");
    }

    /**
     * Test that getEntries() returns null as it is not implemented
     */
    @Test
    public void testGetEntriesReturnsNull() {
        // Arrange
        DereferencedTrieDict dict = new DereferencedTrieDict();
        // Act & Assert
        assertNull(dict.getEntries(), "getEntries() should return null as it is not implemented");
    }

    /**
     * Test that getEntries returns null for an empty dictionary.
     * This tests the edge case of an empty input.
     */
    @Test
    public void testGetEntriesReturnsNullForEmptyDictionary() {
        DereferencedTrieDict dict = new DereferencedTrieDict();
        assertNull(dict.getEntries(), "getEntries should return null for an empty dictionary");
    }

    /**
     * Test that getEntries returns null for a non-empty dictionary.
     * This tests that the method always returns null, even when entries exist.
     */
    @Test
    public void testGetEntriesReturnsNullForNonEmptyDictionary() {
        DereferencedTrieDict dict = new DereferencedTrieDict();
        dict.put(new BytesRef("key"), new BytesRef("value"));
        assertNull(dict.getEntries(), "getEntries should return null even for a non-empty dictionary");
    }

    /**
     * Test that getEntries returns a null Collection, not an empty one.
     * This tests the specific implementation detail of returning null instead of an empty collection.
     */
    @Test
    public void testGetEntriesReturnsNullNotEmptyCollection() {
        DereferencedTrieDict dict = new DereferencedTrieDict();
        Collection<Map.Entry<BytesRef, BytesRef>> entries = dict.getEntries();
        assertNull(entries, "getEntries should return null, not an empty collection");
    }

    /**
     * Test case for put method when adding a new key-value pair
     */
    @Test
    public void testPutNewKeyValuePair() {
        DereferencedTrieDict dict = new DereferencedTrieDict();
        BytesRef key = new BytesRef("testKey");
        BytesRef value = new BytesRef("testValue");
        dict.put(key, value);
        BytesRef retrievedValue = dict.get(key);
        assertNotNull(retrievedValue, "Retrieved value should not be null");
        assertEquals(value, retrievedValue, "Retrieved value should match the inserted value");
    }

    @Test
    public void testPutWithNullKey() {
        DereferencedTrieDict dict = new DereferencedTrieDict();
        assertThrows(AssertionError.class, () -> dict.put(null, new BytesRef("value")));
    }

    @Test
    public void testPutWithNullValue() {
        /**
         * Test putting a null value into the dictionary.
         * This should throw a NullPointerException.
         */
        DereferencedTrieDict dict = new DereferencedTrieDict();
        assertThrows(AssertionError.class, () -> dict.put(new BytesRef("key"), null));
    }

    @Test
    public void testPutWithEmptyKey() {
        /**
         * Test putting an empty key into the dictionary.
         * This should be allowed and not throw an exception.
         */
        DereferencedTrieDict dict = new DereferencedTrieDict();
        BytesRef emptyKey = new BytesRef("");
        BytesRef value = new BytesRef("value");
        assertDoesNotThrow(() -> dict.put(emptyKey, value));
        assertEquals(value, dict.get(emptyKey));
    }

    @Test
    public void testPutWithEmptyValue() {
        /**
         * Test putting an empty value into the dictionary.
         * This should be allowed and not throw an exception.
         */
        DereferencedTrieDict dict = new DereferencedTrieDict();
        BytesRef key = new BytesRef("key");
        BytesRef emptyValue = new BytesRef("");
        assertDoesNotThrow(() -> dict.put(key, emptyValue));
        assertEquals(emptyValue, dict.get(key));
    }

    @Test
    public void testPutWithLargeKey() {
        /**
         * Test putting a very large key into the dictionary.
         * This should be allowed and not throw an exception.
         */
        // 100KB key
        DereferencedTrieDict dict = new DereferencedTrieDict();
        byte[] largeKeyBytes = new byte[10000];
        BytesRef largeKey = new BytesRef(largeKeyBytes);
        BytesRef value = new BytesRef("value");
        assertDoesNotThrow(() -> dict.put(largeKey, value));
        assertEquals(value, dict.get(largeKey));
    }

    @Test
    public void testPutWithLargeValue() {
        /**
         * Test putting a very large value into the dictionary.
         * This should be allowed and not throw an exception.
         */
        DereferencedTrieDict dict = new DereferencedTrieDict();
        BytesRef key = new BytesRef("key");
        // 1MB value
        byte[] largeValueBytes = new byte[1000000];
        BytesRef largeValue = new BytesRef(largeValueBytes);
        assertDoesNotThrow(() -> dict.put(key, largeValue));
        assertEquals(largeValue, dict.get(key));
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
