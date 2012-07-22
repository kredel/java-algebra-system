/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.SortedMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.PrimeList;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;


/**
 * Factor modular tests with JUnit.
 * @author Heinz Kredel.
 */

public class FactorModularTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        //BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>FactorModularTest</CODE> object.
     * @param name String.
     */
    public FactorModularTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(FactorModularTest.class);
        return suite;
    }


    int rl = 3;


    int kl = 5;


    int ll = 5;


    int el = 3;


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
     * Test modular factorization.
     * 
     */
    public void testModularFactorization() {

        PrimeList pl = new PrimeList(PrimeList.Range.medium);
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        ModIntegerRing cfac = new ModIntegerRing(pl.get(3));
        //System.out.println("cfac = " + cfac);
        GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(cfac, 1, to);
        FactorModular<ModInteger> fac = new FactorModular<ModInteger>(cfac);

        for (int i = 1; i < 4; i++) {
            int facs = 0;
            GenPolynomial<ModInteger> a = null; //pfac.random(kl,ll*(i+1),el*(i+1),q);
            GenPolynomial<ModInteger> b = pfac.random(kl, ll * (i + 1), el * (i + 1), q);
            GenPolynomial<ModInteger> c = pfac.random(kl, ll * (i + 1), el * (i + 1), q);
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
            a = a.monic();
            //System.out.println("\na = " + a);
            //System.out.println("b = " + b);
            //System.out.println("c = " + c);

            SortedMap<GenPolynomial<ModInteger>, Long> sm = fac.baseFactors(a);
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
     * Test modular factorization example.
     * 
     */
    public void testModularFactorizationExam() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        ModIntegerRing cfac = new ModIntegerRing(7);
        //System.out.println("cfac = " + cfac);
        String[] vars = new String[] { "x" };
        GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(cfac, 1, to, vars);
        FactorModular<ModInteger> fac = new FactorModular<ModInteger>(cfac);

        int facs = 3;
        GenPolynomial<ModInteger> a = pfac.parse("(x^12+5)");
        a = a.monic();
        //System.out.println("\na = " + a);

        SortedMap<GenPolynomial<ModInteger>, Long> sm = fac.baseFactors(a);
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


    /**
     * Test modular factorization, case p = 2.
     * 
     */
    public void testModular2Factorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        ModIntegerRing cfac = new ModIntegerRing(2L);
        //System.out.println("cfac = " + cfac);
        GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(cfac, 1, to);
        FactorModular<ModInteger> fac = new FactorModular<ModInteger>(cfac);

        for (int i = 1; i < 4; i++) {
            int facs = 0;
            GenPolynomial<ModInteger> a = null; //pfac.random(kl,ll*(i+1),el*(i+1),q);
            GenPolynomial<ModInteger> b = pfac.random(kl, ll * (i + 1), el * (i + 1), q);
            GenPolynomial<ModInteger> c = pfac.random(kl, ll * (i + 1), el * (i + 1), q);
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
            a = a.monic();
            //System.out.println("\na = " + a);
            //System.out.println("b = " + b);
            //System.out.println("c = " + c);

            SortedMap<GenPolynomial<ModInteger>, Long> sm = fac.baseFactors(a);
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
     * Test multivariate modular factorization.
     * 
     */
    public void testMultivariateModularFactorization() {

        //PrimeList pl = new PrimeList(PrimeList.Range.small);
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        ModIntegerRing cfac = new ModIntegerRing(13); // pl.get(3), 7, 11, 13
        GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(cfac, rl, to);
        FactorModular<ModInteger> fac = new FactorModular<ModInteger>(cfac);

        for (int i = 1; i < 2; i++) {
            int facs = 0;
            GenPolynomial<ModInteger> a = null; //pfac.random(kl,ll*(i+1),el,q);
            GenPolynomial<ModInteger> b = pfac.random(kl, 2, el, q);
            GenPolynomial<ModInteger> c = pfac.random(kl, 2, el, q);
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
            //a = a.monic();
            //System.out.println("\na = " + a);
            //System.out.println("b = " + b);
            //System.out.println("c = " + c);

            SortedMap<GenPolynomial<ModInteger>, Long> sm = fac.factors(a);
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
     * Test modular absolute factorization.
     * 
     */
    public void testBaseModularAbsoluteFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        ModIntegerRing cfac = new ModIntegerRing(17);
        String[] alpha = new String[] { "alpha" };
        //String[] vars = new String[] { "z" };
        GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(cfac, 1, to, alpha);
        GenPolynomial<ModInteger> agen = pfac.univariate(0, 4);
        agen = agen.sum(pfac.fromInteger(1)); // x^4 + 1

        FactorModular<ModInteger> engine = new FactorModular<ModInteger>(cfac);

        FactorsMap<ModInteger> F
        //= engine.baseFactorsAbsoluteSquarefree(agen);
        //= engine.baseFactorsAbsoluteIrreducible(agen);
        = engine.baseFactorsAbsolute(agen);
        //System.out.println("agen        = " + agen);
        //System.out.println("F           = " + F);

        boolean t = engine.isAbsoluteFactorization(F);
        //System.out.println("t        = " + t);
        assertTrue("prod(factor(a)) = a", t);
    }

}
