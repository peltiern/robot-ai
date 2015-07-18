package fr.peltier.nicolas.robot.ai;


import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.MMXRegulatedMotor;
import lejos.nxt.addon.NXTMMX;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.RegulatedMotorListener;

import lejos.util.Delay;

/**
 *This is sample code demonstrating how to use the mindsensors NXTMMX.
 * 
 * @author Michael D. Smith mdsmitty@gmail.com
 * @author Kirk P. Thompson
 *
 */
public class NXTMMXSample2 {
    
    public static void main(String[] args) {
        System.out.println("Début");
        
        NXTMMX mux = new NXTMMX(SensorPort.S1);
        System.out.println("Début1");
        
        MMXRegulatedMotor m1 = new MMXRegulatedMotor(mux, NXTMMX.MMX_MOTOR_1);
        System.out.println("Début2");
        MMXRegulatedMotor m2 = new MMXRegulatedMotor(mux, NXTMMX.MMX_MOTOR_2);
        System.out.println("Début3");
        
        
        //Demo of basic forwards and backwards operations
        m1.setPower(30);
        m2.setSpeed(500);
                
        System.out.println("1");
        m1.backward();
        Delay.msDelay(1000);
        System.out.println("2");
        m1.forward();
        Delay.msDelay(1000);
        System.out.println("3");
        m1.backward();
        Delay.msDelay(1000);
        System.out.println("4");
        m1.backward();
        Delay.msDelay(1000);
        System.out.println("5");
        m1.stop();
        System.out.println("6");
        
        m2.backward();
        Delay.msDelay(1000);
        System.out.println("7");
        m2.forward();
        Delay.msDelay(1000);
        System.out.println("8");
        m2.backward();
        Delay.msDelay(1000);
        System.out.println("9");
        m2.backward();
        Delay.msDelay(1000);
        System.out.println("10");
        m2.stop();
        System.out.println("11");
        
        Delay.msDelay(1000);
        
        m1.forward();
        m2.forward();
        Delay.msDelay(2000);
        m1.backward();
        m2.backward();
        Delay.msDelay(2000);
        m1.stop();
        m2.stop();
        
        mux.fltMotors();
        
//        System.out.println("2");
//        Delay.msDelay(3000);
//        System.out.println("3");
//        m2.forward();
//        System.out.println("4");
//        Delay.msDelay(2000);
//        System.out.println("5");
//        mux.stopMotors();
//        System.out.println("5b");
//        m1.setPower(30);
//        m2.setSpeed(500);
//        mux.startMotors();
//        System.out.println("6");
//        m1.backward();
//        Delay.msDelay(2000);
//        System.out.println("7");
//        m2.forward();
//        Delay.msDelay(2000);
//        System.out.println("8");
//        m1.forward();
//        Delay.msDelay(2000);
//        System.out.println("9");
//        mux.stopMotors();
//        System.out.println("10");
    }
}

