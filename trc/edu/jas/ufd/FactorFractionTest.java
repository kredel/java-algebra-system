/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.SortedMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;


/**
 * Factor fractions (of polynomial quotients) tests with JUnit.
 * @author Heinz Kredel
 */

public class FactorFractionTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>FactorFractionTest</CODE> object.
     * @param name String.
     */
    public FactorFractionTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(FactorFractionTest.class);
        return suite;
    }


    int rl = 1;


    int kl = 3;


    int ll = 4;


    int el = 4;


    float q = 0.5f;


    QuotientRing<BigRational> efac;


    GenPolynomialRing<BigRational> mfac;


    @Override
    protected void setUp() {
        BigRational cfac = new BigRational(1);
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[]{ "z" };
        mfac = new GenPolynomialRing<BigRational>(cfac, rl, to, vars);
        efac = new QuotientRing<BigRational>(mfac);
    }


    @Override
    protected void tearDown() {
        //efac.terminate();
        efac = null;
        ComputerThreads.terminate();
    }


    /**
     * Test quotient coefficient polynomial factorization.
     */
    public void testQuotientFactorization() {
        Quotient<BigRational> a = efac.random(kl, ll, el, q); // will be irreducible most times
        //System.out.println("a      = " + a);
        a = a.power(3);
        Quotient<BigRational> b = efac.random(kl, ll, el, q); // will be irreducible most times
        //System.out.println("b      = " + b);
        Quotient<BigRational> c = a.multiply(b);
        //System.out.println("c      = " + c);

        FactorFraction<BigRational,Quotient<BigRational>> engine = new FactorFraction<BigRational,Quotient<BigRational>>(efac);
        //System.out.println("engine = " + engine);

        SortedMap<Quotient<BigRational>, Long> sm = engine.factors(c);
        //System.out.println("factors(c) = " + sm);
        if (c.isZERO()) {
           assertTrue("#facs == 0", sm.size() == 0);
        } else {
           assertTrue("#facs >= 1", sm.size() >= 1);
        }

        for (Quotient<BigRational> q : sm.keySet()) {
             assertTrue("irred(q): " + q, engine.isIrreducible(q));
        }
        boolean t = engine.isFactorization(c, sm);
        //System.out.println("t        = " + t);
        assertTrue("prod(factor(c)) == c", t);
    }

}
