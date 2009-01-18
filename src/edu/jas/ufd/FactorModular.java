/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.Power;


/**
 * Modular coefficients factorization algorithms.
 * @author Heinz Kredel
 */

public class FactorModular //<C extends GcdRingElem<C> > 
        extends FactorAbstract<ModInteger> {


    private static final Logger logger = Logger.getLogger(FactorModular.class);


    private final boolean debug = logger.isInfoEnabled();


    /**
     * GenPolynomial base distinct degree factorization.
     * @param P GenPolynomial<ModInteger>.
     * @return [e_1 -> p_1, ..., e_k -> p_k] with P = prod_{i=1,...,k} p_i and
     *         p_i has only factors of degree e_i.
     */
    public SortedMap<Long, GenPolynomial<ModInteger>> baseDistinctDegreeFactors(GenPolynomial<ModInteger> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        SortedMap<Long, GenPolynomial<ModInteger>> facs = new TreeMap<Long, GenPolynomial<ModInteger>>();
        if (P.isZERO()) {
            return facs;
        }
        GenPolynomialRing<ModInteger> pfac = P.ring;
        if (pfac.nvar > 1) {
            throw new RuntimeException(this.getClass().getName() + " only for univariate polynomials");
        }
        ModIntegerRing mr = (ModIntegerRing) pfac.coFac;
        java.math.BigInteger m = mr.modul;
        if (m.longValue() == 2L) {
            throw new RuntimeException(this.getClass().getName() + " case p = 2 not implemented");
        }
        GenPolynomial<ModInteger> one = pfac.getONE();
        GenPolynomial<ModInteger> x = pfac.univariate(0);
        GenPolynomial<ModInteger> h = x;
        GenPolynomial<ModInteger> f = P;
        GenPolynomial<ModInteger> g;
        GreatestCommonDivisor<ModInteger> engine = GCDFactory.<ModInteger> getImplementation(pfac.coFac);
        Power<GenPolynomial<ModInteger>> pow = new Power<GenPolynomial<ModInteger>>(pfac);
        long d = 0;
        while (d + 1 <= f.degree(0) / 2) {
            d++;
            h = pow.modPower(h, m, f);
            g = engine.gcd(h.subtract(x), f);
            if (!g.isONE()) {
                facs.put(d, g);
                f = f.divide(g);
            }
        }
        if (!f.isONE()) {
            d = f.degree(0);
            facs.put(d, f);
        }
        return facs;
    }


    /**
     * GenPolynomial base equal degree factorization.
     * @param P GenPolynomial<ModInteger>.
     * @param deg such that P has only irreducible factors of degree deg.
     * @return [p_1,...,p_k] with P = prod_{i=1,...,r} p_i.
     */
    public List<GenPolynomial<ModInteger>> baseEqualDegreeFactors(GenPolynomial<ModInteger> P, long deg) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        List<GenPolynomial<ModInteger>> facs = new ArrayList<GenPolynomial<ModInteger>>();
        if (P.isZERO()) {
            return facs;
        }
        GenPolynomialRing<ModInteger> pfac = P.ring;
        if (pfac.nvar > 1) {
            throw new RuntimeException(this.getClass().getName() + " only for univariate polynomials");
        }
        if (P.degree(0) == deg) {
            facs.add(P);
            return facs;
        }
        ModIntegerRing mr = (ModIntegerRing) pfac.coFac;
        java.math.BigInteger m = mr.modul;
        //System.out.println("m = " + m);
        if (m.equals(java.math.BigInteger.valueOf(2L))) {
            throw new RuntimeException(this.getClass().getName() + " case p = 2 not implemented");
        }
        GenPolynomial<ModInteger> one = pfac.getONE();
        GenPolynomial<ModInteger> r;
        GenPolynomial<ModInteger> h;
        GenPolynomial<ModInteger> f = P;
        GreatestCommonDivisor<ModInteger> engine = GCDFactory.<ModInteger> getImplementation(pfac.coFac);
        Power<GenPolynomial<ModInteger>> pow = new Power<GenPolynomial<ModInteger>>(pfac);
        GenPolynomial<ModInteger> g = null;
        int degi = (int) deg; //f.degree(0);
        //System.out.println("deg = " + deg);
        do {
            r = pfac.random(17, degi, 2 * degi, 1.0f);
            if (r.degree(0) > f.degree(0)) {
                r = r.remainder(f);
            }
            r = r.monic();
            //System.out.println("r = " + r);
            BigInteger di = Power.<BigInteger> positivePower(new BigInteger(m), deg);
            //System.out.println("di = " + di);
            java.math.BigInteger d = di.getVal(); //.longValue()-1;
            //System.out.println("d = " + d);
            d = d.shiftRight(1); // divide by 2
            h = pow.modPower(r, d, f);
            g = engine.gcd(h.subtract(one), f);
            //System.out.println("g = " + g);
            degi++;
        } while (g.degree(0) == 0 || g.degree(0) == f.degree(0));
        f = f.divide(g);
        facs.addAll(baseEqualDegreeFactors(f, deg));
        facs.addAll(baseEqualDegreeFactors(g, deg));
        return facs;
    }


    /**
     * GenPolynomial base factorization of a squarefree polynomial.
     * @param P squarefree and monic! GenPolynomial<ModInteger>.
     * @return [p_1,...,p_k] with P = prod_{i=1,...,r} p_i.
     */
    @Override
    public List<GenPolynomial<ModInteger>> baseFactorsSquarefree(GenPolynomial<ModInteger> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<ModInteger>> factors = new ArrayList<GenPolynomial<ModInteger>>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.add(P);
            return factors;
        }
        GenPolynomialRing<ModInteger> pfac = P.ring;
        if (pfac.nvar > 1) {
            throw new RuntimeException(this.getClass().getName() + " only for univariate polynomials");
        }
        if (!P.leadingBaseCoefficient().isONE()) {
            throw new RuntimeException("ldcf(P) != 1 " + P.leadingBaseCoefficient().isONE());
        }
        SortedMap<Long, GenPolynomial<ModInteger>> dfacs = baseDistinctDegreeFactors(P);
        //System.out.println("dfacs    = " + dfacs);
        for (Long e : dfacs.keySet()) {
            GenPolynomial<ModInteger> f = dfacs.get(e);
            List<GenPolynomial<ModInteger>> efacs = baseEqualDegreeFactors(f, e);
            //System.out.println("efacs " + e + "   = " + efacs);
            factors.addAll(efacs);
        }
        //System.out.println("factors  = " + factors);
        SortedSet<GenPolynomial<ModInteger>> ss = new TreeSet<GenPolynomial<ModInteger>>(factors);
        //System.out.println("sorted   = " + ss);
        factors.clear();
        factors.addAll(ss);
        return factors;
    }

}
