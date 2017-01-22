/*
 * $Id$
 */

package edu.jas.fd;


import org.apache.log4j.Logger;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * (Non-unique) factorization domain greatest common divisor common
 * algorithms with monic polynomial remainder sequence. Fake
 * implementation always returns 1 for any gcds.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class GreatestCommonDivisorLR<C extends GcdRingElem<C>> extends GreatestCommonDivisorAbstract<C> {


    private static final Logger logger = Logger.getLogger(GreatestCommonDivisorLR.class);


    private static final boolean debug = true; //logger.isDebugEnabled();


    /**
     * Constructor.
     * @param cf coefficient ring.
     */
    public GreatestCommonDivisorLR(RingFactory<C> cf) {
        super(cf);
    }


    /**
     * Univariate GenSolvablePolynomial greatest common divisor. Uses
     * pseudoRemainder for remainder.
     * @param P univariate GenSolvablePolynomial.
     * @param S univariate GenSolvablePolynomial.
     * @return [P,S,coP,coS,left,right] with left * coP * right = P and left * coS * right = S.
     */
    public GCDcoFactors<C> leftRightBaseGcd(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        if (S == null || P == null) {
            throw new IllegalArgumentException("null polynomials not allowed");
        }
        GenSolvablePolynomialRing<C> ring = P.ring;
        if (ring.nvar > 1) {
            throw new IllegalArgumentException("no univariate polynomial");
        }
        GCDcoFactors<C> ret;
        if (P.isZERO()|| S.isZERO()) {
            ret = new GCDcoFactors<C>(P, S, P, S, ring.getONE(), ring.getONE() );
            return ret;
        }
        // compute on coefficients
        C contP = leftBaseContent(P);
        C contS = leftBaseContent(S);
        C contPS = contP.leftGcd(contS);
        if (contPS.signum() < 0) {
            contPS = contPS.negate();
        }
        if (debug) {
            System.out.println("contP = " + contP + ", contS = " + contS + ", leftGcd(contP,contS) = " + contPS);
            C r1 = contP.leftDivide(contPS);
            boolean t = contPS.multiply(r1).equals(contP);
            if (! t) {
                System.out.println("r1: " + r1 + " * " + contPS + " != " + contP + ", r1*cP=" + contPS.multiply(r1));
            }
            C r2 = contS.leftDivide(contPS);
            t = contPS.multiply(r2).equals(contS);
            if (! t) {
                System.out.println("r2: " + r2 + " * " + contPS + " != " + contS + ", r2*cS=" + contPS.multiply(r2));
            }
            System.out.println("leftGcd(contP,contS) = " + contPS);
        }
        GenSolvablePolynomial<C> p = (GenSolvablePolynomial<C>) P.leftDivideCoeff(contP);
        GenSolvablePolynomial<C> s = (GenSolvablePolynomial<C>) S.leftDivideCoeff(contS);
        if (debug) {
            boolean t = p.multiplyLeft(contP).equals(P);
            if (! t) {
                System.out.println("p: " + p + " * " + contP + " != " + P + ", p*cP=" + p.multiplyLeft(contP));
            }
            t = s.multiplyLeft(contS).equals(S); 
            if (! t) {
                System.out.println("s: " + s + " * " + contS + " != " + S + ", s*cS=" + s.multiplyLeft(contS));
            }
        }
        // compute on main variable
        if (p.isONE()) {
            ret = new GCDcoFactors<C>(P, S, p, s, ring.valueOf(contPS), ring.getONE() );
            return ret;
        }
        if (s.isONE()) {
            ret = new GCDcoFactors<C>(P, S, p, s, ring.valueOf(contPS), ring.getONE() );
            return ret;
        }
        boolean field = ring.coFac.isField();
        GenSolvablePolynomial<C> r = p;
        GenSolvablePolynomial<C> q = s;
        GenSolvablePolynomial<C> x;
        System.out.println("baseGCD: q = " + q + ", r = " + r);
        while (!r.isZERO()) {
            x = FDUtil.<C> leftBaseSparsePseudoRemainder(q, r);
            q = r;
            if (field) {
                r = x.monic();
                System.out.println("baseGCD: lc(q) = " + q.leadingBaseCoefficient() + ", lc(r) = " + r.leadingBaseCoefficient());
            } else {
                r = x;
            }
            //System.out.println("baseGCD: q = " + q + ", r = " + r);
        }
        q = (GenSolvablePolynomial<C>) q.abs();
        q = leftBasePrimitivePart(q);
        //q = rightBasePrimitivePart(q);
        System.out.println("baseGCD: q = " + q + ", r = " + r);
        //GenSolvablePolynomial<C> p1 = (GenSolvablePolynomial<C>) p.leftDivide(q);
        //GenSolvablePolynomial<C> s1 = (GenSolvablePolynomial<C>) s.leftDivide(q);
        p = (GenSolvablePolynomial<C>) P.leftDivideCoeff(contPS); // not contP here
        s = (GenSolvablePolynomial<C>) S.leftDivideCoeff(contPS); // not contS here
        GenSolvablePolynomial<C> p1 = FDUtil.<C> leftBasePseudoQuotient(p, q); // TODO
        GenSolvablePolynomial<C> s1 = FDUtil.<C> leftBasePseudoQuotient(s, q); // TODO 
        //System.out.println("p1 = " + p1 + ", s1 = " + s1);
        //p1 = leftBasePrimitivePart(p1);
        //s1 = leftBasePrimitivePart(s1);
        //System.out.println("pp(p1) = " + p1 + ", pp(s1) = " + s1);
        if (debug) {
            boolean t = p1.multiply(q).equals(p);
            if (! t) {
                System.out.println("p1: " + p1 + " * " + q + " != " + p);
                System.out.println("pp(p1*q): " + leftBasePrimitivePart(p1.multiply(q)).abs() + " != " + leftBasePrimitivePart(p).abs());
            }
            t = s1.multiply(q).equals(s);
            if (! t) {
                System.out.println("s1: " + s1 + " * " + q + " != " + s);
                System.out.println("pp(s1*q): " + leftBasePrimitivePart(s1.multiply(q)).abs() + " != " + leftBasePrimitivePart(s).abs());
            }
            t = p.multiplyLeft(contPS).equals(P); // contPS q p1 == P
            if (! t) {
                System.out.println("p1P: " + contPS + " * " + p + " != " + P);
            }
            t = s.multiplyLeft(contPS).equals(S);
            if (! t) {
                System.out.println("s1S: " + contPS + " * " + s + " != " + S);
            }
            System.out.println("isField: " + field);
        }
        ret = new GCDcoFactors<C>(P, S, p1, s1, ring.valueOf(contPS), q );
        return ret;
    }


    /**
     * Univariate GenSolvablePolynomial greatest common divisor. Uses
     * pseudoRemainder for remainder.
     * @param P univariate GenSolvablePolynomial.
     * @param S univariate GenSolvablePolynomial.
     * @return 1 = gcd(P,S) with P = P'*gcd(P,S) and S = S'*gcd(P,S).
     */
    @Override
    public GenSolvablePolynomial<C> leftBaseGcd(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        if (P.ring.nvar > 1) {
            throw new IllegalArgumentException(this.getClass().getName() + " no univariate polynomial");
        }
        return P.ring.getONE();
    }


    /**
     * Univariate GenSolvablePolynomial right greatest common divisor. Uses
     * pseudoRemainder for remainder.
     * @param P univariate GenSolvablePolynomial.
     * @param S univariate GenSolvablePolynomial.
     * @return 1 = gcd(P,S) with P = gcd(P,S)*P' and S = gcd(P,S)*S'.
     */
    @Override
    public GenSolvablePolynomial<C> rightBaseGcd(GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        if (P.ring.nvar > 1) {
            throw new IllegalArgumentException(this.getClass().getName() + " no univariate polynomial");
        }
        return P.ring.getONE();
    }


    /**
     * Univariate GenSolvablePolynomial left recursive greatest common divisor.
     * Uses pseudoRemainder for remainder.
     * @param P univariate recursive GenSolvablePolynomial.
     * @param S univariate recursive GenSolvablePolynomial.
     * @return 1 = gcd(P,S) with P = P'*gcd(P,S)*p and S = S'*gcd(P,S)*s, where
     *         deg_main(p) = deg_main(s) == 0.
     */
    @Override
    public GenSolvablePolynomial<GenPolynomial<C>> leftRecursiveUnivariateGcd(
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
        return P.ring.getONE();
    }


    /**
     * Univariate GenSolvablePolynomial right recursive greatest common divisor.
     * Uses pseudoRemainder for remainder.
     * @param P univariate recursive GenSolvablePolynomial.
     * @param S univariate recursive GenSolvablePolynomial.
     * @return 1 = gcd(P,S) with P = p*gcd(P,S)*P' and S = s*gcd(P,S)*S', where
     *         deg_main(p) = deg_main(s) == 0.
     */
    @Override
    public GenSolvablePolynomial<GenPolynomial<C>> rightRecursiveUnivariateGcd(
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
        return P.ring.getONE();
    }

}
