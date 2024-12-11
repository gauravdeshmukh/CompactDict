package org.compactdict.trie;

import org.compactdict.util.BytesWrapper;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * Represents a node in a compiled trie data structure that supports optimization
 * through compilation. This class implements both Serializable for persistence
 * and Comparable for ordering nodes based on their input bytes.
 *
 * It stores values on edges such that if we traverse the path a key,
 * appending all values over the path returns the value for the key.
 *
 * <p>The node structure supports two states:
 * <ul>
 *     <li>Uncompiled: uses a TreeMap for dynamic child node storage</li>
 *     <li>Compiled: uses a sorted array for memory-efficient child node storage</li>
 * </ul>
 * </p>
 */
class ValuePrefixedCompiledNode implements Serializable, Comparable<ValuePrefixedCompiledNode> {
    byte inputByte;
    boolean isPrefixEnd;
    boolean isKeyEnd;
    BytesWrapper value;
    transient TreeMap<Byte, ValuePrefixedCompiledNode> childrenMap;
    private ValuePrefixedCompiledNode[] children;
    int bufferOffset;
    int depth;

    /**
     * Constructs an empty node with no input byte or value.
     */
    ValuePrefixedCompiledNode() {
        this.childrenMap = new TreeMap<>();
        this.isKeyEnd = false;
        this.isPrefixEnd = false;
        this.depth = 0;
    }

    /**
     * Constructs a node with the specified input byte.
     *
     * @param inputByte the byte value associated with this node
     * @param depth depth of the node in Trie
     */
    ValuePrefixedCompiledNode(byte inputByte, int depth) {
        this.inputByte = inputByte;
        this.childrenMap = new TreeMap<>();
        this.isKeyEnd = false;
        this.isPrefixEnd = false;
        this.depth = depth;
    }

    /**
     * Recursively stores a key-value pair in the trie.
     * If value is split and distributed over the edges in the path,
     * such that common prefixes are stored on the common edges avoiding duplicate value storage.
     *
     * @param key the BytesRef key to be stored
     * @param value the BytesRef value to be associated with the key
     * @param keyOffset the current position in the key being processed
     */
    void put(BytesWrapper key, BytesWrapper value, int keyOffset, boolean isPrefixKey) {
        assert value != null && key != null;
        ValuePrefixedCompiledNode currentNode = this;
        while (keyOffset < key.getLength()) {
            if (currentNode.value == null) {
                currentNode.value = value;
                value = BytesWrapper.EMPTY;
            } else {
                BytesWrapper commonPrefix = value.commonPrefix(currentNode.value);
                BytesWrapper existingSuffixToPushDown = currentNode.value.suffix(commonPrefix.getLength());
                for (ValuePrefixedCompiledNode child : currentNode.childrenMap.values()) {
                    child.value = child.value.addPrefix(existingSuffixToPushDown);
                }
                currentNode.value = commonPrefix;
                value = value.suffix(commonPrefix.getLength());
            }
            byte keyByte = key.getByteAt(keyOffset);
            int nextNodeDepth = keyOffset+1;
            currentNode = currentNode.childrenMap.computeIfAbsent(keyByte, k -> new ValuePrefixedCompiledNode(keyByte, nextNodeDepth));
            keyOffset++;
        }
        currentNode.value = value;
        currentNode.isKeyEnd = true;
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
    public int compareTo(ValuePrefixedCompiledNode o) {
        return inputByte - o.getInputByte();
    }
}
