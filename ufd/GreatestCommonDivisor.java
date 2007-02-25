
/*
 * $Id$
 */

package edu.jas.ufd;

import java.util.Map;
import java.util.Collection;


import org.apache.log4j.Logger;


import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.GcdRingElem;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.ExpVector;


/**
 * Greatest common divisor algorithms.
 * @author Heinz Kredel
 */

public class GreatestCommonDivisor<C extends GcdRingElem<C> > {


    private static final Logger logger = Logger.getLogger(GreatestCommonDivisor.class);


    /**
     * Recursive representation. 
     * Represent as polynomial in i variables with coefficients in n-i variables.
     * @param rfac recursive polynomial ring factory.
     * @return Recursive represenations of this in the ring rfac.
     */
    public GenPolynomial<GenPolynomial<C>> 
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
            //System.out.println("e = " + e);
            C a = y.getValue();
            System.out.println("a = " + a);
            ExpVector f = e.contract(0,i);
            ExpVector g = e.contract(i,e.length()-i);
            System.out.println("e = " + e + ", f = " + f + ", g = " + g );
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
     * @return distributed polynomial.
     */
    public GenPolynomial<C> 
        distribute( GenPolynomialRing<C> dfac,
                    GenPolynomial<GenPolynomial<C>> B) {
        GenPolynomial<C> C = dfac.getZERO().clone();
        if ( B.isZERO() ) { 
            return C;
        }
        Map<ExpVector,C> Cm = C.getMap();
        for ( Map.Entry<ExpVector,GenPolynomial<C>> y: B.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            System.out.println("e = " + e);
            GenPolynomial<C> A = y.getValue();
            System.out.println("A = " + A);
            for ( Map.Entry<ExpVector,C> x: A.getMap().entrySet() ) {
                ExpVector f = x.getKey();
                System.out.println("f = " + f);
                C b = x.getValue();
                System.out.println("b = " + b);
                ExpVector g = e.combine(f);
                System.out.println("e = " + e + ", f = " + f + ", g = " + g);
                if ( Cm.get(g) != null ) {
                   throw new RuntimeException(this.getClass().getName()
                                       + " debug error");
                }
                Cm.put( g, b );
            }
        }
        return C;
    }


    /**
     * GenPolynomial base coefficient content.
     * @param P GenPolynomial.
     * @return cont(P).
     */
    public C baseContent(GenPolynomial<C> P) {
        if ( P == null ) {
           throw new RuntimeException(this.getClass().getName()
                                       + " P != null");
        }
        if ( P.isZERO() ) {
            return P.ring.getZEROCoefficient();
        }
        C d = null;
        for ( C c : P.getMap().values() ) {
            if ( d == null ) {
               d = c;
            } else {
               d = d.gcd(c);
            }
            if ( d.isONE() ) {
               return d; 
            }
        }
        return d.abs(); 
    }


    /**
     * GenPolynomial base coefficient primitive part.
     * @param P GenPolynomial.
     * @return pp(P).
     */
    public GenPolynomial<C> basePrimitivePart(GenPolynomial<C> P) {
        if ( P == null ) {
           throw new RuntimeException(this.getClass().getName()
                                       + " P != null");
        }
        if ( P.isZERO() ) {
            return P;
        }
        C d = baseContent( P );
        return P.divide(d);
    }


    /**
     * GenPolynomial content.
     * @param P recursive GenPolynomial.
     * @return cont(P).
     */
    public GenPolynomial<C> content( GenPolynomial<GenPolynomial<C>> P ) {
        if ( P == null ) {
           throw new RuntimeException(this.getClass().getName()
                                       + " P != null");
        }
        if ( P.isZERO() ) {
            return P.ring.getZEROCoefficient();
        }
        GenPolynomial<C> d = null;
        for ( GenPolynomial<C> c : P.getMap().values() ) {
            if ( d == null ) {
               d = c;
            } else {
               d = basePseudoGcd(d,c); // tofix
            }
            if ( d.isONE() ) {
               return d; 
            }
        }
        return d.abs(); 
    }


    /**
     * GenPolynomial primitive part.
     * @param P GenPolynomial.
     * @return pp(P).
     */
    public GenPolynomial<GenPolynomial<C>> 
        primitivePart( GenPolynomial<GenPolynomial<C>> P ) {
        if ( P == null ) {
           throw new RuntimeException(this.getClass().getName()
                                       + " P != null");
        }
        if ( P.isZERO() ) {
            return P;
        }
        GenPolynomial<C> d = content( P );
        if ( d.isONE() ) {
            return P;
        }
        GenPolynomial<GenPolynomial<C>> pp = P.divide( d ); 
        return pp;
    }


    /*
        GenPolynomialRing<C> efac = P.ring;
        GenPolynomialRing<C> dfac = pfac.extend(efac.nvar);
        GenPolynomialRing<GenPolynomial<C>> rfac 
           = new GenPolynomialRing<GenPolynomial<C>>(pfac,efac.nvar,efac.tord);
        GenPolynomial<GenPolynomial<C>> Pr = recursive(rfac,P);
        GenPolynomial<GenPolynomial<C>> Pd = Pr.divide( d ); 
        GenPolynomial<C> pp = distribute( dfac, Pd );
    */


    /**
     * GenPolynomial pseudo remainder.
     * For univariate polynomials.
     * @param P GenPolynomial.
     * @param S nonzero GenPolynomial.
     * @return remainder with ldcf(S)<sup>m</sup> P = quotient * S + remainder.
     * @see #remainder(edu.jas.poly.GenPolynomial).
     */
    public GenPolynomial<C> basePseudoRemainder( GenPolynomial<C> P, 
                                                 GenPolynomial<C> S ) {
        if ( S == null || S.isZERO() ) {
            throw new RuntimeException(this.getClass().getName()
                                       + " division by zero");
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
                r = r.multiply( c );
                h = S.multiply( a, f );
                r = r.subtract( h );
            } else {
                break;
            }
        }
        return r;
    }


    /**
     * GenPolynomial pseudo remainder.
     * For recursive polynomials.
     * @param P recursive GenPolynomial.
     * @param S nonzero recursive GenPolynomial.
     * @return remainder with ldcf(S)<sup>m</sup> P = quotient * S + remainder.
     * @see #remainder(edu.jas.poly.GenPolynomial).
     */
    public GenPolynomial<GenPolynomial<C>> 
           pseudoRemainder( GenPolynomial<GenPolynomial<C>> P, 
                            GenPolynomial<GenPolynomial<C>> S) {
        if ( S == null || S.isZERO() ) {
            throw new RuntimeException(this.getClass().getName()
                                       + " division by zero");
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
                r = r.multiply( c );
                h = S.multiply( a, f );
                r = r.subtract( h );
            } else {
                break;
            }
        }
        return r;
    }


    /**
     * GenPolynomial pseudo greatest comon divisor.
     * Uses pseudoRemainder for remainder.
     * @param P recursive GenPolynomial.
     * @param S recursive GenPolynomial.
     * @return gcd(P,S).
     */
    public GenPolynomial<GenPolynomial<C>> 
        pseudoGcd( GenPolynomial<GenPolynomial<C>> P,
                   GenPolynomial<GenPolynomial<C>> S ) {
        if ( S == null || S.isZERO() ) {
            return P;
        }
        if ( P == null || P.isZERO() ) {
            return S;
        }
        GenPolynomial<C> a = content(P);
        GenPolynomial<C> b = content(S);
        GenPolynomial<C> d = basePseudoGcd(a,b); // tofix

        GenPolynomial<GenPolynomial<C>> x;
        GenPolynomial<GenPolynomial<C>> q = P.divide(a);
        GenPolynomial<GenPolynomial<C>> r = S.divide(b);
        while ( !r.isZERO() ) {
            x = pseudoRemainder(q,r);
            q = r;
            r = primitivePart( x );
        }
        return q.multiply(d); 
    }


    /**
     * GenPolynomial pseudo greatest comon divisor.
     * Uses pseudoRemainder for remainder.
     * Correct only for univariate polynomials.
     * Returns 1 for multivariate polynomials.
     * (which is a good guess for random polynomials).
     * @param P GenPolynomial.
     * @param S GenPolynomial.
     * @return pgcd(P,S).
     */
    public GenPolynomial<C> basePseudoGcd( GenPolynomial<C> P,
                                           GenPolynomial<C> S ) {
        if ( S == null || S.isZERO() ) {
            return P;
        }
        if ( P == null || P.isZERO() ) {
            return S;
        }
        if ( P.ring.nvar > 1 ) {
           logger.info("pseudoGcd only for univaraite polynomials");
           // guess
           return P.ring.getONE();
        }
        GenPolynomial<C> x;
        GenPolynomial<C> q = P;
        GenPolynomial<C> r = S;
        while ( !r.isZERO() ) {
            x = basePseudoRemainder(q,r);
            q = r;
            r = basePrimitivePart( x );
        }
        return q; 
    }

}
