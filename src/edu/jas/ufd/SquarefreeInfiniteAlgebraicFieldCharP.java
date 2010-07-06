/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.SortedMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;
import edu.jas.poly.PolyUtil;
import edu.jas.gb.Reduction;
import edu.jas.gb.ReductionSeq;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.Power;
import edu.jas.structure.RingFactory;
import edu.jas.application.Ideal;


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
        // generate system of equations
        AlgebraicNumberRing<C> afac = P.ring;
        System.out.println("afac = " + afac);
        long deg = afac.modul.degree(0);
        int d = (int)deg;
        System.out.println("deg = " + deg);
        String[] vn = GenPolynomialRing.newVars("c",d);
        GenPolynomialRing<AlgebraicNumber<C>> pfac = new GenPolynomialRing<AlgebraicNumber<C>>(afac,d,vn);
        System.out.println("pfac = " + pfac);
        List<GenPolynomial<AlgebraicNumber<C>>> uv = (List<GenPolynomial<AlgebraicNumber<C>>>) pfac.univariateList();
        System.out.println("uv = " + uv);
        GenPolynomial<AlgebraicNumber<C>> cp = pfac.getZERO();
        GenPolynomialRing<C> apfac = afac.ring;
        long i = 0;
        for ( GenPolynomial<AlgebraicNumber<C>> pa : uv ) {
            GenPolynomial<C> ca = apfac.univariate(0,i++);
            System.out.println("ca = " + ca);
            GenPolynomial<AlgebraicNumber<C>> pb = pa.multiply( new AlgebraicNumber<C>(afac,ca) );
            System.out.println("pb = " + pb);
            cp = cp.sum(pb);
        }
        System.out.println("cp = " + cp);
        GenPolynomial<AlgebraicNumber<C>> cpp = Power.<GenPolynomial<AlgebraicNumber<C>>>positivePower(cp,c);
        System.out.println("cpp = " + cpp);
        System.out.println("P   = " + P);
        GenPolynomialRing<C> ppfac = new GenPolynomialRing<C>(apfac.coFac,pfac);
        System.out.println("ppfac = " + ppfac);
        List<GenPolynomial<C>> gl = new ArrayList<GenPolynomial<C>>();
        for (Monomial<AlgebraicNumber<C>> m : cpp) {
            ExpVector f = m.e;
            AlgebraicNumber<C> a = m.c;
            System.out.println("m = " + m);
            GenPolynomial<C> ap = a.val;
            GenPolynomial<C> g = ppfac.getZERO();
            for ( Monomial<C> ma : ap ) {
                //System.out.println("ma = " + ma);
                ExpVector e = ma.e;
                C cc = ma.c;
                C pc = P.val.coefficient(e);
                // System.out.println("pc = " + pc);
                GenPolynomial<C> r = new GenPolynomial<C>(ppfac,cc,f);
                r = r.subtract(pc);
                System.out.println("r = " + r);
                gl.add(r);
            }
        }
        System.out.println("gl = " + gl);
        Reduction<C> red = new ReductionSeq<C>();
        gl = red.irreducibleSet(gl);
        System.out.println("gl = " + gl);
        Ideal<C> L = new Ideal<C>(ppfac, gl, true);
        int z = L.commonZeroTest();
        System.out.println("z = " + z);
        if (z < 0) {
            return null;
        }
        GenPolynomial<C> car = apfac.getZERO();
        for ( GenPolynomial<C> pl : gl ) {
	    if ( pl.length() <= 1 ) {
		continue;
	    }
	    if ( pl.length() > 2 ) {
		throw new RuntimeException("dim > 0 not implemented " + pl);
	    }
            System.out.println("pl = " + pl);
            ExpVector e = pl.leadingExpVector();
            int[] v = e.dependencyOnVariables();
            if (v == null || v.length == 0) {
                continue;
            }
            int vi = v[0];
            System.out.println("vi = " + vi);
            GenPolynomial<C> ca = apfac.univariate(0,deg-1-vi);
            System.out.println("ca = " + ca);
            C tc = pl.trailingBaseCoefficient();
            tc = tc.negate();
	    // p-th root of tc ...
            ca = ca.multiply(tc);
            car = car.sum(ca);
	}
        AlgebraicNumber<C> rr = new AlgebraicNumber<C>(afac,car);
        System.out.println("rr = " + rr);
        //System.out.println("rr^p = " + Power.<AlgebraicNumber<C>>positivePower(rr,c));
        root.put(rr, 1L);
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
            //System.out.println("m = " + m);
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
