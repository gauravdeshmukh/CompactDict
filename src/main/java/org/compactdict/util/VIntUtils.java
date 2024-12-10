package org.compactdict.util;

/**
 * Utils for reading and writing integers with variable number of bytes.
 * This is useful for optimizing memory consumption for +ve integers.
 * For -ve numbers, it will always take 5 bytes.
 */
public class VIntUtils {
    private static final int CONTINUATION_BIT_MASK = 0x80;
    private static final int LSB_SEVEN_BITS_MASK = 0x7F;
    private static final int MSB_TWENTY_FIVE_BITS_MASK = ~LSB_SEVEN_BITS_MASK;

    /**
     * Instead of always using 4 bytes to write the positive integer, it tries to use as only required number of bytes.
     * TL;DR: Integers are stored in little endian format.
     * It starts by writing least significant 7 bits of the value.
     * If value has more bits, 8th bit is set to 1 indicating that rest of the bits will be present in the next byte.
     * Bits in value are then left shifted by 7 bits and the process is repeated until number is 0.
     *
     * For larger numbers, it will take more than 4 bytes. But for smaller numbers it will optimize the byte count.
     * For -ve numbers, it will always take 4 bytes.
     *
     * @param buffer ByteBuffer to write the value to
     * @param value Integer to be written with variable number of bytes
     * @return number of bytes used for integer representation
     */
    public static int writeVInt(ByteBuffer buffer, int value) {
        int byteCount = 1;
        while ((value & MSB_TWENTY_FIVE_BITS_MASK) != 0) {
            // There are more bytes to write apart from current least significant 7 bytes.
            // Hence, set a continuation bit to 1.
            buffer.writeByte((byte)((value & LSB_SEVEN_BITS_MASK) | CONTINUATION_BIT_MASK));
            value = value >>> 7; // Get rid of last 7 bits as those are written to buffer
            byteCount++;
        }
        // We are left with the last 7 bits in the integer. Write those 7 bits without continuation bit.
        buffer.writeByte((byte)(value));

        return byteCount;
    }

    /**
     * Reads a variable number of bytes from the buffer assuming little endian encoding and returns the integer value.
     *
     * @param buffer ByteBuffer to read the value from
     * @return Integer value read from the buffer
     */
    public static int readVInt(ByteBuffer buffer) {
        int value = 0, shiftBy = 0;
        byte currentByte = buffer.readByte();
        while ((currentByte & CONTINUATION_BIT_MASK) != 0) { // Check if continuation bit is set
            // Since we are storing the least significant bits first, bytes are read in reverse order.
            // Hence, shift thread bytes to set them at right position.
            value = value | ((currentByte & LSB_SEVEN_BITS_MASK) << shiftBy);
            currentByte = buffer.readByte();
            shiftBy += 7;
        }
        // Shift and write the last remaining byte
        value = value | (((int) currentByte) << shiftBy);

        return value;
    }
}
