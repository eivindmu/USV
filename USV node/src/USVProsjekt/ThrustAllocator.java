/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package USVProsjekt;

import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.LinearMultivariateRealFunction;
import com.joptimizer.functions.PDQuadraticMultivariateRealFunction;
import com.joptimizer.optimizers.JOptimizer;
import com.joptimizer.optimizers.OptimizationRequest;
import com.joptimizer.optimizers.OptimizationResponse;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author vegard
 * 
 */
public class ThrustAllocator {

    private double[][] Phi;
    private double[][] A1;
    private BlockRealMatrix A2;
    private BlockRealMatrix C2;
    private ArrayRealVector p;
    private PDQuadraticMultivariateRealFunction objectiveFunction;
    ConvexMultivariateRealFunction[] inequalities;
    private JOptimizer opt;
    // NOTE: Lx1 er negativ, Lx2 er positiv, Ly1 er negativ, Ly2 er positiv
    public ThrustAllocator(double Lx1, double Lx2, double Ly1, double Ly2) {
        setUp(Lx1, Lx2, Ly1, Ly2);
    }
    /**
     * Konstruktør for Pioner 8 mini
     */
    public ThrustAllocator(){
        setUp(-0.8, 0.87, -0.2, 0.2);
    }

    /*
    Sett opp matriser og vektorer
    */
    private void setUp(double Lx1, double Lx2, double Ly1, double Ly2) {
        Phi = new double[][]{{1., 0., 0., 0., 0., 0., 0.}, {0., 1., 0., 0., 0., 0., 0.,},
        {0., 0., 1., 0., 0., 0., 0.}, {0., 0., 0., 1., 0., 0., 0.}, {0., 0., 0., 0., 10., 0., 0.},
        {0., 0., 0., 0., 0., 10., 0.}, {0., 0., 0., 0., 0., 0., 10.}};

        A1 = new double[][]{{1., 1., 0., 0., -1., 0., 0.}, {0., 0., -1., -1., 0., -1., 0.},
        {-Ly1, -Ly2, -Lx1, -Lx2, 0., 0., -1.}};

        A2 = new BlockRealMatrix(new double[][]{{-1., 0., 0., 0., 0., 0., 0.}, {0., -1., 0., 0., 0., 0., 0.},
        {0., 0., -1., 0., 0., 0., 0.}, {0., 0., 0., -1., 0., 0., 0.}, {1., 0., 0., 0., 0., 0., 0.},
        {0., 1., 0., 0., 0., 0., 0.}, {0., 0., 1., 0., 0., 0., 0.}, {0., 0., 0., 1., 0., 0., 0.}});
        
        C2 = new BlockRealMatrix(new double[][]{{0., 0., 0., -1., 0., 0., 0., 0., 0., 0., 0.}, {0., 0., 0., 0., -1., 0., 0., 0., 0., 0., 0.},
        {0., 0., 0., 0., 0., -1., 0., 0., 0., 0., 0.}, {0., 0., 0., 0., 0., 0., -1., 0., 0., 0., 0.},
        {0., 0., 0., 0., 0., 0., 0., 1., 0., 0., 0.}, {0., 0., 0., 0., 0., 0., 0., 0., 1., 0., 0.},
        {0., 0., 0., 0., 0., 0., 0., 0., 0., 1., 0.}, {0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 1.}});

        p = new ArrayRealVector(new double[]{0., 0., 0., -29., -29., -29., -29., 34., 34., 34., 34.});

        objectiveFunction = new PDQuadraticMultivariateRealFunction(Phi, null, 0);
        inequalities = new ConvexMultivariateRealFunction[8];
        opt = new JOptimizer();
    }

    /*
    Beregn output basert på en ønsket kraftvektor
    */
    public double[] calculateOutput(double[] tau) throws Exception {
//        long time1 = System.currentTimeMillis();
        // Sett opp p-vektoren med ønskede verdier for tau (kraftvektor)
        p.setEntry(0, tau[0]);
        p.setEntry(1, tau[1]);
        p.setEntry(2, tau[2]);
 

        // Oppsett for ulikheten A2*z <= C2*p
        RealVector v2 = C2.operate(p);
        

        for (int i = 0; i < 8; i++) {
            // Hver ulikhet settes opp som et LinearMultivariateRealFunction
            inequalities[i] = new LinearMultivariateRealFunction(A2.getRow(i), -v2.toArray()[i]);
        }
        // Optimaliseringsproblemet
        OptimizationRequest or = new OptimizationRequest();
        // Sett objektivfunksjonen
        or.setF0(objectiveFunction);
        
        // Sett ulikheten
        or.setFi(inequalities);
        
        // Sett likheten
        or.setA(A1);
        or.setB(p.getSubVector(0, 3).toArray());

        // Sett toleransen på resultatet. Lavere tall = større nøyaktighet
        or.setTolerance(1.E-1);

        // Optimalisering
       
        opt.setOptimizationRequest(or);
        int returnCode = opt.optimize();
        
        if (returnCode == OptimizationResponse.FAILED) {
            System.out.println("Optimization FAIL");
        }
//        long time2 = System.currentTimeMillis()-time1;
        //System.out.println("Tidsbruk = " + time2);
        return opt.getOptimizationResponse().getSolution();

    }
}
