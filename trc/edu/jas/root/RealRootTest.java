/*
 * $Id$
 */

package edu.jas.root;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigRational;
import edu.jas.arith.Roots;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.structure.Power;
import edu.jas.structure.RingFactory;


/**
 * RealRoot tests with JUnit.
 * @author Heinz Kredel
 */

public class RealRootTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>RealRootTest</CODE> object.
     * @param name String.
     */
    public RealRootTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(RealRootTest.class);
        return suite;
    }


    TermOrder to = new TermOrder(TermOrder.INVLEX);


    GenPolynomialRing<BigRational> dfac;


    BigRational ai, bi, ci, di, ei, eps;


    GenPolynomial<BigRational> a, b, c, d, e;


    int rl = 1;


    int kl = 5;


    int ll = 7;


    int el = 7;


    float q = 0.7f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        String[] vars = new String[] { "x" };
        dfac = new GenPolynomialRing<BigRational>(new BigRational(1), rl, to, vars);
        // eps = new BigRational(1L,1000000L*1000000L*1000000L);
        eps = Power.positivePower(new BigRational(1L, 10L), BigDecimal.DEFAULT_PRECISION);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        dfac = null;
        eps = null;
    }


    /**
     * Test Sturm sequence.
     */
    public void testSturmSequence() {
        a = dfac.random(kl, ll, el, q);
        //System.out.println("a = " + a);

        RealRootsSturm<BigRational> rrs = new RealRootsSturm<BigRational>();

        List<GenPolynomial<BigRational>> S = rrs.sturmSequence(a);
        //System.out.println("S = " + S);

        try {
            b = a.remainder(S.get(0));
        } catch (Exception e) {
            fail("not S(0)|f " + e);
        }
        assertTrue("a mod S(0) == 0 ", b.isZERO());

        assertTrue("S(-1) == 1 ", S.get(S.size() - 1).isConstant());
    }


    /**
     * Test root bound.
     */
    public void testRootBound() {
        a = dfac.random(kl, ll, el, q);
        //System.out.println("a = " + a);

        RealRootsAbstract<BigRational> rr = new RealRootsSturm<BigRational>();

        // used root bound
        BigRational M = rr.realRootBound(a);
        //System.out.println("M = " + M);
        assertTrue("M >= 1 ", M.compareTo(BigRational.ONE) >= 0);
        Interval<BigRational> v1 = new Interval<BigRational>(M.negate(), M);
        long r1 = rr.realRootCount(v1, a);
        //System.out.println("v1 = " + v1 + ", r1 = " + r1);

        a = a.monic();
        //System.out.println("a = " + a);
        BigDecimal ar = M.getDecimal();
        //System.out.println("ar = " + ar);
        assertTrue("ar >= 1 ", ar.compareTo(BigDecimal.ONE) >= 0);

        // maxNorm root bound
        BigRational mr = a.maxNorm().getRational().sum(BigRational.ONE);
        BigDecimal dr = mr.getDecimal();
        //System.out.println("dr = " + dr);
        //assertTrue("ar >= maxNorm(a): " + (ar.subtract(dr)), ar.compareTo(dr) >= 0);
        Interval<BigRational> v2 = new Interval<BigRational>(mr.negate(), mr);
        long r2 = rr.realRootCount(v2,a);
        //System.out.println("v2 = " + v2 + ", r2 = " + r2);
        assertTrue("r1 == r2: " + (r2-r1), r1 == r2);

        // squareNorm root bound
        BigRational qr = a.squareNorm().getRational();
        BigDecimal ir = Roots.sqrt(qr.getDecimal());
        //qr = Roots.sqrt(qr);
        //System.out.println("ir = " + ir);
        //assertTrue("ar >= squareNorm(a): " + (ar.subtract(ir)), ar.compareTo(ir) >= 0);
        Interval<BigRational> v3 = new Interval<BigRational>(qr.negate(), qr);
        long r3 = rr.realRootCount(v3,a);
        //System.out.println("v3 = " + v3 + ", r3 = " + r3);
        assertTrue("r1 == r3: " + (r3-r1), r1 == r3);

        // sumNorm root bound
        BigRational pr = a.sumNorm().getRational();
        BigDecimal sr = pr.getDecimal();
        //System.out.println("sr = " + sr);
        //assertTrue("ar >= squareNorm(a): " + (ar.subtract(sr)), ar.compareTo(sr) >= 0);
        Interval<BigRational> v4 = new Interval<BigRational>(pr.negate(), pr);
        long r4 = rr.realRootCount(v4,a);
        //System.out.println("v4 = " + v4 + ", r4 = " + r4);
        assertTrue("r1 == r4: " + (r4-r1), r1 == r4);

        // minimal root bound
        BigDecimal dri = dr.sum(BigDecimal.ONE).inverse();
        //System.out.println("dri = " + dri + ", sign(dri) = " + dri.signum());
        assertTrue("minimal root > 0: " + dri, dri.signum() > 0);
        BigDecimal mri = rr.realMinimalRootBound(a).getDecimal();
        //System.out.println("mri = " + mri + ", sign(mri) = " + mri.signum());
        BigDecimal s = dri.subtract(mri).abs();
        eps = eps.multiply(BigRational.ONE.fromInteger(10));
        //System.out.println("s = " + s + ", eps = " + eps.getDecimal());
        assertTrue("minimal root: " + dri, s.compareTo(eps.getDecimal()) < 0);

        // minimal root separation
        long n = a.degree();
        if (n > 0) {
            BigDecimal sep = sr.sum(BigDecimal.ONE).power(2*n).multiply(sr.fromInteger(n).power(n+1)).inverse();
            //System.out.println("sep = " + sep + ", sign(sep) = " + sep.signum());
            assertTrue("separation(a) > 0: " + sep, sep.signum() > 0);
            BigDecimal sri = rr.realMinimalRootSeparation(a).getDecimal();
            BigDecimal ss = sep.subtract(sri).abs();
            assertTrue("minimal separation: " + sep, ss.compareTo(eps.getDecimal()) < 0);
        }
    }


    /**
     * Test real root isolation.
     */
    public void testRealRootIsolation() {
        a = dfac.random(kl, ll * 2, el * 2, q);
        //a = a.multiply( dfac.univariate(0) );
        //System.out.println("a = " + a);

        RealRoots<BigRational> rr = new RealRootsSturm<BigRational>();

        List<Interval<BigRational>> R = rr.realRoots(a);
        //System.out.println("R = " + R);
        //assertTrue("#roots >= 0 ", R.size() >= 0);
        assertTrue("#roots >= 0 ", R != null);
    }


    /**
     * Test Thom lemma real root sign sequence.
     */
    public void testRealRootSignSequence() {
        a = dfac.random(kl, ll * 2, el * 2, q);
        if (a.degree() % 2 == 0) {
            a = a.multiply( dfac.univariate(0).subtract(dfac.getONE()) );
        }
        //System.out.println("a = " + a);
        RealRootsAbstract<BigRational> rr = new RealRootsSturm<BigRational>();

        List<Interval<BigRational>> R = rr.realRoots(a);
        //System.out.println("R = " + R);
        //assertTrue("#roots >= 0 ", R.size() >= 0);
        assertTrue("#roots >= 0 ", R != null);

        int l = R.size();
        Interval<BigRational> v = R.get(l-1);
        Interval<BigRational> u = R.get(0);
        if (u.left.isZERO() && u.right.isZERO()) {
            Interval<BigRational> w = v;
            v = u;
            u = w;
        }
        Interval<BigRational> vm = new Interval<BigRational>(u.left,v.right);
        //System.out.println("v  = " + v);
        //System.out.println("u  = " + u);
        //System.out.println("vm = " + vm);
        long rc = rr.realRootCount(vm,a);
        //System.out.println("rc = " + rc);
        assertTrue("root number: R = " + R + ", rc = " + rc, rc == l);
        long rn = rr.realRootNumber(a,vm);
        assertTrue("root number == " + rn, rn == l);

        long d = a.degree();
        List<GenPolynomial<BigRational>> fs = rr.fourierSequence(a);
        //System.out.println("fs = " + fs);
        assertTrue("len(fs) == " + (d+1-fs.size()), fs.size() == (d+1));

        //List<Integer> ss = rr.signSequence(a, v);
        //System.out.println("ss = " + ss);
        //assertTrue("len(ss) == " + (d-ss.size()), ss.size() == d);
        for (Interval<BigRational> t : R) {
            List<Integer> ss = rr.signSequence(a, t);
            //System.out.println("ss = " + ss);
            assertTrue("len(ss) == " + (d-ss.size()), ss.size() == d);
        }
    }


    /**
     * Test real root isolation Wilkinson polynomials.
     * p = (x-0)*(x-1)*(x-2)*(x-3)*...*(x-n)
     */
    public void testRealRootIsolationWilkinson() {
        final int N = 10;
        d = dfac.getONE();
        e = dfac.univariate(0);

        List<Interval<BigRational>> Rn = new ArrayList<Interval<BigRational>>(N);
        a = d;
        for (int i = 0; i < N; i++) {
            c = dfac.fromInteger(i);
            Rn.add(new Interval<BigRational>(c.leadingBaseCoefficient()));
            b = e.subtract(c);
            a = a.multiply(b);
        }
        //System.out.println("a = " + a);

        RealRoots<BigRational> rr = new RealRootsSturm<BigRational>();

        List<Interval<BigRational>> R = rr.realRoots(a);
        //System.out.println("R = " + R);

        assertTrue("#roots = " + N + " ", R.size() == N);

        eps = eps.multiply(new BigRational("1/10"));
        //System.out.println("eps = " + eps);

        R = rr.refineIntervals(R, a, eps);
        //System.out.println("R = " + R);
        int i = 0;
        for (Interval<BigRational> v : R) {
            BigDecimal dd = v.toDecimal(); 
            BigDecimal di = Rn.get(i++).toDecimal();
            //System.out.println("v  = " + dd);
            //System.out.println("vi = " + di);
            //System.out.println("|dd - di| < eps: " + dd.compareTo(di));
            assertTrue("|dd - di| < eps ", dd.compareTo(di) == 0);
        }
    }


    /**
     * Test real root isolation Wilkinson polynomials inverse.
     * p = (x-1)*(x-1/2)*(x-1/3)*...*(x-1/n)
     */
    public void testRealRootIsolationWilkinsonInverse() {
        final int N = 9;
        d = dfac.getONE();
        e = dfac.univariate(0);

        List<Interval<BigRational>> Rn = new ArrayList<Interval<BigRational>>(N);
        a = d;
        for (int i = 1; i < N; i++) { // use only for i > 0, since reverse
            c = dfac.fromInteger(i);
            if (i != 0) {
                c = d.divide(c);
            }
            Rn.add(new Interval<BigRational>(c.leadingBaseCoefficient()));
            b = e.subtract(c);
            a = a.multiply(b);
        }
        //System.out.println("a = " + a);
        //System.out.println("Rn = " + Rn);
        Collections.reverse(Rn);
        //System.out.println("Rn = " + Rn);

        RealRoots<BigRational> rr = new RealRootsSturm<BigRational>();

        List<Interval<BigRational>> R = rr.realRoots(a);
        //System.out.println("R = " + R);

        assertTrue("#roots = " + (N - 1) + " ", R.size() == (N - 1));

        eps = eps.multiply(new BigRational("1/100"));
        //System.out.println("eps = " + eps);

        R = rr.refineIntervals(R, a, eps);
        //System.out.println("R = " + R);
        int i = 0;
        for (Interval<BigRational> v : R) {
            BigDecimal dd = v.toDecimal(); //.sum(eps1);
            BigDecimal di = Rn.get(i++).toDecimal();
            //System.out.println("v  = " + dd);
            //System.out.println("vi = " + di);
            //System.out.println("|dd - di| < eps: " + dd.compareTo(di));
            assertTrue("|dd - di| < eps ", dd.compareTo(di) == 0);
        }
    }


    /**
     * Test real algebraic number sign.
     */
    public void testRealAlgebraicNumberSign() {
        d = dfac.fromInteger(2);
        e = dfac.univariate(0);

        a = e.multiply(e);
        // a = a.multiply(e).multiply(e).multiply(e);
        a = a.subtract(d); // x^2 -2
        //System.out.println("a = " + a);

        RealRoots<BigRational> rr = new RealRootsSturm<BigRational>();

        ai = new BigRational(1);
        bi = new BigRational(2);
        Interval<BigRational> iv = new Interval<BigRational>(ai, bi);
        //System.out.println("iv = " + iv);
        assertTrue("sign change", rr.signChange(iv, a));

        b = dfac.random(kl, (int) a.degree() + 1, (int) a.degree(), 1.0f);
        //b = dfac.getZERO();
        //b = dfac.random(kl,ll,el,q);
        //b = b.multiply(b);
        //b = b.abs().negate();
        //System.out.println("b = " + b);
        if (b.isZERO()) {
            int s = rr.realSign(iv, a, b);
            assertTrue("algebraic sign", s == 0);
            return;
        }

        int as = rr.realSign(iv, a, b);
        //System.out.println("as = " + as);
        // how to test?
        int asn = rr.realSign(iv, a, b.negate());
        //System.out.println("asn = " + asn);
        assertTrue("algebraic sign", as != asn);

        iv = new Interval<BigRational>(bi.negate(), ai.negate());
        //System.out.println("iv = " + iv);
        assertTrue("sign change", rr.signChange(iv, a));

        int as1 = rr.realSign(iv, a, b);
        //System.out.println("as1 = " + as1);
        // how to test?
        int asn1 = rr.realSign(iv, a, b.negate());
        //System.out.println("asn1 = " + asn1);
        assertTrue("algebraic sign", as1 != asn1);

        assertTrue("algebraic sign", as * as1 == asn * asn1);
    }


    /**
     * Test real root isolation and decimal refinement of Wilkinson polynomials.
     * p = (x-0)*(x-1)*(x-2)*(x-3)*...*(x-n)
     */
    public void testRealRootIsolationDecimalWilkinson() {
        final int N = 10;
        d = dfac.getONE();
        e = dfac.univariate(0);

        List<Interval<BigRational>> Rn = new ArrayList<Interval<BigRational>>(N);
        a = d;
        for (int i = 0; i < N; i++) {
            c = dfac.fromInteger(i);
            Rn.add(new Interval<BigRational>(c.leadingBaseCoefficient()));
            b = e.subtract(c);
            a = a.multiply(b);
        }
        //System.out.println("a = " + a);

        RealRootsAbstract<BigRational> rr = new RealRootsSturm<BigRational>();

        List<Interval<BigRational>> R = rr.realRoots(a);
        //System.out.println("R = " + R);

        assertTrue("#roots = " + N + " ", R.size() == N);

        eps = eps.multiply(new BigRational(100000));
        //System.out.println("eps = " + eps);
        BigDecimal eps1 = new BigDecimal(eps);
        BigDecimal eps2 = eps1.multiply(new BigDecimal("100"));
        //System.out.println("eps1 = " + eps1);
        //System.out.println("eps2 = " + eps2);

        try {
            int i = 0;
            for (Interval<BigRational> v : R) {
                //System.out.println("v = " + v);
                BigDecimal dd = rr.approximateRoot(v,a,eps);
                BigDecimal di = Rn.get(i++).toDecimal();
                //System.out.println("di = " + di);
                //System.out.println("dd = " + dd);
                assertTrue("|dd - di| < eps ", dd.subtract(di).abs().compareTo(eps2) <= 0);
            }
        } catch (NoConvergenceException e) {
            fail(e.toString());
        }
    }


    /**
     * Test real root isolation and decimal refinement of Wilkinson polynomials, inverse roots.
     * p = (x-1)*(x-1/2)*(x-1/3)*...*(x-1/n)
     */
    public void testRealRootIsolationDecimalWilkinsonInverse() {
        final int N = 10;
        d = dfac.getONE();
        e = dfac.univariate(0);

        List<Interval<BigRational>> Rn = new ArrayList<Interval<BigRational>>(N);
        a = d;
        for (int i = 1; i < N; i++) { // use only for i > 0, since reverse
            c = dfac.fromInteger(i);
            if (i != 0) {
                c = d.divide(c);
            }
            Rn.add(new Interval<BigRational>(c.leadingBaseCoefficient()));
            b = e.subtract(c);
            a = a.multiply(b);
        }
        //System.out.println("a = " + a);
        //System.out.println("Rn = " + Rn);
        Collections.reverse(Rn);
        //System.out.println("Rn = " + Rn);

        RealRootsAbstract<BigRational> rr = new RealRootsSturm<BigRational>();

        List<Interval<BigRational>> R = rr.realRoots(a);
        //System.out.println("R = " + R);

        assertTrue("#roots = " + (N - 1) + " ", R.size() == (N - 1));

        eps = eps.multiply(new BigRational(1000000));
        //System.out.println("eps = " + eps);
        BigDecimal eps1 = new BigDecimal(eps);
        BigDecimal eps2 = eps1.multiply(new BigDecimal("10"));
        //System.out.println("eps1 = " + eps1);
        //System.out.println("eps2 = " + eps2);

        try {
            int i = 0;
            for (Interval<BigRational> v : R) {
                //System.out.println("v = " + v);
                BigDecimal dd = rr.approximateRoot(v,a,eps);
                BigDecimal di = Rn.get(i++).toDecimal();
                //System.out.println("di = " + di);
                //System.out.println("dd = " + dd);
                assertTrue("|dd - di| < eps ", dd.subtract(di).abs().compareTo(eps2) <= 0);
            }
        } catch (NoConvergenceException e) {
            fail(e.toString());
        }
    }


    /**
     * Test real root isolation and decimal refinement of Wilkinson polynomials, all roots.
     * p = (x-0)*(x-1)*(x-2)*(x-3)*...*(x-n)
     */
    public void testRealRootIsolationDecimalWilkinsonAll() {
        final int N = 10; 
        d = dfac.getONE();
        e = dfac.univariate(0);

        List<Interval<BigRational>> Rn = new ArrayList<Interval<BigRational>>(N);
        a = d;
        for (int i = 0; i < N; i++) {
            c = dfac.fromInteger(i);
            Rn.add(new Interval<BigRational>(c.leadingBaseCoefficient()));
            b = e.subtract(c);
            a = a.multiply(b);
        }
        //System.out.println("a = " + a);

        RealRootsAbstract<BigRational> rr = new RealRootsSturm<BigRational>();

        eps = eps.multiply(new BigRational(10000));
        //System.out.println("eps = " + eps);
        BigDecimal eps1 = new BigDecimal(eps);
        BigDecimal eps2 = eps1.multiply(new BigDecimal("100"));
        //System.out.println("eps1 = " + eps1);
        //System.out.println("eps2 = " + eps2);

        List<BigDecimal> R = null;
        R = rr.approximateRoots(a,eps);
        //System.out.println("R = " + R);
        assertTrue("#roots = " + N + " ", R.size() == N);

        int i = 0;
        for (BigDecimal dd : R) {
            //System.out.println("dd = " + dd);
            BigDecimal di = Rn.get(i++).toDecimal();
            //System.out.println("di = " + di);
            assertTrue("|dd - di| < eps ", dd.subtract(di).abs().compareTo(eps2) <= 0);
        }
        boolean t = rr.isApproximateRoot(R,a,eps);
        assertTrue("some |a(dd)| < eps ", t);
    }


    /**
     * Test real root isolation and decimal refinement of Wilkinson polynomials, inverse roots, all roots.
     * p = (x-1)*(x-1/2)*(x-1/3)*...*(x-1/n)
     */
    public void testRealRootIsolationDecimalWilkinsonInverseAll() {
        final int N = 10;
        d = dfac.getONE();
        e = dfac.univariate(0);

        List<Interval<BigRational>> Rn = new ArrayList<Interval<BigRational>>(N);
        a = d;
        for (int i = 1; i < N; i++) { // use only for i > 0, since reverse
            c = dfac.fromInteger(i);
            if (i != 0) {
                c = d.divide(c);
            }
            Rn.add(new Interval<BigRational>(c.leadingBaseCoefficient()));
            b = e.subtract(c);
            a = a.multiply(b);
        }
        //System.out.println("a = " + a);
        //System.out.println("Rn = " + Rn);
        Collections.reverse(Rn);
        //System.out.println("Rn = " + Rn);

        RealRootsAbstract<BigRational> rr = new RealRootsSturm<BigRational>();

        eps = eps.multiply(new BigRational(1000000));
        //System.out.println("eps = " + eps);
        BigDecimal eps1 = new BigDecimal(eps);
        BigDecimal eps2 = eps1.multiply(new BigDecimal("10"));
        //System.out.println("eps1 = " + eps1);
        //System.out.println("eps2 = " + eps2);

        List<BigDecimal> R = null;
        R = rr.approximateRoots(a,eps);
        //System.out.println("R = " + R);
        assertTrue("#roots = " + (N - 1) + " ", R.size() == (N - 1));

        int i = 0;
        for (BigDecimal dd : R) {
            //System.out.println("dd = " + dd);
            BigDecimal di = Rn.get(i++).toDecimal();
            //System.out.println("di = " + di);
            assertTrue("|dd - di| < eps ", dd.subtract(di).abs().compareTo(eps2) <= 0);
        }
        boolean t = rr.isApproximateRoot(R,a,eps);
        assertTrue("some |a(dd)| < eps ", t);
    }

}
