package fr.peltier.nicolas.robot.ai.test.phidget;

import com.phidgets.AdvancedServoPhidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.ServoPositionChangeEvent;
import com.phidgets.event.ServoPositionChangeListener;

public class PhidgetServoTest {

    /**
     * @param args
     * @throws PhidgetException 
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws PhidgetException, InterruptedException {
        AdvancedServoPhidget servoController = new AdvancedServoPhidget();
        servoController.openAny();
        servoController.waitForAttachment();
        servoController.setServoType(0, AdvancedServoPhidget.PHIDGET_SERVO_HITEC_HS422);
        servoController.setEngaged(0, true);
        servoController.addServoPositionChangeListener(new ServoPositionChangeListener() {
            
            @Override
            public void servoPositionChanged(ServoPositionChangeEvent event) {
                System.out.println("Position = " + event.getValue());
            }
        });
        
        while (true) {
            Thread.sleep(10);
        }
    }

}
