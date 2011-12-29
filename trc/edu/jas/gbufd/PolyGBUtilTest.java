/*
 * $Id$
 */

package edu.jas.gbufd;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.PrimeList;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.ufd.GreatestCommonDivisorSimple;
import edu.jas.ufd.GreatestCommonDivisorSubres;
import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.GreatestCommonDivisorModular;
import edu.jas.ufd.GreatestCommonDivisorModEval;


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


    TermOrder to = new TermOrder(TermOrder.INVLEX);
    //TermOrder to = new TermOrder(TermOrder.IGRLEX);


    int rl = 3;


    int kl = 3;


    int ll = 4;


    int el = 3;


    float q = 0.29f;


    @Override
    protected void setUp() {
    }


    @Override
    protected void tearDown() {
    }


    /**
     * Test resultant modular.
     */
    public void testResultantModular() {
        GenPolynomialRing<ModInteger> dfac;
        //GenPolynomialRing<ModInteger> cfac;
        //GenPolynomialRing<GenPolynomial<ModInteger>> rfac;
        ModIntegerRing mi;

        PrimeList primes = new PrimeList();
        mi = new ModIntegerRing(primes.get(1)); // 17, 19, 23, 41, 163, 
        dfac = new GenPolynomialRing<ModInteger>(mi, rl, to);
        //cfac = new GenPolynomialRing<ModInteger>(mi, rl - 1, to);
        //rfac = new GenPolynomialRing<GenPolynomial<ModInteger>>(cfac, 1, to);

        System.out.println("dfac = " + dfac);
        GreatestCommonDivisorAbstract<ModInteger> ufds = new GreatestCommonDivisorSimple<ModInteger>();
        GreatestCommonDivisorAbstract<ModInteger> sres = new GreatestCommonDivisorSubres<ModInteger>();
        GreatestCommonDivisorAbstract<ModInteger> ufdm = new GreatestCommonDivisorModEval<ModInteger>();
        GenPolynomial<ModInteger> a, b, c, d, e;

        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl, ll * 1, el * 2, q);
            b = dfac.random(kl, ll * 2, el * 1, q);
            //a = dfac.parse("6 x0^4 - 17");
            //b = dfac.parse("6 x1^2 - 7 x0^2 - 5 x1 - 14");
            //a = dfac.parse("5 x1^4 * x2^4 + 2 x1^2 + x0^2");
            //b = dfac.parse("5 x0^2 * x1^2 + 2 x2^2 + 5 x2 + 15");

            System.out.println("a = " + a);
            System.out.println("b = " + b);

            c = ufds.resultant(a, b);

            d = sres.resultant(a, b);

            e = ufdm.resultant(a, b);
            System.out.println("c = " + c);
            System.out.println("d = " + d);
            System.out.println("e = " + e);

            boolean t1 = PolyGBUtil.<ModInteger> isResultant(a, b, c);
            System.out.println("t1 = " + t1);
            boolean t2 = PolyGBUtil.<ModInteger> isResultant(a, b, d);
            System.out.println("t2 = " + t2);
            boolean t3 = PolyGBUtil.<ModInteger> isResultant(a, b, e);
            System.out.println("t3 = " + t3);

            assertTrue("isResultant(a,b,c): " + c, t1);
            assertTrue("isResultant(a,b,d): " + d, t2);
            assertTrue("isResultant(a,b,e): " + e, t3);
        }
    }


    /**
     * Test resultant integer.
     */
    public void xtestResultantInteger() {
        GenPolynomialRing<BigInteger> dfac;
        GenPolynomialRing<BigInteger> cfac;
        GenPolynomialRing<GenPolynomial<BigInteger>> rfac;

        dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), rl, to);
        cfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), rl - 1, to);
        rfac = new GenPolynomialRing<GenPolynomial<BigInteger>>(cfac, 1, to);

        System.out.println("dfac = " + dfac);
        GreatestCommonDivisorAbstract<BigInteger> ufds = new GreatestCommonDivisorSimple<BigInteger>();
        GreatestCommonDivisorAbstract<BigInteger> sres = new GreatestCommonDivisorSubres<BigInteger>();
        GreatestCommonDivisorAbstract<BigInteger> ufdm = new GreatestCommonDivisorModular<ModInteger>(); //true);
        GenPolynomial<BigInteger> a, b, c, d, e;

        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl, ll * 1, el * 2, q);
            b = dfac.random(kl, ll * 2, el * 1, q);

            a = dfac.parse("6 x0^4 - 17");
            b = dfac.parse("6 x1^2 - 7 x0^2 - 5 x1 - 14");

            System.out.println("a = " + a);
            System.out.println("b = " + b);

            c = ufds.resultant(a, b);
            System.out.println("c = " + c);

            d = sres.resultant(a, b);
            System.out.println("d = " + d);

            e = ufdm.resultant(a, b);
            System.out.println("e = " + e);

            boolean t1 = PolyGBUtil.<BigInteger> isResultant(a, b, c);
            System.out.println("t1 = " + t1);
            boolean t2 = PolyGBUtil.<BigInteger> isResultant(a, b, d);
            System.out.println("t2 = " + t2);
            boolean t3 = PolyGBUtil.<BigInteger> isResultant(a, b, e);
            System.out.println("t3 = " + t3);

            assertTrue("isResultant(a,b,d): " + d, t2);
            assertTrue("isResultant(a,b,e): " + e, t3);
            assertTrue("isResultant(a,b,c): " + c, t1);
        }
    }

}
