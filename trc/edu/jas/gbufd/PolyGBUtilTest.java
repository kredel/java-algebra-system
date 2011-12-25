/*
 * $Id$
 */

package edu.jas.gbufd;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.ufd.GreatestCommonDivisorSimple;
import edu.jas.ufd.GreatestCommonDivisorSubres;


/**
 * PolyGBUtil tests with JUnit.
 * @author Heinz Kredel.
 */

public class PolyGBUtilTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>PolyUtilTest</CODE> object.
     * @param name String.
     */
    public PolyGBUtilTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(PolyGBUtilTest.class);
        return suite;
    }


    //TermOrder to = new TermOrder(TermOrder.INVLEX);
    TermOrder to = new TermOrder(TermOrder.IGRLEX);


    GenPolynomialRing<ModInteger> dfac;


    GenPolynomialRing<ModInteger> cfac;


    GenPolynomialRing<GenPolynomial<ModInteger>> rfac;


    ModInteger ai;


    ModInteger bi;


    ModInteger ci;


    ModInteger di;


    ModInteger ei;


    GenPolynomial<ModInteger> a;


    GenPolynomial<ModInteger> b;


    GenPolynomial<ModInteger> c;


    GenPolynomial<ModInteger> d;


    GenPolynomial<ModInteger> e;


    int rl = 2;


    int kl = 3;


    int ll = 4;


    int el = 2;


    float q = 0.19f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        dfac = new GenPolynomialRing<ModInteger>(new ModIntegerRing(19), rl, to);
        cfac = new GenPolynomialRing<ModInteger>(new ModIntegerRing(19), rl - 1, to);
        rfac = new GenPolynomialRing<GenPolynomial<ModInteger>>(cfac, 1, to);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        dfac = null;
        cfac = null;
        rfac = null;
    }


    /**
     * Test resultant.
     * 
     */
    public void testResultant() {
        System.out.println("dfac = " + dfac);
        GreatestCommonDivisorSimple<ModInteger> ufds = new GreatestCommonDivisorSimple<ModInteger>();
        GreatestCommonDivisorSubres<ModInteger> sres = new GreatestCommonDivisorSubres<ModInteger>();

        for (int i = 0; i < 2; i++) {
            a = dfac.random(kl, ll * 1, el * 2, q);
            b = dfac.random(kl, ll * 2, el * 1, q);
            System.out.println("a = " + a);
            System.out.println("b = " + b);

            c = ufds.resultant(a,b);
            System.out.println("c = " + c);

            d = sres.resultant(a,b);
            System.out.println("d = " + d);

            boolean t1 = PolyGBUtil.<ModInteger> isResultant(a,b,c);
            System.out.println("t1 = " + t1);
            boolean t2 = PolyGBUtil.<ModInteger> isResultant(a,b,d);
            System.out.println("t2 = " + t2);

            assertTrue("isResultant(a,b,d): " + d, t2 );
            assertTrue("isResultant(a,b,c): " + c, t1 );
        }
    }

}
