/*
 * Copyright 1999-2004 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 */

package fr.peltier.nicolas.robot.ai;

import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.effects.DioramaEffect;
import org.openimaj.image.processor.ProcessorUtilities;
import org.openimaj.math.geometry.line.Line2d;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.capture.VideoCapture;
import org.openimaj.video.capture.VideoCaptureException;
import org.openimaj.video.processor.VideoFrameProcessor;

import fr.peltier.nicolas.si.vox.son.LecteurTexte;


/**
 * A simple HelloWorld demo showing a simple speech application built using Sphinx-4. This application uses the Sphinx-4
 * endpointer, which automatically segments incoming audio into utterances and silences.
 */
public class HelloWorld {
    
    private static LecteurTexte lecteur;

//    public static void main(String[] args) {
//        
////        System.out.println("USER DIR = " + System.getProperty("user.dir"));
////        try {
////            new VideoFaceRecognitionWithVocalSynthesis();
////        } catch (Exception e) {
////            // TODO Auto-generated catch block
////            e.printStackTrace();
////        }
//        
//        ConfigurationManager cm;
//
//        if (args.length > 0) {
//            cm = new ConfigurationManager(args[0]);
//        } else {
//            cm = new ConfigurationManager(System.getProperty("user.dir") + File.separator + "reconnaissance-vocale" + File.separator + "helloworld.config.fr.xml");
//        }
//
//        Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
//        recognizer.allocate();
//
//        // start the microphone or exit if the programm if this is not possible
//        Microphone microphone = (Microphone) cm.lookup("microphone");
//        if (!microphone.startRecording()) {
//            System.out.println("Cannot start microphone.");
//            recognizer.deallocate();
//            System.exit(1);
//        }
//        
//        // Initialisation du lecteur
//        ConfigurationSyntheseVocale.getInstance().setDossierRacine(System.getProperty("user.dir") + File.separator + "synthese-vocale");
//        lecteur = new LecteurTexte();
//        
//      SimpleDateFormat sdfHeure = new SimpleDateFormat("HH 'heure' mm");
//      SimpleDateFormat sdfJour = new SimpleDateFormat("dd MMMMMM yyyy");
//        
//
//        speak("Vous pouvez parler.");
//
//        Result result = null;
//        
//        // loop the recognition until the programm exits.
//        while (true) {
//            if(microphone.isRecording()) {
//                result = recognizer.recognize();
//            }else{
//                result = null;
//                microphone.clear();
//                microphone.startRecording();
//            }
//
//            if (result != null) {
//                String resultText = result.getBestFinalResultNoFiller();
//
//                if (resultText != null && !resultText.trim().equals("")) {
//                    System.out.println("HUMAIN : " + resultText);
//                    if (resultText.trim().equalsIgnoreCase("Bonjour Sami")) {
//                        microphone.stopRecording();
//                        speak("Bonjour Nicolas");
//                    } else if (resultText.trim().equalsIgnoreCase("Tu es gentil")) {
//                        microphone.stopRecording();
//                        speak("Merci, toi aussi.");
//                    }  else if (resultText.trim().equalsIgnoreCase("Comment vas tu")) {
//                        microphone.stopRecording();
//                        speak("Bien. Merci. Et toi?");
//                    } else if (resultText.trim().equalsIgnoreCase("Je vais bien")) {
//                        microphone.stopRecording();
//                        speak("Quelle belle journée.");
//                    } else if (resultText.trim().equalsIgnoreCase("Quel age as tu")) {
//                        microphone.stopRecording();
//                        speak("J'ai une journée. Et toi ?");
//                    } else if (resultText.trim().equalsIgnoreCase("Comment tu t appelle") || resultText.trim().equalsIgnoreCase("Comment t appelle tu") || resultText.trim().equalsIgnoreCase("Quel est ton nom")) {
//                        microphone.stopRecording();
//                        speak("Je m'appelle Sami. Et toi ?");
//                    } else if (resultText.trim().equalsIgnoreCase("Quelle heure il est") || resultText.trim().equalsIgnoreCase("il est quelle heure")) {
//                        microphone.stopRecording();
//                        speak("Il est " + sdfHeure.format(Calendar.getInstance().getTime()));
//                    } else if (resultText.trim().equalsIgnoreCase("Quel jour on est")) {
//                        microphone.stopRecording();
//                        speak("On est le " + sdfJour.format(Calendar.getInstance().getTime()));
//                    } else if (resultText.trim().equalsIgnoreCase("Ou tu habite") || resultText.trim().equalsIgnoreCase("Ou habite tu")) {
//                        microphone.stopRecording();
//                        speak("J'habites dans un ordinateur. Et toi?");
//                    } else if (resultText.trim().equalsIgnoreCase("Carquefou") || resultText.trim().equalsIgnoreCase("Paris") || resultText.trim().equalsIgnoreCase("Angoulême")) {
//                        microphone.stopRecording();
//                        speak("Tu habites " + resultText + ".");
//                    } else if (resultText.trim().equalsIgnoreCase("Au revoir Sami")) {
//                        microphone.stopRecording();
//                        speak("Au revoir Nicolas. A bientôt.");
//                        System.exit(1);
//                    } else if (resultText.trim().equalsIgnoreCase("Stopper")) {
//                        microphone.stopRecording();
//                        speak("Au revoir.");
//                        System.exit(1);
//                    } else {
//                        microphone.stopRecording();
//                        speak("Tu dis : " + resultText + ".");
//                    }
//                    
//
//                } else {
//                    System.out.println("TEXTE = <" + resultText + ">");
//                    System.out.println("Je ne comprends pas.");
//                }
//
//            }
//        }
//    }
//
//    private static void speak(String text) {
//        System.out.println("ORDI : " + text);
//        lecteur.setTexte(text);
//        lecteur.playAll();
////        System.out.println(text);
//    }
    
    public static void main(String[] args) throws VideoCaptureException {
        final int w = 640;
        final int h = 480;
        final Line2d axis = new Line2d(w / 2, h / 2, w / 2, h);

        VideoDisplay.createVideoDisplay(new VideoFrameProcessor<MBFImage>(new VideoCapture(w, h),
                ProcessorUtilities.wrap(new DioramaEffect(axis))));
    }
}
