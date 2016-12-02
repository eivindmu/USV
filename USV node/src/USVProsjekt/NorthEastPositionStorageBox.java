package USVProsjekt;

/**
 *Lagringsboks for Nord og Øst posisjon, delt mellom objekter
 * @author vegard
 */
public class NorthEastPositionStorageBox {
    private double[] position;
    private boolean newPosition;
    
    public NorthEastPositionStorageBox() {
        position = new double[2];
        newPosition = true;
    }
    /**
     * setter posisjonen
     * @param position 
     */
    public synchronized void setPosition(double[] position) {
        this.position = position;
        newPosition = true;
    }
    /**
     * henter posisjoner
     * @return 
     */
    public synchronized double[] getPosition() {
        newPosition = false;
        return position;
    }
    /**
     * flag for å sjekke om posisjon er blitt satt
     * @return 
     */
    public synchronized boolean isNewPosition() {
        return newPosition;
    }
}
