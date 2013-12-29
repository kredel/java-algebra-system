/*
 * $Id$
 */

package edu.jas.fd;


import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.gbmod.SolvableQuotient;
import edu.jas.gbmod.SolvableQuotientRing;
import edu.jas.gbmod.QuotSolvablePolynomial;
import edu.jas.gbmod.QuotSolvablePolynomialRing;


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
        GenSolvablePolynomial<GenPolynomial<C>> D = (GenSolvablePolynomial<GenPolynomial<C>>) Pa.subtract(Rb);
        if (D.isZERO()) {
            return true;
        }
        System.out.println("FDQR: Pa = " + Pa);
        System.out.println("FDQR: Rb = " + Rb);
        System.out.println("FDQR: Pa-Rb = " + D);
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
        return recursivePseudoQuotientRemainder(P,S)[0]; 
    }


    /**
     * GenSolvablePolynomial recursive pseudo quotient and remainder for recursive polynomials.
     * @param <C> coefficient type.
     * @param P recursive GenSolvablePolynomial.
     * @param S nonzero recursive GenSolvablePolynomial.
     * @return [ quotient, remainder ] with ore(ldcf(S)<sup>m'</sup>) P = quotient * S + remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<GenPolynomial<C>>[] recursivePseudoQuotientRemainder(
                    GenSolvablePolynomial<GenPolynomial<C>> P, GenSolvablePolynomial<GenPolynomial<C>> S) {
        if (S == null || S.isZERO()) {
            throw new ArithmeticException(P + " division by zero " + S);
        }
        //if (S.ring.nvar != 1) {
        // ok if exact division
        // throw new RuntimeException("univariate polynomials only");
        //}
        GenSolvablePolynomial<GenPolynomial<C>>[] ret = new GenSolvablePolynomial[2];
        if (P == null || P.isZERO()) {
            ret[0] = S.ring.getZERO();
            ret[1] = S.ring.getZERO();
            return ret;
        }
        if (S.isONE()) {
            ret[0] = P;
            ret[1] = S.ring.getZERO();
            return ret;
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
        ret[0] = q;
        ret[1] = r; 
        return ret;
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


    /**
     * Integral solvable polynomial from solvable rational function coefficients. Represent as
     * polynomial with integral solvable polynomial coefficients by multiplication with
     * the lcm(??) of the numerators of the rational function coefficients.
     * @param fac result polynomial factory.
     * @param A polynomial with solvable rational function coefficients to be converted.
     * @return polynomial with integral solvable polynomial coefficients.
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<GenPolynomial<C>> integralFromQuotientCoefficients(
                    GenSolvablePolynomialRing<GenPolynomial<C>> fac, GenSolvablePolynomial<SolvableQuotient<C>> A) {
        GenSolvablePolynomial<GenPolynomial<C>> B = fac.getZERO().copy();
        if (A == null || A.isZERO()) {
            return B;
        }
        GenSolvablePolynomial<C> c = null;
        GenSolvablePolynomial<C> d;
        GenSolvablePolynomial<C> x;
        GenSolvablePolynomial<C> z;
        GreatestCommonDivisorAbstract<C> fd = new GreatestCommonDivisorPrimitive<C>();
        int s = 0;
        // lcm/ore of denominators ??
        for (SolvableQuotient<C> y : A.getMap().values()) {
            x = y.den;
            // c = lcm(c,x)
            if (c == null) {
                c = x;
                s = x.signum();
            } else {
                d = fd.gcd(c, x);
                z = (GenSolvablePolynomial<C>) x.divide(d); // ??
                c = z.multiply(c); // ?? multiplyLeft
            }
        }
        if (s < 0) {
            c = (GenSolvablePolynomial<C>) c.negate();
        }
        for (Map.Entry<ExpVector, SolvableQuotient<C>> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            SolvableQuotient<C> a = y.getValue();
            // p = n*(c/d)
            GenPolynomial<C> b = c.divide(a.den);
            GenPolynomial<C> p = a.num.multiply(b);
            //B = B.sum( p, e ); // inefficient
            B.doPutToMap(e, p);
        }
        return B;
    }


    /**
     * Integral solvable polynomial from solvable rational function coefficients. Represent as
     * polynomial with integral solvable polynomial coefficients by multiplication with
     * the lcm(??) of the numerators of the solvable rational function coefficients.
     * @param fac result polynomial factory.
     * @param L list of polynomials with solvable rational function coefficients to be
     *            converted.
     * @return list of polynomials with integral solvable polynomial coefficients.
     */
    public static <C extends GcdRingElem<C>> List<GenSolvablePolynomial<GenPolynomial<C>>> 
           integralFromQuotientCoefficients(
                   GenSolvablePolynomialRing<GenPolynomial<C>> fac, 
                   Collection<GenSolvablePolynomial<SolvableQuotient<C>>> L) {
        if (L == null) {
            return null;
        }
        List<GenSolvablePolynomial<GenPolynomial<C>>> list = new ArrayList<GenSolvablePolynomial<GenPolynomial<C>>>(L.size());
        for (GenSolvablePolynomial<SolvableQuotient<C>> p : L) {
            list.add(integralFromQuotientCoefficients(fac, p));
        }
        return list;
    }


    /**
     * Solvable rational function from integral solvable polynomial coefficients. Represent as
     * polynomial with type SolvableQuotient<C> coefficients.
     * @param fac result polynomial factory.
     * @param A polynomial with integral solvable polynomial coefficients to be
     *            converted.
     * @return polynomial with type SolvableQuotient<C> coefficients.
     */
    public static <C extends GcdRingElem<C>> GenSolvablePolynomial<SolvableQuotient<C>> quotientFromIntegralCoefficients(
                    GenSolvablePolynomialRing<SolvableQuotient<C>> fac, GenSolvablePolynomial<GenPolynomial<C>> A) {
        GenSolvablePolynomial<SolvableQuotient<C>> B = fac.getZERO().copy();
        if (A == null || A.isZERO()) {
            return B;
        }
        RingFactory<SolvableQuotient<C>> cfac = fac.coFac;
        SolvableQuotientRing<C> qfac = (SolvableQuotientRing<C>) cfac;
        for (Map.Entry<ExpVector, GenPolynomial<C>> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            GenSolvablePolynomial<C> a = (GenSolvablePolynomial<C>) y.getValue();
            SolvableQuotient<C> p = new SolvableQuotient<C>(qfac, a); // can not be zero
            if (!p.isZERO()) {
                //B = B.sum( p, e ); // inefficient
                B.doPutToMap(e, p);
            }
        }
        return B;
    }


    /**
     * Solvable rational function from integral solvable polynomial coefficients. Represent as
     * polynomial with type SolvableQuotient<C> coefficients.
     * @param fac result polynomial factory.
     * @param L list of polynomials with integral solvable polynomial coefficients to be
     *            converted.
     * @return list of polynomials with type SolvableQuotient<C> coefficients.
     */
    public static <C extends GcdRingElem<C>> List<GenSolvablePolynomial<SolvableQuotient<C>>> 
           quotientFromIntegralCoefficients(
                   GenSolvablePolynomialRing<SolvableQuotient<C>> fac, 
                   Collection<GenSolvablePolynomial<GenPolynomial<C>>> L) {
        if (L == null) {
            return null;
        }
        List<GenSolvablePolynomial<SolvableQuotient<C>>> list = new ArrayList<GenSolvablePolynomial<SolvableQuotient<C>>>(L.size());
        for (GenSolvablePolynomial<GenPolynomial<C>> p : L) {
            list.add(quotientFromIntegralCoefficients(fac, p));
        }
        return list;
    }

}
