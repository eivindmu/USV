package USVProsjekt;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * Klasse for å opprette rotasjonsmatrise mellom BODY og NED
 *
 * @author Albert
 */
public class RotationMatrix {

    private BlockRealMatrix Rz;
    private double[][] raw;
    ArrayRealVector vector;

    public RotationMatrix(float headingDegrees) {
        float headingRadians = headingDegrees * (float) Math.PI / 180.0f;
        raw = new double[][]{
            {c(headingRadians), -s(headingRadians), 0},
            {s(headingRadians), c(headingRadians), 0},
            {0, 0, 1}};
        Rz = new BlockRealMatrix(raw);
    }

    /**
     * transponerer rotasjonsmatrisen og multipliserer den med input vektor
     *
     * @param u
     * @param v
     * @param w
     * @return
     */
    public double[] multiplyRzwithV(float u, float v, float w) {
        vector = new ArrayRealVector(new double[]{u, v, w});
        //Rz'*returnVector 3x3  * 3x1 = 3x1
        RealVector returnVector = Rz.transpose().operate(vector);
        return returnVector.toArray();
    }

    /**
     * cosinus funskjon
     *
     * @param radians
     * @return
     */
    private float c(float radians) {
        return (float) Math.cos(radians);
    }

    /**
     * sinusfunksjon
     *
     * @param radians
     * @return
     */
    private float s(float radians) {
        return (float) Math.sin(radians);
    }
}
