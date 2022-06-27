/*
 * $Id$
 */

package edu.jas.application;


import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.ps.PolynomialTaylorFunction;
import edu.jas.ps.TaylorFunction;
import edu.jas.ps.UnivPowerSeries;
import edu.jas.ps.UnivPowerSeriesRing;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Univariate power series tests with JUnit.
 * @author Heinz Kredel
 */

public class UnivPowerSeriesTaylorTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>UnivPowerSeriesTaylorTest</CODE> object.
     * @param name String.
     */
    public UnivPowerSeriesTaylorTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(UnivPowerSeriesTaylorTest.class);
        return suite;
    }


    BigRational cfac;


    UnivPowerSeriesRing<BigRational> fac;


    UnivPowerSeries<BigRational> a, b, c, d, e;


    int kl = 10;


    float q = 0.5f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        cfac = new BigRational(1);
        fac = new UnivPowerSeriesRing<BigRational>(cfac);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        cfac = null;
        ComputerThreads.terminate();
    }


    /**
     * Test Taylor series of polynomials.
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


    /**
     * Test Taylor series of quotients of polynomials.
     */
    public void testQuotientTaylor() {
        BigRational br = new BigRational(0);
        GenPolynomialRing<BigRational> pr = fac.polyRing();
        //System.out.println("pr  = " + pr);
        QuotientRing<BigRational> qr = new QuotientRing<BigRational>(pr);
        //System.out.println("qr  = " + qr.toScript());

        Quotient<BigRational> p = qr.random(kl, 3, 3, q + q);
        //System.out.println("p   = " + p);
        //Quotient<BigRational> x = qr.generators().get(1);
        //p = p.divide(x);
        //System.out.println("p   = " + p);
        if (p.den.trailingBaseCoefficient().isZERO()) { // divisible by x, evaluates to 0
            return;
        }
        TaylorFunction<BigRational> F = new QuotientTaylorFunction<BigRational>(p);

        UnivPowerSeries<BigRational> ps = fac.seriesOfTaylor(F, br);
        //System.out.println("ps  = " + ps);
        UnivPowerSeries<BigRational> pps = fac.fromPolynomial(p.num).divide(fac.fromPolynomial(p.den));
        //System.out.println("pps = " + pps);
        assertEquals("taylor(p) == p", ps, pps);

        for (Quotient<BigRational> g : qr.generators()) {
            F = new QuotientTaylorFunction<BigRational>(g);
            ps = fac.seriesOfTaylor(F, br);
            //System.out.println("g   = " + g);
            //System.out.println("ps  = " + ps);
            pps = fac.fromPolynomial(g.num).divide(fac.fromPolynomial(g.den));
            //System.out.println("pps = " + pps);
            assertEquals("taylor(p) == p", ps, pps);
        }
    }

}
