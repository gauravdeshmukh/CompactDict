package org.compactdict.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Units tests for {@link VIntUtils}
 */
public class VIntUtilsTest {
    @Test
    public void testWriteAndReadVIntZero() {
        ByteBuffer buffer = new ByteBuffer(new byte[10]);
        VIntUtils.writeVInt(buffer, 0);

        assertEquals(1, buffer.getPosition());

        buffer.setPosition(0);
        assertEquals(0, VIntUtils.readVInt(buffer));
    }

    @Test
    public void testWriteAndReadVIntLessThanOneByteInteger() {
        ByteBuffer buffer = new ByteBuffer(new byte[10]);
        VIntUtils.writeVInt(buffer, 63);

        assertEquals(1, buffer.getPosition());

        buffer.setPosition(0);
        assertEquals(63, VIntUtils.readVInt(buffer));
    }

    @Test
    public void testWriteAndReadVIntExactOneByteInteger() {
        ByteBuffer buffer = new ByteBuffer(new byte[10]);
        VIntUtils.writeVInt(buffer, 0xFF);

        assertEquals(2, buffer.getPosition());

        buffer.setPosition(0);
        assertEquals(0xFF, VIntUtils.readVInt(buffer));
    }

    @Test
    public void testWriteAndReadVIntLessThanTwoBytesInteger() {
        ByteBuffer buffer = new ByteBuffer(new byte[10]);
        VIntUtils.writeVInt(buffer, 0x0FFF);

        assertEquals(2, buffer.getPosition());

        buffer.setPosition(0);
        assertEquals(0x0FFF, VIntUtils.readVInt(buffer));
    }

    @Test
    public void testWriteAndReadVIntExactTwoBytesInteger() {
        ByteBuffer buffer = new ByteBuffer(new byte[10]);
        VIntUtils.writeVInt(buffer, 0xFFFF);

        assertEquals(3, buffer.getPosition());

        buffer.setPosition(0);
        assertEquals(0xFFFF, VIntUtils.readVInt(buffer));
    }

    @Test
    public void testWriteAndReadVIntLessThanThreeBytesInteger() {
        ByteBuffer buffer = new ByteBuffer(new byte[10]);
        VIntUtils.writeVInt(buffer, 0x0FFFFF);

        assertEquals(3, buffer.getPosition());

        buffer.setPosition(0);
        assertEquals(0x0FFFFF, VIntUtils.readVInt(buffer));
    }

    @Test
    public void testWriteAndReadVIntExactThreeBytesInteger() {
        ByteBuffer buffer = new ByteBuffer(new byte[10]);
        VIntUtils.writeVInt(buffer, 0xFFFFFF);

        assertEquals(4, buffer.getPosition());

        buffer.setPosition(0);
        assertEquals(0xFFFFFF, VIntUtils.readVInt(buffer));
    }

    @Test
    public void testWriteAndReadVIntLessThanFourBytesInteger() {
        ByteBuffer buffer = new ByteBuffer(new byte[10]);
        VIntUtils.writeVInt(buffer, 0x02FFFFFF);

        assertEquals(4, buffer.getPosition());

        buffer.setPosition(0);
        assertEquals(0x02FFFFFF, VIntUtils.readVInt(buffer));
    }

    @Test
    public void testWriteAndReadVIntExactFourBytesInteger() {
        ByteBuffer buffer = new ByteBuffer(new byte[10]);
        VIntUtils.writeVInt(buffer, 0xFFFFFFFF);

        assertEquals(5, buffer.getPosition());

        buffer.setPosition(0);
        assertEquals(0xFFFFFFFF, VIntUtils.readVInt(buffer));
    }

    @Test
    public void testWriteAndReadVIntMaxInteger() {
        ByteBuffer buffer = new ByteBuffer(new byte[10]);
        VIntUtils.writeVInt(buffer, Integer.MAX_VALUE);

        assertEquals(5, buffer.getPosition());

        buffer.setPosition(0);
        assertEquals(Integer.MAX_VALUE, VIntUtils.readVInt(buffer));
    }

    @Test
    public void testWriteAndReadVIntNegativeInteger() {
        ByteBuffer buffer = new ByteBuffer(new byte[10]);
        VIntUtils.writeVInt(buffer, -10);

        assertEquals(5, buffer.getPosition());

        buffer.setPosition(0);
        assertEquals(-10, VIntUtils.readVInt(buffer));
    }

    @Test
    public void testWriteAndReadVIntBigNegativeInteger() {
        ByteBuffer buffer = new ByteBuffer(new byte[10]);
        VIntUtils.writeVInt(buffer, -123456789);

        assertEquals(5, buffer.getPosition());

        buffer.setPosition(0);
        assertEquals(-123456789, VIntUtils.readVInt(buffer));
    }

    @Test
    public void testWriteAndReadVIntMinInteger() {
        ByteBuffer buffer = new ByteBuffer(new byte[10]);
        VIntUtils.writeVInt(buffer, Integer.MIN_VALUE);

        assertEquals(5, buffer.getPosition());

        buffer.setPosition(0);
        assertEquals(Integer.MIN_VALUE, VIntUtils.readVInt(buffer));
    }
}
