/*
 * $Id$
 */

package edu.jas.arith;

//import java.util.Random;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import java.math.MathContext;


import edu.jas.structure.Power;


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
     * @param A previous induction result.
     * @param n long.
     * @param k long.
     * @return the binomial coefficient n over k+1.
     */
    public static BigInteger binCoeffInduction(BigInteger A, long n, long k) {
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
     * @param n long.
     * @param k long.
     * @return the binomial coefficient n over k+1.
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
     * @param n long.
     * @param k long.
     * @return the binomial coefficient partial sum n over i.
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
     * Uses BigDecimal and newton iteration.
     * R is the n-th root of A.
     * @param A big integer.
     * @param n long.
     * @return the n-th root of A.
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
        // ensure enough precision
        int s = A.val.bitLength();
        MathContext mc = new MathContext( s ); 
        //System.out.println("mc = " + mc);
        // newton iteration
        BigDecimal Ap = new BigDecimal( A.val, mc );
        //System.out.println("Ap = " + Ap);
        BigDecimal N = new BigDecimal( n, mc ); 
        BigDecimal ninv = new BigDecimal( 1.0/n, mc ); 
        BigDecimal nsub = BigDecimal.ONE.subtract( ninv ); 
        BigDecimal P, R1, R = Ap.multiply(ninv); // initial guess
        BigDecimal d;
        while ( true ) {
            P = Power.positivePower( R, n-1 );
            R1 = Ap.divide( P.multiply(N) ); 
            R1 = R.multiply( nsub ).sum( R1 );
            d = R.subtract(R1).abs();
            R = R1;
            if ( d.compareTo(BigDecimal.ONE) <= 0 ) {
                //System.out.println("d  = " + d);
                break;
            }
        }
        java.math.BigInteger RP = R.val.toBigInteger(); 
        return new BigInteger(RP);
    }


    /** Integer square root.  
     * Uses BigDecimal and newton iteration.
     * R is the square root of A.
     * @param A big integer.
     * @return the square root of A.
     */
    public static BigInteger sqrt(BigInteger A) {
        // ensure enough precision
        int s = A.val.bitLength();
        MathContext mc = new MathContext( s ); 
        //System.out.println("mc = " + mc);
        // newton iteration
        BigDecimal Ap = new BigDecimal( A.val, mc );
        //System.out.println("Ap = " + Ap);
        BigDecimal ninv = new BigDecimal( 0.5, mc ); 
        BigDecimal R1, R = Ap.multiply(ninv); // initial guess
        BigDecimal d;
        while ( true ) {
            R1 = R.sum( Ap.divide( R ) );
            R1 = R1.multiply(ninv); // div n
            d = R.subtract(R1).abs();
            R = R1;
            if ( d.compareTo(BigDecimal.ONE) <= 0 ) {
                //System.out.println("d  = " + d);
                break;
            }
        }
        java.math.BigInteger RP = R.val.toBigInteger(); 
        return new BigInteger(RP);
    }


    /** Integer square root.  
     * Uses BigInteger only.
     * R is the square root of A.
     * @param A big integer.
     * @return the square root of A.
     */
    public static BigInteger sqrtInt(BigInteger A) {
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
            //System.out.println("R1 = " + R1);
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
