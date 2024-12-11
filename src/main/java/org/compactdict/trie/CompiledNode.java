package org.compactdict.trie;

import org.compactdict.util.BytesWrapper;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * Represents a node in a compiled trie data structure that supports optimization
 * through compilation. This class implements both Serializable for persistence
 * and Comparable for ordering nodes based on their input bytes.
 *
 * <p>The node structure supports two states:
 * <ul>
 *     <li>Uncompiled: uses a TreeMap for dynamic child node storage</li>
 *     <li>Compiled: uses a sorted array for memory-efficient child node storage</li>
 * </ul>
 * </p>
 */
class CompiledNode implements Serializable, Comparable<CompiledNode> {
    byte inputByte;
    boolean isPrefixEnd;
    int bufferOffset;
    int value = -1;
    transient TreeMap<Byte, CompiledNode> childrenMap;
    CompiledNode[] children;

    /**
     * Constructs an empty node with no input byte or value.
     */
    CompiledNode() {
        this.childrenMap = new TreeMap<>();
    }

    /**
     * Constructs a node with the specified input byte.
     *
     * @param inputByte the byte value associated with this node
     */
    CompiledNode(byte inputByte) {
        this.inputByte = inputByte;
        this.childrenMap = new TreeMap<>();
    }

    /**
     * Recursively stores a key-value pair in the trie.
     *
     * @param key the BytesRef key to be stored
     * @param value the BytesRef value to be associated with the key
     * @param keyOffset the current position in the key being processed
     */
    void put(BytesWrapper key, int value, int keyOffset, boolean isPrefixKey) {
        CompiledNode currentNode = this;
        while (keyOffset < key.getLength()) {
            byte keyByte = key.getByteAt(keyOffset);
            currentNode = currentNode.childrenMap.computeIfAbsent(keyByte, k -> new CompiledNode(keyByte));
            keyOffset++;
        }
        currentNode.value = value;
        currentNode.isPrefixEnd = isPrefixKey;
    }

    /**
     * Returns the input byte associated with this node.
     * In Trie terms, this represents the arc from parent which leads to this node.
     *
     * @return the input byte value
     */
    byte getInputByte() {
        return inputByte;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(CompiledNode o) {
        return inputByte - o.getInputByte();
    }
}
