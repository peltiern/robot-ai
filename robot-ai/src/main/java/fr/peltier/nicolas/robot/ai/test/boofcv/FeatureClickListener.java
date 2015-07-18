package fr.peltier.nicolas.robot.ai.test.boofcv;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.Image;
import org.openimaj.image.feature.local.engine.ipd.InterestPointImageExtractorProperties;
import org.openimaj.image.feature.local.interest.InterestPointData;
import org.openimaj.image.feature.local.keypoints.InterestPointKeypoint;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.processing.resize.ResizeProcessor;
import org.openimaj.image.processor.SinglebandImageProcessor;
import org.openimaj.math.geometry.point.Point2dImpl;
import org.openimaj.math.geometry.shape.Circle;

/**
 * Click on features and draw them
 * @author Sina Samangooei (ss@ecs.soton.ac.uk)
 *
 * @param <S>
 * @param <T>
 */
public class FeatureClickListener<S,T extends Image<S,T> & SinglebandImageProcessor.Processable<Float,FImage,T> > implements MouseListener {

    private LocalFeatureList<InterestPointKeypoint<InterestPointData>> points = null;
    private T image;
    private JFrame frame = null;
    private ResizeProcessor r = new ResizeProcessor(100,100);
    
    @Override
    public synchronized void mouseClicked(MouseEvent e) {
        if(this.points == null) return;
        double dist = Double.MAX_VALUE;
        Circle foundShape = null;
        InterestPointKeypoint<InterestPointData> foundPoint = null;
        Point2dImpl clickPoint = new Point2dImpl(e.getPoint().x,e.getPoint().y);
        for(InterestPointKeypoint<InterestPointData> point : points){
            Circle ellipse = new Circle(point.location.x, point.location.y, point.scale);
            if(ellipse.isInside(clickPoint)){
//              double pdist = Math.sqrt(clickPoint.x * clickPoint.x + clickPoint.y * clickPoint.y);
                double pdist = point.scale;
                if(pdist < dist){
                    foundShape = ellipse;
                    foundPoint = point;
                    dist = pdist;
                }
            }
        }
        if(foundShape!=null){
//          PolygonExtractionProcessor<S, T> ext = new PolygonExtractionProcessor<S,T>(foundShape, image.newInstance(1, 1).getPixel(0,0));
            FGaussianConvolve blur = new FGaussianConvolve (foundPoint.scale);
            InterestPointImageExtractorProperties<S, T> extract = new InterestPointImageExtractorProperties<S,T>(image.process(blur),foundPoint.location);
            if(frame== null){
                frame = DisplayUtilities.display(extract.image.process(r));
            }
            else{
                frame.dispose();
                frame = DisplayUtilities.display(extract.image.process(r));
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }


    /**
     * @return the clicked on
     */
    public LocalFeatureList<InterestPointKeypoint<InterestPointData>> getPoints() {
        return points;
    }

    /**
     * the image and keypoints to draw
     * @param kpl
     * @param image
     */
    public synchronized void setImage(LocalFeatureList<InterestPointKeypoint<InterestPointData>> kpl,T image) {
        this.image = image;
        this.points = kpl;
    }

    /**
     * @return the underlying image being clicked on
     */
    public T getImage() {
        return image;
    }

}
