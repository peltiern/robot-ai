package fr.peltier.nicolas.robot.ai.test.openimaj;

import java.io.IOException;

import org.openimaj.image.MBFImage;
import org.openimaj.math.geometry.point.Point2d;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.VideoDisplayListener;
import org.openimaj.video.capture.VideoCapture;
import org.openimaj.video.processing.motion.GridMotionEstimator;
import org.openimaj.video.processing.motion.MotionEstimator;
import org.openimaj.video.processing.motion.MotionEstimatorAlgorithm;
import org.openimaj.video.translator.FImageToMBFImageVideoTranslator;
import org.openimaj.video.translator.MBFImageToFImageVideoTranslator;

/**
 * Example showing how to estimate a motion field in a video using phase
 * correlation.
 *
 * @author David Dupplaw (dpd@ecs.soton.ac.uk)
 */
public class PhaseCorrelationExample {
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final VideoCapture vc = new VideoCapture(640, 480);
        final MotionEstimator me = new GridMotionEstimator(
                new MBFImageToFImageVideoTranslator(vc),
                new MotionEstimatorAlgorithm.PHASE_CORRELATION(), 10, 10, true);

        final VideoDisplay<MBFImage> vd = VideoDisplay.createVideoDisplay(
                new FImageToMBFImageVideoTranslator(me));
        vd.addVideoListener(new VideoDisplayListener<MBFImage>()
        {
            @Override
            public void afterUpdate(VideoDisplay<MBFImage> display)
            {
            }

            @Override
            public void beforeUpdate(MBFImage frame)
            {
                for (final Point2d p : me.motionVectors.keySet())
                {
                    final Point2d p2 = me.motionVectors.get(p);
                    frame.drawLine((int) p.getX(), (int) p.getY(),
                            (int) (p.getX() + p2.getX()),
                            (int) (p.getY() + p2.getY()),
                            2, new Float[] { 1f, 0f, 0f });
                }
            }
        });
    }
}
