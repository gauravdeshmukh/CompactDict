package org.compactdict.trie;

import org.compactdict.util.BytesWrapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link ValuePrefixedCompiledTrieDict}
 */
public class ValuePrefixedCompiledTrieDictTest {
    @Test
    public void testValuePrefixedCompiledTrieDictConstructor() {
        ValuePrefixedCompiledTrieDict dict = new ValuePrefixedCompiledTrieDict();
        // Verify that the dictionary is created in an uncompiled state
        assertThrows(IllegalStateException.class, () -> dict.get(new BytesWrapper("test")));
        // Verify that we can add a key-value pair without throwing an exception
        assertDoesNotThrow(() -> dict.put(new BytesWrapper("key"), new BytesWrapper("value")));
        // Verify that after compilation, we can get a value without throwing an exception
        dict.compile();
        assertDoesNotThrow(() -> dict.get(new BytesWrapper("key")));
    }

    @Test
    public void testValuePrefixedCompiledTrieDict_ConstructorDoesNotThrowException() {
       assertDoesNotThrow(() -> new ValuePrefixedCompiledTrieDict());
    }

    @Test
    public void testValuePrefixedCompiledTrieDict_InitialStateIsUncompiled() {
        ValuePrefixedCompiledTrieDict dict = new ValuePrefixedCompiledTrieDict();
        assertDoesNotThrow(() -> dict.put(new BytesWrapper("key"), new BytesWrapper("value")));
    }

    @Test
    public void testValuePrefixedCompiledTrieDict_GetBeforeCompileThrowsException() {
        ValuePrefixedCompiledTrieDict dict = new ValuePrefixedCompiledTrieDict();
        assertThrows(IllegalStateException.class, () -> dict.get(new BytesWrapper("key")));
    }

    @Test
    public void testValuePrefixedCompiledTrieDict_PutAfterCompileThrowsException() {
        ValuePrefixedCompiledTrieDict dict = new ValuePrefixedCompiledTrieDict();
        dict.compile();
        assertThrows(IllegalStateException.class, () -> dict.put(new BytesWrapper("key"), new BytesWrapper("value")));
    }

    @Test
    public void testValuePrefixedCompiledTrieDict_GetEntriesReturnsNull() {
        ValuePrefixedCompiledTrieDict dict = new ValuePrefixedCompiledTrieDict();
        assertNull(dict.getEntries());
    }

    @Test
    public void testCompileAllowsGetOperations() {
        ValuePrefixedCompiledTrieDict dict = new ValuePrefixedCompiledTrieDict();
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
        ValuePrefixedCompiledTrieDict dict = new ValuePrefixedCompiledTrieDict();
        dict.compile();
        assertThrows(IllegalStateException.class, () -> dict.put(new BytesWrapper("key"), new BytesWrapper("value")));
    }

    @Test
    public void testCompileAlreadyCompiledDictionary() {
        ValuePrefixedCompiledTrieDict dict = new ValuePrefixedCompiledTrieDict();
        dict.put(new BytesWrapper("key"), new BytesWrapper("value"));
        dict.compile();
        assertDoesNotThrow(() -> dict.compile());
    }

    @Test
    public void testGetBeforeCompile() {
        ValuePrefixedCompiledTrieDict dict = new ValuePrefixedCompiledTrieDict();
        dict.put(new BytesWrapper("key"), new BytesWrapper("value"));
        assertThrows(IllegalStateException.class, () -> dict.get(new BytesWrapper("key")));
    }

    @Test
    public void testPutAfterCompile() {
        ValuePrefixedCompiledTrieDict dict = new ValuePrefixedCompiledTrieDict();
        dict.compile();
        assertThrows(IllegalStateException.class, () -> dict.put(new BytesWrapper("key"), new BytesWrapper("value")));
    }

    @Test
    public void testGetFromCompiledDictionary() {
        ValuePrefixedCompiledTrieDict dict = new ValuePrefixedCompiledTrieDict();
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
        ValuePrefixedCompiledTrieDict dictionary = new ValuePrefixedCompiledTrieDict();
        dictionary.compile();
        BytesWrapper emptyKey = new BytesWrapper(new byte[0]);
        assertNull(dictionary.get(emptyKey), "Get with empty key should return null");
    }

    @Test
    public void testGetWithNullKey() {
        ValuePrefixedCompiledTrieDict dictionary = new ValuePrefixedCompiledTrieDict();
        dictionary.compile();
        assertThrows(AssertionError.class, () -> dictionary.get(null), "Get with null key should throw NullPointerException");
    }

    @Test
    public void testGetBeforeCompilation() {
        ValuePrefixedCompiledTrieDict dictionary = new ValuePrefixedCompiledTrieDict();
        BytesWrapper key = new BytesWrapper("test".getBytes());
        assertThrows(IllegalStateException.class, () -> dictionary.get(key), "Get before compilation should throw IllegalStateException");
    }

    @Test
    public void testGetNonExistentKey() {
        ValuePrefixedCompiledTrieDict dictionary = new ValuePrefixedCompiledTrieDict();
        dictionary.put(new BytesWrapper("exist".getBytes()), new BytesWrapper("value".getBytes()));
        dictionary.compile();
        BytesWrapper nonExistentKey = new BytesWrapper("nonexistent".getBytes());
        assertNull(dictionary.get(nonExistentKey), "Get with non-existent key should return null");
    }

    @Test
    public void testGetWithLargeKey() {
        // 1 MB key
        ValuePrefixedCompiledTrieDict dictionary = new ValuePrefixedCompiledTrieDict();
        byte[] largeKeyBytes = new byte[1000000];
        BytesWrapper largeKey = new BytesWrapper(largeKeyBytes);
        dictionary.put(largeKey, new BytesWrapper("value".getBytes()));
        dictionary.compile();
        assertNotNull(dictionary.get(largeKey), "Get with very large key should return null");
    }

    @Test
    public void testGetEntriesReturnsNullForEmptyDictionary() {
        ValuePrefixedCompiledTrieDict dict = new ValuePrefixedCompiledTrieDict();
        assertNull(dict.getEntries());
    }

    @Test
    public void testGetEntriesReturnsNullForNonEmptyDictionary() {
        ValuePrefixedCompiledTrieDict dict = new ValuePrefixedCompiledTrieDict();
        dict.put(new BytesWrapper("key"), new BytesWrapper("value"));
        dict.compile();
        assertNull(dict.getEntries());
    }

    @Test
    public void testGetEntriesReturnsNullForCompiledDictionary() {
        ValuePrefixedCompiledTrieDict dict = new ValuePrefixedCompiledTrieDict();
        dict.put(new BytesWrapper("key1"), new BytesWrapper("value1"));
        dict.put(new BytesWrapper("key2"), new BytesWrapper("value2"));
        dict.compile();
        assertNull(dict.getEntries());
    }

    @Test
    public void testGetEntriesReturnsNullForUncompiledDictionary() {
        ValuePrefixedCompiledTrieDict dict = new ValuePrefixedCompiledTrieDict();
        dict.put(new BytesWrapper("key"), new BytesWrapper("value"));
        assertNull(dict.getEntries());
    }

    @Test
    public void testGetEntriesAlwaysReturnsNull() {
        ValuePrefixedCompiledTrieDict dict = new ValuePrefixedCompiledTrieDict();
        // Empty dictionary
        assertNull(dict.getEntries());
        // Non-empty, uncompiled dictionary
        dict.put(new BytesWrapper("key1"), new BytesWrapper("value1"));
        assertNull(dict.getEntries());
        // Non-empty, compiled dictionary
        dict.compile();
        assertNull(dict.getEntries());
    }

    @Test
    public void testPutThrowsExceptionWhenCompiled() {
        ValuePrefixedCompiledTrieDict dict = new ValuePrefixedCompiledTrieDict();
        BytesWrapper key = new BytesWrapper("key");
        BytesWrapper value = new BytesWrapper("value");
        // Compile the dictionary
        dict.compile();
        // Attempt to put a value after compilation
        assertThrows(IllegalStateException.class, () -> {
            dict.put(key, value);
        }, "Should throw IllegalStateException when putting after compilation");
    }

    @Test
    public void testPutWithSameValueForTwoKeys() {
        ValuePrefixedCompiledTrieDict dict = new ValuePrefixedCompiledTrieDict();
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
        ValuePrefixedCompiledTrieDict dictionary = new ValuePrefixedCompiledTrieDict();
        BytesWrapper value = new BytesWrapper("value");
        assertThrows(AssertionError.class, () -> dictionary.put(null, value));
    }

    @Test
    public void testPutWithNullValue() {
        ValuePrefixedCompiledTrieDict dictionary = new ValuePrefixedCompiledTrieDict();
        BytesWrapper key = new BytesWrapper("key");
        assertThrows(AssertionError.class, () -> dictionary.put(key, null));
    }

    @Test
    public void testPutWithEmptyKey() {
        ValuePrefixedCompiledTrieDict dictionary = new ValuePrefixedCompiledTrieDict();
        BytesWrapper key = new BytesWrapper("");
        BytesWrapper value = new BytesWrapper("value");
        assertDoesNotThrow(() -> dictionary.put(key, value));
    }

    @Test
    public void testPutWithEmptyValue() {
        ValuePrefixedCompiledTrieDict dictionary = new ValuePrefixedCompiledTrieDict();
        BytesWrapper key = new BytesWrapper("key");
        BytesWrapper value = new BytesWrapper("");
        assertDoesNotThrow(() -> dictionary.put(key, value));
    }

    @Test
    public void testPutDuplicateKey() {
        ValuePrefixedCompiledTrieDict dictionary = new ValuePrefixedCompiledTrieDict();
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
        ValuePrefixedCompiledTrieDict dict = new ValuePrefixedCompiledTrieDict();
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
