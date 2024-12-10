package org.compactdict.util;

import java.util.Arrays;

public class ByteBuffer {
    public static final ByteBuffer EMPTY = new ByteBuffer(new byte[0]);

    private byte[] buffer;
    private int position;
    private int limit;

    public ByteBuffer(byte[] buffer) {
        this(buffer, 0, buffer.length);
    }

    public ByteBuffer(byte[] buffer, int position, int limit) {
        assert limit >= 0;
        this.buffer = Arrays.copyOf(buffer, buffer.length);
        this.position = position;
        this.limit = limit;
    }

    public ByteBuffer deepClone() {
        byte[] bufferCopy = new byte[buffer.length];
        System.arraycopy(buffer, 0, bufferCopy, 0, buffer.length);
        return new ByteBuffer(bufferCopy, position, limit);
    }

    public void append(ByteBuffer byteBufferToAppend) {
        byte[] bufferCopy = new byte[buffer.length + byteBufferToAppend.getLimit()];
        System.arraycopy(buffer, 0, bufferCopy, 0, buffer.length);
        System.arraycopy(byteBufferToAppend.getBuffer(), 0, bufferCopy, limit, byteBufferToAppend.getLimit());
        buffer = bufferCopy;
        limit +=  byteBufferToAppend.getLimit();
    }

    public byte readByte() {
        assert limit - position >= 1;
        return buffer[position++];
    }

    public byte[] readBytes(int length) {
        assert length >= 0 && length <= limit - position;
        byte[] bytes = new byte[length];
        System.arraycopy(buffer, position, bytes, 0, length);
        position += length;
        return bytes;
    }

    public void writeByte(byte b) {
        ensureSize(position + 1);
        buffer[position++] = b;
    }

    public void writeBytes(byte[] bytes) {
        ensureSize(position + bytes.length);
        System.arraycopy(bytes, 0, buffer, position, bytes.length);
        position += bytes.length;
    }

    private void ensureSize(int size) {
        if (size > limit) {
            byte[] bufferCopy = new byte[Math.max(size, limit * 2)];
            System.arraycopy(buffer, 0, bufferCopy, 0, buffer.length);
            buffer = bufferCopy;
            limit = bufferCopy.length;
        }
    }

    public void trim() {
        byte[] bufferCopy = new byte[position];
        System.arraycopy(buffer, 0, bufferCopy, 0, position);
        buffer = bufferCopy;
        limit = position;
    }

    public void clear() {
        position = 0;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int pos) {
        assert pos >= 0 && pos <= limit;
        position = pos;
    }

    public int getLimit() {
        return limit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ByteBuffer that = (ByteBuffer) o;
        if (limit != that.limit) {
            return false;
        }
        if (position != that.position) {
            return false;
        }
        return java.util.Arrays.equals(buffer, that.getBuffer());
    }
}
