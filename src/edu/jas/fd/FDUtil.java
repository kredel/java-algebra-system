/*
 * $Id$
 */

package edu.jas.fd;


import java.util.Map;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingElem;


/**
 * GB modular utilities, for example recursive pseudo remainder.
 * @author Heinz Kredel
 */

public class FDUtil {


    //private static final Logger logger = Logger.getLogger(FDUtil.class);


    //private static boolean debug = logger.isDebugEnabled();


    /**
     * GenSolvablePolynomial sparse pseudo remainder. For univariate
     * polynomials.
     * @param <C> coefficient type.
     * @param P GenSolvablePolynomial.
     * @param S nonzero GenSolvablePolynomial.
     * @return remainder with ldcf(S)<sup>m'</sup> P = quotient * S + remainder.
     *         m' &le; deg(P)-deg(S)
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public static <C extends RingElem<C>> GenSolvablePolynomial<C> baseSparsePseudoRemainder(
                    GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        if (S == null || S.isZERO()) {
            throw new ArithmeticException(P.toString() + " division by zero " + S);
        }
        if (P.isZERO()) {
            return P;
        }
        if (S.isConstant()) {
            return P.ring.getZERO();
        }
        ExpVector e = S.leadingExpVector();
        GenSolvablePolynomial<C> h;
        GenSolvablePolynomial<C> r = P;
        while (!r.isZERO()) {
            ExpVector f = r.leadingExpVector();
            if (f.multipleOf(e)) {
                C a = r.leadingBaseCoefficient();

                f = f.subtract(e);
                h = S.multiplyLeft(f); // coeff a
                C c = h.leadingBaseCoefficient();

                // need ga, gd: ga a = gd d
                r = r.multiply(c); // coeff ga a, exp f
                h = h.multiplyLeft(a); // coeff gd d, exp f

                r = (GenSolvablePolynomial<C>) r.subtract(h);
            } else {
                break;
            }
        }
        return r;
    }


    /**
     * GenSolvablePolynomial sparse pseudo divide. For univariate polynomials or
     * exact division.
     * @param <C> coefficient type.
     * @param P GenSolvablePolynomial.
     * @param S nonzero GenSolvablePolynomial.
     * @return quotient with ldcf(S)<sup>m'</sup> P = quotient * S + remainder.
     *         m' &le; deg(P)-deg(S)
     * @see edu.jas.poly.GenPolynomial#divide(edu.jas.poly.GenPolynomial).
     */
    public static <C extends RingElem<C>> GenSolvablePolynomial<C> basePseudoQuotient(
                    GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        if (S == null || S.isZERO()) {
            throw new ArithmeticException(P.toString() + " division by zero " + S);
        }
        //if (S.ring.nvar != 1) { // ok if exact division
        // throw new RuntimeException("univariate polynomials only");
        //}
        if (P.isZERO() || S.isONE()) {
            return P;
        }
        C c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenSolvablePolynomial<C> h;
        GenSolvablePolynomial<C> r = P;
        GenSolvablePolynomial<C> q = S.ring.getZERO().copy();

        while (!r.isZERO()) {
            ExpVector f = r.leadingExpVector();
            if (f.multipleOf(e)) {
                C a = r.leadingBaseCoefficient();
                f = f.subtract(e);
                C x = a.remainder(c);
                if (x.isZERO()) {
                    C y = a.divide(c);
                    q = (GenSolvablePolynomial<C>) q.sum(y, f);
                    h = S.multiply(y, f); // coeff a
                } else {
                    q = q.multiply(c);
                    q = (GenSolvablePolynomial<C>) q.sum(a, f);
                    r = r.multiply(c); // coeff ac
                    h = S.multiply(a, f); // coeff ac
                }
                r = (GenSolvablePolynomial<C>) r.subtract(h);
            } else {
                break;
            }
        }
        return q;
    }


    /**
     * GenSolvablePolynomial sparse pseudo quotient and remainder. For
     * univariate polynomials or exact division.
     * @param <C> coefficient type.
     * @param P GenSolvablePolynomial.
     * @param S nonzero GenSolvablePolynomial.
     * @return [ quotient, remainder ] with ldcf(S)<sup>m'</sup> P = quotient *
     *         S + remainder. m' &le; deg(P)-deg(S)
     * @see edu.jas.poly.GenPolynomial#divide(edu.jas.poly.GenPolynomial).
     */
    @SuppressWarnings("unchecked")
    public static <C extends RingElem<C>> GenSolvablePolynomial<C>[] basePseudoQuotientRemainder(
                    GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        if (S == null || S.isZERO()) {
            throw new ArithmeticException(P.toString() + " division by zero " + S);
        }
        //if (S.ring.nvar != 1) { // ok if exact division
        // throw new RuntimeException("univariate polynomials only");
        //}
        GenSolvablePolynomial<C>[] ret = new GenSolvablePolynomial[2];
        ret[0] = null;
        ret[1] = null;
        if (P.isZERO() || S.isONE()) {
            ret[0] = P;
            ret[1] = S.ring.getZERO();
            return ret;
        }
        C c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenSolvablePolynomial<C> h;
        GenSolvablePolynomial<C> r = P;
        GenSolvablePolynomial<C> q = S.ring.getZERO().copy();

        while (!r.isZERO()) {
            ExpVector f = r.leadingExpVector();
            if (f.multipleOf(e)) {
                C a = r.leadingBaseCoefficient();
                f = f.subtract(e);
                C x = a.remainder(c);
                if (x.isZERO()) {
                    C y = a.divide(c);
                    q = (GenSolvablePolynomial<C>) q.sum(y, f);
                    h = S.multiply(y, f); // coeff a
                } else {
                    q = q.multiply(c);
                    q = (GenSolvablePolynomial<C>) q.sum(a, f);
                    r = r.multiply(c); // coeff ac
                    h = S.multiply(a, f); // coeff ac
                }
                r = (GenSolvablePolynomial<C>) r.subtract(h);
            } else {
                break;
            }
        }
        //GenPolynomial<C> rhs = q.multiply(S).sum(r);
        //GenPolynomial<C> lhs = P;
        ret[0] = q;
        ret[1] = r;
        return ret;
    }


    /**
     * GenSolvablePolynomial sparse pseudo remainder. For recursive solvable
     * polynomials.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param S nonzero recursive GenSolvablePolynomial.
     * @return remainder with ore(ldcf(S)<sup>m'</sup>) P = quotient * S +
     *         remainder.
     * @see edu.jas.poly.GenSolvablePolynomial#remainder(edu.jas.poly.GenSolvablePolynomial)
     *      .
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> recursiveSparsePseudoRemainder(
                    GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S) {
        if (S == null || S.isZERO()) {
            throw new ArithmeticException(P + " division by zero " + S);
        }
        if (P == null || P.isZERO()) {
            return P;
        }
        if (S.isConstant()) {
            return P.ring.getZERO();
        }
        //SolvableSyzygyAbstract<C> syz = new SolvableSyzygyAbstract<C>();
        GreatestCommonDivisorAbstract<C> fd = new GreatestCommonDivisorSimple<C>();

        //GenPolynomial<C> c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenSolvablePolynomial<GenPolynomial<C>> h;
        GenSolvablePolynomial<GenPolynomial<C>> r = P;
        while (!r.isZERO()) {
            ExpVector f = r.leadingExpVector();
            if (f.multipleOf(e)) {
                GenSolvablePolynomial<C> a = (GenSolvablePolynomial<C>) r.leadingBaseCoefficient();
                f = f.subtract(e);

                h = S.multiplyLeft(f); // coeff c, exp (f-e) e
                GenSolvablePolynomial<C> d = (GenSolvablePolynomial<C>) h.leadingBaseCoefficient();
                GenSolvablePolynomial<C>[] oc = fd.leftOreCond(a, d);
                GenPolynomial<C> ga = oc[0];
                GenPolynomial<C> gd = oc[1];
                //System.out.println("OreCond:  a = " +  a + ",  d = " +  d);
                //System.out.println("OreCond: ga = " + ga + ", gd = " + gd);
                // ga a = gd d
                r = r.multiplyLeft(ga); // coeff ga a, exp f
                h = h.multiplyLeft(gd); // coeff gd d, exp f

                if (!r.leadingBaseCoefficient().equals(h.leadingBaseCoefficient())) {
                    throw new RuntimeException("should not happen: lc(r) = " + r.leadingBaseCoefficient()
                                    + ", lc(h) = " + h.leadingBaseCoefficient());
                    //} else {
                    //System.out.println("lc(r) = " + r.leadingBaseCoefficient());
                }
                r = (GenSolvablePolynomial<GenPolynomial<C>>) r.subtract(h);
            } else {
                break;
            }
        }
        return r;
    }


    /**
     * GenSolvablePolynomial sparse pseudo remainder. For recursive solvable
     * polynomials. <b>Note:</b> uses right multiplication of P by ldcf(S), not
     * always applicable.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param S nonzero recursive GenSolvablePolynomial.
     * @return remainder with P ldcf(S)<sup>m'</sup> = quotient * S + remainder.
     * @see edu.jas.poly.GenSolvablePolynomial#remainder(edu.jas.poly.GenSolvablePolynomial)
     *      .
     */
    public static <C extends RingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> recursiveSparsePseudoRemainderRight(
                    GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S) {
        if (S == null || S.isZERO()) {
            throw new ArithmeticException(P + " division by zero " + S);
        }
        if (P == null || P.isZERO()) {
            return P;
        }
        if (S.isConstant()) {
            return P.ring.getZERO();
        }
        //GenPolynomial<C> c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenSolvablePolynomial<GenPolynomial<C>> h;
        GenSolvablePolynomial<GenPolynomial<C>> r = P;
        while (!r.isZERO()) {
            ExpVector f = r.leadingExpVector();
            if (f.multipleOf(e)) {
                f = f.subtract(e);

                h = S.multiplyLeft(f); // coeff c, exp (f-e) e
                GenPolynomial<C> d = h.leadingBaseCoefficient();
                GenPolynomial<C> a = r.leadingBaseCoefficient();

                r = r.multiply(d); // coeff a d, exp f
                h = h.multiplyLeft(a); // coeff a d, exp f

                if (!r.leadingBaseCoefficient().equals(h.leadingBaseCoefficient())) {
                    throw new RuntimeException("should not happen: lc(r) = " + r.leadingBaseCoefficient()
                                    + ", lc(h) = " + h.leadingBaseCoefficient());
                    //} else {
                    //System.out.println("lc(r) = " + r.leadingBaseCoefficient());
                }
                r = (GenSolvablePolynomial<GenPolynomial<C>>) r.subtract(h);
            } else {
                break;
            }
        }
        return r;
    }


    /**
     * GenSolvablePolynomial recursive pseudo divide. For recursive polynomials.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param S nonzero recursive GenSolvablePolynomial.
     * @return quotient with ldcf(S)<sup>m'</sup> P = quotient * S + remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> recursivePseudoDivide(
                    GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S) {
        if (S == null || S.isZERO()) {
            throw new ArithmeticException(P + " division by zero " + S);
        }
        //if (S.ring.nvar != 1) {
        // ok if exact division
        // throw new RuntimeException("univariate polynomials only");
        //}
        if (P == null || P.isZERO()) {
            return P;
        }
        if (S.isONE()) {
            return P;
        }
        //SolvableSyzygyAbstract<C> syz = new SolvableSyzygyAbstract<C>();
        GreatestCommonDivisorAbstract<C> fd = new GreatestCommonDivisorSimple<C>();

        //GenPolynomial<C> c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenSolvablePolynomial<GenPolynomial<C>> h;
        GenSolvablePolynomial<GenPolynomial<C>> r = P;
        GenSolvablePolynomial<GenPolynomial<C>> q = S.ring.getZERO().copy();
        while (!r.isZERO()) {
            ExpVector f = r.leadingExpVector();
            if (f.multipleOf(e)) {
                GenSolvablePolynomial<C> a = (GenSolvablePolynomial<C>) r.leadingBaseCoefficient();
                f = f.subtract(e);
                //
                h = S.multiplyLeft(f); // coeff c, exp (f-e) e
                GenSolvablePolynomial<C> d = (GenSolvablePolynomial<C>) h.leadingBaseCoefficient();
                GenSolvablePolynomial<C>[] oc = fd.leftOreCond(a, d);
                GenPolynomial<C> ga = oc[0];
                GenPolynomial<C> gd = oc[1];
                //System.out.println("OreCond:  a = " +  a + ",  d = " +  d);
                //System.out.println("OreCond: ga = " + ga + ", gd = " + gd);
                // ga a = gd d
                r = r.multiplyLeft(ga); // coeff ga a, exp f
                h = h.multiplyLeft(gd); // coeff gd d, exp f
                //
                q = q.multiply(gd);
                q = (GenSolvablePolynomial<GenPolynomial<C>>) q.sum(ga, f);
                //
                r = (GenSolvablePolynomial<GenPolynomial<C>>) r.subtract(h);
            } else {
                break;
            }
        }
        return q;
    }


    /**
     * GenSolvablePolynomial divide. For recursive polynomials. Division by
     * coefficient ring element.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param s GenPolynomial.
     * @return this/s.
     */
    public static <C extends RingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> recursiveDivide(
                    GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<C> s) {
        if (s == null || s.isZERO()) {
            throw new ArithmeticException("division by zero " + P + ", " + s);
        }
        if (P.isZERO()) {
            return P;
        }
        if (s.isONE()) {
            return P;
        }
        GenSolvablePolynomial<GenPolynomial<C>> p = P.ring.getZERO().copy();
        //SortedMap<ExpVector, GenPolynomial<C>> pv = p.val; //getMap();
        for (Map.Entry<ExpVector, GenPolynomial<C>> m1 : P.getMap().entrySet()) {
            GenSolvablePolynomial<C> c1 = (GenSolvablePolynomial<C>) m1.getValue();
            ExpVector e1 = m1.getKey();
            GenSolvablePolynomial<C> c = FDUtil.<C> basePseudoQuotient(c1, s);
            if (!c.isZERO()) {
                //pv.put(e1, c); 
                p.doPutToMap(e1, c);
            } else {
                System.out.println("rDiv, P  = " + P);
                System.out.println("rDiv, c1 = " + c1);
                System.out.println("rDiv, s  = " + s);
                System.out.println("rDiv, c  = " + c);
                throw new RuntimeException("something is wrong");
            }
        }
        return p;
    }

}
