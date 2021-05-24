/*
 * $Id$
 */

package edu.jas.gbufd;


import java.util.ArrayList;
import java.util.List;

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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * PolyGBUtil tests with JUnit.
 * @author Heinz Kredel
 */
public class PolyGBUtilTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
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


    /**
     * Test sub ring membership.
     */
    public void testSubRing() {
        GenPolynomialRing<BigRational> dfac;
        dfac = new GenPolynomialRing<BigRational>(new BigRational(1), rl, to);
        //System.out.println("dfac = " + dfac);
        GenPolynomial<BigRational> a, b, c, d;
        a = dfac.random(kl, ll, el, q).abs();
        b = dfac.random(kl, ll, el, q).abs();
        d = a.multiply(b).sum(b);
        List<GenPolynomial<BigRational>> sr = new ArrayList<GenPolynomial<BigRational>>(3);
        sr.add(a);
        sr.add(b);
        //System.out.println("sr  = " + sr);
        List<GenPolynomial<BigRational>> srg = PolyGBUtil.<BigRational> subRing(sr);
        //System.out.println("srg = " + srg);
        boolean t = PolyGBUtil.<BigRational> subRingMember(srg, d);
        assertTrue("d in SR: ", t);

        c = dfac.random(kl, ll, el + el, q * 1.5f).abs();
        sr.add(c);
        //System.out.println("sr  = " + sr);
        d = a.multiply(b).sum(c);
        t = PolyGBUtil.<BigRational> subRingAndMember(sr, d);
        assertTrue("d in SR: ", t);
    }


    /**
     * Test Chinese remainder theorem, random.
     */
    public void testCRTrandom() {
        String[] vars = new String[] { "x", "y", "z" };
        GenPolynomialRing<BigRational> dfac;
        dfac = new GenPolynomialRing<BigRational>(new BigRational(1), rl, to, vars);
        //System.out.println("dfac = " + dfac.toScript());
        GenPolynomial<BigRational> a, b;
        List<List<GenPolynomial<BigRational>>> F = new ArrayList<List<GenPolynomial<BigRational>>>(2);
        List<GenPolynomial<BigRational>> F1 = new ArrayList<GenPolynomial<BigRational>>(2);
        a = dfac.random(kl, ll, el, q).abs();
        b = dfac.random(kl, ll, el, q).abs();
        F1.add(a);
        F1.add(b);
        F.add(F1);
        List<GenPolynomial<BigRational>> F2 = new ArrayList<GenPolynomial<BigRational>>(2);
        a = dfac.random(kl, ll, el, q).abs();
        b = dfac.random(kl, ll, el, q).abs();
        F2.add(a);
        F2.add(b);
        F.add(F2);
        //System.out.println("F  = " + F);
        List<GenPolynomial<BigRational>> A = new ArrayList<GenPolynomial<BigRational>>(2);
        a = dfac.random(kl / 2, ll, el, q).abs();
        b = dfac.random(kl / 2, ll, el, q).abs();
        A.add(a);
        A.add(b);
        //System.out.println("A  = " + A);
        GenPolynomial<BigRational> h = PolyGBUtil.<BigRational> chineseRemainderTheorem(F, A);
        //System.out.println("h  = " + h);
        assertTrue("h == null or deg(h) >= 0: ", (h == null || h.degree() >= 0));
        if (h == null) {
            return;
        }
        assertTrue("isChineseRemainder " + h, PolyGBUtil.<BigRational> isChineseRemainder(F, A, h));
    }


    /**
     * Test Chinese remainder theorem, random.
     */
    public void testCRTrandomInt() {
        String[] vars = new String[] { "x", "y", "z" };
        GenPolynomialRing<BigInteger> dfac;
        dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), rl, to, vars);
        //System.out.println("dfac = " + dfac.toScript());
        GenPolynomial<BigInteger> a, b;
        List<List<GenPolynomial<BigInteger>>> F = new ArrayList<List<GenPolynomial<BigInteger>>>(2);
        List<GenPolynomial<BigInteger>> F1 = new ArrayList<GenPolynomial<BigInteger>>(2);
        a = dfac.random(kl, ll, el, q).abs();
        b = dfac.random(kl, ll, el, q).abs();
        F1.add(a);
        F1.add(b);
        F.add(F1);
        List<GenPolynomial<BigInteger>> F2 = new ArrayList<GenPolynomial<BigInteger>>(2);
        a = dfac.random(kl, ll, el, q).abs();
        b = dfac.random(kl, ll, el, q).abs();
        F2.add(a);
        F2.add(b);
        F.add(F2);
        //System.out.println("F  = " + F);
        List<GenPolynomial<BigInteger>> A = new ArrayList<GenPolynomial<BigInteger>>(2);
        a = dfac.random(kl / 2, ll, el, q).abs();
        b = dfac.random(kl / 2, ll, el, q).abs();
        A.add(a);
        A.add(b);
        //System.out.println("A  = " + A);
        GenPolynomial<BigInteger> h = PolyGBUtil.<BigInteger> chineseRemainderTheorem(F, A);
        //System.out.println("h  = " + h);
        if (h == null) {
            return;
        }
        assertTrue("h == null or deg(h) >= 0: ", (h == null || h.degree() >= 0));
        //boolean t = PolyGBUtil.<BigInteger> isChineseRemainder(F, A, h);
        //System.out.println("t  = " + t);
        //assertTrue("isChineseRemainder " + h, PolyGBUtil.<BigInteger> isChineseRemainder(F, A, h));
    }


    /**
     * Test Chinese remainder theorem, interpolation.
     */
    public void testCRTLagrange() {
        String[] vars = new String[] { "x", "y", "z" };
        GenPolynomialRing<BigRational> dfac;
        dfac = new GenPolynomialRing<BigRational>(new BigRational(1), rl, to, vars);
        //System.out.println("dfac = " + dfac.toScript());
        GenPolynomial<BigRational> a, b, c;
        List<List<GenPolynomial<BigRational>>> F = new ArrayList<List<GenPolynomial<BigRational>>>(2);
        List<GenPolynomial<BigRational>> F1 = new ArrayList<GenPolynomial<BigRational>>(3);
        a = dfac.parse("(x-(2**16-15))");
        F1.add(a);
        b = dfac.parse("(y-13)");
        F1.add(b);
        c = dfac.parse("(z-169)");
        F1.add(c);
        F.add(F1);
        List<GenPolynomial<BigRational>> F2 = new ArrayList<GenPolynomial<BigRational>>(3);
        a = dfac.parse("(x-7)");
        F2.add(a);
        b = dfac.parse("(y-(2**32-5))");
        F2.add(b);
        c = dfac.parse("(z-101)");
        F2.add(c);
        F.add(F2);
        List<GenPolynomial<BigRational>> F3 = new ArrayList<GenPolynomial<BigRational>>(3);
        a = dfac.parse("(x-17)");
        F3.add(a);
        b = dfac.parse("(y-23)");
        F3.add(b);
        c = dfac.parse("(z-(2**15-19))");
        F3.add(c);
        F.add(F3);
        //System.out.println("F  = " + F);
        List<GenPolynomial<BigRational>> A = new ArrayList<GenPolynomial<BigRational>>(2);
        a = dfac.parse("(4)");
        b = dfac.parse("(11)");
        c = dfac.parse("(103)");
        A.add(a);
        A.add(b);
        A.add(c);
        //System.out.println("A  = " + A);
        GenPolynomial<BigRational> h = PolyGBUtil.<BigRational> chineseRemainderTheorem(F, A);
        //System.out.println("h  = " + h);
        assertTrue("h == null or deg(h) >= 0: ", (h == null || h.degree() >= 0));
        if (h == null) {
            return;
        }
        assertTrue("isChineseRemainder " + h, PolyGBUtil.<BigRational> isChineseRemainder(F, A, h));
    }


    /**
     * Test Chinese remainder theorem, interpolation.
     */
    public void testCRTLagrangeInt() {
        String[] vars = new String[] { "x", "y", "z" };
        GenPolynomialRing<BigInteger> dfac;
        dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), rl, to, vars);
        //System.out.println("dfac = " + dfac.toScript());
        GenPolynomial<BigInteger> a, b, c;
        List<List<GenPolynomial<BigInteger>>> F = new ArrayList<List<GenPolynomial<BigInteger>>>(2);
        List<GenPolynomial<BigInteger>> F1 = new ArrayList<GenPolynomial<BigInteger>>(3);
        a = dfac.parse("(x-(2**16-15))");
        F1.add(a);
        b = dfac.parse("(y-13)");
        F1.add(b);
        c = dfac.parse("(z-169)");
        F1.add(c);
        F.add(F1);
        List<GenPolynomial<BigInteger>> F2 = new ArrayList<GenPolynomial<BigInteger>>(3);
        a = dfac.parse("(x-7)");
        F2.add(a);
        b = dfac.parse("(y-(2**32-5))");
        F2.add(b);
        c = dfac.parse("(z-101)");
        F2.add(c);
        F.add(F2);
        List<GenPolynomial<BigInteger>> F3 = new ArrayList<GenPolynomial<BigInteger>>(3);
        a = dfac.parse("(x-17)");
        F3.add(a);
        b = dfac.parse("(y-23)");
        F3.add(b);
        c = dfac.parse("(z-(2**15-19))");
        F3.add(c);
        F.add(F3);
        //System.out.println("F  = " + F);
        List<GenPolynomial<BigInteger>> A = new ArrayList<GenPolynomial<BigInteger>>(2);
        a = dfac.parse("(4)");
        b = dfac.parse("(11)");
        c = dfac.parse("(103)");
        A.add(a);
        A.add(b);
        A.add(c);
        //System.out.println("A  = " + A);
        GenPolynomial<BigInteger> h = PolyGBUtil.<BigInteger> chineseRemainderTheorem(F, A);
        //System.out.println("h  = " + h);
        assertTrue("deg(h) > 0: ", h.degree() > 0);
        //if (h == null) {
        //    return;
        //}
        //boolean t = PolyGBUtil.<BigInteger> isChineseRemainder(F, A, h);
        //System.out.println("t  = " + t);
        //assertTrue("isChineseRemainder " + h, PolyGBUtil.<BigInteger> isChineseRemainder(F, A, h));
    }


    /**
     * Test Chinese remainder theorem, interpolation.
     */
    public void testCRTnterpolation() {
        String[] vars = new String[] { "x", "y", "z" };
        BigRational cfac = new BigRational(1);
        GenPolynomialRing<BigRational> dfac;
        dfac = new GenPolynomialRing<BigRational>(cfac, rl, to, vars);
        //System.out.println("dfac = " + dfac.toScript());
        BigRational a, b, c;
        List<List<BigRational>> F = new ArrayList<List<BigRational>>(3);
        List<BigRational> F1 = new ArrayList<BigRational>(3);
        a = cfac.parse("65521");
        F1.add(a);
        b = cfac.parse("13");
        F1.add(b);
        c = cfac.parse("169");
        F1.add(c);
        F.add(F1);
        List<BigRational> F2 = new ArrayList<BigRational>(3);
        a = cfac.parse("7");
        F2.add(a);
        b = cfac.parse("4294967291");
        F2.add(b);
        c = cfac.parse("101");
        F2.add(c);
        F.add(F2);
        List<BigRational> F3 = new ArrayList<BigRational>(3);
        a = cfac.parse("17");
        F3.add(a);
        b = cfac.parse("23");
        F3.add(b);
        c = cfac.parse("32749");
        F3.add(c);
        F.add(F3);
        List<BigRational> F4 = new ArrayList<BigRational>(3);
        a = cfac.parse("27");
        F4.add(a);
        b = cfac.parse("33");
        F4.add(b);
        c = cfac.parse("42749");
        F4.add(c);
        F.add(F4);
        List<BigRational> F5 = new ArrayList<BigRational>(3);
        a = cfac.parse("37");
        F5.add(a);
        b = cfac.parse("43");
        F5.add(b);
        c = cfac.parse("22749");
        F5.add(c);
        F.add(F5);
        List<BigRational> F6 = new ArrayList<BigRational>(3);
        a = cfac.parse("47");
        F6.add(a);
        b = cfac.parse("53");
        F6.add(b);
        c = cfac.parse("12749");
        F6.add(c);
        F.add(F6);
        List<BigRational> F7 = new ArrayList<BigRational>(3);
        a = cfac.parse("57");
        F7.add(a);
        b = cfac.parse("63");
        F7.add(b);
        c = cfac.parse("2749");
        F7.add(c);
        F.add(F7);
        List<BigRational> F8 = new ArrayList<BigRational>(3);
        a = cfac.parse("67");
        F8.add(a);
        b = cfac.parse("93");
        F8.add(b);
        c = cfac.parse("749");
        F8.add(c);
        F.add(F8);
        List<BigRational> F9 = new ArrayList<BigRational>(3);
        a = cfac.parse("77");
        F9.add(a);
        b = cfac.parse("103");
        F9.add(b);
        c = cfac.parse("49");
        F9.add(c);
        F.add(F9);
        //System.out.println("F  = " + F);
        List<BigRational> A = new ArrayList<BigRational>(3);
        a = cfac.parse("4");
        b = cfac.parse("11");
        c = cfac.parse("103");
        A.add(a);
        A.add(b);
        A.add(c);
        c = cfac.parse("13");
        A.add(c);
        c = cfac.parse("23");
        A.add(c);
        c = cfac.parse("91");
        A.add(c);
        c = cfac.parse("81");
        A.add(c);
        c = cfac.parse("72");
        A.add(c);
        c = cfac.parse("102");
        A.add(c);
        //System.out.println("A  = " + A);
        GenPolynomial<BigRational> h = PolyGBUtil.<BigRational> CRTInterpolation(dfac, F, A);
        //System.out.println("h  = " + h);
        assertTrue("deg(h) > 0: ", h.degree() > 0);
        //if (h == null) {
        //    return;
        //}
        //boolean t = PolyGBUtil.<BigRational> isChineseRemainder(F, A, h);
        //System.out.println("t  = " + t);
        //assertTrue("isChineseRemainder " + h, PolyGBUtil.<BigRational> isChineseRemainder(F, A, h));
    }

}
