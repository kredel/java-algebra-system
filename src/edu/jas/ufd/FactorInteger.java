/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.PrimeList;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.RingFactory;
import edu.jas.util.KsubSet;


/**
 * Integer coefficients factorization algorithms.
 * @author Heinz Kredel
 */

public class FactorInteger //<C extends GcdRingElem<C> > 
        extends FactorAbstract<BigInteger> {


    private static final Logger logger = Logger.getLogger(FactorInteger.class);


    private final boolean debug = true || logger.isInfoEnabled();


    /**
     * GenPolynomial base factorization of a squarefree polynomial.
     * @param P squarefree and primitive! GenPolynomial<BigInteger>.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<GenPolynomial<BigInteger>> baseFactorsSquarefree(GenPolynomial<BigInteger> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<BigInteger>> factors = new ArrayList<GenPolynomial<BigInteger>>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.add(P);
            return factors;
        }
        GenPolynomialRing<BigInteger> pfac = P.ring;
        if (pfac.nvar > 1) {
            throw new RuntimeException(this.getClass().getName() + " only for univariate polynomials");
        }
        // compute norm
        BigInteger an = P.maxNorm();
        BigInteger ac = P.leadingBaseCoefficient();
        //compute factor coefficient bounds
        ExpVector degv = P.degreeVector();
        BigInteger M = an.multiply(PolyUtil.factorBound(degv));
        M = M.multiply(ac.multiply(ac.fromInteger(8)));
        //System.out.println("M = " + M);

        //initialize prime list and degree vector
        PrimeList primes = new PrimeList(PrimeList.Range.small);
        int pn = 30; //primes.size();
        ModIntegerRing cofac = new ModIntegerRing(13, true);
        RingFactory<ModInteger> mcofac = new ModIntegerRing(13, true);
        GreatestCommonDivisorAbstract<ModInteger> engine = (GreatestCommonDivisorAbstract<ModInteger>) GCDFactory
                .<ModInteger> getImplementation(mcofac);
        GenPolynomial<ModInteger> am = null;
        GenPolynomialRing<ModInteger> mfac = null;
        final int TT = 4; // 7
        List<GenPolynomial<ModInteger>>[] modfac = (List<GenPolynomial<ModInteger>>[]) new List[TT];
        List<GenPolynomial<BigInteger>>[] intfac = (List<GenPolynomial<BigInteger>>[]) new List[TT];
        List<GenPolynomial<ModInteger>> mlist = null;
        List<GenPolynomial<BigInteger>> ilist = null;
        int i = 0;
        if (debug) {
            logger.debug("an  = " + an);
            logger.debug("ac  = " + ac);
            logger.debug("M   = " + M);
            logger.info("degv = " + degv);
        }
        Iterator<java.math.BigInteger> pit = primes.iterator();
        pit.next();
        ModInteger nf = null;
        for (int k = 0; k < TT; k++) {
            if (k == TT - 1) { // -2
                primes = new PrimeList(PrimeList.Range.medium);
                pit = primes.iterator();
            }
            if (k == TT + 1) { // -1
                primes = new PrimeList(PrimeList.Range.large);
                pit = primes.iterator();
            }
            while (pit.hasNext()) {
                java.math.BigInteger p = pit.next();
                //System.out.println("next run ++++++++++++++++++++++++++++++++++");
                if (++i >= pn) {
                    logger.error("prime list exhausted, pn = " + pn);
                    throw new RuntimeException("prime list exhausted");
                }
                cofac = new ModIntegerRing(p, true);
                nf = cofac.fromInteger(ac.getVal());
                if (nf.isZERO()) {
                    logger.info("unlucky prime (nf) = " + p);
                    //System.out.println("unlucky prime (nf) = " + p);
                    continue;
                }
                // initialize polynomial factory and map polynomial
                mfac = new GenPolynomialRing<ModInteger>(cofac, pfac);
                am = PolyUtil.<ModInteger> fromIntegerCoefficients(mfac, P);
                if (!am.degreeVector().equals(degv)) { // allways true
                    logger.info("unlucky prime (deg) = " + p);
                    //System.out.println("unlucky prime (deg) = " + p);
                    continue;
                }
                GenPolynomial<ModInteger> ap = PolyUtil.<ModInteger> baseDeriviative(am);
                if (ap.isZERO()) {
                    logger.info("unlucky prime (a')= " + p);
                    //System.out.println("unlucky prime (a')= " + p);
                    continue;
                }
                GenPolynomial<ModInteger> g = engine.baseGcd(am, ap);
                if (g.isONE()) {
                    logger.info("**lucky prime = " + p);
                    //System.out.println("**lucky prime = " + p);
                    break;
                }
            }
            // now am is squarefree mod p, make monic and factor mod p
            FactorModular mengine = new FactorModular();
            if (!nf.isONE()) {
                //System.out.println("nf = " + nf);
                am = am.divide(nf); // make monic
            }
            mlist = mengine.baseFactorsSquarefree(am);
            if (logger.isInfoEnabled()) {
                logger.info("modlist  = " + mlist);
            }
            if (mlist.size() <= 1) {
                factors.add(P);
                return factors;
            }
            if (!nf.isONE()) {
                GenPolynomial<ModInteger> mp = mfac.getONE(); //mlist.get(0);
                //System.out.println("mp = " + mp);
                mp = mp.multiply(nf);
                //System.out.println("mp = " + mp);
                mlist.add(0, mp); // set(0,mp);
            }
            modfac[k] = mlist;
        }

        // search shortest factor list
        int min = Integer.MAX_VALUE;
        for (int k = 0; k < TT; k++) {
            int s = modfac[k].size();
            logger.info("mod s = " + s);
            //System.out.println("mod s = " + s);
            if (s < min) {
                min = s;
                mlist = modfac[k];
            }
        }
        if (mlist.size() <= 1) {
            logger.info("mod s = 1");
            //System.out.println("int s = 1");
            factors.add(P);
            return factors;
        }

        boolean allLists = false;
        if (allLists) {
            // try each factor list
            for (int k = 0; k < TT; k++) {
                mlist = modfac[k];
                if (P.leadingBaseCoefficient().isONE()) {
                    factors = searchFactorsMonic(P, M, mlist);
                } else {
                    factors = searchFactorsNonMonic(P, M, mlist);
                }
                intfac[k] = factors;
            }
        } else {
            // try only shortest factor list
            if ( P.leadingBaseCoefficient().isONE()) {
                factors = searchFactorsMonic(P, M, mlist);
            } else {
                factors = searchFactorsNonMonic(P, M, mlist);
            }
            return factors;
        }

        // search longest factor list
        int max = 0;
        for (int k = 0; k < TT; k++) {
            int s = intfac[k].size();
            System.out.println("int s = " + s);
            if (s > max) {
                max = s;
                ilist = intfac[k];
            }
        }
        factors = ilist;
        return factors;
    }


    /**
     * Factor search with ModInteger Hensel lifting algorithm. Let p =
     * f_i.ring.coFac.modul() i = 0, ..., n-1 and assume C == prod_{0,...,n-1}
     * f_i mod p with ggt(f_i,f_j) == 1 mod p for i != j
     * @param C GenPolynomial<BigInteger>.
     * @param F = [f_0,...,f_{n-1}] List<GenPolynomial<ModInteger>>.
     * @param M bound on the coefficients of g_i as factors of C.
     * @return [g_0,...,g_{n-1}] = lift(C,F), with C = prod_{0,...,n-1} g_i mod
     *         p**e.
     */
    public static//<C extends RingElem<C>>
    List<GenPolynomial<BigInteger>> searchFactorsMonic(GenPolynomial<BigInteger> C, BigInteger M,
                                                       List<GenPolynomial<ModInteger>> F) {
        //System.out.println("*** monic factor combination ***");
        if (C == null || C.isZERO() || F == null || F.size() == 0) {
            throw new RuntimeException("C must be nonzero and F must be nonempty");
        }
        GenPolynomialRing<BigInteger> pfac = C.ring;
        if (pfac.nvar != 1) { // todo assert
            throw new RuntimeException("polynomial ring not univariate");
        }
        List<GenPolynomial<BigInteger>> factors = new ArrayList<GenPolynomial<BigInteger>>(F.size());
        List<GenPolynomial<ModInteger>> mlist = F;
        List<GenPolynomial<BigInteger>> ilist = null;

        //ModInteger nf = null;
        GenPolynomial<ModInteger> ct = mlist.get(0);
        if (ct.isConstant()) {
            //nf = ct.leadingBaseCoefficient();
            mlist.remove(ct);
            //System.out.println("=== nf = " + nf);
            if (mlist.size() <= 1) {
                factors.add(C);
                return factors;
            }
        } else {
            //nf = ct.ring.coFac.getONE();
        }
        GenPolynomial<BigInteger> PP = C, P = C;
        System.out.println("modlist  = " + mlist); // includes not ldcf
        // lift via Hensel
        ilist = PolyUfdUtil.liftHenselQuadratic(PP, M, mlist);
        if (logger.isInfoEnabled()) {
            logger.info("lifted intlist = " + ilist);
        }
        //System.out.println("intlist  = " + ilist); 

        GreatestCommonDivisorAbstract<BigInteger> iengine 
          = GCDFactory.<BigInteger> getImplementation(pfac.coFac);

        // combine trial factors
        int dl = (ilist.size() + 1) / 2;
        System.out.println("dl = " + dl); 
        GenPolynomial<BigInteger> u = PP;
        long deg = (u.degree(0) + 1L) / 2L;
        System.out.println("deg = " + deg); 
        //BigInteger ldcf = u.leadingBaseCoefficient();
        //System.out.println("ldcf = " + ldcf); 
        for (int j = 1; j <= dl; j++) {
            System.out.println("j = " + j + ", dl = " + dl + ", ilist = " + ilist); 
            KsubSet<GenPolynomial<BigInteger>> ps = new KsubSet<GenPolynomial<BigInteger>>(ilist, j);
            for (List<GenPolynomial<BigInteger>> flist : ps) {
                System.out.println("+flist = " + flist);
                GenPolynomial<BigInteger> trial = pfac.getONE();
                for (int kk = 0; kk < flist.size(); kk++) {
                    GenPolynomial<BigInteger> fk = flist.get(kk);
                    trial = trial.multiply(fk);
                }
                if (trial.degree(0) > deg) {
                    continue;
                }
                System.out.println("+trial    = " + trial);
                //System.out.println("+flist    = " + flist);
                //trial = iengine.basePrimitivePart( trial.multiply(ldcf) );
                trial = iengine.basePrimitivePart(trial);
                //System.out.println("pp(trial)= " + trial);
                if (PolyUtil.<BigInteger> basePseudoRemainder(u, trial).isZERO()) {
                    logger.info("trial    = " + trial);
                    //System.out.println("trial    = " + trial);
                    //System.out.println("flist    = " + flist);
                    //trial = iengine.basePrimitivePart(trial);
                    //System.out.println("pp(trial)= " + trial);
                    factors.add(trial);
                    u = PolyUtil.<BigInteger> basePseudoDivide(u, trial); //u.divide( trial );
                    //System.out.println("u        = " + u);
                    if (ilist.removeAll(flist)) {
                        System.out.println("new ilist= " + ilist);
                        dl = (ilist.size() + 1) / 2;
                        System.out.println("dl = " + dl); 
                        j = 0; // since j++
                        break;
                    } else {
                        logger.error("error removing flist from ilist = " + ilist);
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
            factors.add(PP);
        }
        return factors;
    }


    /**
     * Factor search with ModInteger Hensel lifting algorithm. Let p =
     * f_i.ring.coFac.modul() i = 0, ..., n-1 and assume C == prod_{0,...,n-1}
     * f_i mod p with ggt(f_i,f_j) == 1 mod p for i != j
     * @param C GenPolynomial<BigInteger>.
     * @param F = [f_0,...,f_{n-1}] List<GenPolynomial<ModInteger>>.
     * @param M bound on the coefficients of g_i as factors of C.
     * @return [g_0,...,g_{n-1}] = lift(C,F), with C = prod_{0,...,n-1} g_i mod
     *         p**e.
     */
    public static//<C extends RingElem<C>>
    List<GenPolynomial<BigInteger>> searchFactorsNonMonic(GenPolynomial<BigInteger> C, BigInteger M,
                                                          List<GenPolynomial<ModInteger>> F) {
        // System.out.println("*** non monic factor combination ***");
        if (C == null || C.isZERO() || F == null || F.size() == 0) {
            throw new RuntimeException("C must be nonzero and F must be nonempty");
        }
        GenPolynomialRing<BigInteger> pfac = C.ring;
        if (pfac.nvar != 1) { // todo assert
            throw new RuntimeException("polynomial ring not univariate");
        }
        List<GenPolynomial<BigInteger>> factors = new ArrayList<GenPolynomial<BigInteger>>(F.size());
        List<GenPolynomial<ModInteger>> mlist = F;
        GenPolynomial<BigInteger>[] ilist = null;

        ModInteger nf = null;
        GenPolynomial<ModInteger> ct = mlist.get(0);
        if (ct.isConstant()) {
            nf = ct.leadingBaseCoefficient();
            mlist.remove(ct);
            //System.out.println("=== nf   = " + nf);
            //System.out.println("=== ldcf = " + C.leadingBaseCoefficient());
            if (mlist.size() <= 1) {
                factors.add(C);
                return factors;
            }
        } else {
            nf = ct.ring.coFac.getONE();
        }
        //System.out.println("modlist  = " + mlist); // includes not ldcf
        GenPolynomialRing<ModInteger> mfac = ct.ring;
        GenPolynomial<ModInteger> Pm = PolyUtil.<ModInteger> fromIntegerCoefficients(mfac, C);
        GenPolynomial<BigInteger> PP = C, P = C;

        GreatestCommonDivisorAbstract<BigInteger> iengine 
           = GCDFactory.<BigInteger> getImplementation(pfac.coFac);

        // combine trial factors
        int dl = (mlist.size() + 1) / 2;
        GenPolynomial<BigInteger> u = PP;
        long deg = (u.degree(0) + 1L) / 2L;
        GenPolynomial<ModInteger> um = Pm;
        BigInteger ldcf = u.leadingBaseCoefficient();
        //System.out.println("ldcf = " + ldcf); 
        for (int j = 1; j <= dl; j++) {
            System.out.println("j = " + j + ", dl = " + dl + ", ilist = " + ilist); 
            KsubSet<GenPolynomial<ModInteger>> ps = new KsubSet<GenPolynomial<ModInteger>>(mlist, j);
            for (List<GenPolynomial<ModInteger>> flist : ps) {
                //System.out.println("flist = " + flist);
                GenPolynomial<ModInteger> trial = mfac.getONE().multiply(nf);
                for (int kk = 0; kk < flist.size(); kk++) {
                    GenPolynomial<ModInteger> fk = flist.get(kk);
                    trial = trial.multiply(fk);
                }
                if (trial.degree(0) > deg) {
                    continue;
                }
                GenPolynomial<ModInteger> cofactor = um.divide(trial);
                //System.out.println("trial    = " + trial);
                //System.out.println("cofactor = " + cofactor);

                // lift via Hensel
                try {
                    // ilist = PolyUfdUtil.liftHenselQuadraticFac(PP, M, trial, cofactor);
                    ilist = PolyUfdUtil.liftHenselQuadratic(PP, M, trial, cofactor);
                } catch (RuntimeException e) {
                    // no liftable factors
                    continue;
                }
                GenPolynomial<BigInteger> itrial = ilist[0];
                GenPolynomial<BigInteger> icofactor = ilist[1];
                if (logger.isDebugEnabled()) {
                    logger.info("lifted intlist = " + itrial + ", cofactor " + icofactor);
                }
                //System.out.println("lifted intlist = " + itrial + ", cofactor " + icofactor); 

                itrial = iengine.basePrimitivePart(itrial);
                //System.out.println("pp(trial)= " + itrial);
                if (PolyUtil.<BigInteger> basePseudoRemainder(u, itrial).isZERO()) {
                    logger.info("trial    = " + itrial);
                    //System.out.println("trial    = " + itrial);
                    //System.out.println("cofactor = " + icofactor);
                    //System.out.println("flist    = " + flist);
                    //itrial = iengine.basePrimitivePart(itrial);
                    //System.out.println("pp(itrial)= " + itrial);
                    factors.add(itrial);
                    //u = PolyUtil.<BigInteger> basePseudoDivide(u, itrial); //u.divide( trial );
                    u = icofactor;
                    um = cofactor;
                    //System.out.println("u        = " + u);
                    //System.out.println("um       = " + um);
                    if (mlist.removeAll(flist)) {
                        //System.out.println("new mlist= " + mlist);
                        dl = (mlist.size() + 1) / 2;
                        j = 0; // since j++
                        break;
                    } else {
                        logger.error("error removing flist from ilist = " + mlist);
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
            factors.add(PP);
        }
        return factors;
    }

}
