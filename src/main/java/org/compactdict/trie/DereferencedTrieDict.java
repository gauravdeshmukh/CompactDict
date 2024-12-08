package org.compactdict.trie;

import org.compactdict.BytesRef;
import org.compactdict.Dictionary;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * A memory-optimized trie-based implementation of the Dictionary interface
 * that stores key-value pairs using BytesRef objects. This implementation
 * combines a trie structure with value dereferencing to reduce memory usage.
 *
 * <p>The class uses two main data structures:
 * <ul>
 *     <li>A trie structure for efficient key storage and lookup</li>
 *     <li>A value table that ensures duplicate values are stored only once</li>
 * </ul>
 * </p>
 *
 * <p>This implementation is particularly efficient when multiple keys map
 * to the same value, as the value is stored only once in memory. The class
 * supports serialization for persistent storage.</p>
 *
 * Note: This is not thread-safe.
 */
public class DereferencedTrieDict implements Dictionary, Serializable {
    private final Node root;
    private final Map<BytesRef, BytesRef> valueTable;

    /**
     * Constructs an empty DereferencedTrieDict with a new root node and
     * an empty value table. The dictionary is initialized with no key-value
     * pairs.
     */
    public DereferencedTrieDict() {
        root = new Node();
        valueTable = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation first checks if the value already exists in the
     * value table. If it does, the existing reference is used; if not, the
     * new value is added to the value table. This ensures that duplicate
     * values are stored only once in memory.</p>
     *
     * @param key   the BytesRef key to store
     * @param value the BytesRef value to be associated with the key
     */
    @Override
    public void put(BytesRef key, BytesRef value) {
        put(key, value, false);
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation first checks if the value already exists in the
     * value table. If it does, the existing reference is used; if not, the
     * new value is added to the value table. This ensures that duplicate
     * values are stored only once in memory.</p>
     *
     * @param key   the BytesRef key to store
     * @param value the BytesRef value to be associated with the key
     * @param isPrefixKey A boolean indicating whether the key is a prefix key or not.
     */
    @Override
    public void put(BytesRef key, BytesRef value, boolean isPrefixKey) {
        assert key != null && value != null;
        BytesRef dereferencedValue = valueTable.computeIfAbsent(value, v -> v);
        root.put(key, dereferencedValue, 0, isPrefixKey);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Retrieves the value by traversing the trie following the bytes
     * in the key. The returned value is a reference to the value stored
     * in the value table.</p>
     *
     * If multiple prefixes match keys,
     * value associated with the longest matching prefix will be returned.
     *
     * @param key the BytesRef key whose associated value is to be returned
     * @return the BytesRef value associated with the specified key,
     *         or null if no mapping exists for the key
     */
    @Override
    public BytesRef get(BytesRef key) {
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
    public Collection<Map.Entry<BytesRef, BytesRef>> getEntries() {
        return null;
    }
}
