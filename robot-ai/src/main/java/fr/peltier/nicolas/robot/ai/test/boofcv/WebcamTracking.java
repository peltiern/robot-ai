package fr.peltier.nicolas.robot.ai.test.boofcv;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import com.github.sarxos.webcam.Webcam;

import boofcv.abst.feature.detect.interest.ConfigGeneralDetector;
import boofcv.abst.feature.tracker.PointTrack;
import boofcv.abst.feature.tracker.PointTracker;
import boofcv.alg.tracker.klt.PkltConfig;

import boofcv.factory.feature.tracker.FactoryPointTracker;
import boofcv.gui.feature.VisualizeFeatures;
import boofcv.gui.image.ImagePanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.webcamcapture.UtilWebcamCapture;
import boofcv.struct.image.ImageFloat32;

public class WebcamTracking {
    
    public static void main(String[] args) {
        
        // tune the tracker for the image size and visual appearance
        ConfigGeneralDetector configDetector = new ConfigGeneralDetector(-1,8,1);
        PkltConfig configKlt = new PkltConfig(3,new int[]{1,2,4,8});
 
        PointTracker<ImageFloat32> tracker = FactoryPointTracker.klt(configKlt,configDetector,ImageFloat32.class,null);
 
        // Open a webcam at a resolution close to 640x480
        Webcam webcam = UtilWebcamCapture.openDefault(640,480);
 
        // Create the panel used to display the image and
        ImagePanel gui = new ImagePanel();
        gui.setPreferredSize(webcam.getViewSize());
 
        ShowImages.showWindow(gui,"KLT Tracker");
 
        int minimumTracks = 100;
        while( true ) {
            BufferedImage image = webcam.getImage();
            ImageFloat32 gray = ConvertBufferedImage.convertFrom(image,(ImageFloat32)null);
 
            tracker.process(gray);
 
            List<PointTrack> tracks = tracker.getActiveTracks(null);
 
            // Spawn tracks if there are too few
            if( tracks.size() < minimumTracks ) {
                tracker.spawnTracks();
                tracks = tracker.getActiveTracks(null);
                minimumTracks = tracks.size()/2;
            }
 
            // Draw the tracks
            Graphics2D g2 = image.createGraphics();
 
         // draw active tracks as blue dots
            for( PointTrack p : tracker.getActiveTracks(null) ) {
                VisualizeFeatures.drawPoint(g2, (int)p.x, (int)p.y, Color.blue);
            }
     
            // draw tracks which have just been spawned green
            for( PointTrack p : tracker.getNewTracks(null) ) {
                VisualizeFeatures.drawPoint(g2, (int)p.x, (int)p.y, Color.green);
            }
 
            gui.setBufferedImageSafe(image);
        }
    }

}
