package io.github.kkumar0999.audiovisualizer.audio;

/**
 * @author  Krish Kumar
 * @since July 23, 2026
 */
public class DataChunk extends WavChunk {
    public DataChunk(byte[] payload) {
        super("data", payload);
    }
}
