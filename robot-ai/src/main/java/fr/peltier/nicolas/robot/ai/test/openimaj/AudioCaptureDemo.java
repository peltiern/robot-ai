package fr.peltier.nicolas.robot.ai.test.openimaj;


import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.openimaj.audio.AudioFormat;
import org.openimaj.audio.JavaSoundAudioGrabber;
import org.openimaj.audio.SampleChunk;
import org.openimaj.audio.analysis.FourierTransform;
import org.openimaj.audio.filters.HanningAudioProcessor;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourMap;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.video.xuggle.XuggleAudio;

/**
 * @author David Dupplaw (dpd@ecs.soton.ac.uk)
 *
 * @created 28 Oct 2011
 */

public class AudioCaptureDemo {
    /** We'll at first ask for a sample chunk size of 1024. We might not get it */
    private int sampleChunkSize = 1024;

    /** The image displaying the waveform */
    private FImage img = null;

    /** The image displaying the FFT bins */
    private FImage fft = null;

    /** The image displaying the spectragram */
    private FImage spectra = null;

    /** The frequency bands to mark on the spectragram */
    private final double[] Hz = { 100, 500, 1000, 5000, 10000, 20000, 40000 };

    /** Whether to mark the frequency bands on the spectragram */
    private final boolean drawFreqBands = true;

    /** The Fourier transform processor we're going to use */
    private FourierTransform fftp = null;

    /**
     *
     */
    public AudioCaptureDemo() {
        this.img = new FImage(512, 400);
        DisplayUtilities.displayName(this.img, "display");

        this.fft = new FImage(this.img.getWidth(), 400);
        DisplayUtilities.displayName(this.fft, "fft");
        DisplayUtilities.positionNamed("fft", 0, this.img.getHeight());

        this.fftp = new FourierTransform();
        this.spectra = new FImage(800, this.sampleChunkSize / 2);
        DisplayUtilities.displayName(this.spectra, "spectra", true);
        DisplayUtilities.positionNamed("spectra", this.img.getWidth(), 0);

        // Uncomment the below to read from a file
//         final XuggleAudio xa = new XuggleAudio( AudioCaptureDemo.class.
//         getResource("/org/openimaj/demos/audio/140bpm-Arp.mp3" ) );

        // Uncomment the below for grabbing audio live
        final JavaSoundAudioGrabber xa = new JavaSoundAudioGrabber(new AudioFormat(16, 44.1, 2));
        xa.setMaxBufferSize(this.sampleChunkSize);
        new Thread(xa).start();

        // Hanning processor on top of the main audio stream
        final HanningAudioProcessor g =
                new HanningAudioProcessor(xa, this.img.getWidth() * xa.getFormat().getNumChannels())
            {
                @Override
                public SampleChunk processSamples(final SampleChunk sample)
                {
                    AudioCaptureDemo.this.updateDisplay(sample);
                    return sample;
                }
            };

        System.out.println("Using audio stream: " + g.getFormat());

        try {
            Thread.sleep(500);
            SampleChunk s = null;
            while ((s = g.nextSampleChunk()) != null)
                this.updateDisplay(s);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the visualisation each time a sample chunk comes in.
     *
     * @param s
     *            The sample chunk to display
     */
    public void updateDisplay(final SampleChunk s) {
        ShortBuffer sb = null;
        ByteBuffer bb = null;
        if ((bb = s.getSamplesAsByteBuffer()) != null)
            sb = bb.asShortBuffer();
        else
            return;

        // -------------------------------------------------
        // Draw waveform
        // -------------------------------------------------
        this.img.zero();
        final int yOffset = this.img.getHeight() / 2;
        for (int i = 1; i < s.getNumberOfSamples() / s.getFormat().getNumChannels(); i++) {
            this.img.drawLine(
                    i - 1, sb.get((i - 1) * s.getFormat().getNumChannels()) / 256 + yOffset,
                    i, sb.get(i * s.getFormat().getNumChannels()) / 256 + yOffset, 1f);
        }
        DisplayUtilities.displayName(this.img, "display");

        // -------------------------------------------------
        // Draw FFT
        // -------------------------------------------------
        this.fft.zero();
        this.fftp.process(s);

        final float[] f = this.fftp.getLastFFT()[0];
        final double binSize = (s.getFormat().getSampleRateKHz() * 1000) / (f.length / 2);

        for (int i = 0; i < f.length / 4; i++) {
            final float re = f[i * 2];
            final float im = f[i * 2 + 1];
            final float mag = (float) Math.log(Math.sqrt(re * re + im * im) + 1) / 50f;
            this.fft.drawLine(i * 2, this.fft.getHeight(), i * 2, this.fft.getHeight() - (int) (mag * this.fft.getHeight()), 2, 1f);
        }
        DisplayUtilities.displayName(this.fft, "fft");

        // -------------------------------------------------
        // Draw Spectra
        // -------------------------------------------------
        // System.out.println( "Sample chunk size: "+sampleChunkSize );
        // System.out.println( "Number of samples: "+s.getNumberOfSamples() );
        // System.out.println( "FFT size: "+f.length );
        if (s.getNumberOfSamples() != this.sampleChunkSize) {
            this.sampleChunkSize = s.getNumberOfSamples();
            this.spectra = new FImage(800, this.sampleChunkSize / 2);
            DisplayUtilities.displayName(this.spectra, "spectra");
            DisplayUtilities.positionNamed("spectra", this.img.getWidth(), 0);
        }

        this.spectra.shiftLeftInplace();

        // Draw the spectra
        for (int i = 0; i < f.length / 4; i++) {
            final float re = f[i * 2];
            final float im = f[i * 2 + 1];
            float mag = (float) Math.log(Math.sqrt(re * re + im * im) + 1) / 25f;
            if (mag > 1) {
                mag = 1;
            }
            this.spectra.setPixel(this.spectra.getWidth() - 1, this.spectra.getHeight() - i, mag);
        }

        final MBFImage drawSpectra = ColourMap.Jet.apply(this.spectra);
        if (this.drawFreqBands) {
            // drawSpectra = spectra.clone();

            // Draw the frequency bands
            for (final double freq : this.Hz) {
                final int y = drawSpectra.getHeight() - (int) (freq / binSize);
                drawSpectra.drawLine(0, y, this.spectra.getWidth(), y, RGBColour.GREEN);
                drawSpectra.drawText("" + freq + "Hz", 4, y, HersheyFont.TIMES_BOLD, 10, RGBColour.GREEN);
            }
        }

        DisplayUtilities.displayName(drawSpectra, "spectra");
    }

    /**
     *
     * @param args
     */
    public static void main(final String[] args) {
        new AudioCaptureDemo();
    }
}
