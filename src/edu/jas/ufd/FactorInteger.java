/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.ModLongRing;
import edu.jas.arith.Modular;
import edu.jas.arith.ModularRingFactory;
import edu.jas.arith.PrimeList;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.Power;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.util.KsubSet;


/**
 * Integer coefficients factorization algorithms. This class implements
 * factorization methods for polynomials over integers.
 * @author Heinz Kredel
 */

public class FactorInteger<MOD extends GcdRingElem<MOD> & Modular> extends FactorAbstract<BigInteger> {


    private static final Logger logger = Logger.getLogger(FactorInteger.class);


    private final boolean debug = true || logger.isDebugEnabled();


    /**
     * Factorization engine for modular base coefficients.
     */
    protected final FactorAbstract<MOD> mfactor;


    /**
     * Gcd engine for modular base coefficients.
     */
    protected final GreatestCommonDivisorAbstract<MOD> mengine;


    /**
     * No argument constructor.
     */
    public FactorInteger() {
        this(BigInteger.ONE);
    }


    /**
     * Constructor.
     * @param cfac coefficient ring factory.
     */
    public FactorInteger(RingFactory<BigInteger> cfac) {
        super(cfac);
        ModularRingFactory<MOD> mcofac = (ModularRingFactory<MOD>) (Object) new ModLongRing(13, true); // hack
        mfactor = FactorFactory.getImplementation(mcofac); //new FactorModular(mcofac);
        mengine = GCDFactory.getImplementation(mcofac);
        //mengine = GCDFactory.getProxy(mcofac);
    }


    /**
     * GenPolynomial base factorization of a squarefree polynomial.
     * @param P squarefree and primitive! GenPolynomial.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<GenPolynomial<BigInteger>> baseFactorsSquarefree(GenPolynomial<BigInteger> P) {
        if (P == null) {
            throw new IllegalArgumentException(this.getClass().getName() + " P == null");
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
            throw new IllegalArgumentException(this.getClass().getName() + " only for univariate polynomials");
        }
        if ( !engine.baseContent(P).isONE() ) {
            throw new IllegalArgumentException(this.getClass().getName() + " P not primitive");
        }
        if (P.degree(0) <= 1L) { // linear is irreducible
            factors.add(P);
            return factors;
        }
        // compute norm
        BigInteger an = P.maxNorm();
        BigInteger ac = P.leadingBaseCoefficient();
        //compute factor coefficient bounds
        ExpVector degv = P.degreeVector();
        int degi = (int) P.degree(0);
        BigInteger M = an.multiply(PolyUtil.factorBound(degv));
        M = M.multiply(ac.abs().multiply(ac.fromInteger(8)));
        //System.out.println("M = " + M);
        //M = M.multiply(M); // test

        //initialize prime list and degree vector
        PrimeList primes = new PrimeList(PrimeList.Range.small);
        int pn = 30; //primes.size();
        ModularRingFactory<MOD> cofac = null;
        GenPolynomial<MOD> am = null;
        GenPolynomialRing<MOD> mfac = null;
        final int TT = 5; // 7
        List<GenPolynomial<MOD>>[] modfac = new List[TT];
        List<GenPolynomial<BigInteger>>[] intfac = new List[TT];
        BigInteger[] plist = new BigInteger[TT];
        List<GenPolynomial<MOD>> mlist = null;
        List<GenPolynomial<BigInteger>> ilist = null;
        int i = 0;
        if (debug) {
            logger.debug("an  = " + an);
            logger.debug("ac  = " + ac);
            logger.debug("M   = " + M);
            logger.info("degv = " + degv);
        }
        Iterator<java.math.BigInteger> pit = primes.iterator();
        pit.next(); // skip p = 2
        pit.next(); // skip p = 3
        MOD nf = null;
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
                    throw new ArithmeticException("prime list exhausted");
                }
                if (ModLongRing.MAX_LONG.compareTo(p) > 0) {
                    cofac = (ModularRingFactory) new ModLongRing(p, true);
                } else {
                    cofac = (ModularRingFactory) new ModIntegerRing(p, true);
                }
                logger.info("prime = " + cofac);
                nf = cofac.fromInteger(ac.getVal());
                if (nf.isZERO()) {
                    logger.info("unlucky prime (nf) = " + p);
                    //System.out.println("unlucky prime (nf) = " + p);
                    continue;
                }
                // initialize polynomial factory and map polynomial
                mfac = new GenPolynomialRing<MOD>(cofac, pfac);
                am = PolyUtil.<MOD> fromIntegerCoefficients(mfac, P);
                if (!am.degreeVector().equals(degv)) { // allways true
                    logger.info("unlucky prime (deg) = " + p);
                    //System.out.println("unlucky prime (deg) = " + p);
                    continue;
                }
                GenPolynomial<MOD> ap = PolyUtil.<MOD> baseDeriviative(am);
                if (ap.isZERO()) {
                    logger.info("unlucky prime (a')= " + p);
                    //System.out.println("unlucky prime (a')= " + p);
                    continue;
                }
                GenPolynomial<MOD> g = mengine.baseGcd(am, ap);
                if (g.isONE()) {
                    logger.info("**lucky prime = " + p);
                    //System.out.println("**lucky prime = " + p);
                    break;
                }
            }
            // now am is squarefree mod p, make monic and factor mod p
            if (!nf.isONE()) {
                //System.out.println("nf = " + nf);
                am = am.divide(nf); // make monic
            }
            mlist = mfactor.baseFactorsSquarefree(am);
            if (logger.isInfoEnabled()) {
                logger.info("modlist  = " + mlist);
            }
            if (mlist.size() <= 1) {
                factors.add(P);
                return factors;
            }
            if (!nf.isONE()) {
                GenPolynomial<MOD> mp = mfac.getONE(); //mlist.get(0);
                //System.out.println("mp = " + mp);
                mp = mp.multiply(nf);
                //System.out.println("mp = " + mp);
                mlist.add(0, mp); // set(0,mp);
            }
            modfac[k] = mlist;
            plist[k] = cofac.getIntegerModul(); // p
        }

        // search shortest factor list
        int min = Integer.MAX_VALUE;
        BitSet AD = null;
        for (int k = 0; k < TT; k++) {
            List<ExpVector> ev = PolyUtil.<MOD> leadingExpVector(modfac[k]);
            BitSet D = factorDegrees(ev, degi);
            if (AD == null) {
                AD = D;
            } else {
                AD.and(D);
            }
            int s = modfac[k].size();
            logger.info("mod(" + plist[k] + ") #s = " + s + ", D = " + D /*+ ", lt = " + ev*/);
            //System.out.println("mod s = " + s);
            if (s < min) {
                min = s;
                mlist = modfac[k];
            }
        }
        logger.info("min = " + min + ", AD = " + AD);
        if (mlist.size() <= 1) {
            logger.info("mlist.size() = 1");
            factors.add(P);
            return factors;
        }
        if (AD.cardinality() <= 2) { // only one possible factor
            logger.info("degree set cardinality = " + AD.cardinality());
            factors.add(P);
            return factors;
        }

        boolean allLists = false; //true; //false;
        if (allLists) {
            // try each factor list
            for (int k = 0; k < TT; k++) {
                mlist = modfac[k];
                if (debug) {
                    logger.info("lifting from " + mlist);
                }
                if (false && P.leadingBaseCoefficient().isONE()) {
                    factors = searchFactorsMonic(P, M, mlist, AD); // does now work in all cases
                    if (factors.size() == 1) {
                        factors = searchFactorsNonMonic(P, M, mlist, AD);
                    }
                } else {
                    factors = searchFactorsNonMonic(P, M, mlist, AD);
                }
                intfac[k] = factors;
            }
        } else {
            // try only shortest factor list
            if (debug) {
                logger.info("lifting shortest from " + mlist);
            }
            if (true && P.leadingBaseCoefficient().isONE()) {
                long t = System.currentTimeMillis();
                try {
                    mlist = PolyUtil.<MOD> monic(mlist);
                    factors = searchFactorsMonic(P, M, mlist, AD); // does now work in all cases
                    t = System.currentTimeMillis() - t;
                    //System.out.println("monic time = " + t);
                    if (false && debug) {
                        t = System.currentTimeMillis();
                        List<GenPolynomial<BigInteger>> fnm = searchFactorsNonMonic(P, M, mlist, AD);
                        t = System.currentTimeMillis() - t;
                        System.out.println("non monic time = " + t);
                        if (debug) {
                            if (!factors.equals(fnm)) {
                                System.out.println("monic factors     = " + factors);
                                System.out.println("non monic factors = " + fnm);
                            }
                        }
                    }
                } catch (RuntimeException e) {
                    t = System.currentTimeMillis();
                    factors = searchFactorsNonMonic(P, M, mlist, AD);
                    t = System.currentTimeMillis() - t;
                    //System.out.println("only non monic time = " + t);
                }
            } else {
                long t = System.currentTimeMillis();
                factors = searchFactorsNonMonic(P, M, mlist, AD);
                t = System.currentTimeMillis() - t;
                //System.out.println("non monic time = " + t);
            }
            return factors;
        }

        // search longest factor list
        int max = 0;
        for (int k = 0; k < TT; k++) {
            int s = intfac[k].size();
            logger.info("int s = " + s);
            //System.out.println("int s = " + s);
            if (s > max) {
                max = s;
                ilist = intfac[k];
            }
        }
        factors = ilist;
        return factors;
    }


    /**
     * BitSet for factor degree list.
     * @param E exponent vector list.
     * @return b_0,...,b_k} a BitSet of possible factor degrees.
     */
    public BitSet factorDegrees(List<ExpVector> E, int deg) {
        BitSet D = new BitSet(deg + 1);
        D.set(0); // constant factor
        for (ExpVector e : E) {
            int i = (int) e.getVal(0);
            BitSet s = new BitSet(deg + 1);
            for (int k = 0; k < deg + 1 - i; k++) { // shift by i places
                s.set(i + k, D.get(k));
            }
            //System.out.println("s = " + s);
            D.or(s);
            //System.out.println("D = " + D);
        }
        return D;
    }


    /**
     * Sum of all degrees.
     * @param L univariate polynomial list.
     * @return sum deg(p) for p in L.
     */
    public static <C extends RingElem<C>> long degreeSum(List<GenPolynomial<C>> L) {
        long s = 0L;
        for (GenPolynomial<C> p : L) {
            ExpVector e = p.leadingExpVector();
            long d = e.getVal(0);
            s += d;
        }
        return s;
    }


    /**
     * Factor search with modular Hensel lifting algorithm. Let p =
     * f_i.ring.coFac.modul() i = 0, ..., n-1 and assume C == prod_{0,...,n-1}
     * f_i mod p with ggt(f_i,f_j) == 1 mod p for i != j
     * @param C GenPolynomial.
     * @param M bound on the coefficients of g_i as factors of C.
     * @param F = [f_0,...,f_{n-1}] List&lt;GenPolynomial&gt;.
     * @param D bit set of possible factor degrees.
     * @return [g_0,...,g_{n-1}] = lift(C,F), with C = prod_{0,...,n-1} g_i mod
     *         p**e. <b>Note:</b> does not work in all cases.
     */
    List<GenPolynomial<BigInteger>> searchFactorsMonic(GenPolynomial<BigInteger> C, BigInteger M,
                                                       List<GenPolynomial<MOD>> F, BitSet D) {
        //System.out.println("*** monic factor combination ***");
        if (C == null || C.isZERO() || F == null || F.size() == 0) {
            throw new IllegalArgumentException("C must be nonzero and F must be nonempty");
        }
        GenPolynomialRing<BigInteger> pfac = C.ring;
        if (pfac.nvar != 1) { // todo assert
            throw new IllegalArgumentException("polynomial ring not univariate");
        }
        List<GenPolynomial<BigInteger>> factors = new ArrayList<GenPolynomial<BigInteger>>(F.size());
        List<GenPolynomial<MOD>> mlist = F;
        List<GenPolynomial<MOD>> lift;

        //MOD nf = null;
        GenPolynomial<MOD> ct = mlist.get(0);
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
        //System.out.println("modlist  = " + mlist); // includes not ldcf
        ModularRingFactory<MOD> mcfac = (ModularRingFactory<MOD>) ct.ring.coFac;
        BigInteger m = mcfac.getIntegerModul();
        long k = 1;
        BigInteger pi = m;
        while (pi.compareTo(M) < 0) {
            k++;
            pi = pi.multiply(m);
        }
        logger.info("p^k = " + m + "^" + k);
        GenPolynomial<BigInteger> PP = C, P = C;
        // lift via Hensel
        try {
            lift = HenselUtil.<MOD> liftHenselMonic(PP, mlist, k);
            //System.out.println("lift = " + lift);
        } catch (NoLiftingException e) {
            throw new RuntimeException(e);
        }
        if (logger.isInfoEnabled()) {
            logger.info("lifted modlist = " + lift);
        }
        GenPolynomialRing<MOD> mpfac = lift.get(0).ring;

        // combine trial factors
        int dl = (lift.size() + 1) / 2;
        //System.out.println("dl = " + dl); 
        GenPolynomial<BigInteger> u = PP;
        long deg = (u.degree(0) + 1L) / 2L;
        //System.out.println("deg = " + deg); 
        //BigInteger ldcf = u.leadingBaseCoefficient();
        //System.out.println("ldcf = " + ldcf); 
        for (int j = 1; j <= dl; j++) {
            //System.out.println("j = " + j + ", dl = " + dl + ", lift = " + lift); 
            KsubSet<GenPolynomial<MOD>> ps = new KsubSet<GenPolynomial<MOD>>(lift, j);
            for (List<GenPolynomial<MOD>> flist : ps) {
                //System.out.println("degreeSum = " + degreeSum(flist));
                if (!D.get((int) FactorInteger.<MOD> degreeSum(flist))) {
                    logger.info("skipped by degree set " + D + ", deg = " + degreeSum(flist));
                    continue;
                }
                GenPolynomial<MOD> mtrial = Power.<GenPolynomial<MOD>> multiply(mpfac,flist);
                //GenPolynomial<MOD> mtrial = mpfac.getONE();
                //for (int kk = 0; kk < flist.size(); kk++) {
                //    GenPolynomial<MOD> fk = flist.get(kk);
                //    mtrial = mtrial.multiply(fk);
                //}
                //System.out.println("+flist = " + flist + ", mtrial = " + mtrial);
                if (mtrial.degree(0) > deg) { // this test is sometimes wrong
                    logger.info("degree " +  mtrial.degree(0) + " > deg " + deg);
                    //continue;
                }
                //System.out.println("+flist    = " + flist);
                GenPolynomial<BigInteger> trial = PolyUtil.integerFromModularCoefficients(pfac, mtrial);
                //System.out.println("+trial = " + trial);
                //trial = engine.basePrimitivePart( trial.multiply(ldcf) );
                trial = engine.basePrimitivePart(trial);
                //System.out.println("pp(trial)= " + trial);
                if (PolyUtil.<BigInteger> basePseudoRemainder(u, trial).isZERO()) {
                    logger.info("successful trial = " + trial);
                    //System.out.println("trial    = " + trial);
                    //System.out.println("flist    = " + flist);
                    //trial = engine.basePrimitivePart(trial);
                    //System.out.println("pp(trial)= " + trial);
                    factors.add(trial);
                    u = PolyUtil.<BigInteger> basePseudoDivide(u, trial); //u.divide( trial );
                    //System.out.println("u        = " + u);
                    //if (lift.removeAll(flist)) {
                    lift = removeOnce(lift,flist);
                    logger.info("new lift= " + lift);
                    dl = (lift.size() + 1) / 2;
                    //System.out.println("dl = " + dl); 
                    j = 0; // since j++
                    break;
                    //} logger.error("error removing flist from lift = " + lift);
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
     * Factor search with modular Hensel lifting algorithm. Let p =
     * f_i.ring.coFac.modul() i = 0, ..., n-1 and assume C == prod_{0,...,n-1}
     * f_i mod p with ggt(f_i,f_j) == 1 mod p for i != j
     * @param C GenPolynomial.
     * @param M bound on the coefficients of g_i as factors of C.
     * @param F = [f_0,...,f_{n-1}] List&lt;GenPolynomial&gt;.
     * @param D bit set of possible factor degrees.
     * @return [g_0,...,g_{n-1}] = lift(C,F), with C = prod_{0,...,n-1} g_i mod
     *         p**e.
     */
    List<GenPolynomial<BigInteger>> searchFactorsNonMonic(GenPolynomial<BigInteger> C, BigInteger M,
                                                          List<GenPolynomial<MOD>> F, BitSet D) {
        //System.out.println("*** non monic factor combination ***");
        if (C == null || C.isZERO() || F == null || F.size() == 0) {
            throw new IllegalArgumentException("C must be nonzero and F must be nonempty");
        }
        GenPolynomialRing<BigInteger> pfac = C.ring;
        if (pfac.nvar != 1) { // todo assert
            throw new IllegalArgumentException("polynomial ring not univariate");
        }
        List<GenPolynomial<BigInteger>> factors = new ArrayList<GenPolynomial<BigInteger>>(F.size());
        List<GenPolynomial<MOD>> mlist = F;

        MOD nf = null;
        GenPolynomial<MOD> ct = mlist.get(0);
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
        GenPolynomialRing<MOD> mfac = ct.ring;
        GenPolynomial<MOD> Pm = PolyUtil.<MOD> fromIntegerCoefficients(mfac, C);
        GenPolynomial<BigInteger> PP = C, P = C;

        // combine trial factors
        int dl = (mlist.size() + 1) / 2;
        GenPolynomial<BigInteger> u = PP;
        long deg = (u.degree(0) + 1L) / 2L;
        GenPolynomial<MOD> um = Pm;
        //BigInteger ldcf = u.leadingBaseCoefficient();
        //System.out.println("ldcf = " + ldcf); 
        HenselApprox<MOD> ilist = null;
        for (int j = 1; j <= dl; j++) {
            //System.out.println("j = " + j + ", dl = " + dl + ", ilist = " + ilist); 
            KsubSet<GenPolynomial<MOD>> ps = new KsubSet<GenPolynomial<MOD>>(mlist, j);
            for (List<GenPolynomial<MOD>> flist : ps) {
                //System.out.println("degreeSum = " + degreeSum(flist));
                if (!D.get((int) FactorInteger.<MOD> degreeSum(flist))) {
                    logger.info("skipped by degree set " + D + ", deg = " + degreeSum(flist));
                    continue;
                }
                GenPolynomial<MOD> trial = mfac.getONE().multiply(nf);
                for (int kk = 0; kk < flist.size(); kk++) {
                    GenPolynomial<MOD> fk = flist.get(kk);
                    trial = trial.multiply(fk);
                }
                if (trial.degree(0) > deg) { // this test is sometimes wrong
                    logger.info("degree > deg " + deg + ", degree = " + trial.degree(0));
                    //continue;
                }
                GenPolynomial<MOD> cofactor = um.divide(trial);
                //System.out.println("trial    = " + trial);
                //System.out.println("cofactor = " + cofactor);

                // lift via Hensel
                try {
                    // ilist = HenselUtil.liftHenselQuadraticFac(PP, M, trial, cofactor);
                    ilist = HenselUtil.<MOD> liftHenselQuadratic(PP, M, trial, cofactor);
                    //ilist = HenselUtil.<MOD> liftHensel(PP, M, trial, cofactor);
                } catch (NoLiftingException e) {
                    // no liftable factors
                    if ( /*debug*/logger.isDebugEnabled()) {
                        logger.info("no liftable factors " + e);
                        e.printStackTrace();
                    }
                    continue;
                }
                GenPolynomial<BigInteger> itrial = ilist.A;
                GenPolynomial<BigInteger> icofactor = ilist.B;
                if (logger.isDebugEnabled()) {
                    logger.info("       modlist = " + trial + ", cofactor " + cofactor);
                    logger.info("lifted intlist = " + itrial + ", cofactor " + icofactor);
                }
                //System.out.println("lifted intlist = " + itrial + ", cofactor " + icofactor); 

                itrial = engine.basePrimitivePart(itrial);
                //System.out.println("pp(trial)= " + itrial);
                if (PolyUtil.<BigInteger> basePseudoRemainder(u, itrial).isZERO()) {
                    logger.info("successful trial = " + itrial);
                    //System.out.println("trial    = " + itrial);
                    //System.out.println("cofactor = " + icofactor);
                    //System.out.println("flist    = " + flist);
                    //itrial = engine.basePrimitivePart(itrial);
                    //System.out.println("pp(itrial)= " + itrial);
                    factors.add(itrial);
                    //u = PolyUtil.<BigInteger> basePseudoDivide(u, itrial); //u.divide( trial );
                    u = icofactor;
                    PP = u; // fixed finally on 2009-05-03
                    um = cofactor;
                    //System.out.println("u        = " + u);
                    //System.out.println("um       = " + um);
                    //if (mlist.removeAll(flist)) {
                    mlist = removeOnce(mlist,flist);
                    logger.info("new mlist= " + mlist);
                    dl = (mlist.size() + 1) / 2;
                    j = 0; // since j++
                    break;
                    //} logger.error("error removing flist from ilist = " + mlist);
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
     * GenPolynomial factorization of a multivariate squarefree polynomial, using Hensel lifting if possible.
     * @param P squarefree and primitive! (respectively monic) multivariate GenPolynomial over the integers.
     * @return [p_1,...,p_k] with P = prod_{i=1,...,r} p_i.
     */
    public List<GenPolynomial<BigInteger>> factorsSquarefree(GenPolynomial<BigInteger> P) {
        ExpVector degv = P.degreeVector();
        int[] donv = degv.dependencyOnVariables();
        List<GenPolynomial<BigInteger>> facs = null;
        if ( degv.length() == donv.length ) { // all variables appear, hack for Hensel
            try {
                logger.info("try factorsSquarefreeHensel: " + P);
                facs = factorsSquarefreeHensel(P);
            } catch (Exception e) {
                //System.out.println("exception " + e);
                logger.warn("exception " + e);
            }
        }
        if ( facs == null ) {
            logger.info("factorsSquarefreeHensel failed or not applicable, reverting to Kronecker: " + P);
            facs = super.factorsSquarefree(P);
        }
        return facs;
   }


    /**
     * GenPolynomial factorization of a multivariate squarefree polynomial, using Hensel lifting.
     * @param P squarefree and primitive! (respectively monic) multivariate GenPolynomial over the integers.
     * @return [p_1,...,p_k] with P = prod_{i=1,...,r} p_i.
     */
    public List<GenPolynomial<BigInteger>> factorsSquarefreeHensel(GenPolynomial<BigInteger> P) {
        if (P == null) {
            throw new IllegalArgumentException(this.getClass().getName() + " P != null");
        }
        GenPolynomialRing<BigInteger> pfac = P.ring;
        if (pfac.nvar == 1) {
            return baseFactorsSquarefree(P);
        }
        List<GenPolynomial<BigInteger>> factors = new ArrayList<GenPolynomial<BigInteger>>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.degreeVector().totalDeg() <= 1L) {
            factors.add(P);
            return factors;
        }
        GenPolynomial<BigInteger> pd = P; 
        //System.out.println("pfac = " + pfac.toScript());
        //System.out.println("pd   = " + pd);
        // ldcf(pd)
        BigInteger ac = pd.leadingBaseCoefficient();

        // factor leading coefficient as polynomial in the lowest variable
        GenPolynomialRing<GenPolynomial<BigInteger>> rnfac = pfac.recursive(pfac.nvar-1);
        GenPolynomial<GenPolynomial<BigInteger>> pr = PolyUtil.<BigInteger>recursive(rnfac,pd);
        GenPolynomial<GenPolynomial<BigInteger>> prr = PolyUtil.<BigInteger>switchVariables(pr);
        GenPolynomial<BigInteger> lprr = prr.leadingBaseCoefficient();
        //System.out.println("prr  = " + prr);
        logger.info("leading coeffcient = " + lprr);
        boolean isMonic = false;
        if ( lprr.isConstant() ) { // isONE ?
            isMonic = true;
        }
        SortedMap<GenPolynomial<BigInteger>,Long> lfactors = factors(lprr);
        //System.out.println("lfactors = " + lfactors);
        List<GenPolynomial<BigInteger>> lfacs = new ArrayList<GenPolynomial<BigInteger>>(lfactors.keySet());
        logger.info("leading coefficient factors = " + lfacs);

        // search evaluation point and evaluate
        GenPolynomialRing<BigInteger> cpfac = pfac;
        GenPolynomial<BigInteger> pe = pd;
        GenPolynomial<BigInteger> pep;
        GenPolynomialRing<BigInteger> ccpfac = lprr.ring;
        List<GenPolynomial<BigInteger>> ce = lfacs;
        List<GenPolynomial<BigInteger>> cep = null;
        List<BigInteger> cei = null;
        List<BigInteger> dei = new ArrayList<BigInteger>();
        BigInteger pec = null;
        BigInteger pecw = null;
        BigInteger ped = null;

        List<GenPolynomial<BigInteger>> ufactors = null;
        List<TrialParts> tParts = new ArrayList<TrialParts>();
        final int trials = 3;
        List<GenPolynomial<BigInteger>> lf = null;
        GenPolynomial<BigInteger> lpx = null;
        List<GenPolynomial<BigInteger>> ln = null;
        List<GenPolynomial<BigInteger>> un = null;
        GenPolynomial<BigInteger> pes = null;

        List<BigInteger> V = null;
        long evStart = 0L; //3L * 5L;
        List<Long> Evs = new ArrayList<Long>(pfac.nvar+1); // Evs(0), Evs(1) unused
        for ( int j = 0; j <= pfac.nvar; j++ ) {
            Evs.add(evStart);
        }
        double ran = 1.1;
        boolean isPrimitive = true;
        boolean notLucky = true;
        while ( notLucky ) { // for Wang's test
            if ( evStart < -400L || evStart > 400L ) {
                System.out.println("P = " + P + ",lprr = " + lprr + ", lfacs = " + lfacs);
                throw new RuntimeException("no lucky evaluation point found after " + evStart + " iterations");
            }
            if ( Math.abs(evStart) % 100L <= 3L ) {
                ran = ran * (Math.PI-2.0);
            }
            //System.out.println("-------------------------------------------- Evs = " + Evs);
            notLucky = false;
            V = new ArrayList<BigInteger>();
            cpfac = pfac;
            pe = pd;
            ccpfac = lprr.ring;
            ce = lfacs;
            cep = null;
            cei = null;
            pec = null;
            ped = null;
            long vi = 0L; 
            for ( int j = pfac.nvar; j > 1; j-- ) {
                // evaluation up to univariate case
                long degp = pe.degree(cpfac.nvar-2);
                cpfac = cpfac.contract(1);
                ccpfac = ccpfac.contract(1);
                //vi = evStart; // + j;//0L; //(long)(pfac.nvar-j); // 1L; 0 not so good for small p
                vi = Evs.get(j); //evStart + j;//0L; //(long)(pfac.nvar-j); // 1L; 0 not so good for small p
                BigInteger Vi;

                // search evaluation point
                boolean doIt = true;
                Vi = null;
                pep = null;
                while( doIt ) { 
                    logger.info("vi(" + j + ") = " + vi);
                    Vi = new BigInteger(vi);
                    pep = PolyUtil.<BigInteger> evaluateMain(cpfac,pe,Vi);
                    //System.out.println("pep = " + pep);

                    // check lucky evaluation point 
                    if (degp == pep.degree(cpfac.nvar-1)) {
                        logger.info("pep = " + pep);
                        //System.out.println("deg(pe) = " + degp + ", deg(pep) = " + pep.degree(cpfac.nvar-1));
                        // check squarefree
                        if ( sengine.isSquarefree(pep) ) { // cpfac.nvar > 1 ??
                            //if ( isNearlySquarefree(pep) ) {
                            //System.out.println("squarefeee(pep)"); // + pep);
                            doIt = false;
                            //break;
                        }
                    }
                    if ( vi > 0L ) {
                        vi = -vi;
                    } else {
                        vi = 1L - vi;
                    }
                }
                if ( !isMonic ) {
                    if ( ccpfac.nvar >= 1 ) {
                        cep = PolyUtil.<BigInteger> evaluateMain(ccpfac,ce,Vi);
                    } else {
                        cei = PolyUtil.<BigInteger> evaluateMain(ccpfac.coFac,ce,Vi);
                    }
                }
                int jj = (int)Math.round( ran + j * Math.random() ); // random increment
                //System.out.println("jj = " + jj + ", vi " + vi);
                if ( vi > 0L ) {
                    Evs.set(j,vi+jj); //1L); // record last tested value plus increment
                    evStart = vi+jj;  //1L;
                } else {
                    Evs.set(j,vi-jj); //1L); // record last tested value minus increment
                    evStart = vi-jj;  //1L;
                }
                //evStart = vi+1L;
                V.add(Vi);
                pe = pep;
                ce = cep;
            }
            //System.out.println("ce = " + ce + ", pe = " + pe);
            if ( !isMonic ) {
                pecw = engine.baseContent(pe); // original Wang
                //System.out.println("cei = " + cei + ", pecw = " + pecw);
                //if ( !testSeparate(cei,pecw) ) { // extension of Wang
                //    logger.info("not separate: " + "cei = " + cei + ", pecw = " + pecw);
                //    if ( evStart > -20L && evStart < 20L ) {
                //        notLucky = true;
                //    }
                //} 
                pec = pe.ring.coFac.getONE(); // hack is better
                //pec = pecw;
                //System.out.println("cei = " + cei + ", pec = " + pec + ", pe = " + pe);
                if ( lfacs.get(0).isConstant() ) {
                    ped = cei.remove(0);
                    //lfacs.remove(0); // later
                } else {
                    ped = cei.get(0).getONE();
                }
                //System.out.println("lfacs = " + lfacs + ", cei = " + cei + ", ped = " + ped + ", pecw = " + pecw);
                // test Wang's condition
                dei = new ArrayList<BigInteger>();
                dei.add( pec.multiply(ped).abs() ); // .abs()
                int i = 1;
                for ( BigInteger ci : cei ) {
                    if ( ci.isZERO() ) {
                        logger.info("condition (0) not met for cei = " + cei); // + ", dei = " + dei);
                        notLucky = true;
                        break;
                    } 
                    BigInteger cii = ci.abs();
                    for ( int ii = i-1; ii >= 0; ii-- ) {
                        BigInteger r = dei.get(ii);
                        while ( !r.isONE() ) { 
                            r = r.gcd(cii);
                            cii = cii.divide(r); 
                        }
                    }
                    if ( cii.isONE() ) {
                        logger.info("condition (1) not met for dei = " + dei + ", cei = " + cei);
                        notLucky = true;
                        break;
                    }
                    dei.add(cii);
                    i++;
                }
                //System.out.println("dei = " + dei);
            }
            if ( notLucky ) {
                continue;
            }
            logger.info("evaluation points  = " + V + ", dei = " + dei);
            //System.out.println("Evs = " + Evs);
            logger.info("univariate polynomial = " + pe);
            //pe = pe.abs();
            //ufactors = baseFactorsRadical(pe); //baseFactorsSquarefree(pe); wrong since not primitive
            ufactors = baseFactorsSquarefree( pe.divide(pecw) ); //wrong if not primitive
            if ( !pecw.isONE() ) {
                ufactors.add(0,cpfac.getONE().multiply(pecw));
            }
            if (ufactors.size() <= 1) {
                factors.add(P);
                return factors;
            }
            logger.info("univariate factors = " + ufactors); // + ", of " + pe);
            //System.out.println("lfacs    = " + lfacs);
            //System.out.println("cei      = " + cei);
            //System.out.println("pecw     = " + pecw);

            // determine leading coefficient polynomials for factors
            lf = new ArrayList<GenPolynomial<BigInteger>>();
            lpx = lprr.ring.getONE();
            for ( GenPolynomial<BigInteger> pp : ufactors) {
                lf.add( lprr.ring.getONE() ); 
            }
            //System.out.println("lf = " + lf);             
            if ( !isMonic ) {
                if ( lfacs.get(0).isConstant() ) {
                    GenPolynomial<BigInteger> xx = lfacs.remove(0);
                    BigInteger xxi = xx.leadingBaseCoefficient();
                    System.out.println("xx = " + xx + " == ped = " +ped);
                }
                for ( int i = ufactors.size()-1; i >= 0; i-- ) {
                    GenPolynomial<BigInteger> pp = ufactors.get(i);
                    BigInteger ppl = pp.leadingBaseCoefficient();
                    //System.out.println("ppl = " + ppl + ", pp = " + pp);
                    ppl = ppl.multiply(pec); // content
                    GenPolynomial<BigInteger> lfp = lf.get(i);
                    int ii = 0;
                    for ( BigInteger ci : cei ) {
                        //System.out.println("ci = " + ci + ", lfp = " + lfp + ", lfacs.get(ii) = " + lfacs.get(ii));
                        if ( ci.abs().isONE() ) {
                            System.out.println("ppl = " + ppl + ", ci = " + ci + ", lfp = " + lfp + ", lfacs.get(ii) = " + lfacs.get(ii));
                            throw new RuntimeException("something is wrong, ci is a unit");
                            //notLucky = true;
                        }
                        while ( ppl.remainder(ci).isZERO() ) {
                            ppl = ppl.divide(ci);
                            lfp = lfp.multiply( lfacs.get(ii) );
                        }
                        ii++;
                    }
                    //System.out.println("ppl = " + ppl + ", lfp = " + lfp);
                    lfp = lfp.multiply(ppl);
                    lf.set(i,lfp);
                }
                // adjust if pec != 1
                pec = pecw;
                if ( !pec.isONE() ) { // content, always false by hack
                    lpx = Power.<GenPolynomial<BigInteger>> multiply(lprr.ring,lf);
                    System.out.println("lpx = " + lpx);
                    // evaluate factors of ldcf
                    List<GenPolynomial<BigInteger>> lfe = lf;
                    List<BigInteger> lfei = null;
                    ccpfac = lprr.ring;
                    for ( int j = lprr.ring.nvar; j > 0; j-- ) {
                        ccpfac = ccpfac.contract(1);
                        BigInteger Vi = V.get(lprr.ring.nvar-j);
                        if ( ccpfac.nvar >= 1 ) {
                            lfe = PolyUtil.<BigInteger> evaluateMain(ccpfac,lfe,Vi);
                        } else {
                            lfei = PolyUtil.<BigInteger> evaluateMain(ccpfac.coFac,lfe,Vi);
                        }
                    }
                    System.out.println("lfe = " + lfe + ", lfei = " + lfei + ", V = " + V);

                    ln = new ArrayList<GenPolynomial<BigInteger>>(lf.size());
                    un = new ArrayList<GenPolynomial<BigInteger>>(lf.size());
                    for ( int jj = 0; jj < lf.size(); jj++ ) {
                        GenPolynomial<BigInteger> up = ufactors.get(jj);
                        BigInteger ui = up.leadingBaseCoefficient();
                        BigInteger li = lfei.get(jj);
                        BigInteger di = ui.gcd(li);
                        System.out.println("ui  = " + ui + ", li  = " + li + ", di = " + di);
                        
                        BigInteger udi = ui.divide(di);
                        BigInteger ldi = li.divide(di);
                        System.out.println("udi = " + udi + ", ldi = " + ldi);

                        GenPolynomial<BigInteger> lp = lf.get(jj);
                        GenPolynomial<BigInteger> lpd = lp.multiply(udi);
                        System.out.println("lp  = " + lp + ", lpd = " + lpd + ", udi = " + udi);

                        GenPolynomial<BigInteger> upd = up.multiply(ldi);
                        System.out.println("up  = " + up + ", upd = " + upd + ", ldi = " + ldi);
                        if ( pec.isONE() ) {
                            ln.add(lp);
                            un.add(up);
                        } else {
                            ln.add(lpd);
                            un.add(upd);
                            BigInteger pec1 = pec.divide(ldi);
                            System.out.println("pec = " + pec + ", pec1 = " + pec1);
                            pec = pec1;
                        }
                    }
                    System.out.println("pe  = " + pe);
                    System.out.println("ln  = " + ln + ", lf = " + lf);
                    System.out.println("un  = " + un + ", ufactors = " + ufactors);
                    //lf = ln;
                    //ufactors = un;
                    if ( !pec.isONE() ) { // still not 1
                        ln = new ArrayList<GenPolynomial<BigInteger>>(lf.size());
                        un = new ArrayList<GenPolynomial<BigInteger>>(lf.size());
                        pes = pe;
                        for ( int jj = 0; jj < lf.size(); jj++ ) {
                            GenPolynomial<BigInteger> up = ufactors.get(jj);
                            GenPolynomial<BigInteger> lp = lf.get(jj);
                            System.out.println("up  = " + up + ", lp  = " + lp);
                            if ( !up.isConstant() ) {
                                up = up.multiply(pec);
                            }
                            lp = lp.multiply(pec);
                            if ( jj != 0 ) {
                                pes = pes.multiply(pec);
                            }
                            un.add(up);
                            ln.add(lp);
                        }
                        System.out.println("*pe  = " + pes + ", pec = " + pec);
                        System.out.println("*ln  = " + ln + ", *lf = " + lf);
                        System.out.println("*un  = " + un + ", *ufactors = " + ufactors);
                        if ( pes.equals( Power.<GenPolynomial<BigInteger>> multiply(pe.ring,un) ) ) {
                            System.out.println("*pe == prod(un) ");
                            isPrimitive = false;
                            //pe = pes;
                            //lf = ln;
                            //ufactors = un;
                        } else {
                            System.out.println("*pe != prod(un): " + Power.<GenPolynomial<BigInteger>> multiply(pe.ring,un));
                        }
                    }
                }

                if ( notLucky ) {
                    continue;
                }
                logger.info("distributed factors of leading coefficient = " + lf);
                lpx = Power.<GenPolynomial<BigInteger>> multiply(lprr.ring,lf);
                if ( !lprr.abs().equals(lpx.abs()) ) { // something is wrong
                    //System.out.println("ufactors = " + ufactors + ", of " + pe +  ", is factorizatio: " + isFactorization(pe,ufactors));
                    if ( !lprr.degreeVector().equals(lpx.degreeVector()) ) {
                        logger.info("lprr != lpx: lprr = " + lprr + ", lpx = " + lpx);
                        //throw new RuntimeException("something is wrong: " + "lprr = " + lprr + ", lpx = " + lpx);
                        notLucky = true; 
                    }
                }
            } // end determine leading coefficients for factors

            if ( ! notLucky ) {
                TrialParts tp = null;
                if ( isPrimitive ) {
                    tp = new TrialParts(V,pe,ufactors,cei,lf);
                } else {
                    tp = new TrialParts(V,pes,un,cei,ln);
                }
                //System.out.println("trialParts = " + tp);
                tParts.add(tp);
                if (tParts.size() < trials) {
                    notLucky = true;
                }
            }
        } // end notLucky loop

        // search TrialParts with shortest factorization of univariate polynomial
        int min = Integer.MAX_VALUE;
        TrialParts tpmin = null;
        for ( TrialParts tp : tParts ) {
            logger.info("tp.univFactors.size() = " + tp.univFactors.size());
            if ( tp.univFactors.size() < min ) {
                min = tp.univFactors.size();
                tpmin = tp;
            }
        }
        // set to shortest 
        V = tpmin.evalPoints;
        pe = tpmin.univPoly;
        ufactors = tpmin.univFactors;
        cei = tpmin.ldcfEval; // unused
        lf = tpmin.ldcfFactors;
        logger.info("minimal trial = " + tpmin);

        GenPolynomialRing<BigInteger> ufac = pe.ring;
        //System.out.println("ufac = " + ufac.toScript());

        //initialize prime list
        PrimeList primes = new PrimeList(PrimeList.Range.medium); // PrimeList.Range.medium);
        Iterator<java.math.BigInteger> primeIter = primes.iterator();
        int pn = 50; //primes.size();
        BigInteger ae = pe.leadingBaseCoefficient();
        GenPolynomial<MOD> Pm = null;
        ModularRingFactory<MOD> cofac = null;
        GenPolynomialRing<MOD> mufac = null;

        // search lucky prime
        for ( int i = 0; i < 11; i++ ) { // meta loop
            //for ( int i = 0; i < 1; i++ ) { // meta loop
            java.math.BigInteger p = null; //new java.math.BigInteger("19"); //primes.next();
            // 2 small, 5 medium and 4 large size primes
            if ( i == 0 ) { // medium size
                primes = new PrimeList(PrimeList.Range.medium);
                primeIter = primes.iterator();
            }
            if ( i == 5 ) { // small size
                primes = new PrimeList(PrimeList.Range.small);
                primeIter = primes.iterator();
                p = primeIter.next(); // 2
                p = primeIter.next(); // 3
                p = primeIter.next(); // 5
                p = primeIter.next(); // 7
            }
            if ( i == 7 ) { // large size
                primes = new PrimeList(PrimeList.Range.large);
                primeIter = primes.iterator();
            }
            int pi = 0;
            while ( pi < pn && primeIter.hasNext() ) {
                p = primeIter.next();
                //p = new java.math.BigInteger("19"); // test
                logger.info("prime = " + p);
                // initialize coefficient factory and map normalization factor and polynomials
                ModularRingFactory<MOD> cf = null;
                if (ModLongRing.MAX_LONG.compareTo(p) > 0) {
                    cf = (ModularRingFactory) new ModLongRing(p, true);
                } else {
                    cf = (ModularRingFactory) new ModIntegerRing(p, true);
                }
                MOD nf = cf.fromInteger(ae.getVal());
                if (nf.isZERO()) {
                    continue;
                }
                mufac = new GenPolynomialRing<MOD>(cf, ufac);
                //System.out.println("mufac = " + mufac.toScript());
                Pm = PolyUtil.<MOD> fromIntegerCoefficients(mufac, pe);
                //System.out.println("Pm = " + Pm);
                if ( ! mfactor.isSquarefree(Pm) ) {
                    continue;
                }
                cofac = cf;
                break;
            }
            if ( cofac != null ) { 
                break;
            }
        } // end meta loop
        if ( cofac == null ) { // no lucky prime found
            throw new RuntimeException("giving up on Hensel preparation");
        }
        logger.info("lucky prime = " + cofac.getIntegerModul());
        logger.debug("univariate modulo p: = " + Pm);

        // coefficient bound
        BigInteger an = pd.maxNorm();
        BigInteger mn = an.multiply(ac.abs()).multiply(new BigInteger(2L));
        long k = Power.logarithm(cofac.getIntegerModul(),mn) + 1L;
        //System.out.println("mn = " + mn + ", k = " +k);

        BigInteger q = Power.positivePower(cofac.getIntegerModul(), k);
        ModularRingFactory<MOD> muqfac;
        if (ModLongRing.MAX_LONG.compareTo(q.getVal()) > 0) {
            muqfac = (ModularRingFactory) new ModLongRing(q.getVal());
        } else {
            muqfac = (ModularRingFactory) new ModIntegerRing(q.getVal());
        }
        //System.out.println("muqfac = " + muqfac);
        GenPolynomialRing<MOD> mucpfac = new GenPolynomialRing<MOD>(muqfac, ufac);

        List<GenPolynomial<MOD>> muqfactors = PolyUtil.<MOD> fromIntegerCoefficients(mucpfac, ufactors);
        GenPolynomial<MOD> peqq = PolyUtil.<MOD> fromIntegerCoefficients(mucpfac, pe);
        if ( debug ) {
            if ( !mfactor.isFactorization(peqq,muqfactors) ) { // should not happen
                System.out.println("muqfactors = " + muqfactors);
                System.out.println("peqq       = " + peqq);
                throw new RuntimeException("something is wrong, no modular p^k factorization");
            }
        }
        logger.info("univariate modulo p^k: " + peqq + " = " + muqfactors);

        // convert C from Z[...] to Z_q[...]
        GenPolynomialRing<MOD> qcfac = new GenPolynomialRing<MOD>(muqfac, pd.ring);
        GenPolynomial<MOD> pq = PolyUtil.<MOD> fromIntegerCoefficients(qcfac, pd);
        //System.out.println("pd = " + pd);
        logger.info("multivariate modulo p^k: " + pq);

        List<MOD> Vm = new ArrayList<MOD>(V.size());
        for ( BigInteger v : V ) {
            MOD vm = muqfac.fromInteger(v.getVal());
            Vm.add(vm);
        }
        //System.out.println("Vm = " + Vm);

        // Hensel lifting of factors
        List<GenPolynomial<MOD>> mlift; 
        try {
            mlift = HenselMultUtil.<MOD> liftHensel(pd,pq,muqfactors,Vm,k,lf);
            logger.info("mlift = " + mlift);
        } catch ( NoLiftingException nle ) {
            System.out.println("exception : " + nle);
            //continue;
            mlift = new ArrayList<GenPolynomial<MOD>>();
        } catch ( ArithmeticException aex ) {
            System.out.println("exception : " + aex);
            //continue;  
            mlift = new ArrayList<GenPolynomial<MOD>>();
        }
        if ( mlift.size() <= 1 ) { // irreducible mod I, p^k, can this happen?
            factors.add(P);
            return factors;
        }

        // combine trial factors
        GenPolynomialRing<MOD> mfac = mlift.get(0).ring;
        int dl = (mlift.size() + 1) / 2;
        GenPolynomial<BigInteger> u = P;
        long deg = (u.degree() + 1L) / 2L;

        GenPolynomial<MOD> um = PolyUtil.<MOD> fromIntegerCoefficients(mfac, P);
        GenPolynomial<BigInteger> ui = pd;
        for (int j = 1; j <= dl; j++) {
            //System.out.println("j = " + j + ", dl = " + dl + ", mlift = " + mlift); 
            KsubSet<GenPolynomial<MOD>> subs = new KsubSet<GenPolynomial<MOD>>(mlift, j);
            for (List<GenPolynomial<MOD>> flist : subs) {
                //System.out.println("degreeSum = " + degreeSum(flist));
                GenPolynomial<MOD> mtrial = Power.<GenPolynomial<MOD>> multiply(mfac,flist);
                //GenPolynomial<MOD> mtrial = mfac.getONE(); // .multiply(nf); // == 1, since primitive
                //for (int kk = 0; kk < flist.size(); kk++) {
                //    GenPolynomial<MOD> fk = flist.get(kk);
                //    mtrial = mtrial.multiply(fk);
                //}
                if (mtrial.degree() > deg) { // this test is sometimes wrong
                    logger.info("degree > deg " + deg + ", degree = " + mtrial.degree());
                    //continue;
                }
                GenPolynomial<MOD> cofactor = um.divide(mtrial);
                GenPolynomial<BigInteger> trial = PolyUtil.integerFromModularCoefficients(pfac, mtrial);
                GenPolynomial<BigInteger> cotrial = PolyUtil.integerFromModularCoefficients(pfac, cofactor);
                if ( ! isPrimitive ) {
                    System.out.println("*trial   = " + trial + ", *cotrial = " + cotrial); 
                    trial = engine.basePrimitivePart(trial);
                    cotrial = engine.basePrimitivePart(cotrial);
                }
                System.out.println("trial    = " + trial + ", mtrial = " + mtrial);
                System.out.println("cotrial  = " + cotrial + ", cofactor = " + cofactor);
                System.out.println("trial*cotrial = " + trial.multiply(cotrial) + ", ui = " + ui);
                if ( PolyUtil.<BigInteger> basePseudoRemainder(ui, trial).isZERO() ) {
                    //if (trial.multiply(cotrial).equals(ui) ) {
                    logger.info("successful trial = " + trial);
                    factors.add(trial);
                    //ui = cotrial; //PolyUtil.<BigInteger> basePseudoDivide(ui, trial); //u.divide( trial );
                    ui = PolyUtil.<BigInteger> basePseudoDivide(ui, trial);
                    um = cofactor;
                    //System.out.println("ui        = " + ui);
                    //System.out.println("um        = " + um);
                    mlift = removeOnce(mlift,flist);
                    logger.info("new mlift= " + mlift);
                    //System.out.println("dl = " + dl); 
                    if ( mlift.size() > 1 ) {
                        dl = (mlift.size() + 1) / 2;
                        j = 0; // since j++
                        break;
                    } else {
                        logger.info("last ui = " + ui);
                        factors.add(ui);
                        return factors;
                    }
                }
            }
        }
        //System.out.println("end combine, factors = " + factors);
        if (!ui.isONE() && !ui.equals(pd)) {
            logger.info("rest ui = " + ui);
            //System.out.println("rest ui = " + ui);
            factors.add(ui);
        }
        if (factors.size() == 0) {
            logger.info("irred P = " + P);
            factors.add(P);
        }
        return factors;
    }


    boolean testSeparate(List<BigInteger> cei, BigInteger pec) {
        int i = 0;
        for ( BigInteger c : cei ) {
            if ( !c.gcd(pec).abs().isONE() ) {
                i++;
            }
        }
        return (i <= 1);
    }


    boolean isNearlySquarefree(GenPolynomial<BigInteger> P) { // unused
        // in main variable
        GenPolynomialRing<BigInteger> pfac = P.ring;
        if ( true || pfac.nvar <= 4 ) {
            return sengine.isSquarefree(P);
        }
        GenPolynomialRing<GenPolynomial<BigInteger>> rfac = pfac.recursive(1);
        GenPolynomial<GenPolynomial<BigInteger>> Pr = PolyUtil.<BigInteger> recursive(rfac,P);
        GenPolynomial<GenPolynomial<BigInteger>> Ps = PolyUtil.<BigInteger> recursiveDeriviative(Pr);
        System.out.println("Pr = " + Pr);
        System.out.println("Ps = " + Ps);
        GenPolynomial<GenPolynomial<BigInteger>> g = engine.recursiveUnivariateGcd(Pr,Ps);
        System.out.println("g_m = " + g);
        if ( ! g.isONE() ) {
            return false;
        }
        // in lowest variable
        rfac = pfac.recursive(pfac.nvar-1);
        Pr = PolyUtil.<BigInteger> recursive(rfac,P);
        Pr = PolyUtil.<BigInteger> switchVariables(Pr);
        Ps = PolyUtil.<BigInteger> recursiveDeriviative(Pr);
        System.out.println("Pr = " + Pr);
        System.out.println("Ps = " + Ps);
        g = engine.recursiveUnivariateGcd(Pr,Ps);
        System.out.println("g_1 = " + g);
        if ( ! g.isONE() ) {
            return false;
        }
        return true;
    }

}



class TrialParts {


    public final List<BigInteger> evalPoints;


    public final GenPolynomial<BigInteger> univPoly;


    public final List<GenPolynomial<BigInteger>> univFactors;


    public final List<GenPolynomial<BigInteger>> ldcfFactors;


    public final List<BigInteger> ldcfEval;


    public TrialParts(List<BigInteger> ev, 
                      GenPolynomial<BigInteger> up, List<GenPolynomial<BigInteger>> uf,
                      List<BigInteger> le,
                      List<GenPolynomial<BigInteger>> lf) {
        evalPoints = ev;
        univPoly = up;
        univFactors = uf;
        //ldcfPoly = lp;
        ldcfFactors = lf;
        ldcfEval = le;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("TrialParts[");
        sb.append("evalPoints = " + evalPoints);
        sb.append(", univPoly = " + univPoly);
        sb.append(", univFactors = " + univFactors);
        sb.append(", ldcfEval = " + ldcfEval);
        sb.append(", ldcfFactors = " + ldcfFactors);
        sb.append("]");
        return sb.toString();
    }

}
