package USVProsjekt;

/**
 * Klasse for å lese wind, temperatur og trykk sensorer
 *
 * @author root
 */
public class WindReader extends Thread {

    private float windSpeed;
    private float windDirection;
    private float temperature;

    private SerialConnection serialConnection;
    private Identifier ID;
    private int initPeriod;
    private boolean stop;
    private float airPressurehPa;

    public WindReader(SerialConnection serialConnection, Identifier ID) {
        windSpeed = 0.0f;
        windDirection = 0.0f;
        temperature = 0.0f;
        this.serialConnection = serialConnection;
        this.ID = ID;
        stop = false;
    }

    @Override
    public void run() {
        String line;
        //unngår korrupte seriell setninger
        while (initPeriod < 5 && serialConnection.isConnected()) {
            line = serialConnection.getSerialLine();
            initPeriod++;
        }
        while (serialConnection.isConnected() && !stop) {
            line = serialConnection.getSerialLine();
            parseWindSerialSentence(line);
        }
        serialConnection.close();
        System.out.println("Connection lost/closed on Thread:"
                + " " + this.getName());
    }

    /**
     * kobler til sensor/mikrokontroller og lagrer data
     */
    public void connectToSerialPortAndDisplayWindInfo() {
        serialConnection.connectAndListen(ID);
    }

    /**
     * henter data fra string setningen
     *
     * @param line
     */
    private void parseWindSerialSentence(String line) {
        if (line.startsWith("&")) {
            String[] lineData = line.split(" ");
            windSpeed = Float.parseFloat(lineData[2]);
            windDirection = Float.parseFloat(lineData[5]);
            temperature = Float.parseFloat(lineData[7]);
            airPressurehPa = Float.parseFloat(lineData[9]);
        }
    }

    /**
     * getter for vind fart
     *
     * @return
     */
    public float getWindSpeed() {
        return windSpeed;
    }

    /**
     * getter for vindretning
     *
     * @return
     */
    public float getWindDirection() {
        return windDirection;
    }

    /**
     * getter for temperatur
     *
     * @return
     */
    public float getTemperature() {
        return temperature;
    }

    /**
     * stopper tråden/går ut av run()
     */
    void stopThread() {
        stop = true;
    }

}
