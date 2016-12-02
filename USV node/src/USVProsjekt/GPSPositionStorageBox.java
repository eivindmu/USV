package USVProsjekt;

import USVProsjekt.NMEAparser.GPSPosition;

/**
 * Lagrer geodetisk posisjon, delt objekt
 *
 * @author vegard
 */
public class GPSPositionStorageBox {

    private GPSPosition position;
    private boolean newPosition;

    public GPSPositionStorageBox() {
        newPosition = true;
    }

    /**
     * getter for posisjonsobjektet
     *
     * @return
     */
    public synchronized GPSPosition getPosition() {
        newPosition = false;
        return position;
    }

    /**
     * setter for posisjonsobjektet
     *
     * @param position
     */
    public synchronized void setPosition(GPSPosition position) {
        this.position = position;
        newPosition = true;
    }

    /**
     * flag for å sjekke om ny posisjon er satt
     *
     * @return
     */
    public synchronized boolean isNewPosition() {
        return newPosition;
    }

}
