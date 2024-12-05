package org.compactdict.map;

import org.compactdict.BytesRef;
import org.compactdict.Dictionary;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A HashMap-based implementation of the Dictionary interface that stores
 * key-value pairs using BytesRef objects. This class provides a simple and
 * efficient dictionary implementation using Java's HashMap as the underlying
 * data structure.
 *
 * <p>This class is serializable, allowing dictionary instances to be
 * saved to and restored from persistent storage.</p>
 *
 * Note: This is not thread-safe.
 */
public class HashMapDict implements Dictionary, Serializable {
    private final Map<BytesRef, BytesRef> map;

    /**
     * Constructs an empty HashMapDict with default initial capacity.
     * The default capacity of the underlying HashMap will be used.
     */
    public HashMapDict() {
        map = new HashMap<>();
    }

    /**
     * Constructs an empty HashMapDict with the specified initial capacity.
     *
     * @param size the initial capacity of the dictionary. The underlying
     *             HashMap will be initialized with this capacity for better
     *             performance if the expected number of entries is known
     */
    public HashMapDict(int size) {
        map = new HashMap<>(size);
    }

    /**
     * {@inheritDoc}
     *
     * <p>If the specified key already exists in the dictionary,
     * the old value will be replaced with the new value.</p>
     *
     * @param key   the BytesRef key to store
     * @param value the BytesRef value to be associated with the key
     */
    @Override
    public void put(BytesRef key, BytesRef value) {
        assert key != null;
        map.put(key, value);
    }

    /**
     * {@inheritDoc}
     *
     * @param key the BytesRef key whose associated value is to be returned
     * @return the BytesRef value associated with the specified key,
     *         or null if no mapping exists for the key
     */
    @Override
    public BytesRef get(BytesRef key) {
        assert key != null;
        return map.get(key);
    }

    /**
     * {@inheritDoc}
     *
     * @return a Collection view of all key-value pairs stored in this dictionary.
     *         The collection is backed by the dictionary, so changes to the
     *         dictionary are reflected in the collection
     */
    @Override
    public Collection<Map.Entry<BytesRef, BytesRef>> getEntries() {
        return map.entrySet();
    }
}
