/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.Power;
import edu.jas.structure.RingFactory;


/**
 * Squarefree decomposition for algebraic extensions of infinite coefficient fields 
 * of characteristic p &gt; 0.
 * @author Heinz Kredel
 */

public class SquarefreeInfiniteAlgebraicFieldCharP<C extends GcdRingElem<C>> 
             extends SquarefreeFieldCharP<AlgebraicNumber<C>> {


    private static final Logger logger = Logger.getLogger(SquarefreeInfiniteAlgebraicFieldCharP.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * GCD engine for infinite ring of characteristic p base coefficients.
     */
    protected final SquarefreeAbstract<C> rengine;



    /**
     * Constructor.
     */
    public SquarefreeInfiniteAlgebraicFieldCharP(RingFactory<AlgebraicNumber<C>> fac) {
        super(fac);
        // isFinite() predicate now present
        if ( fac.isFinite() ) {
            throw new IllegalArgumentException("fac must be in-finite"); 
        }
        AlgebraicNumberRing<C> afac = (AlgebraicNumberRing<C>) fac;
        GenPolynomialRing<C> rfac = afac.ring;
        System.out.println("rfac = " + rfac);
        //System.out.println("rfac = " + rfac.coFac);
        rengine = SquarefreeFactory.<C>getImplementation(rfac);
        System.out.println("rengine = " + rengine);
    }


    /* --------- algebraic number char-th roots --------------------- */


    /**
     * Squarefree factors of a AlgebraicNumber.
     * @param P AlgebraicNumber.
     * @return [p_1 -&gt; e_1,...,p_k - &gt; e_k] with P = prod_{i=1, ..., k}
     *         p_i**e_k.
     */
    public SortedMap<AlgebraicNumber<C>, Long> squarefreeFactors(AlgebraicNumber<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        SortedMap<AlgebraicNumber<C>, Long> factors = new TreeMap<AlgebraicNumber<C>, Long>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.put(P, 1L);
            return factors;
        }
        GenPolynomial<C> an = P.val;
        AlgebraicNumberRing<C> pfac = P.ring;
        GenPolynomial<C> one = pfac.ring.getONE();
        if (!an.isONE()) {
            System.out.println("an = " + an);
            System.out.println("rengine = " + rengine);
            SortedMap<GenPolynomial<C>, Long> nfac = rengine.squarefreeFactors(an);
            System.out.println("nfac = " + nfac);
            for (GenPolynomial<C> nfp : nfac.keySet()) {
                AlgebraicNumber<C> nf = new AlgebraicNumber<C>(pfac, nfp);
                factors.put(nf, nfac.get(nfp));
            }
        }
        if (factors.size() == 0) {
            factors.put(P, 1L);
        }
        return factors;
    }


    /**
     * Characteristics root of a AlgebraicNumber.
     * @param P AlgebraicNumber.
     * @return [p -&gt; k] if exists k with e=charactristic(P)*k and P = p**e,
     *         else null.
     */
    public SortedMap<AlgebraicNumber<C>, Long> rootCharacteristic(AlgebraicNumber<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        java.math.BigInteger c = P.ring.characteristic();
        if (c.signum() == 0) {
            return null;
        }
        SortedMap<AlgebraicNumber<C>, Long> root = new TreeMap<AlgebraicNumber<C>, Long>();
        if (P.isZERO()) {
            return root;
        }
        if (P.isONE()) {
            root.put(P, 1L);
            return root;
        }
        SortedMap<AlgebraicNumber<C>, Long> sf = squarefreeFactors(P);
        if (sf == null || sf.size() == 0) {
            return null;
        }
        if ( logger.isInfoEnabled() ) {
            logger.info("sf,algeb = " + sf);
        }
        // better: test if sf.size() == 2 // no, since num and den factors 
        Long k = null;
        Long cl = c.longValue();
        for (AlgebraicNumber<C> p : sf.keySet()) {
            //System.out.println("p = " + p);
            if (p.val.isConstant()) { // todo: check for non-constants in coefficients
                continue;
            }
            Long e = sf.get(p);
            long E = e.longValue();
            long r = E % cl;
            if (r != 0) {
                //System.out.println("r = " + r);
                return null;
            }
            if (k == null) {
                k = e;
            } else if (k >= e) {
                k = e;
            }
        }
        if (k == null) {
            k = 1L; //return null;
        }
        // now c divides all exponents of non constant elements
        AlgebraicNumber<C> rp = P.ring.getONE();
        for (AlgebraicNumber<C> q : sf.keySet()) {
            Long e = sf.get(q);
            //System.out.println("q = " + q + ", e = " + e);
            if (e >= k) {
                e = e / cl;
                //q = Power.<AlgebraicNumber<C>> positivePower(q, e);
                root.put(q, e);
            } else { // constant case
                root.put(q, e);
            }
        }
        //System.out.println("root = " + root);
        return root;
    }


    /**
     * GenPolynomial char-th root main variable.
     * @param P univariate GenPolynomial with AlgebraicNumber coefficients.
     * @return char-th_rootOf(P), or null, if P is no char-th root.
     */
    public GenPolynomial<AlgebraicNumber<C>> rootCharacteristic(GenPolynomial<AlgebraicNumber<C>> P) {
        if (P == null || P.isZERO()) {
            return P;
        }
        GenPolynomialRing<AlgebraicNumber<C>> pfac = P.ring;
        if (pfac.nvar > 1) {
            // go to recursion
            GenPolynomialRing<AlgebraicNumber<C>> cfac = pfac.contract(1);
            GenPolynomialRing<GenPolynomial<AlgebraicNumber<C>>> rfac = new GenPolynomialRing<GenPolynomial<AlgebraicNumber<C>>>(
                    cfac, 1);
            GenPolynomial<GenPolynomial<AlgebraicNumber<C>>> Pr = PolyUtil.<AlgebraicNumber<C>> recursive(rfac, P);
            GenPolynomial<GenPolynomial<AlgebraicNumber<C>>> Prc = recursiveUnivariateRootCharacteristic(Pr);
            if (Prc == null) {
                return null;
            }
            GenPolynomial<AlgebraicNumber<C>> D = PolyUtil.<AlgebraicNumber<C>> distribute(pfac, Prc);
            return D;
        }
        RingFactory<AlgebraicNumber<C>> rf = pfac.coFac;
        if (rf.characteristic().signum() != 1) {
            // basePthRoot not possible
            throw new RuntimeException(P.getClass().getName() + " only for ModInteger polynomials " + rf);
        }
        long mp = rf.characteristic().longValue();
        GenPolynomial<AlgebraicNumber<C>> d = pfac.getZERO().clone();
        for (Monomial<AlgebraicNumber<C>> m : P) {
            ExpVector f = m.e;
            long fl = f.getVal(0);
            if (fl % mp != 0) {
                return null;
            }
            fl = fl / mp;
            SortedMap<AlgebraicNumber<C>, Long> sm = rootCharacteristic(m.c);
            if (sm == null) {
                return null;
            }
            if (logger.isInfoEnabled()) {
                logger.info("sm_alg,root = " + sm);
            }
            AlgebraicNumber<C> r = rf.getONE();
            for (AlgebraicNumber<C> rp : sm.keySet()) {
                long gl = sm.get(rp);
                if (gl > 1) {
                    rp = Power.<AlgebraicNumber<C>> positivePower(rp, gl);
                }
                r = r.multiply(rp);
            }
            ExpVector e = ExpVector.create(1, 0, fl);
            d.doPutToMap(e, r);
        }
        logger.info("sm_alg,root,d = " + d);
        return d;
    }


    /**
     * GenPolynomial char-th root univariate polynomial. 
     * @param P GenPolynomial.
     * @return char-th_rootOf(P).
     */
    @Override
    public GenPolynomial<AlgebraicNumber<C>> baseRootCharacteristic(GenPolynomial<AlgebraicNumber<C>> P) {
        if (P == null || P.isZERO()) {
            return P;
        }
        GenPolynomialRing<AlgebraicNumber<C>> pfac = P.ring;
        if (pfac.nvar > 1) {
            // basePthRoot not possible by return type
            throw new RuntimeException(P.getClass().getName() + " only for univariate polynomials");
        }
        RingFactory<AlgebraicNumber<C>> rf = pfac.coFac;
        if (rf.characteristic().signum() != 1) {
            // basePthRoot not possible
            throw new RuntimeException(P.getClass().getName() + " only for char p > 0 " + rf);
        }
        long mp = rf.characteristic().longValue();
        GenPolynomial<AlgebraicNumber<C>> d = pfac.getZERO().clone();
        for (Monomial<AlgebraicNumber<C>> m : P) {
            System.out.println("m = " + m);
            ExpVector f = m.e;
            long fl = f.getVal(0);
            if (fl % mp != 0) {
                return null;
            }
            fl = fl / mp;
            SortedMap<AlgebraicNumber<C>, Long> sm = rootCharacteristic(m.c);
            if (sm == null) {
                return null;
            }
            if (logger.isInfoEnabled()) {
                logger.info("sm_alg,base,root = " + sm);
            }
            AlgebraicNumber<C> r = rf.getONE();
            for (AlgebraicNumber<C> rp : sm.keySet()) {
                //System.out.println("rp = " + rp);
                long gl = sm.get(rp);
                //System.out.println("gl = " + gl);
                AlgebraicNumber<C> re = rp;
                if (gl > 1) {
                    re = Power.<AlgebraicNumber<C>> positivePower(rp, gl);
                }
                //System.out.println("re = " + re);
                r = r.multiply(re); 
            }
            ExpVector e = ExpVector.create(1, 0, fl);
            d.doPutToMap(e, r);
        }
        if (logger.isInfoEnabled()) {
            logger.info("sm_alg,base,d = " + d);
        }
        return d;
    }


    /**
     * GenPolynomial char-th root univariate polynomial with polynomial coefficients.
     * @param P recursive univariate GenPolynomial.
     * @return char-th_rootOf(P), or null if P is no char-th root.
     */
    @Override
    public GenPolynomial<GenPolynomial<AlgebraicNumber<C>>> recursiveUnivariateRootCharacteristic(
                         GenPolynomial<GenPolynomial<AlgebraicNumber<C>>> P) {
        if (P == null || P.isZERO()) {
            return P;
        }
        GenPolynomialRing<GenPolynomial<AlgebraicNumber<C>>> pfac = P.ring;
        if (pfac.nvar > 1) {
            // basePthRoot not possible by return type
            throw new RuntimeException(P.getClass().getName() + " only for univariate recursive polynomials");
        }
        RingFactory<GenPolynomial<AlgebraicNumber<C>>> rf = pfac.coFac;
        if (rf.characteristic().signum() != 1) {
            // basePthRoot not possible
            throw new RuntimeException(P.getClass().getName() + " only for char p > 0 " + rf);
        }
        long mp = rf.characteristic().longValue();
        GenPolynomial<GenPolynomial<AlgebraicNumber<C>>> d = pfac.getZERO().clone();
        for (Monomial<GenPolynomial<AlgebraicNumber<C>>> m : P) {
            ExpVector f = m.e;
            long fl = f.getVal(0);
            if (fl % mp != 0) {
                return null;
            }
            fl = fl / mp;
            GenPolynomial<AlgebraicNumber<C>> r = rootCharacteristic(m.c);
            if (r == null) {
                return null;
            }
            ExpVector e = ExpVector.create(1, 0, fl);
            d.doPutToMap(e, r);
        }
        return d;
    }

}
