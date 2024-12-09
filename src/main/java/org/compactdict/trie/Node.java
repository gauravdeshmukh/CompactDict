package org.compactdict.trie;

import org.compactdict.util.BytesWrapper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a node in a trie data structure that stores BytesRef values.
 * Each node can hold a value and maintains references to child nodes indexed
 * by bytes. This implementation supports serialization for persistent storage.
 *
 * <p>The node structure is used to build a trie where each edge represents
 * a byte in the key, and values are stored at the nodes corresponding to
 * complete keys.</p>
 */
class Node implements Serializable {
    private BytesWrapper value;
    private boolean isPrefixEnd;
    private final Map<Byte, Node> children;

    /**
     * Constructs an empty node with no value and an empty children map.
     */
    public Node() {
        this.children = new HashMap<>();
        this.isPrefixEnd = false;
    }

    /**
     * Constructs a node with the specified value and an empty children map.
     *
     * @param value the BytesRef value to be stored in this node
     */
    public Node(BytesWrapper value) {
        this.value = value;
        this.children = new HashMap<>();
        this.isPrefixEnd = false;
    }

    /**
     * Recursively stores a key-value pair in the trie, creating new nodes
     * as needed for each byte in the key.
     *
     * @param key       the BytesRef key to be stored
     * @param value     the BytesRef value to be associated with the key
     * @param keyOffset the current position in the key being processed
     * @param isPrefixKey a flag indicating if the key is a prefix key
     */
    public void put(BytesWrapper key, BytesWrapper value, int keyOffset, boolean isPrefixKey) {
        Node currentNode = this;
        while (keyOffset < key.getLength()) {
            byte keyByte = key.getByteAt(keyOffset);
            currentNode = currentNode.children.computeIfAbsent(keyByte, k -> new Node());
            keyOffset++;
        }
        currentNode.value = value;
        currentNode.isPrefixEnd = isPrefixKey;
    }

    /**
     * Recursively retrieves the value associated with a key from the trie.
     *
     * @param key       the BytesRef key to look up
     * @param keyOffset the current position in the key being processed
     * @return the BytesRef value associated with the key, or null if the
     *         key is not found in the trie
     */
    public BytesWrapper get(BytesWrapper key, int keyOffset) {
        Node currentNode = this;
        BytesWrapper prefixBasedValue = null;
        while (keyOffset < key.getLength()) {
            if (currentNode.isPrefixEnd) {
                prefixBasedValue = currentNode.value;
            }
            currentNode = currentNode.children.get(key.getByteAt(keyOffset));
            if (currentNode == null) {
                return prefixBasedValue;
            }
            keyOffset++;
        }
        if (currentNode.value != null) {
            return currentNode.value;
        }
        return prefixBasedValue;
    }

    /**
     * Checks if this node represents a valid end of a key by verifying
     * if it contains a value.
     *
     * @return true if this node contains a value (indicating it's a valid
     *         end of a key), false otherwise
     */
    public boolean isValidEndOfKey() {
        return this.value != null;
    }

    /**
     * Checks if this node is a leaf node (has no children).
     *
     * @return true if this node has no children, false otherwise
     */
    public boolean isLeaf() {
        return this.children.isEmpty();
    }

    BytesWrapper getValue() {
        return value;
    }

    boolean isPrefixEnd() {
        return isPrefixEnd;
    }
}
