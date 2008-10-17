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


    /** Integer n-th root.  
     * R is the n-th root of A.
     */

    public static BigInteger root(BigInteger A, int n) {
        if ( n == 1 ) {
            return A;
        }
        if ( n == 2 ) {
            return sqrt(A);
        }
        if ( n < 1 ) {
            throw new RuntimeException("negative root not defined");
        }
        return A;
    }


    /** Integer square root.  
     * R is the square root of A.
     */
    public static BigInteger sqrt(BigInteger A) {
        int s = A.signum();
        if ( s < 0 ) {
            throw new RuntimeException("root of negative not defined");
        }
        if ( s == 0 ) {
            return A;
        }
        BigInteger R, R1, d;
        int log2 = A.val.bitLength();
        //System.out.println("A = " + A + ", log2 = " + log2);
        int rootlog2 = log2 - log2 / 2;
        R = new BigInteger( A.val.shiftRight(rootlog2) );
        //System.out.println("R = " + R + ", rootlog2 = " + rootlog2);
        d = R;
        while ( !d.isZERO() ) {
            d = new BigInteger( d.val.shiftRight(1) ); // div 2
            R1 = R.sum(d);
            s = A.compareTo( R1.multiply(R1) );
            if ( s == 0 ) {
                return R1;
            }
            if ( s > 0 ) {
                R = R1;
            }
            //System.out.println("R1 = " + R1);
            //System.out.println("d  = " + d);
        }
        while ( true ) {
            R1 = R.sum( BigInteger.ONE );
            System.out.println("R1 = " + R1);
            s = A.compareTo( R1.multiply(R1) );
            if ( s == 0 ) {
                return R1;
            }
            if ( s > 0 ) {
                R = R1;
            }
            if ( s < 0 ) {
                return R;
            }
        }
        //return R;
    }


}
