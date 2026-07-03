package io.github.kkumar0999.audiovisualizer.audio;

import java.io.FileInputStream;
import java.io.IOException;
import static io.github.kkumar0999.audiovisualizer.util.LittleEndian.*;

/**
 * @author  Krish Kumar
 * @since   July 2, 2026
 */
//TODO add error guarding for EOF
//TODO refactor audio stuff into AudioData class
public class WavFile {
    private final String filePath;
    private int fileSize;
    private int audioSize;

    private FormatChunk formatData;

    private short[] leftChannel;
    private short[] rightChannel;

    public WavFile(String filePath) throws IOException {
        if(!filePath.endsWith(".wav")) {
            throw new IOException("Not a valid WAV file!");
        }

        this.filePath = filePath;
        loadAndParse();
    }

    private void loadAndParse() throws IOException {
        try(FileInputStream fis = new FileInputStream(filePath)) {
            byte[] headerBytes = new byte[12];
            fis.read(headerBytes);

            String riffMarker = new String(headerBytes, 0, 4);
            String waveMarker = new String(headerBytes, 8, 4);
            if(!riffMarker.equals("RIFF") || !waveMarker.equals("WAVE")) {
                throw new IOException("Not a valid WAV file!");
            }

            fileSize = readInt(headerBytes, 4) + 8; //Account for 8 byte offset

            boolean dataFound = false;
            byte[] markerBytes = new byte[4];
            byte[] sizeBytes = new byte[4];
            while(!dataFound) {
                fis.read(markerBytes);
                String chunkMarker = new String(markerBytes);

                fis.read(sizeBytes);
                int chunkSize = readInt(sizeBytes);

                //TODO handle different sized fmt chunks
                if(chunkMarker.equals("fmt ")) {
                    byte[] fmtPayload = new byte[chunkSize];
                    fis.read(fmtPayload);

                    formatData = new FormatChunk(fmtPayload);
                }
                else if(chunkMarker.equals("data")) {
                    audioSize = chunkSize;
                    dataFound = true;
                }
                else {
                    int bytesToSkip = (chunkSize % 2 == 1) ? chunkSize + 1 : chunkSize;
                    fis.skip(bytesToSkip);
                }
            }
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public int getFileSize() {
        return fileSize;
    }

    public int getFormatType() {
        return formatData.getFormatType();
    }

    public int getNumChannels() {
        return formatData.getNumChannels();
    }

    public int getSampleRate() {
        return formatData.getSampleRate();
    }

    public int getByteRate() {
        return formatData.getByteRate();
    }

    public int getBytesPerSample() {
        return formatData.getBytesPerSample();
    }

    public int getBitsPerSample() {
        return formatData.getBitsPerSample();
    }
}
