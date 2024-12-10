package org.compactdict.trie;

import org.compactdict.util.BytesWrapper;
import org.compactdict.util.ByteBuffer;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link CompiledNode}
 */
public class CompiledNodeTest {

    /**
     * Test case for the default constructor of CompiledNode
     * Verifies that a newly created CompiledNode is not compiled and has no children
     */
    @Test
    public void testDefaultConstructor() {
        CompiledNode node = new CompiledNode();
        assertEquals(0, node.getInputByte(), "Input byte should be 0 for default constructor");
        // Attempt to get a non-existent child
//        assertNull(node.get(new BytesRef(new byte[] { 1 })), "Get operation on empty node should return null");
    }

    @Test
    public void testCompiledNodeConstructorWithNullValue() {
        /**
         * Test the CompiledNode constructor with a null BytesRef value.
         * This tests the edge case of passing null as a value to the constructor.
         */
//        assertDoesNotThrow(() -> new CompiledNode((BytesRef) null), "CompiledNode constructor should not throw an exception when passed a null BytesRef");
    }

    @Test
    public void testCompiledNodePutWithNullKey() {
        /**
         * Test the put method with a null key.
         * This tests the error handling for an invalid input.
         */
        CompiledNode node = new CompiledNode();
        BytesWrapper value = new BytesWrapper(new byte[] { 1, 2, 3 });
        assertThrows(NullPointerException.class, () -> node.put(null, 0, 0, false), "put method should throw NullPointerException when key is null");
    }

    @Test
    public void testCompiledNodePutWithNegativeOffset() {
        /**
         * Test the put method with a negative offset.
         * This tests the error handling for an input outside accepted bounds.
         */
        CompiledNode node = new CompiledNode();
        BytesWrapper key = new BytesWrapper(new byte[] { 1, 2, 3 });
        BytesWrapper value = new BytesWrapper(new byte[] { 4, 5, 6 });
//        assertThrows(AssertionError.class, () -> node.put(key, value, -1, false), "put method should throw ArrayIndexOutOfBoundsException when offset is negative");
    }

    @Test
    public void testCompiledNodeGetWithNullKey() {
        /**
         * Test the get method with a null key.
         * This tests the error handling for an invalid input.
         */
        CompiledNode node = new CompiledNode();
//        assertThrows(NullPointerException.class, () -> node.get(null), "get method should throw NullPointerException when key is null");
    }

    @Test
    public void testPutWithOffsetEqualToKeyLength() {
        Node node = new Node();
        BytesWrapper key = new BytesWrapper("key");
        BytesWrapper value = new BytesWrapper("value");
        node.put(key, value, key.getLength(), false);
        assertTrue(node.isValidEndOfKey(), "Node should be valid end of key when offset is equal to key length");
    }

    @Test
    public void testGetWithOffsetEqualToKeyLength() {
        Node node = new Node();
        BytesWrapper key = new BytesWrapper("key");
        BytesWrapper value = new BytesWrapper("value");
        node.put(key, value, 0, false);
        assertNull(node.get(key, key.getLength()), "Get should return null when offset equal key length");
    }

    @Test
    public void testPutWithPrefixKey() {
        Node node = new Node();
        BytesWrapper key = new BytesWrapper("key");
        BytesWrapper value = new BytesWrapper("value");
        node.put(key, value, key.getLength(), true);
        assertTrue(node.isValidEndOfKey(), "Node should be valid end of key when offset is equal to key length");
        assertTrue(node.isPrefixEnd());
        assertEquals(value, node.getValue());
    }

    @Test
    public void testGetWithMultiplePrefixKey() {
        Node node = new Node();
        node.put(new BytesWrapper("key"), new BytesWrapper("value"), 0, true);
        node.put(new BytesWrapper("key1"), new BytesWrapper("value1"), 0, true);
        node.put(new BytesWrapper("key12"), new BytesWrapper("value12"), 0, false);
        node.put(new BytesWrapper("key123"), new BytesWrapper("value123"), 0, false);

        assertEquals(new BytesWrapper("value"), node.get(new BytesWrapper("key"), 0));
        assertEquals(new BytesWrapper("value1"), node.get(new BytesWrapper("key1"), 0));
        assertEquals(new BytesWrapper("value12"), node.get(new BytesWrapper("key12"), 0));
        assertEquals(new BytesWrapper("value123"), node.get(new BytesWrapper("key123"), 0));
        assertEquals(new BytesWrapper("value1"), node.get(new BytesWrapper("key111"), 0));
        assertEquals(new BytesWrapper("value1"), node.get(new BytesWrapper("key121"), 0));
        assertEquals(new BytesWrapper("value"), node.get(new BytesWrapper("key21"), 0));
    }
}
