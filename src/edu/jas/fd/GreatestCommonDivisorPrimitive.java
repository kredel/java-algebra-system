/*
 * $Id$
 */

package edu.jas.fd;


import org.apache.log4j.Logger;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;


/**
 * (Non-unique) factorization domain greatest common divisor common algorithms
 * with primitive polynomial remainder sequence.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class GreatestCommonDivisorPrimitive<C extends GcdRingElem<C>> extends GreatestCommonDivisorAbstract<C> {


    private static final Logger logger = Logger.getLogger(GreatestCommonDivisorPrimitive.class);


    private final boolean debug = true; //logger.isDebugEnabled();


    /**
     * Univariate GenSolvablePolynomial greatest common divisor. Uses
     * pseudoRemainder for remainder.
     * @param P univariate GenSolvablePolynomial.
     * @param S univariate GenSolvablePolynomial.
     * @return gcd(P,S).
     */
    @Override
    public GenSolvablePolynomial<C> baseGcd(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        if (P.ring.nvar > 1) {
            throw new IllegalArgumentException(this.getClass().getName() + " no univariate polynomial");
        }
        boolean field = P.ring.coFac.isField();
        long e = P.degree(0);
        long f = S.degree(0);
        GenSolvablePolynomial<C> q;
        GenSolvablePolynomial<C> r;
        if (f > e) {
            r = P;
            q = S;
            long g = f;
            f = e;
            e = g;
        } else {
            q = P;
            r = S;
        }
        if (debug) {
            logger.debug("degrees: e = " + e + ", f = " + f);
        }
        C c;
        if (field) {
            r = r.monic();
            q = q.monic();
        }
        r = (GenSolvablePolynomial<C>) r.abs();
        q = (GenSolvablePolynomial<C>) q.abs();
        C a = baseContent(r);
        C b = baseContent(q);
        c = gcd(a, b); // indirection
        r = divide(r, a); // indirection
        q = divide(q, b); // indirection
        if (r.isONE()) {
            return r.multiply(c);
        }
        if (q.isONE()) {
            return q.multiply(c);
        }
        GenSolvablePolynomial<C> x;
        //System.out.println("q = " + q);
        //System.out.println("r = " + r);
        while (!r.isZERO()) {
            x = FDUtil.<C> baseSparsePseudoRemainder(q, r);
            q = r;
            r = basePrimitivePart(x);
            if (field) {
                r = r.monic();
            }
            //System.out.println("q = " + q);
            //System.out.println("r = " + r);
        }
        return (GenSolvablePolynomial<C>) (q.multiply(c)).abs();
    }


    /**
     * Univariate GenSolvablePolynomial recursive greatest comon divisor. Uses
     * pseudoRemainder for remainder.
     * @param P univariate recursive GenSolvablePolynomial.
     * @param S univariate recursive GenSolvablePolynomial.
     * @return gcd(P,S). 
     */
    @Override
    public GenSolvablePolynomial<GenPolynomial<C>> recursiveUnivariateGcd(
                    GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        if (P.ring.nvar > 1) {
            throw new IllegalArgumentException("no univariate polynomial");
        }
        boolean field = P.leadingBaseCoefficient().ring.coFac.isField();
        long e = P.degree(0);
        long f = S.degree(0);
        GenSolvablePolynomial<GenPolynomial<C>> q;
        GenSolvablePolynomial<GenPolynomial<C>> r;
        if (f > e) {
            r = P;
            q = S;
            long g = f;
            f = e;
            e = g;
        } else {
            q = P;
            r = S;
        }
        if (debug) {
            logger.debug("degrees: e = " + e + ", f = " + f);
        }
        if (field) {
            r = PolyUtil.<C> monic(r);
            q = PolyUtil.<C> monic(q);
        } 
        r = (GenSolvablePolynomial<GenPolynomial<C>>) r.abs();
        q = (GenSolvablePolynomial<GenPolynomial<C>>) q.abs();
        GenSolvablePolynomial<C> a = recursiveContent(r);
        GenSolvablePolynomial<C> b = recursiveContent(q);
        logger.info("recCont a = " + a); // + ", r = " + r);
        logger.info("recCont b = " + b); // + ", q = " + q);

        GenSolvablePolynomial<C> c = gcd(a, b); // go to recursion
        logger.info("Gcd(contents) c = " + c);
        r = FDUtil.<C> recursiveDivide(r, a);
        q = FDUtil.<C> recursiveDivide(q, b);
        if (r.isONE()) {
            return r.multiply(c);
        }
        if (q.isONE()) {
            return q.multiply(c);
        }
        GenSolvablePolynomial<GenPolynomial<C>> x;
        if (debug) {
            logger.debug("r.ring = " + r.ring.toScript());
        }
        while (!r.isZERO()) { //&& r.degree()>0
            if (debug) {
                logger.info("deg(q) = " + q.degree() + ", deg(r) = " + r.degree());
            }
            x = FDUtil.<C> recursiveSparsePseudoRemainder(q, r);
            q = r;
            r = recursivePrimitivePart(x);
            if (field) {
                r = PolyUtil.<C> monic(r);
            } 
        }
        if (debug) {
            //logger.info("var = " + P.ring.varsToString() + ", gcd(pp) = " + q);
            logger.info(P.ring.toScript() + ": gcd(pp) = " + q);
        }
        q = (GenSolvablePolynomial<GenPolynomial<C>>) q.abs().multiply(c);
        return q;
    }

}
