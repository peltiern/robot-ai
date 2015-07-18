package fr.peltier.nicolas.robot.ai.organes.capteurs;

import java.io.File;
import java.io.IOException;

import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleParse;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.listener.Handler;

import org.apache.log4j.Logger;

import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.decoder.ResultListener;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.jsgf.JSGFGrammar;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;
import fr.peltier.nicolas.robot.ai.Robot;
import fr.peltier.nicolas.robot.ai.organes.AbstractOrgane;
import fr.peltier.nicolas.robot.ai.organes.actionneurs.OrganeParole;
import fr.peltier.nicolas.robot.ai.systemenerveux.event.ReconnaissanceVocaleControleEvent;
import fr.peltier.nicolas.robot.ai.systemenerveux.event.ReconnaissanceVocaleControleEvent.CONTROLE;
import fr.peltier.nicolas.robot.ai.systemenerveux.event.ParoleEvent;
import fr.peltier.nicolas.robot.ai.systemenerveux.event.ReconnaissanceVocaleEvent;
import fr.peltier.nicolas.robot.ai.systemenerveux.event.RobotEvent;

/**
 * Capteur lié à la reconnaissance vocale.
 * @author Nicolas Peltier (nico.peltier@gmail.com)
 */
public class CapteurVocal2 extends AbstractOrgane {

    /** Moteur de reconnaissance vocale. */
    private LiveSpeechRecognizer recognizer;

    /** Grammaire. */
    private JSGFGrammar jsgfGrammar;

    private BaseRecognizer baseRecognizer;

    /** Thread lancer pour permettre la reconnaissance vocale en parallèle des autres tâches. */
    private Thread threadReconnaissance;

    /** Flag indiquant de stopper le thread. */
    private boolean stopperThread = false;

    /** Flag indiquant que la reconnaissance est mise en pause. */
    private boolean misEnPause = false;

    /** Logger. */
    private Logger logger = Logger.getLogger(Robot.class);

    public CapteurVocal2(MBassador<RobotEvent> systemeNerveux) {
        super(systemeNerveux);

        final Configuration configuration = new Configuration();
        final String cheminDossierReconnaissanceVocale = System.getProperty("robot.dir") + File.separator + "reconnaissance-vocale" + File.separator;


        // Modèle acoustique
        configuration.setAcousticModelPath(cheminDossierReconnaissanceVocale + "lium_french_f0");

        // Dictionnaire
        configuration.setDictionaryPath(cheminDossierReconnaissanceVocale + "lium_french_f0" + File.separator + "dict" + File.separator + "frenchWords62K.dic");

        // Modèle de langue
        configuration.setLanguageModelPath(cheminDossierReconnaissanceVocale + "lium_french_f0" + File.separator + "dict" + File.separator + "french3g62K.lm.dmp");

        // Grammaire
        configuration.setGrammarPath(cheminDossierReconnaissanceVocale);
        configuration.setGrammarName("grammaire_robot");
        configuration.setUseGrammar(true);

        // Création du moteur de reconnaissance vocale en fonction de la configuration
        try {
            recognizer = new LiveSpeechRecognizer(configuration);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //        // Grammaire
        //        jsgfGrammar = new JSGFGrammar(location, grammarName, showGrammar, optimizeGrammar, addSilenceWords, addFillerWords, dictionary)
        //        
        //        // Moteur de reconnaissance de base pour la récupération des règles
        //        baseRecognizer = new BaseRecognizer(jsgfGrammar.getGrammarManager());
    }

    @Override
    public void initialiser() {
        recognizer.startRecognition(true);
        // Lancement de la reconnaissance dans un thread
        threadReconnaissance = new Thread() {
            @Override
            public void run() {
                while (!stopperThread) {
                    if(!misEnPause) {
                        System.out.println("Avant RESULT");
                        final SpeechResult result = recognizer.getResult();
                        System.out.println("RESULT = " + result);
                        if (result != null) {
                            recognizer.stopRecognition();
                            String resultText = result.getHypothesis();

                            if (resultText != null && !resultText.trim().equals("")) {
                                System.out.println("TEXTE = " + resultText);

                                //                                RuleGrammar ruleGrammar = new BaseRuleGrammar(baseRecognizer, jsgfGrammar.getRuleGrammar());
                                //                                RuleParse ruleParse = null;
                                //                                try {
                                //                                    ruleParse = ruleGrammar.parse(resultText, null);
                                //                                    if (ruleParse != null) {
                                //                                        System.out.println("Regle = " + ruleParse.getRuleName().getRuleName());
                                //                                    }
                                //                                    System.out.println("TEXTE = " + resultText);
                                //                                } catch (GrammarException e) {
                                //                                    // TODO Auto-generated catch block
                                //                                    e.printStackTrace();
                                //                                }
                                //                                //                                    dire(resultText);

                                // Envoi de l'évènement de reconnaissance
                                final ReconnaissanceVocaleEvent event = new ReconnaissanceVocaleEvent();
                                event.setTexteReconnu(resultText);
                                //                                if (ruleParse != null) {
                                //                                    event.setNomRegle(ruleParse.getRuleName().getRuleName());
                                //                                }
                                systemeNerveux.publish(event);

                            }
                            recognizer.startRecognition(true);
                        }
                    }
                }
            }
        };
        threadReconnaissance.start();

    }

    @Override
    public void arreter() {
        stopperThread = true;
        // Arrêt de la reconnaissance
        recognizer.stopRecognition();
    }

    /**
     * Intercepte les évènements de contrôle de la reconnaissance vocale.
     * @param reconnaissanceVocaleControleEvent évènement de contrôle de la reconnaissance vocale
     */
    @Handler
    public void handleReconnaissanceVocaleControleEvent(ReconnaissanceVocaleControleEvent reconnaissanceVocaleControleEvent) {
        if (reconnaissanceVocaleControleEvent.getControle() == CONTROLE.DEMARRER) {
            System.out.println("Démarrage de la reconnaissance vocale");
            //            microphone.clear();
            //            microphone.startRecording();
            recognizer.startRecognition(true);
            misEnPause = false;
        } else if (reconnaissanceVocaleControleEvent.getControle() == CONTROLE.METTRE_EN_PAUSE) {
            System.out.println("Mise en pause de la reconnaissance vocale");
            misEnPause = true;
            recognizer.stopRecognition();
        }
    }

    /**
     * Envoie un évènement pour dire du texte.
     * @param texte le texte à dire
     */
    private void dire(String texte) {
        System.out.println("Dire = " + texte);
        final ParoleEvent paroleEvent = new ParoleEvent();
        paroleEvent.setTexte(texte);
        systemeNerveux.publish(paroleEvent);
    }

    public static void main(String[] args) {
        System.setProperty("robot.dir", "/home/npeltier/Robot/Programme");
        MBassador<RobotEvent> systeme = new MBassador<RobotEvent>(BusConfiguration.Default());
        OrganeParole organeParole = new OrganeParole(systeme);
        systeme.subscribe(organeParole);
        final CapteurVocal2 capteurVocal = new CapteurVocal2(systeme);
        capteurVocal.initialiser();
        systeme.subscribe(capteurVocal);

    }

}
