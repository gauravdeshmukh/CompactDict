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
public class CompiledTrieDict implements CompiledDictionary, Serializable {
    private final CompiledNode root;
    private Map<BytesWrapper, Integer> valueOffsetTable;
    private ByteBuffer serializedDict;
    private ByteBuffer serializedValueTable;
    private int rootPositionInBuffer = -1;
    private final int VALUE_TABLE_START_OFFSET = 4;

    /**
     * Constructs an empty CompiledTrieDict with a new root node and
     * an empty value table. The dictionary is initialized with no key-value
     * pairs.
     */
    public CompiledTrieDict() {
        root = new CompiledNode();
        valueOffsetTable = new HashMap<>();
        serializedValueTable = new ByteBuffer(new byte[10]);
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
//        System.out.println("Input key: " + Arrays.toString(key.getBytes()));

        if (!valueOffsetTable.containsKey(value)) {
            valueOffsetTable.put(value, serializedValueTable.getPosition());
            VIntUtils.writeVInt(serializedValueTable, value.getLength());
            serializedValueTable.writeBytes(value.getBytes());
        }
        int valueOffset = valueOffsetTable.get(value);
        root.put(key, valueOffset, 0, isPrefixKey);
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
        int keyOffset = 0, valueOffset = -1;
        while (keyOffset < key.getLength()) {
            byte valueFlag = serializedDict.readByte();
            if ((valueFlag & 0x02) != 0) {
                valueOffset = VIntUtils.readVInt(serializedDict);
            }
            else if ((valueFlag & 0x01) != 0) {
                VIntUtils.readVInt(serializedDict);
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
                valueOffset = VIntUtils.readVInt(serializedDict);
            }
        }

        BytesWrapper value = null;
        if (valueOffset > -1) {
            serializedDict.setPosition(valueOffset + VALUE_TABLE_START_OFFSET);
            int valueLength = VIntUtils.readVInt(serializedDict);
            value = new BytesWrapper(serializedDict.readBytes(valueLength));
        }

        return value;
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

        serializedDict = new ByteBuffer(new byte[serializedValueTable.getLimit()*2]);
        // Leave first four bytes empty to write root position later
        serializedDict.writeBytes(new byte[4]);

        // Write value table size and then write actual values
        serializedDict.writeBytes(serializedValueTable.getBuffer());
        serializedValueTable = null;
        valueOffsetTable = null;

        // Start writing nodes to buffer level by level
        // Write levels in reverse orders so that offset for child node can be calculated,
        // to make it available to serialize parent node
        int currentNodeWritingOffset = serializedDict.getPosition();
        Stack<Queue<CompiledNode>> perLevelNodes = getLevelOrderedNodes();

        // Creating buffer for small repeated operations, to avoid creating new buffer objects again and again
        ByteBuffer scratchBuffer = new ByteBuffer(new byte[5]);
        while (!perLevelNodes.isEmpty()) {
            Queue<CompiledNode> currentLevelNodes = perLevelNodes.pop();
            while (!currentLevelNodes.isEmpty()) {
                serializeNode(currentLevelNodes.poll(), serializedDict, scratchBuffer);
            }
        }

        serializedDict.trim();
        serializedDict.setPosition(0);
        serializedDict.writeBytes(getIntegerBytes(root.bufferOffset));
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

    private void serializeNode(CompiledNode node, ByteBuffer serializedDict, ByteBuffer scratchBuffer) {
        node.bufferOffset = serializedDict.getPosition();

        byte valueFlag = 0x00;
        if (node.isPrefixEnd) {
            valueFlag |= (byte) 0x02; // Set flag for prefix end
        }
        if (node.value != -1) {
            valueFlag |= (byte) 0x01; // Set flag for value presence
            serializedDict.writeByte(valueFlag); // Write value flag
            VIntUtils.writeVInt(serializedDict, node.value); // Write value
        } else {
            serializedDict.writeByte(valueFlag);
        }

        // Write number of children
        VIntUtils.writeVInt(serializedDict, node.childrenMap.size());

        // Find minimum number of bytes required to represent edge
        // For binary search, we want to ensure that each edge has same number of bytes
        int minEdgeMemSize = 1;
        for (CompiledNode childNode : node.childrenMap.values()) {
            scratchBuffer.clear();
            VIntUtils.writeVInt(scratchBuffer, childNode.bufferOffset);
            minEdgeMemSize = Math.max(minEdgeMemSize, scratchBuffer.getPosition());
        }

        // Write size of each child edge. Adding 1 for inputByte.
        VIntUtils.writeVInt(serializedDict, minEdgeMemSize+1);
        for (CompiledNode childNode : node.childrenMap.values()) {
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
//        compiledCount++;
//        if (compiledCount%1000 == 0) {
//            System.out.println("Compiled " + compiledCount + " nodes");
//        }
    }

    private Stack<Queue<CompiledNode>> getLevelOrderedNodes() {
        Stack<Queue<CompiledNode>> perLevelQueues = new Stack<>();
        Queue<CompiledNode> currentLevelQueue = new LinkedList<>();
        Queue<CompiledNode> nextLevelQueue = new LinkedList<>();
        nextLevelQueue.add(root);

        perLevelQueues.push(nextLevelQueue);
        nextLevelQueue = new LinkedList<>();

        currentLevelQueue.add(root);
        CompiledNode nodeToCompile;
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
