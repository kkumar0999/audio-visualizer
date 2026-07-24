package io.github.kkumar0999.audiovisualizer.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

import static io.github.kkumar0999.audiovisualizer.util.LittleEndian.*;

/**
 * @author Krish Kumar
 * @since July 2, 2026
 */
public class WavFile {
    /**
     * The file path of the WAV file.
     */
    private final String filePath;

    /**
     * The size of the WAV file in bytes.
     */
    private long fileSize;

    /**
     * A map storing all the different chunks in the WAV file.
     */
    private final Map<String, WavChunk> chunks;

    /**
     * Creates a WavFile instance that links itself to the file with the provided file path.
     * @param   filePath        the file path of the file
     * @param   parsers         TODO
     * @throws  IOException     if the file isn't a valid WAV file or if it couldn't be read
     */
    private WavFile(String filePath, Map<String, ChunkParser<?>> parsers) throws IOException {
        if(!filePath.endsWith(".wav")) {
            throw new IOException("Invalid audio format: Not a valid WAV file!");
        }

        this.filePath = filePath;
        chunks = new HashMap<>();

        parseData(parsers);
    }

    public static Builder parse(String filePath) {
        return new Builder(filePath);
    }

    public static class Builder {
        private final String filePath;
        private final Map<String, ChunkParser<?>> parsers;

        public Builder(String filePath) {
            this.filePath = filePath;

            parsers = new HashMap<>();
            parsers.put("fmt ", (id, payload) -> new FormatChunk(payload));
            parsers.put("data", (id, payload) -> new DataChunk(payload));
        }

        public Builder registerChunk(String chunkId, ChunkParser<?> parser) {
            String key = String.format("%-4s", chunkId);    //Ensures 4 char alignment
            parsers.put(key, parser);
            return this;
        }

        public WavFile build() throws IOException {
            return new WavFile(filePath, parsers);
        }
    }

    private void parseData(Map<String, ChunkParser<?>> chunkParsers) throws IOException {
        File f = new File(filePath);
        fileSize = f.length();

        try(FileInputStream fis = new FileInputStream(f);
            DataInputStream dis = new DataInputStream(fis)) {

            //Reads in RIFF and WAVE markers
            byte[] riffBytes = new byte[4];
            byte[] waveBytes = new byte[4];
            dis.readFully(riffBytes);
            dis.skipBytes(4);
            dis.readFully(waveBytes);

            //Checks that the markers are valid
            String riffMarker = new String(riffBytes, StandardCharsets.US_ASCII);
            String waveMarker = new String(waveBytes, StandardCharsets.US_ASCII);
            if(!riffMarker.equals("RIFF") || !waveMarker.equals("WAVE")) {
                throw new IOException("Invalid audio format: Expected RIFF/WAVE " +
                        "container, found " + riffMarker + "/" + waveMarker);
            }

            boolean foundFmt = false;
            boolean foundData = false;

            while(dis.available() >= 8) {
                byte[] chunkHeader = new byte[8];
                dis.readFully(chunkHeader);

                String chunkId = new String(chunkHeader, 0, 4, StandardCharsets.US_ASCII);
                int chunkSize = readInt(chunkHeader, 4);

                byte[] payload = new byte[chunkSize];
                dis.readFully(payload);

                if(chunkSize % 2 == 1) {
                    dis.skipBytes(1);
                }

                if(chunkId.equals("fmt ")) {
                    foundFmt = true;
                }
                else if(chunkId.equals("data")) {
                    if(!foundFmt) {
                        throw new IOException("Malformed WAV file: Found 'data' chunk before 'fmt ' chunk.");
                    }
                    foundData = true;
                }

                ChunkParser<?> parser = chunkParsers.getOrDefault(
                        chunkId,
                        (id, bytes) -> new WavChunk(id.trim(), bytes)
                );

                String key = chunkId.trim();
                chunks.put(key, parser.parse(chunkId, payload));
            }

            if(!foundFmt) {
                throw new IOException("Corrupt WAV file: Missing mandatory 'fmt ' chunk before audio data. " +
                        "Unable to parse sample format.");
            }

            if(!foundData) {
                throw new IOException("Corrupt WAV file: Missing 'data' chunk. File contains format headers " +
                        "but no audio samples.");
            }
        }
    }

    /**
     *
     * @return
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     *
     * @return
     */
    public Map<String, WavChunk> getChunks() {
        return Collections.unmodifiableMap(chunks);
    }
}
