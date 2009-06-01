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
 * Absolute factorization algorithms class.  
 * This class contains
 * implementations of methods for factorization over algebraically
 * closed fields.  The required field extension is computed along with
 * the factors.  The methods have been tested for prime fields of
 * characteristic zero, that is for <code>BigRational</code>.  It
 * might eventually also be used for prime fields of non-zero characteristic,
 * that is with <code>ModInteger</code>.
 * The field extension may yet not be minimal. 
 * @author Heinz Kredel
 * @param <C> coefficient type
 */

public abstract class FactorAbsolute<C extends GcdRingElem<C>> 
                      extends FactorAbstract<C> {


    private static final Logger logger = Logger.getLogger(FactorAbsolute.class);


    private final boolean debug = true || logger.isDebugEnabled();


    /*     
     * Factorization engine for algebraic number coefficients.
     */
    //not possible here because of recursion AN -> Int|Mod -> AN -> ...
    //public final FactorAbstract<AlgebraicNumber<C>> aengine;


    /**
     * No argument constructor. <b>Note:</b> can't use this constructor.
     */
    protected FactorAbsolute() {
        throw new IllegalArgumentException("don't use this constructor");
    }


    /**
     * Constructor.
     * @param cfac coefficient ring factory.
     */
    public FactorAbsolute(RingFactory<C> cfac) {
        super(cfac);
        //GenPolynomialRing<C> fac = new GenPolynomialRing<C>(cfac,1);
        //GenPolynomial<C> p = fac.univariate(0);
        //AlgebraicNumberRing<C> afac = new AlgebraicNumberRing<C>(p);
        //aengine = null; //FactorFactory.<C>getImplementation(afac); // hack
    }


    /** Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getName();
    }


    /**
     * GenPolynomial test if is absolute irreducible.
     * @param P GenPolynomial.
     * @return true if P is absolute irreducible, else false.
     */
    public boolean isAbsoluteIrreducible(GenPolynomial<C> P) {
        if (!isIrreducible(P)) {
            return false;
        }
        Factors<C> F = factorsAbsoluteIrreducible(P);
        if (F.afac == null) {
            return true;
        } else if (F.afactors.size() > 2) {
            return false;
        } else { //F.size() == 2
            boolean cnst = false;
            for ( GenPolynomial<AlgebraicNumber<C>> p : F.afactors ) {
                if ( p.isConstant() ) {
                    cnst = true;
                }
            }
            return cnst;
        }
    }


    /**
     * GenPolynomial absolute base factorization of a polynomial.
     * @param P GenPolynomial.
     * @return factors map container: [p_1 -&gt; e_1, ..., p_k -&gt; e_k] with P = prod_{i=1,...,k} p_i**e_i.
     * <b>Note:</b> K(alpha) not yet minimal.
     */
    // @Override
    public FactorsMap<C> baseFactorsAbsolute(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        SortedMap<GenPolynomial<C>,Long> factors = new TreeMap<GenPolynomial<C>,Long>();
        if (P.isZERO()) {
            return new FactorsMap<C>(P,factors);
        }
        //System.out.println("\nP_base = " + P);
        GenPolynomialRing<C> pfac = P.ring; // K[x]
        if (pfac.nvar > 1) {
            //System.out.println("\nfacs_base: univ");
            throw new RuntimeException("only for univariate polynomials");
        }
        if (!pfac.coFac.isField()) {
            //System.out.println("\nfacs_base: field");
            throw new RuntimeException("only for field coefficients");
        }
        if ( P.degree(0) <= 1 ) {
            factors.put(P,1L);
            return new FactorsMap<C>(P,factors);
        }
        // factor over K (=C)
        SortedMap<GenPolynomial<C>,Long> facs = baseFactors(P);
        if (debug && !isFactorization(P, facs)) {
             System.out.println("facs   = " + facs);
             throw new RuntimeException("isFactorization = false");
        }
        if ( logger.isInfoEnabled() ) {
            logger.info("all K factors = " + facs); // Q[X]
            //System.out.println("\nall K factors = " + facs); // Q[X]
        }
        // factor over some K(alpha)
        SortedMap<Factors<C>,Long> afactors = new TreeMap<Factors<C>,Long>();
        for ( GenPolynomial<C> p : facs.keySet() ) {
            Long e = facs.get(p);
            if ( p.degree(0) <= 1 ) {
                factors.put(p,e);
            } else {
                Factors<C> afacs = baseFactorsAbsoluteIrreducible(p);
                //System.out.println("afacs   = " + afacs);
                afactors.put(afacs,e);
            }
        }
        //System.out.println("K(alpha) factors = " + factors);
        return new FactorsMap<C>(P,factors,afactors);
    }


    /**
     * GenPolynomial absolute base factorization of a squarefree polynomial.
     * @param P squarefree and primitive GenPolynomial.
     * @return factors list container: [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     * <b>Note:</b> K(alpha) not yet minimal.
     */
    // @Override
    public FactorsList<C> baseFactorsAbsoluteSquarefree(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<C>> factors = new ArrayList<GenPolynomial<C>>();
        if (P.isZERO()) {
            return new FactorsList<C>(P,factors);
        }
        //System.out.println("\nP_base_sqf = " + P);
        GenPolynomialRing<C> pfac = P.ring; // K[x]
        if (pfac.nvar > 1) {
            //System.out.println("facs_base_sqf: univ");
            throw new RuntimeException("only for univariate polynomials");
        }
        if (!pfac.coFac.isField()) {
            //System.out.println("facs_base_sqf: field");
            throw new RuntimeException("only for field coefficients");
        }
        if ( P.degree(0) <= 1 ) {
            factors.add(P);
            return new FactorsList<C>(P,factors);
        }
        // factor over K (=C)
        List<GenPolynomial<C>> facs = baseFactorsSquarefree(P);
        //System.out.println("facs_base_irred = " + facs);
        if (debug && !isFactorization(P, facs)) {
             throw new RuntimeException("isFactorization = false");
        }
        if ( logger.isInfoEnabled() ) {
            logger.info("all K factors = " + facs); // Q[X]
            //System.out.println("\nall K factors = " + facs); // Q[X]
        }
        // factor over K(alpha)
        List<Factors<C>> afactors = new ArrayList<Factors<C>>();
        for ( GenPolynomial<C> p : facs ) {
            //System.out.println("facs_base_sqf_p = " + p);
            if ( p.degree(0) <= 1 ) {
               factors.add(p);
            } else {
               Factors<C> afacs = baseFactorsAbsoluteIrreducible(p);
               //System.out.println("afacs_base_sqf = " + afacs);
               if ( logger.isInfoEnabled() ) {
                  logger.info("K(alpha) factors = " + afacs); // K(alpha)[X]
               }
               afactors.add( afacs );
            }
        }
        //System.out.println("K(alpha) factors = " + factors);
        return new FactorsList<C>(P,factors,afactors);
    }


    /**
     * GenPolynomial base absolute factorization of a irreducible polynomial.
     * @param P irreducible! GenPolynomial.
     * @return factors container: [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i in K(alpha)[x] 
     * for suitable alpha and p_i irreducible over L[x], 
     * where K \subset K(alpha) \subset L is an algebraically closed field over K.
     * <b>Note:</b> K(alpha) not yet minimal.
     */
    public Factors<C> baseFactorsAbsoluteIrreducible(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        if (P.isZERO()) {
            return new Factors<C>(P);
        }
        //System.out.println("\nP_base_irred = " + P);
        GenPolynomialRing<C> pfac = P.ring; // K[x]
        if (pfac.nvar > 1) {
            //System.out.println("facs_base_irred: univ");
            throw new RuntimeException("only for univariate polynomials");
        }
        if (!pfac.coFac.isField()) {
            //System.out.println("facs_base_irred: field");
            throw new RuntimeException("only for field coefficients");
        }
        if (P.degree(0) <= 1) {
            return new Factors<C>(P);
        }
        // setup field extension K(alpha) where alpha = z_xx
        String[] vars = new String[] { "z_" + Math.abs(P.hashCode()%1000) };
        pfac = pfac.clone();
        vars=pfac.setVars(vars);
        P = pfac.copy(P); // hack to exchange the variables
        AlgebraicNumberRing<C> afac = new AlgebraicNumberRing<C>(P,true); // since irreducible
        if ( logger.isInfoEnabled() ) {
            logger.info("K(alpha) = " + afac); 
            logger.info("K(alpha) = " + afac.toScript()); 
            //System.out.println("K(alpha) = " + afac);
        }
        GenPolynomialRing<AlgebraicNumber<C>> pafac 
            = new GenPolynomialRing<AlgebraicNumber<C>>(afac, P.ring.nvar, P.ring.tord, /*old*/vars);
        // convert to K(alpha)
        GenPolynomial<AlgebraicNumber<C>> Pa = PolyUtil.<C> convertToAlgebraicCoefficients(pafac, P);
        if ( logger.isInfoEnabled() ) {
            logger.info("P over K(alpha) = " + Pa); 
            //logger.info("P over K(alpha) = " + Pa.toScript()); 
            //System.out.println("P in K(alpha) = " + Pa);
        }
        // factor over K(alpha)
        FactorAbstract<AlgebraicNumber<C>> engine = FactorFactory.<C>getImplementation(afac);
        //System.out.println("K(alpha) engine = " + engine);
        List<GenPolynomial<AlgebraicNumber<C>>> factors = engine.baseFactorsSquarefree( Pa );
        //System.out.println("factors = " + factors);
        if ( debug ) {
            logger.info("factors over K(alpha) = " + factors); 
            //System.out.println("factors over K(alpha) = " + factors);
        }
        // find minimal field extension K(beta) \subset K(alpha)
        return new Factors<C>(P,afac,Pa,factors);
    }


    /**
     * GenPolynomial absolute base factorization of a polynomial.
     * @param P GenPolynomial.
     * @return factors map container: [p_1 -&gt; e_1, ..., p_k -&gt; e_k] with P = prod_{i=1,...,k} p_i**e_i.
     * <b>Note:</b> K(alpha) not yet minimal.
     */
    public FactorsMap<C> factorsAbsolute(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        SortedMap<GenPolynomial<C>,Long> factors = new TreeMap<GenPolynomial<C>,Long>();
        if (P.isZERO()) {
            return new FactorsMap<C>(P,factors);
        }
        //System.out.println("\nP_mult = " + P);
        GenPolynomialRing<C> pfac = P.ring; // K[x]
        if (pfac.nvar <= 1) {
            return baseFactorsAbsolute(P);
        }
        if (!pfac.coFac.isField()) {
            throw new RuntimeException("only for field coefficients");
        }
        if (P.degree() <= 1) {
            factors.put(P,1L);
            return new FactorsMap<C>(P,factors);
        }
        // factor over K (=C)
        SortedMap<GenPolynomial<C>,Long> facs = factors(P);
        if (debug && !isFactorization(P, facs)) {
             throw new RuntimeException("isFactorization = false");
        }
        if ( debug ) {
            logger.info("all K factors = " + facs); // Q[X]
            //System.out.println("\nall K factors = " + facs); // Q[X]
        }
        SortedMap<Factors<C>,Long> afactors = new TreeMap<Factors<C>,Long>();
        // factor over K(alpha)
        for ( GenPolynomial<C> p : facs.keySet() ) {
            Long e = facs.get(p);
            if ( p.degree() <= 1 ) {
                factors.put(p,e);
            } else {
                Factors<C> afacs = factorsAbsoluteIrreducible(p);
                if ( afacs.afac == null ) { // absolute irreducible
                   factors.put(p,e);
                } else {
                   afactors.put(afacs,e);
                }
            }
        }
        //System.out.println("K(alpha) factors multi = " + factors);
        return new FactorsMap<C>(P,factors,afactors);
    }


    /**
     * GenPolynomial absolute base factorization of a squarefree polynomial.
     * @param P squarefree and primitive GenPolynomial.
     * @return factors list container: [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     * <b>Note:</b> K(alpha) not yet minimal.
     */
    // @Override
    public FactorsList<C> factorsAbsoluteSquarefree(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<C>> factors = new ArrayList<GenPolynomial<C>>();
        if (P.isZERO()) {
            return new FactorsList<C>(P,factors);
        }
        //System.out.println("\nP = " + P);
        GenPolynomialRing<C> pfac = P.ring; // K[x]
        if (pfac.nvar <= 1) {
            return baseFactorsAbsoluteSquarefree(P);
        }
        if (!pfac.coFac.isField()) {
            throw new RuntimeException("only for field coefficients");
        }
        if (P.degree() <= 1) {
            factors.add(P);
            return new FactorsList<C>(P,factors);
        }
        // factor over K (=C)
        List<GenPolynomial<C>> facs = factorsSquarefree(P);
        if (debug && !isFactorization(P, facs)) {
             throw new RuntimeException("isFactorization = false");
        }
        if ( debug ) {
            logger.info("all K factors = " + facs); // Q[X]
            //System.out.println("\nall K factors = " + facs); // Q[X]
        }
        List<Factors<C>> afactors = new ArrayList<Factors<C>>();
        // factor over K(alpha)
        for ( GenPolynomial<C> p : facs ) {
            if ( p.degree() <= 1 ) {
                factors.add(p);
            } else {
                Factors<C> afacs = factorsAbsoluteIrreducible(p);
                if ( debug ) {
                    logger.info("K(alpha) factors = " + afacs); // K(alpha)[X]
                }
                if ( afacs.afac == null ) { // absolute irreducible
                   factors.add(p);
                } else {
                   afactors.add(afacs);
                }
            }
        }
        //System.out.println("K(alpha) factors = " + factors);
        return new FactorsList<C>(P,factors,afactors);
    }


    /**
     * GenPolynomial base absolute factorization of a irreducible polynomial.
     * @param P irreducible! GenPolynomial.
     * @return factors container: [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i in K(alpha)[x] 
     * for suitable alpha and p_i irreducible over L[x], 
     * where K \subset K(alpha) \subset L is an algebraically closed field over K.
     * <b>Note:</b> K(alpha) not yet minimal.
     */
    public Factors<C> factorsAbsoluteIrreducible(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        if ( P.isZERO() ) { 
            return new Factors<C>(P);
        }
        GenPolynomialRing<C> pfac = P.ring; // K[x]
        if (pfac.nvar <= 1) {
            return baseFactorsAbsoluteIrreducible(P);
        }
        if (!pfac.coFac.isField()) {
            throw new RuntimeException("only for field coefficients");
        }
        List<GenPolynomial<C>> factors = new ArrayList<GenPolynomial<C>>();
        if ( P.degree() <= 1 ) { 
            return new Factors<C>(P);
        }
        // find field extension K(alpha)
        GenPolynomial<C> up = P;
        RingFactory<C> cf = pfac.coFac;
        long cr = cf.characteristic().longValue(); // char might be larger
        if ( cr == 0L ) {
            cr = Long.MAX_VALUE;
        }
        long rp = 0L;
        for ( int i = 0; i < (pfac.nvar-1); i++ ) {
            rp = 0L;
            GenPolynomialRing<C> nfac = pfac.contract(1);
            String[] vn = new String[] { pfac.getVars()[pfac.nvar-1] };
            GenPolynomialRing<GenPolynomial<C>> rfac 
               = new GenPolynomialRing<GenPolynomial<C>>(nfac,1,pfac.tord,vn);
            GenPolynomial<GenPolynomial<C>> upr = PolyUtil.<C>recursive(rfac,up);
            //System.out.println("upr = " + upr);
            GenPolynomial<C> ep;
            do {
                if ( rp >= cr ) {
                    throw new RuntimeException("elements of prime field exhausted: " + cr);
                }
                C r = cf.fromInteger(rp); //cf.random(rp);
                //System.out.println("r   = " + r);
                ep = PolyUtil.<C>evaluateMain(nfac,upr,r);
                //System.out.println("ep  = " + ep);
                rp++;
            } while( !isSquarefree(ep) /*todo: || ep.degree() <= 1*/ ); // max deg
            up = ep;
            pfac = nfac;
        }
        up = up.monic();
        if ( debug ) {
            logger.info("P("+rp+") = " + up); 
            //System.out.println("up  = " + up);
        }
        if ( debug && !isSquarefree(up) ) {
            throw new RuntimeException("not irreducible up = " + up);
        }
        if ( up.degree(0) <= 1 ) { 
            return new Factors<C>(P);
        }
        // find irreducible factor of up
        List<GenPolynomial<C>> UF = baseFactorsSquarefree(up);
        //System.out.println("UF  = " + UF);
        long e = up.degree(0);
        // search factor polynomial with smallest degree 
        for ( int i = 0; i < UF.size(); i++ ) {
            GenPolynomial<C> upi = UF.get(i);
            long d = upi.degree(0);
            if ( 1 <= d && d <= e ) {
                up = upi;
                e = up.degree(0);
            }
        }
        if ( up.degree(0) <= 1 ) {
            return new Factors<C>(P);
        }
        if ( debug ) {
            logger.info("field extension by " + up); 
        }

        List<GenPolynomial<AlgebraicNumber<C>>> afactors = new ArrayList<GenPolynomial<AlgebraicNumber<C>>>();

        // setup field extension K(alpha)
        String[] vars = new String[] { "z_" + Math.abs(up.hashCode()%1000) };
        pfac = pfac.clone();
        String[] ovars = pfac.setVars(vars); // side effects! 
        up = pfac.copy(up); // hack to exchange the variables

        AlgebraicNumberRing<C> afac = new AlgebraicNumberRing<C>(up,true); // since irreducible
        //System.out.println("afac = " + afac);
        GenPolynomialRing<AlgebraicNumber<C>> pafac 
            = new GenPolynomialRing<AlgebraicNumber<C>>(afac, P.ring.nvar,P.ring.tord,P.ring.getVars());
        //System.out.println("pafac = " + pafac);
        // convert to K(alpha)
        GenPolynomial<AlgebraicNumber<C>> Pa = PolyUtil.<C> convertToAlgebraicCoefficients(pafac, P);
        //System.out.println("Pa = " + Pa);
        // factor over K(alpha)
        FactorAbstract<AlgebraicNumber<C>> engine = FactorFactory.<C>getImplementation(afac);
        afactors = engine.factorsSquarefree( Pa );
        if ( debug ) {
            logger.info("K(alpha) factors multi = " + afactors);
            //System.out.println("K(alpha) factors = " + afactors);
        }
        if ( afactors.size() <= 1 ) {
            return new Factors<C>(P);
        }
        // normalize first factor to monic
        GenPolynomial<AlgebraicNumber<C>> p1 = afactors.get(0);
        AlgebraicNumber<C> p1c = p1.leadingBaseCoefficient();
        if ( !p1c.isONE() ) {
            GenPolynomial<AlgebraicNumber<C>> p2 = afactors.get(1);
            afactors.remove(p1);
            afactors.remove(p2);
            p1 = p1.divide(p1c);
            p2 = p2.multiply(p1c);
            afactors.add(p1);
            afactors.add(p2);
        }
        // find minimal field extension K(beta) \subset K(alpha)
        return new Factors<C>(P,afac,Pa,afactors);
    }


    /**
     * GenPolynomial is factorization.
     * @param facs factors container.
     * @return true if P = prod_{i=1,...,r} p_i, else false.
     */
    public boolean isAbsoluteFactorization(Factors<C> facs) {
        if (facs == null) {
            throw new IllegalArgumentException("facs may not be null");
        }
        if ( facs.afac == null ) {
            return true;
        }
        GenPolynomial<AlgebraicNumber<C>> fa = facs.apoly; 
        GenPolynomialRing<AlgebraicNumber<C>> pafac = fa.ring;
        GenPolynomial<AlgebraicNumber<C>> t = pafac.getONE();
        for (GenPolynomial<AlgebraicNumber<C>> f : facs.afactors) {
            t = t.multiply(f);
        }
        return fa.equals(t) || fa.equals(t.negate());
    }



    /**
     * GenPolynomial is factorization.
     * @param facs factors list container.
     * @return true if P = prod_{i=1,...,r} p_i, else false.
     */
    public boolean isAbsoluteFactorization(FactorsList<C> facs) {
        if (facs == null) {
            throw new IllegalArgumentException("facs may not be null");
        }
        GenPolynomial<C> P = facs.poly;
        GenPolynomial<C> t = P.ring.getONE();
        for (GenPolynomial<C> f : facs.factors) {
            t = t.multiply(f);
        }
        if ( P.equals(t) || P.equals(t.negate()) ) {
            return true;
        }
        if ( facs.afactors == null ) {
            return false;
        }
        for (Factors<C> fs : facs.afactors) {
            if ( !isAbsoluteFactorization(fs) ) {
                return false;
            }
            t = t.multiply(facs.poly);
        }
        return P.equals(t) || P.equals(t.negate());
    }


    /**
     * GenPolynomial is factorization.
     * @param facs factors map container.
     * @return true if P = prod_{i=1,...,k} p_i**e_i , else false.
     */
    public boolean isAbsoluteFactorization(FactorsMap<C> facs) {
        if (facs == null) {
            throw new IllegalArgumentException("facs may not be null");
        }
        GenPolynomial<C> P = facs.poly;
        GenPolynomial<C> t = P.ring.getONE();
        for (GenPolynomial<C> f : facs.factors.keySet() ) {
            long e = facs.factors.get(f);
            GenPolynomial<C> g = Power.<GenPolynomial<C>> positivePower(f, e);
            t = t.multiply(g);
        }
        if ( P.equals(t) || P.equals(t.negate()) ) {
            return true;
        }
        if ( facs.afactors == null ) {
            return false;
        }
        for (Factors<C> fs : facs.afactors.keySet()) {
            if ( !isAbsoluteFactorization(fs) ) {
                return false;
            }
            long e = facs.afactors.get(fs);
            GenPolynomial<C> g = Power.<GenPolynomial<C>> positivePower(fs.poly, e);
            t = t.multiply(g);
        }
        boolean b = P.equals(t) || P.equals(t.negate());
        if (!b) {
            System.out.println("\nfactorization: " + b);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
        }
        return b;
    }

}
