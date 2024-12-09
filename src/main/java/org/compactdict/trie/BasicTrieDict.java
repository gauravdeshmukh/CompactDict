package org.compactdict.trie;

import org.compactdict.util.BytesWrapper;
import org.compactdict.Dictionary;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * A basic trie-based implementation of the Dictionary interface that stores
 * key-value pairs using BytesRef objects. This implementation uses a Trie
 * structure where each arc represents a byte in the key path.
 *
 * <p>This class provides a memory-efficient way to store and retrieve
 * key-value pairs, particularly suitable for keys with common prefixes.
 * The implementation is serializable for persistent storage.</p>
 *
 * Note: This is not thread-safe.
 */
public class BasicTrieDict implements Dictionary, Serializable {
    private final Node root;

    /**
     * Constructs an empty BasicTrieDict with a new root node.
     * The trie is initialized with no key-value pairs.
     */
    public BasicTrieDict() {
        root = new Node();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Stores the key-value pair in the trie by traversing or creating
     * nodes for each byte in the key. If the key already exists, its value
     * will be updated with the new value.</p>
     *
     * @param key   the BytesRef key to store
     * @param value the BytesRef value to be associated with the key
     * @param isPrefixKey A boolean indicating whether the key is a prefix key or not.
     */
    @Override
    public void put(BytesWrapper key, BytesWrapper value, boolean isPrefixKey) {
        assert key != null && value != null;
        root.put(key, value, 0, isPrefixKey);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Stores the key-value pair in the trie by traversing or creating
     * nodes for each byte in the key. If the key already exists, its value
     * will be updated with the new value.</p>
     *
     * @param key   the BytesRef key to store
     * @param value the BytesRef value to be associated with the key
     */
    @Override
    public void put(BytesWrapper key, BytesWrapper value) {
        assert key != null && value != null;
        root.put(key, value, 0, false);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Retrieves the value by traversing the trie following the bytes
     * in the key.</p>
     *
     * If multiple prefixes match keys,
     * value associated with the longest matching prefix will be returned.
     *
     * @param key the BytesRef key whose associated value is to be returned
     * @return the BytesRef value associated with the specified key,
     *         or null if no mapping exists for the key
     */
    @Override
    public BytesWrapper get(BytesWrapper key) {
        assert key != null;
        return root.get(key, 0);
    }

    /**
     * {@inheritDoc}
     * TODO: Return all key-value pairs in the trie.
     * <p>This implementation currently returns null as it does not support
     * listing all entries.</p>
     *
     * @return null as this implementation does not support entry iteration
     */
    @Override
    public Collection<Map.Entry<BytesWrapper, BytesWrapper>> getEntries() {
        return null;
    }
}
