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
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.BigComplex;

import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.GreatestCommonDivisorPrimitive;


/**
 * Polynomial utilities, e.g. 
 * conversion between different representations, evaluation and interpolation.
 * @author Heinz Kredel
 */

public class PolyUtil {


    private static final Logger logger = Logger.getLogger(PolyUtil.class);
    private static boolean debug = logger.isDebugEnabled();


    /**
     * Recursive representation. 
     * Represent as polynomial in i variables with coefficients in n-i variables.
     * Works for arbitrary term orders.
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
        Map<ExpVector,GenPolynomial<C>> Bv = B.val; //getMap();
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
     * Works for arbitrary term orders.
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
        Map<ExpVector,C> Cm = C.val; //getMap();
        for ( Map.Entry<ExpVector,GenPolynomial<C>> y: B.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            GenPolynomial<C> A = y.getValue();
            //System.out.println("e = " + e);
            //System.out.println("A = " + A);
            for ( Map.Entry<ExpVector,C> x: A.val.entrySet() ) {
                ExpVector f = x.getKey();
                C b = x.getValue();
                //System.out.println("f = " + f);
                //System.out.println("b = " + b);
                ExpVector g = e.combine(f);
                //System.out.println("g = " + g);
		assert ( Cm.get(g) != null );
                if ( Cm.get(g) != null ) { // todo assert, done
                   throw new RuntimeException("PolyUtil debug error");
                }
                Cm.put( g, b );
            }
        }
        return C;
    }


    /**
     * BigInteger from ModInteger coefficients, symmetric. 
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
        Map<ExpVector,BigInteger> Bv = B.val; //getMap();
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
     * BigInteger from ModInteger coefficients, positive. 
     * Represent as polynomial with BigInteger coefficients by 
     * removing the modules.
     * @param fac result polynomial factory.
     * @param A polynomial with ModInteger coefficients to be converted.
     * @return polynomial with BigInteger coefficients.
     */
    public static GenPolynomial<BigInteger> 
        integerFromModularCoefficientsPositive( GenPolynomialRing<BigInteger> fac,
                                                GenPolynomial<ModInteger> A ) {
        GenPolynomial<BigInteger> B = fac.getZERO().clone();
        if ( A == null || A.isZERO() ) {
           return B;
        }
        Map<ExpVector,BigInteger> Bv = B.val; //getMap();
        for ( Map.Entry<ExpVector,ModInteger> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            ModInteger a = y.getValue();
            //System.out.println("e = " + e);
            //System.out.println("a = " + a);
            java.math.BigInteger av = a.getVal();
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
        for ( BigRational y: A.val.values() ) {
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
        Map<ExpVector,BigInteger> Bv = B.val; //getMap();
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
        Map<ExpVector,C> Bv = B.val; //getMap();
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
        Map<ExpVector,BigRational> Bv = B.val; //getMap();
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
        Map<ExpVector,BigRational> Bv = B.val; //getMap();
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
        Map<ExpVector,BigComplex> Bv = B.val; //getMap();
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
     * @return S = cra(A,B), with S mod A.coFac.modul == A 
     *                       and S mod B.coFac.modul == B. 
     */
    public static //<C extends RingElem<C>>
        GenPolynomial<ModInteger> 
        chineseRemainder( GenPolynomialRing<ModInteger> fac,
                          GenPolynomial<ModInteger> A,
                          ModInteger mi,
                          GenPolynomial<ModInteger> B ) {
        ModIntegerRing cfac = (ModIntegerRing)(Object)fac.coFac; // get RingFactory
        GenPolynomial<ModInteger> S = fac.getZERO().clone(); 
        GenPolynomial<ModInteger> Ap = A.clone(); 
        SortedMap<ExpVector,ModInteger> av = Ap.val; //getMap();
        SortedMap<ExpVector,ModInteger> bv = B.getMap();
        SortedMap<ExpVector,ModInteger> sv = S.val; //getMap();
        ModInteger c = null;
        for ( ExpVector e : bv.keySet() ) {
            ModInteger x = av.get( e );
            ModInteger y = bv.get( e ); // assert y != null
            if ( x != null ) {
               av.remove( e );
               c = cfac.chineseRemainder(x,mi,y);
               if ( ! c.isZERO() ) { // 0 cannot happen
                   sv.put( e, c );
               }
            } else {
               //c = cfac.fromInteger( y.getVal() );
               c = cfac.chineseRemainder(A.ring.coFac.getZERO(),mi,y);
               if ( ! c.isZERO() ) { // 0 cannot happen
                  sv.put( e, c ); // c != null
               }
            }
        }
        // assert bv is empty = done
        for ( ExpVector e : av.keySet() ) { // rest of av
            ModInteger x = av.get( e ); // assert x != null
            //c = cfac.fromInteger( x.getVal() );
            c = cfac.chineseRemainder(x,mi,B.ring.coFac.getZERO());
            if ( ! c.isZERO() ) { // 0 cannot happen
               sv.put( e, c ); // c != null
            }
        }
        return S;
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


    /**
     * GenPolynomial coefficient wise remainder.
     * @param P GenPolynomial.
     * @param s nonzero coefficient.
     * @return coefficient wise remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public static <C extends RingElem<C>>
           GenPolynomial<C> baseRemainderPoly( GenPolynomial<C> P, 
                                               C s ) {
        if ( s == null || s.isZERO() ) {
            throw new RuntimeException(P.getClass().getName()
                                       + " division by zero");
        }
        GenPolynomial<C> h = P.ring.getZERO().clone();
        Map<ExpVector,C> hm = h.val; //getMap();
        for ( Map.Entry<ExpVector,C> m : P.getMap().entrySet() ) {
            ExpVector f = m.getKey(); 
            C a = m.getValue(); 
            C x = a.remainder(s);
            hm.put(f,x);
        }
        return h;
    }


    /**
     * GenPolynomial sparse pseudo remainder.
     * For univariate polynomials.
     * @param P GenPolynomial.
     * @param S nonzero GenPolynomial.
     * @return remainder with ldcf(S)<sup>m'</sup> P = quotient * S + remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public static <C extends RingElem<C>>
           GenPolynomial<C> basePseudoRemainder( GenPolynomial<C> P, 
                                                 GenPolynomial<C> S ) {
        if ( S == null || S.isZERO() ) {
            throw new RuntimeException(P.getClass().getName()
                                       + " division by zero");
        }
        if ( P.isZERO() ) {
            return P;
        }
        if ( S.isONE() ) {
            return P.ring.getZERO();
        }
        C c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenPolynomial<C> h;
        GenPolynomial<C> r = P; 
        while ( ! r.isZERO() ) {
            ExpVector f = r.leadingExpVector();
            if ( ExpVector.EVMT(f,e) ) {
                C a = r.leadingBaseCoefficient();
                f = ExpVector.EVDIF( f, e );
                //logger.info("red div = " + e);
                C x = a.remainder(c);
                if ( false && x.isZERO() ) {
                   C y = a.divide(c);
                   //System.out.println("y = " + y);
                   h = S.multiply( y, f ); // coeff a
                } else {
                   r = r.multiply( c );    // coeff ac
                   h = S.multiply( a, f ); // coeff ac
                }
                r = r.subtract( h );
            } else {
                break;
            }
        }
        return r;
    }


    /**
     * GenPolynomial pseudo divide.
     * For univariate polynomials or exact division.
     * @param P GenPolynomial.
     * @param S nonzero GenPolynomial.
     * @return quotient with ldcf(S)<sup>m'</sup> P = quotient * S + remainder.
     * @see edu.jas.poly.GenPolynomial#divide(edu.jas.poly.GenPolynomial).
     */
    public static <C extends RingElem<C>>
           GenPolynomial<C> basePseudoDivide( GenPolynomial<C> P, 
                                              GenPolynomial<C> S ) {
        if ( S == null || S.isZERO() ) {
            throw new RuntimeException(P.getClass().getName()
                                       + " division by zero");
        }
        if ( S.ring.nvar != 1 ) {
           // ok if exact division
           // throw new RuntimeException(this.getClass().getName()
           //                            + " univariate polynomials only");
        }
        if ( P.isZERO() || S.isONE() ) {
            return P;
        }
        C c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenPolynomial<C> h;
        GenPolynomial<C> r = P;
        GenPolynomial<C> q = S.ring.getZERO().clone();

        while ( ! r.isZERO() ) {
            ExpVector f = r.leadingExpVector();
            //System.out.println("f = " + f);
            if ( ExpVector.EVMT(f,e) ) {
                C a = r.leadingBaseCoefficient();
                f = ExpVector.EVDIF( f, e );
                //logger.info("red div = " + e);
                //System.out.println("a = " + a + ", f = " + f);
                C x = a.remainder(c);
                //System.out.println("rem x = " + x);
                if ( x.isZERO() ) {
                   C y = a.divide(c);
                   //System.out.println("y = " + y);
                   q = q.sum( y, f );
                   h = S.multiply( y, f ); // coeff a
                } else {
                   q = q.sum( a, f );
                   q = q.multiply( c );
                   r = r.multiply( c );    // coeff ac
                   h = S.multiply( a, f ); // coeff ac
                }
                r = r.subtract( h );
            } else {
                break;
            }
        }
        return q;
    }


    /**
     * GenPolynomial pseudo divide.
     * For recursive polynomials.
     * Division by coefficient ring element.
     * @param P recursive GenPolynomial.
     * @param s GenPolynomial.
     * @return this/s.
     */
    public static <C extends RingElem<C>>
           GenPolynomial<GenPolynomial<C>> 
           recursiveDivide( GenPolynomial<GenPolynomial<C>> P, 
                            GenPolynomial<C> s ) {
        if ( s == null || s.isZERO() ) {
            throw new RuntimeException(P.getClass().getName()
                                       + " division by zero");
        }
        if ( P.isZERO() ) {
            return P;
        }
        if ( s.isONE() ) {
            return P;
        }
        GenPolynomial<GenPolynomial<C>> p = P.ring.getZERO().clone(); 
        SortedMap<ExpVector,GenPolynomial<C>> pv = p.val; //getMap();
        //System.out.println("P  = " + P);
        //System.out.println("s  = " + s);
        for ( Map.Entry<ExpVector,GenPolynomial<C>> m1 : P.getMap().entrySet() ) {
            GenPolynomial<C> c1 = m1.getValue();
            ExpVector e1 = m1.getKey();
            GenPolynomial<C> c = PolyUtil.<C>basePseudoDivide(c1,s);
            pv.put( e1, c ); // or m1.setValue( c )
        }
        //System.out.println("p  = " + p);
        return p;
    }


    /**
     * GenPolynomial sparse pseudo remainder.
     * For recursive polynomials.
     * @param P recursive GenPolynomial.
     * @param S nonzero recursive GenPolynomial.
     * @return remainder with ldcf(S)<sup>m'</sup> P = quotient * S + remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public static <C extends RingElem<C>>
           GenPolynomial<GenPolynomial<C>> 
           recursivePseudoRemainder( GenPolynomial<GenPolynomial<C>> P, 
                                     GenPolynomial<GenPolynomial<C>> S) {
        if ( S == null || S.isZERO() ) {
            throw new RuntimeException(P.getClass().getName()
                                       + " division by zero");
        }
        if ( P == null || P.isZERO() ) {
            return P;
        }
        if ( S.isONE() ) {
            return P.ring.getZERO();
        }
        GenPolynomial<C> c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenPolynomial<GenPolynomial<C>> h;
        GenPolynomial<GenPolynomial<C>> r = P; 
        while ( ! r.isZERO() ) {
            ExpVector f = r.leadingExpVector();
            if ( ExpVector.EVMT(f,e) ) {
                GenPolynomial<C> a = r.leadingBaseCoefficient();
                f = ExpVector.EVDIF( f, e );
                //logger.info("red div = " + e);
                GenPolynomial<C> x = c; //test basePseudoRemainder(a,c);
                if ( x.isZERO() ) {
                   GenPolynomial<C> y = PolyUtil.<C>basePseudoDivide(a,c);
                   //System.out.println("y = " + y);
                   h = S.multiply( y, f ); // coeff a
                } else {
                   r = r.multiply( c );    // coeff ac
                   h = S.multiply( a, f ); // coeff ac
                }
                r = r.subtract( h );
            } else {
                break;
            }
        }
        return r;
    }


    /**
     * GenPolynomial pseudo divide.
     * For recursive polynomials.
     * @param P recursive GenPolynomial.
     * @param S nonzero recursive GenPolynomial.
     * @return quotient with ldcf(S)<sup>m</sup> P = quotient * S + remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public static <C extends RingElem<C>>
           GenPolynomial<GenPolynomial<C>> 
           recursivePseudoDivide( GenPolynomial<GenPolynomial<C>> P, 
                                  GenPolynomial<GenPolynomial<C>> S) {
        if ( S == null || S.isZERO() ) {
            throw new RuntimeException(P.getClass().getName()
                                       + " division by zero");
        }
        if ( S.ring.nvar != 1 ) {
           // ok if exact division
           // throw new RuntimeException(this.getClass().getName()
           //                            + " univariate polynomials only");
        }
        if ( P == null || P.isZERO() ) {
            return P;
        }
        if ( S.isONE() ) {
            return P;
        }
        GenPolynomial<C> c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenPolynomial<GenPolynomial<C>> h;
        GenPolynomial<GenPolynomial<C>> r = P; 
        GenPolynomial<GenPolynomial<C>> q = S.ring.getZERO().clone();
        while ( ! r.isZERO() ) {
            ExpVector f = r.leadingExpVector();
            if ( ExpVector.EVMT(f,e) ) {
                GenPolynomial<C> a = r.leadingBaseCoefficient();
                f = ExpVector.EVDIF( f, e );
                //logger.info("red div = " + e);
                GenPolynomial<C> x = PolyUtil.<C>basePseudoRemainder(a,c);
                //System.out.println("x = " + x);
                if ( x.isZERO() ) {
                   GenPolynomial<C> y = PolyUtil.<C>basePseudoDivide(a,c);
                   //System.out.println("y = " + y);
                   q = q.sum( y, f );
                   h = S.multiply( y, f ); // coeff a
                } else {
                   q = q.sum( a, f );
                   q = q.multiply( c );
                   r = r.multiply( c );    // coeff ac
                   h = S.multiply( a, f ); // coeff ac
                }
                r = r.subtract( h );
            } else {
                break;
            }
        }
        return q;
    }


    /**
     * GenPolynomial polynomial derivative main variable.
     * @param P GenPolynomial.
     * @return deriviative(P).
     */
    public static <C extends RingElem<C>>
           GenPolynomial<C> 
           baseDeriviative( GenPolynomial<C> P ) {
        if ( P == null || P.isZERO() ) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if ( pfac.nvar > 1 ) { 
           // baseContent not possible by return type
           throw new RuntimeException(P.getClass().getName()
                     + " only for univariate polynomials");
        }
        RingFactory<C> rf = pfac.coFac;
        GenPolynomial<C> d = pfac.getZERO().clone();
        Map<ExpVector,C> dm = d.val; //getMap();
        for ( Map.Entry<ExpVector,C> m : P.getMap().entrySet() ) {
            ExpVector f = m.getKey();  
            long fl = f.getVal(0);
            if ( fl > 0 ) {
               C cf = rf.fromInteger( fl );
               C a = m.getValue(); 
               C x = a.multiply(cf);
               ExpVector e = new ExpVector( 1, 0, fl-1L );  
               dm.put(e,x);
            }
        }
        return d; 
    }


    /**
     * GenPolynomial recursive polynomial derivative main variable.
     * @param P recursive GenPolynomial.
     * @return deriviative(P).
     */
    public static <C extends RingElem<C>>
           GenPolynomial<GenPolynomial<C>> 
           recursiveDeriviative( GenPolynomial<GenPolynomial<C>> P ) {
        if ( P == null || P.isZERO() ) {
            return P;
        }
        GenPolynomialRing<GenPolynomial<C>> pfac = P.ring;
        if ( pfac.nvar > 1 ) { 
           // baseContent not possible by return type
           throw new RuntimeException(P.getClass().getName()
                     + " only for univariate polynomials");
        }
        GenPolynomialRing<C> pr = (GenPolynomialRing<C>)pfac.coFac;
        RingFactory<C> rf = pr.coFac;
        GenPolynomial<GenPolynomial<C>> d = pfac.getZERO().clone();
        Map<ExpVector,GenPolynomial<C>> dm = d.val; //getMap();
        for ( Map.Entry<ExpVector,GenPolynomial<C>> m : P.getMap().entrySet() ) {
            ExpVector f = m.getKey();  
            long fl = f.getVal(0);
            if ( fl > 0 ) {
               C cf = rf.fromInteger( fl );
               GenPolynomial<C> a = m.getValue(); 
               GenPolynomial<C> x = a.multiply(cf);
               ExpVector e = new ExpVector( 1, 0, fl-1L );  
               dm.put(e,x);
            }
        }
        return d; 
    }


    /**
     * Factor coefficient bound.
     * See SACIPOL.IPFCB: the product of all maxNorms of potential factors
     * is less than or equal to 2**b times the maxNorm of A.
     * @param e degree vector of a GenPolynomial A.
     * @return 2**b.
     */
    public static BigInteger factorBound(ExpVector e) {
        int n = 0;
        java.math.BigInteger p = java.math.BigInteger.ONE;
        java.math.BigInteger v;
        if ( e == null || e.isZERO() ) {
           return BigInteger.ONE;
        }
        long[] val = e.getVal();
        for ( int i = 0; i < val.length; i++ ) {
            if ( val[i] > 0 ) {
               n += ( 2*val[i] - 1 );
               v = new java.math.BigInteger( "" + (val[i] - 1) );
               p = p.multiply( v );
            }
        }
        n += ( p.bitCount() + 1 ); // log2(p)
        n /= 2;
        v = new java.math.BigInteger( "" + 2 );
        v = v.shiftLeft( n );
        BigInteger N = new BigInteger( v );
        return N;
    }


    /**
     * Evaluate at main variable. 
     * @param cfac coefficent polynomial ring factory.
     * @param A polynomial to be evaluated.
     * @param a value to evaluate at.
     * @return A( x_1, ..., x_{n-1}, a ).
     */
    public static <C extends RingElem<C>> 
        GenPolynomial<C> 
        evaluateMain( GenPolynomialRing<C> cfac, 
                      GenPolynomial<GenPolynomial<C>> A,
                      C a ) {
        if ( A == null || A.isZERO() ) {
           return cfac.getZERO();
        }
        if ( A.ring.nvar != 1 ) { // todo assert
           throw new RuntimeException("evaluateMain no univariate polynomial");
        }
        if ( a == null || a.isZERO() ) {
           return A.trailingBaseCoefficient();
        }
        // assert decending exponents, i.e. compatible term order
        Map<ExpVector,GenPolynomial<C>> val = A.getMap();
        GenPolynomial<C> B = null;
        long el1 = -1; // undefined
        long el2 = -1;
        for ( ExpVector e : val.keySet() ) {
            el2 = e.getVal(0);
            if ( B == null /*el1 < 0*/ ) { // first turn
               B = val.get( e );
            } else {
               for ( long i = el2; i < el1; i++ ) {
                   B = B.multiply( a );
               }
               B = B.sum( val.get( e ) );
            }
            el1 = el2;
        }
        for ( long i = 0; i < el2; i++ ) {
            B = B.multiply( a );
        }
        return B;
    }


    /**
     * Evaluate at main variable. 
     * @param cfac coefficent ring factory.
     * @param A univariate polynomial to be evaluated.
     * @param a value to evaluate at.
     * @return A( a ).
     */
    public static <C extends RingElem<C>> 
        C 
        evaluateMain( RingFactory<C> cfac, 
                      GenPolynomial<C> A,
                      C a ) {
        if ( A == null || A.isZERO() ) {
           return cfac.getZERO();
        }
        if ( A.ring.nvar != 1 ) { // todo assert
           throw new RuntimeException("evaluateMain no univariate polynomial");
        }
        if ( a == null || a.isZERO() ) {
           return A.trailingBaseCoefficient();
        }
        // assert decreasing exponents, i.e. compatible term order
        Map<ExpVector,C> val = A.getMap();
        C B = null;
        long el1 = -1; // undefined
        long el2 = -1;
        for ( ExpVector e : val.keySet() ) {
            el2 = e.getVal(0);
            if ( B == null /*el1 < 0*/ ) { // first turn
               B = val.get( e );
            } else {
               for ( long i = el2; i < el1; i++ ) {
                   B = B.multiply( a );
               }
               B = B.sum( val.get( e ) );
            }
            el1 = el2;
        }
        for ( long i = 0; i < el2; i++ ) {
            B = B.multiply( a );
        }
        return B;
    }


    /**
     * Evaluate at k-th variable. 
     * @param cfac coefficient polynomial ring in k variables 
     *        C[x_1, ..., x_k] factory.
     * @param rfac coefficient polynomial ring 
     *        C[x_1, ..., x_{k-1}] [x_k] factory,
     *        a recursive polynomial ring in 1 variable with 
     *        coefficients in k-1 variables.
     * @param nfac polynomial ring in n-1 varaibles
     *        C[x_1, ..., x_{k-1}] [x_{k+1}, ..., x_n] factory,
     *        a recursive polynomial ring in n-k+1 variables with 
     *        coefficients in k-1 variables.
     * @param dfac polynomial ring in n-1 variables.
     *        C[x_1, ..., x_{k-1}, x_{k+1}, ..., x_n] factory.
     * @param A polynomial to be evaluated.
     * @param a value to evaluate at.
     * @return A( x_1, ..., x_{k-1}, a, x_{k+1}, ..., x_n).
     */
    public static <C extends RingElem<C>> 
        GenPolynomial<C>
        evaluate( GenPolynomialRing<C> cfac,
                  GenPolynomialRing<GenPolynomial<C>> rfac, 
                  GenPolynomialRing<GenPolynomial<C>> nfac, 
                  GenPolynomialRing<C> dfac,
                  GenPolynomial<C> A,
                  C a ) {
        if ( rfac.nvar != 1 ) { // todo assert
           throw new RuntimeException("evaluate coefficient ring not univariate");
        }
        if ( A == null || A.isZERO() ) {
           return cfac.getZERO();
        }
        Map<ExpVector,GenPolynomial<C>> Ap = A.contract(cfac);
        GenPolynomialRing<C> rcf = (GenPolynomialRing<C>)rfac.coFac;
        GenPolynomial<GenPolynomial<C>> Ev = nfac.getZERO().clone();
        Map<ExpVector,GenPolynomial<C>> Evm = Ev.val; //getMap();
        for ( Map.Entry<ExpVector,GenPolynomial<C>> m : Ap.entrySet() ) {
            ExpVector e = m.getKey();
            GenPolynomial<C> b = m.getValue();
            GenPolynomial<GenPolynomial<C>> c = recursive( rfac, b );
            GenPolynomial<C> d = evaluateMain(rcf,c,a);
            if ( d != null && !d.isZERO() ) {
               Evm.put(e,d);
            }
        }
        GenPolynomial<C> B = distribute(dfac,Ev);
        return B;
    }


    /**
     * Evaluate at first (lowest) variable. 
     * @param cfac coefficient polynomial ring in first variable 
     *        C[x_1] factory.
     * @param dfac polynomial ring in n-1 variables.
     *        C[x_2, ..., x_n] factory.
     * @param A polynomial to be evaluated.
     * @param a value to evaluate at.
     * @return A( a, x_2, ..., x_n).
     */
    public static <C extends RingElem<C>> 
        GenPolynomial<C>
        evaluateFirst( GenPolynomialRing<C> cfac,
                       GenPolynomialRing<C> dfac,
                       GenPolynomial<C> A,
                       C a ) {
        if ( A == null || A.isZERO() ) {
           return dfac.getZERO();
        }
        Map<ExpVector,GenPolynomial<C>> Ap = A.contract(cfac);
        //RingFactory<C> rcf = cfac.coFac; // == dfac.coFac

        GenPolynomial<C> B = dfac.getZERO().clone();
        Map<ExpVector,C> Bm = B.val; //getMap();

        for ( Map.Entry<ExpVector,GenPolynomial<C>> m : Ap.entrySet() ) {
            ExpVector e = m.getKey();
            GenPolynomial<C> b = m.getValue();
            C d = evaluateMain(cfac.coFac,b,a);
            if ( d != null && !d.isZERO() ) {
               Bm.put(e,d);
            }
        }
        return B;
    }


    /**
     * Evaluate at first (lowest) variable. 
     * Could also be called evaluateFirst(), but type erasure of A parameter
     * does not allow same name.
     * @param cfac coefficient polynomial ring in first variable 
     *        C[x_1] factory.
     * @param dfac polynomial ring in n-1 variables.
     *        C[x_2, ..., x_n] factory.
     * @param A recursive polynomial to be evaluated.
     * @param a value to evaluate at.
     * @return A( a, x_2, ..., x_n).
     */
    public static <C extends RingElem<C>> 
        GenPolynomial<C>
        evaluateFirstRec( GenPolynomialRing<C> cfac,
                          GenPolynomialRing<C> dfac,
                          GenPolynomial<GenPolynomial<C>> A,
                          C a ) {
        if ( A == null || A.isZERO() ) {
           return dfac.getZERO();
        }
        Map<ExpVector,GenPolynomial<C>> Ap = A.getMap();
        //RingFactory<C> rcf = cfac.coFac; // == dfac.coFac

        GenPolynomial<C> B = dfac.getZERO().clone();
        Map<ExpVector,C> Bm = B.val; //getMap();

        for ( Map.Entry<ExpVector,GenPolynomial<C>> m : Ap.entrySet() ) {
            ExpVector e = m.getKey();
            GenPolynomial<C> b = m.getValue();
            C d = evaluateMain(cfac.coFac,b,a);
            if ( d != null && !d.isZERO() ) {
               Bm.put(e,d);
            }
        }
        return B;
    }


    /** ModInteger interpolate on first variable.
     * @param fac GenPolynomial<C> result factory.
     * @param A GenPolynomial<C>.
     * @param M GenPolynomial<C> interpolation modul of A.
     * @param mi inverse of M(am) in ring fac.coFac.
     * @param B evaluation of other GenPolynomial<C>.
     * @param am evaluation point (interpolation modul) of B, i.e. P(am) = B.
     * @return S, with S mod M == A and S(am) == B.
     */
    public static <C extends RingElem<C>>
        GenPolynomial<GenPolynomial<C>> 
        interpolate( GenPolynomialRing<GenPolynomial<C>> fac,
                     GenPolynomial<GenPolynomial<C>> A,
                     GenPolynomial<C> M,
                     C mi,
                     GenPolynomial<C> B, 
                     C am ) {
        GenPolynomial<GenPolynomial<C>> S = fac.getZERO().clone(); 
        GenPolynomial<GenPolynomial<C>> Ap = A.clone(); 
        SortedMap<ExpVector,GenPolynomial<C>> av = Ap.val; //getMap();
        SortedMap<ExpVector,C> bv = B.getMap();
        SortedMap<ExpVector,GenPolynomial<C>> sv = S.val; //getMap();
        GenPolynomialRing<C> cfac = (GenPolynomialRing<C>)fac.coFac; 
        RingFactory<C> bfac = cfac.coFac; 
        GenPolynomial<C> c = null;
        for ( ExpVector e : bv.keySet() ) {
            GenPolynomial<C> x = av.get( e );
            C y = bv.get( e ); // assert y != null
            if ( x != null ) {
               av.remove( e );
               c = PolyUtil.<C>interpolate(cfac,x,M,mi,y,am);
               if ( ! c.isZERO() ) { // 0 cannot happen
                   sv.put( e, c );
               }
            } else {
               //c = cfac.fromInteger( y.getVal() );
               c = PolyUtil.<C>interpolate(cfac,cfac.getZERO(),M,mi,y,am);
               if ( ! c.isZERO() ) { // 0 cannot happen
                  sv.put( e, c ); // c != null
               }
            }
        }
        // assert bv is empty = done
        for ( ExpVector e : av.keySet() ) { // rest of av
            GenPolynomial<C> x = av.get( e ); // assert x != null
            //c = x; //new GenPolynomial<C>( cfac, x.getMap() );
            c = PolyUtil.<C>interpolate(cfac,x,M,mi,bfac.getZERO(),am);
            if ( ! c.isZERO() ) { // 0 cannot happen
               sv.put( e, c ); // c != null
            }
        }
        return S;
    }


    /** Univariate polynomial interpolation.
     * @param fac GenPolynomial<C> result factory.
     * @param A GenPolynomial<C>.
     * @param M GenPolynomial<C> interpolation modul of A.
     * @param mi inverse of M(am) in ring fac.coFac.
     * @param a evaluation of other GenPolynomial<C>.
     * @param am evaluation point (interpolation modul) of a, i.e. P(am) = a.
     * @return S, with S mod M == A and S(am) == a.
     */
    public static <C extends RingElem<C>>
        GenPolynomial<C> 
        interpolate( GenPolynomialRing<C> fac,
                     GenPolynomial<C> A,
                     GenPolynomial<C> M,
                     C mi,
                     C a, 
                     C am ) {
        GenPolynomial<C> s; 
        C b = PolyUtil.<C>evaluateMain( fac.coFac, A, am ); 
                              // A mod a.modul
        C d = a.subtract( b ); // a-A mod a.modul
        if ( d.isZERO() ) {
           return A;
        }
        b = d.multiply( mi ); // b = (a-A)*mi mod a.modul
        // (M*b)+A mod M = A mod M = 
        // (M*mi*(a-A)+A) mod a.modul = a mod a.modul
        s = M.multiply( b );
        s = s.sum( A );
        return s;
    }


    /**
     * Maximal degree in the coefficient polynomials.
     * @return maximal degree in the coefficients.
     */
    public static <C extends RingElem<C>>
           long 
           coeffMaxDegree(GenPolynomial<GenPolynomial<C>> A) {
        if ( A.isZERO() ) {
           return 0; // 0 or -1 ?;
        }
        long deg = 0;
        for ( GenPolynomial<C> a : A.getMap().values() ) {
            long d = a.degree();
            if ( d > deg ) {
               deg = d;
            }
        }
        return deg;
    }


    /** ModInteger Hensel lifting algorithm on coefficients.
     * Let p = A.ring.coFac.modul() = B.ring.coFac.modul() 
     * and assume C == A*B mod p with ggt(A,B) == 1 mod p and
     * S A + T B == 1 mod p. 
     * See Algorithm 6.1. in Geddes et.al.. 
     * Linear version, as it does not lift S A + T B == 1 mod p^{e+1}.
     * @param C GenPolynomial<BigInteger>.
     * @param A GenPolynomial<ModInteger>.
     * @param B other GenPolynomial<ModInteger>.
     * @param S GenPolynomial<ModInteger>.
     * @param T GenPolynomial<ModInteger>.
     * @param M bound on the coefficients of A1 and B1 as factors of C.
     * @return [A1,B1] = lift(C,A,B), with C = A1 * B1.
     */
    public static //<C extends RingElem<C>>
        GenPolynomial<BigInteger>[] 
        liftHensel( GenPolynomial<BigInteger> C,
                    BigInteger M,
                    GenPolynomial<ModInteger> A,
                    GenPolynomial<ModInteger> B, 
                    GenPolynomial<ModInteger> S,
                    GenPolynomial<ModInteger> T ) {
        GenPolynomial<BigInteger>[] AB = new GenPolynomial[2];
        if ( C == null || C.isZERO() ) {
           AB[0] = C;
           AB[1] = C;
           return AB;
        }
        if ( A == null || A.isZERO() || B == null || B.isZERO() ) {
           throw new RuntimeException("A and B must be nonzero");
        }
        GenPolynomialRing<BigInteger> fac = C.ring;
        if ( fac.nvar != 1 ) { // todo assert
           throw new RuntimeException("polynomial ring not univariate");
        }
        // setup factories
        GenPolynomialRing<ModInteger> pfac = A.ring;
        RingFactory<ModInteger> p = pfac.coFac;
        RingFactory<ModInteger> q = p;
        ModIntegerRing P = (ModIntegerRing)p;
        ModIntegerRing Q = (ModIntegerRing)q;
        BigInteger Qi = new BigInteger( Q.getModul() );
        BigInteger M2 = M.multiply( M.fromInteger(2) );
        BigInteger Mq = Qi;
        //System.out.println("M2  = " + M2);
        //System.out.println("Qi = " + Qi);
        //System.out.println("P  = " + P.getModul());
        //--ModInteger Mm = new ModInteger( Qi.multiply(Qi).getVal() );
        //--ModInteger Qm = Mm.fromInteger( Q.getModul() );
        //--GenPolynomialRing<ModInteger> mfac 
        //-- = new GenPolynomialRing<ModInteger>(Mm,pfac.nvar,pfac.tord,pfac.vars);
        //--System.out.println("M  = " + M);
        //--System.out.println("Qm = " + Qm);

        // normalize c and a, b factors, assert p is prime
        GenPolynomial<BigInteger> Ai;
        GenPolynomial<BigInteger> Bi;
        BigInteger c = C.leadingBaseCoefficient();
        C = C.multiply(c); // sic
        //System.out.println("c  = " + c);
        //System.out.println("C  = " + C);
        ModInteger a = A.leadingBaseCoefficient();
        if ( !a.isONE() ) { // A = A.monic();
           A = A.divide( a );
           S = S.multiply( a );
        }
        ModInteger b = B.leadingBaseCoefficient();
        if ( !b.isONE() ) { // B = B.monic();
           B = B.divide( b );
           T = T.multiply( b );
        }
        ModInteger ci = P.fromInteger( c.getVal() );
        A = A.multiply( ci );
        B = B.multiply( ci );
        T = T.divide( ci );
        S = S.divide( ci );
        Ai = PolyUtil.integerFromModularCoefficients( fac, A );
        Bi = PolyUtil.integerFromModularCoefficients( fac, B );
        // replace leading base coefficients
        ExpVector ea = Ai.leadingExpVector();
        ExpVector eb = Bi.leadingExpVector();
        Ai.val.put(ea,c);
        Bi.val.put(eb,c);

        // polynomials mod M
        //--GenPolynomial<ModInteger> Am; 
        //--GenPolynomial<ModInteger> Bm; 
        //--GenPolynomial<ModInteger> Eam; 
        //--GenPolynomial<ModInteger> Ebm; 
        //--Am = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,Ai); 
        //--Bm = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,Bi); 
        //System.out.println("Am = " + Am);
        //System.out.println("Bm = " + Bm);

        // polynomials mod p
        GenPolynomial<ModInteger> Ap; 
        GenPolynomial<ModInteger> Bp;
        GenPolynomial<ModInteger> A1p; 
        GenPolynomial<ModInteger> B1p;
        GenPolynomial<ModInteger> Ep;

        // polynomials over the integers
        GenPolynomial<BigInteger> E;
        GenPolynomial<BigInteger> Ea;
        GenPolynomial<BigInteger> Eb;
        GenPolynomial<BigInteger> Ea1;
        GenPolynomial<BigInteger> Eb1;

        while ( Mq.compareTo( M2 ) < 0 ) {
            // compute E=(C-AB)/q over the integers
            E = C.subtract( Ai.multiply(Bi) );
            //System.out.println("\nQi = " + Qi);
            //System.out.println("Ai = " + Ai);
            //System.out.println("Bi = " + Bi);
            //System.out.println("E  = " + E);
            if ( E.isZERO() ) {
               //System.out.println("leaving on zero error");
               logger.info("leaving on zero error");
               break;
            }
            E = E.divide( Qi );
            //System.out.println("E  = " + E);
            // E mod p
            Ep = PolyUtil.<ModInteger>fromIntegerCoefficients(pfac,E); 
            logger.info("Ep = " + Ep);

            // construct approximation mod p
            Ap = S.multiply( Ep ); // S,T ++ T,S
            Bp = T.multiply( Ep );
            //System.out.println("Ap = " + Ap);
            //System.out.println("Bp = " + Bp);
            //System.out.println("A*Ap+B*Bp-Ep= " + A.multiply(Ap).sum(B.multiply(Bp)).subtract(Ep) );
            GenPolynomial<ModInteger>[] QR;
            QR = Ap.divideAndRemainder( B );
            GenPolynomial<ModInteger> Qp;
            GenPolynomial<ModInteger> Rp;
            Qp = QR[0];
            Rp = QR[1];
            A1p = Rp;
            B1p = Bp.sum( A.multiply( Qp ) );
            //++System.out.println("A1p  = " + A1p);
            //System.out.println("Qp   = " + Qp);
            //++System.out.println("B1p  = " + B1p);
            //System.out.println("Qp*B+Rp-Ap    = " + Qp.multiply(B).sum(Rp).subtract(Ap));
            //System.out.println("A*A1p+B*B1p-Ep= " + A.multiply(A1p).sum(B.multiply(B1p)).subtract(Ep) );

            // construct q-adic approximation, convert to integer
            Ea = PolyUtil.integerFromModularCoefficients(fac,A1p);
            Eb = PolyUtil.integerFromModularCoefficients(fac,B1p);
            //--Eam = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,Ea); 
            //--Ebm = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,Eb); 
            //--Eam = Eam.multiply( Qm );
            //--Ebm = Ebm.multiply( Qm );
            //--System.out.println("Eam = " + Eam);
            //--System.out.println("Ebm = " + Ebm);
            Ea1 = Ea.multiply( Qi );
            Eb1 = Eb.multiply( Qi );
            //System.out.println("Ea1 = " + Ea1);
            //System.out.println("Eb1 = " + Eb1);

            //--if ( Ea1.isZERO() && Eb1.isZERO() ) {
               //--System.out.println("leaving on zero correction");
               //-- not do that break;
            //--}
            //--Am = Am.sum( Ebm );
            //--Bm = Bm.sum( Eam ); //--------------------------
            //--System.out.println("Am = " + Am);
            //--System.out.println("Bm = " + Bm);
            Ea = Ai.sum( Eb1 ); // Eb1 and Ea1 are required
            Eb = Bi.sum( Ea1 ); //--------------------------
            //System.out.println("Ea = " + Ea);
            //System.out.println("Eb = " + Eb);
            if ( Ea.degree(0)+Eb.degree(0) > C.degree(0) ) { // debug
               throw new RuntimeException("deg(A)+deg(B) > deg(C)");
            }

            // prepare for next iteration
            //--Ai = PolyUtil.integerFromModularCoefficients(fac,Am);
            //--Bi = PolyUtil.integerFromModularCoefficients(fac,Bm);
            Mq = Qi;
            Qi = new BigInteger( Q.getModul().multiply( P.getModul() ) );
            Q = new ModIntegerRing( Qi.getVal() );
            //--Mm = new ModInteger( Mm.getModul().multiply( P.getModul() ) );
            //--System.out.println("Mm = " + Mm.getModul());
            //--mfac = new GenPolynomialRing<ModInteger>(Mm,pfac.nvar,pfac.tord,pfac.vars);
            //--Qm = Mm.fromInteger( Qi.getVal() );
            //--Am = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,Ai); 
            //--Bm = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,Bi); 
            Ai = Ea;
            Bi = Eb;
        }
        //System.out.println("Qi at exit = " + Qi);

        GreatestCommonDivisorAbstract<BigInteger> ufd
            = new GreatestCommonDivisorPrimitive<BigInteger>();

        // remove normalization
        BigInteger ai = ufd.baseContent(Ai);
        //System.out.println("ai = " + ai);
        //System.out.println("c  = " + c);
        Ai = Ai.divide( ai );
        BigInteger bi = c.divide(ai);
        //System.out.println("bi = " + bi);
        Bi = Bi.divide( bi ); // divide( c/a )

        AB[0] = Ai;
        AB[1] = Bi;
        return AB;
    }


    /** ModInteger Hensel lifting algorithm on coefficients.
     * Let p = A.ring.coFac.modul() = B.ring.coFac.modul() 
     * and assume C == A*B mod p with ggt(A,B) == 1 mod p and
     * S A + T B == 1 mod p. 
     * See algorithm 6.1. in Geddes et.al. and algorithms 3.5.{5,6} in Cohen. 
     * Quadratic version, as it also lifts S A + T B == 1 mod p^{e+1}.
     * @param C GenPolynomial<BigInteger>.
     * @param A GenPolynomial<ModInteger>.
     * @param B other GenPolynomial<ModInteger>.
     * @param S GenPolynomial<ModInteger>.
     * @param T GenPolynomial<ModInteger>.
     * @param M bound on the coefficients of A1 and B1 as factors of C.
     * @return [A1,B1] = lift(C,A,B), with C = A1 * B1.
     */
    public static //<C extends RingElem<C>>
        GenPolynomial<BigInteger>[] 
        liftHenselQuadratic( GenPolynomial<BigInteger> C,
                             BigInteger M,
                             GenPolynomial<ModInteger> A,
                             GenPolynomial<ModInteger> B, 
                             GenPolynomial<ModInteger> S,
                             GenPolynomial<ModInteger> T ) {
        GenPolynomial<BigInteger>[] AB = new GenPolynomial[2];
        if ( C == null || C.isZERO() ) {
           AB[0] = C;
           AB[1] = C;
           return AB;
        }
        if ( A == null || A.isZERO() || B == null || B.isZERO() ) {
           throw new RuntimeException("A and B must be nonzero");
        }
        GenPolynomialRing<BigInteger> fac = C.ring;
        if ( fac.nvar != 1 ) { // todo assert
           throw new RuntimeException("polynomial ring not univariate");
        }
        // setup factories
        GenPolynomialRing<ModInteger> pfac = A.ring;
        RingFactory<ModInteger> p = pfac.coFac;
        RingFactory<ModInteger> q = p;
        ModIntegerRing P = (ModIntegerRing)p;
        ModIntegerRing Q = (ModIntegerRing)q;
        BigInteger Qi = new BigInteger( Q.getModul() );
        BigInteger M2 = M.multiply( M.fromInteger(2) );
        BigInteger Mq = Qi;
        GenPolynomialRing<ModInteger> qfac;
        qfac = new GenPolynomialRing(Q,pfac.nvar,pfac.tord,pfac.vars);
        //System.out.println("M2  = " + M2);
        //System.out.println("Qi = " + Qi);
        //System.out.println("P  = " + P.getModul());

        // normalize c and a, b factors, assert p is prime
        GenPolynomial<BigInteger> Ai;
        GenPolynomial<BigInteger> Bi;
        BigInteger c = C.leadingBaseCoefficient();
        C = C.multiply(c); // sic
        //System.out.println("c  = " + c);
        //System.out.println("C  = " + C);
        ModInteger a = A.leadingBaseCoefficient();
        if ( !a.isONE() ) { // A = A.monic();
           A = A.divide( a );
           S = S.multiply( a );
        }
        ModInteger b = B.leadingBaseCoefficient();
        if ( !b.isONE() ) { // B = B.monic();
           B = B.divide( b );
           T = T.multiply( b );
        }
        ModInteger ci = P.fromInteger( c.getVal() );
        A = A.multiply( ci );
        B = B.multiply( ci );
        T = T.divide( ci );
        S = S.divide( ci );
        Ai = PolyUtil.integerFromModularCoefficients( fac, A );
        Bi = PolyUtil.integerFromModularCoefficients( fac, B );
        // replace leading base coefficients
        ExpVector ea = Ai.leadingExpVector();
        ExpVector eb = Bi.leadingExpVector();
        Ai.val.put(ea,c);
        Bi.val.put(eb,c);

        // polynomials mod p
        GenPolynomial<ModInteger> Ap; 
        GenPolynomial<ModInteger> Bp;
        GenPolynomial<ModInteger> A1p; 
        GenPolynomial<ModInteger> B1p;
        GenPolynomial<ModInteger> Ep;
        GenPolynomial<ModInteger> Sp = S;
        GenPolynomial<ModInteger> Tp = T;

        // polynomials mod q
        GenPolynomial<ModInteger> Aq; 
        GenPolynomial<ModInteger> Bq;
        GenPolynomial<ModInteger> Eq;

        // polynomials over the integers
        GenPolynomial<BigInteger> E;
        GenPolynomial<BigInteger> Ea;
        GenPolynomial<BigInteger> Eb;
        GenPolynomial<BigInteger> Ea1;
        GenPolynomial<BigInteger> Eb1;
        GenPolynomial<BigInteger> Si;
        GenPolynomial<BigInteger> Ti;

        Si = PolyUtil.integerFromModularCoefficients( fac, S );
        Ti = PolyUtil.integerFromModularCoefficients( fac, T );

        Aq = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,Ai); 
        Bq = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,Bi); 

        while ( Mq.compareTo( M2 ) < 0 ) {
            // compute E=(C-AB)/q over the integers
            E = C.subtract( Ai.multiply(Bi) );
            //System.out.println("\nQi = " + Qi);
            //System.out.println("Ai = " + Ai);
            //System.out.println("Bi = " + Bi);
            //System.out.println("E  = " + E);
            if ( E.isZERO() ) {
               //System.out.println("leaving on zero error");
               logger.info("leaving on zero error");
               break;
            }
            E = E.divide( Qi );
            //System.out.println("E  = " + E);
            // E mod p
            Ep = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,E); 
            //++System.out.println("Ep  = " + Ep);
            logger.info("Ep = " + Ep);

            // construct approximation mod p
            Ap = Sp.multiply( Ep ); // S,T ++ T,S
            Bp = Tp.multiply( Ep );
            //System.out.println("Ap = " + Ap);
            //System.out.println("Bp = " + Bp);
            //System.out.println("A*Ap+B*Bp-Ep= " + A.multiply(Ap).sum(B.multiply(Bp)).subtract(Ep) );
            GenPolynomial<ModInteger>[] QR;
            QR = Ap.divideAndRemainder( Bq );
            GenPolynomial<ModInteger> Qp;
            GenPolynomial<ModInteger> Rp;
            Qp = QR[0];
            Rp = QR[1];
            A1p = Rp;
            B1p = Bp.sum( Aq.multiply( Qp ) );
            //++System.out.println("A1p  = " + A1p);
            //System.out.println("Qp   = " + Qp);
            //++System.out.println("B1p  = " + B1p);
            //System.out.println("Qp*B+Rp-Ap    = " + Qp.multiply(B).sum(Rp).subtract(Ap));
            //System.out.println("A*A1p+B*B1p-Ep= " + A.multiply(A1p).sum(B.multiply(B1p)).subtract(Ep) );

            // construct q-adic approximation, convert to integer
            Ea = PolyUtil.integerFromModularCoefficients(fac,A1p);
            Eb = PolyUtil.integerFromModularCoefficients(fac,B1p);
            Ea1 = Ea.multiply( Qi );
            Eb1 = Eb.multiply( Qi );
            //System.out.println("Ea1 = " + Ea1);
            //System.out.println("Eb1 = " + Eb1);
            Ea = Ai.sum( Eb1 ); // Eb1 and Ea1 are required
            Eb = Bi.sum( Ea1 ); //--------------------------
            //System.out.println("Ea = " + Ea);
            //System.out.println("Eb = " + Eb);
            if ( Ea.degree(0)+Eb.degree(0) > C.degree(0) ) { // debug
               throw new RuntimeException("deg(A)+deg(B) > deg(C)");
            }
            Ai = Ea;
            Bi = Eb;

            // gcd representation factors error --------------------------------
            // compute E=(1-SA-TB)/q over the integers
            E = fac.getONE();
            E = E.subtract( Si.multiply(Ai) ).subtract( Ti.multiply(Bi) );
            //System.out.println("Si = " + Si);
            //System.out.println("Ti = " + Ti);
            //System.out.println("E2  = " + E);
            E = E.divide( Qi );
            //System.out.println("E2  = " + E);
            // E mod q
            Ep = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,E); 
            //++System.out.println("Ep2  = " + Ep);
            logger.info("Ep2 = " + Ep);

            // construct approximation mod q
            Ap = Sp.multiply( Ep ); // S,T ++ T,S
            Bp = Tp.multiply( Ep );
            //System.out.println("Ap = " + Ap);
            //System.out.println("Bp = " + Bp);
            //System.out.println("A*Ap+B*Bp-Ep= " + A.multiply(Ap).sum(B.multiply(Bp)).subtract(Ep) );
            //GenPolynomial<ModInteger>[] QR;
            QR = Bp.divideAndRemainder( Aq ); // Ai == A mod p ?
            //GenPolynomial<ModInteger> Qp;
            //GenPolynomial<ModInteger> Rp;
            Qp = QR[0];
            Rp = QR[1];
            B1p = Rp;
            A1p = Ap.sum( Bq.multiply( Qp ) );
            //System.out.println("A1p  = " + A1p);
            //System.out.println("Qp   = " + Qp);
            //System.out.println("B1p  = " + B1p);

            if ( debug ) {
               Eq = A1p.multiply(Aq).sum(B1p.multiply(Bq)).subtract(Ep);
               if ( !Eq.isZERO() ) {
                  System.out.println("A*A1p+B*B1p-Ep2 != 0 " + Eq );
                  throw new RuntimeException("A*A1p+B*B1p-Ep2 != 0 mod " + Q.getModul());
               }
            }

            // construct q-adic approximation, convert to integer
            Ea = PolyUtil.integerFromModularCoefficients(fac,A1p);
            Eb = PolyUtil.integerFromModularCoefficients(fac,B1p);
            Ea1 = Ea.multiply( Qi );
            Eb1 = Eb.multiply( Qi );
            //System.out.println("Ea1 = " + Ea1);
            //System.out.println("Eb1 = " + Eb1);
            Ea = Si.sum( Ea1 ); // Eb1 and Ea1 are required
            Eb = Ti.sum( Eb1 ); //--------------------------
            //System.out.println("Ea = " + Ea);
            //System.out.println("Eb = " + Eb);
            Si = Ea;
            Ti = Eb;

            // prepare for next iteration
            //Qi = new BigInteger( Q.getModul().multiply( P.getModul() ) );
            Mq = Qi;
            Qi = new BigInteger( Q.getModul().multiply( Q.getModul() ) );
            Q = new ModIntegerRing( Qi.getVal() );
            //System.out.println("Q = " + Q.getModul());
            qfac = new GenPolynomialRing(Q,pfac.nvar,pfac.tord,pfac.vars);

            Aq = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,Ai);
            Bq = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,Bi);
            Sp = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,Si);
            Tp = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,Ti);
            //System.out.println("Aq = " + Aq);
            //System.out.println("Bq = " + Bq);
            //System.out.println("Sp = " + Sp);
            //System.out.println("Tp = " + Tp);

            if ( debug ) {
               E = Ai.multiply(Si).sum( Bi.multiply(Ti) );
               Eq = PolyUtil.<ModInteger>fromIntegerCoefficients(qfac,E); 
               if ( !Eq.isONE() ) {
                  System.out.println("Ai*Si+Bi*Ti=1 " + Eq );
                  throw new RuntimeException("Ai*Si+Bi*Ti != 1 mod " + Q.getModul());
               }
            }
        }
        //System.out.println("Qi at exit = " + Qi);

        GreatestCommonDivisorAbstract<BigInteger> ufd
            = new GreatestCommonDivisorPrimitive<BigInteger>();

        // remove normalization
        BigInteger ai = ufd.baseContent(Ai);
        //System.out.println("ai = " + ai);
        //System.out.println("c  = " + c);
        Ai = Ai.divide( ai );
        BigInteger bi = c.divide(ai);
        //System.out.println("bi = " + bi);
        Bi = Bi.divide( bi ); // divide( c/a )

        AB[0] = Ai;
        AB[1] = Bi;
        return AB;
    }

}