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
 * with monic polynomial remainder sequence.
 * If C is a field, then the monic PRS (on coefficients) is computed otherwise
 * no simplifications in the reduction are made.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class GreatestCommonDivisorSimple<C extends GcdRingElem<C>> extends GreatestCommonDivisorAbstract<C> {


    private static final Logger logger = Logger.getLogger(GreatestCommonDivisorSimple.class);


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
            c = P.ring.getONECoefficient();
        } else {
            r = (GenSolvablePolynomial<C>) r.abs();
            q = (GenSolvablePolynomial<C>) q.abs();
            C a = baseContent(r);
            C b = baseContent(q);
            c = gcd(a, b); // indirection
            r = divide(r, a); // indirection
            q = divide(q, b); // indirection
        }
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
            if (field) {
                r = x.monic();
            } else {
                r = x;
            }
            //System.out.println("q = " + q);
            //System.out.println("r = " + r);
        }
        q = basePrimitivePart(q);
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
    public GenSolvablePolynomial<GenPolynomial<C>> 
           recursiveUnivariateGcd(GenSolvablePolynomial<GenPolynomial<C>> P, 
                                  GenSolvablePolynomial<GenPolynomial<C>> S) {
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
        GenSolvablePolynomial<GenPolynomial<C>> q, r, x, qs, rs, qp, rp;
        if (f > e) {
            r = P;
            q = S;
            long g = f;
            f = e;
            e = g;
        } else if (f < e) {
            q = P;
            r = S;
        } else { // f == e
            if ( P.leadingBaseCoefficient().degree() > S.leadingBaseCoefficient().degree() ) {
                q = P;
                r = S;
	    } else {
                r = P;
                q = S;
            }
        }
        if (debug) {
            logger.debug("degrees: e = " + e + ", f = " + f);
        }
        if (field) {
            r = PolyUtil.<C> monic(r);
            q = PolyUtil.<C> monic(q);
        } else {
            r = (GenSolvablePolynomial<GenPolynomial<C>>) r.abs();
            q = (GenSolvablePolynomial<GenPolynomial<C>>) q.abs();
        }
        GenSolvablePolynomial<C> a = recursiveContent(r);
        logger.info("recCont a = " + a + ", r = " + r);
        r = FDUtil.<C> recursiveDivideRightEval(r, a);
        logger.info("recCont r/a = " + r);

        GenSolvablePolynomial<C> b = recursiveContent(q);
        logger.info("recCont b = " + b + ", q = " + q);
        q = FDUtil.<C> recursiveDivideRightEval(q, b);
        logger.info("recCont q/b = " + q);
        //r = FDUtil.<C> recursiveDivide(r, a);
        //r = FDUtil.<C> recursiveDivideRightPolynomial(r, a);
        //rr = FDUtil.<C> recursiveRightDivide(r, a);
        //q = FDUtil.<C> recursiveDivide(q, b);
        //q = FDUtil.<C> recursiveDivideRightPolynomial(q, b);
        //qr = FDUtil.<C> recursiveRightDivide(q, b);

        GenSolvablePolynomial<C> c = gcd(a, b); // go to recursion
        logger.info("Gcd(contents) c = " + c);
        if (r.isONE()) {
            return r.multiply(c);
        }
        if (q.isONE()) {
            return q.multiply(c);
        }
        if (debug) {
            logger.info("r.ring = " + r.ring.toScript());
        }
        rs = r; qs = q;
        while (!r.isZERO()) { //&& r.degree()>0
            if (debug) {
                logger.info("deg(q) = " + q.degree() + ", deg(r) = " + r.degree());
            }
            x = FDUtil.<C> recursiveSparsePseudoRemainder(q, r);
            //x = FDUtil.<C> recursiveRightSparsePseudoRemainder(q, r);
            q = r;
            if (field) {
                r = PolyUtil.<C> monic(x);
            } else {
                r = x;
            }
            logger.info("q = " + q + ", r = " + r);
            if (r.isConstant()) { // this should not happen since both polynomials are primitive
                return P.ring.getONE().multiply(c);
            }
        }
        logger.info("gcd(div) = " + q + ", rs = " + rs + ", qs = " + qs);
        if (debug) {
            //rp = FDUtil.<C> recursiveRightPseudoQuotient(rs, q);
            //qp = FDUtil.<C> recursiveRightPseudoQuotient(qs, q);
            rp = FDUtil.<C> recursiveSparsePseudoRemainder(rs, q);
            qp = FDUtil.<C> recursiveSparsePseudoRemainder(qs, q);
            if (!qp.isZERO()||!rp.isZERO()) {
                logger.info("gcd(div): rem(r,g) = " + rp + ", rem(q,g) = " + qp);
                rp = FDUtil.<C> recursivePseudoQuotient(rs, q);
                qp = FDUtil.<C> recursivePseudoQuotient(qs, q);
                logger.info("gcd(div): r/g = " + rp + ", q/g = " + qp);
                //logger.info("gcd(div): rp*g = " + rp.multiply(q) + ", qp*g = " + qp.multiply(q));
                throw new RuntimeException("recGcd: not divisible");
            }
        }
        
        qp = recursivePrimitivePart(q);
        if (!qp.equals(q)) {
            logger.info("gcd(pp) = " + q + ", qp = " + qp); // + ", ring = " + P.ring.toScript());
        }
        q = qp;
        // no left: q = (GenSolvablePolynomial<GenPolynomial<C>>) q.multiply(c,P.ring.getONECoefficient()).abs();
        q = (GenSolvablePolynomial<GenPolynomial<C>>) q.multiply(c).abs();
        if (debug) {
            qs = FDUtil.<C> recursiveSparsePseudoRemainder(P, q);
            rs = FDUtil.<C> recursiveSparsePseudoRemainder(S, q);
            if (!qs.isZERO()||!rs.isZERO()) {
                System.out.println("recGcd, P  = " + P);
                System.out.println("recGcd, S  = " + S);
                System.out.println("recGcd, q  = " + q);
                System.out.println("recGcd, qs = " + qs);
                System.out.println("recGcd, rs = " + rs);
                throw new RuntimeException("recGcd: not divisible");
            }
        }
        return q;
    }

}
