package fr.peltier.nicolas.robot.ai.test.reconnaissance;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openimaj.feature.DoubleFV;
import org.openimaj.feature.DoubleFVComparison;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.CLMDetectedFace;
import org.openimaj.image.processing.face.detection.CLMFaceDetector;
import org.openimaj.image.processing.face.feature.CLMShapeFeature;
import org.openimaj.image.processing.face.feature.comparison.FaceFVComparator;
import org.openimaj.image.processing.face.feature.comparison.FacialFeatureComparator;
import org.openimaj.image.processing.face.recognition.AnnotatorFaceRecogniser;
import org.openimaj.io.IOUtils;
import org.openimaj.ml.annotation.AnnotatedObject;
import org.openimaj.ml.annotation.ScoredAnnotation;
import org.openimaj.ml.annotation.basic.KNNAnnotator;

/**
 * Cohn-Kanade database
 * @author Nicolas Peltier (nico.peltier@gmail.com)
 */
public class ReconnaissanceEmotion2 {

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
    
    private static final int[] compteurs = new int[8];

    public static void main(String[] args) {
        moteurReconnaissanceFisher();
        reconnaissanceFisher();

    }

    public static void entrainerLFW() {

    }

    public static void moteurReconnaissanceFisher() {
        try {
            AnnotatorFaceRecogniser<CLMDetectedFace, String> faceRecogniser;
            final CLMFaceDetector detector = new CLMFaceDetector();
            File faceEngineFile = new File("/home/npeltier/Robot/reconnaissance-faciale/emotion.sav");

            // Récupération ou création du moteur de reconnaissance
            if (!faceEngineFile.exists()) {
                
                final CLMShapeFeature.Extractor extractor = new CLMShapeFeature.Extractor();
                final FacialFeatureComparator<CLMShapeFeature> comparator = new FaceFVComparator<CLMShapeFeature, DoubleFV>(DoubleFVComparison.EUCLIDEAN);

                final KNNAnnotator<CLMDetectedFace, String, CLMShapeFeature> knn = KNNAnnotator.create(extractor, comparator, 1, 10f);

                faceRecogniser = AnnotatorFaceRecogniser.create(knn);
            } else {
                faceRecogniser = IOUtils.read(faceEngineFile);
            }
            
            for (int i = 0; i < 8; i++) {
                compteurs[i] = 0;
            }

            File dossierInfos = new File(dossierBaseImages + File.separator + "Emotion");
            // Une personne par dossier
            File[] listeDossiersPersonnes = dossierInfos.listFiles();
            if (listeDossiersPersonnes != null) {
                for (File dossierPersonne : listeDossiersPersonnes) {
                    if (dossierPersonne.isDirectory()) {
                        // Plusieurs images par personne
                        int[] compteursPersonne = new int[8];
                        for (int i = 0; i < 8; i++) {
                            compteursPersonne[i] = 0;
                        }
                        
                        File[] listeDossiersImages = dossierPersonne.listFiles();
                        if (listeDossiersImages != null) {
                            for (File dossierImage : listeDossiersImages) {
                                if (dossierImage.isDirectory()) {
                                    // Un fichier par dossier
                                    File[] listeFichiersEmotion = dossierImage.listFiles();
                                    if (listeFichiersEmotion != null && listeFichiersEmotion.length == 1) {
                                        // Lecture du fichier
                                        int indexEmotion = Double.valueOf(FileUtils.readFileToString(listeFichiersEmotion[0]).trim()).intValue();
                                        if ((indexEmotion == 5 || indexEmotion == 6|| indexEmotion == 7) /*&& compteurs[indexEmotion] < 40 && compteursPersonne[indexEmotion] < 2*/) {
                                            // Traitement de l'image associée
                                            String nomFichierEmotion = listeFichiersEmotion[0].getName();
                                            String suffixeCheminImage = dossierPersonne.getName() + File.separator + dossierImage.getName() + File.separator + nomFichierEmotion.replace("_emotion.txt", ".png");
                                            File fichierImage = new File(dossierBaseImages + File.separator + "cohn-kanade-images" + File.separator + suffixeCheminImage);
                                            if (fichierImage.exists()) {
                                                System.out.println(fichierImage.getAbsolutePath() + " --> " + emotions[indexEmotion]);
                                                FImage fimage = ImageUtilities.readF(fichierImage);
                                                final List<CLMDetectedFace> faces = detector.detectFaces(fimage);
                                                if (faces.size() == 1) {
                                                    faceRecogniser.train(new AnnotatedObject<CLMDetectedFace, String>(faces.get(0), emotions[indexEmotion]));
                                                    compteurs[indexEmotion]++;
                                                    compteursPersonne[indexEmotion]++;
                                                } else {
                                                    System.out.println("Wrong number of faces found : " + faces.size());
                                                }
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
            faceEngineFile.delete();
            IOUtils.writeBinaryFull(faceEngineFile, faceRecogniser);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void reconnaissanceFisher() {
        try {
            // Lancement du moteur de reconnaissance
            File faceEngineFile = new File("/home/npeltier/Robot/reconnaissance-faciale/emotion.sav");
            AnnotatorFaceRecogniser<CLMDetectedFace, String> faceRecogniser = IOUtils.read(faceEngineFile);
            
            final CLMFaceDetector detector = new CLMFaceDetector();

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
            listeUrls.add(new URL("http://www.gizmodo.fr/wp-content/uploads/2013/05/shutterstock_78268930.jpg"));
            
            System.out.println("Face Recogniser = " + faceRecogniser.listPeople());

            for (URL urlImage : listeUrls) {
                FImage image = null;
                try {
                    image = ImageUtilities.readF(urlImage);
                } catch (Exception e) {
                    System.out.println("Erreur image = " + urlImage);
                    e.printStackTrace();
                }
                
                if (image != null && faceRecogniser != null && faceRecogniser.listPeople().size() >= 1) {
                    for (final CLMDetectedFace f : detector.detectFaces(image)) {
                        final List<ScoredAnnotation<String>> name = faceRecogniser.annotate(f);

                        if (name.size() > 0) {
                            for (ScoredAnnotation<String> score : name) {
                                DisplayUtilities.display(f.getFacePatch(), score.annotation);
                            }
                        } else {
                            DisplayUtilities.display(f.getFacePatch(), "RIEN");
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
