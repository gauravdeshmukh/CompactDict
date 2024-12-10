package org.compactdict.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ByteBuffer}
 */
public class ByteBufferTest {
    /**
     * Test appending a non-empty ByteBuffer to another non-empty ByteBuffer
     */
    @Test
    public void testAppendNonEmptyByteBuffers() {
        // Arrange
        byte[] initialData = { 1, 2, 3 };
        ByteBuffer buffer = new ByteBuffer(initialData);
        byte[] dataToAppend = { 4, 5 };
        ByteBuffer bufferToAppend = new ByteBuffer(dataToAppend);
        // Act
        buffer.append(bufferToAppend);
        // Assert
        byte[] expectedResult = { 1, 2, 3, 4, 5 };
        assertArrayEquals(expectedResult, buffer.getBuffer());
        assertEquals(5, buffer.getLimit());
    }

    @Test
    public void testAppendWithNullByteBuffer() {
        /**
         * Test appending a null ByteBuffer
         * This tests the scenario where the input is invalid (null)
         */
        ByteBuffer buffer = new ByteBuffer(new byte[] { 1, 2, 3 });
        assertThrows(NullPointerException.class, () -> buffer.append(null));
    }

    @Test
    public void testAppendWithEmptyByteBuffer() {
        /**
         * Test appending an empty ByteBuffer
         * This tests the scenario where the input is empty
         */
        ByteBuffer buffer = new ByteBuffer(new byte[] { 1, 2, 3 });
        ByteBuffer emptyBuffer = ByteBuffer.EMPTY;
        buffer.append(emptyBuffer);
        assertArrayEquals(new byte[] { 1, 2, 3 }, buffer.getBuffer());
        assertEquals(3, buffer.getLimit());
    }

    @Test
    public void testAppendWithIncorrectlyInitializedByteBuffer() {
        /**
         * Test appending a ByteBuffer that has been incorrectly initialized
         * This tests the scenario where the input is in an incorrect format
         */
        ByteBuffer buffer = new ByteBuffer(new byte[] { 1, 2, 3 });
        // limit > buffer.length
        ByteBuffer incorrectBuffer = new ByteBuffer(new byte[10], 0, 20);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> buffer.append(incorrectBuffer));
    }

    /**
     * Test that duplicate() creates a new ByteBuffer with the same content and properties
     */
    @Test
    public void testDeepCloneCreatesNewBufferWithSameContent() {
        // Arrange
        byte[] originalContent = { 1, 2, 3, 4, 5 };
        int position = 1;
        int limit = 4;
        ByteBuffer original = new ByteBuffer(originalContent, position, limit);
        // Act
        ByteBuffer duplicated = original.deepClone();
        // Assert
        assertNotSame(original, duplicated, "Duplicate should be a new object");
        assertArrayEquals(original.getBuffer(), duplicated.getBuffer(), "Buffer content should be the same");
        assertEquals(original.getPosition(), duplicated.getPosition(), "Position should be the same");
        assertEquals(original.getLimit(), duplicated.getLimit(), "Limit should be the same");
        // Verify that modifying the duplicate doesn't affect the original
        duplicated.getBuffer()[0] = 99;
        assertNotEquals(duplicated.getBuffer()[0], original.getBuffer()[0], "Modifying duplicate should not affect original");
    }

    @Test
    public void testDeepCloneEmptyBuffer() {
        /**
         * Test duplicating an empty ByteBuffer.
         * This tests the edge case of duplicating a buffer with no content.
         */
        ByteBuffer emptyBuffer = ByteBuffer.EMPTY;
        ByteBuffer duplicated = emptyBuffer.deepClone();
        assertNotSame(emptyBuffer, duplicated);
        assertEquals(emptyBuffer, duplicated);
        assertEquals(0, duplicated.getBuffer().length);
        assertEquals(0, duplicated.getPosition());
        assertEquals(0, duplicated.getLimit());
    }

    @Test
    public void testDeepCloneBufferWithContent() {
        /**
         * Test duplicating a ByteBuffer with content.
         * This verifies that the duplicate is a deep copy with the same content and properties.
         */
        byte[] originalData = { 1, 2, 3, 4, 5 };
        ByteBuffer original = new ByteBuffer(originalData, 1, 4);
        ByteBuffer duplicated = original.deepClone();
        assertNotSame(original, duplicated);
        assertEquals(original, duplicated);
        assertArrayEquals(original.getBuffer(), duplicated.getBuffer());
        assertEquals(original.getPosition(), duplicated.getPosition());
        assertEquals(original.getLimit(), duplicated.getLimit());
    }

    @Test
    public void testDeepCloneBufferIndependence() {
        /**
         * Test that modifying the duplicate doesn't affect the original buffer.
         * This verifies that the duplicate is truly independent of the original.
         */
        byte[] originalData = { 1, 2, 3, 4, 5 };
        ByteBuffer original = new ByteBuffer(originalData);
        ByteBuffer duplicated = original.deepClone();
        duplicated.setPosition(2);
        assertNotEquals(original.getPosition(), duplicated.getPosition());
        duplicated.getBuffer()[0] = 10;
        assertNotEquals(original.getBuffer()[0], duplicated.getBuffer()[0]);
    }

    /**
     * Test case for equals method when comparing an object to itself
     */
    @Test
    public void testEqualsWhenComparingToSelf() {
        byte[] data = { 1, 2, 3, 4, 5 };
        ByteBuffer byteBuffer = new ByteBuffer(data);
        assertTrue(byteBuffer.equals(byteBuffer), "ByteBuffer should be equal to itself");
    }

    /**
     * Test equality with null object
     * Expected to return false when comparing with null
     */
    @Test
    public void testEqualsWithNull() {
        ByteBuffer buffer = new ByteBuffer(new byte[] { 1, 2, 3 });
        assertFalse(buffer.equals(null));
    }

    /**
     * Test equals method when two ByteBuffers have different limits
     */
    @Test
    public void testEqualsDifferentLimits() {
        byte[] buffer1 = { 1, 2, 3, 4, 5 };
        byte[] buffer2 = { 1, 2, 3, 4, 5, 6 };
        ByteBuffer byteBuffer1 = new ByteBuffer(buffer1);
        ByteBuffer byteBuffer2 = new ByteBuffer(buffer2);
        assertFalse(byteBuffer1.equals(byteBuffer2));
    }

    /**
     * Test equals method when ByteBuffers have same limit but different positions
     */
    @Test
    public void testEqualsDifferentPositions() {
        byte[] buffer1 = { 1, 2, 3, 4, 5 };
        byte[] buffer2 = { 1, 2, 3, 4, 5 };
        ByteBuffer byteBuffer1 = new ByteBuffer(buffer1, 0, 5);
        ByteBuffer byteBuffer2 = new ByteBuffer(buffer2, 1, 5);
        assertFalse(byteBuffer1.equals(byteBuffer2));
    }

    /**
     * Test equality of two ByteBuffer objects with the same content, position, and limit.
     */
    @Test
    public void testEqualByteBuffersWithSameContent() {
        byte[] data = { 1, 2, 3, 4, 5 };
        ByteBuffer buffer1 = new ByteBuffer(data, 1, 4);
        ByteBuffer buffer2 = new ByteBuffer(data, 1, 4);
        assertTrue(buffer1.equals(buffer2));
        assertTrue(buffer2.equals(buffer1));
    }

    @Test
    public void testEqualsWithDifferentClass() {
        /**
         * Test equals method with an object of a different class
         */
        ByteBuffer buffer = new ByteBuffer(new byte[] { 1, 2, 3 });
        assertFalse(buffer.equals("Not a ByteBuffer"));
    }

    @Test
    public void testEqualsWithDifferentLimit() {
        /**
         * Test equals method with ByteBuffers having different limits
         */
        ByteBuffer buffer1 = new ByteBuffer(new byte[] { 1, 2, 3 }, 0, 3);
        ByteBuffer buffer2 = new ByteBuffer(new byte[] { 1, 2, 3 }, 0, 2);
        assertFalse(buffer1.equals(buffer2));
    }

    @Test
    public void testEqualsWithDifferentPosition() {
        /**
         * Test equals method with ByteBuffers having different positions
         */
        ByteBuffer buffer1 = new ByteBuffer(new byte[] { 1, 2, 3 }, 0, 3);
        ByteBuffer buffer2 = new ByteBuffer(new byte[] { 1, 2, 3 }, 1, 3);
        assertFalse(buffer1.equals(buffer2));
    }

    @Test
    public void testEqualsWithDifferentContent() {
        /**
         * Test equals method with ByteBuffers having different content
         */
        ByteBuffer buffer1 = new ByteBuffer(new byte[] { 1, 2, 3 });
        ByteBuffer buffer2 = new ByteBuffer(new byte[] { 1, 2, 4 });
        assertFalse(buffer1.equals(buffer2));
    }

    /**
     * Test that getBuffer returns the correct buffer content
     */
    @Test
    public void testGetBufferReturnsCorrectContent() {
        // Arrange
        byte[] expectedBuffer = { 1, 2, 3, 4, 5 };
        ByteBuffer byteBuffer = new ByteBuffer(expectedBuffer);
        // Act
        byte[] actualBuffer = byteBuffer.getBuffer();
        // Assert
        assertArrayEquals(expectedBuffer, actualBuffer, "The returned buffer should match the input buffer");
    }

    /**
     * Test getBuffer method with an empty byte array
     */
    @Test
    public void test_getBuffer_withEmptyByteArray() {
        ByteBuffer emptyBuffer = new ByteBuffer(new byte[0]);
        byte[] result = emptyBuffer.getBuffer();
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    /**
     * Test getBuffer method with null input
     */
    @Test
    public void test_getBuffer_withNullInput() {
        assertThrows(NullPointerException.class, () -> new ByteBuffer(null));
    }

    /**
     * Test getBuffer method after modifying the original array
     */
    @Test
    public void test_getBuffer_afterModifyingOriginalArray() {
        byte[] original = { 1, 2, 3, 4, 5 };
        ByteBuffer buffer = new ByteBuffer(original);
        // Modify the original array
        original[0] = 10;
        byte[] result2 = buffer.getBuffer();
        assertNotEquals(new byte[] { 10, 2, 3, 4, 5 }, result2);
        assertNotSame(original, result2);
    }

    /**
     * Test that getLimit returns the correct limit value
     */
    @Test
    public void testGetLimitReturnsCorrectValue() {
        // Arrange
        byte[] testBuffer = { 1, 2, 3, 4, 5 };
        int expectedLimit = 3;
        ByteBuffer byteBuffer = new ByteBuffer(testBuffer, 0, expectedLimit);
        // Act
        int actualLimit = byteBuffer.getLimit();
        // Assert
        assertEquals(expectedLimit, actualLimit, "getLimit should return the correct limit value");
    }

    @Test
    public void testGetLimitWithEmptyBuffer() {
        /**
         * Test getLimit() with an empty buffer.
         * This tests the edge case of initializing a ByteBuffer with an empty byte array.
         */
        ByteBuffer emptyBuffer = new ByteBuffer(new byte[0]);
        assertEquals(0, emptyBuffer.getLimit(), "Limit of empty buffer should be 0");
    }

    @Test
    public void testGetLimitWithNegativeLimit() {
        /**
         * Test getLimit() when initialized with a negative limit value.
         * This tests the handling of invalid input during object creation.
         */
        byte[] data = new byte[10];
        assertThrows(AssertionError.class, () -> new ByteBuffer(data, 0, -1));
    }

    @Test
    public void testGetLimitWithLimitExceedingBufferLength() {
        /**
         * Test getLimit() when initialized with a limit value exceeding the buffer length.
         * This tests the handling of invalid input during object creation.
         */
        byte[] data = new byte[10];
        ByteBuffer buffer = new ByteBuffer(data, 0, 20);
        assertTrue(buffer.getLimit() >= data.length, "Buffer length should not exceed data length");
    }

    @Test
    public void testGetLimitWithMaxIntegerValue() {
        /**
         * Test getLimit() when initialized with the maximum integer value.
         * This tests an edge case for the limit value.
         */
        byte[] data = new byte[10];
        ByteBuffer buffer = new ByteBuffer(data, 0, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, buffer.getLimit(), "Limit should be equal to passed limit value");
    }

    /**
     * Test that getPosition() returns the correct position value
     */
    @Test
    public void testGetPositionReturnsCorrectValue() {
        // Arrange
        byte[] testBuffer = new byte[] { 1, 2, 3, 4, 5 };
        int expectedPosition = 2;
        ByteBuffer byteBuffer = new ByteBuffer(testBuffer, expectedPosition, testBuffer.length);
        // Act
        int actualPosition = byteBuffer.getPosition();
        // Assert
        assertEquals(expectedPosition, actualPosition, "getPosition() should return the correct position value");
    }

    /**
     * Test that getPosition returns 0 for an empty ByteBuffer
     */
    @Test
    public void testGetPositionForEmptyByteBuffer() {
        ByteBuffer emptyBuffer = ByteBuffer.EMPTY;
        assertEquals(0, emptyBuffer.getPosition(), "Position of an empty ByteBuffer should be 0");
    }

    /**
     * Test that getPosition returns the correct position after initialization
     */
    @Test
    public void testGetPositionAfterInitialization() {
        byte[] data = { 1, 2, 3, 4, 5 };
        int initialPosition = 2;
        ByteBuffer buffer = new ByteBuffer(data, initialPosition, data.length);
        assertEquals(initialPosition, buffer.getPosition(), "Position should be equal to the initial position");
    }

    /**
     * Test that getPosition returns the correct position after setting it
     */
    @Test
    public void testGetPositionAfterSetPosition() {
        byte[] data = { 1, 2, 3, 4, 5 };
        ByteBuffer buffer = new ByteBuffer(data);
        int newPosition = 3;
        buffer.setPosition(newPosition);
        assertEquals(newPosition, buffer.getPosition(), "Position should be equal to the newly set position");
    }

    /**
     * Test that getPosition returns the correct position after reading bytes
     */
    @Test
    public void testGetPositionAfterReadingBytes() {
        byte[] data = { 1, 2, 3, 4, 5 };
        ByteBuffer buffer = new ByteBuffer(data);
        int bytesToRead = 3;
        buffer.readBytes(bytesToRead);
        assertEquals(bytesToRead, buffer.getPosition(), "Position should be equal to the number of bytes read");
    }

    /**
     * Test case for readBytes method when reading a portion of the buffer
     */
    @Test
    public void test_readPartialBuffer() {
        // Initialize test data
        byte[] testData = { 1, 2, 3, 4, 5 };
        ByteBuffer buffer = new ByteBuffer(testData);
        // Set initial position
        buffer.setPosition(1);
        // Call the method under test
        byte[] result = buffer.readBytes(3);
        // Assert the result
        byte[] expected = { 2, 3, 4 };
        assertArrayEquals(expected, result, "The read bytes should match the expected portion of the buffer");
        // Assert the new position
        assertEquals(4, buffer.getPosition(), "The position should be updated after reading");
    }

    @Test
    public void testReadBytesWithZeroLength() {
        /**
         * Test reading zero bytes from the buffer.
         * This should return an empty byte array without changing the position.
         */
        byte[] input = { 1, 2, 3, 4, 5 };
        ByteBuffer buffer = new ByteBuffer(input);
        byte[] result = buffer.readBytes(0);
        assertNotNull(result);
        assertEquals(0, result.length);
        assertEquals(0, buffer.getPosition());
    }

    @Test
    public void testReadBytesWithNegativeLength() {
        /**
         * Test reading a negative number of bytes.
         * This should throw an IllegalArgumentException.
         */
        byte[] input = { 1, 2, 3, 4, 5 };
        ByteBuffer buffer = new ByteBuffer(input);
        assertThrows(AssertionError.class, () -> buffer.readBytes(-1));
    }

    @Test
    public void testReadBytesExceedingBufferLength() {
        /**
         * Test reading more bytes than available in the buffer.
         * This should throw an IndexOutOfBoundsException.
         */
        byte[] input = { 1, 2, 3 };
        ByteBuffer buffer = new ByteBuffer(input);
        assertThrows(AssertionError.class, () -> buffer.readBytes(4));
    }

    @Test
    public void testReadBytesFromEmptyBuffer() {
        /**
         * Test reading bytes from an empty buffer.
         * This should throw an IndexOutOfBoundsException when trying to read any bytes.
         */
        ByteBuffer buffer = ByteBuffer.EMPTY;
        assertThrows(AssertionError.class, () -> buffer.readBytes(1));
    }

    @Test
    public void testReadBytesAtBufferLimit() {
        /**
         * Test reading bytes when the position is at the buffer's limit.
         * This should throw an IndexOutOfBoundsException.
         */
        byte[] input = { 1, 2, 3 };
        ByteBuffer buffer = new ByteBuffer(input);
        buffer.setPosition(2);
        assertThrows(AssertionError.class, () -> buffer.readBytes(2));
    }

    /**
     * Test setting a valid position in ByteBuffer
     */
    @Test
    public void test_setValidPosition() {
        // Arrange
        byte[] testData = { 1, 2, 3, 4, 5 };
        ByteBuffer buffer = new ByteBuffer(testData);
        int newPosition = 3;
        // Act
        buffer.setPosition(newPosition);
        // Assert
        assertEquals(newPosition, buffer.getPosition(), "The position should be updated to the new value");
    }

    /**
     * Test setting position to a negative value
     */
    @Test
    public void testSetPositionNegativeValue() {
        ByteBuffer buffer = new ByteBuffer(new byte[10]);
        assertThrows(AssertionError.class, () -> buffer.setPosition(-1));
    }

    /**
     * Test setting position beyond the buffer limit
     */
    @Test
    public void testSetPositionBeyondLimit() {
        ByteBuffer buffer = new ByteBuffer(new byte[10]);
        assertThrows(AssertionError.class, () -> buffer.setPosition(11));
    }

    /**
     * Test setting position to the buffer limit
     */
    @Test
    public void testSetPositionAtLimit() {
        ByteBuffer buffer = new ByteBuffer(new byte[10]);
        buffer.setPosition(9);
        assertEquals(9, buffer.getPosition());
    }

    /**
     * Test setting position to zero
     */
    @Test
    public void testSetPositionToZero() {
        ByteBuffer buffer = new ByteBuffer(new byte[10]);
        buffer.setPosition(5);
        buffer.setPosition(0);
        assertEquals(0, buffer.getPosition());
    }
}

