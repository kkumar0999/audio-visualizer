package io.github.kkumar0999.audiovisualizer.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Simple utility class for reading Little Endian formatted data.
 * <p>
 *     This class was created primarily to parse primitives (ints and shorts)
 *     from binary file headers, such as RIFF/WAV formats.
 * </p>
 *
 * @author  Krish Kumar
 * @since   July 2, 2026
 */
public class LittleEndian {
    /**
     * Reads a 4-byte int from a byte array at the provided offset in Little Endian order.
     *
     * @param arr                           the byte array to read from
     * @param offset                        the offset to start at
     * @return                              the parsed 4-byte int value
     * @throws  IndexOutOfBoundsException   if the offset is out of bounds or the array
     *                                      doesn't have enough remaining bytes
     */
    public static int readInt(byte[] arr, int offset) {
        if(offset + 4 > arr.length) {
            throw new IndexOutOfBoundsException("Cannot read a 4-byte int at offset " +
                    offset + " for array of length " + arr.length);
        }

        return ByteBuffer.wrap(arr, offset, 4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .getInt();
    }

    /**
     * Reads a 4-byte int from the start of a byte array in Little Endian order.
     * <p>
     *     This method has the same effect as calling {@code readInt(arr, 0)}.
     * </p>
     *
     * @param arr                           the byte array to read from
     * @return                              the parsed 4-byte int value
     * @throws  IndexOutOfBoundsException   if the array doesn't have enough
     *                                      remaining bytes
     */
    public static int readInt(byte[] arr) {
        return readInt(arr, 0);
    }

    /**
     * Reads a 2-byte short from a byte array at the provided offset in Little Endian order.
     *
     * @param arr                           the byte array to read from
     * @param offset                        the offset to start at
     * @return                              the parsed 2-byte short value
     * @throws  IndexOutOfBoundsException   if the offset is out of bounds or the array
     *                                      doesn't have enough remaining bytes
     */
    public static int readShort(byte[] arr, int offset) {
        if(offset + 2 > arr.length) {
            throw new IndexOutOfBoundsException("Cannot read a 2-byte short at offset " +
                    offset + " for array of length " + arr.length);
        }

        return ByteBuffer.wrap(arr, offset, 2)
                .order(ByteOrder.LITTLE_ENDIAN)
                .getInt();
    }

    /**
     * Reads a 2-byte short from the start of a byte array in Little Endian order.
     * <p>
     *     This method has the same effect as calling {@code readShort(arr, 0)}.
     * </p>
     *
     * @param arr                           the byte array to read from
     * @return                              the parsed 2-byte short value
     * @throws  IndexOutOfBoundsException   if the array doesn't have enough
     *                                      remaining bytes
     */
    public static int readShort(byte[] arr) {
        return readShort(arr, 0);
    }
}
