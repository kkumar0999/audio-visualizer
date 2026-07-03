package io.github.kkumar0999.audiovisualizer.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LittleEndian {
    public static int readInt(byte[] arr, int offset) {
        return ByteBuffer.wrap(arr, offset, 4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .getInt();
    }

    public static int readInt(byte[] arr) {
        return readInt(arr, 0);
    }

    public static int readShort(byte[] arr, int offset) {
        return ByteBuffer.wrap(arr, offset, 2)
                .order(ByteOrder.LITTLE_ENDIAN)
                .getInt();
    }

    public static int readShort(byte[] arr) {
        return readShort(arr, 0);
    }
}
