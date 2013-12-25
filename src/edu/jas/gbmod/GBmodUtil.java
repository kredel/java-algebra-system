/*
 * $Id$
 */

package edu.jas.gbmod;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.Element;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.util.ListUtil;


/**
 * GB modular utilities, for example recursive pseudo remainder.
 * @author Heinz Kredel
 */

public class GBmodUtil {


    private static final Logger logger = Logger.getLogger(GBmodUtil.class);


    private static boolean debug = logger.isDebugEnabled();


    /**
     * GenPolynomial sparse pseudo remainder. For univariate polynomials.
     * @param <C> coefficient type.
     * @param P GenPolynomial.
     * @param S nonzero GenPolynomial.
     * @return remainder with ldcf(S)<sup>m'</sup> P = quotient * S + remainder.
     *         m' &le; deg(P)-deg(S)
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public static <C extends RingElem<C>> GenPolynomial<C> baseSparsePseudoRemainder(GenPolynomial<C> P,
                    GenPolynomial<C> S) {
        if (S == null || S.isZERO()) {
            throw new ArithmeticException(P.toString() + " division by zero " + S);
        }
        if (P.isZERO()) {
            return P;
        }
        if (S.isConstant()) {
            return P.ring.getZERO();
        }
        C c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenPolynomial<C> h;
        GenPolynomial<C> r = P;
        while (!r.isZERO()) {
            ExpVector f = r.leadingExpVector();
            if (f.multipleOf(e)) {
                C a = r.leadingBaseCoefficient();
                f = f.subtract(e);
                C x = a.remainder(c);
                if (x.isZERO()) {
                    C y = a.divide(c);
                    h = S.multiply(y, f); // coeff a
                } else {
                    r = r.multiply(c); // coeff ac
                    h = S.multiply(a, f); // coeff ac
                }
                r = r.subtract(h);
            } else {
                break;
            }
        }
        return r;
    }


    /**
     * GenPolynomial sparse pseudo divide. For univariate polynomials or exact
     * division.
     * @param <C> coefficient type.
     * @param P GenPolynomial.
     * @param S nonzero GenPolynomial.
     * @return quotient with ldcf(S)<sup>m'</sup> P = quotient * S + remainder.
     *         m' &le; deg(P)-deg(S)
     * @see edu.jas.poly.GenPolynomial#divide(edu.jas.poly.GenPolynomial).
     */
    public static <C extends RingElem<C>> GenPolynomial<C> basePseudoDivide(GenPolynomial<C> P,
                    GenPolynomial<C> S) {
        if (S == null || S.isZERO()) {
            throw new ArithmeticException(P.toString() + " division by zero " + S);
        }
        //if (S.ring.nvar != 1) {
            // ok if exact division
            // throw new RuntimeException(this.getClass().getName()
            //                            + " univariate polynomials only");
        //}
        if (P.isZERO() || S.isONE()) {
            return P;
        }
        C c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenPolynomial<C> h;
        GenPolynomial<C> r = P;
        GenPolynomial<C> q = S.ring.getZERO().copy();

        while (!r.isZERO()) {
            ExpVector f = r.leadingExpVector();
            if (f.multipleOf(e)) {
                C a = r.leadingBaseCoefficient();
                f = f.subtract(e);
                C x = a.remainder(c);
                if (x.isZERO()) {
                    C y = a.divide(c);
                    q = q.sum(y, f);
                    h = S.multiply(y, f); // coeff a
                } else {
                    q = q.multiply(c);
                    q = q.sum(a, f);
                    r = r.multiply(c); // coeff ac
                    h = S.multiply(a, f); // coeff ac
                }
                r = r.subtract(h);
            } else {
                break;
            }
        }
        return q;
    }


    /**
     * GenSolvablePolynomial sparse pseudo remainder. For recursive solvable polynomials.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param S nonzero recursive GenSolvablePolynomial.
     * @return remainder with ore(ldcf(S)<sup>m'</sup>) P = quotient * S + remainder.
     * @see edu.jas.poly.GenSolvablePolynomial#remainder(edu.jas.poly.GenSolvablePolynomial).
     */
    public static <C extends RingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> 
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
        SolvableSyzygyAbstract<C> syz = new SolvableSyzygyAbstract<C>();

        //GenPolynomial<C> c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenSolvablePolynomial<GenPolynomial<C>> h;
        GenSolvablePolynomial<GenPolynomial<C>> r = P;
        while (!r.isZERO()) {
            ExpVector f = r.leadingExpVector();
            if (f.multipleOf(e)) {
                GenSolvablePolynomial<C> a = (GenSolvablePolynomial<C>) r.leadingBaseCoefficient();
                f = f.subtract(e);

                h = S.multiplyLeft(f);  // coeff c, exp (f-e) e
                GenSolvablePolynomial<C> d = (GenSolvablePolynomial<C>) h.leadingBaseCoefficient();
                GenSolvablePolynomial<C>[] oc = syz.leftOreCond(a,d);
                GenPolynomial<C> ga = oc[0];
                GenPolynomial<C> gd = oc[1];
                //System.out.println("OreCond:  a = " +  a + ",  d = " +  d);
                //System.out.println("OreCond: ga = " + ga + ", gd = " + gd);
                // ga a = gd d
                r = r.multiplyLeft(ga);  // coeff ga a, exp f
                h = h.multiplyLeft(gd);  // coeff gd d, exp f

                if (!r.leadingBaseCoefficient().equals(h.leadingBaseCoefficient())) {
                    throw new RuntimeException("should not happen: lc(r) = " + r.leadingBaseCoefficient() + ", lc(h) = " + h.leadingBaseCoefficient());
                } else {
                    System.out.println("lc(r) = " + r.leadingBaseCoefficient());
                }
                r = (GenSolvablePolynomial<GenPolynomial<C>>) r.subtract(h);
            } else {
                break;
            }
        }
        return r;
    }


    /**
     * GenSolvablePolynomial sparse pseudo remainder. For recursive solvable polynomials.
     * <b>Note:</b> uses right multiplication of P by ldcf(S), not always applicable.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param S nonzero recursive GenSolvablePolynomial.
     * @return remainder with P ldcf(S)<sup>m'</sup> = quotient * S + remainder.
     * @see edu.jas.poly.GenSolvablePolynomial#remainder(edu.jas.poly.GenSolvablePolynomial).
     */
    public static <C extends RingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> 
           recursiveSparsePseudoRemainderRight(
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
        GenPolynomial<C> c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenSolvablePolynomial<GenPolynomial<C>> h;
        GenSolvablePolynomial<GenPolynomial<C>> r = P;
        while (!r.isZERO()) {
            ExpVector f = r.leadingExpVector();
            if (f.multipleOf(e)) {
                f = f.subtract(e);

                h = S.multiplyLeft(f);  // coeff c, exp (f-e) e
                GenPolynomial<C> d = h.leadingBaseCoefficient();
                GenPolynomial<C> a = r.leadingBaseCoefficient();

                r = r.multiply(d);      // coeff a d, exp f
                h = h.multiplyLeft(a);  // coeff a d, exp f

                if (!r.leadingBaseCoefficient().equals(h.leadingBaseCoefficient())) {
                    throw new RuntimeException("should not happen: lc(r) = " + r.leadingBaseCoefficient() + ", lc(h) = " + h.leadingBaseCoefficient());
                } else {
                    System.out.println("lc(r) = " + r.leadingBaseCoefficient());
                }
                r = (GenSolvablePolynomial<GenPolynomial<C>>) r.subtract(h);
            } else {
                break;
            }
        }
        return r;
    }


    /**
     * GenPolynomial recursive pseudo divide. For recursive polynomials.
     * @param <C> coefficient type.
     * @param P recursive GenPolynomial.
     * @param S nonzero recursive GenPolynomial.
     * @return quotient with ldcf(S)<sup>m'</sup> P = quotient * S + remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public static <C extends RingElem<C>> GenPolynomial<GenPolynomial<C>> recursivePseudoDivide(
                    GenPolynomial<GenPolynomial<C>> P, GenPolynomial<GenPolynomial<C>> S) {
        if (S == null || S.isZERO()) {
            throw new ArithmeticException(P + " division by zero " + S);
        }
        //if (S.ring.nvar != 1) {
            // ok if exact division
            // throw new RuntimeException(this.getClass().getName()
            //                            + " univariate polynomials only");
        //}
        if (P == null || P.isZERO()) {
            return P;
        }
        if (S.isONE()) {
            return P;
        }
        GenPolynomial<C> c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenPolynomial<GenPolynomial<C>> h;
        GenPolynomial<GenPolynomial<C>> r = P;
        GenPolynomial<GenPolynomial<C>> q = S.ring.getZERO().copy();
        while (!r.isZERO()) {
            ExpVector f = r.leadingExpVector();
            if (f.multipleOf(e)) {
                GenPolynomial<C> a = r.leadingBaseCoefficient();
                f = f.subtract(e);
                GenPolynomial<C> x = GBmodUtil.<C> baseSparsePseudoRemainder(a, c);
                if (x.isZERO() && !c.isConstant()) {
                    GenPolynomial<C> y = GBmodUtil.<C> basePseudoDivide(a, c);
                    q = q.sum(y, f);
                    h = S.multiply(y, f); // coeff a
                } else {
                    q = q.multiply(c);
                    q = q.sum(a, f);
                    r = r.multiply(c); // coeff ac
                    h = S.multiply(a, f); // coeff ac
                }
                r = r.subtract(h);
            } else {
                break;
            }
        }
        return q;
    }


    /**
     * GenPolynomial divide. For recursive polynomials. Division by coefficient
     * ring element.
     * @param <C> coefficient type.
     * @param P recursive GenPolynomial.
     * @param s GenPolynomial.
     * @return this/s.
     */
    public static <C extends RingElem<C>> GenPolynomial<GenPolynomial<C>> recursiveDivide(
                    GenPolynomial<GenPolynomial<C>> P, GenPolynomial<C> s) {
        if (s == null || s.isZERO()) {
            throw new ArithmeticException("division by zero " + P + ", " + s);
        }
        if (P.isZERO()) {
            return P;
        }
        if (s.isONE()) {
            return P;
        }
        GenPolynomial<GenPolynomial<C>> p = P.ring.getZERO().copy();
        //SortedMap<ExpVector, GenPolynomial<C>> pv = p.getMap();
        for (Map.Entry<ExpVector, GenPolynomial<C>> m1 : P.getMap().entrySet()) {
            GenPolynomial<C> c1 = m1.getValue();
            ExpVector e1 = m1.getKey();
            GenPolynomial<C> c = PolyUtil.<C> basePseudoDivide(c1, s);
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
