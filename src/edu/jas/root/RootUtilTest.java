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
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.structure.Power;


/**
 * RootUtil tests with JUnit.
 * @author Heinz Kredel.
 */

public class RootUtilTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>RootUtilTest</CODE> object.
     * @param name String.
     */
    public RootUtilTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(RootUtilTest.class);
        return suite;
    }


    //private final static int bitlen = 100;

    TermOrder to = new TermOrder(TermOrder.INVLEX);


    GenPolynomialRing<BigRational> dfac;


    BigRational ai;


    BigRational bi;


    BigRational ci;


    BigRational di;


    BigRational ei;


    BigRational eps;


    GenPolynomial<BigRational> a;


    GenPolynomial<BigRational> b;


    GenPolynomial<BigRational> c;


    GenPolynomial<BigRational> d;


    GenPolynomial<BigRational> e;


    int rl = 1;


    int kl = 5;


    int ll = 7;


    int el = 7;


    float q = 0.7f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        dfac = new GenPolynomialRing<BigRational>(new BigRational(1), rl, to);
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
     * Test sign variations.
     * 
     */
    public void testSignVar() {
        int[] li = new int[] { 1, 0, 0, -1, 2, 3, 0, 1, 0, 0, 0, -1 };

        List<BigRational> Li = new ArrayList<BigRational>();

        ai = new BigRational();

        for (int i = 0; i < li.length; i++) {
            bi = ai.fromInteger(li[i]);
            Li.add(bi);
        }
        //System.out.println("Li = " + Li);

        long v = RootUtil.<BigRational> signVar(Li);
        //System.out.println("v = " + v);

        assertEquals("varSign(Li)", v, 3);

        List<BigRational> Mi = new ArrayList<BigRational>();
        for (int i = 0; i < 7; i++) {
            bi = ai.random(kl);
            Mi.add(bi);
        }
        //System.out.println("Mi = " + Mi);

        v = RootUtil.<BigRational> signVar(Mi);
        //System.out.println("v = " + v);
        long vv = v;

        assertTrue("varSign(Mi)>=0", v >= 0);

        List<BigRational> Ni = new ArrayList<BigRational>(Mi);
        Ni.addAll(Li);
        //System.out.println("Ni = " + Ni);

        v = RootUtil.<BigRational> signVar(Ni);
        //System.out.println("v = " + v);

        assertTrue("varSign(Mi)>=3", v >= 3 + vv);

        Ni = new ArrayList<BigRational>(Ni);
        Ni.addAll(Mi);
        //System.out.println("Ni = " + Ni);

        v = RootUtil.<BigRational> signVar(Ni);
        //System.out.println("v = " + v);

        assertTrue("varSign(Mi)>=3", v >= 3 + vv);
    }


    /**
     * Test Sturm sequence.
     * 
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
     * 
     */
    public void testRootBound() {
        a = dfac.random(kl, ll, el, q);
        //System.out.println("a = " + a);

        RealRoots<BigRational> rr = new RealRootsSturm<BigRational>();

        BigRational M = rr.realRootBound(a);

        //System.out.println("M = " + M);
        assertTrue("M >= 1 ", M.compareTo(BigRational.ONE) >= 0);

        a = a.monic();
        //System.out.println("a = " + a);
        M = rr.realRootBound(a);

        //System.out.println("M = " + M);
        assertTrue("M >= 1 ", M.compareTo(BigRational.ONE) >= 0);
    }


    /**
     * Test real root isolation.
     * 
     */
    public void testRealRootIsolation() {
        a = dfac.random(kl, ll * 2, el * 2, q);
        //a = a.multiply( dfac.univariate(0) );
        //System.out.println("a = " + a);

        RealRoots<BigRational> rr = new RealRootsSturm<BigRational>();

        List<Interval<BigRational>> R = rr.realRoots(a);
        //System.out.println("R = " + R);
        assertTrue("#roots >= 0 ", R.size() >= 0);
    }


    /**
     * Test real root isolation Wilkinson polynomials.
     * 
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

        //System.out.println("eps = " + eps);
        BigDecimal eps1 = new BigDecimal(eps);
        //System.out.println("eps1 = " + eps1);

        R = rr.refineIntervals(R, a, eps);
        //System.out.println("R = " + R);
        int i = 0;
        for (Interval<BigRational> v : R) {
            BigDecimal dd = v.toDecimal(); //.sum(eps1);
            BigDecimal di = Rn.get(i++).toDecimal();
            //System.out.println("v  = " + dd);
            //System.out.println("vi = " + di);
            assertTrue("|dd - di| < eps ", dd.compareTo(di) == 0);
        }
    }


    /**
     * Test real root isolation Wilkinson polynomials inverse.
     * 
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

        //System.out.println("eps = " + eps);
        BigDecimal eps1 = new BigDecimal(eps);
        //System.out.println("eps1 = " + eps1);

        R = rr.refineIntervals(R, a, eps);
        //System.out.println("R = " + R);
        int i = 0;
        for (Interval<BigRational> v : R) {
            BigDecimal dd = v.toDecimal(); //.sum(eps1);
            BigDecimal di = Rn.get(i++).toDecimal();
            //System.out.println("v  = " + dd);
            //System.out.println("vi = " + di);
            assertTrue("|dd - di| < eps ", dd.compareTo(di) == 0);
        }
    }


    /**
     * Test real algebraic number sign.
     * 
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

}
