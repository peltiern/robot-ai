package fr.peltier.nicolas.robot.ai.test.boofcv;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import com.github.sarxos.webcam.Webcam;

import georegression.struct.shapes.Quadrilateral_F64;
import boofcv.abst.tracker.ConfigComaniciu2003;
import boofcv.abst.tracker.TrackerObjectQuad;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.factory.tracker.FactoryTrackerObjectQuad;
import boofcv.gui.feature.VisualizeFeatures;
import boofcv.gui.image.ShowImages;
import boofcv.gui.tracker.TrackerObjectQuadPanel;
import boofcv.io.MediaManager;
import boofcv.io.image.SimpleImageSequence;
import boofcv.io.webcamcapture.UtilWebcamCapture;
import boofcv.io.wrapper.DefaultMediaManager;
import boofcv.misc.BoofMiscOps;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageType;
import boofcv.struct.image.ImageUInt8;

public class WebcamTracking2 {

    public static void main(String[] args) {
        MediaManager media = DefaultMediaManager.INSTANCE;
        String fileName = "../data/applet/tracking/track_book.mjpeg";
 
        // Create the tracker.  Comment/Uncomment to change the tracker.  Mean-shift trackers have been omitted
        // from the list since they use color information and including color images could clutter up the example.
        TrackerObjectQuad tracker =
                FactoryTrackerObjectQuad.circulant(null, ImageUInt8.class);
//              FactoryTrackerObjectQuad.sparseFlow(null,ImageUInt8.class,null);
//              FactoryTrackerObjectQuad.tld(null,ImageUInt8.class);
//              FactoryTrackerObjectQuad.meanShiftComaniciu2003(new ConfigComaniciu2003(), ImageType<T>.ms(3,ImageUInt8.class));
//              FactoryTrackerObjectQuad.meanShiftComaniciu2003(new ConfigComaniciu2003(true),ImageType.ms(3,ImageUInt8.class));
 
                // Mean-shift likelihood will fail in this video, but is excellent at tracking objects with
                // a single unique color.  See ExampleTrackerMeanShiftLikelihood
//              FactoryTrackerObjectQuad.meanShiftLikelihood(30,5,255, MeanShiftLikelihoodType.HISTOGRAM,ImageType.ms(3,ImageUInt8.class));
 
     // Open a webcam at a resolution close to 640x480
        Webcam webcam = UtilWebcamCapture.openDefault(640,480);
 
        // specify the target's initial location and initialize with the first frame
        Quadrilateral_F64 location = new Quadrilateral_F64(276,159,362,163,358,292,273,289);
        BufferedImage image = webcam.getImage();
        ImageBase frame = (ImageBase) ConvertBufferedImage.convertFrom(image,(ImageUInt8)null);
        tracker.initialize(frame,location);
 
        // For displaying the results
        TrackerObjectQuadPanel gui = new TrackerObjectQuadPanel(null);
        gui.setPreferredSize(new Dimension(frame.getWidth(),frame.getHeight()));
        gui.setBackGround(image);
        gui.setTarget(location,true);
        ShowImages.showWindow(gui,"Tracking Results");
 
        // Track the object across each video frame and display the results
        while( true ) {
            image = webcam.getImage();
            frame = (ImageBase) ConvertBufferedImage.convertFrom(image,(ImageUInt8)null);
 
            boolean visible = tracker.process(frame,location);
            
            gui.setBackGround(image);
            gui.setTarget(location,visible);
            gui.repaint();
 
            BoofMiscOps.pause(20);
        }
    }

}
