/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;

import edu.jas.structure.GcdRingElem;
import edu.jas.structure.Power;
import edu.jas.structure.RingFactory;

import edu.jas.util.KsubSet;


/**
 * Abstract factorization algorithms class.
 * @author Heinz Kredel
 */

public abstract class FactorAbstract<C extends GcdRingElem<C>> 
                      implements Factorization<C> {


    private static final Logger logger = Logger.getLogger(FactorAbstract.class);


    private final boolean debug = logger.isInfoEnabled();


    /** Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getName();
    }


    /**
     * GenPolynomial test if is irreducible.
     * @param P GenPolynomial<C>.
     * @return true if P is irreducible, else false.
     */
    public boolean isIrreducible(GenPolynomial<C> P) {
        if (!isSquarefree(P)) {
            return false;
        }
        List<GenPolynomial<C>> F = factorsSquarefree(P);
        if (F.size() == 1) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * GenPolynomial test if a non trivial factorization exsists.
     * @param P GenPolynomial<C>.
     * @return true if P is reducible, else false.
     */
    public boolean isReducible(GenPolynomial<C> P) {
        return !isIrreducible(P);
    }


    /**
     * GenPolynomial test if is squarefree.
     * @param P GenPolynomial<C>.
     * @return true if P is squarefree, else false.
     */
    public boolean isSquarefree(GenPolynomial<C> P) {
        GenPolynomial<C> S = squarefreePart(P);
        GenPolynomial<C> Ps = basePrimitivePart(P);
        return Ps.equals(S);
    }


    /**
     * GenPolynomial factorization of a squarefree polynomial.
     * @param P squarefree and primitive! (respectively monic) GenPolynomial<C>.
     * @return [p_1,...,p_k] with P = prod_{i=1,...,r} p_i.
     */
    public List<GenPolynomial<C>> factorsSquarefree(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar == 1) {
            return baseFactorsSquarefree(P);
        }
        List<GenPolynomial<C>> factors = new ArrayList<GenPolynomial<C>>();
        if (P.isZERO()) {
            return factors;
        }
        long d = P.degree() + 1L;
        GenPolynomial<C> kr = PolyUfdUtil.<C> substituteKronecker(P, d);
        //System.out.println("subs(P,d=" + d + ") = " + kr);
        if (kr.degree(0) > 100) {
            logger.warn("Kronecker substitution has to high degree");
        }

        // factor Kronecker polynomial
        List<GenPolynomial<C>> klist = new ArrayList<GenPolynomial<C>>();
        // kr might not be squarefree so complete factor univariate
        SortedMap<GenPolynomial<C>, Long> slist = baseFactors(kr);
        //System.out.println("slist = " + slist);
        if (true && !isFactorization(kr, slist)) {
            throw new RuntimeException("no factorization");
        }
        for (GenPolynomial<C> g : slist.keySet()) {
            long e = slist.get(g);
            for (int i = 0; i < e; i++) { // is this really required? 
                klist.add(g);
            }
        }
        //System.out.println("klist = " + klist);
        if (klist.size() == 1 && klist.get(0).degree() == P.degree()) {
            factors.add(P);
            return factors;
        }
        klist = PolyUfdUtil.<C> backSubstituteKronecker(pfac, klist, d);
        //System.out.println("back(klist) = " + klist);

        // remove constants
        GenPolynomial<C> cnst = null;
        GenPolynomial<C> ng = null;
        for (GenPolynomial<C> g : klist) {
            if (g.isConstant()) {
                cnst = g;
            } else if (g.signum() < 0) {
                ng = g;
            }
        }
        if (cnst != null) {
            //System.out.println("*** cnst = " + cnst);
            //System.out.println("*** ng   = " + ng);
            if (ng != null) {
                klist.remove(cnst);
                klist.remove(ng);
                cnst = cnst.negate();
                ng = ng.negate();
                if (!cnst.isONE()) {
                    klist.add(cnst);
                }
                klist.add(ng);
                //System.out.println("back(klist) = " + klist);
                //} else {
            }
        }

        // combine trial factors
        int dl = (klist.size() + 1) / 2;
        //System.out.println("dl = " + dl);
        int ti = 0;
        GenPolynomial<C> u = P;
        long deg = (u.degree() + 1L) / 2L;
        for (int j = 1; j <= dl; j++) {
            KsubSet<GenPolynomial<C>> ps = new KsubSet<GenPolynomial<C>>(klist, j);
            for (List<GenPolynomial<C>> flist : ps) {
                //System.out.println("flist = " + flist);
                GenPolynomial<C> trial = pfac.getONE();
                for (int k = 0; k < flist.size(); k++) {
                    trial = trial.multiply(flist.get(k));
                }
                if (trial.degree() > deg) {
                    continue;
                }
                ti++;
                if (ti % 1000 == 0) {
                    System.out.print("ti(" + ti + ") ");
                    if (ti % 10000 == 0) {
                        System.out.println("\ndl   = " + dl + ", deg(u) = " + deg);
                        System.out.println("klist = " + klist);
                        System.out.println("kr    = " + kr);
                        System.out.println("u     = " + u);
                    }
                }
                //GenPolynomial<C> trial = PolyUfdUtil.<C> backSubstituteKronecker( pfac, utrial, d ); 
                if (PolyUtil.<C> basePseudoRemainder(u, trial).isZERO()) {
                    logger.info("trial = " + trial);
                    //System.out.println("trial = " + trial);
                    factors.add(trial);
                    u = PolyUtil.<C> basePseudoDivide(u, trial); //u = u.divide( trial );
                    if (klist.removeAll(flist)) {
                        //System.out.println("new klist = " + klist);
                        dl = (klist.size() + 1) / 2;
                        j = 1;
                        if (klist.size() > 0) {
                            ps = new KsubSet<GenPolynomial<C>>(klist, j);
                        }
                        break;
                    } else {
                        logger.error("error removing flist from klist = " + klist);
                    }
                }
            }
        }
        if (!u.isONE() && !u.equals(P)) {
            logger.info("rest u = " + u);
            //System.out.println("rest u = " + u);
            factors.add(u);
        }
        if (factors.size() == 0) {
            logger.info("irred u = " + u);
            //System.out.println("irred u = " + u);
            factors.add(P);
        }
        return factors;
    }


    /**
     * Univariate GenPolynomial factorization.
     * @param P GenPolynomial<C> in one variable.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i**e_i.
     */
    public SortedMap<GenPolynomial<C>, Long> baseFactors(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        GenPolynomialRing<C> pfac = P.ring;
        SortedMap<GenPolynomial<C>, Long> factors = new TreeMap<GenPolynomial<C>, Long>(pfac.getComparator());
        if (P.isZERO()) {
            return factors;
        }
        if (pfac.nvar > 1) {
            throw new RuntimeException(this.getClass().getName() + " only for univariate polynomials");
        }
        GreatestCommonDivisorAbstract<C> engine = (GreatestCommonDivisorAbstract<C>) GCDFactory
                .<C> getImplementation(pfac.coFac);
        C c;
        if (pfac.characteristic().signum() > 0) {
            c = P.leadingBaseCoefficient();
        } else {
            c = engine.baseContent(P);
        }
        // move sign to the content
        if (P.signum() < 0 && c.signum() > 0) {
            c = c.negate();
            P = P.negate();
            //System.out.println("c = " + c);
            //System.out.println("P = " + P);
        }
        if (!c.isONE()) {
            //System.out.println("c = " + c);
            GenPolynomial<C> pc = pfac.getONE().multiply(c);
            factors.put(pc, 1L);
            P = P.divide(c.abs()); // make primitive or monic
        }
        SortedMap<GenPolynomial<C>, Long> facs = engine.baseSquarefreeFactors(P);
        //System.out.println("sfacs   = " + facs);
        for (GenPolynomial<C> g : facs.keySet()) {
            Long k = facs.get(g);
            List<GenPolynomial<C>> sfacs = baseFactorsSquarefree(g);
            //System.out.println("sfacs   = " + sfacs);
            for (GenPolynomial<C> h : sfacs) {
                factors.put(h, k);
            }
        }
        //System.out.println("factors = " + factors);
        return factors;
    }


    /**
     * Univariate GenPolynomial factorization of a squarefree polynomial.
     * @param P squarefree and primitive! GenPolynomial<C> in one variable.
     * @return [p_1, ..., p_k] with P = prod_{i=1,...,k} p_i.
     */
    public abstract List<GenPolynomial<C>> baseFactorsSquarefree(GenPolynomial<C> P);


    /**
     * GenPolynomial factorization.
     * @param P GenPolynomial<C>.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i**e_i.
     */
    public SortedMap<GenPolynomial<C>, Long> factors(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar == 1) {
            return baseFactors(P);
        }
        SortedMap<GenPolynomial<C>, Long> factors = new TreeMap<GenPolynomial<C>, Long>(pfac.getComparator());
        if (P.isZERO()) {
            return factors;
        }
        GreatestCommonDivisorAbstract<C> engine = (GreatestCommonDivisorAbstract<C>) GCDFactory
                .<C> getImplementation(pfac.coFac);
        C c;
        if (pfac.characteristic().signum() > 0) {
            c = P.leadingBaseCoefficient();
        } else {
            c = engine.baseContent(P);
        }
        // move sign to the content
        if (P.signum() < 0 && c.signum() > 0) {
            c = c.negate();
            P = P.negate();
            //System.out.println("c = " + c);
            //System.out.println("P = " + P);
        }
        if (!c.isONE()) {
            //System.out.println("baseContent = " + c);
            GenPolynomial<C> pc = pfac.getONE().multiply(c);
            factors.put(pc, 1L);
            P = P.divide(c.abs()); // make base primitive or monic
        }
        SortedMap<GenPolynomial<C>, Long> facs = engine.squarefreeFactors(P);
        //System.out.println("sfacs   = " + facs);
        for (GenPolynomial<C> g : facs.keySet()) {
            Long d = facs.get(g);
            List<GenPolynomial<C>> sfacs = factorsSquarefree(g);
            //System.out.println("sfacs   = " + sfacs);
            for (GenPolynomial<C> h : sfacs) {
                factors.put(h, d);
            }
        }
        //System.out.println("factors = " + factors);
        return factors;
    }


    /**
     * GenPolynomial absolute base factorization of a squarefree polynomial.
     * @param P squarefree and primitive GenPolynomial<C>.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    // @Override
    public List<GenPolynomial<AlgebraicNumber<C>>> baseFactorsAbsoluteSquarefree(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<AlgebraicNumber<C>>> factors = new ArrayList<GenPolynomial<AlgebraicNumber<C>>>();
        if (P.isZERO()) {
            return factors;
        }
        GenPolynomialRing<C> pfac = P.ring; // K[x]
        if (pfac.nvar > 1) {
            throw new RuntimeException("only for univariate polynomials");
        }

        //System.out.println("\nP = " + P);
        List<GenPolynomial<C>> facs = baseFactorsSquarefree(P);

        if (!isFactorization(P, facs)) {
             throw new RuntimeException("isFactorization = false");
        }
        System.out.println("\nfacs = " + facs); // Q[X]

        for ( GenPolynomial<C> p : facs ) {
            List<GenPolynomial<AlgebraicNumber<C>>> afacs = baseFactorsAbsoluteIrreducible(p);
            factors.addAll( afacs );
        }
        System.out.println("factors = " + factors);
        return factors;
    }


    /**
     * GenPolynomial absolute base factorization of a polynomial.
     * @param P GenPolynomial<C>.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    // @Override
    public SortedMap<GenPolynomial<AlgebraicNumber<C>>,Long> baseFactorsAbsolute(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        SortedMap<GenPolynomial<AlgebraicNumber<C>>,Long> factors 
            = new TreeMap<GenPolynomial<AlgebraicNumber<C>>,Long>();
        if (P.isZERO()) {
            return factors;
        }
        GenPolynomialRing<C> pfac = P.ring; // K[x]
        if (pfac.nvar > 1) {
            throw new RuntimeException("only for univariate polynomials");
        }

        //System.out.println("\nP = " + P);
        SortedMap<GenPolynomial<C>,Long> facs = baseFactors(P);

        if (!isFactorization(P, facs)) {
             throw new RuntimeException("isFactorization = false");
        }
        System.out.println("\nfacs = " + facs); // Q[X]

        for ( GenPolynomial<C> p : facs.keySet() ) {
            Long e = facs.get(p);
            List<GenPolynomial<AlgebraicNumber<C>>> afacs = baseFactorsAbsoluteIrreducible(p);
            for ( GenPolynomial<AlgebraicNumber<C>> ap : afacs ) {
                factors.put(ap,e);
            }
        }
        System.out.println("factors = " + factors);
        return factors;
    }


    /**
     * GenPolynomial base absolute factorization of a irreducible polynomial.
     * @param P irreducible! GenPolynomial<C>.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i in K(alpha)[x] for suitable alpha
     * and p_i irreducible over L[x], 
     * where K \subset K(alpha) \subset L is an algebraically closed field over K.
     */
    public List<GenPolynomial<AlgebraicNumber<C>>> baseFactorsAbsoluteIrreducible(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<AlgebraicNumber<C>>> factors = new ArrayList<GenPolynomial<AlgebraicNumber<C>>>();
        if (P.isZERO()) {
            return factors;
        }
        GenPolynomialRing<C> pfac = P.ring; // K[x]
        if (pfac.nvar > 1) {
            throw new RuntimeException("only for univariate polynomials");
        }
        // setup field extension K(alpha)
        String[] vars = new String[] { "z_"+"17" /*"pfac.getVars().hashCode()*/ };
        AlgebraicNumberRing<C> afac = new AlgebraicNumberRing<C>(P,true); // since irreducible
        GenPolynomialRing<AlgebraicNumber<C>> pafac 
            = new GenPolynomialRing<AlgebraicNumber<C>>(afac, P.ring.nvar,P.ring.tord,vars);
        // convert to K(alpha)
        GenPolynomial<AlgebraicNumber<C>> Pa 
            = PolyUtil.<C> convertToAlgebraicCoefficients(pafac, P);
        if ( Pa.degree(0) <= 1 ) {
            factors.add(Pa);
            return factors;
        }
        System.out.println("Pa = " + Pa);
        // factor over K(alpha)
        FactorAbstract<AlgebraicNumber<C>> engine = FactorFactory.<C>getImplementation(afac);
        factors = engine.baseFactorsSquarefree( Pa );
        System.out.println("factors = " + factors);
        return factors;
    }


    /**
     * GenPolynomial greatest squarefree divisor. Delegates computation to a
     * GreatestCommonDivisor class.
     * @param P GenPolynomial.
     * @return squarefree(P).
     */
    public GenPolynomial<C> squarefreePart(GenPolynomial<C> P) {
        if (P == null) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        //GenPolynomialRing<C> pfac = P.ring;
        RingFactory<C> cfac = P.ring.coFac;
        GreatestCommonDivisor<C> engine = GCDFactory.<C> getProxy(cfac);
        return engine.squarefreePart(P);
    }


    /**
     * GenPolynomial primitive part. Delegates computation to a
     * GreatestCommonDivisor class.
     * @param P GenPolynomial.
     * @return primitivePart(P).
     */
    public GenPolynomial<C> primitivePart(GenPolynomial<C> P) {
        if (P == null) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        //GenPolynomialRing<C> pfac = P.ring;
        RingFactory<C> cfac = P.ring.coFac;
        GreatestCommonDivisor<C> engine = GCDFactory.<C> getProxy(cfac);
        return engine.primitivePart(P);
    }


    /**
     * GenPolynomial base primitive part. Delegates computation to a
     * GreatestCommonDivisor class.
     * @param P GenPolynomial.
     * @return basePrimitivePart(P).
     */
    public GenPolynomial<C> basePrimitivePart(GenPolynomial<C> P) {
        if (P == null) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        //GenPolynomialRing<C> pfac = P.ring;
        RingFactory<C> cfac = P.ring.coFac;
        GreatestCommonDivisorAbstract<C> engine = (GreatestCommonDivisorAbstract<C>) GCDFactory
                .<C> getProxy(cfac);
        return engine.basePrimitivePart(P);
    }


    /**
     * GenPolynomial squarefree factorization. Delegates computation to a
     * GreatestCommonDivisor class.
     * @param P GenPolynomial.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i**e_i.
     */
    public SortedMap<GenPolynomial<C>, Long> squarefreeFactors(GenPolynomial<C> P) {
        if (P == null) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        //GenPolynomialRing<C> pfac = P.ring;
        RingFactory<C> cfac = P.ring.coFac;
        GreatestCommonDivisor<C> engine = GCDFactory.<C> getProxy(cfac);
        return engine.squarefreeFactors(P);
    }


    /**
     * GenPolynomial is factorization.
     * @param P GenPolynomial<C>.
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
        return P.equals(t) || P.equals(t.negate());
    }


    /**
     * GenPolynomial is factorization.
     * @param P GenPolynomial<C>.
     * @param F = [p_1 -> e_1, ..., p_k -> e_k].
     * @return true if P = prod_{i=1,...,k} p_i**e_i , else false.
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
            if (E != null) { // test obsolet, always true
                long e = E.longValue();
                GenPolynomial<C> g = Power.<GenPolynomial<C>> positivePower(f, e);
                t = t.multiply(g);
            }
        }
        boolean f = P.equals(t) || P.equals(t.negate());
        if (!f) {
            System.out.println("\nfactorization: " + f);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
        }
        return f;
    }

}
