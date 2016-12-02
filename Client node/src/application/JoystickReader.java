/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.util.TimerTask;
import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
//import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;

/**
 *
 * @author vegard 
 * Klasse for å lese data fra en joystick.
 * Benytter Jinput for å finne og lese joystick
 */
public class JoystickReader extends TimerTask {

    private final double[] axisValues;
    private Controller controller;

    public JoystickReader() {
        axisValues = new double[3];

        searchForController();
        if (controller == null) {
            //System.exit(1);
        }

    }

    // Sjekk om joystick er tilkoblet
    private void searchForController() {
        Controller[] controllers = ControllerEnvironment.
                getDefaultEnvironment().getControllers();
        for (Controller cont : controllers) {
            if (cont.getType() == Controller.Type.STICK) {
                controller = cont;
                break;
            }
        }
    }

    @Override
    public void run() {

        // Poll data fra controlleren.
        controller.poll();
        // Returnerer et array av Component-objekter, som inneholder
        // data fra joystick
        Component[] components = controller.getComponents();
        // Gå gjennom hver komponent, for å hente ut ønsket data
        for (int i = 0; i < components.length; i++) {
            Component comp = components[i];
            Identifier componentIdentifier = comp.getIdentifier();
            
            // Leser kun analogverdier.
            // Hvis ikke analog, gå til neste komponent
            if (comp.isAnalog()) {
                float axisValue = comp.getPollData();

                // X akse
                if (componentIdentifier == Component.Identifier.Axis.X) {
                    setXaxisValue(axisValue);
                    continue; // gå til neste komponent
                }
                // Y akse
                if (componentIdentifier == Component.Identifier.Axis.Y) {
                    setYaxisValue(axisValue);
                    continue; // Gå til neste komponent
                }
                // Vridning
                if (componentIdentifier == Component.Identifier.Axis.RZ) {
                    setZaxisValue(axisValue);
                }

//                printValues();
            }
        }
    }

    /**
     *
     * Mapper akseverdiene fra [-1 , 1] til antatt maksimalverdier i respektive
     * retninger
     */
    private synchronized void setXaxisValue(double axisValue) {
        if (axisValue < -0.05f) {
            axisValues[1] = axisValue * 58;
        } else if (axisValue > 0.05f) {
            axisValues[1] = axisValue * 68;
        } else {
            axisValues[1] = 0.0f;
        }
    }

     /*
     *
     * Mapper akseverdiene fra [-1 , 1] til antatt maksimalverdier i respektive
     * retninger
     */
    private synchronized void setYaxisValue(double axisValue) {
        if (axisValue < -0.05f) {
            axisValues[0] = -axisValue * 68;
        } else if (axisValue > 0.05f) {
            axisValues[0] = -axisValue * 58;
        }
        else axisValues[0] = 0.0f;
    }

    // Returnerer et array med verdier fra hver akse
    public synchronized double[] getAxisValues() {
        return axisValues;
    }

    // Mapper akseverdier fra z-aksen til antatt maksimale verdier for moment
    private synchronized void setZaxisValue(double axisValue) {
        float len1 = 0.8f;
        float len2 = 0.87f;
        float lenB = 0.2f;
        if (axisValue < -0.05f) {
            axisValues[2] = (-34.0f * len1 - 29.0f * len2 - 2 * 34.0f 
                    - (34.0f + 29.0f) * lenB) * (-axisValue);
        } else if (axisValue > 0.05f) {
            axisValues[2] = (34.0f * len1 + 29.0f * len2 + 2 * 34.0f 
                    + (34.0f + 29.0f) * lenB) * axisValue;
        }
        else axisValues[2] = 0;
    }

    private void printValues() {
        double[] values = getAxisValues();
        System.out.println("X axis: " + values[0] + " Y axis: " + values[1] + " Yaw: " + values[2]);
    }
}
