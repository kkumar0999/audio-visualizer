package io.github.kkumar0999.audiovisualizer.audio;

import static io.github.kkumar0999.audiovisualizer.util.LittleEndian.*;

/**
 * @author  Krish Kumar
 * @since   July 2, 2026
 */
public class FormatChunk {
    private final int size;

    private int formatType;
    private int numChannels;
    private int sampleRate;
    private int byteRate;
    private int bytesPerSample;
    private int bitsPerSample;

    public FormatChunk(byte[] payload) {
        size = payload.length;

        formatType = readShort(payload, 0);
        numChannels = readShort(payload, 2);
        sampleRate = readInt(payload, 4);
        byteRate = readInt(payload, 8);
        bytesPerSample = readShort(payload, 12);
        bitsPerSample = (size >= 16) ? readShort(payload, 14) : 8;
    }

    public int getSize() {
        return size;
    }

    public int getFormatType() {
        return formatType;
    }

    public int getNumChannels() {
        return numChannels;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getByteRate() {
        return byteRate;
    }

    public int getBytesPerSample() {
        return bytesPerSample;
    }

    public int getBitsPerSample() {
        return bitsPerSample;
    }
}
