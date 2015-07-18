package fr.peltier.nicolas.robot.ai.test.boofcv;

import boofcv.abst.fiducial.FiducialDetector;
import boofcv.factory.fiducial.ConfigFiducialBinary;
import boofcv.factory.fiducial.FactoryFiducial;
import boofcv.gui.fiducial.VisualizeFiducial;
import boofcv.gui.image.ImagePanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.UtilIO;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.webcamcapture.UtilWebcamCapture;
import boofcv.struct.calib.IntrinsicParameters;
import boofcv.struct.image.ImageFloat32;
import com.github.sarxos.webcam.Webcam;
import georegression.metric.UtilAngle;
import georegression.struct.se.Se3_F64;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Actively tracks and displays found fiducials live in a video stream from a webcam.
 *
 * @author Peter Abeles
 */
public class TrackFiducialWebcam {
    public static void main(String[] args) {
        
        System.out.println("Webcam = " + Webcam.getWebcams().get(0));

        String nameIntrinsic = null;
        int cameraId = 0;

        if (args.length >= 1) {
            cameraId = Integer.parseInt(args[0]);
        }
        if (args.length >= 2) {
            nameIntrinsic = args[1];
        } else {
            System.out.println();
            System.out.println("SERIOUSLY YOU NEED TO CALIBRATE THE CAMERA YOURSELF!");
            System.out.println("There will be a lot more jitter and inaccurate pose");
            System.out.println();
        }

        System.out.println();
        System.out.println("camera ID = "+cameraId);
        System.out.println("intrinsic file = " + nameIntrinsic);
        System.out.println();

        Webcam webcam = Webcam.getWebcams().get(cameraId);
        UtilWebcamCapture.adjustResolution(webcam, 640, 480);
        webcam.open();

        // Load intrinsic camera parameters for the camera
        // SERIOUSLY, YOU NEED TO CALIBRATE YOUR CAMERA AND USE THE FILE YOU GENERATE
        IntrinsicParameters param;

        // just make up some reasonable parameters for a webcam and assume no lens distortion
        if (nameIntrinsic == null) {
            param = new IntrinsicParameters();
            Dimension d = webcam.getDevice().getResolution();
            param.width = d.width; param.height = d.height;
            param.cx = d.width/2;
            param.cy = d.height/2;
            param.fx = param.cx/Math.tan(UtilAngle.degreeToRadian(30)); // assume 60 degree FOV
            param.fy = param.cx/Math.tan(UtilAngle.degreeToRadian(30));
            param.flipY = false;
        } else {
            param = UtilIO.loadXML(nameIntrinsic);
        }


        // Detect the fiducial
        FiducialDetector<ImageFloat32> detector = FactoryFiducial.
                squareBinaryRobust(new ConfigFiducialBinary(0.1), 6, ImageFloat32.class);
//              calibChessboard(new ConfigChessboard(5,7), 0.03, ImageFloat32.class);
//              calibSquareGrid(new ConfigSquareGrid(5,7), 0.03, ImageFloat32.class);

        detector.setIntrinsic(param);

        ImageFloat32 gray = new ImageFloat32(640,480);
        ImagePanel gui = new ImagePanel(640,480);
        ShowImages.showWindow(gui,"Fiducials") ;

        while( true ) {
            BufferedImage frame = webcam.getImage();

            ConvertBufferedImage.convertFrom(frame,gray);

            detector.detect(gray);

            // display the results
            Graphics2D g2 = frame.createGraphics();
            Se3_F64 targetToSensor = new Se3_F64();
            for (int i = 0; i < detector.totalFound(); i++) {
                detector.getFiducialToWorld(i, targetToSensor);

                VisualizeFiducial.drawCube(targetToSensor, param, 0.1, g2);
            }

            gui.setBufferedImageSafe(frame);
            gui.repaint();
        }
    }
}
