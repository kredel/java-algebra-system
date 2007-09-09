/*
 * $Id$
 */

package edu.jas.ufd;

import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;

import edu.jas.application.Quotient;
import edu.jas.application.QuotientRing;


/**
 * Polynomial udf utilities, e.g. 
 * conversion between different representations.
 * @author Heinz Kredel
 */

public class PolyUfdUtil {


    private static final Logger logger = Logger.getLogger(PolyUfdUtil.class);
    private static boolean debug = logger.isDebugEnabled();


    /**
     * Integral polynomial from rational function coefficients. 
     * Represent as polynomial with integral polynomial coefficients by 
     * multiplication with the lcm of the numerators of the 
     * rational function coefficients.
     * @param fac result polynomial factory.
     * @param A polynomial with rational function coefficients to be converted.
     * @return polynomial with integral polynomial coefficients.
     */
    public static <C extends GcdRingElem<C>> 
        GenPolynomial<GenPolynomial<C>>
        integralFromQuotientCoefficients( GenPolynomialRing<GenPolynomial<C>> fac,
                                          GenPolynomial<Quotient<C>> A ) {
        GenPolynomial<GenPolynomial<C>> B = fac.getZERO().clone();
        if ( A == null || A.isZERO() ) {
           return B;
        }
        GenPolynomial<C> c = null;
        GenPolynomial<C> d;
        GenPolynomial<C> x;
        GreatestCommonDivisor<C> ufd = new GreatestCommonDivisorSubres<C>();
        int s = 0;
        // lcm of denominators
        for ( Quotient<C> y: A.getMap().values() ) {
            x = y.den;
            // c = lcm(c,x)
            if ( c == null ) {
               c = x; 
               s = x.signum();
            } else {
               d = ufd.gcd( c, x );
               c = c.multiply( x.divide( d ) );
            }
        }
        if ( s < 0 ) {
           c = c.negate();
        }
        //Map<ExpVector,GenPolynomial<C>> Bv = B.getMap();
        for ( Map.Entry<ExpVector,Quotient<C>> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            Quotient<C> a = y.getValue();
            //System.out.println("e = " + e);
            //System.out.println("a = " + a);
            // p = n*(c/d)
            GenPolynomial<C> b = c.divide( a.den );
            GenPolynomial<C> p = a.num.multiply( b );
            //Bv.put( e, p );
            B = B.sum( p, e ); // inefficient
        }
        return B;
    }


    /**
     * Integral polynomial from rational function coefficients. 
     * Represent as polynomial with integral polynomial coefficients by 
     * multiplication with the lcm of the numerators of the 
     * rational function coefficients.
     * @param fac result polynomial factory.
     * @param L list of polynomial with rational function coefficients to be converted.
     * @return list of polynomials with integral polynomial coefficients.
     */
    public static <C extends GcdRingElem<C>> 
        List<GenPolynomial<GenPolynomial<C>>>
        integralFromQuotientCoefficients( GenPolynomialRing<GenPolynomial<C>> fac,
                                          Collection<GenPolynomial<Quotient<C>>> L ) {
        if ( L == null ) {
           return null;
        }
        List<GenPolynomial<GenPolynomial<C>>> list 
            = new ArrayList<GenPolynomial<GenPolynomial<C>>>( L.size() );
        for ( GenPolynomial<Quotient<C>> p : L ) {
            list.add( integralFromQuotientCoefficients(fac,p) );
        }
        return list;
    }


    /**
     * Rational function from integral polynomial coefficients. 
     * Represent as polynomial with type Quotient<C> coefficients.
     * @param fac result polynomial factory.
     * @param A polynomial with integral polynomial coefficients to be converted.
     * @return polynomial with type Quotient<C> coefficients.
     */
    public static <C extends GcdRingElem<C>>
        GenPolynomial<Quotient<C>> 
        quotientFromIntegralCoefficients( GenPolynomialRing<Quotient<C>> fac,
                                          GenPolynomial<GenPolynomial<C>> A ) {
        GenPolynomial<Quotient<C>> B = fac.getZERO().clone();
        if ( A == null || A.isZERO() ) {
           return B;
        }
        RingFactory<Quotient<C>> cfac = fac.coFac;
        QuotientRing<C> qfac = (QuotientRing<C>)cfac; 
        //Map<ExpVector,Quotient<C>> Bv = B.getMap();
        for ( Map.Entry<ExpVector,GenPolynomial<C>> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            GenPolynomial<C> a = y.getValue();
            //System.out.println("e = " + e);
            //System.out.println("a = " + a);
            Quotient<C> p = new Quotient<C>(qfac, a); // can not be zero
            if ( p != null && !p.isZERO() ) {
                //Bv.put( e, p );
                B = B.sum( p, e ); // inefficient
            }
        }
        return B;
    }


    /**
     * Rational function from integral polynomial coefficients. 
     * Represent as polynomial with type Quotient<C> coefficients.
     * @param fac result polynomial factory.
     * @param L list of polynomials with integral polynomial 
     *        coefficients to be converted.
     * @return list of polynomials with type Quotient<C> coefficients.
     */
    public static <C extends GcdRingElem<C>>
        List<GenPolynomial<Quotient<C>>> 
        quotientFromIntegralCoefficients( GenPolynomialRing<Quotient<C>> fac,
                                          Collection<GenPolynomial<GenPolynomial<C>>> L ) {
        if ( L == null ) {
           return null;
        }
        List<GenPolynomial<Quotient<C>>> list 
            = new ArrayList<GenPolynomial<Quotient<C>>>( L.size() );
        for ( GenPolynomial<GenPolynomial<C>> p : L ) {
            list.add( quotientFromIntegralCoefficients(fac,p) );
        }
        return list;
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
        GenPolynomial<BigInteger> B = fac.getZERO().clone();
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
     * BigInteger from BigRational coefficients. 
     * Represent as list of polynomials with BigInteger coefficients by 
     * multiplication with the lcm of the numerators of the 
     * BigRational coefficients of each polynomial.
     * @param fac result polynomial factory.
     * @param L list of polynomials with BigRational coefficients to be converted.
     * @return polynomial list with BigInteger coefficients.
     */
    public static List<GenPolynomial<BigInteger>> 
        integerFromRationalCoefficients( GenPolynomialRing<BigInteger> fac,
                                         List<GenPolynomial<BigRational>> L ) {
        List<GenPolynomial<BigInteger>> K = null;
        if ( L == null ) {
           return K;
        }
        K = new ArrayList<GenPolynomial<BigInteger>>( L.size() );
        if ( L.size() == 0 ) {
           return K;
        }
        for ( GenPolynomial<BigRational> a: L ) {
            GenPolynomial<BigInteger> b 
               = integerFromRationalCoefficients( fac, a );
            K.add( b );
        }
        return K;
    }

}