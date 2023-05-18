/*
 * $Id$
 */

package edu.jas.poly;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.jas.arith.BigComplex;
import edu.jas.arith.BigQuaternion;
import edu.jas.arith.BigQuaternionRing;
import edu.jas.arith.BigRational;
import edu.jas.structure.RingElem;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * GenSolvablePolynomial Test using JUnit. <b>Note:</b> not optimal since
 * GenSolvablePolynomial does not implement
 * RingElem&lt;GenSolvablePolynomial&gt;
 * @author Heinz Kredel
 */

public class GenSolvablePolynomialTest extends TestCase {


    /**
     * main
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GenSolvablePolynomialTest</CODE> object.
     * @param name String.
     */
    public GenSolvablePolynomialTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GenSolvablePolynomialTest.class);
        return suite;
    }


    int rl = 6; // even for Weyl


    int kl = 10;


    int ll = 7;


    int el = 4;


    float q = 0.5f;

    @Override
    protected void setUp() {
        // a = b = c = d = e = null;
    }


    @Override
    protected void tearDown() {
        // a = b = c = d = e = null;
    }


    /**
     * Test constructors and factory.
     */
    public void testConstructors() {
        // rational numbers
        BigRational rf = new BigRational();
        // System.out.println("rf = " + rf);

        //BigRational r = rf.fromInteger( 99 );
        // System.out.println("r = " + r);
        //r = rf.random( 9 );
        // System.out.println("r = " + r);

        // polynomials over rational numbers
        GenSolvablePolynomialRing<BigRational> pf = new GenSolvablePolynomialRing<BigRational>(rf, 2);
        // System.out.println("pf = " + pf);

        GenSolvablePolynomial<BigRational> p = pf.getONE();
        // System.out.println("p = " + p);
        p = pf.random(9);
        // System.out.println("p = " + p);
        p = pf.getZERO();
        // System.out.println("p = " + p);

        RingElem<GenPolynomial<BigRational>> pe = new GenSolvablePolynomial<BigRational>(pf);
        //System.out.println("pe = " + pe);
        //System.out.println("p.equals(pe) = " + p.equals(pe) );
        //System.out.println("p.equals(p) = " + p.equals(p) );
        assertTrue("p.equals(pe) = ", p.equals(pe));
        assertTrue("p.equals(p) = ", p.equals(p));

        pe = pe.sum(p); // why not p = p.add(pe) ?
        //System.out.println("pe = " + pe);
        assertTrue("pe.isZERO() = ", pe.isZERO());
        p = pf.random(9);
        p = (GenSolvablePolynomial<BigRational>) p.subtract(p);
        //System.out.println("p = " + p);
        //System.out.println("p.isZERO() = " + p.isZERO());
        assertTrue("p.isZERO() = ", p.isZERO());


        // polynomials over (polynomials over rational numbers)
        GenSolvablePolynomialRing<GenPolynomial<BigRational>> ppf = new GenSolvablePolynomialRing<GenPolynomial<BigRational>>(
                        pf, 3);
        // System.out.println("ppf = " + ppf);

        GenSolvablePolynomial<GenPolynomial<BigRational>> pp = ppf.getONE();
        // System.out.println("pp = " + pp);
        pp = ppf.random(2);
        // System.out.println("pp = " + pp);
        pp = ppf.getZERO();
        // System.out.println("pp = " + pp);

        RingElem<GenPolynomial<GenPolynomial<BigRational>>> ppe = new GenSolvablePolynomial<GenPolynomial<BigRational>>(
                        ppf);
        // System.out.println("ppe = " + ppe);
        // System.out.println("pp.equals(ppe) = " + pp.equals(ppe) );
        // System.out.println("pp.equals(pp) = " + pp.equals(pp) );
        assertTrue("pp.equals(ppe) = ", pp.equals(ppe));
        assertTrue("pp.equals(pp) = ", pp.equals(pp));

        ppe = ppe.sum(pp); // why not pp = pp.add(ppe) ?
        //System.out.println("ppe = " + ppe);
        assertTrue("ppe.isZERO() = ", ppe.isZERO());
        pp = ppf.random(2);
        pp = (GenSolvablePolynomial<GenPolynomial<BigRational>>) pp.subtract(pp);
        //System.out.println("pp = " + pp);
        //System.out.println("pp.isZERO() = " + pp.isZERO());
        assertTrue("pp.isZERO() = ", pp.isZERO());


        // polynomials over (polynomials over (polynomials over rational numbers))
        GenSolvablePolynomialRing<GenPolynomial<GenPolynomial<BigRational>>> pppf = new GenSolvablePolynomialRing<GenPolynomial<GenPolynomial<BigRational>>>(
                        ppf, 4);
        // System.out.println("pppf = " + pppf);

        GenSolvablePolynomial<GenPolynomial<GenPolynomial<BigRational>>> ppp = pppf.getONE();
        //System.out.println("ppp = " + ppp);
        ppp = pppf.random(2);
        // System.out.println("ppp = " + ppp);
        ppp = pppf.getZERO();
        // System.out.println("ppp = " + ppp);

        RingElem<GenPolynomial<GenPolynomial<GenPolynomial<BigRational>>>> pppe = new GenSolvablePolynomial<GenPolynomial<GenPolynomial<BigRational>>>(
                        pppf);
        // System.out.println("pppe = " + pppe);
        // System.out.println("ppp.equals(pppe) = " + ppp.equals(pppe) );
        // System.out.println("ppp.equals(ppp) = " + ppp.equals(ppp) );
        assertTrue("ppp.equals(pppe) = ", ppp.equals(pppe));
        assertTrue("ppp.equals(ppp) = ", ppp.equals(ppp));

        pppe = pppe.sum(ppp); // why not ppp = ppp.add(pppe) ?
        // System.out.println("pppe = " + pppe);
        assertTrue("pppe.isZERO() = ", pppe.isZERO());
        ppp = pppf.random(2);
        ppp = (GenSolvablePolynomial<GenPolynomial<GenPolynomial<BigRational>>>) ppp.subtract(ppp);
        // System.out.println("ppp = " + ppp);
        // System.out.println("ppp.isZERO() = " + ppp.isZERO());
        assertTrue("ppp.isZERO() = ", ppp.isZERO());
    }


    /**
     * Test extension and contraction.
     */
    public void testExtendContract() {
        // rational numbers
        BigRational cf = new BigRational(99);
        // System.out.println("cf = " + cf);

        // polynomials over rational numbers
        GenSolvablePolynomialRing<BigRational> pf = new GenSolvablePolynomialRing<BigRational>(cf, rl);
        // System.out.println("pf = " + pf);

        GenSolvablePolynomial<BigRational> a = pf.random(kl, ll, el, q);
        //System.out.println("a = " + a);

        int k = rl;
        GenSolvablePolynomialRing<BigRational> pfe = pf.extend(k);
        GenSolvablePolynomialRing<BigRational> pfec = pfe.contract(k);
        assertEquals("pf == pfec", pf, pfec);

        GenSolvablePolynomial<BigRational> ae = (GenSolvablePolynomial<BigRational>) a.extend(pfe, 0, 0);

        Map<ExpVector, GenPolynomial<BigRational>> m = ae.contract(pfec);
        List<GenPolynomial<BigRational>> ml = new ArrayList<GenPolynomial<BigRational>>(m.values());
        GenSolvablePolynomial<BigRational> aec = (GenSolvablePolynomial<BigRational>) ml.get(0);
        assertEquals("a == aec", a, aec);
        //System.out.println("ae = " + ae);
        //System.out.println("aec = " + aec);
    }


    /**
     * Test extension and contraction for Weyl relations.
     */
    public void testExtendContractWeyl() {
        // rational numbers
        BigRational cf = new BigRational(99);
        // System.out.println("cf = " + cf);

        // polynomials over rational numbers
        GenSolvablePolynomialRing<BigRational> pf = new GenSolvablePolynomialRing<BigRational>(cf, rl);
        RelationGenerator<BigRational> wl = new WeylRelations<BigRational>();
        //wl.generate(pf);
        pf.addRelations(wl);
        // System.out.println("pf = " + pf);

        GenSolvablePolynomial<BigRational> a = pf.random(kl, ll, el, q);
        //System.out.println("a = " + a);

        int k = rl;
        GenSolvablePolynomialRing<BigRational> pfe = pf.extend(k);
        GenSolvablePolynomialRing<BigRational> pfec = pfe.contract(k);
        assertEquals("pf == pfec", pf, pfec);

        GenSolvablePolynomial<BigRational> ae = (GenSolvablePolynomial<BigRational>) a.extend(pfe, 0, 0);

        Map<ExpVector, GenPolynomial<BigRational>> m = ae.contract(pfec);
        List<GenPolynomial<BigRational>> ml = new ArrayList<GenPolynomial<BigRational>>(m.values());
        GenSolvablePolynomial<BigRational> aec = (GenSolvablePolynomial<BigRational>) ml.get(0);
        assertEquals("a == aec", a, aec);
        //System.out.println("ae = " + ae);
        //System.out.println("aec = " + aec);
    }


    /**
     * Test reversion with rational coefficients.
     */
    public void testReverse() {
        // rational numbers
        BigRational cf = new BigRational(99);
        // System.out.println("cf = " + cf);
        String[] vars = new String[] { "x1", "x2", "x3", "x4", "x5", "x6" };

        // polynomials over rational numbers
        GenSolvablePolynomialRing<BigRational> pf = new GenSolvablePolynomialRing<BigRational>(cf, vars);
        //System.out.println("pf = " + pf.toScript());

        GenSolvablePolynomial<BigRational> a, b, c, d;
        a = pf.random(kl, ll, el, q);
        //System.out.println("a = " + a);

        GenSolvablePolynomialRing<BigRational> pfr = pf.reverse();
        GenSolvablePolynomialRing<BigRational> pfrr = pfr.reverse();
        assertEquals("pf == pfrr", pf, pfrr);
        //System.out.println("pfr = " + pfr);

        GenSolvablePolynomial<BigRational> ar, br, cr;
        ar = (GenSolvablePolynomial<BigRational>) a.reverse(pfr);
        GenSolvablePolynomial<BigRational> arr = (GenSolvablePolynomial<BigRational>) ar.reverse(pfrr);
        assertEquals("a == arr", a, arr);
        //System.out.println("ar = " + ar);
        //System.out.println("arr = " + arr);

        b = pf.random(kl, ll, el, q);
        //System.out.println("b = " + b);
        br = (GenSolvablePolynomial<BigRational>) b.reverse(pfr);
        //System.out.println("br = " + br);

        c = b.multiply(a);
        cr = ar.multiply(br);
        //System.out.println("cr = " + cr);

        d = (GenSolvablePolynomial<BigRational>) cr.reverse(pfrr);
        //System.out.println("d = " + d);
        assertEquals("b*a == rev(a)*rev(b): ", c, d);
    }


    /**
     * Test reversion for Weyl relations.
     */
    public void testReverseWeyl() {
        // rational numbers
        BigRational cf = new BigRational(99);
        // System.out.println("cf = " + cf);
        String[] vars = new String[] { "x1", "x2", "x3", "x4", "x5", "x6" };

        // polynomials over rational numbers
        GenSolvablePolynomialRing<BigRational> pf = new GenSolvablePolynomialRing<BigRational>(cf, vars);
        RelationGenerator<BigRational> wl = new WeylRelations<BigRational>();
        wl.generate(pf);
        //System.out.println("pf = " + pf.toScript());

        GenSolvablePolynomial<BigRational> a, b, c, d;
        a = pf.random(kl, ll, el, q);
        //System.out.println("a = " + a);

        GenSolvablePolynomialRing<BigRational> pfr = pf.reverse();
        GenSolvablePolynomialRing<BigRational> pfrr = pfr.reverse();
        assertEquals("pf == pfrr", pf, pfrr);
        //System.out.println("pfr = " + pfr);

        GenSolvablePolynomial<BigRational> ar, br, cr;
        ar = (GenSolvablePolynomial<BigRational>) a.reverse(pfr);
        GenSolvablePolynomial<BigRational> arr = (GenSolvablePolynomial<BigRational>) ar.reverse(pfrr);
        assertEquals("a == arr", a, arr);
        //System.out.println("ar = " + ar);
        //System.out.println("arr = " + arr);

        b = pf.random(kl, ll, el, q);
        //System.out.println("b = " + b);
        br = (GenSolvablePolynomial<BigRational>) b.reverse(pfr);
        //System.out.println("br = " + br);

        c = b.multiply(a);
        cr = ar.multiply(br);
        //System.out.println("cr = " + cr);

        d = (GenSolvablePolynomial<BigRational>) cr.reverse(pfrr);
        //System.out.println("d = " + d);
        assertEquals("rev(b*a) == rev(a)*rev(b): ", c, d);
    }


    /**
     * Test recursion.
     */
    public void testRecursion() {
        // rational numbers
        BigRational rf = new BigRational();
        // System.out.println("rf = " + rf);

        String[] vars = new String[] { "a", "b", "c", "d" };
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        // polynomials over rational numbers
        GenSolvablePolynomialRing<BigRational> pf = new GenSolvablePolynomialRing<BigRational>(rf, 4, to,
                        vars);
        RelationGenerator<BigRational> wl = new WeylRelations<BigRational>();
        //wl.generate(pf);
        pf.addRelations(wl);
        //System.out.println("pf = " + pf);

        GenSolvablePolynomial<BigRational> sp = pf.random(5);
        //System.out.println("sp = " + sp);
        sp = (GenSolvablePolynomial<BigRational>) sp.subtract(sp);
        assertTrue("sp == 0 ", sp.isZERO());

        // polynomials over (solvable) polynomials over rational numbers
        GenSolvablePolynomialRing<GenPolynomial<BigRational>> rpf = new GenSolvablePolynomialRing<GenPolynomial<BigRational>>(
                        pf, 2);
        RelationGenerator<GenPolynomial<BigRational>> rwl = new WeylRelations<GenPolynomial<BigRational>>();
        //rwl.generate(rpf);
        rpf.addRelations(rwl);
        //System.out.println("rpf = " + rpf);

        GenSolvablePolynomial<GenPolynomial<BigRational>> rsp = rpf.random(5);
        //System.out.println("rsp = " + rsp);
        rsp = (GenSolvablePolynomial<GenPolynomial<BigRational>>) rsp.subtract(rsp);
        assertTrue("rsp == 0 ", rsp.isZERO());
    }


    /**
     * Test reversion with quaternion coefficients.
     */
    public void testReverseQuat() {
        // quaternion numbers
        BigQuaternionRing cf = new BigQuaternionRing();
        // System.out.println("cf = " + cf);
        String[] vars = new String[] { "x1", "x2", "x3", "x4" };

        // polynomials over quaternion numbers
        GenSolvablePolynomialRing<BigQuaternion> pf = new GenSolvablePolynomialRing<BigQuaternion>(cf, vars);
        //System.out.println("pf = " + pf.toScript());
        GenSolvablePolynomialRing<BigQuaternion> pfr;
        try {
            pfr = pf.reverse();
            assertFalse("pf coefficents commuative: " + pf, pf.coFac.isCommutative());
        } catch (IllegalArgumentException e) {
            assertTrue("pf coefficents commuative: " + pf, pf.coFac.isCommutative());
            pfr = null;
        }
        //System.out.println("pf = " + pf.toScript());
        //System.out.println("pfr = " + pfr.toScript());
        GenSolvablePolynomialRing<BigQuaternion> pfrr = pfr.reverse();
        assertEquals("pf == pfrr", pf, pfrr);

        // elements
        BigQuaternion aq, bq, cq, dq;
        aq = cf.random(3);
        bq = cf.random(3);
        //System.out.println("aq = " + aq);
        //System.out.println("bq = " + bq);

        cq = aq.multiply(bq).conjugate();
        dq = bq.conjugate().multiply(aq.conjugate());
        //System.out.println("cq = " + cq);
        //System.out.println("dq = " + dq);
        assertEquals("con(a*b) == con(b)*con(a): ", cq, dq);

        GenSolvablePolynomial<BigQuaternion> a, b, c, d;
        a = pf.random(kl, ll, el, q);
        //System.out.println("a = " + a);

        // coefficients
        GenSolvablePolynomial<BigQuaternion> ar, br, cr;
        ar = (GenSolvablePolynomial<BigQuaternion>) a.reverse(pfr);
        //System.out.println("StarRingElem case: " + a.ring.coFac.getONE().getClass());
        ar = (GenSolvablePolynomial<BigQuaternion>) PolyUtil.<BigQuaternion> conjugateCoeff(ar);

        GenSolvablePolynomial<BigQuaternion> arr = (GenSolvablePolynomial<BigQuaternion>) ar.reverse(pfrr);
        arr = (GenSolvablePolynomial<BigQuaternion>) PolyUtil.<BigQuaternion> conjugateCoeff(arr);
        assertEquals("a == arr", a, arr);
        //System.out.println("ar = " + ar);
        //System.out.println("arr = " + arr);

        b = pf.random(kl, ll, el, q);
        //System.out.println("b = " + b);
        br = (GenSolvablePolynomial<BigQuaternion>) b.reverse(pfr);
        br = (GenSolvablePolynomial<BigQuaternion>) PolyUtil.<BigQuaternion> conjugateCoeff(br);
        //System.out.println("br = " + br);

        c = b.multiply(a);
        cr = ar.multiply(br);
        //System.out.println("cr = " + cr);

        d = (GenSolvablePolynomial<BigQuaternion>) cr.reverse(pfrr);
        d = (GenSolvablePolynomial<BigQuaternion>) PolyUtil.<BigQuaternion> conjugateCoeff(d);
        //System.out.println("d = " + d);
        assertEquals("rev(a*b) == rev(b)*rev(a): ", c, d);
    }


    /**
     * Test reversion with complex coefficients.
     */
    public void testReverseComplex() {
        // complex numbers
        BigComplex cf = new BigComplex();
        // System.out.println("cf = " + cf);
        String[] vars = new String[] { "x1", "x2", "x3", "x4" };

        // polynomials over complex numbers
        GenSolvablePolynomialRing<BigComplex> pf = new GenSolvablePolynomialRing<BigComplex>(cf, vars);
        //System.out.println("pf = " + pf.toScript());

        GenSolvablePolynomialRing<BigComplex> pfr = pf.reverse();
        GenSolvablePolynomialRing<BigComplex> pfrr = pfr.reverse();
        assertEquals("pf == pfrr", pf, pfrr);
        //System.out.println("pfr = " + pfr);

        int kl = 2;
        int ll = 4;
        int el = 3;
        float q = 0.3f;

        GenSolvablePolynomial<BigComplex> a, b, c, d;
        GenSolvablePolynomial<BigComplex> ar, br, cr, dr;

        a = pf.random(kl, ll, el, q);
        //System.out.println("a = " + a);
        ar = (GenSolvablePolynomial<BigComplex>) a.reverse(pfr);
        GenSolvablePolynomial<BigComplex> arr = (GenSolvablePolynomial<BigComplex>) ar.reverse(pfrr);
        assertEquals("a == arr", a, arr);
        //System.out.println("ar = " + ar);
        //System.out.println("arr = " + arr);

        b = pf.random(kl, ll, el, q);
        //System.out.println("b = " + b);
        br = (GenSolvablePolynomial<BigComplex>) b.reverse(pfr);
        //System.out.println("br = " + br);

        c = b.multiply(a);
        //System.out.println("c  = " + c);
        cr = ar.multiply(br);
        //System.out.println("cr = " + cr);

        d = (GenSolvablePolynomial<BigComplex>) cr.reverse(pfrr);
        //System.out.println("d = " + d);
        //System.out.println("c-d = " + c.subtract(d));

        dr = (GenSolvablePolynomial<BigComplex>) c.reverse(pfr);
        //System.out.println("dr = " + dr);
        //System.out.println("cr-dr = " + cr.subtract(dr));

        assertEquals("b*a == rev(a)*rev(b): ", c, d);
        assertEquals("rev(a)*rev(b) = b * a: ", cr, dr);
    }


    /**
     * Test reversion with complex coefficients as Weyl algebra.
     */
    public void testReverseComplexWeyl() {
        // complex numbers
        BigComplex cf = new BigComplex();
        // System.out.println("cf = " + cf);
        String[] vars = new String[] { "x1", "x2", "x3", "x4" };

        // polynomials over complex numbers
        GenSolvablePolynomialRing<BigComplex> pf = new GenSolvablePolynomialRing<BigComplex>(cf, vars);
        RelationGenerator<BigComplex> wl = new WeylRelations<BigComplex>();
        wl.generate(pf);
        //System.out.println("pf = " + pf.toScript());

        GenSolvablePolynomialRing<BigComplex> pfr = pf.reverse();
        //System.out.println("pfr = " + pfr.toScript());
        GenSolvablePolynomialRing<BigComplex> pfrr = pfr.reverse();
        assertEquals("pf == pfrr", pf, pfrr);

        int kl = 2;
        int ll = 4;
        int el = 3;
        float q = 0.3f;

        GenSolvablePolynomial<BigComplex> a, b, c, d;
        GenSolvablePolynomial<BigComplex> ar, br, cr, dr;

        a = pf.random(kl, ll, el, q);
        //System.out.println("a = " + a);
        ar = (GenSolvablePolynomial<BigComplex>) a.reverse(pfr);
        GenSolvablePolynomial<BigComplex> arr = (GenSolvablePolynomial<BigComplex>) ar.reverse(pfrr);
        assertEquals("a == arr", a, arr);
        //System.out.println("ar = " + ar);
        //System.out.println("arr = " + arr);

        b = pf.random(kl, ll, el, q);
        //System.out.println("b = " + b);
        br = (GenSolvablePolynomial<BigComplex>) b.reverse(pfr);
        //System.out.println("br = " + br);

        c = b.multiply(a);
        //System.out.println("c  = " + c);
        cr = ar.multiply(br);
        //System.out.println("cr = " + cr);

        d = (GenSolvablePolynomial<BigComplex>) cr.reverse(pfrr);
        //System.out.println("d = " + d);
        //System.out.println("c-d = " + c.subtract(d));

        dr = (GenSolvablePolynomial<BigComplex>) c.reverse(pfr);
        //System.out.println("dr = " + dr);
        //System.out.println("cr-dr = " + cr.subtract(dr));

        assertEquals("b*a == rev(a)*rev(b): ", c, d);
        assertEquals("rev(a)*rev(b) = b * a: ", cr, dr);
    }


    /**
     * Test reversion with quaternion coefficients and Weyl relations.
     */
    public void testReverseQuatWeyl() {
        // quaternion numbers
        BigQuaternionRing cf = new BigQuaternionRing();
        // System.out.println("cf = " + cf);
        String[] vars = new String[] { "x1", "x2", "x3", "x4" };

        // polynomials over quaternion numbers
        GenSolvablePolynomialRing<BigQuaternion> pf = new GenSolvablePolynomialRing<BigQuaternion>(cf, vars);
        RelationGenerator<BigQuaternion> wl = new WeylRelations<BigQuaternion>();
        wl.generate(pf);
        //System.out.println("pf = " + pf.toScript());
        GenSolvablePolynomialRing<BigQuaternion> pfr;
        try {
            pfr = pf.reverse();
            assertFalse("pf coefficents commuative: " + pf, pf.coFac.isCommutative());
        } catch (IllegalArgumentException e) {
            assertTrue("pf coefficents commuative: " + pf, pf.coFac.isCommutative());
            pfr = null;
        }
        //System.out.println("pf = " + pf.toScript());
        //System.out.println("pfr = " + pfr.toScript());
        GenSolvablePolynomialRing<BigQuaternion> pfrr = pfr.reverse();
        assertEquals("pf == pfrr", pf, pfrr);

        // elements
        BigQuaternion aq, bq, cq, dq;
        aq = cf.random(3);
        bq = cf.random(3);
        //System.out.println("aq = " + aq);
        //System.out.println("bq = " + bq);

        cq = aq.multiply(bq).conjugate();
        dq = bq.conjugate().multiply(aq.conjugate());
        //System.out.println("cq = " + cq);
        //System.out.println("dq = " + dq);
        assertEquals("con(a*b) == con(b)*con(a): ", cq, dq);

        GenSolvablePolynomial<BigQuaternion> a, b, c, d;
        a = pf.random(kl, ll, el, q);
        //System.out.println("a = " + a);

        // coefficients
        GenSolvablePolynomial<BigQuaternion> ar, br, cr;
        ar = (GenSolvablePolynomial<BigQuaternion>) a.reverse(pfr);
        ar = (GenSolvablePolynomial<BigQuaternion>) PolyUtil.<BigQuaternion> conjugateCoeff(ar);

        GenSolvablePolynomial<BigQuaternion> arr = (GenSolvablePolynomial<BigQuaternion>) ar.reverse(pfrr);
        arr = (GenSolvablePolynomial<BigQuaternion>) PolyUtil.<BigQuaternion> conjugateCoeff(arr);
        assertEquals("a == arr", a, arr);
        //System.out.println("ar = " + ar);
        //System.out.println("arr = " + arr);

        b = pf.random(kl, ll, el, q);
        //System.out.println("b = " + b);
        br = (GenSolvablePolynomial<BigQuaternion>) b.reverse(pfr);
        br = (GenSolvablePolynomial<BigQuaternion>) PolyUtil.<BigQuaternion> conjugateCoeff(br);
        //System.out.println("br = " + br);

        c = b.multiply(a);
        cr = ar.multiply(br);
        //System.out.println("cr = " + cr);

        d = (GenSolvablePolynomial<BigQuaternion>) cr.reverse(pfrr);
        d = (GenSolvablePolynomial<BigQuaternion>) PolyUtil.<BigQuaternion> conjugateCoeff(d);
        //System.out.println("d = " + d);
        assertEquals("rev(a*b) == rev(b)*rev(a): ", c, d);
    }

}
