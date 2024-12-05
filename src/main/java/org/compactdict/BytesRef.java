package org.compactdict;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import org.apache.commons.codec.digest.MurmurHash3;

public class BytesRef implements Comparable<BytesRef>, Serializable {
    /** An empty byte array for convenience */
    public static final byte[] EMPTY_BYTES = new byte[0];

    /** The contents of the BytesRef. Should never be {@code null}. */
    private byte[] bytes;

    public BytesRef() {
        this(EMPTY_BYTES);
    }

    public BytesRef(String s) {
        this(s.getBytes(StandardCharsets.UTF_8));
    }

    public BytesRef(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * Expert: compares the bytes against another BytesRef, returning true if the bytes are equal.
     *
     * @param other Another BytesRef, should not be null.
     */
    public boolean bytesEquals(BytesRef other) {
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
    public BytesRef clone() {
        return new BytesRef(bytes);
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
        if (other instanceof BytesRef) {
            return this.bytesEquals((BytesRef) other);
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
    public int compareTo(BytesRef other) {
        return Arrays.compareUnsigned(
                this.bytes, 0, bytes.length, other.bytes, 0, other.getLength());
    }

    /**
     * Creates a new BytesRef that points to a copy of the bytes from <code>other</code>
     *
     * <p>The returned BytesRef will have a length of other.length and an offset of zero.
     */
    public BytesRef deepCopy() {
        final byte[] copy = new byte[bytes.length];
        System.arraycopy(bytes, 0, copy, 0, bytes.length);
        return new BytesRef(copy);
    }
}
