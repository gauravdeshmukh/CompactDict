package org.compactdict.util;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import org.apache.commons.codec.digest.MurmurHash3;

public class BytesWrapper implements Comparable<BytesWrapper>, Serializable {
    /** An empty byte array for convenience */
    public static final byte[] EMPTY_BYTES = new byte[0];
    public static final BytesWrapper EMPTY = new BytesWrapper(EMPTY_BYTES);

    /** The contents of the BytesRef. Should never be {@code null}. */
    private byte[] bytes;

    public BytesWrapper() {
        this(EMPTY_BYTES);
    }

    public BytesWrapper(String s) {
        this(s.getBytes(StandardCharsets.UTF_8));
    }

    public BytesWrapper(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * Expert: compares the bytes against another BytesRef, returning true if the bytes are equal.
     *
     * @param other Another BytesRef, should not be null.
     */
    public boolean bytesEquals(BytesWrapper other) {
        return Arrays.equals(this.bytes, 0, bytes.length, other.bytes, 0, other.getLength());
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public byte getByteAt(int offset) {
        assert offset >= 0 && offset < this.bytes.length;
        return this.bytes[offset];
    }

    public int getLength() {
        return bytes.length;
    }

    /**
     * Returns a shallow clone of this instance (the underlying bytes are <b>not</b> copied and will
     * be shared by both the returned object and this object.
     *
     * @see #deepCopyOf
     */
    @Override
    public BytesWrapper clone() {
        return new BytesWrapper(bytes);
    }

    /**
     * Calculates the hash code as required by TermsHash during indexing.
     *
     * <p>This is currently implemented as MurmurHash3 (32 bit), using the seed from {@link
     * StringHelper#GOOD_FAST_HASH_SEED}, but is subject to change from release to release.
     */
    @Override
    public int hashCode() {
        return MurmurHash3.hash32x86(bytes);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof BytesWrapper) {
            return this.bytesEquals((BytesWrapper) other);
        }
        return false;
    }

    /** Returns hex encoded bytes, e.g. "[6c 75 63 65 6e 65]" */
    @Override
    public String toString() {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /** Unsigned byte order comparison */
    @Override
    public int compareTo(BytesWrapper other) {
        return Arrays.compareUnsigned(
                this.bytes, 0, bytes.length, other.bytes, 0, other.getLength());
    }

    /**
     * Creates a new BytesRef that points to a copy of the bytes from <code>other</code>
     *
     * <p>The returned BytesRef will have a length of other.length and an offset of zero.
     */
    public BytesWrapper deepCopy() {
        final byte[] copy = new byte[bytes.length];
        System.arraycopy(bytes, 0, copy, 0, bytes.length);
        return new BytesWrapper(copy);
    }

    /**
     * Check the common prefix with another {@link BytesWrapper} object and returns it
     * @return {@link BytesWrapper} which is common prefix for this and input {@link BytesWrapper} object
     */
    public BytesWrapper commonPrefix(BytesWrapper other) {
        int i = 0;
        while (i < this.bytes.length && i < other.bytes.length && this.bytes[i] == other.bytes[i]) {
            i++;
        }
        if (i == 0) {
            return EMPTY;
        }
        return new BytesWrapper(Arrays.copyOfRange(this.bytes, 0, i));
    }

    /**
     * Returns a new {@link BytesWrapper} object which is suffix of this object
     * @param offset offset from which suffix should be returned
     * @return {@link BytesWrapper} which is suffix of this object
     */
    public BytesWrapper suffix(int offset) {
        if (offset == 0) {
            return this;
        }
        if (offset >= this.bytes.length) {
            return EMPTY;
        }
        return new BytesWrapper(Arrays.copyOfRange(this.bytes, offset, this.bytes.length));
    }

    /**
     * Returns a new {@link BytesWrapper} object by appending input bytes to this object
     * @param other {@link BytesWrapper} object to be appended to this object
     * @return {@link BytesWrapper} which is combination of this and input {@link BytesWrapper} object
     */
    public BytesWrapper append(BytesWrapper other) {
        if (other.bytes.length == 0) {
            return this;
        }
        if (this.bytes.length == 0) {
            return other;
        }
        byte[] newBytes = new byte[this.bytes.length + other.bytes.length];
        System.arraycopy(this.bytes, 0, newBytes, 0, this.bytes.length);
        System.arraycopy(other.bytes, 0, newBytes, this.bytes.length, other.bytes.length);
        return new BytesWrapper(newBytes);
    }

    /**
     * Returns a new {@link BytesWrapper} object by appending this object's to other object bytes
     * @param other {@link BytesWrapper} object to be appended to this object
     * @return {@link BytesWrapper} which is combination input {@link BytesWrapper} object and this object
     */
    public BytesWrapper addPrefix(BytesWrapper other) {
        if (other.bytes.length == 0) {
            return this;
        }
        if (this.bytes.length == 0) {
            return other;
        }
        byte[] newBytes = new byte[this.bytes.length + other.bytes.length];
        System.arraycopy(other.bytes, 0, newBytes, 0, other.bytes.length);
        System.arraycopy(this.bytes, 0, newBytes, other.bytes.length, this.bytes.length);
        return new BytesWrapper(newBytes);
    }

    public long hash() {
        return Arrays.hashCode(this.bytes);
    }
}
