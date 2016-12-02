package USVProsjekt;

/**
 *
 * @author Albert
 */
public class PIDController {
//
    //IO Variables

    private float outputVariable;

    //Gains
    private float Kp;
    private float Ki;
    private float Kd;

    //Class variables
    private float integralTerm;
    private float lastError;
    private final float cycleTimeInSeconds;

    private float maxOutput;
    private float minOutput;

    public PIDController() {
        outputVariable = 0.0f;
        integralTerm = 0.0f;
        lastError = 0.0f;

        maxOutput = 2 * 34.0f;//Maks output jaging og tverrskipss
        minOutput = 2 * -29.0f;//Minimum output jaging og tverrskips

        Kp = 1.0f;
        Ki = 0.0f;
        Kd = 0.0f;
        cycleTimeInSeconds = 0.2f;
    }

    /**
     * Beregner output for regulatoren
     *
     * @param newInput
     * @param referenceVariable
     * @param continuous
     * @return
     */
    public float computeOutput(float newInput, float referenceVariable,
            boolean continuous) {

        float error = referenceVariable - newInput;

        // Dersom kontinuerlig kan wrap around
        if (continuous) {
            maxOutput = 121.0f; //torque(yaw)
            minOutput = -121.0f; //torque(yaw)
            if (Math.abs(error) > 180) {
                if (error > 0) {
                    error = error - 360.0f;
                } else {
                    error = error + 360.0f;
                }
            }
        }
        // Integrator anti-windup og integrator ledd
        if ((integralTerm + error * cycleTimeInSeconds * Ki) < maxOutput
                && (integralTerm + error * cycleTimeInSeconds * Ki)
                > minOutput) {
            integralTerm += Ki * error * cycleTimeInSeconds;
        }
        float dError = (error - lastError) / cycleTimeInSeconds;
        //Beregn PID Output
        outputVariable = Kp * error + integralTerm + Kd * dError;
        limitOutputVariable();
        lastError = error;
        return outputVariable;

    }

    /**
     * resetter feil
     */
    public void resetErrors() {
        integralTerm = 0;
        lastError = 0;
    }

    /**
     * setter forsterkningskonstant for denne regulatoren
     *
     * @param Kp
     * @param Ki
     * @param Kd
     */
    public void setTunings(float Kp, float Ki, float Kd) {
        this.Kp = Kp;
        this.Ki = Ki;
        this.Kd = Kd;
    }

    /**
     * returener forsterkningskonstantene for denne regulatoren
     *
     * @return
     */
    public float[] getTunings() {
        return new float[]{Kp, Ki, Kd};
    }

    /**
     * setter forsterkningskonstantene for vdenne regulatoren
     *
     * @param gainChanged
     * @param newControllerGain
     */
    void setGain(int gainChanged, float newControllerGain) {
        switch (gainChanged) {
            case 1:
            case 4:
            case 7:
                Kp = newControllerGain;
                break;
            case 2:
            case 5:
            case 8:
                Ki = newControllerGain;
                break;
            case 3:
            case 6:
            case 9:
                Kd = newControllerGain;
                break;
        }
    }

    /**
     * begrenser output variablene
     */
    private void limitOutputVariable() {
        if (outputVariable > maxOutput) {
            outputVariable = maxOutput;
        } else if (outputVariable < minOutput) {
            outputVariable = minOutput;
        }
    }

}
