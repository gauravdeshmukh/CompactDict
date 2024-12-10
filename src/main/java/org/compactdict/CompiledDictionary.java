package org.compactdict;

/**
 * Represents a dictionary that can be compiled for optimized memory consumption.
 * This interface extends the base Dictionary interface and provides additional
 * functionality to compile the dictionary contents into a more efficient format.
 *
 * <p>Implementations of this interface should maintain the standard dictionary
 * operations while providing the ability to optimize the internal structure
 * through compilation.</p>
 */
public interface CompiledDictionary extends Dictionary {
    /**
     * Compiles the dictionary to optimize its internal structure and performance.
     * This method should be called after all entries have been added to the dictionary
     * and before performing lookup operations for best performance.
     *
     * <p>After compilation, the dictionary may reorganize its internal data structures
     * to provide more efficient access to the stored key-value pairs. The behavior of
     * put operations after compilation may vary depending on the implementation.</p>
     */
    void compile();
}
