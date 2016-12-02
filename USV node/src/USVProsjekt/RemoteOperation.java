package USVProsjekt;

/**
 * Klasse som kontrollerer fjernstyring
 *
 * @author Albert
 */
public class RemoteOperation {

    private ThrustAllocator thrustAllocator;
    private ThrustWriter thrustWrite;

    public RemoteOperation(ThrustWriter thrustWriter) {
        thrustAllocator = new ThrustAllocator();
        this.thrustWrite = thrustWriter;
    }

    /**
     * Metode for styring
     *
     * @param remoteCommand
     */
    public void remoteOperate(double[] remoteCommand) {
        try {
            thrustWrite.setThrustForAll(
                    thrustAllocator.calculateOutput(remoteCommand));
            thrustWrite.writeThrust();
        } catch (Exception ex) {
            System.out.println("exception ro");
        }
    }
}
