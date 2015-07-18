package fr.peltier.nicolas.robot.ai.organes.actionneurs;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;
import fr.peltier.nicolas.robot.ai.Robot;
import fr.peltier.nicolas.robot.ai.organes.AbstractOrgane;
import fr.peltier.nicolas.robot.ai.systemenerveux.event.ParoleEvent;
import fr.peltier.nicolas.robot.ai.systemenerveux.event.ReconnaissanceVocaleControleEvent;
import fr.peltier.nicolas.robot.ai.systemenerveux.event.ReconnaissanceVocaleControleEvent.CONTROLE;
import fr.peltier.nicolas.robot.ai.systemenerveux.event.RobotEvent;
import fr.peltier.nicolas.si.vox.son.LecteurTexte;
import fr.peltier.nicolas.si.vox.util.ConfigurationSyntheseVocale;

/**
 * Organe de la parole permettant la synthèse vocale.
 * @author Nicolas Peltier (nico.peltier@gmail.com)
 */
public class OrganeParole extends AbstractOrgane {

    /** Lecteur de texte. */
    private LecteurTexte lecteurTexte;
    
    /** Logger. */
    private Logger logger = Logger.getLogger(OrganeParole.class);

    /** Constructeur. */
    public OrganeParole(MBassador<RobotEvent> systemeNerveux) {
        super(systemeNerveux);
        // Initialisation du lecteur
        ConfigurationSyntheseVocale.getInstance().setDossierRacine(System.getProperty("robot.dir") + File.separator + "synthese-vocale");
        lecteurTexte = new LecteurTexte();
    }

    /**
     * Lit un texte.
     * @param texte le texte à dire
     */
    public void lire(String texte) {
        if(texte != null && !texte.isEmpty()) {
            // Envoi d'un évènement pour mettre en pause la reconnaissance vocale
            final ReconnaissanceVocaleControleEvent eventPause = new ReconnaissanceVocaleControleEvent();
            eventPause.setControle(CONTROLE.METTRE_EN_PAUSE);
            systemeNerveux.publish(eventPause);

            logger.debug("Lecture :\t" + texte);
            // Lecture du texte
            lecteurTexte.setTexte(texte);
            lecteurTexte.playAll();

            logger.debug("Fin lecture :\t" + texte);

            // Envoi d'un évènement pour redémarrer la reconnaissance vocale
            final ReconnaissanceVocaleControleEvent eventRedemarrage = new ReconnaissanceVocaleControleEvent();
            eventRedemarrage.setControle(CONTROLE.DEMARRER);
            systemeNerveux.publish(eventRedemarrage);
        }
    }
    
    /**
     * Intercepte les évènements pour lire du texte.
     * @param paroleEvent évènement pour lire du texte
     */
    @Handler
    public void handleParoleEvent(ParoleEvent paroleEvent) {
        if (paroleEvent.getTexte() != null && !paroleEvent.getTexte().trim().equals("")) {
            lire(paroleEvent.getTexte().trim());
        }
    }

    @Override
    public void initialiser() {
        
    }

    @Override
    public void arreter() {
        
    }
    
    public static void main(String[] args) {
     // Initialisation du lecteur
//        ConfigurationSyntheseVocale.getInstance().setDossierRacine("/home/npeltier/Robot/Programme" + File.separator + "synthese-vocale");
//        LecteurTexte lecteurTexte = new LecteurTexte();
//        lecteurTexte.setTexte("Je suis une voix de synthèse en cours d'expérimentation. Arrives-tu à me comprendre ?");
//        lecteurTexte.playAll();
        
        try {
            Thread.sleep(1000);
            String[] params = {"espeak", "-v", "fr+f4", "Quel est ton nom ? Tu tappelles Nicolas !"};
            Process p = Runtime.getRuntime().exec(params);
            p.waitFor();
//            System.out.print("J");
//            Thread.sleep(185-25);
//            System.out.print("e");
//            Thread.sleep(112-25);
//            System.out.print("s");
//            Thread.sleep(118-25);
//            System.out.print("u");
//            Thread.sleep(84-25);
//            System.out.print("i");
//            Thread.sleep(84-25);
//            System.out.print("s");
//            Thread.sleep(94-25);
//            System.out.print("u");
//            Thread.sleep(87-25);
//            System.out.print("n");
//            Thread.sleep(76-25);
//            System.out.print("v");
//            Thread.sleep(86-25);
//            System.out.print("o");
//            Thread.sleep(76-25);
//            System.out.print("i");
//            Thread.sleep(92-25);
//            System.out.print("d");
//            Thread.sleep(75-25);
//            System.out.print("e");
//            Thread.sleep(113-25);
//            System.out.print("s");
//            Thread.sleep(117-25);
//            System.out.print("yn");
//            Thread.sleep(120-25);
//            System.out.print("th");
//            Thread.sleep(97-25);
//            System.out.print("è");
//            Thread.sleep(105-25);
//            System.out.print("s");
//            Thread.sleep(92-25);
//            System.out.print("en");
//            Thread.sleep(121-25);
//            System.out.print("c");
//            Thread.sleep(98-25);
//            System.out.print("ou");
//            Thread.sleep(92-25);
//            System.out.print("r");
//            Thread.sleep(65-25);
//            System.out.print("d");
//            Thread.sleep(78-25);
//            System.out.print("e");
//            Thread.sleep(108-25);
//            System.out.print("x");
//            Thread.sleep(92-25);
//            Thread.sleep(115-25);
//            System.out.print("p");
//            Thread.sleep(102-25);
//            System.out.print("é");
//            Thread.sleep(98-25);
//            System.out.print("r");
//            Thread.sleep(64-25);
//            System.out.print("i");
//            Thread.sleep(83-25);
//            System.out.print("m");
//            Thread.sleep(82-25);
//            System.out.print("en");
//            Thread.sleep(121-25);
//            System.out.print("t");
//            Thread.sleep(92-25);
//            System.out.print("a");
//            Thread.sleep(97-25);
//            System.out.print("t");
//            Thread.sleep(118-25);
//            System.out.print("i");
//            Thread.sleep(88-25);
//            System.out.print("on");
//            Thread.sleep(219-25);
//            System.out.print(" ");
//            Thread.sleep(60-25);
//            System.out.print("a");
//            Thread.sleep(197-25);
//            System.out.print("rr");
//            Thread.sleep(65-25);
//            System.out.print("i");
//            Thread.sleep(88-25);
//            System.out.print("ve");
//            Thread.sleep(85-25);
//            System.out.print("t");
//            Thread.sleep(98-25);
//            System.out.print("u");
//            Thread.sleep(185-25);
//            System.out.print("");
//            Thread.sleep(10);
//            System.out.print("à");
//            Thread.sleep(193-25);
//            System.out.print("m");
//            Thread.sleep(85-25);
//            System.out.print("e");
//            Thread.sleep(115-25);
//            System.out.print("c");
//            Thread.sleep(92-25);
//            System.out.print("om");
//            Thread.sleep(120-25);
//            System.out.print("p");
//            Thread.sleep(105-25);
//            System.out.print("r");
//            Thread.sleep(65-25);
//            System.out.print("en");
//            Thread.sleep(117-25);
//            System.out.print("d");
//            Thread.sleep(75-25);
//            System.out.print("re");
//            Thread.sleep(168-25);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
