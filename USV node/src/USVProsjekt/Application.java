package USVProsjekt;

import USVProsjekt.NMEAparser.GPSPosition;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.Date;
import java.util.Timer;
import org.apache.commons.io.FileUtils;

/**
 * Hovedklasse for applikasjon
 *
 * @author Albert
 */
public class Application extends Thread {

    private SerialConnection serialGPS;
    private SerialConnection serialIMU;
    private SerialConnection serialWind;
    private SerialConnection serialThrust;

    private GPSreader gps;
    private IMUreader imu;
    private WindReader windReader;
    private ThrustWriter thrustWriter;
    private GPSPositionStorageBox gpsPositionStorage;
    private GPSPosition gpsPosition;

    private double latitudeBody;
    private double longitudeBody;
    private double latitudeReference;
    private double longitudeReference;

    private double xNorth;
    private double yEast;
    private float yaw, heading, headingReference;

    private double speed;
    private double direction;

    private double windSpeed;
    private double windDirection;
    private double temperature;

    private int guiCommand;
    private Server server;

    private boolean dpStarted;
    private DynamicPositioning dynamicPositioning;
    private RemoteOperation remoteOperation;
    private double[] northEastPosition;

    private Timer timer;
    private int gainChanged;
    private float newControllerGain;
    private int northInc;
    private int eastInc;
    private float incrementAmountX;
    private float incrementAmountY;
    private double[] remoteCommand;
    private int egnos;
    private NorthEastPositionStorageBox northEastPositionStorage;
    private float yawSpeed;

    public Application(Server server) {

        xNorth = 0.0f;
        yEast = 0.0f;
        yaw = 0.0f;
        heading = 0.0f;
        speed = 0.0f;
        direction = 0.0f;
        windSpeed = 0.0f;
        windDirection = 0.0f;
        temperature = 0.0f;
        latitudeBody = 0.0f;
        longitudeBody = 0.0f;
        latitudeReference = 0.0f;
        longitudeReference = 0.0f;

        guiCommand = 0;

        headingReference = 0;
        newControllerGain = 0;
        gainChanged = 0;
        northInc = 0;
        eastInc = 0;

        remoteCommand = new double[3];

        this.server = server;
    }

    @Override
    public void run() {
        readPreviousTuningsFromFile();
        while (guiCommand != 4) {
            //************************************************
            //Lagrer verdier fra klient
            guiCommand = server.getGuiCommand();
            headingReference = server.getHeadingReference();
            if (server.isGainChanged()) {
                gainChanged = server.getGainChanged();
                newControllerGain = server.getControllerGain();
            } else {
                gainChanged = 0;
            }
            northInc = server.getNorthIncDecRequest();
            eastInc = server.getEastIncDecRequest();
            remoteCommand = server.getRemoteCommand();

            //*************************************************
            //Utfører sekundere oppgaver basert på klientønske
            //Stopper timere og resetter flagget
            if (guiCommand != 1 && dpStarted) {
                timer.cancel();
                gps.setReferencePositionOff();
                dpStarted = false;
                incrementAmountX = 0;
                incrementAmountY = 0;
                northEastPositionStorage.setPosition(new double[]{0, 0});
                float[][] a = dynamicPositioning.getAllControllerTunings();
                storeControllerTunings(a);
                dynamicPositioning = new DynamicPositioning(thrustWriter,
                        northEastPositionStorage, imu);
                dynamicPositioning.setPreviousGains(a);
                System.out.println("timer cancelled and flag reset");
            }
            //Endrer forsterkningskonstanter dersom endring er oppdaget
            if (gainChanged != 0) {
                dynamicPositioning.setNewControllerGain(gainChanged,
                        newControllerGain);
                float[][] a = dynamicPositioning.getAllControllerTunings();
                storeControllerTunings(a);
            }
            //endrer referansen dersom en eller flere av parameterene != 0
            if (northInc != 0 || eastInc != 0) {
                setIncrementAmount(northInc, eastInc);
            }

            //************************************************
            //Utfører primære oppgaver basert på klientønske
            switch (guiCommand) {
                default:
                    idle();
                    break;
                case 1:
                    dynamicPositioning();
                    break;
                case 2:
                    remoteOperation();
                    break;
            }
            server.setDataFields(getDataLine());

        }

        stopThreads();
        System.out.println("Run()-method in Application Class finished");
    }

    /**
     * Båten ligger på "tomgang"
     */
    private void idle() {
        dynamicPositioning.stopWriter();
        thrustWriter.setThrustForAll(new double[]{0d, 0d, 0d, 0d});
        thrustWriter.writeThrust();
        updateBasicFields();
    }

    /**
     * Aktiverer DP modus
     */
    private void dynamicPositioning() {
        updateAllFields();
        gps.lockReferencePosition();
        dynamicPositioning.setReferenceHeading(headingReference);
        if (!dpStarted) {
            dynamicPositioning.startWriter();
            dynamicPositioning.resetControllerErrors();//nullstill feil
            int startTime = 0;
            int periodTime = 200;
            timer = new Timer();
            timer.scheduleAtFixedRate(dynamicPositioning,
                    startTime, periodTime); //start DP på konstant intervall
            dpStarted = true;
        }
    }

    /**
     * Fjernstyringsmodus
     */
    private void remoteOperation() {
        dynamicPositioning.stopWriter();
        updateBasicFields();
        remoteOperation.remoteOperate(remoteCommand);
    }

    /**
     * Oppdaterer variabler for alminnelige felt
     */
    private void updateBasicFields() {
        if (gpsPositionStorage.isNewPosition()) {
            gpsPosition = gpsPositionStorage.getPosition();
        }
        latitudeBody = gpsPosition.lat;
        longitudeBody = gpsPosition.lon;
        windSpeed = windReader.getWindSpeed();
        windDirection = windReader.getWindDirection();
        temperature = windReader.getTemperature();
        heading = imu.getHeading();
        yawSpeed = imu.getYawSpeedValue();
        speed = gpsPosition.velocity;
        direction = gpsPosition.dir;
        egnos = gpsPosition.quality;
        latitudeReference = 0;
        longitudeReference = 0;
        xNorth = 0;
        yEast = 0;
        yaw = 0;
    }

    /**
     * Oppdaterer alle felt
     */
    private void updateAllFields() {
        updateBasicFields();
        if (northEastPositionStorage.isNewPosition()) {
            northEastPosition = northEastPositionStorage.getPosition();
        }
        latitudeReference = gps.getLatRef();
        longitudeReference = gps.getLonRef();
        xNorth = northEastPosition[0] + incrementAmountX;
        yEast = northEastPosition[1] + incrementAmountY;
        yaw = imu.getYawValue();

    }

    /**
     * Initialiserer applikasjonen
     */
    public void initializeApplication() {
        boolean windows = System.getProperty("os.name").contains("Windows");
        String comPortGPS;
        String comPortIMU;
        String comPortWind;
        String comPortThrust;
        //communication parameters
        if (windows) {
            comPortGPS = "COM4";
            comPortIMU = "COM5";
            comPortWind = "COM6";
            comPortThrust = "COM7";
        } else {
            comPortGPS = "ttyACM0";
            comPortIMU = "ttyACM1";
            comPortWind = "ttyACM3";
            comPortThrust = "ttyACM2";
        }
        int baudRateGPS = 115200;

        int baudRateIMU = 57600;

        int baudRateWind = 57600;

        int baudRateThrust = 115200;

        //One serial connection for each sensor/port
        serialGPS = new SerialConnection(comPortGPS,
                baudRateGPS);

        serialIMU = new SerialConnection(comPortIMU,
                baudRateIMU);

        serialWind = new SerialConnection(comPortWind,
                baudRateWind);

        serialThrust = new SerialConnection(comPortThrust,
                baudRateThrust);

        northEastPositionStorage = new NorthEastPositionStorageBox();
        // Create and start threads
        gpsPositionStorage = new GPSPositionStorageBox();
        gps = new GPSreader(serialGPS, Identifier.GPS,
                northEastPositionStorage);
        // Set gps position storage box, and initialize with values
        gps.setStorageBox(gpsPositionStorage);
        gps.connectToSerialPortAndDisplayGPSInfo();
        gps.setName("GPS Reader Thread");
        gps.start();

        northEastPosition = northEastPositionStorage.getPosition();

        gpsPosition = gpsPositionStorage.getPosition();

        imu = new IMUreader(serialIMU, Identifier.IMU);
        imu.connectToSerialPortAndDisplayIMUInfo();
        imu.setName("IMU Reader Thread");
        imu.start();

        windReader = new WindReader(serialWind, Identifier.WIND);
        windReader.connectToSerialPortAndDisplayWindInfo();
        windReader.setName("Wind Reader Thread");
        windReader.start();

        thrustWriter = new ThrustWriter(serialThrust, Identifier.THRUSTERS);
        dynamicPositioning = new DynamicPositioning(thrustWriter,
                northEastPositionStorage, imu);
        remoteOperation = new RemoteOperation(thrustWriter);
    }

    public static void main(String[] args) throws Exception {
        ServerSocket ssocket = new ServerSocket(2345);
        //While true sørger for at vi kan koble til og koble fra som ønsket
        while (true) {
            Server server = new Server(ssocket);
            //denne metoden blokker til forbindelse
            //er opprettet
            server.acceptConnection();
            Application app = new Application(server);
            app.initializeApplication();
            server.start();
            app.start();
        }
    }

    /**
     * Stringen med data som skal sendes til klienten
     *
     * @return
     */
    private String getDataLine() {
        float[][] a = dynamicPositioning.getAllControllerTunings();
        float[] vector = dynamicPositioning.getPIDOutputVector();
        return "Latitude: " + latitudeBody + " Longitude: "
                + longitudeBody + " xNorth: " + xNorth + " Sway: " + yEast
                + " Heading: " + heading + " Speed: " + speed + " Direction: "
                + direction + " WindSpeed: " + windSpeed
                + " WindDirection: " + windDirection
                + " Temperature: " + temperature + " LatRef: "
                + latitudeReference + " LonRef: " + longitudeReference + " "
                + a[0][0] + " " + a[0][1] + " " + a[0][2] + " "
                + a[1][0] + " " + a[1][1] + " " + a[1][2] + " "
                + a[2][0] + " " + a[2][1] + " " + a[2][2] + " "
                + egnos + " " + vector[0] + " " + vector[1] + " " + vector[2]
                + " " + yawSpeed;
    }

    /**
     * Lar alle trådene gå ut av run()
     */
    private void stopThreads() {
        gps.stopThread();
        imu.stopThread();
        windReader.stopThread();
        thrustWriter.closeSerialConn();
        server.stopThread();
    }

    /**
     * Øker inkrement i referanse
     *
     * @param northInc
     * @param eastInc
     */
    private void setIncrementAmount(int northInc, int eastInc) {
        incrementAmountX -= northInc / 2.0f;
        incrementAmountY -= eastInc / 2.0f;
        dynamicPositioning.setIncrementAmount(northInc, eastInc);
    }

    /**
     * Lagrer alle forsterkningskonstanter slik de kan brukes neste gang appen
     * starter
     *
     * @param a
     */
    private void storeControllerTunings(float[][] a) {
        File log = new File("PIDControllerTunings.txt");
        try {
            System.out.println("PID-tunings files created.");
            log.createNewFile();
            PrintWriter out = new PrintWriter(new FileWriter(log, false));

            out.println(a[0][0] + " " + a[0][1] + " " + a[0][2] + " "
                    + a[1][0] + " " + a[1][1] + " " + a[1][2] + " "
                    + a[2][0] + " " + a[2][1] + " " + a[2][2] + " ");
            out.println("Tunings stored at " + new Date().toString() + " by "
                    + System.getProperty("user.name"));
            out.close();
        } catch (IOException e) {
            System.out.println("COULD NOT LOG!!");
        }
    }

    private void readPreviousTuningsFromFile() {
        try {
            String s = FileUtils.readFileToString(
                    new File(System.getProperty("user.dir") 
                            + "//PIDControllerTunings.txt"));

            String d[] = s.split(" ");
            float a[][] = new float[][]{{Float.parseFloat(d[0]),
                Float.parseFloat(d[1]), Float.parseFloat(d[2])},
            {Float.parseFloat(d[3]), Float.parseFloat(d[4]),
                Float.parseFloat(d[5])},
            {Float.parseFloat(d[6]), Float.parseFloat(d[7]),
                Float.parseFloat(d[8])}};
            dynamicPositioning.setPreviousGains(a);
        } catch (IOException ex) {
            System.out.println("Exception readfile)");
        }

    }

}
