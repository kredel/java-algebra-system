/*
 * $Id$
 */

package edu.jas.ps;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import edu.jas.arith.BigRational;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Standard base of multivariate power series tests with JUnit.
 * @author Heinz Kredel
 */
public class STDMultiPSTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>STDMultiPSTest</CODE> object.
     * @param name String.
     */
    public STDMultiPSTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(STDMultiPSTest.class);
        return suite;
    }


    BigRational cfac;


    MultiVarPowerSeriesRing<BigRational> fac;


    MultiVarPowerSeries<BigRational> a, b, c, d, e, f;


    int rl = 3;


    int kl = 5;


    float q = 0.3f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        String[] vars = new String[] { "x", "y", "z" };
        cfac = new BigRational(1);
        fac = new MultiVarPowerSeriesRing<BigRational>(cfac, rl, vars);
        //System.out.println("fac = " + fac);
        //System.out.println("fac = " + fac.toScript());
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
    }


    /**
     * Test fix point constructions.
     */
    public void testFixpoints() {
        int r = 0;
        UnivPowerSeriesRing<BigRational> ufac = new UnivPowerSeriesRing<BigRational>(fac.coFac, fac.vars[r]);
        UnivPowerSeries<BigRational> exp = ufac.getEXP();
        //System.out.println("exp = " + exp);

        a = fac.fromPowerSeries(exp, 0);
        b = fac.fromPowerSeries(exp, 1);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        c = fac.getEXP(0);
        d = fac.getEXP(1);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a == c ", a, c);
        assertEquals("b == d ", b, d);

        e = d.differentiate(0);
        //System.out.println("e = " + e);
        assertTrue("isZERO( e )", e.isZERO());
        e = d.differentiate(1);
        //System.out.println("e = " + e);
        assertEquals("e == d ", d, e);
    }


    /**
     * Test gcd.
     */
    public void testGcd() {
        a = fac.random(kl);
        //System.out.println("a = " + a);

        b = fac.random(kl);
        //System.out.println("b = " + b);

        c = a.gcd(b);
        //System.out.println("c = " + c);

        d = a.divide(c);
        //System.out.println("d = " + d);

        e = b.divide(c);
        //System.out.println("e = " + e);

        f = d.gcd(e);
        //System.out.println("f = " + f);
        assertTrue("gcd(a/gcd(a,b),b/gcd(a,b)) == 1 ", f.isONE());
    }


    /**
     * Test Taylor series.
     */
    public void testTaylor() {
        BigRational ar = new BigRational(5);
        BigRational br = new BigRational(0);
        BigRational cr = new BigRational(-5);
        List<BigRational> Ar = new ArrayList<BigRational>(rl);
        List<BigRational> Br = new ArrayList<BigRational>(rl);
        List<BigRational> Cr = new ArrayList<BigRational>(rl);
        for (int i = 0; i < rl; i++) {
            Ar.add(ar);
            Br.add(br);
            Cr.add(cr);
        }
        GenPolynomialRing<BigRational> pr = fac.polyRing();
        //System.out.println("pr  = " + pr.toScript());

        GenPolynomial<BigRational> p = pr.random(kl, 3, 3, q + q);
        //System.out.println("p   = " + p);
        int tdeg = (int) p.degree();
        fac.setTruncate(tdeg + 1);

        TaylorFunction<BigRational> F = new PolynomialTaylorFunction<BigRational>(p);

        MultiVarPowerSeries<BigRational> pps = fac.fromPolynomial(p);
        //System.out.println("pps = " + pps);
        MultiVarPowerSeries<BigRational> ps = fac.seriesOfTaylor(F, Br);
        //System.out.println("ps  = " + ps);
        assertEquals("taylor(p) == p", ps, pps);

        MultiVarPowerSeries<BigRational> psa = fac.seriesOfTaylor(F, Ar);
        //System.out.println("psa  = " + psa);
        F = new PolynomialTaylorFunction<BigRational>(psa.asPolynomial());
        MultiVarPowerSeries<BigRational> psc = fac.seriesOfTaylor(F, Cr);
        //System.out.println("psc  = " + psc);
        assertEquals("taylor(taylor(p,5),-5) == p", ps, psc);

        for (GenPolynomial<BigRational> g : pr.generators()) {
            F = new PolynomialTaylorFunction<BigRational>(g);
            ps = fac.seriesOfTaylor(F, Br);
            //System.out.println("g   = " + g);
            //System.out.println("ps  = " + ps);
            pps = fac.fromPolynomial(g);
            //System.out.println("pps = " + pps);
            assertEquals("taylor(p) == p", ps, pps);

            psa = fac.seriesOfTaylor(F, Ar);
            //System.out.println("psa  = " + psa);
            F = new PolynomialTaylorFunction<BigRational>(psa.asPolynomial());
            psc = fac.seriesOfTaylor(F, Cr);
            //System.out.println("psc  = " + psc);
            assertEquals("taylor(taylor(p,5),-5) == p", ps, psc);
        }
    }


    /**
     * Test evaluation.
     */
    public void testEvaluation() {
        a = fac.random(kl, q);
        b = fac.random(kl, q);
        BigRational fv = new BigRational(0);
        List<BigRational> v = new ArrayList<BigRational>(rl);
        for (int i = 0; i < rl; i++) {
            v.add(fv.random(kl));
        }

        BigRational av = a.evaluate(v);
        BigRational bv = b.evaluate(v);

        c = a.sum(b);
        BigRational cv = c.evaluate(v);
        BigRational dv = av.sum(bv);

        assertEquals("a(v)+b(v) = (a+b)(v) ", cv, dv);

        c = fac.getZERO();
        cv = c.evaluate(v);
        dv = fv.getZERO();
        assertEquals("0(v) = 0 ", cv, dv);

        c = fac.getONE();
        cv = c.evaluate(v);
        dv = fv.getONE();
        assertEquals("1(v) = 1 ", cv, dv);
    }


    /**
     * Test standard base.
     * Example from CLO(UAG), 4.4.
     */
    public void testSTD() {
        GenPolynomialRing<BigRational> pfac = fac.polyRing();
        //System.out.println("pfac  = " + pfac.toScript());

        GenPolynomial<BigRational> ap, bp, cp;
        ap = pfac.parse("x**5 - x * y**6 - z**7");
        //System.out.println("ap = " + ap);
        bp = pfac.parse("x * y + y**3 + z**3");
        //System.out.println("bp = " + bp);
        cp = pfac.parse("x**2 + y**2 - z**2");
        //System.out.println("cp = " + cp);

        a = fac.fromPolynomial(ap);
        //System.out.println("a = " + a);
        b = fac.fromPolynomial(bp);
        //System.out.println("b = " + b);
        c = fac.fromPolynomial(cp);
        //System.out.println("c = " + c);

        List<MultiVarPowerSeries<BigRational>> L, S;
        L = new ArrayList<MultiVarPowerSeries<BigRational>>();
        L.add(a);
        L.add(b);
        L.add(c);
        //System.out.println("L = " + L);

        StandardBaseSeq<BigRational> tm = new StandardBaseSeq<BigRational>();
        //assertFalse("isSTD(L): ", tm.isSTD(L));
        S = tm.STD(L);
        //System.out.println("S = " + S);
        assertTrue("isSTD(S): ", tm.isSTD(S));
    }

}
