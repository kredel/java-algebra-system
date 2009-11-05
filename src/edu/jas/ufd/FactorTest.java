/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.SortedMap;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.application.Quotient;
import edu.jas.application.QuotientRing;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.PrimeList;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
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
        //BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>FactorTest</CODE> object.
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
    public void xtestModularFactorization() {

        PrimeList pl = new PrimeList(PrimeList.Range.medium);
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        ModIntegerRing cfac = new ModIntegerRing(pl.get(3));
        //System.out.println("cfac = " + cfac);
        GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(cfac, 1, to);
        FactorModular fac = new FactorModular();

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
    public void xtestMultivariateModularFactorization() {

        PrimeList pl = new PrimeList(PrimeList.Range.small);
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        ModIntegerRing cfac = new ModIntegerRing(13); // pl.get(3), 7, 11, 13
        GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(cfac, rl, to);
        FactorModular fac = new FactorModular();

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
     * Test integer factorization.
     * 
     */
    public void xtestIntegerFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigInteger cfac = new BigInteger(4);
        BigInteger one = cfac.getONE();
        GenPolynomialRing<BigInteger> pfac = new GenPolynomialRing<BigInteger>(cfac, 1, to);
        FactorInteger fac = new FactorInteger();

        for (int i = 1; i < 3; i++) {
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
            GreatestCommonDivisorAbstract<BigInteger> engine = GCDFactory.getProxy(cfac);
            //a = engine.basePrimitivePart(a);
            // a = a.abs();
            //System.out.println("\na = " + a);
            //System.out.println("b = " + b);
            //System.out.println("c = " + c);

            SortedMap<GenPolynomial<BigInteger>, Long> sm = fac.baseFactors(a);
            //             System.out.println("\na   = " + a);
            //             System.out.println("b   = " + b);
            //             System.out.println("c   = " + c);
            //             System.out.println("sm = " + sm);

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
     * Test multivariate integer factorization.
     * 
     */
    public void ytestMultivariateIntegerFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigInteger cfac = new BigInteger(1);
        GenPolynomialRing<BigInteger> pfac = new GenPolynomialRing<BigInteger>(cfac, 2, to);
        FactorInteger fac = new FactorInteger();

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


    /**
     * Test rational factorization.
     * 
     */
    public void xtestRationalFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to);
        FactorRational fac = new FactorRational();

        for (int i = 1; i < 3; i++) {
            int facs = 0;
            GenPolynomial<BigRational> a;
            GenPolynomial<BigRational> c = pfac.random(kl - 2, ll * i, el + i, q);
            // a = a.monic();
            GenPolynomial<BigRational> b = pfac.random(kl - 2, ll, el, q);
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
            //System.out.println("\na = " + a);
            //System.out.println("b = " + b);
            //System.out.println("c = " + c);

            SortedMap<GenPolynomial<BigRational>, Long> sm = fac.baseFactors(a);
            //System.out.println("\na   = " + a);
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
     * Test algebraic factorization.
     * 
     */
    public void xtestAlgebraicFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
        String[] alpha = new String[] { "alpha" };
        String[] vars = new String[] { "z" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to, alpha);
        GenPolynomial<BigRational> agen = pfac.univariate(0, 2);
        agen = agen.sum(pfac.getONE()); // x^2 + 1
        AlgebraicNumberRing<BigRational> afac = new AlgebraicNumberRing<BigRational>(agen, true);
        GenPolynomialRing<AlgebraicNumber<BigRational>> apfac = new GenPolynomialRing<AlgebraicNumber<BigRational>>(
                afac, 1, to, vars); // univariate

        //System.out.println("agen  = " + agen);
        //System.out.println("afac  = " + afac);
        //System.out.println("apfac = " + apfac);

        FactorAlgebraic<BigRational> fac = new FactorAlgebraic<BigRational>(afac);

        for (int i = 1; i < 2; i++) {
            int facs = 0;
            GenPolynomial<AlgebraicNumber<BigRational>> a;
            GenPolynomial<AlgebraicNumber<BigRational>> c = apfac.random(2, ll + i, el + i, q);
            //a = a.monic();
            GenPolynomial<AlgebraicNumber<BigRational>> b = apfac.random(2, ll + i, el + i, q);
            if (b.degree() == 0) {
                b = b.multiply(apfac.univariate(0));
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
            //System.out.println("\na = " + a);
            //System.out.println("b = " + b.monic());
            //System.out.println("c = " + c.monic());

            SortedMap<GenPolynomial<AlgebraicNumber<BigRational>>, Long> sm = fac.baseFactors(a);
            //System.out.println("\na   =  " + a);
            //System.out.println("sm = " + sm);
            if (sm.size() >= facs) {
                assertTrue("#facs < " + facs, sm.size() >= facs);
            } else {
                System.out.println("sm.size() < facs = " + facs);
            }
            boolean t = fac.isFactorization(a, sm);
            //System.out.println("t        = " + t);
            assertTrue("prod(factor(a)) = a", t);
        }
    }


    /**
     * Test rational absolute factorization.
     * 
     */
    public void xtestBaseRationalAbsoluteFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
        String[] alpha = new String[] { "alpha" };
        String[] vars = new String[] { "z" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to, alpha);
        GenPolynomial<BigRational> agen = pfac.univariate(0, 4);
        agen = agen.sum(pfac.fromInteger(4)); // x^4 + 4

        FactorRational engine = new FactorRational();

        FactorsMap<BigRational> F
        //= engine.baseFactorsAbsoluteSquarefree(agen);
        //= engine.baseFactorsAbsoluteIrreducible(agen);
        = engine.baseFactorsAbsolute(agen);
        //System.out.println("agen     = " + agen);
        //System.out.println("F        = " + F);

        boolean t = engine.isAbsoluteFactorization(F);
        //System.out.println("t        = " + t);
        assertTrue("prod(factor(a)) = a", t);
    }


    /**
     * Test modular absolute factorization.
     * 
     */
    public void ztestBaseModularAbsoluteFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        ModIntegerRing cfac = new ModIntegerRing(17);
        String[] alpha = new String[] { "alpha" };
        String[] vars = new String[] { "z" };
        GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(cfac, 1, to, alpha);
        GenPolynomial<ModInteger> agen = pfac.univariate(0, 4);
        agen = agen.sum(pfac.fromInteger(1)); // x^4 + 1

        FactorModular engine = new FactorModular();

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


    /**
     * Test rational absolute factorization.
     * 
     */
    public void ztestRationalAbsoluteFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
        String[] vars = new String[] { "x", "y" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 2, to, vars);
        GenPolynomial<BigRational> xp = pfac.univariate(0, 2);
        GenPolynomial<BigRational> yp = pfac.univariate(1, 2);
        GenPolynomial<BigRational> g = xp.sum(yp); // x^2 + y^2
        //GenPolynomial<BigRational> g = xp.subtract(yp); // x^2 - y^2

        FactorRational engine = new FactorRational();

        FactorsMap<BigRational> F
        //= engine.baseFactorsAbsoluteSquarefree(agen);
        //= engine.baseFactorsAbsoluteIrreducible(agen);
        = engine.factorsAbsolute(g);
        //System.out.println("g    = " + g);
        //System.out.println("F    = " + F);

        boolean t = engine.isAbsoluteFactorization(F);
        //System.out.println("t        = " + t);
        assertTrue("prod(factor(a)) = a", t);
    }


    /**
     * Test factory.
     * 
     */
    public void xtestFactory() {
        ModIntegerRing mi = new ModIntegerRing(19, true);
        Factorization<ModInteger> ufdm = FactorFactory.getImplementation(mi);
        //System.out.println("ufdm = " + ufdm);
        assertTrue("ufd != Modular " + ufdm, ufdm instanceof FactorModular);

        BigInteger bi = new BigInteger(1);
        Factorization<BigInteger> ufdi = FactorFactory.getImplementation(bi);
        //System.out.println("ufdi = " + ufdi);
        assertTrue("ufd != Integer " + ufdi, ufdi instanceof FactorInteger);

        BigRational br = new BigRational(1);
        Factorization<BigRational> ufdr = FactorFactory.getImplementation(br);
        //System.out.println("ufdr = " + ufdr);
        assertTrue("ufd != Rational " + ufdr, ufdr instanceof FactorRational);

        GenPolynomialRing<ModInteger> pmfac = new GenPolynomialRing<ModInteger>(mi, 1);
        GenPolynomial<ModInteger> pm = pmfac.univariate(0);
        AlgebraicNumberRing<ModInteger> am = new AlgebraicNumberRing<ModInteger>(pm, true);
        Factorization<AlgebraicNumber<ModInteger>> ufdam = FactorFactory.getImplementation(am);
        //System.out.println("ufdam = " + ufdam);
        assertTrue("ufd != AlgebraicNumber<ModInteger> " + ufdam, ufdam instanceof FactorAlgebraic);

        GenPolynomialRing<BigRational> prfac = new GenPolynomialRing<BigRational>(br, 1);
        GenPolynomial<BigRational> pr = prfac.univariate(0);
        AlgebraicNumberRing<BigRational> ar = new AlgebraicNumberRing<BigRational>(pr, true);
        Factorization<AlgebraicNumber<BigRational>> ufdar = FactorFactory.getImplementation(ar);
        //System.out.println("ufdar = " + ufdar);
        assertTrue("ufd != AlgebraicNumber<BigRational> " + ufdar, ufdar instanceof FactorAlgebraic);

        prfac = new GenPolynomialRing<BigRational>(br, 2);
        QuotientRing<BigRational> qrfac = new QuotientRing<BigRational>(prfac);
        Factorization<Quotient<BigRational>> ufdqr = FactorFactory.getImplementation(qrfac);
        //System.out.println("ufdqr = " + ufdqr);
        assertTrue("ufd != Quotient<BigRational> " + ufdqr, ufdqr instanceof FactorQuotient);
    }


    /**
     * Test rational absolute factorization, Rothstein-Trager step.
     * 
     */
    public void testBaseRationalAbsoluteFactorizationRoT() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
        String[] alpha = new String[] { "alpha" };
        String[] vars = new String[] { "x" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to, vars);
        GenPolynomial<BigRational> agen = pfac.univariate(0, 4);
        agen = agen.sum(pfac.fromInteger(4)); // x^4 + 4

//         GenPolynomial<BigRational> x6 = pfac.univariate(0, 6);
//         GenPolynomial<BigRational> x4 = pfac.univariate(0, 4);
//         GenPolynomial<BigRational> x2 = pfac.univariate(0, 2);
//         // x^6 - 5 x^4 + 5 x^2 + 4
//         agen = x6.subtract(x4.multiply(pfac.fromInteger(5))); 
//         agen = agen.sum(x2.multiply(pfac.fromInteger(5))); 
//         agen = agen.sum(pfac.fromInteger(4)); 

//         GenPolynomial<BigRational> x3 = pfac.univariate(0, 3);
//         GenPolynomial<BigRational> x = pfac.univariate(0);
//         // x^3 + x
//         agen = x3.sum(x); 

        GenPolynomial<BigRational> x2 = pfac.univariate(0, 2);
        // x^2 - 2
        agen = x2.subtract(pfac.fromInteger(2)); 

        GenPolynomial<BigRational> N = pfac.getONE();

        FactorRational engine = new FactorRational();

        PartialFraction<BigRational> F = engine.baseAlgebraicPartialFraction(N,agen);
        System.out.println("\npartial fraction = " + F);

        //boolean t = engine.isAbsoluteFactorization(F);
        //System.out.println("t        = " + t);
        // assertTrue("prod(factor(a)) = a", t);
    }

}
