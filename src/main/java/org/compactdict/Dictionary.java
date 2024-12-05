package org.compactdict;

import java.util.Collection;
import java.util.Map;

/**
 * Represents a dictionary that stores key-value pairs using BytesRef objects.
 * This interface provides basic operations for storing, retrieving, and listing
 * dictionary entries.
 */
public interface Dictionary {
    /**
     * Stores a key-value pair in the dictionary.
     * If the key already exists, its value will be updated with the new value.
     *
     * @param key   The BytesRef object representing the key to be stored
     * @param value The BytesRef object representing the value to be associated with the key
     */
    void put(BytesRef key, BytesRef value);

    /**
     * Stores a key-value pair in the dictionary.
     * If the key already exists, its value will be updated with the new value.
     *
     * @param key   The BytesRef object representing the key to be stored
     * @param value The BytesRef object representing the value to be associated with the key
     * @param isPrefixKey A boolean indicating whether the key is a prefix key or not.
     *                    By default, it falls back to assumption that key is not prefix key.
     */
    default void put(BytesRef key, BytesRef value, boolean isPrefixKey) {
        put(key, value);
    }

    /**
     * Retrieves the value associated with the specified key.
     *
     * @param key The BytesRef object representing the key to look up
     * @return The BytesRef value associated with the key, or null if the key is not found
     */
    BytesRef get(BytesRef key);

    /**
     * Returns a collection of all key-value pairs stored in the dictionary.
     *
     * @return A Collection of Map.Entry objects containing all the key-value pairs
     *         where both keys and values are BytesRef objects
     */
    Collection<Map.Entry<BytesRef, BytesRef>> getEntries();
}
