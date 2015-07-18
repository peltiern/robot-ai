package fr.peltier.nicolas.robot.ai.test.boofcv;

import org.openimaj.image.Image;
import org.openimaj.image.processor.SinglebandImageProcessor;
import org.openimaj.image.renderer.ScanRasteriser;
import org.openimaj.image.renderer.ScanRasteriser.ScanLineListener;
import org.openimaj.math.geometry.shape.Polygon;
import org.openimaj.math.geometry.shape.Rectangle;

/**
 * Extract a polygon from an image. The output image will just contain the
 * polygon. The polygon is rasterised using a {@link ScanRasteriser}.
 * 
 * @author Jonathon Hare (jsh2@ecs.soton.ac.uk)
 * 
 * @param <T>
 *            The pixel type
 * @param <S>
 *            The {@link Image} type
 */
public class PolygonExtractionProcessor<T, S extends Image<T, S>> implements SinglebandImageProcessor<T, S> {
    private Polygon polygon;
    private T background;

    /**
     * Construct with the given polygon and background colour
     * 
     * @param p
     * @param colour
     */
    public PolygonExtractionProcessor(Polygon p, T colour) {
        this.polygon = p;
        this.background = colour;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openimaj.image.processor.ImageProcessor#processImage(org.openimaj
     * .image.Image)
     */
    @Override
    public void processImage(final S image) {
        final Polygon p = this.polygon;
        final Rectangle r = p.calculateRegularBoundingBox();

        final int dx = (int) r.x;
        final int dy = (int) r.y;

        final S output = image.newInstance((int) r.width, (int) r.height);
        output.fill(background);

        ScanRasteriser.scanFill(p.getVertices(), new ScanLineListener() {
            @Override
            public void process(int x1, int x2, int y) {
                for (int x = x1; x <= x2; x++) {
                    output.setPixel(x - dx, y - dy, image.getPixel(x, y));
                }
            }
        });

        image.internalAssign(output);
    }
}