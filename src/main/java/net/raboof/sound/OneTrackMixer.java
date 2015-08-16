package net.raboof.sound;

import net.raboof.sound.filters.Filter;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.Arrays;

/** captures from an input source (e.g. microphone) and outputs to speakers.
 *
 * Filters are applied in between.
 * */
public class OneTrackMixer {
    public final AudioFormat format;
    private Filter filter;
    public TargetDataLine in;
    public SourceDataLine out;

    DataLine.Info inInfo;
    DataLine.Info outInfo;

    public OneTrackMixer(AudioFormat format, Filter filter) throws InitializationException {
        this.format = format;
        this.filter = filter;
        inInfo = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(inInfo)) {
            throw new InitializationException("line-in format not supported");
        }

        outInfo = new DataLine.Info(SourceDataLine.class, format);
        if (!AudioSystem.isLineSupported(outInfo)) {
            throw new InitializationException("line-out format not supported");
        }
    }

    public void connect() throws InitializationException {
        try {
            in = (TargetDataLine) AudioSystem.getLine(inInfo);
            in.open(format);
        } catch (LineUnavailableException ex) {
            throw new InitializationException("line missing");
        }

        try {
            if (!AudioSystem.isLineSupported(outInfo)) {
                throw new InitializationException("line-in format not supported");
            }
            out = AudioSystem.getSourceDataLine(format);
            out.open(format);
        } catch (LineUnavailableException e) {
            throw new InitializationException(e);
        }

        // begin capture
        in.start();

        // begin output
        out.start();
    }

    public void close() {
        in.close();
        out.close();
    }

    public void run() {
        int numBytesRead;
        byte[] data = new byte[in.getBufferSize() / 20];
        debug("input buffer: %s internal buffer: %s", in.getBufferSize(), data.length);

        while (true) {
            // Read the next chunk of data from the TargetDataLine.
            numBytesRead = in.read(data, 0, data.length);

            debug("orig: (%s) %s", numBytesRead, Arrays.toString(data));

            int[] intData = intArrayFrom16bits(numBytesRead, data);

            int numShorts = numBytesRead / Short.BYTES;

            // Transform it
            filter.filter(numShorts, intData);

            intArrayTo16bits(data, intData, numShorts);

            debug("out: (%s)   %s", numBytesRead, Arrays.toString(data));

            // Save this chunk of data.
            out.write(data, 0, numBytesRead);
        }

    }

    private int[] intArrayFrom16bits(int numBytesRead, byte[] data) {
        int[] intData = new int[numBytesRead];
        ShortBuffer shortBuffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN).asShortBuffer();

        for (int i = 0; i < shortBuffer.limit(); i++) {
            intData[i] = shortBuffer.get(i);
        }
        return intData;
    }

    private void intArrayTo16bits(byte[] data, int[] intData, int numShorts) {
        for (int i = 0; i < numShorts; i++) {
            short value = (short) intData[i];
            data[i*2] = (byte) ((value >> 8) & 0xFF);
            data[i*2+1] = (byte) (value & 0xFF);
        }
    }

    boolean debug_print = false;
    private void debug(String fmt, Object... args) {
        if (debug_print)
            System.out.printf(fmt + "\n", args);
    }
}
