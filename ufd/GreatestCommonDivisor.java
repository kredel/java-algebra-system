
/*
 * $Id$
 */

package edu.jas.ufd;

import java.util.Map;
import java.util.Collection;


import org.apache.log4j.Logger;


import edu.jas.structure.RingElem;
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
        }
        return d; 
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
     * @param P GenPolynomial.
     * @return cont(P).
     */
    public GenPolynomial<C> content( GenPolynomialRing<C> pfac, 
                                     GenPolynomial<C> P) {
        if ( P == null ) {
           throw new RuntimeException(this.getClass().getName()
                                       + " P != null");
        }
        if ( P.isZERO() ) {
            return pfac.getZERO();
        }
        GenPolynomial<C> d = null;
        for ( GenPolynomial<C> c : P.contract(pfac).values() ) {
            if ( d == null ) {
                d = c;
            } else {
                d = pseudoGcd(d,c);
            }
        }
        return d; 
    }


    /**
     * GenPolynomial pseudo remainder.
     * Meaningful only for univariate polynomials but works 
     * in any case.
     * @param P GenPolynomial.
     * @param S nonzero GenPolynomial.
     * @return remainder with ldcf(S)<sup>m</sup> P = quotient * S + remainder.
     * @see #remainder(edu.jas.poly.GenPolynomial).
     */
    public GenPolynomial<C> pseudoRemainder(GenPolynomial<C> P, 
                                            GenPolynomial<C> S) {
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
     * GenPolynomial pseudo greatest comon divisor.
     * Uses pseudoRemainder for remainder.
     * Correct only for univariate polynomials.
     * Returns 1 for multivariate polynomials.
     * (which is a good guess for random polynomials).
     * @param P GenPolynomial.
     * @param S GenPolynomial.
     * @return pgcd(P,S).
     */
    public GenPolynomial<C> pseudoGcd(GenPolynomial<C> P,
                                      GenPolynomial<C> S) {
        if ( S == null || S.isZERO() ) {
            return P;
        }
        if ( P == null || P.isZERO() ) {
            return S;
        }
        if ( P.ring.nvar != 1 ) {
            logger.info("gcd only for univariate polynomials");
            // keep going
            return P.ring.getONE();
        }
        GenPolynomial<C> x;
        GenPolynomial<C> q = P;
        GenPolynomial<C> r = S;
        while ( !r.isZERO() ) {
            x = q.pseudoRemainder(r);
            q = r;
            r = x;
        }
        return q; // p.primitivePart() //q.monic(); // normalize
    }


}
