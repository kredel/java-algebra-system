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
 * This class contains implementations of all methods of the <code>Factorization</code>
 * interface, except the method for factorization of a squarefree polynomial. 
 * The methods to obtain squarefree polynomials delegate the computation to the 
 * <code>GreatestCommonDivisor</code> classes and are included for convenience.
 * @param <C> coefficient type
 * @author Heinz Kredel
 * @usage 
 * @see edu.jas.ufd.FactorFactory
 */

public abstract class FactorAbstract<C extends GcdRingElem<C>> 
                      implements Factorization<C> {


    private static final Logger logger = Logger.getLogger(FactorAbstract.class);


    private final boolean debug = logger.isInfoEnabled();


    /**
     * Gcd engine for base coefficients.
     */
    protected final GreatestCommonDivisorAbstract<C> engine;


    /**
     * No argument constructor. 
     */
    protected FactorAbstract() {
        throw new IllegalArgumentException("don't use this constructor");
    }


    /**
     * Constructor.
     * @param cfac coefficient ring factory.
     */
    public FactorAbstract(RingFactory<C> cfac) {
        engine = GCDFactory.<C> getProxy(cfac);
        //engine = GCDFactory.<C> getImplementation(cfac);
    }


    /** Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getName();
    }


    /**
     * GenPolynomial test if is irreducible.
     * @param P GenPolynomial.
     * @return true if P is irreducible, else false.
     */
    public boolean isIrreducible(GenPolynomial<C> P) {
        if (!isSquarefree(P)) {
            return false;
        }
        List<GenPolynomial<C>> F = factorsSquarefree(P);
        if (F.size() == 1) {
            return true;
        } else if (F.size() > 2) {
            return false;
        } else { //F.size() == 2
            boolean cnst = false;
            for ( GenPolynomial<C> p : F ) {
                if ( p.isConstant() ) {
                    cnst = true;
                }
            }
            return cnst;
        }
    }


    /**
     * GenPolynomial test if a non trivial factorization exsists.
     * @param P GenPolynomial.
     * @return true if P is reducible, else false.
     */
    public boolean isReducible(GenPolynomial<C> P) {
        return !isIrreducible(P);
    }


    /**
     * GenPolynomial test if is squarefree.
     * @param P GenPolynomial.
     * @return true if P is squarefree, else false.
     */
    public boolean isSquarefree(GenPolynomial<C> P) {
        GenPolynomial<C> S = squarefreePart(P);
        GenPolynomial<C> Ps = basePrimitivePart(P);
        return Ps.equals(S);
    }


    /**
     * GenPolynomial factorization of a squarefree polynomial.
     * @param P squarefree and primitive! (respectively monic) GenPolynomial.
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
     * Univariate GenPolynomial factorization ignoring multiplicities.
     * @param P GenPolynomial in one variable.
     * @return [p_1, ..., p_k] with P = prod_{i=1,...,k} p_i**{e_i} for some e_i.
     */
    public List<GenPolynomial<C>> baseFactorsRadical(GenPolynomial<C> P) {
        return new ArrayList<GenPolynomial<C>>( baseFactors(P).keySet() );
    }


    /**
     * Univariate GenPolynomial factorization.
     * @param P GenPolynomial in one variable.
     * @return [p_1 -&gt; e_1, ..., p_k -&gt; e_k] with P = prod_{i=1,...,k} p_i**e_i.
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
        C c;
        if ( pfac.coFac.isField() ) { //pfac.characteristic().signum() > 0
            c = P.leadingBaseCoefficient();
        } else {
            c = engine.baseContent(P);
            // move sign to the content
            if (P.signum() < 0 && c.signum() > 0 ) {
                c = c.negate();
                //P = P.negate();
            }
        }
        if (!c.isONE()) {
            GenPolynomial<C> pc = pfac.getONE().multiply(c);
            factors.put(pc, 1L);
            P = P.divide(c); // make primitive or monic
        }
        logger.info("squarefree facs P = " + P);
        SortedMap<GenPolynomial<C>, Long> facs = engine.baseSquarefreeFactors(P);
        if ( debug ) {
            logger.info("squarefree facs   = " + facs);
            //System.out.println("sfacs   = " + facs);
            boolean tt = isFactorization(P,facs);
            System.out.println("sfacs tt   = " + tt);
        }
        for (GenPolynomial<C> g : facs.keySet()) {
            Long k = facs.get(g);
            //System.out.println("g       = " + g);
            if ( pfac.coFac.isField() && !g.leadingBaseCoefficient().isONE() ) {
                g = g.monic(); // how can this happen?
                logger.warn("squarefree facs mon = " + g);
            }
            List<GenPolynomial<C>> sfacs = baseFactorsSquarefree(g);
            if ( debug ) {
               logger.info("factors of squarefree = " + sfacs);
               //System.out.println("sfacs   = " + sfacs);
            }
            for (GenPolynomial<C> h : sfacs) {
                if ( factors.get(h) != null ) {
                   System.out.println("h = (" + h + ")**" + k); 
                   throw new RuntimeException("multiple factors");
                }
                factors.put(h, k);
            }
        }
        //System.out.println("factors = " + factors);
        return factors;
    }


    /**
     * Univariate GenPolynomial factorization of a squarefree polynomial.
     * @param P squarefree and primitive! GenPolynomial in one variable.
     * @return [p_1, ..., p_k] with P = prod_{i=1,...,k} p_i.
     */
    public abstract List<GenPolynomial<C>> baseFactorsSquarefree(GenPolynomial<C> P);


    /**
     * GenPolynomial factorization ignoring multiplicities.
     * @param P GenPolynomial.
     * @return [p_1, ..., p_k] with P = prod_{i=1,...,k} p_i**{e_i} for some e_i.
     */
    public List<GenPolynomial<C>> factorsRadical(GenPolynomial<C> P) {
        return new ArrayList<GenPolynomial<C>>( factors(P).keySet() );
    }


    /**
     * GenPolynomial factorization.
     * @param P GenPolynomial.
     * @return [p_1 -&gt; e_1, ..., p_k -&gt; e_k] with P = prod_{i=1,...,k} p_i**e_i.
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
        C c;
        if ( pfac.coFac.isField() ) { // pfac.characteristic().signum() > 0
            c = P.leadingBaseCoefficient();
        } else {
            c = engine.baseContent(P);
            // move sign to the content
            if (P.signum() < 0 && c.signum() > 0) {
                c = c.negate();
                //P = P.negate();
            }
        }
        if (!c.isONE()) {
            GenPolynomial<C> pc = pfac.getONE().multiply(c);
            factors.put(pc, 1L);
            P = P.divide(c); // make base primitive or base monic
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
                logger.info("factors of squarefree = " + sfacs);
               //System.out.println("sfacs   = " + sfacs);
            }
            for (GenPolynomial<C> h : sfacs) {
                if ( factors.get(h) != null ) {
                   throw new RuntimeException("multiple factors " + "h = (" + h + ")**" + d + "::" + factors.get(h));
                }
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
        return engine.squarefreePart(P);
    }


    /**
     * GenPolynomial primitive part. Delegates computation to a
     * GreatestCommonDivisor class.
     * @param P GenPolynomial.
     * @return primitivePart(P).
     */
    public GenPolynomial<C> primitivePart(GenPolynomial<C> P) {
        return engine.primitivePart(P);
    }


    /**
     * GenPolynomial base primitive part. Delegates computation to a
     * GreatestCommonDivisor class.
     * @param P GenPolynomial.
     * @return basePrimitivePart(P).
     */
    public GenPolynomial<C> basePrimitivePart(GenPolynomial<C> P) {
        return engine.basePrimitivePart(P);
    }


    /**
     * GenPolynomial squarefree factorization. Delegates computation to a
     * GreatestCommonDivisor class.
     * @param P GenPolynomial.
     * @return [p_1 -&gt; e_1, ..., p_k -&gt; e_k] with P = prod_{i=1,...,k} p_i**e_i.
     */
    public SortedMap<GenPolynomial<C>, Long> squarefreeFactors(GenPolynomial<C> P) {
        return engine.squarefreeFactors(P);
    }


    /**
     * GenPolynomial is factorization.
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
            System.out.println("P = " + P);
            System.out.println("t = " + t);
        }
        return f;
    }


    /**
     * GenPolynomial is factorization.
     * @param P GenPolynomial.
     * @param F = [p_1 -&gt; e_1, ..., p_k -&gt; e_k].
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
            System.out.println("P = " + P);
            System.out.println("t = " + t);
            P = P.monic();
            t = t.monic();
            f = P.equals(t) || P.equals(t.negate());
            if ( f ) {
                return f;
            }
            System.out.println("\nfactorization(map): " + f);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
            //RuntimeException e = new RuntimeException("fac-map");
            //e.printStackTrace();
            //throw e;
        }
        return f;
    }


    /**
     * GenPolynomial is factorization.
     * @param P GenPolynomial.
     * @param F = [p_1 -&gt; e_1, ..., p_k -&gt; e_k].
     * @return true if P = prod_{i=1,...,k} p_i**e_i , else false.
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
            System.out.println("P = " + P);
            System.out.println("t = " + t);
            P = P.monic();
            t = t.monic();
            f = P.equals(t) || P.equals(t.negate());
            if ( f ) {
                return f;
            }
            System.out.println("\nfactorization(map): " + f);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
            //RuntimeException e = new RuntimeException("fac-map");
            //e.printStackTrace();
            //throw e;
        }
        return f;
    }


    /**
     * Recursive GenPolynomial factorization of a squarefree polynomial.
     * @param P squarefree recursive GenPolynomial.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    public List<GenPolynomial<GenPolynomial<C>>> 
      recursiveFactorsSquarefree(GenPolynomial<GenPolynomial<C>> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<GenPolynomial<C>>> factors = new ArrayList<GenPolynomial<GenPolynomial<C>>>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.add(P);
            return factors;
        }
        GenPolynomialRing<GenPolynomial<C>> pfac = P.ring;
        GenPolynomialRing<C> qi = (GenPolynomialRing<C>)pfac.coFac;
        GenPolynomialRing<C> ifac = qi.extend(pfac.nvar);
        GenPolynomial<C> Pi = PolyUtil.<C>distribute(ifac, P);
        //System.out.println("Pi = " + Pi);

        C ldcf = Pi.leadingBaseCoefficient();
        if ( !ldcf.isONE() && ldcf.isUnit() ) {
            //System.out.println("ldcf = " + ldcf);
            Pi = Pi.monic();
        }

        // factor in C[x_1,...,x_n,y_1,...,y_m]
        List<GenPolynomial<C>> ifacts = factorsSquarefree(Pi);
        if (logger.isInfoEnabled()) {
            logger.info("ifacts = " + ifacts);
        }
        if (ifacts.size() <= 1) {
            factors.add(P);
            return factors;
        }
        if ( !ldcf.isONE() && ldcf.isUnit() ) {
            GenPolynomial<C> r = ifacts.get(0);
            ifacts.remove(r);
            r = r.multiply(ldcf);
            ifacts.add(0, r);
        }
        List<GenPolynomial<GenPolynomial<C>>> rfacts = PolyUtil.<C>recursive(pfac, ifacts);
        //System.out.println("rfacts = " + rfacts);
        if (logger.isInfoEnabled()) {
            logger.info("rfacts = " + rfacts);
        }
        factors.addAll(rfacts);
        return factors;
    }


    /**
     * Recursive GenPolynomial factorization.
     * @param P recursive GenPolynomial.
     * @return [p_1 -&gt; e_1, ..., p_k -&gt; e_k] with P = prod_{i=1,...,k} p_i**e_i.
     */
    public SortedMap<GenPolynomial<GenPolynomial<C>>, Long> 
      recursiveFactors(GenPolynomial<GenPolynomial<C>> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        GenPolynomialRing<GenPolynomial<C>> pfac = P.ring;
        SortedMap<GenPolynomial<GenPolynomial<C>>, Long> factors 
              = new TreeMap<GenPolynomial<GenPolynomial<C>>, Long>(pfac.getComparator());
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.put(P,1L);
            return factors;
        }
        GenPolynomialRing<C> qi = (GenPolynomialRing<C>)pfac.coFac;
        GenPolynomialRing<C> ifac = qi.extend(pfac.nvar);
        GenPolynomial<C> Pi = PolyUtil.<C>distribute(ifac, P);
        //System.out.println("Pi = " + Pi);

        C ldcf = Pi.leadingBaseCoefficient();
        if ( !ldcf.isONE() && ldcf.isUnit() ) {
            //System.out.println("ldcf = " + ldcf);
            Pi = Pi.monic();
        }

        // factor in C[x_1,...,x_n,y_1,...,y_m]
        SortedMap<GenPolynomial<C>,Long> dfacts = factors(Pi);
        if (logger.isInfoEnabled()) {
            logger.info("dfacts = " + dfacts);
        }
        if ( !ldcf.isONE() && ldcf.isUnit() ) {
            GenPolynomial<C> r = dfacts.firstKey();
            Long E = dfacts.remove(r);
            r = r.multiply(ldcf);
            dfacts.put(r,E);
        }
        for (GenPolynomial<C> f : dfacts.keySet()) {
            Long E = dfacts.get(f);
            GenPolynomial<GenPolynomial<C>> rp = PolyUtil.<C>recursive(pfac, f);
            factors.put(rp,E);
        }
        //System.out.println("rfacts = " + rfacts);
        if (logger.isInfoEnabled()) {
            logger.info("factors = " + factors);
        }
        return factors;
    }

}
