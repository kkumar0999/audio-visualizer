import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/*
 * An experiment for learning about the internals of a WAV file. This program
 * prints some information about a WAV file.
 *
 * This program is not intended for use. It's simply here as a reference for my
 * actual code.
 *
 * Started: July 1, 2026
 * Ended: July 2, 2026
 */
public class WavInfo {
    public static void main(String[] args) {
        Scanner read = new Scanner(System.in);

        System.out.print("Enter in the WAV file you want to analyze: ");
        String filename = read.nextLine().trim();

        if(!filename.endsWith(".wav")) {
            System.err.println("Error: File provided was not a WAV file!");
            System.exit(1);
        }

        File f = new File(filename);
        try(FileInputStream fis = new FileInputStream(f)) {
            readFile(fis);
        } catch(IOException e) {
            System.err.println("Error: Could not read file: " + filename);
            System.exit(1);
        }
    }

    public static void readFile(FileInputStream fis) throws IOException {
        byte[] headerBytes = new byte[36];
        fis.read(headerBytes);

        //Bytes 0-3 are the RIFF marker
        String riffMarker = new String(headerBytes, 0, 4);
        if(riffMarker.equals("RIFF")) {
            System.out.println("RIFF marker found");
        }
        else {
            System.err.println("Error: Not a valid RIFF file!");
            System.exit(1);
        }

        //Bytes 4-7 are the size bytes (minus 8 bytes) in Little Endian
        int sizeFromHeader = readInt(headerBytes, 4);
        int actualSize = sizeFromHeader + 8;
        System.out.println("File size from header: " + sizeFromHeader + " bytes");
        System.out.println("Actual file size: " + actualSize + " bytes");

        //Bytes 8-11 are the WAVE marker
        String waveMarker = new String(headerBytes, 8, 4);
        if(waveMarker.equals("WAVE")) {
            System.out.println("WAVE marker found");
        }
        else {
            System.err.println("Error: Not a valid WAVE file!");
            System.exit(1);
        }

        //Bytes 12-15 are the format chunk marker
        String fmtMarker = new String(headerBytes, 12, 4);
        if(fmtMarker.equals("fmt ")) {
            System.out.println("Format chunk found");
        }
        else {
            System.err.println("Error: Format chunk not found!");
            System.exit(1);
        }

        //TODO make format chunk parsing dynamic
        //Bytes 16-19 are the format chunk size in Little Endian
        int fmtChunkSize = readInt(headerBytes, 16);
        System.out.println("Format chunk size: " + fmtChunkSize + " bytes");

        //Bytes 20-21 are the format type
        short fmtType = readShort(headerBytes, 20);
        System.out.println("Format type: " + fmtType);

        //Bytes 22-23 are the number of channels used
        short numChannels = readShort(headerBytes, 22);
        System.out.println("Number of channels: " + numChannels);

        //Bytes 24-27 are the sample rate
        int sampleRate = readInt(headerBytes, 24);
        System.out.println("Sample rate: " + sampleRate + " Hz");

        //Bytes 28-31 are the byte rate
        int byteRate = readInt(headerBytes, 28);
        System.out.println("Byte rate: " + byteRate + " bytes/sec");

        //Bytes 32-33 are the block align (bytes/sample)
        short blockAlign = readShort(headerBytes, 32);
        System.out.println("Block align: " + blockAlign + " bytes/sample");

        //Bytes 34-35 are the bits per sample (audio resolution)
        short bitsPerSample = readShort(headerBytes, 34);
        System.out.println("Bits per sample: " + bitsPerSample + "-bit");

        //Handles other metadata and the data chunk
        boolean dataFound = false;
        int audioDataSize = 0;
        while(!dataFound) {
            byte[] markerBytes = new byte[4];
            byte[] sizeBytes = new byte[4];
            fis.read(markerBytes);
            fis.read(sizeBytes);

            String chunkMarker = new String(markerBytes);
            int chunkSize = readInt(sizeBytes, 0);
            System.out.println("Encountered chunk: " + chunkMarker + " (" +
                    chunkSize + " bytes)");

            if(chunkMarker.equals("data")) {
                audioDataSize = chunkSize;
                dataFound = true;
                System.out.println("Audio data chunk found (" + audioDataSize + " bytes)");
            }
            else {
                System.out.println("Skipping " + chunkSize + " bytes of metadata...");
                fis.skip(chunkSize);
            }
        }
    }

    public static int readInt(byte[] arr, int offset) {
        return ByteBuffer.wrap(arr, offset, 4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .getInt();
    }

    public static short readShort(byte[] arr, int offset) {
        return ByteBuffer.wrap(arr, offset, 2)
                .order(ByteOrder.LITTLE_ENDIAN)
                .getShort();
    }
}