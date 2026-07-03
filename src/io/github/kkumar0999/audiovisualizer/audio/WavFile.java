package io.github.kkumar0999.audiovisualizer.audio;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

//TODO add error guarding for EOF
public class WavFile {
    private final String filePath;
    private int fileSize;
    private int audioSize;

    private FormatChunk formatData;

    //Format chunk
    private int formatType;
    private int numChannels;
    private int sampleRate;
    private int byteRate;
    private int bytesPerSample;
    private int bitsPerSample;

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
                    byte[] fmtBytes = new byte[chunkSize];
                    fis.read(fmtBytes);

                    formatType = readShort(fmtBytes, 0);
                    numChannels = readShort(fmtBytes, 2);
                    sampleRate = readInt(fmtBytes, 4);
                    byteRate = readInt(fmtBytes, 8);
                    bytesPerSample = readShort(fmtBytes, 12);
                    bitsPerSample = (chunkSize >= 16) ? readShort(fmtBytes, 14) : 8;
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

    private int readInt(byte[] arr, int offset) {
        return ByteBuffer.wrap(arr, offset, 4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .getInt();
    }

    private int readInt(byte[] arr) {
        return readInt(arr, 0);
    }

    private int readShort(byte[] arr, int offset) {
        return ByteBuffer.wrap(arr, offset, 2)
                .order(ByteOrder.LITTLE_ENDIAN)
                .getInt();
    }

    private int readShort(byte[] arr) {
        return readShort(arr, 0);
    }

    public String getFilePath() {
        return filePath;
    }

    public int getFileSize() {
        return fileSize;
    }

    public int getAudioSize() {
        return audioSize;
    }
}
