/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.SortedMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;


/**
 * Factor tests with JUnit.
 * @author Heinz Kredel.
 */

public class FactorIntegerTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        //BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>FactorIntegerTest</CODE> object.
     * @param name String.
     */
    public FactorIntegerTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(FactorIntegerTest.class);
        return suite;
    }


    int rl = 3;


    int kl = 5;


    int ll = 5;


    int el = 5;


    float q = 0.3f;


    @Override
    protected void setUp() {
    }


    @Override
    protected void tearDown() {
        ComputerThreads.terminate();
    }


    /**
     * Test dummy for Junit.
     * 
     */
    public void testDummy() {
    }


    /**
     * Test integer monic factorization.
     * 
     */
    public void testIntegerMonicFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigInteger cfac = new BigInteger(4);
        BigInteger one = cfac.getONE();
        GenPolynomialRing<BigInteger> pfac = new GenPolynomialRing<BigInteger>(cfac, 1, to,
                new String[] { "x" });
        FactorAbstract<BigInteger> fac = new FactorInteger<ModInteger>();

        for (int i = 1; i < 3; i++) {
            int facs = 0;
            GenPolynomial<BigInteger> a = null; //pfac.random(kl,ll*(i+1),el*(i+1),q);
            GenPolynomial<BigInteger> b = pfac.random(kl * 2, ll * (i), el * (i + 1), q);
            GenPolynomial<BigInteger> c = pfac.random(kl, ll * (i), el * (i + 2), q);
            //b = pfac.parse("((x^2 + 1)*(x^2 - 111111111))");
            //c = pfac.parse("(x^3 - 222222)");
            if (b.isZERO() || c.isZERO()) {
                continue;
            }
            if (c.degree() > 0) {
                facs++;
            }
            if (b.degree() > 0) {
                facs++;
            }
            if (!c.leadingBaseCoefficient().isUnit()) {
                ExpVector e = c.leadingExpVector();
                c.doPutToMap(e, one);
            }
            if (!b.leadingBaseCoefficient().isUnit()) {
                ExpVector e = b.leadingExpVector();
                b.doPutToMap(e, one);
            }
            a = c.multiply(b);
            if (a.isConstant()) {
                continue;
            }
            GreatestCommonDivisorAbstract<BigInteger> engine = GCDFactory.getProxy(cfac);
            //a = engine.basePrimitivePart(a);
            // a = a.abs();
            //System.out.println("\na = " + a);
            //System.out.println("b = " + b);
            //System.out.println("c = " + c);

            SortedMap<GenPolynomial<BigInteger>, Long> sm = fac.baseFactors(a);
            //System.out.println("\na   = " + a);
            //System.out.println("b   = " + b);
            //System.out.println("c   = " + c);
            //System.out.println("sm = " + sm);

            if (sm.size() >= facs) {
                assertTrue("#facs < " + facs, sm.size() >= facs);
            } else {
                long sf = 0;
                for (Long e : sm.values()) {
                    sf += e;
                }
                assertTrue("#facs < " + facs + ", " + b + " * " + c, sf >= facs);
            }

            boolean t = fac.isFactorization(a, sm);
            //System.out.println("t        = " + t);
            assertTrue("prod(factor(a)) = a", t);
        }
    }


    /**
     * Test integer factorization.
     * 
     */
    public void testIntegerFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigInteger cfac = new BigInteger(4);
        BigInteger one = cfac.getONE();
        GenPolynomialRing<BigInteger> pfac = new GenPolynomialRing<BigInteger>(cfac, 1, to);
        FactorAbstract<BigInteger> fac = new FactorInteger<ModInteger>();

        for (int i = 1; i < 2; i++) {
            int facs = 0;
            GenPolynomial<BigInteger> a = null; //pfac.random(kl,ll*(i+1),el*(i+1),q);
            GenPolynomial<BigInteger> b = pfac.random(kl * 2, ll * (i), el * (i + 1), q);
            GenPolynomial<BigInteger> c = pfac.random(kl, ll * (i), el * (i + 2), q);
            if (b.isZERO() || c.isZERO()) {
                continue;
            }
            if (c.degree() > 0) {
                facs++;
            }
            if (b.degree() > 0) {
                facs++;
            }
            a = c.multiply(b);
            if (a.isConstant()) {
                continue;
            }
            GreatestCommonDivisorAbstract<BigInteger> engine = GCDFactory.getProxy(cfac);
            //a = engine.basePrimitivePart(a);
            // a = a.abs();
            //System.out.println("\na = " + a);
            //System.out.println("b = " + b);
            //System.out.println("c = " + c);

            SortedMap<GenPolynomial<BigInteger>, Long> sm = fac.baseFactors(a);
            //System.out.println("\na   = " + a);
            //System.out.println("b   = " + b);
            //System.out.println("c   = " + c);
            //System.out.println("sm = " + sm);

            if (sm.size() >= facs) {
                assertTrue("#facs < " + facs, sm.size() >= facs);
            } else {
                long sf = 0;
                for (Long e : sm.values()) {
                    sf += e;
                }
                assertTrue("#facs < " + facs, sf >= facs);
            }

            boolean t = fac.isFactorization(a, sm);
            //System.out.println("t        = " + t);
            assertTrue("prod(factor(a)) = a", t);
        }
    }


    /**
     * Test integer factorization irreducible polynomial.
     * 
     */
    public void testIntegerFactorizationIrred() {
        //BasicConfigurator.configure();
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigInteger cfac = new BigInteger(4);
        BigInteger one = cfac.getONE();
        GenPolynomialRing<BigInteger> pfac = new GenPolynomialRing<BigInteger>(cfac, 1, to,
                new String[] { "x" });
        FactorAbstract<BigInteger> fac = new FactorInteger<ModInteger>();

        for (int i = 1; i < 2; i++) {
            int facs = 0;
            GenPolynomial<BigInteger> a = pfac.random(kl, ll * (i + 1), el * (i + 1), q);
            a = pfac.parse("( x^8 - 40 x^6 + 352 x^4 - 960 x^2 + 576 )"); // Swinnerton-Dyer example
            if (a.isConstant()) {
                continue;
            }
            SortedMap<GenPolynomial<BigInteger>, Long> sm = fac.baseFactors(a);
            //System.out.println("\na   = " + a);
            //System.out.println("sm = " + sm);

            if (sm.size() >= 1) {
                assertTrue("#facs < " + facs, sm.size() >= 1);
            }

            boolean t = fac.isFactorization(a, sm);
            //System.out.println("t        = " + t);
            assertTrue("prod(factor(a)) = a", t);
        }
    }


    /**
     * Test multivariate integer factorization.
     * 
     */
    public void testMultivariate2IntegerFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigInteger cfac = new BigInteger(1);
        GenPolynomialRing<BigInteger> pfac = new GenPolynomialRing<BigInteger>(cfac, 2, to);
        FactorAbstract<BigInteger> fac = new FactorInteger<ModInteger>();

        for (int i = 1; i < 2; i++) {
            GenPolynomial<BigInteger> b = pfac.random(kl, 3, el, q / 2.0f);
            GenPolynomial<BigInteger> c = pfac.random(kl, 2, el, q);
            GenPolynomial<BigInteger> a;
            //             if ( !a.leadingBaseCoefficient().isUnit()) {
            //                 //continue;
            //                 //ExpVector e = a.leadingExpVector();
            //                 //a.doPutToMap(e,cfac.getONE());
            //             }
            a = b.multiply(c);
            //System.out.println("\na = " + a);

            SortedMap<GenPolynomial<BigInteger>, Long> sm = fac.factors(a);
            //System.out.println("sm = " + sm);

            boolean t = fac.isFactorization(a, sm);
            //System.out.println("t        = " + t);
            assertTrue("prod(factor(a)) = a", t);
        }
    }

}
