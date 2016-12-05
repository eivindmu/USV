package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Albert
 * klasse for Klient-serverkommunikasjon mellom USV og land
 * 
 */
public class Client extends Thread {

    private boolean gainChanged;
    private boolean isConnected;
    private String line;

    private float headingReference;

    private int guiCommand;
    private int incrementReferenceNorth;
    private int incrementReferenceEast;

    private float[] dataFromUSV;
    private double[] latLonFromUSV;

    private JoystickReader reader;
    private int gainNr;
    private float gainValue;
    private DataStorage storage;
    private PrintWriter nedWriter;
    private boolean writing;
    
    private ArrayList<String> NMEASentences;
    private boolean shouldSendRoute = false;

    public Client(JoystickReader reader, DataStorage storage) {
        this.reader = reader;
        this.storage = storage;
        isConnected = gainChanged = writing = false;
        incrementReferenceNorth = incrementReferenceEast = 0;
        guiCommand = 0;
        latLonFromUSV = new double[2];
        dataFromUSV = new float[23];
        
        NMEASentences = new ArrayList<>();
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("GUI Command: " + 
                    guiCommand + " in FIRST While-loop in Reader");
            // Bruker trykker connect
            if (guiCommand == 3) { 
                try {
                    System.out.println("Should connect");
                    // Opprett ny socket 192.168.0.101
                    Socket clientSocket = new Socket("localhost", 2345);
                    System.out.println(clientSocket.isConnected());

                    BufferedReader inFromServer;
                    PrintStream pstream = new PrintStream(clientSocket.
                            getOutputStream());
                    long lastTime = 0;
                    long delay = 100;
                    while (!clientSocket.isClosed()) {
                        // Send data med 100 ms mellomrom
                        if (lastTime + delay < System.currentTimeMillis()) {
                            System.out.println("GUI Command: "
                                    + guiCommand 
                                    + " in SECOND While-loop in Reader");
                            isConnected = true;
                            // Hvis fjernstyring
                            if (guiCommand == 2) {
                                pstream.println(getRemoteCommand());
                            } else {
                                pstream.println(getCommand());
                            }
                            inFromServer = 
                                    new BufferedReader(new InputStreamReader
                                            (clientSocket.getInputStream()));
                            // Les data fra USV
                            line = inFromServer.readLine();
                            if (line != null) {
                                if (!line.isEmpty()) {
                                    parseLine(line);
                                }
                            }
                            if (guiCommand == 1) {
                                if (!writing) startWriter();
                                storeDataFromUSV();
                            }
                            else stopWriter();
                            if (guiCommand == 4) {

                                pstream.println("" + 4 + " " + 0 + " " + 0 + " "
                                        + 0f + " " + 0 + " " + 0);
                                pstream.close();
                                clientSocket.close();
                                System.out.println("Socket CLOSED");
                            }
                            
                            // If travel mode
                            if(guiCommand == 5)
                            {
                                if(shouldSendRoute)
                                {
                                    pstream.println(NMEASentences);
                                    this.shouldSendRoute = false;
                                }
                            }

                            lastTime = System.currentTimeMillis();
                        }

                    }

                    isConnected = false;
                } catch (IOException ex) {
                    System.out.println("IOexeption");
                }
            }
        }
    }

    public synchronized void setGUIcommand(int i) {
        guiCommand = i;
    }

    public synchronized void setHeadingReference(float reference) {
        headingReference = reference;
    }

    public synchronized float getHeadingReference() {
        return headingReference;
    }


   /*
    Metode for parsing av datalinje fra USV
    */
    private synchronized void parseLine(String line) {

        String[] lineData = line.split(" ");
        if (lineData.length > 24) {
            // breddegrad USV
            latLonFromUSV[0] = Double.parseDouble(lineData[1]);
            // lengdegrad USV
            latLonFromUSV[1] = Double.parseDouble(lineData[3]);
            // avstand nord
            dataFromUSV[0] = Float.parseFloat(lineData[5]);
            // avstand øst
            dataFromUSV[1] = Float.parseFloat(lineData[7]);
            // peiling (grader)
            dataFromUSV[2] = Float.parseFloat(lineData[9]);
            // hastighet
            dataFromUSV[3] = Float.parseFloat(lineData[11]);
            // hastighetsretning
            dataFromUSV[4] = Float.parseFloat(lineData[13]);
            // vindhastighet (ikke i bruk)
            dataFromUSV[5] = Float.parseFloat(lineData[15]);
            // vindretning (ikke i bruk)
            dataFromUSV[6] = Float.parseFloat(lineData[17]);
            // temperatur (i kapsling)
            dataFromUSV[7] = Float.parseFloat(lineData[19]);
            // breddegrad referanse
            dataFromUSV[8] = Float.parseFloat(lineData[21]);
            // lengdegrad referanse
            dataFromUSV[9] = Float.parseFloat(lineData[23]);
            // PID gain: jaging (kp ki kd) sidevis (kp ki kd) giring (kp ki kd)

            for (int i = 0; i < 9; i++) {
                dataFromUSV[i + 10] = Float.parseFloat(lineData[i + 24]);
            }
            // kraft: X, Y, N, vinkelhastighet
            for (int i = 0; i < 4; i++) {
                dataFromUSV[i + 19] = Float.parseFloat(lineData[i + 34]);
            }
            // Lagre mottatt data
            storage.setArray(dataFromUSV, latLonFromUSV);
            int egnosEnabled = Integer.parseInt(lineData[33]);

            //System.out.println("EGNOS: " + egnosEnabled);
            // Sjekk om DGPS 
            if (egnosEnabled == 2) {
                storage.setEgnosEnabled(true);
            } else {
                storage.setEgnosEnabled(false);
            }
        }
    }

    private synchronized String getCommand() {
        // data format: gui kommando, nummer på regulator gain som skal tunes,
        // ny heading referanse til dp-kontroller, verdi på gain som skal 
        // endres, antall halvmeter referansen flyttes nordover, 
        // antall halvmeter referansen flyttes østover 
        // (-1 vil da være hhv 0,5 m sør eller vest)
        int a = incrementReferenceNorth;
        int b = incrementReferenceEast;
        incrementReferenceNorth = 0;
        incrementReferenceEast = 0;
        if (!gainChanged) {
            return guiCommand + " " + 0 + " " + headingReference + " "
                    + 0f + " " + a + " " + b;
        } else {
            // Forandret parameter i PID regulering
            gainChanged = false;
            return guiCommand + " " + gainNr + " " + headingReference + " "
                    + gainValue + " " + a + " " + b;
        }
    }
    // Les data fra joystick og send til USV
    private synchronized String getRemoteCommand() {
        double axisValues[] = reader.getAxisValues();
        return guiCommand + " " + 0 + " X-Y-Yaw: " 
                + axisValues[0] + " " + axisValues[1]
                + " " + axisValues[2];

    }

    public boolean isConnected() {
        return isConnected;
    }
    
    // Sett ny forsterkning til PID regulator
    public synchronized void gainChanged(int gainNr, float value) {
        this.gainNr = gainNr;
        gainValue = value;
        gainChanged = true;
    }

    // Flytt DP-settpunkt nord
    public synchronized void incrementNorth(int increment) {
        incrementReferenceNorth += increment;
    }
    
    // Flytt DP-settpunkt øst
    public synchronized void incrementEast(int increment) {
        incrementReferenceEast += increment;
    }
    
    public void setNMEASentences(ArrayList<String> sentences)
    {
        this.NMEASentences = sentences;
        this.shouldSendRoute = true;
    }

    int getGuiCommand() {
        return guiCommand;
    }
    
    // Lagre NED-data til fil
    private void storeDataFromUSV() {
        nedWriter.println(dataFromUSV[0] + " " + dataFromUSV[1] + " " 
                + (headingReference - dataFromUSV[2]) + " " 
                + latLonFromUSV[0] + " " + latLonFromUSV[1]);
    }

    
    // Start filskriver
    private void startWriter() {
        File log = new File("NED_Data.txt");

        try {
            log.createNewFile();
            nedWriter = new PrintWriter(new FileWriter(log, true));
            nedWriter.println(new Date().toString());
            writing = true;
        } catch (IOException ex) {
        }
    }

    // Stopp filskriver
    private void stopWriter() {
        if (nedWriter != null) {
            nedWriter.close();
            writing = false;
        }
    }
}
