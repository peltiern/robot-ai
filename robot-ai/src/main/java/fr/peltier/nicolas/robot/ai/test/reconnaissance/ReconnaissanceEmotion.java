package fr.peltier.nicolas.robot.ai.test.reconnaissance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.openimaj.feature.DoubleFVComparison;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.alignment.RotateScaleAligner;
import org.openimaj.image.processing.face.detection.FaceDetector;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processing.face.detection.keypoints.FKEFaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.KEDetectedFace;
import org.openimaj.image.processing.face.recognition.FaceRecognitionEngine;
import org.openimaj.image.processing.face.recognition.FisherFaceRecogniser;
import org.openimaj.ml.annotation.ScoredAnnotation;
import org.openimaj.util.pair.IndependentPair;

/**
 * Cohn-Kanade database
 * @author Nicolas Peltier (nico.peltier@gmail.com)
 */
public class ReconnaissanceEmotion {

    private static final String dossierBaseImages = "/home/npeltier/Robot/reconnaissance-faciale/CK+";

    private static final String NEUTRE = "NEUTRE";
    private static final String COLERE = "COLERE";
    private static final String MEPRIS = "MEPRIS";
    private static final String DEGOUT = "DEGOUT";
    private static final String PEUR = "PEUR";
    private static final String JOIE = "JOIE";
    private static final String TRISTESSE = "TRISTESSE";
    private static final String SURPRISE = "SURPRISE";

    private static final String[] emotions = {NEUTRE, COLERE, MEPRIS, DEGOUT, PEUR, JOIE, TRISTESSE, SURPRISE};

    public static void main(String[] args) {
        moteurReconnaissanceFisher();
        reconnaissanceFisher();

    }

    public static void entrainerLFW() {

    }

    public static void moteurReconnaissanceFisher() {
        try {
            FaceRecognitionEngine<KEDetectedFace, String> faceEngine = null;
            File faceEngineFile = new File("/home/npeltier/Robot/reconnaissance-faciale/emotion.sav");

            // Récupération ou création du moteur de reconnaissance
            if (!faceEngineFile.exists()) {
                FaceDetector<KEDetectedFace, FImage> faceDetector = new FKEFaceDetector(new HaarCascadeDetector());
                FisherFaceRecogniser<KEDetectedFace, String> faceRecognizer = FisherFaceRecogniser.create(20, new RotateScaleAligner(), 1, DoubleFVComparison.EUCLIDEAN, 0.9f);
                faceEngine = FaceRecognitionEngine.create(faceDetector, faceRecognizer);
            } else {
                faceEngine = FaceRecognitionEngine.load(faceEngineFile);
            }

            File dossierInfos = new File(dossierBaseImages + File.separator + "Emotion");
            // Une personne par dossier
            File[] listeDossiersPersonnes = dossierInfos.listFiles();
            if (listeDossiersPersonnes != null) {
                for (File dossierPersonne : listeDossiersPersonnes) {
                    if (dossierPersonne.isDirectory()) {
                        // Plusieurs images par personne
                        File[] listeDossiersImages = dossierPersonne.listFiles();
                        if (listeDossiersImages != null) {
                            for (File dossierImage : listeDossiersImages) {
                                if (dossierImage.isDirectory()) {
                                    // Un fichier par dossier
                                    File[] listeFichiersEmotion = dossierImage.listFiles();
                                    if (listeFichiersEmotion != null && listeFichiersEmotion.length == 1) {
                                        // Lecture du fichier
                                        int indexEmotion = Double.valueOf(FileUtils.readFileToString(listeFichiersEmotion[0]).trim()).intValue();
                                        if (indexEmotion == 5 || indexEmotion == 6) {
                                            // Traitement de l'image associée
                                            String nomFichierEmotion = listeFichiersEmotion[0].getName();
                                            String suffixeCheminImage = dossierPersonne.getName() + File.separator + dossierImage.getName() + File.separator + nomFichierEmotion.replace("_emotion.txt", ".png");
                                            File fichierImage = new File(dossierBaseImages + File.separator + "cohn-kanade-images" + File.separator + suffixeCheminImage);
                                            if (fichierImage.exists()) {
                                                System.out.println(fichierImage.getAbsolutePath() + " --> " + emotions[indexEmotion]);
                                                FImage fimage = ImageUtilities.readF(fichierImage);
                                                faceEngine.train(emotions[indexEmotion], fimage);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Sauvegarde du moteur de reconnaissance
            faceEngine.save(faceEngineFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void reconnaissanceFisher() {
        try {
            // Lancement du moteur de reconnaissance
            File faceEngineFile = new File("/home/npeltier/Robot/reconnaissance-facialeemotion.sav");
            FaceRecognitionEngine<KEDetectedFace, String> faceEngine = FaceRecognitionEngine.load(faceEngineFile);

            final List<URL> listeUrls = new ArrayList<URL>();
            // Joie
            listeUrls.add(new URL("http://static1.puretrend.com/articles/1/51/87/1/@/518482-le-sourire-gnan-gnan-la-vie-est-637x0-2.jpg"));
            listeUrls.add(new URL("http://static1.purepeople.com/articles/3/30/28/3/@/207384-anne-hathaway-et-son-joli-sourire-a-637x0-3.jpg"));
            listeUrls.add(new URL("http://mingyue.e-monsite.com/medias/images/tout-sourire-james-franco-illumine-palm-springs-74768-w460.jpg"));
            listeUrls.add(new URL("http://www.canalcentral.fr/wp-content/uploads/2012/01/Tai-chi-du-sourire-avec-Dominique-Tinjust.jpg"));
            // Tristesse
            listeUrls.add(new URL("http://www.never-be-lied.com/inc/upload/img/07062013152712_CIMG0011.png"));
            listeUrls.add(new URL("http://apprendrelementalismedotcom.files.wordpress.com/2011/11/sadeface049.jpg"));
            listeUrls.add(new URL("http://mentalismeblog.files.wordpress.com/2012/06/homme-triste-thumb5942247.jpg"));
            listeUrls.add(new URL("http://static1.purepeople.com/articles/5/54/87/5/@/402122-comment-reconnaitre-la-tristesse-637x0-2.jpg"));

            for (URL urlImage : listeUrls) {
                List<IndependentPair<KEDetectedFace,ScoredAnnotation<String>>> listeResultatsPersonne = faceEngine.recogniseBest(ImageUtilities.readF(urlImage));
                if (listeResultatsPersonne != null && ! listeResultatsPersonne.isEmpty()) {
                    // Recherche du visage le plus grand
                    double aireVisagePlusGrand = 0d;
                    IndependentPair<KEDetectedFace,ScoredAnnotation<String>> visageLePlusGrand = null;
                    for (IndependentPair<KEDetectedFace,ScoredAnnotation<String>> resultat : listeResultatsPersonne) {
                        double aireVisage = resultat.getFirstObject().getBounds().calculateArea();
                        if (aireVisage > aireVisagePlusGrand) {
                            aireVisagePlusGrand = aireVisage;
                            visageLePlusGrand = resultat;
                        }
                    }
                    if (visageLePlusGrand != null) {
                        DisplayUtilities.display(visageLePlusGrand.getFirstObject().getFacePatch());
                        if (visageLePlusGrand.secondObject() != null) {
                            System.out.println(visageLePlusGrand.getSecondObject().annotation + ", score : " + visageLePlusGrand.getSecondObject().confidence);
                        }
                    }
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
