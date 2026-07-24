package io.github.kkumar0999.audiovisualizer.audio;

/**
 * @author  Krish Kumar
 * @since   July 23, 2026
 * @param <T>
 */
public interface ChunkParser<T extends WavChunk> {
    T parse(String id, byte[] payload);
}
