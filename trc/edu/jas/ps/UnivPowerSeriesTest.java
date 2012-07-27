/*
 * $Id$
 */

package edu.jas.ps;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;


/**
 * Univariate power series tests with JUnit.
 * @author Heinz Kredel.
 */

public class UnivPowerSeriesTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>UnivPowerSeriesTest</CODE> object.
     * @param name String.
     */
    public UnivPowerSeriesTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(UnivPowerSeriesTest.class);
        return suite;
    }


    //private final static int bitlen = 100;

    UnivPowerSeriesRing<BigRational> fac;


    UnivPowerSeries<BigRational> a;


    UnivPowerSeries<BigRational> b;


    UnivPowerSeries<BigRational> c;


    UnivPowerSeries<BigRational> d;


    UnivPowerSeries<BigRational> e;


    int kl = 10;


    float q = 0.5f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        fac = new UnivPowerSeriesRing<BigRational>(new BigRational(1));
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
    }


    /**
     * Test constructor and toString.
     * 
     */
    public void testConstruction() {
        c = fac.getONE();
        assertTrue("isZERO( c )", !c.isZERO());
        assertTrue("isONE( c )", c.isONE());

        d = fac.getZERO();
        assertTrue("isZERO( d )", d.isZERO());
        assertTrue("isONE( d )", !d.isONE());
    }


    /**
     * Test random polynomial.
     */
    public void testRandom() {
        for (int i = 0; i < 5; i++) {
            a = fac.random(i + 2);
            //System.out.println("a = " + a);
            assertTrue(" not isZERO( a" + i + " )", !a.isZERO());
            assertTrue(" not isONE( a" + i + " )", !a.isONE());
        }
    }


    /**
     * Test addition.
     * 
     */
    public void testAddition() {
        a = fac.random(kl);
        b = fac.random(kl);

        c = a.sum(b);
        d = b.sum(a);
        assertEquals("a+b = b+a", c, d);

        d = c.subtract(b);
        assertEquals("a+b-b = a", a, d);

        c = fac.random(kl);
        d = a.sum(b.sum(c));
        e = a.sum(b).sum(c);
        assertEquals("a+(b+c) = (a+b)+c", d, e);
    }


    /**
     * Test multiplication.
     * 
     */
    public void testMultiplication() {
        a = fac.random(kl);
        assertTrue("not isZERO( a )", !a.isZERO());

        b = fac.random(kl);
        assertTrue("not isZERO( b )", !b.isZERO());

        c = b.multiply(a);
        d = a.multiply(b);
        assertTrue("not isZERO( c )", !c.isZERO());
        assertTrue("not isZERO( d )", !d.isZERO());

        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        e = d.subtract(c);
        assertTrue("isZERO( a*b-b*a ) " + e, e.isZERO());

        assertTrue("a*b = b*a", c.equals(d));
        assertEquals("a*b = b*a", c, d);

        c = fac.random(kl);
        //System.out.println("c = " + c);
        d = a.multiply(b.multiply(c));
        e = (a.multiply(b)).multiply(c);

        //System.out.println("d = " + d);
        //System.out.println("e = " + e);

        //System.out.println("d-e = " + d.subtract(c) );

        assertEquals("a(bc) = (ab)c", d, e);
        assertTrue("a(bc) = (ab)c", d.equals(e));
    }


    /**
     * Test distributive law.
     * 
     */
    public void testDistributive() {
        a = fac.random(kl, q);
        b = fac.random(kl, q);
        c = fac.random(kl, q);

        d = a.multiply(b.sum(c));
        e = a.multiply(b).sum(a.multiply(c));

        assertEquals("a(b+c) = ab+ac", d, e);
    }


    /**
     * Test object quotient and remainder.
     * 
     */
    public void testQuotRem() {
        fac = new UnivPowerSeriesRing<BigRational>(new BigRational(1));

        a = fac.random(kl);
        assertTrue("not isZERO( a )", !a.isZERO());

        b = fac.random(kl);
        assertTrue("not isZERO( b )", !b.isZERO());

        UnivPowerSeries<BigRational> g = fac.random(kl);
        assertTrue("not isZERO( g )", !g.isZERO());
        a = a.multiply(g);
        b = b.multiply(g);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("g = " + g);

        /*
        UnivPowerSeries<BigRational>[] qr;
        qr = b.divideAndRemainder(a);
        c = qr[0];
        d = qr[1];
        //System.out.println("q = " + c);
        //System.out.println("r = " + d);
        e = c.multiply(a).sum(d);
        assertEquals("b = q a + r", b, e );
        */

        // gcd tests -------------------------------
        c = a.gcd(b);
        //System.out.println("gcd = " + c);
        assertTrue("a mod gcd(a,b) = 0", a.remainder(c).isZERO());
        assertTrue("b mod gcd(a,b) = 0", b.remainder(c).isZERO());
        //assertEquals("g = gcd(a,b)", c, g );
    }


    /**
     * Test evaluation.
     * 
     */
    public void testEvaluation() {
        a = fac.random(kl, q);
        b = fac.random(kl, q);
        BigRational fv = new BigRational(0);
        BigRational v = fv.random(kl);

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

        // not true: 
        //c = a.multiply(b);
        //cv = c.evaluate(v);
        //dv = av.multiply(bv);
        //assertEquals("a(v)*b(v) = (a*b)(v) ", cv, dv);
    }


    /**
     * Test Taylor series.
     * 
     */
    public void testTaylor() {
        BigRational br = new BigRational(0);
        GenPolynomialRing<BigRational> pr = fac.polyRing();
        //System.out.println("pr  = " + pr);

        GenPolynomial<BigRational> p = pr.random(kl, 3, 3, q + q);
        //System.out.println("p   = " + p);

        TaylorFunction<BigRational> F = new PolynomialTaylorFunction<BigRational>(p);

        UnivPowerSeries<BigRational> ps = fac.seriesOfTaylor(F, br);
        //System.out.println("ps  = " + ps);
        UnivPowerSeries<BigRational> pps = fac.fromPolynomial(p);
        //System.out.println("pps = " + pps);
        assertEquals("taylor(p) == p", ps, pps);

        for (GenPolynomial<BigRational> g : pr.generators()) {
            F = new PolynomialTaylorFunction<BigRational>(g);
            ps = fac.seriesOfTaylor(F, br);
            //System.out.println("g   = " + g);
            //System.out.println("ps  = " + ps);
            pps = fac.fromPolynomial(g);
            //System.out.println("pps = " + pps);
            assertEquals("taylor(p) == p", ps, pps);
        }
    }
}
