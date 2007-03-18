/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Map;
//import java.util.Map.Entry;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.BigComplex;


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
     * BigInteger from ModInteger coefficients. 
     * Represent as polynomial with BigInteger coefficients by 
     * removing the modules and making coefficients symmetric to 0.
     * @param fac result polynomial factory.
     * @param A polynomial with ModInteger coefficients to be converted.
     * @return polynomial with BigInteger coefficients.
     */
    public static GenPolynomial<BigInteger> 
        integerFromModularCoefficients( GenPolynomialRing<BigInteger> fac,
                                        GenPolynomial<ModInteger> A ) {
        GenPolynomial<BigInteger> B = fac.getZERO().clone();
        if ( A == null || A.isZERO() ) {
           return B;
        }
        Map<ExpVector,BigInteger> Bv = B.getMap();
        for ( Map.Entry<ExpVector,ModInteger> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            ModInteger a = y.getValue();
            //System.out.println("e = " + e);
            //System.out.println("a = " + a);
            java.math.BigInteger av = a.getVal();
            java.math.BigInteger am = a.getModul();
            if ( av.add( av ).compareTo( am ) > 0 ) {
               // a > m/2, make symmetric to 0
               av = av.subtract( am );
            }
            BigInteger p = new BigInteger( av );
            Bv.put( e, p );
        }
        return B;
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
     * from BigInteger coefficients. 
     * Represent as polynomial with type C coefficients,
     * e.g. ModInteger or BigRational.
     * @param fac result polynomial factory.
     * @param A polynomial with BigInteger coefficients to be converted.
     * @return polynomial with type C coefficients.
     */
    public static <C extends RingElem<C>>
        GenPolynomial<C> 
        fromIntegerCoefficients( GenPolynomialRing<C> fac,
                                 GenPolynomial<BigInteger> A ) {
        GenPolynomial<C> B = fac.getZERO().clone();
        if ( A == null || A.isZERO() ) {
           return B;
        }
        RingFactory<C> cfac = fac.coFac;
        Map<ExpVector,C> Bv = B.getMap();
        for ( Map.Entry<ExpVector,BigInteger> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            BigInteger a = y.getValue();
            //System.out.println("e = " + e);
            //System.out.println("a = " + a);
            C p = cfac.fromInteger( a.getVal() ); // can be zero
            if ( p != null && !p.isZERO() ) {
               Bv.put( e, p );
            }
        }
        return B;
    }


    /**
     * Real part. 
     * @param fac result polynomial factory.
     * @param A polynomial with BigComplex coefficients to be converted.
     * @return polynomial with real part of the coefficients.
     */
    public static GenPolynomial<BigRational> 
        realPart( GenPolynomialRing<BigRational> fac,
                  GenPolynomial<BigComplex> A ) {
        GenPolynomial<BigRational> B = fac.getZERO().clone();
        if ( A == null || A.isZERO() ) {
           return B;
        }
        Map<ExpVector,BigRational> Bv = B.getMap();
        for ( Map.Entry<ExpVector,BigComplex> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            BigComplex a = y.getValue();
            //System.out.println("e = " + e);
            //System.out.println("a = " + a);
            BigRational p = a.getRe();
            if ( p != null && !p.isZERO() ) {
               Bv.put( e, p );
            }
        }
        return B;
    }


    /**
     * Imaginary part. 
     * @param fac result polynomial factory.
     * @param A polynomial with BigComplex coefficients to be converted.
     * @return polynomial with imaginary part of coefficients.
     */
    public static GenPolynomial<BigRational> 
        imaginaryPart( GenPolynomialRing<BigRational> fac,
                       GenPolynomial<BigComplex> A ) {
        GenPolynomial<BigRational> B = fac.getZERO().clone();
        if ( A == null || A.isZERO() ) {
           return B;
        }
        Map<ExpVector,BigRational> Bv = B.getMap();
        for ( Map.Entry<ExpVector,BigComplex> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            BigComplex a = y.getValue();
            //System.out.println("e = " + e);
            //System.out.println("a = " + a);
            BigRational p = a.getIm();
            if ( p != null && !p.isZERO() ) {
               Bv.put( e, p );
            }
        }
        return B;
    }


    /**
     * Complex from rational real part. 
     * @param fac result polynomial factory.
     * @param A polynomial with BigRational coefficients to be converted.
     * @return polynomial with BigComplex coefficients.
     */
    public static GenPolynomial<BigComplex> 
        complexFromRational( GenPolynomialRing<BigComplex> fac,
                             GenPolynomial<BigRational> A ) {
        GenPolynomial<BigComplex> B = fac.getZERO().clone();
        if ( A == null || A.isZERO() ) {
           return B;
        }
        Map<ExpVector,BigComplex> Bv = B.getMap();
        for ( Map.Entry<ExpVector,BigRational> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            BigRational a = y.getValue();
            //System.out.println("e = " + e);
            //System.out.println("a = " + a);
            BigComplex p = new BigComplex( a );
            Bv.put( e, p );
        }
        return B;
    }


    /** ModInteger chinese remainder algorithm on coefficients.
     * @param fac GenPolynomial<ModInteger> result factory 
     * with A.coFac.modul*B.coFac.modul = C.coFac.modul.
     * @param A GenPolynomial<ModInteger>.
     * @param B other GenPolynomial<ModInteger>.
     * @param mi inverse of A.coFac.modul in ring B.coFac.
     * @return cra(A,B).
     */
    public static GenPolynomial<ModInteger> 
        chineseRemainder( GenPolynomialRing<ModInteger> fac,
                          GenPolynomial<ModInteger> A,
                          ModInteger mi,
                          GenPolynomial<ModInteger> B ) {
        ModInteger cfac = fac.coFac.getZERO(); // get RingFactory
        GenPolynomial<ModInteger> C = fac.getZERO().clone(); 
        GenPolynomial<ModInteger> Ap = A.clone(); 
        SortedMap<ExpVector,ModInteger> av = Ap.getMap();
        SortedMap<ExpVector,ModInteger> bv = B.getMap();
        SortedMap<ExpVector,ModInteger> cv = C.getMap();
        ModInteger c = null;
        for ( ExpVector e : bv.keySet() ) {
            ModInteger x = av.get( e );
            ModInteger y = bv.get( e ); // assert y != null
            if ( x != null ) {
               av.remove( e );
               c = cfac.chineseRemainder(x,mi,y);
               if ( ! c.isZERO() ) { // cannot happen
                   cv.put( e, c );
               }
            } else {
               c = cfac.fromInteger( y.getVal() );
               cv.put( e, c ); // c != null
            }
        }
        // assert bv is empty = done
        for ( ExpVector e : av.keySet() ) { // rest of av
            ModInteger x = av.get( e ); // assert x != null
            c = cfac.fromInteger( x.getVal() );
            cv.put( e, c ); // c != null
        }
        return C;
    }


    /**
     * GenPolynomial monic, i.e. leadingBaseCoefficient == 1.
     * If leadingBaseCoefficient is not invertible returns this unmodified.
     * @param p recursive GenPolynomial<GenPolynomial<C>>.
     * @return monic(p).
     */
    public static <C extends RingElem<C>>
           GenPolynomial<GenPolynomial<C>> 
           monic(GenPolynomial<GenPolynomial<C>> p) {
        if ( p == null || p.isZERO() ) {
            return p;
        }
        C lc = p.leadingBaseCoefficient().leadingBaseCoefficient();
        if ( !lc.isUnit() ) {
            //System.out.println("lc = "+lc);
            return p;
        }
        C lm = lc.inverse();
        GenPolynomial<C> L = p.ring.coFac.getONE();
        L = L.multiply(lm);
        return p.multiply(L);
    }

}