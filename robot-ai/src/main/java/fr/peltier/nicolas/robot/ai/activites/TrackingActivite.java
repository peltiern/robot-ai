package fr.peltier.nicolas.robot.ai.activites;

import net.engio.mbassy.bus.MBassador;
import fr.peltier.nicolas.robot.ai.decisionnel.Contexte;
import fr.peltier.nicolas.robot.ai.memoire.ReconnaissanceFaciale;
import fr.peltier.nicolas.robot.ai.systemenerveux.event.MouvementTeteEvent;
import fr.peltier.nicolas.robot.ai.systemenerveux.event.MouvementTeteEvent.MOUVEMENTS_GAUCHE_DROITE;
import fr.peltier.nicolas.robot.ai.systemenerveux.event.MouvementTeteEvent.MOUVEMENTS_HAUT_BAS;
import fr.peltier.nicolas.robot.ai.systemenerveux.event.ReconnaissanceVocaleEvent;
import fr.peltier.nicolas.robot.ai.systemenerveux.event.RobotEvent;
import fr.peltier.nicolas.robot.ai.systemenerveux.event.VisagesEvent;

/**
 * Activité "Tracking de visage".
 * @author Nicolas Peltier (nico.peltier@gmail.com)
 */
public class TrackingActivite extends AbstractActivite {

    /**
     * Constructeur
     * @param systemeNerveux système nerveux du robot
     */
    public TrackingActivite(MBassador<RobotEvent> systemeNerveux, Contexte contexte, ReconnaissanceFaciale reconnaissanceFaciale) {
        super(systemeNerveux, contexte, reconnaissanceFaciale);
    }
    
    @Override
    public void initialiser() {
        dire("Début du suivi du visage.");
    }

    @Override
    public void handleVisagesEvent(VisagesEvent visagesEvent) {
        suivreVisage(visagesEvent);
    }

    @Override
    public void handleReconnaissanceVocalEvent(ReconnaissanceVocaleEvent reconnaissanceVocaleEvent) {
        
    }

    @Override
    public void arreter() {
        // Arrêt de la tête
        final MouvementTeteEvent mouvementTeteEvent = new MouvementTeteEvent();
        mouvementTeteEvent.setMouvementGaucheDroite(MOUVEMENTS_GAUCHE_DROITE.STOPPER);
        mouvementTeteEvent.setMouvementHauBas(MOUVEMENTS_HAUT_BAS.STOPPER);
        systemeNerveux.publish(mouvementTeteEvent);
        
        dire("Fin du suivi du visage.");
    }

}
