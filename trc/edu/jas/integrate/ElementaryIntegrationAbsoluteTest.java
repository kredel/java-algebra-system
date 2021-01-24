/*
 * $Id$
 */

package edu.jas.integrate;


import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Elementary integration Bernoulli algorithm with linear factors with JUnit.
 * @author Heinz Kredel
 */

public class ElementaryIntegrationAbsoluteTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>ElementaryIntegrationAbsoluteTest</CODE> object.
     * @param name String.
     */
    public ElementaryIntegrationAbsoluteTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(ElementaryIntegrationAbsoluteTest.class);
        return suite;
    }


    TermOrder tord;


    QuotientRing<BigRational> qfac;


    GenPolynomialRing<BigRational> pfac;


    ElementaryIntegration<BigRational> integrator;


    QuotIntegral<BigRational> rint;


    @Override
    protected void setUp() {
        tord = new TermOrder(TermOrder.INVLEX);
        BigRational br = new BigRational(1);
        String[] vars = new String[] { "x" };
        pfac = new GenPolynomialRing<BigRational>(br, 1, tord, vars);
        qfac = new QuotientRing<BigRational>(pfac);
        integrator = new ElementaryIntegrationBernoulli<BigRational>(br);
    }


    @Override
    protected void tearDown() {
        ComputerThreads.terminate();
    }


    /**
     * Test Bernoulli algorithm.
     */
    public void testRationalBernoulli() {
        GenPolynomial<BigRational> agen = pfac.univariate(0, 4);
        agen = agen.sum(pfac.fromInteger(4)); // x^4 + 4

        // GenPolynomial<BigRational> x6 = pfac.univariate(0, 6);
        // GenPolynomial<BigRational> x4 = pfac.univariate(0, 4);
        // GenPolynomial<BigRational> x2 = pfac.univariate(0, 2);
        // // x^6 - 5 x^4 + 5 x^2 + 4
        // agen = x6.subtract(x4.multiply(pfac.fromInteger(5))); 
        // agen = agen.sum(x2.multiply(pfac.fromInteger(5))); 
        // agen = agen.sum(pfac.fromInteger(4)); 

        // GenPolynomial<BigRational> x3 = pfac.univariate(0, 3);
        // GenPolynomial<BigRational> x = pfac.univariate(0);
        // // x^3 + x
        // agen = x3.sum(x); 

        // GenPolynomial<BigRational> x2 = pfac.univariate(0, 2);
        // // x^2 - 2
        // agen = x2.subtract(pfac.fromInteger(2));

        GenPolynomial<BigRational> N = pfac.getONE();
        Quotient<BigRational> Q = new Quotient<BigRational>(qfac, N, agen);

        rint = integrator.integrate(Q);
        //System.out.println("\nquot integral: " + rint.toString());
        assertTrue("isIntegral ", integrator.isIntegral(rint));
    }

}
