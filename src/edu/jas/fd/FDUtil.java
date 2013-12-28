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
 * Factorization domain utilities, for example recursive pseudo remainder.
 * @author Heinz Kredel
 */

public class FDUtil {


    //private static final Logger logger = Logger.getLogger(FDUtil.class);


    //private static boolean debug = logger.isDebugEnabled();


    /**
     * GenSolvablePolynomial sparse pseudo remainder for univariate
     * polynomials.
     * @param <C> coefficient type.
     * @param P GenSolvablePolynomial.
     * @param S nonzero GenSolvablePolynomial.
     * @return remainder with ldcf(S)<sup>m'</sup> P = quotient * S + remainder.
     *         m' &le; deg(P)-deg(S)
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<C> baseSparsePseudoRemainder(
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
        GreatestCommonDivisorAbstract<C> fd = new GreatestCommonDivisorSimple<C>();
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
                // need ga, gc: ga a = gc c
                C[] oc = fd.leftOreCond(a,c);
                C ga = oc[0];
                C gc = oc[1];
                r = r.multiplyLeft(ga); // coeff ga a, exp f
                h = h.multiplyLeft(gc); // coeff gc c, exp f

                r = (GenSolvablePolynomial<C>) r.subtract(h);
            } else {
                break;
            }
        }
        return r;
    }


    /**
     * GenSolvablePolynomial sparse pseudo quotient for univariate polynomials or
     * exact division.
     * @param <C> coefficient type.
     * @param P GenSolvablePolynomial.
     * @param S nonzero GenSolvablePolynomial.
     * @return quotient with ldcf(S)<sup>m'</sup> P = quotient * S + remainder.
     *         m' &le; deg(P)-deg(S)
     * @see edu.jas.poly.GenPolynomial#divide(edu.jas.poly.GenPolynomial).
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<C> basePseudoQuotient(
                    GenSolvablePolynomial<C> P, GenSolvablePolynomial<C> S) {
        return basePseudoQuotientRemainder(P,S)[0];
    }


    /**
     * GenSolvablePolynomial sparse pseudo quotient and remainder for
     * univariate polynomials or exact division.
     * @param <C> coefficient type.
     * @param P GenSolvablePolynomial.
     * @param S nonzero GenSolvablePolynomial.
     * @return [ quotient, remainder ] with ldcf(S)<sup>m'</sup> P = quotient *
     *         S + remainder. m' &le; deg(P)-deg(S)
     * @see edu.jas.poly.GenPolynomial#divide(edu.jas.poly.GenPolynomial).
     */
    @SuppressWarnings("unchecked")
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<C>[] basePseudoQuotientRemainder(
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
        GreatestCommonDivisorAbstract<C> fd = new GreatestCommonDivisorSimple<C>();
        ExpVector e = S.leadingExpVector();
        GenSolvablePolynomial<C> h;
        GenSolvablePolynomial<C> r = P;
        GenSolvablePolynomial<C> q = S.ring.getZERO().copy();

        while (!r.isZERO()) {
            ExpVector f = r.leadingExpVector();
            if (f.multipleOf(e)) {
                C a = r.leadingBaseCoefficient();

                f = f.subtract(e);
                h = S.multiplyLeft(f); // coeff a
                C c = h.leadingBaseCoefficient();

                C x = a.remainder(c);
                if (false&&x.isZERO()) {
                    C y = a.divide(c);
                    q = (GenSolvablePolynomial<C>) q.sum(y, f);
                    h = S.multiply(y, f); // coeff a
                } else {
                    // need ga, gc: ga a = gc c
                    C[] oc = fd.leftOreCond(a,c);
                    C ga = oc[0];
                    C gc = oc[1];
                    r = r.multiplyLeft(ga); // coeff ga a, exp f
                    h = h.multiplyLeft(gc); // coeff gc c, exp f
                    q = q.multiply(ga); // c
                    q = (GenSolvablePolynomial<C>) q.sum(gc, f); // a
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
     * GenSolvablePolynomial sparse pseudo remainder for recursive solvable
     * polynomials.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param S nonzero recursive GenSolvablePolynomial.
     * @return remainder with ore(ldcf(S)<sup>m'</sup>) P = quotient * S +
     *         remainder.
     * @see edu.jas.poly.GenSolvablePolynomial#remainder(edu.jas.poly.GenSolvablePolynomial)
     *      .
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> 
           recursiveSparsePseudoRemainder(
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
                    System.out.println("OreCond:  a = " +  a + ",  d = " +  d);
                    System.out.println("OreCond: ga = " + ga + ", gd = " + gd);
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
     * GenSolvablePolynomial right sparse pseudo remainder for recursive solvable
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
     * Is recursive GenSolvablePolynomial pseudo quotient and remainder. For recursive
     * polynomials.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param S nonzero recursive GenSolvablePolynomial.
     * @return true, if P ~= q * S + r, else false.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     *      <b>Note:</b> not always meaningful and working
     */
    public static <C extends GcdRingElem<C>> boolean isRecursivePseudoQuotientRemainder(
                    GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S,
                    GenSolvablePolynomial<GenPolynomial<C>> q, GenSolvablePolynomial<GenPolynomial<C>> r) {
        GenSolvablePolynomial<GenPolynomial<C>> rhs = (GenSolvablePolynomial<GenPolynomial<C>>) q.multiply(S).sum(r);
        GenSolvablePolynomial<GenPolynomial<C>> lhs = P;
        GenPolynomial<C> ldcf = S.leadingBaseCoefficient();
        long d = P.degree(0) - S.degree(0) + 1;
        d = (d > 0 ? d : -d + 2);
        for (long i = 0; i <= d; i++) {
            //System.out.println("lhs = " + lhs);
            //System.out.println("rhs = " + rhs);
            //System.out.println("lhs-rhs = " + lhs.subtract(rhs));
            if (lhs.equals(rhs)) {
                return true;
            }
            lhs = lhs.multiply(ldcf);
        }
        GenSolvablePolynomial<GenPolynomial<C>> Pp = P;
        rhs = q.multiply(S);
        //System.out.println("rhs,2 = " + rhs);
        for (long i = 0; i <= d; i++) {
            lhs = (GenSolvablePolynomial<GenPolynomial<C>>) Pp.subtract(r);
            //System.out.println("lhs-rhs = " + lhs.subtract(rhs));
            if (lhs.equals(rhs)) {
                //System.out.println("lhs,2 = " + lhs);
                return true;
            }
            Pp = Pp.multiply(ldcf);
        }
        GreatestCommonDivisorAbstract<C> fd = new GreatestCommonDivisorSimple<C>();
        GenSolvablePolynomial<C> a = (GenSolvablePolynomial<C>) P.leadingBaseCoefficient();
        rhs = (GenSolvablePolynomial<GenPolynomial<C>>) q.multiply(S).sum(r);
        GenSolvablePolynomial<C> b = (GenSolvablePolynomial<C>) rhs.leadingBaseCoefficient();
        GenSolvablePolynomial<C>[] oc = fd.leftOreCond(a, b);
        GenPolynomial<C> ga = oc[0];
        GenPolynomial<C> gb = oc[1];
        System.out.println("OreCond:  a = " +  a + ",  b = " +  b);
        System.out.println("OreCond: ga = " + ga + ", gb = " + gb);
        // ga a = gd d
        GenSolvablePolynomial<GenPolynomial<C>> Pa = P.multiplyLeft(ga);   // coeff ga a
        GenSolvablePolynomial<GenPolynomial<C>> Rb = rhs.multiplyLeft(gb); // coeff gb b
        System.out.println("Pa = " + Pa);
        System.out.println("Rb = " + Rb);
        System.out.println("Pa-Rb = " + Pa.subtract(Rb));
        if (Pa.equals(Rb)) {
            return true;
        }
        return false;
    }


    /**
     * GenSolvablePolynomial recursive pseudo quotient for recursive polynomials.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param S nonzero recursive GenSolvablePolynomial.
     * @return quotient with ore(ldcf(S)<sup>m'</sup>) P = quotient * S + remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> recursivePseudoQuotient(
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
                q = q.multiply(ga); // d
                q = (GenSolvablePolynomial<GenPolynomial<C>>) q.sum(gd, f); // a
                //
                r = (GenSolvablePolynomial<GenPolynomial<C>>) r.subtract(h);
            } else {
                break;
            }
        }
        return q;
    }


    /**
     * GenSolvablePolynomial recursive quotient for recursive polynomials and division by
     * coefficient ring element.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param s GenPolynomial.
     * @return this/s.
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> recursiveDivide(
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
