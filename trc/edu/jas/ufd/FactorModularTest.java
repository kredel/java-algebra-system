/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.List;
import java.util.SortedMap;

import edu.jas.arith.ModInt;
import edu.jas.arith.ModIntRing;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.PrimeList;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Factor modular tests with JUnit.
 * @author Heinz Kredel
 */

public class FactorModularTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
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
     */
    public void testModular2Factorization() {
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        ModIntegerRing cfac = new ModIntegerRing(2L);
        //System.out.println("cfac = " + cfac);
        GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(cfac, new String[] { "x" },
                        to);
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
            //a = pfac.parse(" x**13 + x**11 + x**7 + x**3 + x ");
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


    /**
     * Berlekamp small odd prime factorization test.
     */
    public void testFactorBerlekampSmallOdd() {
        int q = 11; //32003; //11;
        ModIntRing mi = new ModIntRing(q);
        //System.out.println("mi = " + mi.toScript());
        GenPolynomialRing<ModInt> pfac = new GenPolynomialRing<ModInt>(mi, new String[] { "x" });
        //System.out.println("SmallOdd pfac = " + pfac.toScript());
        GenPolynomial<ModInt> A = pfac.parse("x^6 - 3 x^5 + x^4 - 3 x^3 - x^2 -3 x + 1");
        //System.out.println("A = " + A.toScript());

        FactorAbstract<ModInt> bf = new FactorModularBerlekamp<ModInt>(pfac.coFac);
        List<GenPolynomial<ModInt>> factors = bf.baseFactorsSquarefree(A);
        //System.out.println("factors = " + factors + "\n");
        //System.out.println("isFactorization = " + bf.isFactorization(A,factors));
        assertTrue("A == prod(factors): " + factors, bf.isFactorization(A, factors));

        GenPolynomial<ModInt> B = pfac.random(5).monic();
        GenPolynomial<ModInt> C = pfac.random(5).monic();
        A = B.multiply(C);
        //System.out.println("A = " + A.toScript());
        //System.out.println("B = " + B.toScript());
        //System.out.println("C = " + C.toScript());

        factors = bf.baseFactorsSquarefree(A);
        //System.out.println("factors = " + factors + "\n");
        //System.out.println("isFactorization = " + bf.isFactorization(A,factors));
        assertTrue("A == prod(factors): " + factors, bf.isFactorization(A, factors));
    }


    /**
     * Berlekamp big odd prime factorization test.
     */
    public void testFactorBerlekampBigOdd() {
        int q = 32003; //11;
        ModIntRing mi = new ModIntRing(q);
        //System.out.println("mi = " + mi.toScript());
        GenPolynomialRing<ModInt> pfac = new GenPolynomialRing<ModInt>(mi, new String[] { "x" });
        //System.out.println("BigOdd pfac = " + pfac.toScript());
        GenPolynomial<ModInt> A = pfac.parse("x^6 - 3 x^5 + x^4 - 3 x^3 - x^2 -3 x + 1");
        //System.out.println("A = " + A.toScript());

        //FactorAbstract<ModInt> bf = new FactorModularBerlekamp<ModInt>(pfac.coFac);
        FactorModularBerlekamp<ModInt> bf = new FactorModularBerlekamp<ModInt>(pfac.coFac);
        List<GenPolynomial<ModInt>> factors = bf.baseFactorsSquarefreeBigPrime(A);
        //System.out.println("factors = " + factors + "\n");
        //System.out.println("isFactorization = " + bf.isFactorization(A,factors));
        assertTrue("A == prod(factors): " + factors, bf.isFactorization(A, factors));

        GenPolynomial<ModInt> B = pfac.random(5).monic();
        GenPolynomial<ModInt> C = pfac.random(5).monic();
        A = B.multiply(C);
        //System.out.println("A = " + A.toScript());
        //System.out.println("B = " + B.toScript());
        //System.out.println("C = " + C.toScript());

        factors = bf.baseFactorsSquarefreeBigPrime(A);
        //System.out.println("factors = " + factors + "\n");
        //System.out.println("isFactorization = " + bf.isFactorization(A,factors));
        assertTrue("A == prod(factors): " + factors, bf.isFactorization(A, factors));
    }


    /**
     * Berlekamp big even prime factorization test.
     */
    public void testFactorBerlekampBigEven() {
        int q = 2;
        ModIntRing mi = new ModIntRing(q);
        //System.out.println("mi = " + mi.toScript());
        GenPolynomialRing<ModInt> pfac = new GenPolynomialRing<ModInt>(mi, new String[] { "x" });
        //System.out.println("BigEven pfac = " + pfac.toScript());
        GenPolynomial<ModInt> A = pfac.parse("x^6 - 3 x^5 + x^4 - 3 x^3 - x^2 -3 x + 1");
        //GenPolynomial<ModInt> A = pfac.parse(" x**13 + x**11 + x**7 + x**3 + x "); //sm = {x=1, x^2 + x + 1 =6}

        //System.out.println("A = " + A.toScript());

        //FactorAbstract<ModInt> bf = new FactorModularBerlekamp<ModInt>(pfac.coFac);
        FactorModularBerlekamp<ModInt> bf = new FactorModularBerlekamp<ModInt>(pfac.coFac);
        List<GenPolynomial<ModInt>> factors = bf.baseFactorsSquarefreeBigPrime(A);
        //System.out.println("factors = " + factors + "\n");
        //System.out.println("isFactorization = " + bf.isFactorization(A,factors));
        assertTrue("A == prod(factors): " + factors, bf.isFactorization(A, factors));

        GenPolynomial<ModInt> B = pfac.random(10).monic();
        GenPolynomial<ModInt> C = pfac.random(10).monic();
        A = B.multiply(C);
        //System.out.println("A = " + A.toScript());
        //System.out.println("B = " + B.toScript());
        //System.out.println("C = " + C.toScript());

        factors = bf.baseFactorsSquarefreeBigPrime(A);
        //System.out.println("factors = " + factors + "\n");
        //System.out.println("isFactorization = " + bf.isFactorization(A,factors));
        assertTrue("A == prod(factors): " + factors, bf.isFactorization(A, factors));
    }


    /**
     * Berlekamp small even prime factorization test.
     */
    public void testFactorBerlekampSmallEven() {
        int q = 2;
        ModIntRing mi = new ModIntRing(q);
        //System.out.println("mi = " + mi.toScript());
        GenPolynomialRing<ModInt> pfac = new GenPolynomialRing<ModInt>(mi, new String[] { "x" });
        //System.out.println("SmallEven pfac = " + pfac.toScript());
        GenPolynomial<ModInt> A = pfac.parse("x^6 - 3 x^5 + x^4 - 3 x^3 - x^2 -3 x + 1");
        //GenPolynomial<ModInt> A = pfac.parse(" x**13 + x**11 + x**7 + x**3 + x "); //sm = {x=1, x^2 + x + 1 =6}
        //System.out.println("A = " + A.toScript());

        //FactorAbstract<ModInt> bf = new FactorModularBerlekamp<ModInt>(pfac.coFac);
        FactorModularBerlekamp<ModInt> bf = new FactorModularBerlekamp<ModInt>(pfac.coFac);
        List<GenPolynomial<ModInt>> factors = bf.baseFactorsSquarefreeSmallPrime(A);
        //System.out.println("factors = " + factors + "\n");
        //System.out.println("isFactorization = " + bf.isFactorization(A,factors));
        assertTrue("A == prod(factors): " + factors, bf.isFactorization(A, factors));

        GenPolynomial<ModInt> B = pfac.random(10).monic();
        GenPolynomial<ModInt> C = pfac.random(10).monic();
        A = B.multiply(C);
        //System.out.println("A = " + A.toScript());
        //System.out.println("B = " + B.toScript());
        //System.out.println("C = " + C.toScript());

        factors = bf.baseFactorsSquarefreeSmallPrime(A);
        //System.out.println("factors = " + factors + "\n");
        //System.out.println("isFactorization = " + bf.isFactorization(A,factors));
        assertTrue("A == prod(factors): " + factors, bf.isFactorization(A, factors));
    }


    /**
     * Berlekamp big even prime power factorization test.
     */
    public void testFactorBerlekampBigEvenPower() {
        int q = 2;
        //int qp = 4;
        ModIntRing mi = new ModIntRing(q);
        //System.out.println("mi = " + mi.toScript());
        GenPolynomialRing<ModInt> mfac = new GenPolynomialRing<ModInt>(mi, new String[] { "a" });
        GenPolynomial<ModInt> amod = mfac.parse("a^4 + a + 1");
        //AlgebraicNumberRing<ModInt> gf = PolyUfdUtil.algebraicNumberField(mi, qp);
        AlgebraicNumberRing<ModInt> gf = new AlgebraicNumberRing<ModInt>(amod, true);
        //System.out.println("gf(2^4) = " + gf.toScript());
        GenPolynomialRing<AlgebraicNumber<ModInt>> pfac = new GenPolynomialRing<AlgebraicNumber<ModInt>>(gf,
                        new String[] { "x" });
        //System.out.println("BigEvenPower pfac = " + pfac.toScript());
        //GenPolynomial<AlgebraicNumber<ModInt>> A = pfac.parse("x^6 - 3 x^5 + x^4 - 3 x^3 - x^2 -3 x + 1");
        GenPolynomial<AlgebraicNumber<ModInt>> A = pfac
                        .parse("x^5 + (a^3 + a + 1) x^4 + (a^3 + a^2 + 1) x^3 + ( a ) x + (a^3 + a + 1)");
        //System.out.println("A = " + A.toScript());

        //FactorAbstract<AlgebraicNumber<ModInt>> bf = new FactorModularBerlekamp<AlgebraicNumber<ModInt>>(pfac.coFac);
        FactorModularBerlekamp<AlgebraicNumber<ModInt>> bf = new FactorModularBerlekamp<AlgebraicNumber<ModInt>>(
                        pfac.coFac);
        List<GenPolynomial<AlgebraicNumber<ModInt>>> factors = bf.baseFactorsSquarefreeBigPrime(A);
        //System.out.println("factors = " + factors + "\n");
        //System.out.println("isFactorization = " + bf.isFactorization(A,factors));
        assertTrue("A == prod(factors): " + factors, bf.isFactorization(A, factors));

        GenPolynomial<AlgebraicNumber<ModInt>> B = pfac.random(5).monic();
        GenPolynomial<AlgebraicNumber<ModInt>> C = pfac.random(7).monic();
        A = B.multiply(C);
        //System.out.println("A = " + A.toScript());
        //System.out.println("B = " + B.toScript());
        //System.out.println("C = " + C.toScript());

        factors = bf.baseFactorsSquarefreeBigPrime(A);
        //System.out.println("factors = " + factors + "\n");
        //System.out.println("isFactorization = " + bf.isFactorization(A,factors));
        assertTrue("A == prod(factors): " + factors, bf.isFactorization(A, factors));
    }


    /**
     * Berlekamp small even prime power factorization test.
     */
    public void testFactorBerlekampSmallEvenPower() {
        int q = 2;
        //int qp = 4;
        ModIntRing mi = new ModIntRing(q);
        //System.out.println("mi = " + mi.toScript());
        GenPolynomialRing<ModInt> mfac = new GenPolynomialRing<ModInt>(mi, new String[] { "a" });
        GenPolynomial<ModInt> amod = mfac.parse("a^4 + a + 1");
        //AlgebraicNumberRing<ModInt> gf = PolyUfdUtil.algebraicNumberField(mi, qp);
        AlgebraicNumberRing<ModInt> gf = new AlgebraicNumberRing<ModInt>(amod, true);
        //System.out.println("gf(2^4) = " + gf.toScript());
        GenPolynomialRing<AlgebraicNumber<ModInt>> pfac = new GenPolynomialRing<AlgebraicNumber<ModInt>>(gf,
                        new String[] { "x" });
        //System.out.println("SmallEvenPower pfac = " + pfac.toScript());
        //GenPolynomial<AlgebraicNumber<ModInt>> A = pfac.parse("x^6 - 3 x^5 + x^4 - 3 x^3 - x^2 -3 x + 1");
        GenPolynomial<AlgebraicNumber<ModInt>> A = pfac
                        .parse("x^5 + (a^3 + a + 1) x^4 + (a^3 + a^2 + 1) x^3 + ( a ) x + (a^3 + a + 1)");
        //System.out.println("A = " + A.toScript());

        //FactorAbstract<AlgebraicNumber<ModInt>> bf = new FactorModularBerlekamp<AlgebraicNumber<ModInt>>(pfac.coFac);
        FactorModularBerlekamp<AlgebraicNumber<ModInt>> bf = new FactorModularBerlekamp<AlgebraicNumber<ModInt>>(
                        pfac.coFac);
        List<GenPolynomial<AlgebraicNumber<ModInt>>> factors = bf.baseFactorsSquarefreeSmallPrime(A);
        //System.out.println("factors = " + factors + "\n");
        //System.out.println("isFactorization = " + bf.isFactorization(A,factors));
        assertTrue("A == prod(factors): " + factors, bf.isFactorization(A, factors));

        GenPolynomial<AlgebraicNumber<ModInt>> B = pfac.random(5).monic();
        GenPolynomial<AlgebraicNumber<ModInt>> C = pfac.random(7).monic();
        A = B.multiply(C);
        //System.out.println("A = " + A.toScript());
        //System.out.println("B = " + B.toScript());
        //System.out.println("C = " + C.toScript());

        factors = bf.baseFactorsSquarefreeSmallPrime(A);
        //System.out.println("factors = " + factors + "\n");
        //System.out.println("isFactorization = " + bf.isFactorization(A,factors));
        assertTrue("A == prod(factors): " + factors, bf.isFactorization(A, factors));
    }


    /**
     * Berlekamp small odd prime power factorization test.
     */
    public void testFactorBerlekampSmallOddPower() {
        int q = 3;
        int qp = 4;
        ModIntRing mi = new ModIntRing(q);
        //System.out.println("mi = " + mi.toScript());
        GenPolynomialRing<ModInt> mfac = new GenPolynomialRing<ModInt>(mi, new String[] { "a" });
        //GenPolynomial<ModInt> amod = mfac.parse("a^4 + a + 1");
        //AlgebraicNumberRing<ModInt> gf = new AlgebraicNumberRing<ModInt>(amod, true);
        AlgebraicNumberRing<ModInt> gf = PolyUfdUtil.algebraicNumberField(mfac, qp);
        //System.out.println("gf(2^4) = " + gf.toScript());
        GenPolynomialRing<AlgebraicNumber<ModInt>> pfac = new GenPolynomialRing<AlgebraicNumber<ModInt>>(gf,
                        new String[] { "x" });
        //System.out.println("SmallOddPower pfac = " + pfac.toScript());
        //GenPolynomial<AlgebraicNumber<ModInt>> A = pfac.parse("x^6 - 3 x^5 + x^4 - 3 x^3 - x^2 -3 x + 1");
        GenPolynomial<AlgebraicNumber<ModInt>> A = pfac
                        .parse("x^5 + (a^3 + a + 1) x^4 + (a^3 + a^2 + 1) x^3 + ( a ) x + (a^3 + a + 1)");
        //System.out.println("A = " + A.toScript());

        //FactorAbstract<AlgebraicNumber<ModInt>> bf = new FactorModularBerlekamp<AlgebraicNumber<ModInt>>(pfac.coFac);
        FactorModularBerlekamp<AlgebraicNumber<ModInt>> bf = new FactorModularBerlekamp<AlgebraicNumber<ModInt>>(
                        pfac.coFac);
        List<GenPolynomial<AlgebraicNumber<ModInt>>> factors = bf.baseFactorsSquarefreeSmallPrime(A);
        //System.out.println("factors = " + factors + "\n");
        //System.out.println("isFactorization = " + bf.isFactorization(A,factors));
        assertTrue("A == prod(factors): " + factors, bf.isFactorization(A, factors));

        GenPolynomial<AlgebraicNumber<ModInt>> B = pfac.random(5).monic();
        GenPolynomial<AlgebraicNumber<ModInt>> C = pfac.random(7).monic();
        A = B.multiply(C);
        //System.out.println("A = " + A.toScript());
        //System.out.println("B = " + B.toScript());
        //System.out.println("C = " + C.toScript());

        factors = bf.baseFactorsSquarefreeSmallPrime(A);
        //System.out.println("factors = " + factors + "\n");
        //System.out.println("isFactorization = " + bf.isFactorization(A,factors));
        assertTrue("A == prod(factors): " + factors, bf.isFactorization(A, factors));
    }


    /**
     * Berlekamp big odd prime power factorization test.
     */
    public void testFactorBerlekampBigOddPower() {
        int q = 3;
        int qp = 4;
        ModIntRing mi = new ModIntRing(q);
        //System.out.println("mi = " + mi.toScript());
        GenPolynomialRing<ModInt> mfac = new GenPolynomialRing<ModInt>(mi, new String[] { "a" });
        //GenPolynomial<ModInt> amod = mfac.parse("a^4 + a + 1");
        //AlgebraicNumberRing<ModInt> gf = new AlgebraicNumberRing<ModInt>(amod, true);
        AlgebraicNumberRing<ModInt> gf = PolyUfdUtil.algebraicNumberField(mfac, qp);
        //System.out.println("gf(2^4) = " + gf.toScript());
        GenPolynomialRing<AlgebraicNumber<ModInt>> pfac = new GenPolynomialRing<AlgebraicNumber<ModInt>>(gf,
                        new String[] { "x" });
        //System.out.println("BigOddPower pfac = " + pfac.toScript());
        //GenPolynomial<AlgebraicNumber<ModInt>> A = pfac.parse("x^6 - 3 x^5 + x^4 - 3 x^3 - x^2 -3 x + 1");
        GenPolynomial<AlgebraicNumber<ModInt>> A = pfac
                        .parse("x^5 + (a^3 + a + 1) x^4 + (a^3 + a^2 + 1) x^3 + ( a ) x + (a^3 + a + 1)");
        //System.out.println("A = " + A.toScript());

        //FactorAbstract<AlgebraicNumber<ModInt>> bf = new FactorModularBerlekamp<AlgebraicNumber<ModInt>>(pfac.coFac);
        FactorModularBerlekamp<AlgebraicNumber<ModInt>> bf = new FactorModularBerlekamp<AlgebraicNumber<ModInt>>(
                        pfac.coFac);
        List<GenPolynomial<AlgebraicNumber<ModInt>>> factors = bf.baseFactorsSquarefreeBigPrime(A);
        //System.out.println("factors = " + factors + "\n");
        //System.out.println("isFactorization = " + bf.isFactorization(A,factors));
        assertTrue("A == prod(factors): " + factors, bf.isFactorization(A, factors));

        GenPolynomial<AlgebraicNumber<ModInt>> B = pfac.random(5).monic();
        GenPolynomial<AlgebraicNumber<ModInt>> C = pfac.random(7).monic();
        A = B.multiply(C);
        //System.out.println("A = " + A.toScript());
        //System.out.println("B = " + B.toScript());
        //System.out.println("C = " + C.toScript());

        factors = bf.baseFactorsSquarefreeBigPrime(A);
        //System.out.println("factors = " + factors + "\n");
        //System.out.println("isFactorization = " + bf.isFactorization(A,factors));
        assertTrue("A == prod(factors): " + factors, bf.isFactorization(A, factors));
    }


    /**
     * Compare Berlekamp with other factorization test.
     */
    public void testCompareFactorBerlekamp() {
        int q = 32003; //11; 29; 
        ModIntRing mi = new ModIntRing(q);
        //System.out.println("mi = " + mi.toScript());
        GenPolynomialRing<ModInt> pfac = new GenPolynomialRing<ModInt>(mi, new String[] { "x" });
        //System.out.println("time pfac = " + pfac.toScript());
        GenPolynomial<ModInt> A = pfac.parse("x^6 - 3 x^5 + x^4 - 3 x^3 - x^2 -3 x + 1");
        //System.out.println("A = " + A.toScript());

        FactorAbstract<ModInt> df = new FactorModular<ModInt>(pfac.coFac);
        FactorModularBerlekamp<ModInt> bf = new FactorModularBerlekamp<ModInt>(pfac.coFac);
        SortedMap<GenPolynomial<ModInt>, Long> factors, f2;

        GenPolynomial<ModInt> B = pfac.random(kl, ll * ll + 20, el * el + 10, q + q).monic();
        GenPolynomial<ModInt> C = pfac.random(kl, ll * ll + 20, el * el + 10, q + q).monic();
        A = B.multiply(C);
        //System.out.println("A = " + A.toScript());
        //System.out.println("B = " + B.toScript());
        //System.out.println("C = " + C.toScript());

        long tb = System.currentTimeMillis();
        factors = bf.baseFactors(A);
        tb = System.currentTimeMillis() - tb;
        assertTrue("A == prod(factors): " + factors, bf.isFactorization(A, factors));
        //System.out.println("factors = " + factors + "\n");

        long td = System.currentTimeMillis();
        f2 = df.baseFactors(A);
        td = System.currentTimeMillis() - td;
        //System.out.println("tberle = " + tb + ", tdd = " + td + "\n");
        assertEquals("factors == f2: ", factors, f2);
        //System.out.println("isFactorization = " + bf.isFactorization(A,factors));
        assertTrue("A == prod(factors): " + factors, bf.isFactorization(A, factors));
        assertTrue("t >= 0: " + tb + ", " + td, tb >= 0 && td >= 0);
    }

}
