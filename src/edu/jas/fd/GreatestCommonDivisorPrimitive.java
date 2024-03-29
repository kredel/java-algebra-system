/*
 * $Id$
 */

package edu.jas.fd;


import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * (Non-unique) factorization domain greatest common divisor common algorithms
 * with primitive polynomial remainder sequence.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class GreatestCommonDivisorPrimitive<C extends GcdRingElem<C>> extends
                GreatestCommonDivisorAbstract<C> {


    private static final Logger logger = LogManager.getLogger(GreatestCommonDivisorPrimitive.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     * @param cf coefficient ring.
     */
    public GreatestCommonDivisorPrimitive(RingFactory<C> cf) {
        super(cf);
    }


    /**
     * Univariate GenSolvablePolynomial greatest common divisor. Uses
     * pseudoRemainder for remainder.
     * @param P univariate GenSolvablePolynomial.
     * @param S univariate GenSolvablePolynomial.
     * @return gcd(P,S) with P = P'*gcd(P,S) and S = S'*gcd(P,S).
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
        boolean field = P.ring.coFac.isField();
        System.out.println("baseGcd: field = " + field + ", is " + P.ring.coFac.toScript());
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
            logger.debug("degrees: e = {}, f = {}", e, f);
        }
        C c;
        if (field) {
            r = r.monic();
            q = q.monic();
            c = P.ring.getONECoefficient();
        } else {
            r = (GenSolvablePolynomial<C>) r.abs();
            q = (GenSolvablePolynomial<C>) q.abs();
            C a = leftBaseContent(r);
            C b = leftBaseContent(q);
            r = divide(r, a); // indirection
            q = divide(q, b); // indirection
            c = gcd(a, b); // indirection
        }
        System.out.println("baseCont: gcd(cont) = " + c);
        if (r.isONE()) {
            return r.multiplyLeft(c);
        }
        if (q.isONE()) {
            return q.multiplyLeft(c);
        }
        GenSolvablePolynomial<C> x;
        logger.info("baseGCD: q = {}", q);
        logger.info("baseGCD: r = {}", r);
        System.out.println("baseGcd: rem = " + r);
        while (!r.isZERO()) {
            x = FDUtil.<C> leftBaseSparsePseudoRemainder(q, r);
            q = r;
            if (field) {
                r = x.monic();
            } else {
                r = leftBasePrimitivePart(x);
            }
            System.out.println("baseGcd: rem = " + r);
            logger.info("baseGCD: q = {}", q);
            logger.info("baseGCD: r = {}", r);
        }
        System.out.println("baseGcd: quot = " + q);
        q = leftBasePrimitivePart(q);
        logger.info("baseGCD: pp(q) = {}", q);
        return (GenSolvablePolynomial<C>) (q.multiply(c)).abs();
    }


    /**
     * Univariate GenSolvablePolynomial right greatest common divisor. Uses
     * pseudoRemainder for remainder.
     * @param P univariate GenSolvablePolynomial.
     * @param S univariate GenSolvablePolynomial.
     * @return gcd(P,S) with P = gcd(P,S)*P' and S = gcd(P,S)*S'.
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
            logger.debug("degrees: e = {}, f = {}", e, f);
        }
        C c;
        if (field) {
            r = r.monic();
            q = q.monic();
            c = P.ring.getONECoefficient();
        } else {
            r = (GenSolvablePolynomial<C>) r.abs();
            q = (GenSolvablePolynomial<C>) q.abs();
            C a = leftBaseContent(r);
            C b = leftBaseContent(q);
            r = divide(r, a); // indirection
            q = divide(q, b); // indirection
            c = gcd(a, b); // indirection
        }
        //System.out.println("baseCont: gcd(cont) = " + b);
        if (r.isONE()) {
            return r.multiply(c);
        }
        if (q.isONE()) {
            return q.multiply(c);
        }
        GenSolvablePolynomial<C> x;
        //System.out.println("baseGCD: q = " + q);
        //System.out.println("baseGCD: r = " + r);
        while (!r.isZERO()) {
            x = FDUtil.<C> rightBaseSparsePseudoRemainder(q, r);
            q = r;
            if (field) {
                r = x.monic();
            } else {
                r = rightBasePrimitivePart(x);
            }
            //System.out.println("baseGCD: q = " + q);
            //System.out.println("baseGCD: r = " + r);
        }
        q = leftBasePrimitivePart(q); // todo
        return (GenSolvablePolynomial<C>) (q.multiplyLeft(c)).abs();
    }


    /**
     * Univariate GenSolvablePolynomial left recursive greatest common divisor.
     * Uses pseudoRemainder for remainder.
     * @param P univariate recursive GenSolvablePolynomial.
     * @param S univariate recursive GenSolvablePolynomial.
     * @return gcd(P,S) with P = P'*gcd(P,S)*p and S = S'*gcd(P,S)*s, where
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
        //boolean field = P.leadingBaseCoefficient().ring.coFac.isField();
        boolean field = P.ring.coFac.isField();
        System.out.println("recursiveUnivGcd: field = " + field);
        long e = P.degree(0);
        long f = S.degree(0);
        GenSolvablePolynomial<GenPolynomial<C>> q, r, x, qs, rs;
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
            if (P.leadingBaseCoefficient().degree() > S.leadingBaseCoefficient().degree()) {
                q = P;
                r = S;
            } else {
                r = P;
                q = S;
            }
        }
        logger.debug("degrees: e = {}, f = {}", e, f);
        if (field) {
            r = PolyUtil.<C> monic(r);
            q = PolyUtil.<C> monic(q);
        } else {
            r = (GenSolvablePolynomial<GenPolynomial<C>>) r.abs();
            q = (GenSolvablePolynomial<GenPolynomial<C>>) q.abs();
        }
        GenSolvablePolynomial<C> a = leftRecursiveContent(r);
        System.out.println("recursiveUnivGcd: a = " + a);
        rs = FDUtil.<C> recursiveLeftDivide(r, a);
        System.out.println("recursiveUnivGcd: rs = " + rs);
        //logger.info("recCont a = {}, r = {}", a, r);
        if (!r.equals(rs.multiplyLeft(a))) { // todo: should be rs.multiplyLeft(a))
            System.out.println("recursiveUnivGcd: r         = " + r);
            System.out.println("recursiveUnivGcd: cont(r)   = " + a);
            System.out.println("recursiveUnivGcd: pp(r)     = " + rs);
            System.out.println("recursiveUnivGcd: pp(r)c(r) = " + rs.multiply(a));
            System.out.println("recursiveUnivGcd: c(r)pp(r) = " + rs.multiplyLeft(a));
            throw new RuntimeException("recursiveUnivGcd: r: not divisible");
        }
        r = rs;
        GenSolvablePolynomial<C> b = leftRecursiveContent(q);
        System.out.println("recursiveUnivGcd: b = " + b);
        qs = FDUtil.<C> recursiveLeftDivide(q, b);
        System.out.println("recursiveUnivGcd: qs = " + qs);
        //logger.info("recCont b = {}, q = {}", b, q);
        if (!q.equals(qs.multiplyLeft(b))) { // todo: should be qs.multiplyLeft(b))
            System.out.println("recursiveUnivGcd: q         = " + q);
            System.out.println("recursiveUnivGcd: cont(q)   = " + b);
            System.out.println("recursiveUnivGcd: pp(q)     = " + qs);
            System.out.println("recursiveUnivGcd: pp(q)c(q) = " + qs.multiply(b));
            System.out.println("recursiveUnivGcd: c(q)pp(q) = " + qs.multiplyLeft(b));
            throw new RuntimeException("recursiveUnivGcd: q: not divisible");
        }
        q = qs;
        GenSolvablePolynomial<C> c = leftGcd(a, b); // go to recursion
        //GenSolvablePolynomial<C> c = rightGcd(a, b); // go to recursion
        logger.info("Gcd(contents) c = {}, a = {}, b = {}", c, a, b);
        if (r.isONE()) {
            return r.multiplyLeft(c);
        }
        if (q.isONE()) {
            return q.multiplyLeft(c);
        }
        logger.info("r.ring = {}", r.ring.toScript());
        System.out.println("recursiveUnivGcd: r = " + r);
        while (!r.isZERO()) {
            x = FDUtil.<C> recursiveSparsePseudoRemainder(q, r);
            q = r;
            if (field) {
                r = PolyUtil.<C> monic(x);
            } else {
                r = leftRecursivePrimitivePart(x);
            }
            System.out.println("recursiveUnivGcd: r = " + r);
        }
        if (debug) {
            logger.info("gcd(pp) = {}, ring = {}", q, P.ring.toScript());
        }
        q = leftRecursivePrimitivePart(q);
        q = (GenSolvablePolynomial<GenPolynomial<C>>) q.multiplyLeft(c).abs();
        return q;
    }


    /**
     * Univariate GenSolvablePolynomial right recursive greatest common divisor.
     * Uses pseudoRemainder for remainder.
     * @param P univariate recursive GenSolvablePolynomial.
     * @param S univariate recursive GenSolvablePolynomial.
     * @return gcd(P,S) with P = p*gcd(P,S)*P' and S = s*gcd(P,S)*S', where
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
        boolean field = P.leadingBaseCoefficient().ring.coFac.isField();
        //boolean field = P.ring.coFac.isField();
        long e = P.degree(0);
        long f = S.degree(0);
        GenSolvablePolynomial<GenPolynomial<C>> q, r, x, qs, rs;
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
            if (P.leadingBaseCoefficient().degree() > S.leadingBaseCoefficient().degree()) {
                q = P;
                r = S;
            } else {
                r = P;
                q = S;
            }
        }
        if (debug) {
            logger.debug("RI-degrees: e = {}, f = {}", e, f);
        }
        if (field) {
            r = PolyUtil.<C> monic(r);
            q = PolyUtil.<C> monic(q);
        } else {
            r = (GenSolvablePolynomial<GenPolynomial<C>>) r.abs();
            q = (GenSolvablePolynomial<GenPolynomial<C>>) q.abs();
        }
        GenSolvablePolynomial<C> a = leftRecursiveContent(r);
        rs = FDUtil.<C> recursiveRightDivide(r, a);
        if (debug) {
            logger.info("RI-recCont a = {}, r = {}", a, r);
            logger.info("RI-recCont r/a = {}, r%a = {}", r, r.subtract(rs.multiplyLeft(a)));
            if (!r.equals(rs.multiplyLeft(a))) {
                System.out.println("RI-recGcd: r         = " + r);
                System.out.println("RI-recGcd: cont(r)   = " + a);
                System.out.println("RI-recGcd: pp(r)     = " + rs);
                System.out.println("RI-recGcd: pp(r)c(r) = " + rs.multiply(a));
                System.out.println("RI-recGcd: c(r)pp(r) = " + rs.multiplyLeft(a));
                throw new RuntimeException("RI-recGcd: pp: not divisible");
            }
        }
        r = rs;
        GenSolvablePolynomial<C> b = leftRecursiveContent(q);
        qs = FDUtil.<C> recursiveRightDivide(q, b);
        if (debug) {
            logger.info("RI-recCont b = {}, q = {}", b, q);
            logger.info("RI-recCont q/b = {}, q%b = {}", qs, q.subtract(qs.multiplyLeft(b)));
            if (!q.equals(qs.multiplyLeft(b))) {
                System.out.println("RI-recGcd: q         = " + q);
                System.out.println("RI-recGcd: cont(q)   = " + b);
                System.out.println("RI-recGcd: pp(q)     = " + qs);
                System.out.println("RI-recGcd: pp(q)c(q) = " + qs.multiply(b));
                System.out.println("RI-recGcd: c(q)pp(q) = " + qs.multiplyLeft(b));
                throw new RuntimeException("RI-recGcd: pp: not divisible");
            }
        }
        q = qs;
        //no: GenSolvablePolynomial<C> c = rightGcd(a, b); // go to recursion
        GenSolvablePolynomial<C> c = leftGcd(a, b); // go to recursion
        logger.info("RI-Gcd(contents) c = {}, a = {}, b = {}", c, a, b);
        if (r.isONE()) {
            return r.multiplyLeft(c);
        }
        if (q.isONE()) {
            return q.multiplyLeft(c);
        }
        if (debug) {
            logger.info("RI-r.ring = {}", r.ring.toScript());
        }
        while (!r.isZERO()) {
            x = FDUtil.<C> recursiveRightSparsePseudoRemainder(q, r);
            q = r;
            if (field) {
                r = PolyUtil.<C> monic(x);
            } else {
                r = leftRecursivePrimitivePart(x);
            }
        }
        logger.info("RI-recGcd(P,S) pre pp okay: q = {}", q);
        //q = rightRecursivePrimitivePart(q);
        q = leftRecursivePrimitivePart(q); // sic
        System.out.println("RI-recGcd: pp(q)     = " + q);
        if (debug) {
            logger.info("RI-gcd(pp) = {}, ring = {}", q, P.ring.toScript());
        }
        q = (GenSolvablePolynomial<GenPolynomial<C>>) q.multiplyLeft(c).abs();
        return q;
    }

}
