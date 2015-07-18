package fr.peltier.nicolas.robot.ai.test.openimaj;

import org.openimaj.image.FImage;
import org.openimaj.image.MBFImage;
import org.openimaj.video.capture.VideoCapture;
import org.openimaj.video.capture.VideoCaptureException;

public class SimpleFrameDifferenceMotionDetector {
    /**
     * @param args
     * @throws VideoCaptureException
     */
    public static void main(String[] args) throws VideoCaptureException {
        // Setup live capture
        final VideoCapture c = new VideoCapture(320, 240);

        // get the first frame
        FImage last = c.getNextFrame().flatten();
        // iterate through the frames
        for (final MBFImage frame : c) {
            final FImage current = frame.flatten();

            // compute the squared difference from the last frame
            float val = 0;
            for (int y = 0; y < current.height; y++) {
                for (int x = 0; x < current.width; x++) {
                    final float diff = (current.pixels[y][x] - last.pixels[y][x]);
                    val += diff * diff;
                }
            }

            // might need adjust threshold:
            if (val > 10) {
                System.out.println("motion");
            }

            // set the current frame to the last frame
            last = current;
        }

        c.close();
    }
}
