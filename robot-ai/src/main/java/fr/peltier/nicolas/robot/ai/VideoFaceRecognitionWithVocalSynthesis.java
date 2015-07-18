package fr.peltier.nicolas.robot.ai;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openimaj.image.FImage;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.face.detection.CLMDetectedFace;
import org.openimaj.image.processing.face.feature.LocalLBPHistogram;
import org.openimaj.image.processing.face.recognition.AnnotatorFaceRecogniser;
import org.openimaj.image.processing.face.recognition.FaceRecognitionEngine;
import org.openimaj.image.processing.face.tracking.clm.CLMFaceTracker;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.math.geometry.point.Point2d;
import org.openimaj.ml.annotation.ScoredAnnotation;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.VideoDisplayListener;
import org.openimaj.video.capture.VideoCapture;

import fr.peltier.nicolas.si.vox.son.LecteurTexte;

public class VideoFaceRecognitionWithVocalSynthesis implements VideoDisplayListener<MBFImage> {
    private VideoCapture capture;
    private VideoDisplay<MBFImage> videoFrame;
    private AnnotatorFaceRecogniser<CLMDetectedFace, ?, String> recogniser;
    private CLMFaceTracker engine;
    private FImage currentFrame;
    private Map<String, Calendar> listePersonnesReconnues = new HashMap<>();
    public VideoFaceRecognitionWithVocalSynthesis() throws Exception {
        capture = new VideoCapture(640, 480);
        engine = new CLMFaceTracker();
        engine.fpd = 120;
        // engine.fcheck = true;
        videoFrame = VideoDisplay.createVideoDisplay(capture);
        videoFrame.addVideoListener(this);

        
        final File annotatorFaceEngineFile = new File(System.getProperty("user.dir") + File.separator + "reconnaissance-faciale" + File.separator + "annotatorFaceEngineFile.sav");
        FaceRecognitionEngine<CLMDetectedFace, LocalLBPHistogram.Extractor<CLMDetectedFace>, String> faceRecognitionEngine = FaceRecognitionEngine.load(annotatorFaceEngineFile);
        recogniser = (AnnotatorFaceRecogniser<CLMDetectedFace, LocalLBPHistogram.Extractor<CLMDetectedFace>, String>) faceRecognitionEngine.getRecogniser();
    }
   
    private List<CLMDetectedFace> detectFaces() {
        return CLMDetectedFace.convert(engine.model.trackedFaces, currentFrame);
    }
    @Override
    public void afterUpdate(VideoDisplay<MBFImage> display) {
        // do nothing
    }
    @Override
    public synchronized void beforeUpdate(MBFImage frame) {
        this.currentFrame = frame.flatten();
        engine.track(frame);
        engine.drawModel(frame, true, true, true, true, true);
        if (recogniser != null && recogniser.listPeople().size() >= 1) {
            for (final CLMDetectedFace f : detectFaces()) {
                final List<ScoredAnnotation<String>> name = recogniser.annotate(f);
                if (name.size() > 0) {
                    final Point2d r = f.getBounds().getTopLeft();
                    frame.drawText(name.get(0).annotation, r, HersheyFont.ROMAN_SIMPLEX, 15, RGBColour.GREEN);
                    String nom = name.get(0).annotation.replace("_", " ");
                    Calendar datePersonneReconnue = listePersonnesReconnues.get(nom);
                    if (datePersonneReconnue == null) {
                        // Personne non citée
                        listePersonnesReconnues.put(nom, Calendar.getInstance());
//                        LecteurTexte lecteur = new LecteurTexte(nom);
//                        lecteur.playAll();
                    } else {
                        Calendar date = Calendar.getInstance();
                        if ((date.getTimeInMillis() - datePersonneReconnue.getTimeInMillis()) > 10000) {
                            // Personne citée il y a plus de 10 secondes ==> on recite
                            listePersonnesReconnues.put(nom, Calendar.getInstance());
//                            LecteurTexte lecteur = new LecteurTexte(nom);
//                            lecteur.playAll();
                        }
                    }
                }
            }
        }
    }
    public static void main(String[] args) throws Exception {
        new VideoFaceRecognitionWithVocalSynthesis();
    }
} 
