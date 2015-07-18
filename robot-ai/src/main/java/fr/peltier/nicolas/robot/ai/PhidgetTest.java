package fr.peltier.nicolas.robot.ai;

import lejos.util.Delay;

import com.phidgets.AdvancedServoPhidget;
import com.phidgets.Phidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.AttachEvent;
import com.phidgets.event.AttachListener;
import com.phidgets.event.DetachEvent;
import com.phidgets.event.DetachListener;
import com.phidgets.event.ErrorEvent;
import com.phidgets.event.ErrorListener;
import com.phidgets.event.ServoPositionChangeEvent;
import com.phidgets.event.ServoPositionChangeListener;

public class PhidgetTest {
    
    private AdvancedServoPhidget servo;
    
    public PhidgetTest() {
    }
    
    private void init() {
        System.out.println(Phidget.getLibraryVersion());


        try {
            servo = new AdvancedServoPhidget();

            servo.addAttachListener(new AttachListener() {
                public void attached(AttachEvent ae) {
                    System.out.println("attachment of " + ae);
                }
            });
            servo.addDetachListener(new DetachListener() {
                public void detached(DetachEvent ae) {
                    System.out.println("detachment of " + ae);
                }
            });
            servo.addErrorListener(new ErrorListener() {
                public void error(ErrorEvent ee) {
                    System.out.println("error event for " + ee);
                }
            });
            servo.addServoPositionChangeListener(new ServoPositionChangeListener()
            {
                public void servoPositionChanged(ServoPositionChangeEvent oe)
                {
                    System.out.println(oe);
                }
            });

            //        System.out.println("Serial n° = " + servo.getSerialNumber());

            servo.openAny();
            System.out.println("waiting for AdvancedServo attachment...");
            servo.waitForAttachment();

            System.out.println("Serial: " + servo.getSerialNumber());
            System.out.println("Servos: " + servo.getMotorCount());
            
            //Initialize the Advanced Servo
            servo.setServoType(0, AdvancedServoPhidget.PHIDGET_SERVO_HITEC_HS422);
            servo.setEngaged(0, false);
            servo.setSpeedRampingOn(0, false);
            
            servo.setEngaged(0, true);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("Min = " + servo.getAccelerationMin(0) + ", Max = " + servo.getAccelerationMax(0) + ", Pos = " + servo.getPosition(0));
            servo.setSpeedRampingOn(0, true);
            servo.setAcceleration(0, 100000);
            servo.setVelocityLimit(0, 50);
            
            
            
        } catch (PhidgetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static final void main(String args[]) throws Exception {
        
        PhidgetTest pt = new PhidgetTest();
        System.out.println("Init ...");
        pt.init();
        
        System.out.println("FORWARD");
        pt.forward();
        Thread.sleep(1000);
        System.out.println("BACKWARD");
        pt.backward();
        Thread.sleep(1000);
        System.out.println("STOP");
        pt.stop();
        Thread.sleep(1000);
        System.out.println("BACKWARD");
        pt.backward();       // <-- Doesn't work
        Thread.sleep(1000);
        System.out.println("FORWARD");
        pt.forward();
        Thread.sleep(1000);
        System.out.println("STOP");
        pt.stop();
        Thread.sleep(1000);
        System.out.println("FORWARD");
        pt.forward();        // <-- Doesn't work
        Thread.sleep(1000);
        System.out.println("BACKWARD");
        pt.backward();
        Thread.sleep(1000);
        System.out.println("STOP");
        pt.stop();
        Thread.sleep(1000);
        System.out.println("FORWARD");
        pt.forward();
        Thread.sleep(1000);
        System.out.println("BACKWARD");
        pt.backward();
        Thread.sleep(1000);
        System.out.println("STOP");
        pt.stop();
        Thread.sleep(1000);
        System.out.println("FORWARD");
        pt.forward();
        Thread.sleep(1000);
        System.out.println("STOP");
        pt.stop();
        Thread.sleep(1000);
        System.out.println("FORWARD");
        pt.forward();        // <-- Doesn't work
        Thread.sleep(1000);
        System.out.println("BACKWARD");
        pt.backward();
        Thread.sleep(1000);
        System.out.println("STOP");
        pt.stop();
        Thread.sleep(1000);
        System.out.println("20");
        
        
//        System.out.println("Backward ...");
//        pt.backward();
//        System.in.read();
//        System.out.println("Stop ...");
//        pt.stop();
//        System.in.read();
//        System.out.println("Forward ...");
//        pt.forward();
//        System.in.read();
//        System.out.println("Stop ...");
//        pt.stop();
//        System.out.println("Backward ...");
//        pt.backward();
//        System.in.read();
//        System.out.println("Stop ...");
//        pt.stop();
//        System.in.read();
//        System.out.println("Forward ...");
//        pt.forward();
//        System.in.read();
//        System.out.println("Stop ...");
//        pt.stop();
//        System.out.println("Backward ...");
//        pt.backward();
//        System.in.read();
//        System.out.println("Stop ...");
//        pt.stop();
//        System.in.read();
//        System.out.println("Forward ...");
//        pt.forward();
//        System.in.read();
//        System.out.println("Stop ...");
//        pt.stop();
//        System.out.println("Backward ...");
//        pt.backward();
//        System.in.read();
//        System.out.println("Stop ...");
//        pt.stop();
//        System.in.read();
//        System.out.println("Forward ...");
//        pt.forward();
//        System.in.read();
//        System.out.println("Stop ...");
//        
//        pt.stop();
//        System.out.println("Backward ...");
//        pt.backward();
//        System.in.read();
//        System.out.println("Stop ...");
//        pt.stop();
//        System.in.read();
//        System.out.println("Forward ...");
//        pt.forward();
//        System.in.read();
//        System.out.println("Stop ...");
//        pt.stop();
        System.out.println("Close ...");
        pt.close();
    }
    
    public void backward() {
        try {
            servo.setVelocityLimit(0, 50);
            servo.setPosition(0, servo.getPositionMin(0));
        } catch (PhidgetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void forward() {
        
    }
    
    public void stop() {
        try {
//            servo.setSpeedRampingOn(0, false);
//            servo.setPosition(0, servo.getPosition(0));
//            servo.setSpeedRampingOn(0, true);
            servo.setPosition(0, servo.getPosition(0));
            servo.setVelocityLimit(0, 0);
        } catch (PhidgetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void close() {
        try {
            servo.setEngaged(0, false);
            servo.close();
        } catch (PhidgetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        servo = null;
    }
    
    
}
