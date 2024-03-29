/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * Squarefree decomposition for coefficient rings of characteristic 0.
 * @author Heinz Kredel
 */

public class SquarefreeRingChar0<C extends GcdRingElem<C>>
                extends SquarefreeAbstract<C> /*implements Squarefree<C>*/ {


    private static final Logger logger = LogManager.getLogger(SquarefreeRingChar0.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Factory for ring of characteristic 0 coefficients.
     */
    protected final RingFactory<C> coFac;


    /**
     * Constructor.
     */
    public SquarefreeRingChar0(RingFactory<C> fac) {
        super(GCDFactory.<C> getProxy(fac));
        if (fac.isField()) {
            throw new IllegalArgumentException("fac is a field: use SquarefreeFieldChar0");
        }
        if (fac.characteristic().signum() != 0) {
            throw new IllegalArgumentException("characteristic(fac) must be zero");
        }
        coFac = fac;
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getName() + " with " + engine + " over " + coFac;
    }


    /**
     * GenPolynomial polynomial greatest squarefree divisor.
     * @param P GenPolynomial.
     * @return squarefree(pp(P)).
     */
    @Override
    public GenPolynomial<C> baseSquarefreePart(GenPolynomial<C> P) {
        if (P == null || P.isZERO()) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar > 1) {
            throw new IllegalArgumentException(
                            this.getClass().getName() + " only for univariate polynomials");
        }
        GenPolynomial<C> pp = engine.basePrimitivePart(P);
        if (pp.isConstant()) {
            return pp;
        }
        GenPolynomial<C> d = PolyUtil.<C> baseDerivative(pp);
        d = engine.basePrimitivePart(d);
        GenPolynomial<C> g = engine.baseGcd(pp, d);
        g = engine.basePrimitivePart(g);
        GenPolynomial<C> q = PolyUtil.<C> basePseudoDivide(pp, g);
        q = engine.basePrimitivePart(q);
        return q;
    }


    /**
     * GenPolynomial polynomial squarefree factorization.
     * @param A GenPolynomial.
     * @return [p_1 -&gt; e_1, ..., p_k -&gt; e_k] with A = prod_{i=1,...,k}
     *         p_i^{e_i} and p_i squarefree and gcd(p_i, p_j) = 1, for i != j.
     */
    @Override
    public SortedMap<GenPolynomial<C>, Long> baseSquarefreeFactors(GenPolynomial<C> A) {
        SortedMap<GenPolynomial<C>, Long> sfactors = new TreeMap<GenPolynomial<C>, Long>();
        if (A == null || A.isZERO()) {
            return sfactors;
        }
        if (A.isConstant()) {
            sfactors.put(A, 1L);
            return sfactors;
        }
        GenPolynomialRing<C> pfac = A.ring;
        if (pfac.nvar > 1) {
            throw new IllegalArgumentException(
                            this.getClass().getName() + " only for univariate polynomials");
        }
        C ldbcf = A.leadingBaseCoefficient();
        if (!ldbcf.isONE()) {
            C cc = engine.baseContent(A);
            A = A.divide(cc);
            GenPolynomial<C> f1 = pfac.getONE().multiply(cc);
            //System.out.println("gcda sqf f1 = " + f1);
            sfactors.put(f1, 1L);
        }
        // divide by trailing term
        ExpVector et = A.trailingExpVector();
        if (!et.isZERO()) {
            GenPolynomial<C> tr = pfac.valueOf(et);
            logger.info("trailing term = {}", tr);
            A = PolyUtil.<C> basePseudoDivide(A, tr);
            long ep = et.getVal(0); // univariate
            et = et.subst(0, 1);
            tr = pfac.valueOf(et);
            logger.info("tr, ep = {}, {}", tr, ep);
            sfactors.put(tr, ep);
            if (A.length() == 1) {
                return sfactors;
            }
        }
        GenPolynomial<C> T0 = A;
        GenPolynomial<C> Tp;
        GenPolynomial<C> T = null;
        GenPolynomial<C> V = null;
        long k = 0L;
        boolean init = true;
        while (true) {
            if (init) {
                if (T0.isConstant() || T0.isZERO()) {
                    break;
                }
                Tp = PolyUtil.<C> baseDerivative(T0);
                T = engine.baseGcd(T0, Tp);
                T = engine.basePrimitivePart(T);
                V = PolyUtil.<C> basePseudoDivide(T0, T);
                //System.out.println("iT0 = " + T0);
                //System.out.println("iTp = " + Tp);
                //System.out.println("iT  = " + T);
                //System.out.println("iV  = " + V);
                k = 0L;
                init = false;
            }
            if (V.isConstant()) {
                break;
            }
            k++;
            GenPolynomial<C> W = engine.baseGcd(T, V);
            W = engine.basePrimitivePart(W);
            GenPolynomial<C> z = PolyUtil.<C> basePseudoDivide(V, W);
            //System.out.println("W = " + W);
            //System.out.println("z = " + z);
            V = W;
            T = PolyUtil.<C> basePseudoDivide(T, V);
            //System.out.println("V = " + V);
            //System.out.println("T = " + T);
            if (z.degree(0) > 0) {
                if (ldbcf.isONE() && !z.leadingBaseCoefficient().isONE()) {
                    z = engine.basePrimitivePart(z);
                    //logger.info("z,pp = {}", z);
                }
                logger.info("z, k = {}, {}", z, k);
                sfactors.put(z, k);
            }
        }
        return normalizeFactorization(sfactors);
    }


    /**
     * GenPolynomial recursive univariate polynomial greatest squarefree
     * divisor.
     * @param P recursive univariate GenPolynomial.
     * @return squarefree(P).
     */
    @Override
    public GenPolynomial<GenPolynomial<C>> recursiveUnivariateSquarefreePart(
                    GenPolynomial<GenPolynomial<C>> P) {
        if (P == null || P.isZERO()) {
            return P;
        }
        GenPolynomialRing<GenPolynomial<C>> pfac = P.ring;
        if (pfac.nvar > 1) {
            throw new IllegalArgumentException(
                            this.getClass().getName() + " only for multivariate polynomials");
        }
        // remove content
        GenPolynomial<GenPolynomial<C>> pp = P;
        GenPolynomial<C> Pc = engine.recursiveContent(P);
        Pc = engine.basePrimitivePart(Pc);
        if (!Pc.isONE()) {
            pp = PolyUtil.<C> coefficientPseudoDivide(pp, Pc);
            //System.out.println("pp/cont = " + pp);
        }
        // squarefree content
        GenPolynomial<C> Ps = squarefreePart(Pc);
        Ps = engine.basePrimitivePart(Ps);
        //System.out.println("Pc = " + Pc + ", Ps = " + Ps);
        if (pp.leadingExpVector().getVal(0) < 1) {
            //System.out.println("pp = " + pp);
            //System.out.println("Ps = " + Ps);
            pp = pp.multiply(Ps);
            pp = engine.baseRecursivePrimitivePart(pp);
            return pp;
        }
        GenPolynomial<GenPolynomial<C>> d = PolyUtil.<C> recursiveDerivative(pp);
        //System.out.println("d = " + d);
        GenPolynomial<GenPolynomial<C>> g = engine.recursiveUnivariateGcd(pp, d);
        //System.out.println("g,rec = " + g);
        g = engine.baseRecursivePrimitivePart(g);
        //System.out.println("g,bPP = " + g);
        GenPolynomial<GenPolynomial<C>> q = PolyUtil.<C> recursivePseudoDivide(pp, g);
        //System.out.println("q,bPP = " + q);
        q = q.multiply(Ps);
        //q = engine.basePrimitivePart(q);
        q = engine.baseRecursivePrimitivePart(q);
        return q;
    }


    /**
     * GenPolynomial recursive univariate polynomial squarefree factorization.
     * @param P recursive univariate GenPolynomial.
     * @return [p_1 -&gt; e_1, ..., p_k -&gt; e_k] with P = prod_{i=1,...,k}
     *         p_i^{e_i} and p_i squarefree and gcd(p_i, p_j) = 1, for i != j.
     */
    @Override
    public SortedMap<GenPolynomial<GenPolynomial<C>>, Long> recursiveUnivariateSquarefreeFactors(
                    GenPolynomial<GenPolynomial<C>> P) {
        SortedMap<GenPolynomial<GenPolynomial<C>>, Long> sfactors = new TreeMap<GenPolynomial<GenPolynomial<C>>, Long>();
        if (P == null || P.isZERO()) {
            return sfactors;
        }
        GenPolynomialRing<GenPolynomial<C>> pfac = P.ring;
        if (pfac.nvar > 1) {
            // recursiveContent not possible by return type
            throw new IllegalArgumentException(
                            this.getClass().getName() + " only for univariate polynomials");
        }
        // if base coefficient ring is a field, make monic
        GenPolynomialRing<C> cfac = (GenPolynomialRing<C>) pfac.coFac;
        C bcc = engine.baseRecursiveContent(P);
        if (!bcc.isONE()) {
            GenPolynomial<C> lc = cfac.getONE().multiply(bcc);
            GenPolynomial<GenPolynomial<C>> pl = pfac.getONE().multiply(lc);
            sfactors.put(pl, 1L);
            P = PolyUtil.<C> baseRecursiveDivide(P, bcc);
        }
        // factors of content
        GenPolynomial<C> Pc = engine.recursiveContent(P);
        logger.info("Pc = {}", Pc);
        Pc = engine.basePrimitivePart(Pc);
        //System.out.println("Pc,PP = " + Pc);
        if (!Pc.isONE()) {
            P = PolyUtil.<C> coefficientPseudoDivide(P, Pc);
        }
        SortedMap<GenPolynomial<C>, Long> rsf = squarefreeFactors(Pc);
        logger.info("rsf = {}", rsf);
        // add factors of content
        for (Map.Entry<GenPolynomial<C>, Long> me : rsf.entrySet()) {
            GenPolynomial<C> c = me.getKey();
            if (!c.isONE()) {
                GenPolynomial<GenPolynomial<C>> cr = pfac.getONE().multiply(c);
                Long rk = me.getValue(); //rsf.get(c);
                sfactors.put(cr, rk);
            }
        }
        // divide by trailing term
        ExpVector et = P.trailingExpVector();
        if (!et.isZERO()) {
            GenPolynomial<GenPolynomial<C>> tr = pfac.valueOf(et);
            logger.info("trailing term = {}", tr);
            P = PolyUtil.<C> recursivePseudoDivide(P, tr);
            long ep = et.getVal(0); // univariate
            et = et.subst(0, 1);
            tr = pfac.valueOf(et);
            sfactors.put(tr, ep);
        }

        // factors of recursive polynomial
        GenPolynomial<GenPolynomial<C>> T0 = P;
        GenPolynomial<GenPolynomial<C>> Tp;
        GenPolynomial<GenPolynomial<C>> T = null;
        GenPolynomial<GenPolynomial<C>> V = null;
        long k = 0L;
        boolean init = true;
        while (true) {
            if (init) {
                if (T0.isConstant() || T0.isZERO()) {
                    break;
                }
                Tp = PolyUtil.<C> recursiveDerivative(T0);
                //System.out.println("iTp = " + Tp);
                T = engine.recursiveUnivariateGcd(T0, Tp);
                //System.out.println("iT = " + T);
                if (debug) {
                    logger.info("T0 = {}, Tp = {}, T = {}", T0, Tp, T);
                }
                T = engine.baseRecursivePrimitivePart(T);
                //System.out.println("iT = " + T);
                V = PolyUtil.<C> recursivePseudoDivide(T0, T);
                //System.out.println("iT0 = " + T0);
                //System.out.println("iV = " + V);
                k = 0L;
                init = false;
            }
            if (V.isConstant()) {
                break;
            }
            k++;
            GenPolynomial<GenPolynomial<C>> W = engine.recursiveUnivariateGcd(T, V);
            if (debug) {
                logger.info("T = {}, V = {}, W = {}", T, V, W);
            }
            W = engine.baseRecursivePrimitivePart(W);
            GenPolynomial<GenPolynomial<C>> z = PolyUtil.<C> recursivePseudoDivide(V, W);
            //System.out.println("W = " + W);
            //System.out.println("z = " + z);
            V = W;
            T = PolyUtil.<C> recursivePseudoDivide(T, V);
            //System.out.println("V = " + V);
            //System.out.println("T = " + T);
            //was: if ( z.degree(0) > 0 ) {
            if (!z.isONE() && !z.isZERO()) {
                z = engine.baseRecursivePrimitivePart(z);
                logger.info("z = {}, k = {}", z, k);
                sfactors.put(z, k);
            }
        }
        return sfactors;
    }


    /**
     * GenPolynomial greatest squarefree divisor.
     * @param P GenPolynomial.
     * @return squarefree(P).
     */
    @Override
    public GenPolynomial<C> squarefreePart(GenPolynomial<C> P) {
        if (P == null) {
            throw new IllegalArgumentException(this.getClass().getName() + " P != null");
        }
        if (P.isZERO()) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar <= 1) {
            return baseSquarefreePart(P);
        }
        GenPolynomialRing<GenPolynomial<C>> rfac = pfac.recursive(1);
        GenPolynomial<GenPolynomial<C>> Pr = PolyUtil.<C> recursive(rfac, P);
        GenPolynomial<C> Pc = engine.recursiveContent(Pr);
        Pr = PolyUtil.<C> coefficientPseudoDivide(Pr, Pc);
        GenPolynomial<C> Ps = squarefreePart(Pc);
        GenPolynomial<GenPolynomial<C>> PP = recursiveUnivariateSquarefreePart(Pr);
        GenPolynomial<GenPolynomial<C>> PS = PP.multiply(Ps);
        GenPolynomial<C> D = PolyUtil.<C> distribute(pfac, PS);
        return D;
    }


    /**
     * GenPolynomial squarefree factorization.
     * @param P GenPolynomial.
     * @return [p_1 -&gt; e_1, ..., p_k -&gt; e_k] with P = prod_{i=1,...,k}
     *         p_i^{e_i} and p_i squarefree and gcd(p_i, p_j) = 1, for i != j.
     */
    @Override
    public SortedMap<GenPolynomial<C>, Long> squarefreeFactors(GenPolynomial<C> P) {
        if (P == null) {
            throw new IllegalArgumentException(this.getClass().getName() + " P != null");
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar <= 1) {
            return baseSquarefreeFactors(P);
        }
        SortedMap<GenPolynomial<C>, Long> sfactors = new TreeMap<GenPolynomial<C>, Long>();
        if (P.isZERO()) {
            return sfactors;
        }
        if (P.isONE()) {
            sfactors.put(P, 1L);
            return sfactors;
        }
        GenPolynomialRing<GenPolynomial<C>> rfac = pfac.recursive(1);

        GenPolynomial<GenPolynomial<C>> Pr = PolyUtil.<C> recursive(rfac, P);
        SortedMap<GenPolynomial<GenPolynomial<C>>, Long> PP = recursiveUnivariateSquarefreeFactors(Pr);

        for (Map.Entry<GenPolynomial<GenPolynomial<C>>, Long> m : PP.entrySet()) {
            Long i = m.getValue();
            GenPolynomial<GenPolynomial<C>> Dr = m.getKey();
            GenPolynomial<C> D = PolyUtil.<C> distribute(pfac, Dr);
            sfactors.put(D, i);
        }
        return normalizeFactorization(sfactors);
    }


    /**
     * Coefficients squarefree factorization.
     * @param P coefficient.
     * @return [p_1 -&gt; e_1, ..., p_k -&gt; e_k] with P = prod_{i=1,...,k}
     *         p_i^{e_i} and p_i squarefree and gcd(p_i, p_j) = 1, for i != j.
     */
    @Override
    public SortedMap<C, Long> squarefreeFactors(C P) {
        throw new UnsupportedOperationException("method not implemented");
    }

}
