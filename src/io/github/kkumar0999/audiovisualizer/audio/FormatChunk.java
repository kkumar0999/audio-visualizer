package io.github.kkumar0999.audiovisualizer.audio;

import static io.github.kkumar0999.audiovisualizer.util.LittleEndian.*;

/**
 * @author  Krish Kumar
 * @since   July 2, 2026
 */
public class FormatChunk extends WavChunk {
    private int formatType;
    private int numChannels;
    private int sampleRate;
    private int byteRate;
    private int bytesPerSample;
    private int bitsPerSample;

    //TODO handle chunks bigger than 16 bytes
    public FormatChunk(byte[] payload) {
        super("fmt", payload);
        parseData(payload);
    }

    private void parseData(byte[] payload) {
        int size = payload.length;

        formatType = readShort(payload, 0);
        numChannels = readShort(payload, 2);
        sampleRate = readInt(payload, 4);
        byteRate = readInt(payload, 8);
        bytesPerSample = readShort(payload, 12);
        bitsPerSample = (size >= 16) ? readShort(payload, 14) : 8;
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
