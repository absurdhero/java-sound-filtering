package net.raboof.sound;

import net.raboof.sound.filters.*;

import javax.sound.sampled.AudioFormat;

public class Main {

    public static void main(String args[]) {
        new Main().run();
    }

    private void run() {
        // define the audio format used for both input and output
        AudioFormat format = new AudioFormat(22050.0f, 16, 1, true, true);

        // create our filters and put them together in order
        // the PassThrough filter could be used instead to hear the input unmodified
        Filter amp = new Amplify(1.5f);
        //Filter distortion = new Distortion();
        Filter filterChain = new FilterChain(amp);

        // The OneTrackMixer reads input from an input source and outputs it
        OneTrackMixer mixer;
        try {
            mixer = new OneTrackMixer(format, filterChain);
            mixer.connect();
        } catch (InitializationException e) {
            System.err.println(e.getMessage());
            return;
        }

        // run() never returns
        mixer.run();
        mixer.close();
    }

}
