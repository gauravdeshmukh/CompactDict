package org.compactdict.trie;

import org.compactdict.BytesRef;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link Node}
 */
public class NodeTest {

    /**
     * Test case for Node() constructor
     * Verifies that a new Node is created with an empty children map
     */
    @Test
    public void testNodeConstructorCreatesEmptyChildrenMap() throws NoSuchFieldException, IllegalAccessException {
        // Create a new Node
        Node node = new Node();
        // Use reflection to access the private children field
        Field childrenField = Node.class.getDeclaredField("children");
        childrenField.setAccessible(true);
        Map<Byte, Node> children = (Map<Byte, Node>) childrenField.get(node);
        // Assert that the children map is not null and is empty
        assertNotNull(children, "Children map should not be null");
        assertTrue(children.isEmpty(), "Children map should be empty");
    }

    @Test
    public void testNodeConstructorCreatesEmptyNode() {
        Node node = new Node();
        assertTrue(node.isLeaf(), "Newly created node should be a leaf");
        assertFalse(node.isValidEndOfKey(), "Newly created node should not be a valid end of key");
    }

    @Test
    public void testPutWithNullKey() {
        Node node = new Node();
        assertThrows(NullPointerException.class, () -> node.put(null, new BytesRef("value"), 0, false));
    }

    @Test
    public void testPutWithNullValue() {
        Node node = new Node();
        BytesRef key = new BytesRef("key");
        node.put(key, null, 0, false);
        assertNull(node.get(key, 0), "Putting null value should result in null when retrieved");
    }

    @Test
    public void testGetWithNullKey() {
        Node node = new Node();
        assertThrows(NullPointerException.class, () -> node.get(null, 0));
    }

    @Test
    public void testPutWithNegativeOffset() {
        Node node = new Node();
        BytesRef key = new BytesRef("key");
        BytesRef value = new BytesRef("value");
        assertThrows(AssertionError.class, () -> node.put(key, value, -1, false));
    }

    @Test
    public void testPutWithOffsetLargerThanKeyLength() {
        Node node = new Node();
        BytesRef key = new BytesRef("key");
        BytesRef value = new BytesRef("value");
        assertThrows(AssertionError.class, () -> node.put(key, value, key.getLength()+1, false));
    }

    @Test
    public void testGetWithNegativeOffset() {
        Node node = new Node();
        BytesRef key = new BytesRef("key");
        assertThrows(AssertionError.class, () -> node.get(key, -1));
    }

    @Test
    public void testGetWithOffsetLargerThanKeyLength() {
        Node node = new Node();
        BytesRef key = new BytesRef("key");
        assertThrows(AssertionError.class, () -> node.get(key, key.getLength()+1));
    }

    @Test
    public void testPutWithOffsetEqualToKeyLength() {
        Node node = new Node();
        BytesRef key = new BytesRef("key");
        BytesRef value = new BytesRef("value");
        node.put(key, value, key.getLength(), false);
        assertTrue(node.isValidEndOfKey(), "Node should be valid end of key when offset is equal to key length");
    }

    @Test
    public void testGetWithOffsetEqualToKeyLength() {
        Node node = new Node();
        BytesRef key = new BytesRef("key");
        BytesRef value = new BytesRef("value");
        node.put(key, value, 0, false);
        assertNull(node.get(key, key.getLength()), "Get should return null when offset equal key length");
    }

    @Test
    public void testPutWithPrefixKey() {
        Node node = new Node();
        BytesRef key = new BytesRef("key");
        BytesRef value = new BytesRef("value");
        node.put(key, value, key.getLength(), true);
        assertTrue(node.isValidEndOfKey(), "Node should be valid end of key when offset is equal to key length");
        assertTrue(node.isPrefixEnd());
        assertEquals(value, node.getValue());
    }

    @Test
    public void testGetWithMultiplePrefixKey() {
        Node node = new Node();
        node.put(new BytesRef("key"), new BytesRef("value"), 0, true);
        node.put(new BytesRef("key1"), new BytesRef("value1"), 0, true);
        node.put(new BytesRef("key12"), new BytesRef("value12"), 0, false);
        node.put(new BytesRef("key123"), new BytesRef("value123"), 0, false);

        assertEquals(new BytesRef("value"), node.get(new BytesRef("key"), 0));
        assertEquals(new BytesRef("value1"), node.get(new BytesRef("key1"), 0));
        assertEquals(new BytesRef("value12"), node.get(new BytesRef("key12"), 0));
        assertEquals(new BytesRef("value123"), node.get(new BytesRef("key123"), 0));
        assertEquals(new BytesRef("value1"), node.get(new BytesRef("key111"), 0));
        assertEquals(new BytesRef("value1"), node.get(new BytesRef("key121"), 0));
        assertEquals(new BytesRef("value"), node.get(new BytesRef("key21"), 0));
    }
}
