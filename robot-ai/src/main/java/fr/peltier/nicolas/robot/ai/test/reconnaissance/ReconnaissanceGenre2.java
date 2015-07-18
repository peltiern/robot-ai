package fr.peltier.nicolas.robot.ai.test.reconnaissance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.openimaj.feature.DoubleFV;
import org.openimaj.feature.DoubleFVComparison;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.alignment.RotateScaleAligner;
import org.openimaj.image.processing.face.detection.CLMDetectedFace;
import org.openimaj.image.processing.face.detection.CLMFaceDetector;
import org.openimaj.image.processing.face.detection.FaceDetector;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processing.face.detection.keypoints.FKEFaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.KEDetectedFace;
import org.openimaj.image.processing.face.feature.CLMShapeFeature;
import org.openimaj.image.processing.face.feature.comparison.FaceFVComparator;
import org.openimaj.image.processing.face.feature.comparison.FacialFeatureComparator;
import org.openimaj.image.processing.face.recognition.AnnotatorFaceRecogniser;
import org.openimaj.image.processing.face.recognition.FaceRecognitionEngine;
import org.openimaj.image.processing.face.recognition.FisherFaceRecogniser;
import org.openimaj.io.IOUtils;
import org.openimaj.ml.annotation.AnnotatedObject;
import org.openimaj.ml.annotation.ScoredAnnotation;
import org.openimaj.ml.annotation.basic.KNNAnnotator;
import org.openimaj.util.pair.IndependentPair;

/**
 * @author Nicolas Peltier (nico.peltier@gmail.com)
 */
public class ReconnaissanceGenre2 {
    
    public static void main(String[] args) {
        moteurReconnaissanceFisherCSV();
//        moteurReconnaissanceFisher();
        reconnaissanceFisher();
    }
    
    public static void entrainerLFW() {
        
    }
    
    public static void moteurReconnaissanceFisher() {
        try {
            FaceRecognitionEngine<KEDetectedFace, String> faceEngine = null;
            File faceEngineFile = new File("/home/npeltier/Robot/test/reconnaissance_genre/genre.sav");

            // Récupération ou création du moteur de reconnaissance
            if (!faceEngineFile.exists()) {
                FaceDetector<KEDetectedFace, FImage> faceDetector = new FKEFaceDetector(new HaarCascadeDetector());
                FisherFaceRecogniser<KEDetectedFace, String> faceRecognizer = FisherFaceRecogniser.create(20, new RotateScaleAligner(), 1, DoubleFVComparison.EUCLIDEAN, 0.9f);
                faceEngine = FaceRecognitionEngine.create(faceDetector, faceRecognizer);
            } else {
                faceEngine = FaceRecognitionEngine.load(faceEngineFile);
            }

            File dossierBase = new File("/home/npeltier/Robot/test/reconnaissance_genre");
            // Une personne par dossier
            File[] listeDossiersPersonnes = dossierBase.listFiles();
            if (listeDossiersPersonnes != null) {
                for (File dossierPersonne : listeDossiersPersonnes) {
                    if (dossierPersonne.isDirectory()) {
                        String genre = dossierPersonne.getName();
                        System.out.println(genre);
                        File[] listeImages = dossierPersonne.listFiles();
                        if (listeImages != null) {
                            for (File image : listeImages) {
                                if (image.isFile() && !image.isHidden()) {
                                    // Transformation de l'image en FImage
                                    FImage fimage = ImageUtilities.readF(image);
                                    KEDetectedFace visage = faceEngine.train(genre, fimage);
                                    if (visage != null) {
                                        System.out.println("Visage = " +visage);
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
    
    /** Lecture du CSV. */
    public static void moteurReconnaissanceFisherCSV() {


        try {
            AnnotatorFaceRecogniser<CLMDetectedFace, String> faceRecogniser;
            final CLMFaceDetector detector = new CLMFaceDetector();
            File faceEngineFile = new File("/home/npeltier/Robot/test/reconnaissance_genre/genre2.sav");

            // Récupération ou création du moteur de reconnaissance
            if (!faceEngineFile.exists()) {
                
                final CLMShapeFeature.Extractor extractor = new CLMShapeFeature.Extractor();
                final FacialFeatureComparator<CLMShapeFeature> comparator = new FaceFVComparator<CLMShapeFeature, DoubleFV>(DoubleFVComparison.EUCLIDEAN);

                final KNNAnnotator<CLMDetectedFace, String, CLMShapeFeature> knn = KNNAnnotator.create(extractor, comparator, 1, 10f);

                faceRecogniser = AnnotatorFaceRecogniser.create(knn);
            } else {
                faceRecogniser = IOUtils.read(faceEngineFile);
            }
            
            // Récupération du fichier CSV
            Reader in = new FileReader("/home/npeltier/Robot/reconnaissance/lfw_attributes.txt");
            CSVFormat csvFormat = CSVFormat.newFormat('\t');
            Iterable<CSVRecord> records = csvFormat.parse(in);
            int i = 0;
            for (CSVRecord record : records) {
                String nom = record.get(0);
                if (!nom.startsWith("#")) {
                    String numero = record.get(1);
                    numero = StringUtils.leftPad(numero, 4, '0');
                    String genre = record.get(2);
                    System.out.println(nom + "\t" + numero + "\t" + genre);
                    double valeurGenre = Double.valueOf(genre);
                    
                    if (valeurGenre < -1 || valeurGenre > 1) {
                        String nomGenre  = valeurGenre > 0 ? "H" : "F";

                        // Construction du nom du dossier et du fichier
                        String nomDossierPersonne = nom.replaceAll(" ", "_");
                        String nomFichierPersonne = nomDossierPersonne + "_" + numero + ".jpg";

                        File fichierImage = new File("/home/npeltier/Robot/reconnaissance/lfw-deepfunneled/" + nomDossierPersonne + "/" + nomFichierPersonne);
                        if (fichierImage.exists()) {
                            if (fichierImage.isFile() && !fichierImage.isHidden()) {
                                FImage fimage = ImageUtilities.readF(fichierImage);
                                final List<CLMDetectedFace> faces = detector.detectFaces(fimage);
                                if (faces.size() == 1) {
                                    faceRecogniser.train(new AnnotatedObject<CLMDetectedFace, String>(faces.get(0), nomGenre));
                                    System.out.println(nom + " ==> " + nomGenre);
                                } else {
                                    System.out.println("Wrong number of faces found : " + faces.size());
                                }
                            }
                        } else {
                            System.err.println("ERREUR : fichier " + fichierImage.getPath() + " inexistant");
                        }
                    }

                }
                i++;
                if (i % 100 == 0) {
                    // Sauvegarde du moteur de reconnaissance
                    IOUtils.writeBinaryFull(faceEngineFile, faceRecogniser);
                }
                if (i > 2000) {
                    break;
                }
                
            }
            // Sauvegarde du moteur de reconnaissance
            IOUtils.writeBinaryFull(faceEngineFile, faceRecogniser);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void reconnaissanceFisher() {
        try {
            // Lancement du moteur de reconnaissance
            File faceEngineFile = new File("/home/npeltier/Robot/test/reconnaissance_genre/genre2.sav");
            AnnotatorFaceRecogniser<CLMDetectedFace, String> faceRecogniser = IOUtils.read(faceEngineFile);
            
            final CLMFaceDetector detector = new CLMFaceDetector();
            
            File dossierTest = new File("/home/npeltier/Robot/test/test");

            File[] listeImages = dossierTest.listFiles();
            if (listeImages != null) {
                for (File image : listeImages) {
                    if (image.isFile() && !image.isHidden()) {
                        System.out.println("Image : " + image.getPath());
                        FImage fimage = null;
                        try {
                            fimage = ImageUtilities.readF(image);
                        } catch (Exception e) {
                            System.out.println("Erreur image = " + image);
                            e.printStackTrace();
                        }
                        
                        if (fimage != null && faceRecogniser != null && faceRecogniser.listPeople().size() >= 1) {
                            for (final CLMDetectedFace f : detector.detectFaces(fimage)) {
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
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
