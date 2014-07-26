/*
 * $Id$
 */

package edu.jas.commons.math;


import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

import edu.jas.arith.BigDecimal;
import edu.jas.arith.Rational;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.ps.UnivPowerSeries;
import edu.jas.ps.UnivPowerSeriesRing;
import edu.jas.structure.RingElem;


/**
 * Algorithms related to polynomial roots. Conversion to commons-math classes
 * and delegation to commons-math algorithms.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public class Roots<C extends RingElem<C> & Rational> {


    /**
     * Complex root approximation using companion matrix.
     * @param a squarefree univariate polynomial
     * @return list of approximations of complex roots of the polynomial
     */
    public List<Complex<BigDecimal>> complexRoots(GenPolynomial<C> a) {
        List<Complex<BigDecimal>> r = new ArrayList<Complex<BigDecimal>>();
        ComplexRing<BigDecimal> cr = new ComplexRing<BigDecimal>(new BigDecimal(0.0, MathContext.DECIMAL64));
        Complex<BigDecimal> cc = new Complex<BigDecimal>(cr);

        if (a == null || a.isZERO()) {
            r.add(cc);
            return r;
        }
        if (a.isConstant()) {
            return r;
        }
        a = a.monic();
        UnivPowerSeriesRing<C> pr = new UnivPowerSeriesRing<C>(a.ring);
        UnivPowerSeries<C> ps = pr.fromPolynomial(a);

        // Construct the companion matrix
        int N = (int) a.degree();
        RealMatrix A = new Array2DRowRealMatrix(N, N);
        for (int i = 0; i < N; i++) {
            A.setEntry(i, N - 1, -ps.coefficient(i).getRational().doubleValue());
        }
        for (int i = 1; i < N; i++) {
            A.setEntry(i, i - 1, 1.0);
        }
        //System.out.println("A = " + A);

        // compute eigenvalues
        EigenDecomposition ed = new EigenDecomposition(A);
        double[] realValues = ed.getRealEigenvalues();
        double[] imagValues = ed.getImagEigenvalues();

        //RealMatrix V = ed.getV();
        //System.out.println("V = " + V);
        //RealMatrix D = ed.getD();
        //System.out.println("D = " + D); 

        // construct root list
        for (int i = 0; i < N; i++) {
            cc = new Complex<BigDecimal>(cr, new BigDecimal(realValues[i], MathContext.DECIMAL64),
                            new BigDecimal(imagValues[i], MathContext.DECIMAL64));
            //System.out.println("cc = " + cc + ", re = " + realValues[i] + ", im = " + imagValues[i]);
            r.add(cc);
        }
        return r;
    }

}
