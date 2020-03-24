package io.teamplayer.teamcore.util;

/**
 * Object used for easily creating bitmasked bytes mostly used while interacting with the
 * minecraft protocol
 */
public class BitMaskBuilder {

    private byte data = 0;

    /**
     * Modify the bit at the given position
     *
     * @param positon position of the bit
     * @param value new value of the bit at the specified position
     * @return this BitMaskBuilder
     * @throws IndexOutOfBoundsException when position is greater than 7 or less than 0
     */
    public final BitMaskBuilder setBit(byte positon, boolean value) {
        checkIndex(positon);

        if (value) {
            data |= (1 << positon);
        } else {
            data &= ~(1 << positon);
        }

        return this;
    }

    /**
     * Get the value of the bit at the given position
     *
     * @param position position of the bit
     * @return value of the bit at the specified position
     * @throws IndexOutOfBoundsException when position is greater than 7 or less than 0
     */
    public final boolean getBit(byte position) {
        checkIndex(position);

        return (data & 1 << position) == 1 << position;
    }

    private void checkIndex(byte index) {
        if (index < 0 || index > 7) throw new IndexOutOfBoundsException();
    }

    public final byte buildByte() {
        return data;
    }

}
