package fr.peltier.nicolas.robot.ai.util.phidget;

import com.phidgets.AdvancedServoPhidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.ServoPositionChangeEvent;
import com.phidgets.event.ServoPositionChangeListener;

/**
 * Singleton permettant d'accéder et de contrôler des moteurs.
 * @author Java Developer
 */
public class PhidgetServoController implements ServoPositionChangeListener {

    /** Instance de la classe (singleton). */
    private static PhidgetServoController instance;

    /** Servo-contrôleur. */
    private static AdvancedServoPhidget servoController;

    /** Tableau des moteurs. */
    private static PhidgetMotor[] tabMotors = new PhidgetMotor[8];

    /** Constructeur privé. */
    private PhidgetServoController() {
        // Création du servo-contrôleur
        try {
            servoController = new AdvancedServoPhidget();
            servoController.openAny();
            servoController.waitForAttachment();
            System.out.println("waiting for AdvancedServo attachment...");
            servoController.addServoPositionChangeListener(this);
        } catch (PhidgetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Récupère l'instance de la classe.
     * @return l'instance de la classe
     */
    public static PhidgetServoController getInstance() {
        if (instance == null) {
            instance = new PhidgetServoController();
        }
        return instance;
    }

    /**
     * Récupère l'instance du moteur demandé. Le crée si nécessaire.
     * @param index index du moteur demandé
     */
    public static PhidgetMotor getMotor(int index) {
        // Création de l'instance du contrôleur si nécessaire
        getInstance();
        // Création du moteur si nécessaire
        if (tabMotors[index] == null) {
            tabMotors[index] = new PhidgetMotor(index, servoController);
        }
        return tabMotors[index];
    }

    public void stopMotors() {
        try {
            for (int i = 0; i < servoController.getMotorCount(); i++) {
                servoController.setEngaged(i, false);
            }
            servoController.close();
        } catch (PhidgetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void servoPositionChanged(ServoPositionChangeEvent event) {
        if (tabMotors != null && tabMotors.length > 0) {
            for (PhidgetMotor motor : tabMotors) {
                if (motor != null) {
                    if (event.getIndex() == motor.getIndex()) {
                        motor.servoPositionChanged(event);
                    }
                }
            }
        }
    }

}
