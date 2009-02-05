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
        GenPolynomialRing<C> ufac = kr.ring;
        ufac.setVars( new String[] { "zz" }  ); // side effects 
        if ( debug ) {
            logger.info("subs(P,d=" + d + ") = " + kr);
            //System.out.println("subs(P,d=" + d + ") = " + kr);
        }
        if (kr.degree(0) > 100) {
            logger.warn("Kronecker substitution has to high degree");
        }

        // factor Kronecker polynomial
        List<GenPolynomial<C>> ulist = new ArrayList<GenPolynomial<C>>();
        // kr might not be squarefree so complete factor univariate
        SortedMap<GenPolynomial<C>, Long> slist = baseFactors(kr);
        if ( debug && !isFactorization(kr, slist)) {
            System.out.println("kr    = " + kr);
            System.out.println("slist = " + slist);
            throw new RuntimeException("no factorization");
        }
        for (GenPolynomial<C> g : slist.keySet()) {
            long e = slist.get(g);
            for (int i = 0; i < e; i++) { // is this really required? yes!
                ulist.add(g);
            }
        }
        //System.out.println("ulist = " + ulist);
        if (ulist.size() == 1 && ulist.get(0).degree() == P.degree()) {
            factors.add(P);
            return factors;
        }
        //List<GenPolynomial<C>> klist = PolyUfdUtil.<C> backSubstituteKronecker(pfac, ulist, d);
        //System.out.println("back(klist) = " + PolyUfdUtil.<C> backSubstituteKronecker(pfac, ulist, d));
        if ( debug ) {
            logger.info("ulist = " + ulist);
            //System.out.println("ulist = " + ulist);
        }
        // combine trial factors
        int dl = ulist.size()-1; //(ulist.size() + 1) / 2;
        //System.out.println("dl = " + dl);
        int ti = 0;
        GenPolynomial<C> u = P;
        long deg = (u.degree() + 1L) / 2L; // max deg
        //System.out.println("deg = " + deg);
        for (int j = 1; j <= dl; j++) {
            KsubSet<GenPolynomial<C>> ps = new KsubSet<GenPolynomial<C>>(ulist, j);
            for (List<GenPolynomial<C>> flist : ps) {
                //System.out.println("flist = " + flist);
                GenPolynomial<C> utrial = ufac.getONE();
                for (int k = 0; k < flist.size(); k++) {
                    utrial = utrial.multiply(flist.get(k));
                }
                GenPolynomial<C> trial = PolyUfdUtil.<C> backSubstituteKronecker(pfac, utrial, d);
                if (trial.degree() > deg || trial.isConstant() ) {
                    continue;
                }
                ti++;
                if (ti % 1000 == 0) {
                    System.out.print("ti(" + ti + ") ");
                    if (ti % 10000 == 0) {
                        System.out.println("\ndl   = " + dl + ", deg(u) = " + deg);
                        System.out.println("ulist = " + ulist);
                        System.out.println("kr    = " + kr);
                        System.out.println("u     = " + u);
                    }
                }
                GenPolynomial<C> rem = PolyUtil.<C> basePseudoRemainder(u, trial);
                //System.out.println(" rem = " + rem);
                if ( rem.isZERO()) {
                    logger.info("trial = " + trial);
                    //System.out.println("trial = " + trial);
                    factors.add(trial);
                    u = PolyUtil.<C> basePseudoDivide(u, trial); //u = u.divide( trial );
                    if (ulist.removeAll(flist)) {
                        //System.out.println("new ulist = " + ulist);
                        dl = (ulist.size() + 1) / 2;
                        j = 0; // since j++
                        break;
                    } else {
                        logger.error("error removing flist from ulist = " + ulist);
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
        if ( debug ) {
            logger.info("squarefree facs   = " + facs);
            //System.out.println("sfacs   = " + facs);
        }
        for (GenPolynomial<C> g : facs.keySet()) {
            Long k = facs.get(g);
            //System.out.println("g       = " + g);
            List<GenPolynomial<C>> sfacs = baseFactorsSquarefree(g);
            if ( debug ) {
               logger.info("factors of squarefree = " + sfacs);
               //System.out.println("sfacs   = " + sfacs);
            }
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
        if ( debug ) {
            logger.info("squarefree facs   = " + facs);
            //System.out.println("facs   = " + facs);
        }
        for (GenPolynomial<C> g : facs.keySet()) {
            Long d = facs.get(g);
            List<GenPolynomial<C>> sfacs = factorsSquarefree(g);
            if ( debug ) {
                logger.info("foctors of squarefree = " + sfacs);
               //System.out.println("sfacs   = " + sfacs);
            }
            for (GenPolynomial<C> h : sfacs) {
                factors.put(h, d);
            }
        }
        //System.out.println("factors = " + factors);
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
            long e = E.longValue();
            GenPolynomial<C> g = Power.<GenPolynomial<C>> positivePower(f, e);
            t = t.multiply(g);
        }
        boolean f = P.equals(t) || P.equals(t.negate());
        if (!f) {
            System.out.println("\nfactorization: " + f);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
        }
        return f;
    }


    /**
     * GenPolynomial is factorization.
     * @param P GenPolynomial<C>.
     * @param F = [p_1,...,p_k].
     * @return true if P = prod_{i=1,...,r} p_i, else false.
     */
    public boolean nonoAbsoluteFactorization(GenPolynomial<C> P, List<GenPolynomial<AlgebraicNumber<C>>> F) {
        if (P == null || F == null || F.isEmpty()) {
            throw new IllegalArgumentException("P or F may not be null or empty");
        }
        GenPolynomial<AlgebraicNumber<C>> fa = F.get(0); // not ok since different Q(alpha)
        GenPolynomialRing<AlgebraicNumber<C>> pafac = fa.ring;
        // convert to K(alpha)
        GenPolynomial<AlgebraicNumber<C>> Pa 
            = PolyUtil.<C> convertToAlgebraicCoefficients(pafac, P);

        GenPolynomial<AlgebraicNumber<C>> t = Pa.ring.getONE();
        for (GenPolynomial<AlgebraicNumber<C>> f : F) {
            t = t.multiply(f);
        }
        return Pa.equals(t) || Pa.equals(t.negate());
    }

}
