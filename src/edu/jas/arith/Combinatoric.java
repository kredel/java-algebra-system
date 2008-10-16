/*
 * $Id$
 */

package edu.jas.arith;

//import java.util.Random;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;


/**
 * Combinatoric algorithms.
 * Similar to ALDES/SAC2 SACCOMB module.
 * @author Heinz Kredel
 */

public class Combinatoric {


    /** Integer binomial coefficient induction.  
     * n and k are beta-integers with 0 less than 
     * or equal to k less than or equal to n.  A is the
     * binomial coefficient n over k.  B is the binomial coefficient n
     * over k+1.
     */
    public static BigInteger binCoeffInduction(BigInteger A, int n, int k) {
        BigInteger B;
        BigInteger kp, np;
        np = new BigInteger(n-k);
        kp = new BigInteger(k+1);
        B = A.multiply(np).divide(kp);
        return B;
    }


    /** Integer binomial coefficient.  
     * n and k are beta-integers with 0 less than 
     * or equal to k less than or equal to n.  
     * A is the binomial coefficient n over k.
     */

    public static BigInteger binCoeff(int n, int k) {
        BigInteger A;
        int kp;
        kp = ( k < n-k ? k : n-k ); 
        A = BigInteger.ONE;
        for (int j = 0; j < k; j++) {
            A = binCoeffInduction( A, n, j );
        }
        return A;
    }


    /** Integer binomial coefficient partial sum.  
     * n and k are integers, 0 le k le n.  
     * A is the sum on i, from 0 to k, of the
     * binomial coefficient n over i.
     */

    public static BigInteger binCoeffSum(int n, int k) {
        BigInteger B, S;
        S = BigInteger.ONE;
        B = BigInteger.ONE;
        for (int j = 0; j < k; j++) {
            B = binCoeffInduction( B, n, j );
            S = S.sum( B );
        }
        return S;
    }

}
