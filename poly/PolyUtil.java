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
     * @return S = cra(A,B), with S mod A.coFac.modul == A 
     *                       and S mod B.coFac.modul == B. 
     */
    public static //<C extends RingElem<C>>
        GenPolynomial<ModInteger> 
        chineseRemainder( GenPolynomialRing<ModInteger> fac,
                          GenPolynomial<ModInteger> A,
                          ModInteger mi,
                          GenPolynomial<ModInteger> B ) {
        ModInteger cfac = fac.coFac.getZERO(); // get RingFactory
        GenPolynomial<ModInteger> S = fac.getZERO().clone(); 
        GenPolynomial<ModInteger> Ap = A.clone(); 
        SortedMap<ExpVector,ModInteger> av = Ap.getMap();
        SortedMap<ExpVector,ModInteger> bv = B.getMap();
        SortedMap<ExpVector,ModInteger> sv = S.getMap();
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
        long[] val = e.getval();
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
        // assert decreasing exponents, i.e. compatible term order
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
        Map<ExpVector,GenPolynomial<C>> Evm = Ev.getMap();
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
        Map<ExpVector,C> Bm = B.getMap();

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
        Map<ExpVector,C> Bm = B.getMap();

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
        SortedMap<ExpVector,GenPolynomial<C>> av = Ap.getMap();
        SortedMap<ExpVector,C> bv = B.getMap();
        SortedMap<ExpVector,GenPolynomial<C>> sv = S.getMap();
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

}