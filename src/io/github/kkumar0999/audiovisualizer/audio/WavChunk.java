package io.github.kkumar0999.audiovisualizer.audio;

/**
 * @author  Krish Kumar
 * @since   July 3, 2026
 */
public class WavChunk {
    private final String id;
    private final byte[] payload;

    public WavChunk(String id, byte[] payload) {
        this.id = id;
        this.payload = payload;
    }

    public int getSize() {
        return payload.length;
    }

    public String getId() {
        return id;
    }

    public byte[] getBytes() {
        return payload;
    }
}
