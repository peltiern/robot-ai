package fr.peltier.nicolas.robot.ai.test.openimaj;

import java.util.ArrayList;
import java.util.List;

import javax.swing.border.Border;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.contour.Contour;
import org.openimaj.image.contour.ContourRenderer;
import org.openimaj.image.contour.ContourType;
import org.openimaj.image.contour.SuzukiContourProcessor;
import org.openimaj.image.processing.convolution.FFastGaussianConvolve;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.processing.edges.SUSANEdgeDetector;
import org.openimaj.image.processing.threshold.AdaptiveLocalThresholdGaussian;
import org.openimaj.image.processing.threshold.AdaptiveLocalThresholdMean;
import org.openimaj.image.processing.threshold.AdaptiveLocalThresholdMedian;
import org.openimaj.math.geometry.line.Line2d;
import org.openimaj.math.geometry.point.Point2d;
import org.openimaj.math.geometry.shape.Polygon;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.VideoDisplayListener;
import org.openimaj.video.capture.VideoCapture;

public class MotionTest implements VideoDisplayListener<MBFImage> {

    private VideoCapture capture;
    private VideoDisplay<MBFImage> videoFrame;
    
    private FImage imageGriseFloueTampon;
    
    private FImage imageNoire;

    public MotionTest() throws Exception {
        capture = new VideoCapture(640, 480);
        capture.setFPS(25);

        videoFrame = VideoDisplay.createVideoDisplay(capture);
        videoFrame.addVideoListener(this);
        
        imageNoire = new FImage(640,  480);
        imageNoire.zero();
    }

    @Override
    public void afterUpdate(VideoDisplay<MBFImage> display) {
        // do nothing
    }

    @Override
    public synchronized void beforeUpdate(MBFImage frame) {
        if (imageGriseFloueTampon != null) {
            // Mettre l'image en niveaux de gris en récupérant un seul canal
            FImage imageGriseFloue = frame.flatten();
            
            // Application d'un flou pour supprimer le bruit
            FFastGaussianConvolve gauss = new FFastGaussianConvolve(10, 3);
//            FGaussianConvolve gauss = new FGaussianConvolve(10);
            gauss.processImage(imageGriseFloue);
            
            // Soustraction de l'image avec l'image précédente
            FImage imageDiff = imageGriseFloue.subtract(imageGriseFloueTampon).abs();
            
            MBFImage imageCalculee = MBFImage.createRGB(imageDiff);
            imageCalculee.getBand(0).zero().add(100f);
            imageCalculee.getBand(1).add(40f);
            imageCalculee.getBand(2).add(30f);
;//            imageCalculee.
//            imageCalculee.addBand(imageNoire.zero());
//            imageCalculee.addBand(imageDiff);
//            imageCalculee.addBand(imageNoire.zero());
//            for (int i = 0; i < imageDiff.getWidth(); i++) {
//                for (int j = 0; j < imageDiff.getHeight(); j++) {
//                    imageCalculee.setPixel(i, j, new Float[] {0f, imageDiff.getPixel(i,j), 0f});
//                }
//            }
            
            
            // Binarisation par seuil
            imageDiff.threshold(0.1f);
           
//            AdaptiveLocalThresholdGaussian threshold = new AdaptiveLocalThresholdGaussian(10, 10f);
//            threshold.processImage(imageDiff);
            
            Contour contours = SuzukiContourProcessor.findContours(imageDiff);
////            Point2d centre = contours.calculateCentroid();
////            SUSANEdgeDetector edgeDetector = new SUSANEdgeDetector();
////            edgeDetector.processImage(imageDiff);
            final List<Point2d> listePointsCentres = new ArrayList<Point2d>();
            for (Contour contour : contours.children) {
                if (contour.type.equals(ContourType.OUTER)) {
                    if (contour.calculateArea() > 1000) {
                        listePointsCentres.add(contour.calculateCentroid());
                        imageCalculee.drawLines(contour.getLines(), 1, new Float[] {0f,255f,0f});
                    }
                }
            }
            if (listePointsCentres.size() > 0) {
                if (listePointsCentres.size() == 1) {
                    frame.drawPoint(listePointsCentres.get(0), new Float[] {255f,0f,0f}, 20);
                } else if (listePointsCentres.size() == 2) {
                    Line2d ligne = new Line2d(listePointsCentres.get(0), listePointsCentres.get(1));
                    frame.drawLine(ligne, 10, new Float[] {0f,0f,255f});
                    frame.drawPoint(ligne.calculateCentroid(), new Float[] {255f,0f,0f}, 20);
                } else {
                    Polygon polygon = new Polygon(listePointsCentres);
                    frame.drawPolygon(polygon, 10, new Float[] {0f,0f,255f});
                    frame.drawPoint(polygon.calculateCentroid(), new Float[] {255f,0f,0f}, 20);
                }
            }
            DisplayUtilities.displayName(imageCalculee, "imageCalculee");
//            
////            resultat.drawPoint(centre, new Float[] {255f,0f,0f}, 20);
////            ContourRenderer.drawContours(resultat, contours);
            imageGriseFloueTampon = imageGriseFloue;
//            frame.drawImage(MBFImage.createRGB(imageDiff), 0, 0);
        } else {
            imageGriseFloueTampon = frame.clone().flatten();
            // Application d'un flou pour supprimer le bruit
            FFastGaussianConvolve gauss = new FFastGaussianConvolve(30, 3);
            gauss.processImage(imageGriseFloueTampon);
        }
        
    }

    public static void main(String[] args) throws Exception {
        new MotionTest();
    }

}
