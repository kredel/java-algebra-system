/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.SortedMap;

import org.apache.log4j.BasicConfigurator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.PrimeList;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;


/**
 * Factor tests with JUnit.
 * @author Heinz Kredel.
 */

public class FactorTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>PolyUtilTest</CODE> object.
     * @param name String.
     */
    public FactorTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(FactorTest.class);
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
        ModIntegerRing cfac = new ModIntegerRing(pl.get(0));
        System.out.println("cfac = " + cfac);
        GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(cfac, 1,
                to);
        FactorModular fac = new FactorModular();

        for (int i = 0; i < 7; i++) {
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
            System.out.println("\na = " + a);
            //System.out.println("b = " + b);
            //System.out.println("c = " + c);

            SortedMap<GenPolynomial<ModInteger>, Long> sm = fac.baseFactors(a);
            System.out.println("sm = " + sm);

            if (sm.size() >= facs) {
                assertTrue("#facs < " + facs, sm.size() >= facs);
            } else {
                long sf = 1;
                for (Long e : sm.values()) {
                    sf *= e;
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

        PrimeList pl = new PrimeList(PrimeList.Range.small);
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        ModIntegerRing cfac = new ModIntegerRing(pl.get(13)); // 13
        GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(cfac, rl,
                to);
        FactorModular fac = new FactorModular();

        for (int i = 1; i < 2; i++) {
            int facs = 0;
            GenPolynomial<ModInteger> a = null; //pfac.random(kl,ll*(i+1),el,q);
            GenPolynomial<ModInteger> b = pfac.random(kl, 3, el, q);
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
            a = a.monic();
            System.out.println("\na = " + a);
            //System.out.println("b = " + b);
            //System.out.println("c = " + c);

            SortedMap<GenPolynomial<ModInteger>, Long> sm = fac.factors(a);
            System.out.println("sm = " + sm);

            if (sm.size() >= facs) {
                assertTrue("#facs < " + facs, sm.size() >= facs);
            } else {
                long sf = 1;
                for (Long e : sm.values()) {
                    sf *= e;
                }
                assertTrue("#facs < " + facs, sf >= facs);
            }

            boolean t = fac.isFactorization(a, sm);
            //System.out.println("t        = " + t);
            assertTrue("prod(factor(a)) = a", t);
        }
        ComputerThreads.terminate();
    }


    /**
     * Test integer factorization.
     * 
     */
    public void testIntegerFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigInteger cfac = new BigInteger(4);
        BigInteger one = cfac.getONE();
        GenPolynomialRing<BigInteger> pfac = new GenPolynomialRing<BigInteger>(cfac, 1,
                to);
        FactorInteger fac = new FactorInteger();

        for (int i = 1; i < 3; i++) {
            int facs = 0;
            GenPolynomial<BigInteger> a = null; //pfac.random(kl,ll*(i+1),el*(i+1),q);
            GenPolynomial<BigInteger> b = pfac.random(kl*3, ll * (i + 1), el * (i + 1), q);
            GenPolynomial<BigInteger> c = pfac.random(kl, ll * (i + 1), el * (i + 3), q);
            if (b.isZERO() || c.isZERO()) {
                continue;
            }
            if (c.degree() > 0) {
                facs++;
            }
            if (b.degree() > 0) {
                facs++;
            }
//             if (!c.leadingBaseCoefficient().isUnit()) {
//                 ExpVector e = c.leadingExpVector();
//                 c.doPutToMap(e, one);
//             }
//             if (!b.leadingBaseCoefficient().isUnit()) {
//                 ExpVector e = b.leadingExpVector();
//                 b.doPutToMap(e, one);
//             }
            a = c.multiply(b);
            if (a.isConstant()) {
                continue;
            }
            GreatestCommonDivisorAbstract<BigInteger> engine 
                = (GreatestCommonDivisorAbstract<BigInteger>)GCDFactory.<BigInteger>getProxy( cfac );
            //a = engine.basePrimitivePart(a);
            // a = a.abs();
            System.out.println("\na = " + a);
            //System.out.println("b = " + b);
            //System.out.println("c = " + c);

            SortedMap<GenPolynomial<BigInteger>, Long> sm = fac.baseFactors(a);
            System.out.println("\na   = " + a);
            System.out.println("b   = " + b);
            System.out.println("c   = " + c);
            System.out.println("sm = " + sm);

            if (sm.size() >= facs) {
                assertTrue("#facs < " + facs, sm.size() >= facs);
            } else {
                long sf = 1;
                for (Long e : sm.values()) {
                    sf *= e;
                }
                assertTrue("#facs < " + facs, sf >= facs);
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
    public void testMultivariateIntegerFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigInteger cfac = new BigInteger(1);
        GenPolynomialRing<BigInteger> pfac = new GenPolynomialRing<BigInteger>(cfac, rl,
                to);
        FactorInteger fac = new FactorInteger();

        for (int i = 1; i < 2; i++) {
            GenPolynomial<BigInteger> b = pfac.random(kl, 3, el, q);
            GenPolynomial<BigInteger> c = pfac.random(kl, 2, el, q);
            GenPolynomial<BigInteger> a;
//             if ( !a.leadingBaseCoefficient().isUnit()) {
//                 //continue;
//                 //ExpVector e = a.leadingExpVector();
//                 //a.doPutToMap(e,cfac.getONE());
//             }
            a = b.multiply(c);
            System.out.println("\na = " + a);

            SortedMap<GenPolynomial<BigInteger>, Long> sm = fac.factors(a);
            System.out.println("sm = " + sm);

            boolean t = fac.isFactorization(a, sm);
            //System.out.println("t        = " + t);
            assertTrue("prod(factor(a)) = a", t);
        }
        ComputerThreads.terminate();
    }


    /**
     * Test rational factorization.
     * 
     */
    public void testRationalFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1,
                to);
        FactorRational fac = new FactorRational();

        for (int i = 1; i < 3; i++) {
            int facs = 0;
            GenPolynomial<BigRational> a;
            GenPolynomial<BigRational> c = pfac.random(kl, ll * (i + 1), el + (i + 1), q);
            // a = a.monic();
            GenPolynomial<BigRational> b = pfac.random(kl, ll, el + 1, q); 
            //b = b.monic();
            //         if ( false && ! a.leadingBaseCoefficient().isONE() ) {
            //continue;
            //ExpVector e = a.leadingExpVector();
            //a.doPutToMap(e,cfac.getONE());
            //}
            if (c.degree() > 0) {
                facs++;
            }
            if (b.degree() > 0) {
                facs++;
            }
            a = c.multiply(b);
            System.out.println("\na = " + a);
            System.out.println("b = " + b);
            System.out.println("c = " + c);

            SortedMap<GenPolynomial<BigRational>, Long> sm = fac.baseFactors(a);
            System.out.println("\na   = " + a);
            System.out.println("sm = " + sm);
            assertTrue("#facs < " + facs, sm.size() >= facs);

            if (sm.size() >= facs) {
                assertTrue("#facs < " + facs, sm.size() >= facs);
            } else {
                long sf = 1;
                for (Long e : sm.values()) {
                    sf *= e;
                }
                assertTrue("#facs < " + facs, sf >= facs);
            }

            boolean t = fac.isFactorization(a, sm);
            //System.out.println("t        = " + t);
            assertTrue("prod(factor(a)) = a", t);
        }
    }


    /**
     * Test algebraic factorization.
     * 
     */
    public void testAlgebraicFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
        String[] alpha = new String[] { "alpha" };
        String[] vars = new String[] { "z" };
        GenPolynomialRing<BigRational> pfac 
           = new GenPolynomialRing<BigRational>(cfac, 1, to, alpha);
        GenPolynomial<BigRational> agen = pfac.univariate(0, 2);
        agen = agen.sum(pfac.getONE()); // x^2 + 1
        AlgebraicNumberRing<BigRational> afac 
           = new AlgebraicNumberRing<BigRational>(agen, true);
        GenPolynomialRing<AlgebraicNumber<BigRational>> apfac 
            = new GenPolynomialRing<AlgebraicNumber<BigRational>>(afac, 1, to, vars); // univariate

        System.out.println("agen  = " + agen);
        System.out.println("afac  = " + afac);
        System.out.println("apfac = " + apfac);

        FactorAlgebraic<BigRational> fac 
           = new FactorAlgebraic<BigRational>(new FactorRational());

        for (int i = 1; i < 2; i++) {
            int facs = 0;
            GenPolynomial<AlgebraicNumber<BigRational>> a;
            GenPolynomial<AlgebraicNumber<BigRational>> c 
                = apfac.random(2, ll+i, el+i, q);
            //a = a.monic();
            GenPolynomial<AlgebraicNumber<BigRational>> b 
                = apfac.random(2, ll+i, el+i, q);
            if (b.degree() == 0) {
                b = b.multiply( apfac.univariate(0) );
            }
            //b = b.monic();
            //if ( false && ! a.leadingBaseCoefficient().isONE() ) {
            //continue;
            //ExpVector e = a.leadingExpVector();
            //a.doPutToMap(e,cfac.getONE());
            //}
            if (c.degree() > 0) {
                facs++;
            }
            if (b.degree() > 0) {
                facs++;
            }
            //a = apfac.univariate(0,2).sum( apfac.getONE() ); // x^2 + 1 
            //a = a.multiply(a);
            //a = a.multiply( apfac.univariate(0,2).subtract( apfac.getONE() ) ); // x^2 - 1 
            //a = apfac.univariate(0,3).subtract( apfac.getONE() ); // x^3 - 1 
            //a = apfac.univariate(0,3).sum( apfac.getONE() ); // x^3 + 1 
            a = c.multiply(b);
            //a = a.monic();
            System.out.println("\na = " + a);
            System.out.println("b = " + b.monic());
            System.out.println("c = " + c.monic());

            SortedMap<GenPolynomial<AlgebraicNumber<BigRational>>, Long> sm 
                = fac.baseFactors(a);
            System.out.println("\na   =  " + a);
            System.out.println("sm = " + sm);
            if ( sm.size() >= facs ) {
               assertTrue("#facs < " + facs, sm.size() >= facs);
            } else {
               System.out.println("sm.size() < facs = " + facs);
            }
            boolean t = fac.isFactorization(a, sm);
            System.out.println("t        = " + t);
            assertTrue("prod(factor(a)) = a", t);
            ComputerThreads.terminate();
        }
    }

}
