package org.compactdict.map;

import org.compactdict.util.BytesWrapper;
import org.compactdict.Dictionary;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A memory-optimized implementation of the Dictionary interface that stores
 * key-value pairs using BytesRef objects. This implementation maintains a separate
 * value table to store unique values, ensuring that duplicate values are stored
 * only once in memory.
 *
 * <p>This class uses two HashMaps internally:
 * <ul>
 *     <li>A primary map for storing key-value mappings</li>
 *     <li>A value table for storing unique values and maintaining references</li>
 * </ul>
 * </p>
 *
 * <p>This class is serializable, allowing dictionary instances to be
 * saved to and restored from persistent storage.</p>
 *
 * Note: This is not thread-safe.
 */
public class DereferencedHashMapDict implements Dictionary, Serializable {
    private final Map<BytesWrapper, BytesWrapper> map;
    private final Map<BytesWrapper, BytesWrapper> valueTable;

    /**
     * Constructs an empty DereferencedHashMapDict with default initial capacity
     * for both the primary map and value table.
     */
    public DereferencedHashMapDict() {
        map = new HashMap<>();
        valueTable = new HashMap<>();
    }

    /**
     * Constructs an empty DereferencedHashMapDict with the specified initial
     * capacity for the primary map. The value table is initialized with
     * default capacity.
     *
     * @param size the initial capacity of the primary map. The underlying
     *             HashMap will be initialized with this capacity for better
     *             performance if the expected number of entries is known
     */
    public DereferencedHashMapDict(int size) {
        map = new HashMap<>(size);
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
    public void put(BytesWrapper key, BytesWrapper value) {
        assert key != null;
        BytesWrapper dereferencedValue = valueTable.computeIfAbsent(value, v -> v);
        map.put(key, dereferencedValue);
    }

    /**
     * {@inheritDoc}
     *
     * @param key the BytesRef key whose associated value is to be returned
     * @return the BytesRef value associated with the specified key,
     *         or null if no mapping exists for the key
     */
    @Override
    public BytesWrapper get(BytesWrapper key) {
        assert key != null;
        return map.get(key);
    }

    /**
     * {@inheritDoc}
     *
     * @return a Collection view of all key-value pairs stored in this dictionary.
     *         The collection is backed by the primary map, so changes to the
     *         dictionary are reflected in the collection. Note that values
     *         in the returned entries are dereferenced values from the value table
     */
    @Override
    public Collection<Map.Entry<BytesWrapper, BytesWrapper>> getEntries() {
        return map.entrySet();
    }
}
