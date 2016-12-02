package USVProsjekt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author vegard
 */
public class Server extends Thread {

    private Socket csocket;
    private PrintStream printStream;
    private ServerSocket ssocket;
    private int guiCommand;
    private String data;
    private float headingReference;
    private double[] remoteCommand;
    private float controllerGain;
    private int id;
    private int northIncRequest;
    private int eastIncRequest;
    private boolean gainChanged;
    private boolean available;
    private boolean stop;

    public Server(ServerSocket ssocket) {

        remoteCommand = new double[3];
        gainChanged = false;
        stop = false;
        this.ssocket = ssocket;
    }

    public void acceptConnection() throws IOException {
        System.out.println("SERVER LISTENING");
        csocket = ssocket.accept();
        System.out.println("SERVER ACCEPTED");
    }

    public synchronized int getGuiCommand() {
        return guiCommand;
    }

    public synchronized boolean isGainChanged() {
        return gainChanged;
    }

    private synchronized void setGuiCommand(int guiCommand) {
        this.guiCommand = guiCommand;
    }

    public synchronized float getHeadingReference() {
        return headingReference;
    }

    private synchronized void setHeadingReference(float headingReference) {
        this.headingReference = headingReference;
    }

    public synchronized float getControllerGain() {
        gainChanged = false;
        return controllerGain;
    }

    public synchronized void setControllerGain(int id, float controllerGain) {
        if (id != 0) {
            gainChanged = true;
            this.id = id;
            this.controllerGain = controllerGain;
        }
    }

    public synchronized int getGainChanged() {
        return id;
    }

    public synchronized int getNorthIncDecRequest() {
        return northIncRequest;
    }

    public synchronized int getEastIncDecRequest() {
        return eastIncRequest;
    }

    public synchronized void setNorthIncDecRequest(int request) {
        northIncRequest = request;
    }

    public synchronized void setEastIncDecRequest(int request) {
        eastIncRequest = request;
    }

    private void setRemoteCommand(String[] lineData) {
        for (int i = 0; i < 3; i++) {
            remoteCommand[i] = Double.parseDouble(lineData[i + 3]);
        }
    }

    public synchronized double[] getRemoteCommand() {

        return remoteCommand;
    }


    @Override
    public void run() {
        try {
            printStream = new PrintStream(csocket.getOutputStream(), true);
            while (csocket.isConnected() && !stop) {
                BufferedReader r = new BufferedReader(
                        new InputStreamReader(csocket.getInputStream()));
                String line = r.readLine();//linjen for mottatt data
                String[] lineData;
                // System.out.println(line);
                if (line !=null && !line.isEmpty()) {
                    //System.out.println("Server received data");
                   lineData = line.split(" ");
                    setGuiCommand(Integer.parseInt(lineData[0]));
                    if (guiCommand == 2) {
                        //setter denne linjen dersom det er fjernstyring
                        setRemoteCommand(lineData);
                    } else {
                        //setter variablene i synkroniserte metoder
                        setHeadingReference(Float.parseFloat(lineData[2]));
                        setControllerGain(Integer.parseInt(lineData[1]), 
                                Float.parseFloat(lineData[3]));
                        setNorthIncDecRequest(Integer.parseInt(lineData[4]));
                        setEastIncDecRequest(Integer.parseInt(lineData[5]));
                    }
                    
                }
                //returnerer data til klient
                printStream.println(getDataFields());
            }
            printStream.close();
            csocket.close();
            System.out.println("Server thread run() exit:");
        } catch (IOException ex) {
            System.out.println("IO ex server");

        }
    }
/**
 * setter linjen som skal sendes
 * @param data 
 */
    public synchronized void setDataFields(String data) {
        //hindret for mye inkrementering ved ett klikk i GUI
        while (available) {
            try {
                wait();
            } catch (InterruptedException ex) {
            }
        }
        notifyAll();
        available = true;
        this.data = data;
    }
/**
 * henter linjen med data som skal sendes
 * @return 
 */
    private synchronized String getDataFields() {
        while (!available) {
            try {
                wait();
            } catch (InterruptedException ex) {
            }
        }
        notifyAll();
        available = false;
        return data;
    }

    public void stopThread() {
        stop = true;
    }

    public boolean isClosed() {
        return csocket.isClosed();
    }
}
