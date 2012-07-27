/*
 * $Id$
 */

package edu.jas.gbufd;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.PrimeList;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.ufd.GCDProxy;
import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.GreatestCommonDivisorModEval;
import edu.jas.ufd.GreatestCommonDivisorModular;
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
        ModIntegerRing mi;

        PrimeList primes = new PrimeList();
        mi = new ModIntegerRing(primes.get(1)); // 17, 19, 23, 41, 163, 
        dfac = new GenPolynomialRing<ModInteger>(mi, rl, to);
        //System.out.println("dfac = " + dfac);

        GreatestCommonDivisorAbstract<ModInteger> ufds = new GreatestCommonDivisorSimple<ModInteger>();
        GreatestCommonDivisorAbstract<ModInteger> sres = new GreatestCommonDivisorSubres<ModInteger>();
        GreatestCommonDivisorAbstract<ModInteger> ufdm = new GreatestCommonDivisorModEval<ModInteger>();
        GenPolynomial<ModInteger> a, b, c, d, e;

        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl, ll, el, q);
            b = dfac.random(kl, ll + 2, el, q);
            //a = dfac.parse("6 x0^4 - 17");
            //b = dfac.parse("6 x1^2 - 7 x0^2 - 5 x1 - 14");
            //a = dfac.parse("5 x1^4 * x2^4 + 2 x1^2 + x0^2");
            //b = dfac.parse("5 x0^2 * x1^2 + 2 x2^2 + 5 x2 + 15");
            //a = dfac.parse("x0^3 * x2^2 + 6 x0^4 + 6 x0 + 7");
            //b = dfac.parse("7 x0^2 * x2^2 + 3 x2^2 + 4 x1^2 * x2 + 4 x2 + 4 x1^2 + x1 + 7");
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);

            c = ufds.resultant(a, b);
            d = sres.resultant(a, b);
            e = ufdm.resultant(a, b);

            boolean t1 = PolyGBUtil.<ModInteger> isResultant(a, b, c);
            //System.out.println("t1 = " + t1);
            boolean t2 = PolyGBUtil.<ModInteger> isResultant(a, b, d);
            //System.out.println("t2 = " + t2);
            boolean t3 = PolyGBUtil.<ModInteger> isResultant(a, b, e);
            //System.out.println("t3 = " + t3);

            //System.out.println("c = " + c);
            //System.out.println("d = " + d);
            //System.out.println("e = " + e);

            assertTrue("isResultant(a,b,c): " + c, t1);
            assertTrue("isResultant(a,b,d): " + d, t2);
            assertTrue("isResultant(a,b,e): " + e, t3);
        }
    }


    /**
     * Test resultant integer.
     */
    public void testResultantInteger() {
        GenPolynomialRing<BigInteger> dfac;
        dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), rl, to);
        //System.out.println("dfac = " + dfac);

        GreatestCommonDivisorAbstract<BigInteger> ufds = new GreatestCommonDivisorSimple<BigInteger>();
        GreatestCommonDivisorAbstract<BigInteger> sres = new GreatestCommonDivisorSubres<BigInteger>();
        GreatestCommonDivisorAbstract<BigInteger> ufdm = new GreatestCommonDivisorModular<ModInteger>(); //true);
        GenPolynomial<BigInteger> a, b, c, d, e;

        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl, ll, el, q);
            b = dfac.random(kl, ll + 2, el, q);
            //a = dfac.parse("6 x0^4 - 17");
            //b = dfac.parse("6 x1^2 - 7 x0^2 - 5 x1 - 14");
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);

            c = ufds.resultant(a, b);
            d = sres.resultant(a, b);
            e = ufdm.resultant(a, b);

            boolean t1 = PolyGBUtil.<BigInteger> isResultant(a, b, c);
            //System.out.println("t1 = " + t1);
            boolean t2 = PolyGBUtil.<BigInteger> isResultant(a, b, d);
            //System.out.println("t2 = " + t2);
            boolean t3 = PolyGBUtil.<BigInteger> isResultant(a, b, e);
            //System.out.println("t3 = " + t3);

            //System.out.println("c = " + c);
            //System.out.println("d = " + d);
            //System.out.println("e = " + e);

            assertTrue("isResultant(a,b,d): " + d, t2);
            assertTrue("isResultant(a,b,e): " + e, t3);
            assertTrue("isResultant(a,b,c): " + c, t1);
        }
    }


    /**
     * Test resultant modular parallel proxy.
     */
    public void testResultantModularParallel() {
        GenPolynomialRing<ModInteger> dfac;
        ModIntegerRing mi;

        PrimeList primes = new PrimeList();
        mi = new ModIntegerRing(primes.get(1)); // 17, 19, 23, 41, 163, 
        dfac = new GenPolynomialRing<ModInteger>(mi, rl, to);
        //System.out.println("dfac = " + dfac);

        GreatestCommonDivisorAbstract<ModInteger> ufds = new GreatestCommonDivisorSimple<ModInteger>();
        GreatestCommonDivisorAbstract<ModInteger> sres = new GreatestCommonDivisorSubres<ModInteger>();
        GreatestCommonDivisorAbstract<ModInteger> ufdm = new GreatestCommonDivisorModEval<ModInteger>();

        GreatestCommonDivisorAbstract<ModInteger> pufds = new GCDProxy<ModInteger>(sres, ufds);
        GreatestCommonDivisorAbstract<ModInteger> pufdm = new GCDProxy<ModInteger>(ufdm, sres);

        GenPolynomial<ModInteger> a, b, c, d;

        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl, ll, el, q);
            b = dfac.random(kl, ll + 2, el, q);
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);

            c = pufds.resultant(a, b);
            d = pufdm.resultant(a, b);

            boolean t1 = PolyGBUtil.<ModInteger> isResultant(a, b, c);
            //System.out.println("t1 = " + t1);
            boolean t2 = PolyGBUtil.<ModInteger> isResultant(a, b, d);
            //System.out.println("t2 = " + t2);

            //System.out.println("c = " + c);
            //System.out.println("d = " + d);

            assertTrue("isResultant(a,b,c): " + c, t1);
            assertTrue("isResultant(a,b,d): " + d, t2);
        }
    }


    /**
     * Test resultant integer parallel proxy.
     */
    public void testResultantIntegerProxy() {
        GenPolynomialRing<BigInteger> dfac;
        dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), rl, to);
        //System.out.println("dfac = " + dfac);

        GreatestCommonDivisorAbstract<BigInteger> ufds = new GreatestCommonDivisorSimple<BigInteger>();
        GreatestCommonDivisorAbstract<BigInteger> sres = new GreatestCommonDivisorSubres<BigInteger>();
        GreatestCommonDivisorAbstract<BigInteger> ufdm = new GreatestCommonDivisorModular<ModInteger>(); //true);

        GreatestCommonDivisorAbstract<BigInteger> pufds = new GCDProxy<BigInteger>(sres, ufds);
        GreatestCommonDivisorAbstract<BigInteger> pufdm = new GCDProxy<BigInteger>(ufdm, sres);

        GenPolynomial<BigInteger> a, b, c, d;

        for (int i = 0; i < 1; i++) {
            a = dfac.random(kl, ll, el, q);
            b = dfac.random(kl, ll + 1, el, q);
            //System.out.println("a = " + a);
            //System.out.println("b = " + b);

            c = pufds.resultant(a, b);
            d = pufdm.resultant(a, b);

            boolean t1 = PolyGBUtil.<BigInteger> isResultant(a, b, c);
            //System.out.println("t1 = " + t1);
            boolean t2 = PolyGBUtil.<BigInteger> isResultant(a, b, d);
            //System.out.println("t2 = " + t2);

            //System.out.println("c = " + c);
            //System.out.println("d = " + d);

            assertTrue("isResultant(a,b,d): " + d, t2);
            assertTrue("isResultant(a,b,c): " + c, t1);
        }
    }


    /**
     * Test coefficient base pseudo remainder.
     */
    public void testCoefficientBasePseudoRemainder() {
        GenPolynomialRing<BigRational> dfac;
        BigRational br = new BigRational();
        to = new TermOrder(TermOrder.INVLEX);
        //String[] vars = new String[] { "x1",  "x2", "x3" };
        String[] vars = new String[] { "x1", "x2" };
        dfac = new GenPolynomialRing<BigRational>(br, to, vars);
        //System.out.println("dfac = " + dfac);
        GenPolynomialRing<GenPolynomial<BigRational>> rfac = dfac.recursive(1);
        //System.out.println("rfac = " + rfac);
        GenPolynomialRing<BigRational> cfac = (GenPolynomialRing<BigRational>) rfac.coFac;
        //System.out.println("cfac = " + cfac);
        GenPolynomial<GenPolynomial<BigRational>> ar, cr, dr, er;
        GenPolynomial<BigRational> b;

        ar = rfac.random(kl, ll, el, q * 1.1f);
        b = cfac.random(kl, ll + 2, el * 2, q);
        //System.out.println("ar = " + ar);
        //System.out.println("b  = " + b);

        cr = PolyGBUtil.<BigRational> coefficientPseudoRemainderBase(ar, b);
        //System.out.println("cr = " + cr);
        assertTrue("deg(c) < deg(a): ", cr.degree(0) <= ar.degree(0) || ar.degree(0) == 0);
        assertTrue("deg(lfcd(c)) < deg(b): ",
                        cr.leadingBaseCoefficient().degree(0) < b.degree(0) || b.degree(0) == 0);

        dr = ar.multiply(b);
        //System.out.println("dr = " + dr);
        cr = PolyGBUtil.<BigRational> coefficientPseudoRemainderBase(dr, b);
        //System.out.println("cr = " + cr);
        assertTrue("c == 0: ", cr.isZERO());

        long s = ar.degree(0);
        er = rfac.univariate(0, s + 1);
        //System.out.println("er = " + er);
        er = er.multiply(b.multiply(b));
        er = er.sum(ar);
        //System.out.println("er = " + er);
        cr = PolyGBUtil.<BigRational> coefficientPseudoRemainderBase(er, b);
        //System.out.println("cr = " + cr);
        assertTrue("deg(c) < deg(a): ", cr.degree(0) < er.degree(0));
    }


    /**
     * Test coefficient recursive pseudo remainder.
     */
    public void testCoefficientRecursivePseudoRemainder() {
        GenPolynomialRing<BigRational> dfac;
        BigRational br = new BigRational();
        to = new TermOrder(TermOrder.INVLEX);
        String[] vars = new String[] { "x1", "x2", "x3" };
        //String[] vars = new String[] { "x1",  "x2" };
        dfac = new GenPolynomialRing<BigRational>(br, to, vars);
        //System.out.println("dfac = " + dfac);
        GenPolynomialRing<GenPolynomial<BigRational>> r1fac = dfac.recursive(2);
        //System.out.println("r1fac = " + r1fac);
        GenPolynomialRing<GenPolynomial<GenPolynomial<BigRational>>> rfac = r1fac.recursive(1);
        //System.out.println("rfac = " + rfac);
        GenPolynomialRing<GenPolynomial<BigRational>> cfac = (GenPolynomialRing<GenPolynomial<BigRational>>) rfac.coFac;
        //System.out.println("cfac = " + cfac);
        GenPolynomial<GenPolynomial<GenPolynomial<BigRational>>> ar, cr, dr, er;
        GenPolynomial<GenPolynomial<BigRational>> b;

        ar = rfac.random(kl, ll, el, q);
        b = cfac.random(kl, ll + 2, el, q);
        //System.out.println("ar = " + ar);
        //System.out.println("b  = " + b);

        cr = PolyGBUtil.<BigRational> coefficientPseudoRemainder(ar, b);
        //System.out.println("cr = " + cr);
        assertTrue("deg(c) < deg(a): ", cr.degree(0) <= ar.degree(0) || ar.degree(0) == 0);
        assertTrue("deg(lfcd(c)) < deg(b): ",
                        cr.leadingBaseCoefficient().degree(0) < b.degree(0) || b.degree(0) == 0);

        dr = ar.multiply(b);
        //System.out.println("dr = " + dr);
        cr = PolyGBUtil.<BigRational> coefficientPseudoRemainder(dr, b);
        //System.out.println("cr = " + cr);
        assertTrue("c == 0: ", cr.isZERO());

        long s = ar.degree(0);
        er = rfac.univariate(0, s + 1);
        ////System.out.println("er = " + er);
        er = er.multiply(b.multiply(cfac.fromInteger(100)));
        er = er.sum(ar);
        //System.out.println("er = " + er);
        cr = PolyGBUtil.<BigRational> coefficientPseudoRemainder(er, b);
        //System.out.println("cr = " + cr);
        assertTrue("deg(c) < deg(a): ", cr.degree(0) < er.degree(0));
    }

}
