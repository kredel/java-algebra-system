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
        System.out.println("subs(P,d=" + d + ") = " + kr);
        if (kr.degree(0) > 100) {
            logger.warn("Kronecker substitution has to high degree");
        }

        // factor Kronecker polynomial
        List<GenPolynomial<C>> ulist = new ArrayList<GenPolynomial<C>>();
        // kr might not be squarefree so complete factor univariate
        SortedMap<GenPolynomial<C>, Long> slist = baseFactors(kr);
        System.out.println("slist = " + slist);
        if ( !isFactorization(kr, slist)) {
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
        System.out.println("ulist = " + ulist);

        // combine trial factors
        int dl = ulist.size()-1; //(ulist.size() + 1) / 2;
        System.out.println("dl = " + dl);
        int ti = 0;
        GenPolynomial<C> u = P;
        long deg = (u.degree() + 1L) / 2L;
        System.out.println("deg = " + deg);
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
                //GenPolynomial<C> trial = PolyUfdUtil.<C> backSubstituteKronecker( pfac, utrial, d ); 
                // if (PolyUtil.<C> basePseudoRemainder(u, trial.abs()).isZERO()) {
                System.out.println("-trial = " + trial);
                System.out.println("-flist = " + flist);
                //System.out.println("prem = " + PolyUtil.<C> basePseudoRemainder(u, trial));
                //GenPolynomial<C> rem = u.remainder(trial);
                GenPolynomial<C> rem = PolyUtil.<C> basePseudoRemainder(u, trial);
                System.out.println(" rem = " + rem);
                if ( rem.isZERO()) {
                    logger.info("trial = " + trial);
                    System.out.println("trial = " + trial);
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
            System.out.println("rest u = " + u);
            factors.add(u);
        }
        if (factors.size() == 0) {
            logger.info("irred u = " + u);
            System.out.println("irred u = " + u);
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
            System.out.println("g       = " + g);
            List<GenPolynomial<C>> sfacs = baseFactorsSquarefree(g);
            System.out.println("sfacs   = " + sfacs);
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


    /* ------------------------------------------- */


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
        //System.out.println("\nP = " + P);
        GenPolynomialRing<C> pfac = P.ring; // K[x]
        if (pfac.nvar > 1) {
            throw new RuntimeException("only for univariate polynomials");
        }
        if (!pfac.coFac.isField()) {
            throw new RuntimeException("only for field coefficients");
        }
        // factor over K (=C)
        SortedMap<GenPolynomial<C>,Long> facs = baseFactors(P);
        if (!isFactorization(P, facs)) {
             throw new RuntimeException("isFactorization = false");
        }
        System.out.println("\nall K factors = " + facs); // Q[X]
        // factor over K(alpha)
        for ( GenPolynomial<C> p : facs.keySet() ) {
            Long e = facs.get(p);
            List<GenPolynomial<AlgebraicNumber<C>>> afacs = baseFactorsAbsoluteIrreducible(p);
            for ( GenPolynomial<AlgebraicNumber<C>> ap : afacs ) {
                //System.out.println("ap = (" + ap + ")**" + e + " over " + ap.ring.coFac);
                factors.put(ap,e);
            }
        }
        System.out.println("Q(alpha) factors = " + factors);
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
        //System.out.println("\nP = " + P);
        GenPolynomialRing<C> pfac = P.ring; // K[x]
        if (pfac.nvar > 1) {
            throw new RuntimeException("only for univariate polynomials");
        }
        if (!pfac.coFac.isField()) {
            throw new RuntimeException("only for field coefficients");
        }
        // factor over K (=C)
        List<GenPolynomial<C>> facs = baseFactorsSquarefree(P);
        if (debug && !isFactorization(P, facs)) {
             throw new RuntimeException("isFactorization = false");
        }
        System.out.println("\nall K factors = " + facs); // K[X]
        // factor over K(alpha)
        for ( GenPolynomial<C> p : facs ) {
            List<GenPolynomial<AlgebraicNumber<C>>> afacs = baseFactorsAbsoluteIrreducible(p);
            factors.addAll( afacs );
        }
        System.out.println("Q(alpha) factors = " + factors);
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
        if (!pfac.coFac.isField()) {
            throw new RuntimeException("only for field coefficients");
        }
        // setup field extension K(alpha)
        String[] vars = new String[] { "z_"+"7" /*"pfac.getVars().hashCode()*/ };
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
        System.out.println("Q(alpha) factors = " + factors);
        return factors;
    }


    /**
     * GenPolynomial absolute base factorization of a polynomial.
     * @param P GenPolynomial<C>.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    // @Override
    public SortedMap<GenPolynomial<AlgebraicNumber<C>>,Long> factorsAbsolute(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        SortedMap<GenPolynomial<AlgebraicNumber<C>>,Long> factors 
            = new TreeMap<GenPolynomial<AlgebraicNumber<C>>,Long>();
        if (P.isZERO()) {
            return factors;
        }
        //System.out.println("\nP = " + P);
        GenPolynomialRing<C> pfac = P.ring; // K[x]
        if (pfac.nvar <= 1) {
            return baseFactorsAbsolute(P);
        }
        if (!pfac.coFac.isField()) {
            throw new RuntimeException("only for field coefficients");
        }
        // factor over K (=C)
        SortedMap<GenPolynomial<C>,Long> facs = factors(P);
        if (!isFactorization(P, facs)) {
             throw new RuntimeException("isFactorization = false");
        }
        System.out.println("\nall K factors = " + facs); // Q[X]
        // factor over K(alpha)
        for ( GenPolynomial<C> p : facs.keySet() ) {
            Long e = facs.get(p);
            List<GenPolynomial<AlgebraicNumber<C>>> afacs = factorsAbsoluteIrreducible(p);
            for ( GenPolynomial<AlgebraicNumber<C>> ap : afacs ) {
                //System.out.println("ap = (" + ap + ")**" + e + " over " + ap.ring.coFac);
                factors.put(ap,e);
            }
        }
        System.out.println("Q(alpha) factors = " + factors);
        return factors;
    }


    /**
     * GenPolynomial absolute base factorization of a squarefree polynomial.
     * @param P squarefree and primitive GenPolynomial<C>.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    // @Override
    public List<GenPolynomial<AlgebraicNumber<C>>> factorsAbsoluteSquarefree(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<AlgebraicNumber<C>>> factors = new ArrayList<GenPolynomial<AlgebraicNumber<C>>>();
        if (P.isZERO()) {
            return factors;
        }
        //System.out.println("\nP = " + P);
        GenPolynomialRing<C> pfac = P.ring; // K[x]
        if (pfac.nvar <= 1) {
            return baseFactorsAbsoluteSquarefree(P);
        }
        if (!pfac.coFac.isField()) {
            throw new RuntimeException("only for field coefficients");
        }
        // factor over K (=C)
        List<GenPolynomial<C>> facs = factorsSquarefree(P);
        if (debug && !isFactorization(P, facs)) {
             throw new RuntimeException("isFactorization = false");
        }
        System.out.println("\nall K factors = " + facs); // K[X]
        // factor over K(alpha)
        for ( GenPolynomial<C> p : facs ) {
            List<GenPolynomial<AlgebraicNumber<C>>> afacs = factorsAbsoluteIrreducible(p);
            factors.addAll( afacs );
        }
        System.out.println("Q(alpha) factors = " + factors);
        return factors;
    }


    /**
     * GenPolynomial base absolute factorization of a irreducible polynomial.
     * @param P irreducible! GenPolynomial<C>.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i in K(alpha)[x] for suitable alpha
     * and p_i irreducible over L[x], 
     * where K \subset K(alpha) \subset L is an algebraically closed field over K.
     */
    public List<GenPolynomial<AlgebraicNumber<C>>> factorsAbsoluteIrreducible(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<AlgebraicNumber<C>>> factors = new ArrayList<GenPolynomial<AlgebraicNumber<C>>>();
        if (P.isZERO()) {
            return factors;
        }
        GenPolynomialRing<C> pfac = P.ring; // K[x]
        if (pfac.nvar <= 1) {
            return baseFactorsAbsoluteIrreducible(P);
        }
        if (!pfac.coFac.isField()) {
            throw new RuntimeException("only for field coefficients");
        }
        // find field extension K(alpha)
        GenPolynomial<C> up = P;
        RingFactory<C> cf = pfac.coFac;
        long rp = 0L;
        for ( int i = 0; i < (pfac.nvar-1); i++ ) {
            GenPolynomialRing<C> nfac = pfac.contract(1);
            String[] vn = new String[] { pfac.getVars()[pfac.nvar-1] };
            GenPolynomialRing<GenPolynomial<C>> rfac 
               = new GenPolynomialRing<GenPolynomial<C>>(nfac,1,pfac.tord,vn);
            GenPolynomial<GenPolynomial<C>> upr = PolyUtil.<C>recursive(rfac,up);
            System.out.println("upr = " + upr);
            GenPolynomial<C> ep;
            do {
                C r = cf.fromInteger(rp); //cf.random(rp);
                System.out.println("r   = " + r);
                ep = PolyUtil.<C>evaluateMain(nfac,upr,r);
                System.out.println("ep  = " + ep);
                rp++;
            } while( !isSquarefree(ep) );
            up = ep;
            pfac = nfac;
        }
        up = up.monic();
        System.out.println("up  = " + up);
        if ( ! isSquarefree(up) ) {
            throw new RuntimeException("not irreducible up = " + up);
        }
        // find irreducible factor of up
        List<GenPolynomial<C>> UF = baseFactorsSquarefree(up);
        System.out.println("UF  = " + UF);
        if ( UF.size() > 1 ) {
           up = UF.get(0);
        } else { // what to take ? zero would be correct but cannot be used for Q(alpha)
           up = UF.get(0);
        }
        System.out.println("up  = " + up);

        // setup field extension K(alpha)
        String[] vars = new String[] { "alpha" /*"pfac.getVars().hashCode()*/ };
        String[] ovars = pfac.setVars(vars); 
        up = pfac.copy(up); // hack

        AlgebraicNumberRing<C> afac = new AlgebraicNumberRing<C>(up,true); // since irreducible
        System.out.println("afac = " + afac);
        GenPolynomialRing<AlgebraicNumber<C>> pafac 
            = new GenPolynomialRing<AlgebraicNumber<C>>(afac, P.ring.nvar,P.ring.tord,P.ring.getVars());
        //System.out.println("pafac = " + pafac);
        // convert to K(alpha)
        GenPolynomial<AlgebraicNumber<C>> Pa = PolyUtil.<C> convertToAlgebraicCoefficients(pafac, P);
        if ( Pa.degree(0) <= 1 ) {
            factors.add(Pa);
            return factors;
        }
        System.out.println("Pa = " + Pa);
        // factor over K(alpha)
        FactorAbstract<AlgebraicNumber<C>> engine = FactorFactory.<C>getImplementation(afac);
        factors = engine.factorsSquarefree( Pa );
        System.out.println("Q(alpha) factors = " + factors);
        if ( factors.size() > 1 ) {
            GenPolynomial<AlgebraicNumber<C>> p1 = factors.get(0);
            AlgebraicNumber<C> p1c = p1.leadingBaseCoefficient();
            if ( !p1c.isONE() ) {
                GenPolynomial<AlgebraicNumber<C>> p2 = factors.get(1);
                factors.remove(p1);
                factors.remove(p2);
                p1 = p1.divide(p1c);
                p2 = p2.multiply(p1c);
                factors.add(p1);
                factors.add(p2);
            }
        }
        return factors;
    }


    /* ------------------------------------------- */


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


    /**
     * GenPolynomial is factorization.
     * @param P GenPolynomial<C>.
     * @param F = [p_1 -> e_1, ..., p_k -> e_k].
     * @return true if P = prod_{i=1,...,k} p_i**e_i , else false.
     */
    public boolean nonoAbsoluteFactorization(GenPolynomial<C> P, SortedMap<GenPolynomial<AlgebraicNumber<C>>, Long> F) {
        if (P == null || F == null || F.isEmpty()) {
            throw new IllegalArgumentException("P or F may not be null or empty");
        }
        GenPolynomial<AlgebraicNumber<C>> fa = F.firstKey(); // not ok since different Q(alpha)
        GenPolynomialRing<AlgebraicNumber<C>> pafac = fa.ring;
        // convert to K(alpha)
        GenPolynomial<AlgebraicNumber<C>> Pa 
            = PolyUtil.<C> convertToAlgebraicCoefficients(pafac, P);

        GenPolynomial<AlgebraicNumber<C>> t = Pa.ring.getONE();
        for (GenPolynomial<AlgebraicNumber<C>> f : F.keySet()) {
            Long E = F.get(f);
            long e = E.longValue();
            GenPolynomial<AlgebraicNumber<C>> g = Power.<GenPolynomial<AlgebraicNumber<C>>> positivePower(f, e);
            t = t.multiply(g);
        }
        boolean f = Pa.equals(t) || Pa.equals(t.negate());
        if (!f) {
            System.out.println("\nfactorization: " + f);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
        }
        return f;
    }

}
