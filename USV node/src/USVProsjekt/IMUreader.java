package USVProsjekt;

/**
 *
 * @author Albert
 */
public class IMUreader extends Thread {

    //IMU/Compass variables
    private float yaw;
    private float pitch;
    private float roll;
    private float yawSpeed;

    private Identifier ID;
    private final SerialConnection serialConnection;
    private int initPeriod;
    private boolean stop;

    public IMUreader(SerialConnection serialConnection, Identifier ID) {
        this.serialConnection = serialConnection;
        this.ID = ID;
        yaw = 0.0f;
        pitch = 0.0f;
        roll = 0.0f;
        yawSpeed = 0.0f;
        stop = false;

    }

    @Override
    public void run() {
        String line;
        float[] magnData;

        while (initPeriod < 5 && serialConnection.isConnected()) {
            line = serialConnection.getSerialLine();
            initPeriod++;
        }
        while (serialConnection.isConnected() && !stop) {
            line = serialConnection.getSerialLine();
            setYawValue(parseReceivedIMUline(line));
        }
        System.out.println("Connection lost/closed on Thread: "
                + this.getName());
        serialConnection.close();
    }

    /**
     * Endrer yaw fra -180 til 180 til 0 -360
     *
     * @param yaw
     * @return
     */
    private float getHeadingFromYawValue(float yaw) {
        if (yaw >= 90 && yaw <= 180) {
            return yaw - 90;
        }
        if (yaw >= -180 && yaw <= 90) {
            return yaw + 270;
        } else {
            return yaw;
        }
    }

    //yaw:     (-179.9) - 0    //yaw:     0 - 180
    //heading:  0 - 179.9     //heading: 180 - 359.9
    public void connectToSerialPortAndDisplayIMUInfo() {
        serialConnection.connectAndListen(ID);
    }

    /**
     * henter data fra mottatt setning
     *
     * @param line
     * @return
     */
    private float parseReceivedIMUline(String line) {
        float yw;
        if (line.startsWith("#")) {
            String[] lineData = line.split(",");
            if (lineData.length >= 3) {
                String xString = lineData[0].substring(5);
                yw = Float.parseFloat(xString);
                // Derivering for å finne rotasjonshastighet
                setYawSpeedValue((yw - yaw) / 0.1f);
                return yw;
            }
        }
        setYawSpeedValue(0.0f);
        return yaw;
    }

    /**
     * returnere yaw verdi
     *
     * @return
     */
    public synchronized float getYawValue() {
        return yaw;
    }

    /**
     * returnerer yaw hastighet
     *
     * @return
     */
    public synchronized float getYawSpeedValue() {
        return yawSpeed;
    }

    /**
     * lagrer yaw hastighets verdi
     */
    private synchronized void setYawSpeedValue(float yawSpeed) {
        this.yawSpeed = yawSpeed;
    }

    public synchronized void setYawValue(float yaw) {
        this.yaw = yaw;
    }

    public synchronized float getHeading() {
        return getHeadingFromYawValue(yaw);
    }

    public float getPitchAngle() {
        return pitch;
    }

    public float getRollAngle() {
        return roll;
    }

    void stopThread() {
        stop = true;
    }

}
