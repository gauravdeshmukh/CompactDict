package org.compactdict.trie;

import org.compactdict.CompiledDictionary;
import org.compactdict.util.ByteBuffer;
import org.compactdict.util.BytesWrapper;
import org.compactdict.util.VIntUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;


/**
 * A memory-optimized trie-based implementation of the Dictionary interface
 * that stores key-value pairs using Finite State Transducer (FST).
 *
 * FST is then serialized into byte array using as minimum bytes as possible.
 *
 * Note: This is not thread-safe.
 */
public class FST implements CompiledDictionary, Serializable {
    private final ValuePrefixedCompiledNode root;
    private ByteBuffer serializedDict;
    private int rootPositionInBuffer = -1;
    private Map<Long, Integer> suffixCache = new HashMap<>();

    /**
     * Constructs an empty FST with a new root node and
     * an empty value table. The dictionary is initialized with no key-value
     * pairs.
     */
    public FST() {
        root = new ValuePrefixedCompiledNode();
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
    public void put(BytesWrapper key, BytesWrapper value, boolean isPrefixKey) {
        assert key != null && value != null;
        if (serializedDict != null) {
            throw new IllegalStateException("Cannot put values after dictionary is compiled");
        }

        root.put(key, value, 0, isPrefixKey);
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
     * Since values are distributed over edges in the path,
     * it combines values while traversing the path.
     *
     * @param key the BytesRef key whose associated value is to be returned
     * @return the BytesRef value associated with the specified key,
     *         or null if no mapping exists for the key
     */
    @Override
    public BytesWrapper get(BytesWrapper key) {
        assert key != null;
        if (serializedDict == null) {
            throw new IllegalStateException("Dictionary needs to be compiled before querying");
        }
        if (rootPositionInBuffer == -1) {
            // Parse serializedDict buffer initialize required variables before reading Trie
            serializedDict.setPosition(0);
            // First four bytes always represent position of root node
            rootPositionInBuffer = getIntegerFromBytes(serializedDict.readBytes(4));
        }

        serializedDict.setPosition(rootPositionInBuffer);
        int keyOffset = 0;
        BytesWrapper value = BytesWrapper.EMPTY;
        BytesWrapper prefixValue = null;
        while (keyOffset < key.getLength()) {
            byte valueFlag = serializedDict.readByte();

            if ((valueFlag & 0x04) != 0) {
                int valueBytesLength = VIntUtils.readVInt(serializedDict);
                BytesWrapper readValue = new BytesWrapper(serializedDict.readBytes(valueBytesLength));
                value = value.append(readValue);
            }
            if ((valueFlag & 0x02) != 0) {
                prefixValue = value;
            }
            int childCount = VIntUtils.readVInt(serializedDict);
            int edgeSize = VIntUtils.readVInt(serializedDict);
            int nextNodeOffset = getChildOffset(key.getByteAt(keyOffset), serializedDict.getPosition(), childCount, edgeSize, serializedDict);
            if (nextNodeOffset == -1) {
                break;
            }
            keyOffset++;
            serializedDict.setPosition(nextNodeOffset);
        }

        if (keyOffset == key.getLength()) {
            // Found exact match
            byte valueFlag = serializedDict.readByte();
            if ((valueFlag & 0x01) != 0) {
                prefixValue = value;
                if ((valueFlag & 0x04) != 0) {
                    int valueBytesLength = VIntUtils.readVInt(serializedDict);
                    BytesWrapper readValue = new BytesWrapper(serializedDict.readBytes(valueBytesLength));
                    value = value.append(readValue);
                    prefixValue = value;
                }
            }
        }

        if (prefixValue == BytesWrapper.EMPTY) {
            return null;
        }
        return prefixValue;
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

    @Override
    public void compile() {
        if (serializedDict != null) {
            return;
        }

        suffixCache = new HashMap<>();
        serializedDict = new ByteBuffer(new byte[100]);
        // Leave first four bytes empty to write root position later
        serializedDict.writeBytes(new byte[4]);

        // Start writing nodes to buffer level by level
        // Write levels in reverse orders so that offset for child node can be calculated,
        // to make it available to serialize parent node
        Stack<Queue<ValuePrefixedCompiledNode>> perLevelNodes = getLevelOrderedNodes();

        // Creating buffer for small repeated operations, to avoid creating new buffer objects again and again
        ByteBuffer scratchBuffer = new ByteBuffer(new byte[5]);
        while (!perLevelNodes.isEmpty()) {
            Queue<ValuePrefixedCompiledNode> currentLevelNodes = perLevelNodes.pop();
            while (!currentLevelNodes.isEmpty()) {
                serializeNode(currentLevelNodes.poll(), serializedDict, scratchBuffer);
            }
        }

        serializedDict.trim();
        serializedDict.setPosition(0);
        serializedDict.writeBytes(getIntegerBytes(root.bufferOffset));
        suffixCache = null;
    }

    public void save(String fileName) throws IOException {
        FileOutputStream fileOutputStream
                = new FileOutputStream(fileName);
        System.out.println("Writing buffer size " + serializedDict.getBuffer().length);
        fileOutputStream.write(serializedDict.getBuffer());
        fileOutputStream.close();
    }

    public void load(String fileName) throws IOException {
        FileInputStream fileInputStream
                = new FileInputStream(fileName);
        serializedDict = new ByteBuffer(fileInputStream.readAllBytes());
        System.out.println("Loaded dictionary from file " + serializedDict.getBuffer().length);
        root.childrenMap = null;
    }

    private void serializeNode(ValuePrefixedCompiledNode node, ByteBuffer serializedDict, ByteBuffer scratchBuffer) {
        long nodeHash = hash(node);
        if (suffixCache.containsKey(nodeHash)) {
            node.bufferOffset = suffixCache.get(nodeHash);
            node.childrenMap = null;
            return;
        }
        node.bufferOffset = serializedDict.getPosition();

        byte valueFlag = 0x00;
        if (node.isKeyEnd) {
            valueFlag |= (byte) 0x01; // Set flag for key end
        }
        if (node.isPrefixEnd) {
            valueFlag |= (byte) 0x02; // Set flag for prefix end
        }
        if (node.value != null && node.value != BytesWrapper.EMPTY) {
            valueFlag |= (byte) 0x04; // Set flag for value presence
            serializedDict.writeByte(valueFlag);
            VIntUtils.writeVInt(serializedDict, node.value.getLength()); // Write value length
            serializedDict.writeBytes(node.value.getBytes()); // Write value bytes
        } else {
            serializedDict.writeByte(valueFlag);
        }

        // Write number of children
        VIntUtils.writeVInt(serializedDict, node.childrenMap.size());

        // Find minimum number of bytes required to represent edge
        // For binary search, we want to ensure that each edge has same number of bytes
        int minEdgeMemSize = 1;
        for (ValuePrefixedCompiledNode childNode : node.childrenMap.values()) {
            scratchBuffer.clear();
            VIntUtils.writeVInt(scratchBuffer, childNode.bufferOffset);
            minEdgeMemSize = Math.max(minEdgeMemSize, scratchBuffer.getPosition());
        }

        // Write size of each child edge. Adding 1 for inputByte.
        VIntUtils.writeVInt(serializedDict, minEdgeMemSize+1);
        for (ValuePrefixedCompiledNode childNode : node.childrenMap.values()) {
            // Write input byte for the edge
            serializedDict.writeByte(childNode.inputByte);
            // Write offset of child node in the buffer
            int bytesUsedForEdge = VIntUtils.writeVInt(serializedDict, childNode.bufferOffset);
            // Pad bytes to ensure minEdgeMemSize
            while (bytesUsedForEdge < minEdgeMemSize) {
                serializedDict.writeByte((byte)0x00);
                bytesUsedForEdge++;
            }
        }

        node.childrenMap = null;
        suffixCache.put(nodeHash, node.bufferOffset);
    }

    private Stack<Queue<ValuePrefixedCompiledNode>> getLevelOrderedNodes() {
        Stack<Queue<ValuePrefixedCompiledNode>> perLevelQueues = new Stack<>();
        Queue<ValuePrefixedCompiledNode> currentLevelQueue = new LinkedList<>();
        Queue<ValuePrefixedCompiledNode> nextLevelQueue = new LinkedList<>();
        nextLevelQueue.add(root);

        perLevelQueues.push(nextLevelQueue);
        nextLevelQueue = new LinkedList<>();

        currentLevelQueue.add(root);
        ValuePrefixedCompiledNode nodeToCompile;
        while (!currentLevelQueue.isEmpty()) {
            nodeToCompile = currentLevelQueue.poll();
            nextLevelQueue.addAll(nodeToCompile.childrenMap.values());

            if (currentLevelQueue.isEmpty()) {
                currentLevelQueue = nextLevelQueue;
                perLevelQueues.push(new LinkedList<>(nextLevelQueue));
                nextLevelQueue = new LinkedList<>();
            }
        }

        return perLevelQueues;
    }

    int getChildOffset(byte keyByte, int startPosition, int childCount, int edgeSize, ByteBuffer buffer) {
        int start = 0, end = childCount - 1;
        while (start <= end) {
            int mid = start + ((end - start) / 2);
            buffer.setPosition(startPosition+mid*edgeSize);
            byte check = buffer.readByte();
            if (check > keyByte) {
                end = mid - 1;
            } else if (check < keyByte) {
                start = mid + 1;
            } else {
                return VIntUtils.readVInt(buffer);
            }
        }
        return -1;
    }

    private long hash(ValuePrefixedCompiledNode node) {
        final int PRIME = 31;
        long h = 0;
        if (node.childrenMap != null) {
            for (ValuePrefixedCompiledNode childNode : node.childrenMap.values()) {
                h = PRIME * h + childNode.inputByte;
                long n = childNode.bufferOffset;
                h = PRIME * h + (int) (n ^ (n >> 32));
            }
        }

        h = PRIME * h + (node.value == null ? 0 : node.value.hash());
        h = PRIME * h + node.depth;
        h = PRIME * h + (node.isPrefixEnd ? 1 : 0);
        h = PRIME * h + (node.isKeyEnd ? 1 : 0);
        return h;
    }

    private byte[] getIntegerBytes(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    private int getIntegerFromBytes(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8) |
                (bytes[3] & 0xFF);
    }
}
