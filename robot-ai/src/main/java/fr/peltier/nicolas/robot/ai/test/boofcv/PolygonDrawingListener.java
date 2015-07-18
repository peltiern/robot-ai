package fr.peltier.nicolas.robot.ai.test.boofcv;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.renderer.MBFImageRenderer;
import org.openimaj.math.geometry.point.Point2d;
import org.openimaj.math.geometry.point.Point2dImpl;
import org.openimaj.math.geometry.shape.Polygon;

/**
 * {@link MouseListener} that allows users to click
 * a series of points in an image representing the vertices
 * of a {@link Polygon}.
 *
 * @author Sina Samangooei (ss@ecs.soton.ac.uk)
 *
 */
public class PolygonDrawingListener implements MouseListener {
    private Polygon polygon;

    /**
     * Default constructor
     */
    public PolygonDrawingListener() {
        this.polygon = new Polygon();
    }

    /**
     * Reset the polygon.
     */
    public void reset() {
        this.polygon = new Polygon();
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {

    }

    @Override
    public void mouseEntered(MouseEvent arg0) {}

    @Override
    public void mouseExited(MouseEvent arg0) {}

    @Override
    public void mousePressed(MouseEvent arg0) {
        this.polygon.getVertices().add(new Point2dImpl(arg0.getX(),arg0.getY()));
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {}

    /**
     * @return the polygon created by the user
     */
    public Polygon getPolygon() {
        return this.polygon;
    }

    /**
     * Draw the polygon onto an image.
     * @param image the image to draw on.
     */
    public void drawPoints(MBFImage image) {
        Polygon p = getPolygon();
        MBFImageRenderer renderer = image.createRenderer();

        if(p.getVertices().size() > 2) {
            renderer.drawPolygon(p, 3,RGBColour.RED);
        }

        for(Point2d point : p.getVertices()) {
            renderer.drawPoint(point, RGBColour.BLUE, 5);
        }
    }
}