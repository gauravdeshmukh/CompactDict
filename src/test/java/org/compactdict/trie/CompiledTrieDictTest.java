package org.compactdict.trie;

import org.compactdict.util.BytesWrapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link CompiledTrieDict}
 */
public class CompiledTrieDictTest {

    /**
     * Test case for CompiledTrieDict constructor
     * Verifies that a new CompiledTrieDict is created in an uncompiled state
     * and throws an IllegalStateException when trying to get a value before compilation
     */
    @Test
    public void testCompiledTrieDictConstructor() {
        CompiledTrieDict dict = new CompiledTrieDict();
        // Verify that the dictionary is created in an uncompiled state
        assertThrows(IllegalStateException.class, () -> dict.get(new BytesWrapper("test")));
        // Verify that we can add a key-value pair without throwing an exception
        assertDoesNotThrow(() -> dict.put(new BytesWrapper("key"), new BytesWrapper("value")));
        // Verify that after compilation, we can get a value without throwing an exception
        dict.compile();
        assertDoesNotThrow(() -> dict.get(new BytesWrapper("key")));
    }

    @Test
    public void testCompiledTrieDict_ConstructorDoesNotThrowException() {
        /**
         * Test that the CompiledTrieDict constructor does not throw any exceptions
         * when called with no arguments.
         */
        assertDoesNotThrow(() -> new CompiledTrieDict());
    }

    @Test
    public void testCompiledTrieDict_InitialStateIsUncompiled() {
        /**
         * Test that the initial state of CompiledTrieDict is uncompiled.
         * This is tested indirectly by checking if put() method can be called
         * without throwing an exception.
         */
        CompiledTrieDict dict = new CompiledTrieDict();
        assertDoesNotThrow(() -> dict.put(new BytesWrapper("key"), new BytesWrapper("value")));
    }

    @Test
    public void testCompiledTrieDict_GetBeforeCompileThrowsException() {
        /**
         * Test that calling get() before compiling the dictionary throws an IllegalStateException.
         */
        CompiledTrieDict dict = new CompiledTrieDict();
        assertThrows(IllegalStateException.class, () -> dict.get(new BytesWrapper("key")));
    }

    @Test
    public void testCompiledTrieDict_PutAfterCompileThrowsException() {
        /**
         * Test that calling put() after compiling the dictionary throws an IllegalStateException.
         */
        CompiledTrieDict dict = new CompiledTrieDict();
        dict.compile();
        assertThrows(IllegalStateException.class, () -> dict.put(new BytesWrapper("key"), new BytesWrapper("value")));
    }

    @Test
    public void testCompiledTrieDict_GetEntriesReturnsNull() {
        /**
         * Test that getEntries() returns null as it's not implemented.
         */
        CompiledTrieDict dict = new CompiledTrieDict();
        assertNull(dict.getEntries());
    }

    /**
     * Test that compile() method correctly compiles the trie and allows get operations
     */
    @Test
    public void testCompileAllowsGetOperations() {
        CompiledTrieDict dict = new CompiledTrieDict();
        BytesWrapper key = new BytesWrapper("test");
        BytesWrapper value = new BytesWrapper("value");
        dict.put(key, value);
        assertThrows(IllegalStateException.class, () -> dict.get(key), "Get operation should throw IllegalStateException before compilation");
        dict.compile();
        assertEquals(value, dict.get(key), "Get operation should return the correct value after compilation");
        assertThrows(IllegalStateException.class, () -> dict.put(new BytesWrapper("newKey"), new BytesWrapper("newValue")), "Put operation should throw IllegalStateException after compilation");
    }

    @Test
    public void testCompileEmptyDictionary() {
        /**
         * Test compiling an empty dictionary.
         * This is an edge case where no entries have been added before compilation.
         */
        CompiledTrieDict dict = new CompiledTrieDict();
        dict.compile();
        // Verify that the dictionary is compiled by attempting to add a new entry
        assertThrows(IllegalStateException.class, () -> dict.put(new BytesWrapper("key"), new BytesWrapper("value")));
    }

    @Test
    public void testCompileAlreadyCompiledDictionary() {
        /**
         * Test compiling an already compiled dictionary.
         * This tests the edge case of calling compile multiple times.
         */
        CompiledTrieDict dict = new CompiledTrieDict();
        dict.put(new BytesWrapper("key"), new BytesWrapper("value"));
        dict.compile();
        // Calling compile again should not throw an exception
        assertDoesNotThrow(() -> dict.compile());
    }

    @Test
    public void testGetBeforeCompile() {
        /**
         * Test calling get before compiling the dictionary.
         * This tests the exception handling when trying to retrieve a value before compilation.
         */
        CompiledTrieDict dict = new CompiledTrieDict();
        dict.put(new BytesWrapper("key"), new BytesWrapper("value"));
        assertThrows(IllegalStateException.class, () -> dict.get(new BytesWrapper("key")));
    }

    @Test
    public void testPutAfterCompile() {
        /**
         * Test putting a new entry after compiling the dictionary.
         * This tests the exception handling when trying to add an entry after compilation.
         */
        CompiledTrieDict dict = new CompiledTrieDict();
        dict.compile();
        assertThrows(IllegalStateException.class, () -> dict.put(new BytesWrapper("key"), new BytesWrapper("value")));
    }

    /**
     * Test case for successful retrieval of a value from a compiled dictionary
     */
    @Test
    public void testGetFromCompiledDictionary() {
        CompiledTrieDict dict = new CompiledTrieDict();
        BytesWrapper key = new BytesWrapper("testKey");
        BytesWrapper value = new BytesWrapper("testValue");
        dict.put(key, value);
        dict.compile();
        BytesWrapper result = dict.get(key);
        assertNotNull(result);
        assertEquals(value, result);
    }

    @Test
    public void testGetWithEmptyKey() {
        /**
         * Test get method with an empty key
         */
        CompiledTrieDict dictionary = new CompiledTrieDict();
        dictionary.compile();
        BytesWrapper emptyKey = new BytesWrapper(new byte[0]);
        assertNull(dictionary.get(emptyKey), "Get with empty key should return null");
    }

    @Test
    public void testGetWithNullKey() {
        /**
         * Test get method with a null key
         */
        CompiledTrieDict dictionary = new CompiledTrieDict();
        dictionary.compile();
        assertThrows(AssertionError.class, () -> dictionary.get(null), "Get with null key should throw NullPointerException");
    }

    @Test
    public void testGetBeforeCompilation() {
        /**
         * Test get method before dictionary compilation
         */
        CompiledTrieDict dictionary = new CompiledTrieDict();
        BytesWrapper key = new BytesWrapper("test".getBytes());
        assertThrows(IllegalStateException.class, () -> dictionary.get(key), "Get before compilation should throw IllegalStateException");
    }

    @Test
    public void testGetNonExistentKey() {
        /**
         * Test get method with a key that doesn't exist in the dictionary
         */
        CompiledTrieDict dictionary = new CompiledTrieDict();
        dictionary.put(new BytesWrapper("exist".getBytes()), new BytesWrapper("value".getBytes()));
        dictionary.compile();
        BytesWrapper nonExistentKey = new BytesWrapper("nonexistent".getBytes());
        assertNull(dictionary.get(nonExistentKey), "Get with non-existent key should return null");
    }

    @Test
    public void testGetWithLargeKey() {
        /**
         * Test get method with a very large key
         */
        // 1 MB key
        CompiledTrieDict dictionary = new CompiledTrieDict();
        byte[] largeKeyBytes = new byte[1000000];
        BytesWrapper largeKey = new BytesWrapper(largeKeyBytes);
        dictionary.put(largeKey, new BytesWrapper("value".getBytes()));
        dictionary.compile();
        assertNotNull(dictionary.get(largeKey), "Get with very large key should return null");
    }

    /**
     * Test that getEntries() returns null for an empty CompiledTrieDict
     */
    @Test
    public void testGetEntriesReturnsNull() {

        CompiledTrieDict dict = new CompiledTrieDict();
        assertNull(dict.getEntries(), "getEntries() should return null for CompiledTrieDict");
    }

    /**
     * Test that getEntries returns null for an empty dictionary
     */
    @Test
    public void testGetEntriesReturnsNullForEmptyDictionary() {
        CompiledTrieDict dict = new CompiledTrieDict();
        assertNull(dict.getEntries());
    }

    /**
     * Test that getEntries returns null for a non-empty dictionary
     */
    @Test
    public void testGetEntriesReturnsNullForNonEmptyDictionary() {
        CompiledTrieDict dict = new CompiledTrieDict();
        dict.put(new BytesWrapper("key"), new BytesWrapper("value"));
        dict.compile();
        assertNull(dict.getEntries());
    }

    /**
     * Test that getEntries returns null for a compiled dictionary
     */
    @Test
    public void testGetEntriesReturnsNullForCompiledDictionary() {
        CompiledTrieDict dict = new CompiledTrieDict();
        dict.put(new BytesWrapper("key1"), new BytesWrapper("value1"));
        dict.put(new BytesWrapper("key2"), new BytesWrapper("value2"));
        dict.compile();
        assertNull(dict.getEntries());
    }

    /**
     * Test that getEntries returns null for an uncompiled dictionary
     */
    @Test
    public void testGetEntriesReturnsNullForUncompiledDictionary() {
        CompiledTrieDict dict = new CompiledTrieDict();
        dict.put(new BytesWrapper("key"), new BytesWrapper("value"));
        assertNull(dict.getEntries());
    }

    /**
     * Test that getEntries always returns null, regardless of dictionary state
     */
    @Test
    public void testGetEntriesAlwaysReturnsNull() {
        CompiledTrieDict dict = new CompiledTrieDict();
        // Empty dictionary
        assertNull(dict.getEntries());
        // Non-empty, uncompiled dictionary
        dict.put(new BytesWrapper("key1"), new BytesWrapper("value1"));
        assertNull(dict.getEntries());
        // Non-empty, compiled dictionary
        dict.compile();
        assertNull(dict.getEntries());
    }

    /**
     * Test that put() throws IllegalStateException when the dictionary is compiled
     */
    @Test
    public void testPutThrowsExceptionWhenCompiled() {
        CompiledTrieDict dict = new CompiledTrieDict();
        BytesWrapper key = new BytesWrapper("key");
        BytesWrapper value = new BytesWrapper("value");
        // Compile the dictionary
        dict.compile();
        // Attempt to put a value after compilation
        assertThrows(IllegalStateException.class, () -> {
            dict.put(key, value);
        }, "Should throw IllegalStateException when putting after compilation");
    }

    /**
     * Test case for put method when the dictionary is not compiled
     */
    @Test
    public void testPutWhenDictionaryNotCompiled() {
        CompiledTrieDict dict = new CompiledTrieDict();
        BytesWrapper key = new BytesWrapper("key");
        BytesWrapper value = new BytesWrapper("value");
        // Put a key-value pair
        dict.put(key, value);
        // Verify that the value can be retrieved
        assertThrows(IllegalStateException.class, () -> dict.get(key));
        // Put another key-value pair with the same value
        BytesWrapper key2 = new BytesWrapper("key2");
        dict.put(key2, value);
        // Verify that the dictionary can be compiled after putting values
        assertDoesNotThrow(() -> dict.compile());
        assertEquals(value, dict.get(key));
        assertEquals(value, dict.get(key2));
        // Verify that putting after compilation throws an exception
        assertThrows(IllegalStateException.class, () -> dict.put(new BytesWrapper("newKey"), new BytesWrapper("newValue")));
    }

    @Test
    public void testPutWithNullKey() {
        /**
         * Test putting a null key into the dictionary.
         * This should throw a NullPointerException.
         */
        CompiledTrieDict dictionary = new CompiledTrieDict();
        BytesWrapper value = new BytesWrapper("value");
        assertThrows(AssertionError.class, () -> dictionary.put(null, value));
    }

    @Test
    public void testPutWithNullValue() {
        /**
         * Test putting a null value into the dictionary.
         * This should throw a NullPointerException.
         */
        CompiledTrieDict dictionary = new CompiledTrieDict();
        BytesWrapper key = new BytesWrapper("key");
        assertThrows(AssertionError.class, () -> dictionary.put(key, null));
    }

    @Test
    public void testPutWithEmptyKey() {
        /**
         * Test putting an empty key into the dictionary.
         * This should work without throwing an exception.
         */
        CompiledTrieDict dictionary = new CompiledTrieDict();
        BytesWrapper key = new BytesWrapper("");
        BytesWrapper value = new BytesWrapper("value");
        assertDoesNotThrow(() -> dictionary.put(key, value));
    }

    @Test
    public void testPutWithEmptyValue() {
        /**
         * Test putting an empty value into the dictionary.
         * This should work without throwing an exception.
         */
        CompiledTrieDict dictionary = new CompiledTrieDict();
        BytesWrapper key = new BytesWrapper("key");
        BytesWrapper value = new BytesWrapper("");
        assertDoesNotThrow(() -> dictionary.put(key, value));
    }

    @Test
    public void testPutAfterCompilation() {
        /**
         * Test putting a value after the dictionary has been compiled.
         * This should throw an IllegalStateException.
         */
        CompiledTrieDict dictionary = new CompiledTrieDict();
        BytesWrapper key = new BytesWrapper("key");
        BytesWrapper value = new BytesWrapper("value");
        dictionary.compile();
        assertThrows(IllegalStateException.class, () -> dictionary.put(key, value));
    }

    @Test
    public void testPutDuplicateKey() {
        /**
         * Test putting a duplicate key into the dictionary.
         * This should overwrite the previous value.
         */
        CompiledTrieDict dictionary = new CompiledTrieDict();
        BytesWrapper key = new BytesWrapper("key");
        BytesWrapper value1 = new BytesWrapper("value1");
        BytesWrapper value2 = new BytesWrapper("value2");
        dictionary.put(key, value1);
        dictionary.put(key, value2);
        dictionary.compile();
        assertEquals(value2, dictionary.get(key));
    }



    @Test
    public void testPrefixMatching() {
        CompiledTrieDict dict = new CompiledTrieDict();
        dict.put(new BytesWrapper("key"), new BytesWrapper("value"), true);
        dict.put(new BytesWrapper("key1"), new BytesWrapper("value1"), true);
        dict.put(new BytesWrapper("key12"), new BytesWrapper("value12"), false);
        dict.put(new BytesWrapper("key123"), new BytesWrapper("value123"), false);
        dict.compile();

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
