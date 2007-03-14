/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Map;
//import java.util.Map.Entry;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;


/**
 * Polynomial utilities.
 * Conversion between different representations.
 * @author Heinz Kredel
 */

public class PolyUtil {


    private static final Logger logger = Logger.getLogger(PolyUtil.class);


    /**
     * Recursive representation. 
     * Represent as polynomial in i variables with coefficients in n-i variables.
     * @param rfac recursive polynomial ring factory.
     * @param A polynomial to be converted.
     * @return Recursive represenations of this in the ring rfac.
     */
    public static <C extends RingElem<C>> 
        GenPolynomial<GenPolynomial<C>> 
        recursive( GenPolynomialRing<GenPolynomial<C>> rfac, 
                   GenPolynomial<C> A ) {

        GenPolynomial<GenPolynomial<C>> B = rfac.getZERO().clone();
        if ( A.isZERO() ) {
           return B;
        }
        int i = rfac.nvar;
        GenPolynomial<C> zero = rfac.getZEROCoefficient();
        Map<ExpVector,GenPolynomial<C>> Bv = B.getMap();
        for ( Map.Entry<ExpVector,C> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            //System.out.println("e = " + e);
            //System.out.println("a = " + a);
            ExpVector f = e.contract(0,i);
            ExpVector g = e.contract(i,e.length()-i);
            //System.out.println("f = " + f + ", g = " + g );
            GenPolynomial<C> p = Bv.get(f);
            if ( p == null ) {
                p = zero;
            }
            p = p.sum( a, g );
            Bv.put( f, p );
        }
        return B;
    }


    /**
     * Distribute a recursive polynomial to a generic polynomial. 
     * @param dfac combined polynomial ring factory of coefficients and this.
     * @param B polynomial to be converted.
     * @return distributed polynomial.
     */
    public static <C extends RingElem<C>>
        GenPolynomial<C> 
        distribute( GenPolynomialRing<C> dfac,
                    GenPolynomial<GenPolynomial<C>> B) {
        GenPolynomial<C> C = dfac.getZERO().clone();
        if ( B.isZERO() ) { 
            return C;
        }
        Map<ExpVector,C> Cm = C.getMap();
        for ( Map.Entry<ExpVector,GenPolynomial<C>> y: B.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            GenPolynomial<C> A = y.getValue();
            //System.out.println("e = " + e);
            //System.out.println("A = " + A);
            for ( Map.Entry<ExpVector,C> x: A.getMap().entrySet() ) {
                ExpVector f = x.getKey();
                C b = x.getValue();
                //System.out.println("f = " + f);
                //System.out.println("b = " + b);
                ExpVector g = e.combine(f);
                //System.out.println("g = " + g);
                if ( Cm.get(g) != null ) { // todo assert
                   throw new RuntimeException("PolyUtil debug error");
                }
                Cm.put( g, b );
            }
        }
        return C;
    }


    /**
     * BigInteger from BigRational coefficients. 
     * Represent as polynomial with BigInteger coefficients by 
     * multiplication with the lcm of the numerators of the 
     * BigRational coefficients.
     * @param fac result polynomial factory.
     * @param A polynomial with BigRational coefficients to be converted.
     * @return polynomial with BigInteger coefficients.
     */
    public static GenPolynomial<BigInteger> 
        integerFromRationalCoefficients( GenPolynomialRing<BigInteger> fac,
                                         GenPolynomial<BigRational> A ) {
        GenPolynomial<BigInteger> B = fac.getZERO();
        if ( A == null || A.isZERO() ) {
           return B;
        }
        java.math.BigInteger c = null;
        java.math.BigInteger d;
        java.math.BigInteger x;
        int s = 0;
        // lcm of denominators
        for ( BigRational y: A.getMap().values() ) {
            x = y.denominator();
            // c = lcm(c,x)
            if ( c == null ) {
               c = x; 
               s = x.signum();
            } else {
               d = c.gcd( x );
               c = c.multiply( x.divide( d ) );
            }
        }
        if ( s < 0 ) {
           c = c.negate();
        }
        Map<ExpVector,BigInteger> Bv = B.getMap();
        for ( Map.Entry<ExpVector,BigRational> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            BigRational a = y.getValue();
            //System.out.println("e = " + e);
            //System.out.println("a = " + a);
            // p = n*(c/d)
            java.math.BigInteger b = c.divide( a.denominator() );
            BigInteger p = new BigInteger( a.numerator().multiply( b ) );
            Bv.put( e, p );
        }
        return B;
    }


    /**
     * BigRational from BigInteger coefficients. 
     * Represent as polynomial with BigRational coefficients.
     * @param fac result polynomial factory.
     * @param A polynomial with BigInteger coefficients to be converted.
     * @return polynomial with BigRational coefficients.
     */
    public static GenPolynomial<BigRational> 
        rationalFromIntegerCoefficients( GenPolynomialRing<BigRational> fac,
                                         GenPolynomial<BigInteger> A ) {
        GenPolynomial<BigRational> B = fac.getZERO();
        if ( A == null || A.isZERO() ) {
           return B;
        }
        Map<ExpVector,BigRational> Bv = B.getMap();
        for ( Map.Entry<ExpVector,BigInteger> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            BigInteger a = y.getValue();
            //System.out.println("e = " + e);
            //System.out.println("a = " + a);
            BigRational p = new BigRational( a.getVal() );
            Bv.put( e, p );
        }
        return B;
    }


}