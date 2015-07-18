package fr.peltier.nicolas.robot.ai.organes.capteurs;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.apache.log4j.Logger;

import fr.peltier.nicolas.robot.ai.organes.AbstractOrgane;
import fr.peltier.nicolas.robot.ai.server.WebSpeechServer;
import fr.peltier.nicolas.robot.ai.server.WebSpeechServerListener;
import fr.peltier.nicolas.robot.ai.systemenerveux.event.ReconnaissanceVocaleControleEvent;
import fr.peltier.nicolas.robot.ai.systemenerveux.event.ReconnaissanceVocaleControleEvent.CONTROLE;
import fr.peltier.nicolas.robot.ai.systemenerveux.event.ReconnaissanceVocaleEvent;
import fr.peltier.nicolas.robot.ai.systemenerveux.event.RobotEvent;

/**
 * Capteur lié à la reconnaissance vocale.
 * @author Nicolas Peltier (nico.peltier@gmail.com)
 */
public class CapteurVocal3 extends AbstractOrgane implements WebSpeechServerListener {

    /** Serveur Speech. */
    private WebSpeechServer speechWebServer;

    /** Logger. */
    private Logger logger = Logger.getLogger(CapteurVocal3.class);

    public CapteurVocal3(MBassador<RobotEvent> systemeNerveux) {
        super(systemeNerveux);

        speechWebServer = WebSpeechServer.getInstance();
        // Le capteur vocal est écouteur du serveur
        speechWebServer.addListener(this);
    }

    @Override
    public void initialiser() {
    }

    @Override
    public void arreter() {
    }

    /**
     * Intercepte les évènements de contrôle de la reconnaissance vocale.
     * @param reconnaissanceVocaleControleEvent évènement de contrôle de la reconnaissance vocale
     */
    @Handler
    public void handleReconnaissanceVocaleControleEvent(ReconnaissanceVocaleControleEvent reconnaissanceVocaleControleEvent) {
        if (reconnaissanceVocaleControleEvent.getControle() == CONTROLE.DEMARRER) {
            logger.debug("Démarrage de la reconnaissance vocale");
            //            microphone.clear();
            //            microphone.startRecording();
            //            misEnPause = false;
            //            recognizer.addResultListener(this);
        } else if (reconnaissanceVocaleControleEvent.getControle() == CONTROLE.METTRE_EN_PAUSE) {
            logger.debug("Mise en pause de la reconnaissance vocale");
            //            recognizer.removeResultListener(this);
            //            misEnPause = true;
            //            microphone.stopRecording();
        }
    }

    @Override
    public void onSpeechResult(String speechResult) {
        if (speechResult != null && !speechResult.trim().equals("")) {
            // Envoi de l'évènement de reconnaissance
            final ReconnaissanceVocaleEvent event = new ReconnaissanceVocaleEvent();
            event.setTexteReconnu(speechResult);
            systemeNerveux.publish(event);
        }
    }

}
