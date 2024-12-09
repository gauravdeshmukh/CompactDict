package org.compactdict.trie;

import org.compactdict.util.BytesWrapper;
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
        BytesWrapper key = new BytesWrapper("test");
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
        assertNull(dict.get(new BytesWrapper("anyKey")));
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
        BytesWrapper key = new BytesWrapper("hello");
        BytesWrapper value = new BytesWrapper("world");
        dict.put(key, value);
        BytesWrapper result = dict.get(key);
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
        BytesWrapper emptyKey = new BytesWrapper(new byte[0]);
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
        BytesWrapper nonExistentKey = new BytesWrapper(new byte[] { 1, 2, 3 });
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
        BytesWrapper largeKey = new BytesWrapper(largeKeyBytes);
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
        dict.put(new BytesWrapper("key"), new BytesWrapper("value"));
        assertNull(dict.getEntries(), "getEntries should return null even for a non-empty dictionary");
    }

    /**
     * Test that getEntries returns a null Collection, not an empty one.
     * This tests the specific implementation detail of returning null instead of an empty collection.
     */
    @Test
    public void testGetEntriesReturnsNullNotEmptyCollection() {
        DereferencedTrieDict dict = new DereferencedTrieDict();
        Collection<Map.Entry<BytesWrapper, BytesWrapper>> entries = dict.getEntries();
        assertNull(entries, "getEntries should return null, not an empty collection");
    }

    /**
     * Test case for put method when adding a new key-value pair
     */
    @Test
    public void testPutNewKeyValuePair() {
        DereferencedTrieDict dict = new DereferencedTrieDict();
        BytesWrapper key = new BytesWrapper("testKey");
        BytesWrapper value = new BytesWrapper("testValue");
        dict.put(key, value);
        BytesWrapper retrievedValue = dict.get(key);
        assertNotNull(retrievedValue, "Retrieved value should not be null");
        assertEquals(value, retrievedValue, "Retrieved value should match the inserted value");
    }

    @Test
    public void testPutWithNullKey() {
        DereferencedTrieDict dict = new DereferencedTrieDict();
        assertThrows(AssertionError.class, () -> dict.put(null, new BytesWrapper("value")));
    }

    @Test
    public void testPutWithNullValue() {
        /**
         * Test putting a null value into the dictionary.
         * This should throw a NullPointerException.
         */
        DereferencedTrieDict dict = new DereferencedTrieDict();
        assertThrows(AssertionError.class, () -> dict.put(new BytesWrapper("key"), null));
    }

    @Test
    public void testPutWithEmptyKey() {
        /**
         * Test putting an empty key into the dictionary.
         * This should be allowed and not throw an exception.
         */
        DereferencedTrieDict dict = new DereferencedTrieDict();
        BytesWrapper emptyKey = new BytesWrapper("");
        BytesWrapper value = new BytesWrapper("value");
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
        BytesWrapper key = new BytesWrapper("key");
        BytesWrapper emptyValue = new BytesWrapper("");
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
        BytesWrapper largeKey = new BytesWrapper(largeKeyBytes);
        BytesWrapper value = new BytesWrapper("value");
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
        BytesWrapper key = new BytesWrapper("key");
        // 1MB value
        byte[] largeValueBytes = new byte[1000000];
        BytesWrapper largeValue = new BytesWrapper(largeValueBytes);
        assertDoesNotThrow(() -> dict.put(key, largeValue));
        assertEquals(largeValue, dict.get(key));
    }



    @Test
    public void testPrefixMatching() {
        BasicTrieDict dict = new BasicTrieDict();
        dict.put(new BytesWrapper("key"), new BytesWrapper("value"), true);
        dict.put(new BytesWrapper("key1"), new BytesWrapper("value1"), true);
        dict.put(new BytesWrapper("key12"), new BytesWrapper("value12"), false);
        dict.put(new BytesWrapper("key123"), new BytesWrapper("value123"), false);

        assertEquals(new BytesWrapper("value"), dict.get(new BytesWrapper("key")));
        assertEquals(new BytesWrapper("value1"), dict.get(new BytesWrapper("key1")));
        assertEquals(new BytesWrapper("value12"), dict.get(new BytesWrapper("key12")));
        assertEquals(new BytesWrapper("value123"), dict.get(new BytesWrapper("key123")));
        assertEquals(new BytesWrapper("value1"), dict.get(new BytesWrapper("key111")));
        assertEquals(new BytesWrapper("value1"), dict.get(new BytesWrapper("key121")));
        assertEquals(new BytesWrapper("value"), dict.get(new BytesWrapper("key21")));
        assertNull(dict.get(new BytesWrapper("ke1y")));
    }
}
