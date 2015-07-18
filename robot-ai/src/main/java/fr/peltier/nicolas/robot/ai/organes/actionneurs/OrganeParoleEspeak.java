package fr.peltier.nicolas.robot.ai.organes.actionneurs;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.sound.sampled.AudioInputStream;

import marytts.MaryInterface;
import marytts.client.RemoteMaryInterface;
import marytts.exceptions.SynthesisException;
import marytts.util.data.audio.AudioPlayer;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.listener.Handler;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.apache.log4j.Logger;

import fr.peltier.nicolas.robot.ai.organes.AbstractOrgane;
import fr.peltier.nicolas.robot.ai.server.WebSpeechServer;
import fr.peltier.nicolas.robot.ai.server.WebSpeechServerListener;
import fr.peltier.nicolas.robot.ai.systemenerveux.event.ParoleEvent;
import fr.peltier.nicolas.robot.ai.systemenerveux.event.ReconnaissanceVocaleControleEvent;
import fr.peltier.nicolas.robot.ai.systemenerveux.event.RobotEvent;
import fr.peltier.nicolas.robot.ai.systemenerveux.event.ReconnaissanceVocaleControleEvent.CONTROLE;
import fr.peltier.nicolas.si.vox.son.LecteurTexte;
import fr.peltier.nicolas.si.vox.util.ConfigurationSyntheseVocale;

public class OrganeParoleEspeak extends AbstractOrgane {

    private String fichierSyntheseVocale;
    
    /** Logger. */
    private Logger logger = Logger.getLogger(OrganeParole.class);

    /** Constructeur. */
    public OrganeParoleEspeak(MBassador<RobotEvent> systemeNerveux) {
        super(systemeNerveux);
        fichierSyntheseVocale = System.getProperty("robot.dir") + File.separator + "synthese-vocale" + File.separator + "synthese.sh";
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

            System.out.println("Lecture :\t" + texte);
            String[] params = {fichierSyntheseVocale, texte};
            try {
                Process p = Runtime.getRuntime().exec(params);
                p.waitFor();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

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
        
        System.setProperty("robot.dir", "/home/npeltier/Robot/Programme");
        
        final OrganeParoleEspeak organeParole = new OrganeParoleEspeak(new MBassador<RobotEvent>(BusConfiguration.Default()));
        
        final WebSpeechServer webSpeechServer = WebSpeechServer.getInstance();
        webSpeechServer.initialiser();
        
        organeParole.lire("Fin de l'initialisation");
        
     // Création du chat
//        String botname="amy";
//        String path=System.getProperty("robot.dir");
//        Bot bot = new Bot(botname, path);
//        final Chat chat = new Chat(bot);
//        
        WebSpeechServer.getInstance().addListener(new WebSpeechServerListener() {
            
            @Override
            public void onSpeechResult(String speechResult) {
                System.out.println("Recu : " + speechResult);
//                WebSpeechServer.getInstance().lire("Bonjour tout le monde ! Comment ça va ?");
//                final String reponse = chat.multisentenceRespond(speechResult);
                organeParole.lire(speechResult);
//                WebSpeechServer.getInstance().lire(speechResult);
            }
        });
    }

}
