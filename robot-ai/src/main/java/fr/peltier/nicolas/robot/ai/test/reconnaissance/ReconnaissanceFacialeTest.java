package fr.peltier.nicolas.robot.ai.test.reconnaissance;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.ml.annotation.ScoredAnnotation;
import org.openimaj.util.pair.IndependentPair;

import fr.peltier.nicolas.robot.ai.memoire.ReconnaissanceFaciale;
import fr.peltier.nicolas.robot.ai.memoire.ReconnaissanceFacialeAnnotator;
import fr.peltier.nicolas.robot.ai.memoire.ReconnaissanceFacialeEigenface;
import fr.peltier.nicolas.robot.ai.memoire.ReconnaissanceFacialeFisherface;

public class ReconnaissanceFacialeTest {

    private ReconnaissanceFaciale reconnaissanceFaciale;
    
    private static final int NB_PERSONNES_A_TRAITER = 40;
    
    private static final int NB_IMAGES_A_TRAITER = 10;

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        System.setProperty("robot.dir", "/home/npeltier/Robot");

        ReconnaissanceFacialeTest reconnaissanceFacialeTest = new ReconnaissanceFacialeTest();
        
        reconnaissanceFacialeTest.apprendre();
        reconnaissanceFacialeTest.reconnaitre();

//        reconnaissanceFacialeTest.apprendrePersonne("Adam_Sandler");
//        reconnaissanceFacialeTest.apprendrePersonne("George_Clooney");
//        reconnaissanceFacialeTest.apprendrePersonne("George_HW_Bush");
//        reconnaissanceFacialeTest.apprendrePersonne("Nicolas_Sarkozy");
//        reconnaissanceFacialeTest.apprendrePersonne("Jennifer_Aniston");
//        reconnaissanceFacialeTest.apprendrePersonne("Jennifer_Connelly");
//        reconnaissanceFacialeTest.apprendrePersonne("Jennifer_Garner");

//        reconnaissanceFacialeTest.reconnaitrePersonne("Bush", new URL("http://i.usatoday.net/news/_photos/2012/01/28/Obama-meets-with-George-HW-and-Jeb-Bush-85TKFSQ-x-large.jpg"));
//        reconnaissanceFacialeTest.reconnaitrePersonne("Clooney", new URL("http://img3.wikia.nocookie.net/__cb20110728201144/spykids/images/c/c8/George_Clooney.jpg"));
//        reconnaissanceFacialeTest.reconnaitrePersonne("Aniston", new URL("http://wolfgangpreuss.net/Girls3/images/fullscreen/9.jpg"));
//        reconnaissanceFacialeTest.reconnaitrePersonne("Sarkozy", new URL("http://www.babelio.com/users/AVT_Nicolas-Sarkozy_6090.jpeg"));
//        reconnaissanceFacialeTest.reconnaitrePersonne("Garner", new URL("http://www.chaytoday.ca/files/2014/10/jennifer-garner.jpg"));
//        reconnaissanceFacialeTest.reconnaitrePersonne("Sandler", new URL("http://cdn.celebritycarsblog.com/wp-content/uploads/Adam-Sandler.jpg"));
//        reconnaissanceFacialeTest.reconnaitrePersonne("Sandler", new File("/home/npeltier/Robot/reconnaissance/lfw-deepfunneled/Adam_Sandler/Adam_Sandler_0001.jpg"));
//        reconnaissanceFacialeTest.reconnaitrePersonne("Bush", new URL("http://mediad.publicbroadcasting.net/p/wamc/files/201202/3444482-1533742600.jpg"));
//        reconnaissanceFacialeTest.reconnaitrePersonne("Connelly", new URL("http://imstars.aufeminin.com/stars/fan/jennifer-connelly/jennifer-connelly-20060618-138061.jpg"));

    }

    public ReconnaissanceFacialeTest() {
        reconnaissanceFaciale = new ReconnaissanceFacialeFisherface();
    }
    
    private void apprendre() throws IOException {
        int nbPersonnes = 0;
        File dossierBase = new File(System.getProperty("robot.dir") + "/reconnaissance-faciale/LFW/lfw-deepfunneled/");
        File[] listeDossiersPersonnes = dossierBase.listFiles();
        if (listeDossiersPersonnes != null) {
            for (File dossierPersonne : listeDossiersPersonnes) {
                if (dossierPersonne.isDirectory()) {
                    File[] listeImages = dossierPersonne.listFiles();
                    if (listeImages != null) {
                        if (listeImages.length > NB_IMAGES_A_TRAITER) {
                            System.out.println(nbPersonnes + " - " + dossierPersonne.getName() + " : " + listeImages.length);
                            final List<FImage> listeVisagesAApprendre = new ArrayList<FImage>();
                            for (int i = 0; i < NB_IMAGES_A_TRAITER ; i++) {
                                File image = listeImages[i];
                                if (image.isFile() && !image.isHidden()) {
                                    listeVisagesAApprendre.add(ImageUtilities.readF(image));
                                }
                            }
                            reconnaissanceFaciale.apprendreVisagesPersonne(listeVisagesAApprendre, dossierPersonne.getName());
                            nbPersonnes++;
                        }
                    }
                }
                if (nbPersonnes >= NB_PERSONNES_A_TRAITER) {
                    break;
                }
            }
        }
    }
    
    private void reconnaitre() throws MalformedURLException, IOException {
        int nbPersonnesTraitees = 0;
        int nbPersonnesReconnues = 0;
        File dossierBase = new File(System.getProperty("robot.dir") + "/reconnaissance-faciale/LFW/lfw-deepfunneled/");
        File[] listeDossiersPersonnes = dossierBase.listFiles();
        if (listeDossiersPersonnes != null) {
            for (File dossierPersonne : listeDossiersPersonnes) {
                if (dossierPersonne.isDirectory()) {
                    File[] listeImages = dossierPersonne.listFiles();
                    if (listeImages != null) {
                        if (listeImages.length > NB_IMAGES_A_TRAITER) {
                            if (reconnaitrePersonne(dossierPersonne.getName(), listeImages[NB_IMAGES_A_TRAITER])) {
                                nbPersonnesReconnues++;
                            }
                            nbPersonnesTraitees++;
                        }
                    }
                }
                if (nbPersonnesTraitees >= NB_PERSONNES_A_TRAITER) {
                    break;
                }
            }
        }
        System.out.println("Resultat : " + nbPersonnesReconnues + " / " + nbPersonnesTraitees);
    }

    private void apprendrePersonne(String nomPersonne) throws IOException {
        File dossierPersonne = new File(System.getProperty("robot.dir") + "/reconnaissance-faciale/LFW/lfw-deepfunneled/" + nomPersonne);
        File[] listeImages = dossierPersonne.listFiles();
        if (listeImages != null) {
            final List<FImage> listeVisagesAApprendre = new ArrayList<FImage>();
            for (File image : listeImages) {
                if (image.isFile() && !image.isHidden()) {
                    listeVisagesAApprendre.add(ImageUtilities.readF(image));
                }
            }
            reconnaissanceFaciale.apprendreVisagesPersonne(listeVisagesAApprendre, nomPersonne);
        }
    }


    private boolean reconnaitrePersonne(String personneAttendue, File fichier) throws MalformedURLException, IOException {
        return reconnaitrePersonne(personneAttendue, ImageUtilities.readF(fichier));
    }
    
    private boolean reconnaitrePersonne(String personneAttendue, URL url) throws MalformedURLException, IOException {
        return reconnaitrePersonne(personneAttendue, ImageUtilities.readF(url));
    }
    
    private boolean reconnaitrePersonne(String personneAttendue, FImage image) throws MalformedURLException, IOException {
        IndependentPair<DetectedFace,ScoredAnnotation<String>> resultat = reconnaissanceFaciale.reconnaitre(image);
        if (resultat != null) {
            System.out.println(personneAttendue + " --> " + resultat.getSecondObject());
        } else {
            System.out.println(personneAttendue + " --> rien");
        }
        if (resultat != null && resultat.getSecondObject() != null) {
            return personneAttendue.equalsIgnoreCase(resultat.getSecondObject().annotation);
        } else {
            return false;
        }
    }

}
