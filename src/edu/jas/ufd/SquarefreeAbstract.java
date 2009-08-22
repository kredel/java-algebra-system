/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.Power;
import edu.jas.structure.RingFactory;


/**
 * Abstract squarefree decomposition class.
 * @author Heinz Kredel
 */

public abstract class SquarefreeAbstract<C extends GcdRingElem<C>> implements Squarefree<C> {


    /**
     * GenPolynomial polynomial greatest squarefree divisor.
     * @param P GenPolynomial.
     * @return squarefree(pp(P)).
     */
    public abstract GenPolynomial<C> baseSquarefreePart(GenPolynomial<C> P);


    /**
     * GenPolynomial polynomial squarefree factorization.
     * @param A GenPolynomial.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i^{e_i}
     *         and p_i squarefree.
     */
    public abstract SortedMap<GenPolynomial<C>, Long> baseSquarefreeFactors(GenPolynomial<C> A);


    /**
     * GenPolynomial recursive polynomial greatest squarefree divisor.
     * @param P recursive univariate GenPolynomial.
     * @return squarefree(pp(P)).
     */
    public abstract GenPolynomial<GenPolynomial<C>> recursiveUnivariateSquarefreePart(
            GenPolynomial<GenPolynomial<C>> P);


    /**
     * GenPolynomial recursive univariate polynomial squarefree factorization.
     * @param P recursive univariate GenPolynomial.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i^{e_i}
     *         and p_i squarefree.
     */
    public abstract SortedMap<GenPolynomial<GenPolynomial<C>>, Long> recursiveUnivariateSquarefreeFactors(
            GenPolynomial<GenPolynomial<C>> P);


    /**
     * GenPolynomial greatest squarefree divisor.
     * @param P GenPolynomial.
     * @return squarefree(pp(P)).
     */
    public abstract GenPolynomial<C> squarefreePart(GenPolynomial<C> P);


    /**
     * GenPolynomial test if is squarefree.
     * @param P GenPolynomial.
     * @return true if P is squarefree, else false.
     */
    public boolean isSquarefree(GenPolynomial<C> P) {
        GenPolynomial<C> S = squarefreePart(P);
        boolean f = P.equals(S);
        if (!f) {
            System.out.println("\nisSquarefree: " + f);
            System.out.println("S  = " + S);
            System.out.println("P  = " + P);
        }
        return f;
    }


    /**
     * Recursive GenPolynomial test if is squarefree.
     * @param P recursive univariate GenPolynomial.
     * @return true if P is squarefree, else false.
     */
    public boolean isRecursiveSquarefree(GenPolynomial<GenPolynomial<C>> P) {
        GenPolynomial<GenPolynomial<C>> S = recursiveUnivariateSquarefreePart(P);
        boolean f = P.equals(S);
        if (!f) {
            System.out.println("\nisSquarefree: " + f);
            System.out.println("S = " + S);
            System.out.println("P = " + P);
        }
        return f;
    }


    /**
     * GenPolynomial squarefree factorization.
     * @param P GenPolynomial.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i^{e_i}
     *         and p_i squarefree.
     */
    public abstract SortedMap<GenPolynomial<C>, Long> squarefreeFactors(GenPolynomial<C> P);


    /**
     * GenPolynomial squarefree and co-prime list.
     * @param A list of GenPolynomials.
     * @return B with gcd(b,c) = 1 for all b != c in B and for all non-constant
     *         a in A there exists b in B with b|a and each b in B is
     *         squarefree. B does not contain zero or constant polynomials.
     */
    public abstract List<GenPolynomial<C>> coPrimeSquarefree(List<GenPolynomial<C>> A);


    /**
     * GenPolynomial squarefree and co-prime list.
     * @param a polynomial.
     * @param P squarefree co-prime list of GenPolynomials.
     * @return B with gcd(b,c) = 1 for all b != c in B and for non-constant a
     *         there exists b in P with b|a. B does not contain zero or constant
     *         polynomials.
     */
    public abstract List<GenPolynomial<C>> coPrimeSquarefree(GenPolynomial<C> a, List<GenPolynomial<C>> P);



    /**
     * GenPolynomial is (squarefree) factorization.
     * @param P GenPolynomial.
     * @param F = [p_1,...,p_k].
     * @return true if P = prod_{i=1,...,r} p_i, else false.
     */
    public boolean isFactorization(GenPolynomial<C> P, List<GenPolynomial<C>> F) {
        if (P == null || F == null) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        GenPolynomial<C> t = P.ring.getONE();
        for (GenPolynomial<C> f : F) {
            t = t.multiply(f);
        }
        boolean f = P.equals(t) || P.equals(t.negate());
        if (!f) {
            System.out.println("\nfactorization(list): " + f);
            System.out.println("F = " + F);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
        }
        return f;
    }


    /**
     * GenPolynomial is (squarefree) factorization.
     * @param P GenPolynomial.
     * @param F = [p_1 -&gt; e_1, ..., p_k -&gt; e_k].
     * @return true if P = prod_{i=1,...,k} p_i**e_i, else false.
     */
    public boolean isFactorization(GenPolynomial<C> P, SortedMap<GenPolynomial<C>, Long> F) {
        if (P == null || F == null) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        if (P.isZERO() && F.size() == 0) {
            return true;
        }
        GenPolynomial<C> t = P.ring.getONE();
        for (GenPolynomial<C> f : F.keySet()) {
            Long E = F.get(f);
            long e = E.longValue();
            GenPolynomial<C> g = Power.<GenPolynomial<C>> positivePower(f, e);
            t = t.multiply(g);
        }
        boolean f = P.equals(t) || P.equals(t.negate());
        if (!f) {
            //System.out.println("P = " + P);
            //System.out.println("t = " + t);
            P = P.monic();
            t = t.monic();
            f = P.equals(t) || P.equals(t.negate());
            if (f) {
                return f;
            }
            System.out.println("\nfactorization(map): " + f);
            System.out.println("F = " + F);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
            //RuntimeException e = new RuntimeException("fac-map");
            //e.printStackTrace();
            //throw e;
        }
        return f;
    }


    /**
     * GenPolynomial is (squarefree) factorization.
     * @param P GenPolynomial.
     * @param F = [p_1 -&gt; e_1, ..., p_k -&gt; e_k].
     * @return true if P = prod_{i=1,...,k} p_i**e_i, else false.
     */
    public boolean isRecursiveFactorization(GenPolynomial<GenPolynomial<C>> P,
            SortedMap<GenPolynomial<GenPolynomial<C>>, Long> F) {
        if (P == null || F == null) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        if (P.isZERO() && F.size() == 0) {
            return true;
        }
        GenPolynomial<GenPolynomial<C>> t = P.ring.getONE();
        for (GenPolynomial<GenPolynomial<C>> f : F.keySet()) {
            Long E = F.get(f);
            long e = E.longValue();
            GenPolynomial<GenPolynomial<C>> g = Power.<GenPolynomial<GenPolynomial<C>>> positivePower(f, e);
            t = t.multiply(g);
        }
        boolean f = P.equals(t) || P.equals(t.negate());
        if (!f) {
            //System.out.println("P = " + P);
            //System.out.println("t = " + t);
            GenPolynomialRing<C> cf = (GenPolynomialRing<C>)P.ring.coFac;
            GreatestCommonDivisorAbstract<C> engine = GCDFactory.getProxy(cf.coFac);
            GenPolynomial<GenPolynomial<C>> Pp = engine.recursivePrimitivePart(P);
            GenPolynomial<GenPolynomial<C>> tp = engine.recursivePrimitivePart(t);
            f = Pp.equals(tp) || Pp.equals(tp.negate());
            if (f) {
                return f;
            }
            System.out.println("\nfactorization(map): " + f);
            System.out.println("F = " + F);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
            //RuntimeException e = new RuntimeException("fac-map");
            //e.printStackTrace();
            //throw e;
        }
        return f;
    }


    /**
     * GenPolynomial recursive polynomial greatest squarefree divisor.
     * @param P recursive GenPolynomial.
     * @return squarefree(pp(P)).
     */
    public GenPolynomial<GenPolynomial<C>> recursiveSquarefreePart(GenPolynomial<GenPolynomial<C>> P) {
        if (P == null || P.isZERO()) {
            return P;
        }
        if (P.ring.nvar <= 1) {
            return recursiveUnivariateSquarefreePart(P);
        }
        // distributed polynomials squarefree part
        GenPolynomialRing<GenPolynomial<C>> rfac = P.ring;
        RingFactory<GenPolynomial<C>> rrfac = rfac.coFac;
        GenPolynomialRing<C> cfac = (GenPolynomialRing<C>) rrfac;
        GenPolynomialRing<C> dfac = cfac.extend(rfac.nvar);
        GenPolynomial<C> Pd = PolyUtil.<C> distribute(dfac, P);
        GenPolynomial<C> Dd = squarefreePart(Pd);
        // convert to recursive
        GenPolynomial<GenPolynomial<C>> C = PolyUtil.<C> recursive(rfac, Dd);
        return C;
    }


    /**
     * GenPolynomial recursive polynomial squarefree factorization.
     * @param P recursive GenPolynomial.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i^{e_i}
     *         and p_i squarefree.
     */
    public SortedMap<GenPolynomial<GenPolynomial<C>>, Long> recursiveSquarefreeFactors(
            GenPolynomial<GenPolynomial<C>> P) {
        SortedMap<GenPolynomial<GenPolynomial<C>>, Long> factors;
        factors = new TreeMap<GenPolynomial<GenPolynomial<C>>, Long>();
        if (P == null || P.isZERO()) {
            return factors;
        }
        if (P.ring.nvar <= 1) {
            return recursiveUnivariateSquarefreeFactors(P);
        }
        // distributed polynomials squarefree part
        GenPolynomialRing<GenPolynomial<C>> rfac = P.ring;
        RingFactory<GenPolynomial<C>> rrfac = rfac.coFac;
        GenPolynomialRing<C> cfac = (GenPolynomialRing<C>) rrfac;
        GenPolynomialRing<C> dfac = cfac.extend(rfac.nvar);
        GenPolynomial<C> Pd = PolyUtil.<C> distribute(dfac, P);
        SortedMap<GenPolynomial<C>, Long> dfacs = squarefreeFactors(Pd);
        // convert to recursive
        for (Map.Entry<GenPolynomial<C>, Long> Dm : dfacs.entrySet()) {
            GenPolynomial<C> Dd = Dm.getKey();
            Long e = Dm.getValue();
            GenPolynomial<GenPolynomial<C>> C = PolyUtil.<C> recursive(rfac, Dd);
            factors.put(C, e);
        }
        return factors;
    }

}
