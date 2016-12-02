package USVProsjekt;

/**
 *
 * @author root
 */
public class ThrustWriter {

    private int pulseWidth1;
    private int pulseWidth2;
    private int pulseWidth3;
    private int pulseWidth4;

    private SerialConnection serialConnection;
    private Identifier ID;

    /**
     * Klasse for å konvertere newton til pwm og skrive verdier
     *
     * @param serialConnection
     * @param ID
     */
    public ThrustWriter(SerialConnection serialConnection, Identifier ID) {
        pulseWidth1 = 1500;
        pulseWidth2 = 1500;
        pulseWidth3 = 1500;
        pulseWidth4 = 1500;

        this.serialConnection = serialConnection;

        this.ID = ID;
        this.serialConnection.connect(this.ID);
    }

    /**
     * skriver verdier via SerialConnection
     */
    public void writeThrust() {
        if (serialConnection.isConnected()) {
            serialConnection.writeThrustMicros(pulseWidth1, pulseWidth2, pulseWidth3, pulseWidth4);
        }
    }

    /**
     * setter pwm variablene
     *
     * @param newton
     */
    public void setThrustForAll(double[] newton) {
        pulseWidth1 = newtonToPulseWidth(newton[0]);
        pulseWidth2 = newtonToPulseWidth(newton[1]);
        pulseWidth3 = newtonToPulseWidth(newton[2]);
        pulseWidth4 = newtonToPulseWidth(newton[3]);
    }

    /**
     * konverterer newton til pwm
     *
     * @param xNewton
     * @return
     */
    public int newtonToPulseWidth(double xNewton) {
        double x = xNewton / 9.81; //From Newton to kgF because of regression

        int pulseWidth = 1500;

        if (xNewton > 0.0f) {
            double pulseWidthFloat = 6.8044878418 * x * x * x - 45.9428509628 * x * x + 183.3806624031 * x + 1528.9249065579;
            pulseWidth = (int) pulseWidthFloat;
        } else if (xNewton <= 0.0f) {

            double pulseWidthFloat = 8.1266850947 * x * x * x + 53.0777818795 * x * x + 206.7802157499 * x + 1466.368281074;
            pulseWidth = (int) pulseWidthFloat;
        } else {
            pulseWidth = 1500;
        }

        if (pulseWidth < 1100) {
            pulseWidth = 1100;
        }
        if (pulseWidth > 1900) {
            pulseWidth = 1900;
        }
        return pulseWidth;
    }

    /**
     * lukker seriell forbindelsen
     */
    void closeSerialConn() {
        serialConnection.close();
        System.out.println("ThrustWriter: Connection closed");
    }

}
