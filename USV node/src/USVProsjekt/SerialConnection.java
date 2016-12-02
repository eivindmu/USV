package USVProsjekt;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Klasse for å opprette seriell forbindelse
 *
 * @author Albert
 */
public class SerialConnection {

    private int baudRate;
    private SerialPort comPort;
    private String serialString;
    private boolean available;

    public SerialConnection(String comPortString, int baudRate) {
        this.comPort = SerialPort.getCommPort(comPortString);
        this.baudRate = baudRate;
        serialString = "";
        available = false;
    }

    /**
     * lister opp tilgjengelige porter
     */
    public void listAvailableSerialPorts() {
        SerialPort ports[] = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            System.out.println(port.getSystemPortName());
        }

    }

    /**
     * Kobler til med en ID (thruster,GPS...etc)
     *
     * @param ID
     */
    public void connect(Identifier ID) {
        if (comPort.openPort()) {
            comPort.setBaudRate(baudRate);
            comPort.setComPortTimeouts(
                    SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
            System.out.println("Successfully connected to " + ID
                    + " via " + comPort.getSystemPortName() + " with Baud Rate:"
                    + " " + comPort.getBaudRate());
        } else {
            System.out.println("Not able to connect to " + ID + "..");

        }
    }

    /**
     * metode som skriver pwm verdier over seriellporten
     *
     * @param thrustMicros1
     * @param thrustMicros2
     * @param thrustMicros3
     * @param thrustMicros4
     */
    public void writeThrustMicros(int thrustMicros1, int thrustMicros2,
            int thrustMicros3, int thrustMicros4) {
        //skriver til arduino
        String writeString;
        writeString = "" + thrustMicros1 + ":" + thrustMicros2 + ":"
                + thrustMicros3 + ":" + thrustMicros4 + ":";

        PrintWriter output = new PrintWriter(comPort.getOutputStream());
        output.write(writeString);
        output.flush();
        //Leser tilbake dataene fra arduino
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(comPort.getInputStream(), "UTF-8"));
            System.out.println(br.readLine());
        } catch (IOException e) {
            System.out.println("ioex writeThrustMicros() in SerialConnection");
        }
    }

    /**
     * kobler til og lytter på data på seriellportene
     *
     * @param ID
     */
    public void connectAndListen(Identifier ID) {
        connect(ID);
        comPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType()
                        != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    return;
                }
                byte[] newData = null;
                try {
                    switch (ID) {
                        case GPS:
                            Thread.sleep(90);
                            newData = new byte[comPort.bytesAvailable()];
                            break;
                        case WIND:
                            Thread.sleep(120);
                            newData = new byte[comPort.bytesAvailable()];
                            break;
                        case IMU:
                            //IMU
                            Thread.sleep(30);
                            newData = new byte[comPort.bytesAvailable()];
                            break;
                    }
                } catch (InterruptedException ex) {
                }
                comPort.readBytes(newData, newData.length);
                setSerialLine(new String(newData));
            }
        }
        );
    }

    /**
     * setter seriell linjen som er mottatt
     *
     * @param serialLine
     */
    public synchronized void setSerialLine(String serialLine) {
        while (available) {
            try {
                wait();
            } catch (InterruptedException ex) {
            }
        }

        available = true;
        notifyAll();
        serialString = serialLine;
    }

    /**
     * heter den mottatte seriell linjen fra denne klassen
     *
     * @return
     */
    public synchronized String getSerialLine() {
        while (!available) {
            try {
                wait();
            } catch (InterruptedException ex) {

            }
        }
        available = false;
        notifyAll();
        return serialString;
    }
/**
 * sjekker om porten er åpnet
 * @return 
 */
    public boolean isConnected() {
        return comPort.isOpen();
    }
/**
 * lukker porten
 */
    public void close() {
        comPort.closePort();
    }
}
