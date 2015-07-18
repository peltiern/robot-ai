package fr.peltier.nicolas.robot.ai.rc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;

/**
 * Classe permettant le contrôle à distance de la brique NXT pour les mouvements des roues et de la tête.
 * @author Java Developer
 */
public class RobotRemoteControl {
    
    /** Singleton de la classe. */
    private static RobotRemoteControl instance;
    
    /** Flag indiquant que la connexion est établie avec la brique NXT. */
    private static boolean connected = false;

    // Codes pour les mouvements des roues
    public final int PILOTE_FORWARD = 0;
    public final int PILOTE_BACKWARD = 1;
    public final int PILOTE_STOP = 2;
    public final int PILOTE_TRAVEL = 3;
    public final int PILOTE_ROTATE = 4;
    public final int PILOTE_STEER = 5;
    public final int PILOTE_ARC = 6;
    public final int PILOTE_RESET = 7;
    public final int PILOTE_SETMOVESPEED = 8;
    public final int PILOTE_SETTURNSPEED = 9;
    public final int PILOTE_REPORT = 10;

    // Codes pour les mouvements de la tête
    public final int TETE_TOURNER_GAUCHE = 11;
    public final int TETE_TOURNER_DROITE = 12;
    public final int TETE_GAUCHE_DROITE_STOP = 13;
    public final int TETE_TOURNER_HAUT = 14;
    public final int TETE_TOURNER_BAS = 15;
    public final int TETE_HAUT_BAS_STOP = 16;

    // Codes pour le robot
    public static final int ROBOT_STOP = 99;

    private boolean _isMoving;
    private float _distance;
    private float _angle;

    private static DataInputStream dataIn;
    /**
     * used by send()
     */
    private static DataOutputStream dataOut;
    /*
     * input field for robot name or address
     */
    private static NXTConnector con;

    public static void main(String[] args) throws InterruptedException {
        RobotRemoteControl rbc = new RobotRemoteControl();
        rbc.connect("NXT", "");
        rbc.tournerTeteHaut();
        Thread.sleep(1000);
        rbc.tournerTeteBas();
        Thread.sleep(1000);
        rbc.stopTeteHautBas();
    }


    /** Constructeur. */
    private RobotRemoteControl() { }
    
    public static RobotRemoteControl getInstance() {
        if (instance == null) {
            instance = new RobotRemoteControl();
        }
        // Etablissement de la connexion
        if (!connected) {
            connect("NXT", "");
        }
        return instance;
    }


    /**
     * connects to NXT using Bluetooth
     * @param name of NXT
     * @param address  bluetooth address
     */
    private static boolean connect(String name, String address)
    {

        System.out.println(" connecting to " + name + " " + address);
        con = new NXTConnector();
        boolean res = con.connectTo(name, address, NXTCommFactory.BLUETOOTH);
        System.out.println(" connect result " + res);
        boolean connected = res;
        if (!connected)
        {
            return connected;
        }
        OutputStream os = con.getOutputStream();
        InputStream is = con.getInputStream();
        try  // handshake
        {
            byte[] hello = new byte[]
                    {
                'R', 'C', 'P'
                    };
            os.write(hello);
            os.flush();
            byte[] reply  = {0,0,0};
            is.read(reply);

            for (int i = 0; i<hello.length; i++)
            {
                connected  = connected && reply[i]==hello[i];
            }
        } catch (IOException e)
        {
            System.out.println("Handshake failed - is RCPilot running?");
            connected = false;
            return false;
        }
        if(!connected )
        {
            System.out.println("handshake failed - is RCPilot running?");
            return false;
        }
        dataIn = new DataInputStream(con.getInputStream());
        dataOut = new DataOutputStream(con.getOutputStream());
        if (dataIn != null)
        {
            System.out.println(" data In  OK");
        } else
        {
            connected = false;
            System.out.println(" dataIn NULL");
            return connected;
        }
        return connected;
    }

    /**
     * true if the robot is moving
     * @return true if the robot is moving under power.
     */
    public boolean isMoving()
    {
        report();
        return _isMoving;
    }

    /**
     * distance traveled  since the last call to reset.
     * @return the distance traveled  since last call to reset
     **/
    public float getTravelDistance()
    {
        report();
        return _distance;
    }

    /**
     * angle of rotation of the robot since last call to reset.
     * @return the angle of rotation of the robot since last call to reset.
     */
    public float getAngle()
    {
        report();
        return _angle;
    }

    /**
     * Moves the NXT robot along an arc with a specified radius and  angle,
     * after which the robot stops moving. This method has the ability to return immediately
     * by using the <code>immediateReturn</code> parameter.
     * <p>
     * If <code>radius</code> is positive, the robot arcs left, and the center of the turning circle is on the left side of the robot.<br>
     * If <code>radius</code> is negative, the robot arcs right, and the center of the turning circle is on the right side of the robot.<br>
     * If <code>radius</code> is zero, is zero, the robot rotates in place.
     * <p>
     * The <code>arc(float, float, boolean)</code> method <b>can not drive a straight line</b>, which makes
     * it impractical for line following. A better solution for line following is
     * {@link #steer(float, float, boolean)}, which uses proportional steering and can drive straight lines and arcs.
     * <p>
     * The robot will stop when the degrees it has moved along the arc equals <code>angle</code>.<br>
     * If <code>angle</code> is positive, the robot will move travel forwards.<br>
     * If <code>angle</code> is negative, the robot will move travel backwards.
     * If <code>angle</code> is zero, the robot will not move and the method returns immediately.
     * <p>
     * Postcondition: Motor speeds are unpredictable.
     * <p>
     * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
     *
     * @param radius of the arc path. If positive, the left side of the robot is on the inside of the turn. If negative, the left
     *          side of the robot is on the outside of the turn.
     * @param angle The sign of the angle determines the direction of robot motion. Positive drives the robot forward, negative drives it backward.
     * @param immediateReturn If immediateReturn is true then the method returns immediately and your code MUST call
     *          updatePostion() when the robot has stopped. Otherwise, the robot position is lost.
     * @see #steer(float, float, boolean)
     * @see #travelArc(float, float, boolean)
     */
    public void arc(float radius, float angle, boolean immediateReturn)
    {
        send(PILOTE_ARC, radius, angle, immediateReturn);
    }

    /**
     * Moves the NXT robot along an arc with a specified radius and  angle,
     * after which the robot stops moving. This method does not return until the robot has
     * completed moving <code>angle</code> degrees along the arc.
     * <p>
     * If <code>radius</code> is positive, the robot arcs left, and the center of the turning circle is on the left side of the robot.<br>
     * If <code>radius</code> is negative, the robot arcs right, and the center of the turning circle is on the right side of the robot.<br>
     * If <code>radius</code> is zero, is zero, the robot rotates in place.
     * <p>
     * The <code>arc(float)</code> method <b>can not drive a straight line</b>, which makes
     * it impractical for line following. A better solution for line following is
     * {@link #steer(float)}, which uses proportional steering and can drive straight lines and arcs.
     * <p>
     * Robot will stop when the degrees it has moved along the arc equals <code>angle</code>.<br>
     * If <code>angle</code> is positive, the robot will move travel forwards.<br>
     * If <code>angle</code> is negative, the robot will move travel backwards.
     * If <code>angle</code> is zero, the robot will not move and the method returns immediately.
     * <p>
     * Postcondition: Motor speeds are unpredictable.
     * <p>
     * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
     *
     * @param radius of the arc path. If positive, the left side of the robot is on the inside of the turn. If negative, the left
     *          side of the robot is on the outside of the turn.
     * @param angle The sign of the angle determines the direction of robot motion. Positive drives the robot forward, negative drives it backward.
     * @see #steer(float, float)
     * @see #travelArc(float, float)
     */
    public void arc(float radius, float angle)
    {
        arc(radius, angle, false);
    }
    /**
     * Starts the  NXT robot moving along an arc with a specified radius.
     * <p>
     * If <code>radius</code> is positive, the robot arcs left, and the center of the turning circle is on the left side of the robot.<br>
     * If <code>radius</code> is negative, the robot arcs right, and the center of the turning circle is on the right side of the robot.<br>
     * If <code>radius</code> is zero, the robot rotates in place.
     * <p>
     * The <code>arc(float)</code> method <b>can not drive a straight line</b>, which makes
     * it impractical for line following. A better solution for line following is
     * {@link #steer(float)}, which uses proportional steering and can drive straight lines and arcs.
     * <p>
     * Postcondition: Motor speeds are unpredictable.
     * <p>
     * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
     *
     * @param radius of the arc path. If positive, the left side of the robot is on the inside of the turn. If negative, the left
     *          side of the robot is on the outside of the turn.
     * @see #steer(float)
     */
    public void arc(float radius)
    {
        arc(radius, Float.MAX_VALUE, true);
    }

    /**
     * Moves the NXT robot a specific distance. A positive value moves it forward and a negative value moves it backward.
     * @param distance The positive or negative distance to move the robot, in wheel diameter units.
     * @param immediateReturn If immediateReturn is true then the method returns immediately.
     */
    public void travel(float distance, boolean immediateReturn)
    {
        send(PILOTE_TRAVEL, distance, 0, immediateReturn);
    }

    /**
     * Moves the NXT robot a specific distance. A positive value moves it forward and a negative value moves it backward.
     * Method returns when movement is done.
     *
     * @param distance The positive or negative distance to move the robot.
     */
    public void travel(float distance)
    {
        travel(distance, false);
    }

    /**
     * Rotates the NXT robot the specifed number of degress; direction determined by the sign of the parameter.
     * Motion stops  when rotation is done.
     *
     * @param angle The angle to rotate in degrees. A positive value rotates left, a negative value right (clockwise).
     * @param immediateReturn If immediateReturn is true then the method returns immediately
     */
    public void rotate(float angle, boolean immediateReturn)
    {
        send(PILOTE_ROTATE,angle,0,immediateReturn);
    }

    /**
     * Rotates the NXT robot the specified number of degrees; direction determined by the sign of the parameter.
     * Method returns when rotation is done.
     *
     * @param angle The angle to rotate in degrees. A positive value rotates left, a negative value right (clockwise).
     */
    public void rotate(float angle)
    {
        rotate(angle,false);
    }


    /**
     * Moves the robot along a curved path for a specified angle of rotation. This method is similar to the
     * {@link #arc(float radius, float angle, boolean immediateReturn)} method except it uses a ratio of motor
     * speeds to speeds to determine the curvature of the path and therefore has the ability to drive straight.
     * This makes it useful for line following applications. This method has the ability to return immediately
     * by using the <code>immediateReturn</code> parameter set to <b>true</b>.
     *
     * <p>
     * The <code>turnRate</code> specifies the sharpness of the turn, between -200 and +200.<br>
     * For details about how this paramet works, see {@link #steer(float turnRate) }
     * <p>
     * The robot will stop when the degrees it has moved along the arc equals <code>angle</code>.<br>
     * If <code>angle</code> is positive, the robot will move travel forwards.<br>
     * If <code>angle</code> is negative, the robot will move travel backwards.
     * If <code>angle</code> is zero, the robot will not move and the method returns immediately.
     * <p>
     * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
     *
     * @param turnRate If positive, the left side of the robot is on the inside of the turn. If negative,
     * the left side is on the outside.
     * @param angle The angle through which the robot will rotate. If negative, robot traces the turning circle backwards.
     * @param immediateReturn If immediateReturn is true then the method returns immediately and your code MUST call
     *          updatePostion() when the robot has stopped. Otherwise, the robot position is lost.
     */
    public void steer(float turnRate, float angle, boolean immediateReturn)
    {
        send(PILOTE_STEER, turnRate, angle, immediateReturn);
    }

    /**
     * Moves the robot along a curved path through a specified turn angle. This method is similar to the
     * {@link #arc(float radius , float angle)} method except it uses a ratio of motor
     * speeds to determine the curvature of the  path and therefore has the ability to drive straight. This makes
     * it useful for line following applications. This method does not return until the robot has
     * completed moving <code>angle</code> degrees along the arc.<br>
     * The <code>turnRate</code> specifies the sharpness of the turn, between -200 and +200.<br>
     * For details about how this paramet works.See {@link #steer(float turnRate) }
     * <p>
     * The robot will stop when the degrees it has moved along the arc equals <code>angle</code>.<br>
     * If <code>angle</code> is positive, the robot will move travel forwards.<br>
     * If <code>angle</code> is negative, the robot will move travel backwards.
     * If <code>angle</code> is zero, the robot will not move and the method returns immediately.
     * <p>
     * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
     *
     * @param turnRate If positive, the left side of the robot is on the inside of the turn. If negative,
     * the left side is on the outside.
     * @param angle The angle through which the robot will rotate. If negative, robot traces the turning circle backwards.
     */
    public void steer(float turnRate, float angle)
    {
        steer(turnRate, angle, false);
    }

    /**
     * Moves the robot along a curved path through a specified turn angle. This method is similar to the
     * {@link #arc(float radius , float angle)} method except it uses a ratio of motor
     * speeds to determine the curvature of the  path and therefore has the ability to drive straight. This makes
     * it useful for line following applications. This method does not return until the robot has
     * completed moving <code>angle</code> degrees along the arc.<br>
     * The <code>turnRate</code> specifies the sharpness of the turn, between -200 and +200.<br>
     * For details about how this paramet works.See {@link #steer(float turnRate) }
     * <p>
     * The robot will stop when the degrees it has moved along the arc equals <code>angle</code>.<br>
     * If <code>angle</code> is positive, the robot will move travel forwards.<br>
     * If <code>angle</code> is negative, the robot will move travel backwards.
     * If <code>angle</code> is zero, the robot will not move and the method returns immediately.
     * <p>
     * Note: If you have specified a drift correction in the constructor it will not be applied in this method.
     *
     * @param turnRate If positive, the left side of the robot is on the inside of the turn. If negative,
     * the left side is on the outside.
     * @param angle The angle through which the robot will rotate. If negative, robot traces the turning circle backwards.
     */
    public void steer(float turnRate)
    {
        steer(turnRate, 36000, true);
    }

    /**
     *Starts the  NXT robot moving  forward.
     */
    public void forward()
    {
        send(PILOTE_FORWARD,0,0,false);
    }

    /**
     *Starts the  NXT robot moving  backward .
     */
    public void backward()
    {
        send(PILOTE_BACKWARD,0,0,false);
    }

    public void setMoveSpeed(float speed)
    {
        send(PILOTE_SETMOVESPEED, speed, 0, false);
    }

    public void setTurnSpeed(float speed)
    {
        send(PILOTE_SETTURNSPEED, speed, 0, false);
    }

    /**
     * Reset traveled distance and rotated angle.
     */
    public void reset()
    {
        send(PILOTE_RESET, 0, 0, false);
    }



    /**
     * Halts the NXT robot
     */
    public void stop()
    {
        send(PILOTE_STOP, 0, 0, false);
    }

    /**
     * called by all methods that get data from the remote pilot
     * including getAngle(), getTravelDistance(), isMoving()
     */
    private void report()
    {
        send(PILOTE_REPORT, 0, 0, false);
    }


    /**
     * Tourne la tête à gauche.
     */
    public void tournerTeteGauche() {
        send(TETE_TOURNER_GAUCHE, 0, 0, false);
    }

    /**
     * Tourne la tête à droite.
     */
    public void tournerTeteDroite() {
        send(TETE_TOURNER_DROITE, 0, 0, false);
    }

    /**
     * Stoppe le mouvement gauche-droite de la tête.
     */
    public void stopTeteGaucheDroite() {
        send(TETE_GAUCHE_DROITE_STOP, 0, 0, false);
    }

    /**
     * Tourne la tête en haut.
     */
    public void tournerTeteHaut() {
        send(TETE_TOURNER_HAUT, 0, 0, false);
    }

    /**
     * Tourne la tête à bas.
     */
    public void tournerTeteBas() {
        send(TETE_TOURNER_BAS, 0, 0, false);
    }

    /**
     * Stoppe le mouvement haut-bas de la tête.
     */
    public void stopTeteHautBas() {
        send(TETE_HAUT_BAS_STOP, 0, 0, false);
    }

    public void stopRobot() {
        send(ROBOT_STOP, 0, 0, true);
        System.exit(0);
    }


    /**
     * send data to the reomote Pilot
     * @param code
     * @param param1
     * @param param2
     * @param immediateReturn
     */
    private void send(int code, float param1, float param2,
        boolean immediateReturn)
    {
        try
        {
            dataOut.writeInt(code);
            dataOut.writeFloat(param1);
            dataOut.writeFloat(param2);
            dataOut.writeBoolean(immediateReturn);
            dataOut.flush();
        } catch (IOException e)
        {
            System.out.println("send problem " + e);
        }
        // wait for reply
        try
        {
            _isMoving = dataIn.readBoolean();
            _distance = dataIn.readFloat();
            _angle = dataIn.readFloat();
            //          System.out.println(" read " + _isMoving + " " + _distance + " " + _angle);
        } catch (IOException e)
        {
            System.out.println("read error " + e);
        }
    }

}
