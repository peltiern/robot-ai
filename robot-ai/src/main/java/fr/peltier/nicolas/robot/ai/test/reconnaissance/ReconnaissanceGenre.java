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
 * @author Nicolas Peltier (nico.peltier@gmail.com)
 */
public class ReconnaissanceGenre {
    
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
            File faceEngineFile = new File("/home/npeltier/Robot/test/reconnaissance_genre/genre.sav");
            // Création du moteur de reconnaissance
            if (faceEngineFile.exists()) {
                faceEngineFile.delete();
            }
            FaceDetector<KEDetectedFace, FImage> faceDetector = new FKEFaceDetector(new HaarCascadeDetector());
            FisherFaceRecogniser<KEDetectedFace, String> faceRecognizer = FisherFaceRecogniser.create(20, new RotateScaleAligner(), 1, DoubleFVComparison.EUCLIDEAN, 0.9f);
            FaceRecognitionEngine<KEDetectedFace, String> faceEngine = FaceRecognitionEngine.create(faceDetector, faceRecognizer);

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
                                // Transformation de l'image en FImage
                                FImage fimage = ImageUtilities.readF(fichierImage);
                                KEDetectedFace visage = faceEngine.train(nomGenre, fimage);
                                if (visage != null) {
                                    System.out.println(nom + " ==> " + nomGenre);
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
                    faceEngine.save(faceEngineFile);
                }
                if (i > 2000) {
                    break;
                }
                
            }
            // Sauvegarde du moteur de reconnaissance
            faceEngine.save(faceEngineFile);
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
            File faceEngineFile = new File("/home/npeltier/Robot/test/reconnaissance_genre/genre.sav");
            FaceRecognitionEngine<KEDetectedFace, String> faceEngine = FaceRecognitionEngine.load(faceEngineFile);
            
            File dossierTest = new File("/home/npeltier/Robot/test/test");

            File[] listeImages = dossierTest.listFiles();
            if (listeImages != null) {
                for (File image : listeImages) {
                    if (image.isFile() && !image.isHidden()) {
                        System.out.println("Image : " + image.getPath());
                        List<IndependentPair<KEDetectedFace,ScoredAnnotation<String>>> listeResultatsPersonne = faceEngine.recogniseBest(ImageUtilities.readF(image));
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
                                DisplayUtilities.display(resultat.getFirstObject().getFacePatch());
                            }
                            if (visageLePlusGrand != null) {
                                if (visageLePlusGrand.secondObject() != null) {
                                    System.out.println(image.getName() + " : " + visageLePlusGrand.getSecondObject().annotation + ", score : " + visageLePlusGrand.getSecondObject().confidence);
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
