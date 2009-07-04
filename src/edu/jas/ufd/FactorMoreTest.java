/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.SortedMap;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.structure.Power;

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

import edu.jas.application.Quotient;
import edu.jas.application.QuotientRing;


/**
 * Factor tests with JUnit.
 * @author Heinz Kredel.
 */

public class FactorMoreTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        //BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>PolyUtilTest</CODE> object.
     * @param name String.
     */
    public FactorMoreTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(FactorMoreTest.class);
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
     * Test factory.
     * 
     */
    public void testFactory() {
        ModIntegerRing mi = new ModIntegerRing(19,true);
        Factorization<ModInteger> ufdm = FactorFactory.getImplementation(mi);
        //System.out.println("ufdm = " + ufdm);
        assertTrue("ufd != Modular " + ufdm, ufdm instanceof FactorModular );

        BigInteger bi = new BigInteger(1);
        Factorization<BigInteger> ufdi = FactorFactory.getImplementation(bi);
        //System.out.println("ufdi = " + ufdi);
        assertTrue("ufd != Integer " + ufdi, ufdi instanceof FactorInteger );

        BigRational br = new BigRational(1);
        Factorization<BigRational> ufdr = FactorFactory.getImplementation(br);
        //System.out.println("ufdr = " + ufdr);
        assertTrue("ufd != Rational " + ufdr, ufdr instanceof FactorRational );

        GenPolynomialRing<ModInteger> pmfac = new GenPolynomialRing<ModInteger>(mi, 1);
        GenPolynomial<ModInteger> pm = pmfac.univariate(0);
        AlgebraicNumberRing<ModInteger> am = new AlgebraicNumberRing<ModInteger>(pm);
        Factorization<AlgebraicNumber<ModInteger>> ufdam = FactorFactory.getImplementation(am);
        //System.out.println("ufdam = " + ufdam);
        assertTrue("ufd != AlgebraicNumber<ModInteger> " + ufdam, ufdam instanceof FactorAlgebraic );

        GenPolynomialRing<BigRational> prfac = new GenPolynomialRing<BigRational>(br, 1);
        GenPolynomial<BigRational> pr = prfac.univariate(0);
        AlgebraicNumberRing<BigRational> ar = new AlgebraicNumberRing<BigRational>(pr);
        Factorization<AlgebraicNumber<BigRational>> ufdar = FactorFactory.getImplementation(ar);
        //System.out.println("ufdar = " + ufdar);
        assertTrue("ufd != AlgebraicNumber<BigRational> " + ufdar, ufdar instanceof FactorAlgebraic );

        prfac = new GenPolynomialRing<BigRational>(br, 2);
        QuotientRing<BigRational> qrfac = new QuotientRing<BigRational>(prfac);
        Factorization<Quotient<BigRational>> ufdqr = FactorFactory.getImplementation(qrfac);
        //System.out.println("ufdqr = " + ufdqr);
        assertTrue("ufd != Quotient<BigRational> " + ufdqr, ufdqr instanceof FactorQuotient );
    }


    /**
     * Test integral function factorization.
     * 
     */
    public void xtestIntegralFunctionFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
        String[] qvars = new String[]{ "t" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to, qvars);
        GenPolynomial<BigRational> t  = pfac.univariate(0);

        FactorAbstract<BigRational> fac = new FactorRational();

        String[] vars = new String[]{ "x" };
        GenPolynomialRing<GenPolynomial<BigRational>> pqfac = new GenPolynomialRing<GenPolynomial<BigRational>>(pfac,1,to,vars);
        GenPolynomial<GenPolynomial<BigRational>> x  = pqfac.univariate(0);
        GenPolynomial<GenPolynomial<BigRational>> x2 = pqfac.univariate(0,2);

        for (int i = 1; i < 3; i++) {
            int facs = 0;
            GenPolynomial<GenPolynomial<BigRational>> a;
            GenPolynomial<GenPolynomial<BigRational>> b = pqfac.random(2, 3, el, q);
            //b = b.monic();
            //b = b.multiply(b);
            GenPolynomial<GenPolynomial<BigRational>> c = pqfac.random(2, 3, el, q);
            //c = c.monic();
            if ( c.degree() < 1 ) {
                c = x2.subtract(pqfac.getONE().multiply(t));
            }
            if ( b.degree() < 1 ) {
                b = x.sum(pqfac.getONE());
            }

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

            SortedMap<GenPolynomial<GenPolynomial<BigRational>>, Long> sm = fac.recursiveFactors(a);
            //System.out.println("\na   = " + a);
            System.out.println("sm = " + sm);

            if (sm.size() >= facs) {
                assertTrue("#facs < " + facs, sm.size() >= facs);
            } else {
                long sf = 0;
                for (Long e : sm.values()) {
                    sf += e;
                }
                assertTrue("#facs < " + facs, sf >= facs);
            }

            boolean tt = fac.isRecursiveFactorization(a, sm);
            //System.out.println("t        = " + tt);
            assertTrue("prod(factor(a)) = a", tt);
        }
        ComputerThreads.terminate();
    }


    /**
     * Test integer integral function factorization.
     * 
     */
    public void xtestIntegerIntegralFunctionFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigInteger cfac = new BigInteger(1);
        String[] qvars = new String[]{ "t" };
        GenPolynomialRing<BigInteger> pfac = new GenPolynomialRing<BigInteger>(cfac, 1, to, qvars);
        GenPolynomial<BigInteger> t  = pfac.univariate(0);

        FactorAbstract<BigInteger> fac = new FactorInteger();

        String[] vars = new String[]{ "x" };
        GenPolynomialRing<GenPolynomial<BigInteger>> pqfac = new GenPolynomialRing<GenPolynomial<BigInteger>>(pfac,1,to,vars);
        GenPolynomial<GenPolynomial<BigInteger>> x  = pqfac.univariate(0);
        GenPolynomial<GenPolynomial<BigInteger>> x2 = pqfac.univariate(0,2);

        for (int i = 1; i < 3; i++) {
            int facs = 0;
            GenPolynomial<GenPolynomial<BigInteger>> a;
            GenPolynomial<GenPolynomial<BigInteger>> b = pqfac.random(2, 3, el, q);
            //b = b.monic();
            //b = b.multiply(b);
            GenPolynomial<GenPolynomial<BigInteger>> c = pqfac.random(2, 3, el, q);
            //c = c.monic();
            if ( c.degree() < 1 ) {
                c = x2.subtract(pqfac.getONE().multiply(t));
            }
            if ( b.degree() < 1 ) {
                b = x.sum(pqfac.getONE());
            }

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

            SortedMap<GenPolynomial<GenPolynomial<BigInteger>>, Long> sm = fac.recursiveFactors(a);
            //System.out.println("\na   = " + a);
            System.out.println("sm = " + sm);

            if (sm.size() >= facs) {
                assertTrue("#facs < " + facs, sm.size() >= facs);
            } else {
                long sf = 0;
                for (Long e : sm.values()) {
                    sf += e;
                }
                assertTrue("#facs < " + facs, sf >= facs);
            }

            boolean tt = fac.isRecursiveFactorization(a, sm);
            //System.out.println("t        = " + tt);
            assertTrue("prod(factor(a)) = a", tt);
        }
        ComputerThreads.terminate();
    }


    /**
     * Test rational function factorization.
     * 
     */
    public void xtestRationalFunctionFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
        String[] qvars = new String[]{ "t" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to, qvars);
        QuotientRing<BigRational> qfac = new QuotientRing<BigRational>(pfac);
        Quotient<BigRational> t = qfac.generators().get(1);

        FactorQuotient<BigRational> fac = new FactorQuotient<BigRational>(qfac);

        String[] vars = new String[]{ "x" };
        GenPolynomialRing<Quotient<BigRational>> pqfac = new GenPolynomialRing<Quotient<BigRational>>(qfac,1,to,vars);
        GenPolynomial<Quotient<BigRational>> x  = pqfac.univariate(0);
        GenPolynomial<Quotient<BigRational>> x2 = pqfac.univariate(0,2);

        for (int i = 1; i < 3; i++) {
            int facs = 0;
            GenPolynomial<Quotient<BigRational>> a;
            GenPolynomial<Quotient<BigRational>> b = pqfac.random(2, 3, el, q);
            //b = b.monic();
            //b = b.multiply(b);
            GenPolynomial<Quotient<BigRational>> c = pqfac.random(2, 3, el, q);
            //c = c.monic();
            if ( c.degree() < 1 ) {
                c = x2.subtract(pqfac.getONE().multiply(t));
            }
            if ( b.degree() < 1 ) {
                b = x.sum(pqfac.getONE());
            }

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

            SortedMap<GenPolynomial<Quotient<BigRational>>, Long> sm = fac.factors(a);
            //System.out.println("\na   = " + a);
            System.out.println("sm = " + sm);

            if (sm.size() >= facs) {
                assertTrue("#facs < " + facs, sm.size() >= facs);
            } else {
                long sf = 0;
                for (Long e : sm.values()) {
                    sf += e;
                }
                assertTrue("#facs < " + facs, sf >= facs);
            }

            boolean tt = fac.isFactorization(a, sm);
            //System.out.println("t        = " + tt);
            assertTrue("prod(factor(a)) = a", tt);
        }
        ComputerThreads.terminate();
    }


    /**
     * Test modular rational function factorization.
     * 
     */
    public void xtestModularRationalFunctionFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        ModIntegerRing cfac = new ModIntegerRing(19,true);
        String[] qvars = new String[]{ "t" };
        GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(cfac, 1, to, qvars);
        QuotientRing<ModInteger> qfac = new QuotientRing<ModInteger>(pfac);
        Quotient<ModInteger> t = qfac.generators().get(1);

        FactorQuotient<ModInteger> fac = new FactorQuotient<ModInteger>(qfac);

        String[] vars = new String[]{ "x" };
        GenPolynomialRing<Quotient<ModInteger>> pqfac = new GenPolynomialRing<Quotient<ModInteger>>(qfac,1,to,vars);
        GenPolynomial<Quotient<ModInteger>> x  = pqfac.univariate(0);
        GenPolynomial<Quotient<ModInteger>> x2 = pqfac.univariate(0,2);

        for (int i = 1; i < 3; i++) {
            int facs = 0;
            GenPolynomial<Quotient<ModInteger>> a;
            GenPolynomial<Quotient<ModInteger>> b = pqfac.random(2, 3, el, q);
            //b = b.monic();
            //b = b.multiply(b);
            GenPolynomial<Quotient<ModInteger>> c = pqfac.random(2, 3, el, q);
            //c = c.monic();
            if ( c.degree() < 1 ) {
                c = x2.subtract(pqfac.getONE().multiply(t));
            }
            if ( b.degree() < 1 ) {
                b = x.sum(pqfac.getONE());
            }

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

            SortedMap<GenPolynomial<Quotient<ModInteger>>, Long> sm = fac.factors(a);
            //System.out.println("\na   = " + a);
            System.out.println("sm = " + sm);

            if (sm.size() >= facs) {
                assertTrue("#facs < " + facs, sm.size() >= facs);
            } else {
                long sf = 0;
                for (Long e : sm.values()) {
                    sf += e;
                }
                assertTrue("#facs < " + facs, sf >= facs);
            }

            boolean tt = fac.isFactorization(a, sm);
            //System.out.println("t        = " + tt);
            assertTrue("prod(factor(a)) = a", tt);
        }
        ComputerThreads.terminate();
    }


    /**
     * Test modular quotient factorization.
     * 
     */
    public void testModularQuotientFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        ModIntegerRing cfac = new ModIntegerRing(19,true);
        String[] qvars = new String[]{ "t" };
        GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(cfac, 1, to, qvars);
        QuotientRing<ModInteger> qfac = new QuotientRing<ModInteger>(pfac);
        Quotient<ModInteger> t = qfac.generators().get(1);

        FactorQuotient<ModInteger> fac = new FactorQuotient<ModInteger>(qfac);

        for (int i = 1; i < 2; i++) {
            int facs = 0;
            Quotient<ModInteger> a;
            Quotient<ModInteger> b = qfac.random(3, 3, el, q);
            b = b.multiply(b);
            Quotient<ModInteger> c = qfac.random(3, 3, el, q);
            c = c.multiply(c);

            if (c.num.degree() > 0) {
                facs++;
            }
            if (b.num.degree() > 0) {
                facs++;
            }
            a = c.multiply(b);
            a = a.multiply(qfac.fromInteger(5));
            if (a.isZERO()) {
                facs = 0;
            }

            System.out.println("\na = " + a);
            System.out.println("b = " + b);
            System.out.println("c = " + c);

            SortedMap<Quotient<ModInteger>, Long> sm = fac.quotientSquarefreeFactors(a);
            //SortedMap<Quotient<ModInteger>, Long> sm = fac.quotientFactors(a);
            //List<Quotient<ModInteger>> sm = fac.quotientFactorsSquarefree(a);
            System.out.println("\na   = " + a);
            System.out.println("sm = " + sm);

            if (sm.size() >= facs) {
                assertTrue("#facs < " + facs, sm.size() >= facs);
            } else {
                long sf = 0;
                for (Long e : sm.values()) {
                    sf += e;
                }
                assertTrue("#facs < " + facs, sf >= facs);
            }

            boolean tt = fac.isFactorization(a, sm);
            //System.out.println("t        = " + tt);
            assertTrue("prod(factor(a)) = a", tt);
        }
        ComputerThreads.terminate();
    }


    /**
     * Test modular quotient characteristic-th root.
     * 
     */
    public void xtestModularQuotientCharRoot() {

        long p = 19L;
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        ModIntegerRing cfac = new ModIntegerRing(p,true);
        String[] qvars = new String[]{ "t" };
        GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(cfac, 1, to, qvars);
        QuotientRing<ModInteger> qfac = new QuotientRing<ModInteger>(pfac);
        Quotient<ModInteger> t = qfac.generators().get(1);

        FactorQuotient<ModInteger> fac = new FactorQuotient<ModInteger>(qfac);

        int facs = 0;
        Quotient<ModInteger> a = qfac.random(3, 3, el, q);
        //b = b.multiply(b);
        //System.out.println("\na = " + a);

        SortedMap<Quotient<ModInteger>, Long> sm = fac.quotientRootCharacteristic(a);
        //System.out.println("\na   = " + a);
        //System.out.println("sm = " + sm);
        if ( sm == null ) {
            assertTrue("rootCharacteristic(a) = null", sm == null);
        }

        a = Power.<Quotient<ModInteger>> positivePower(t, p*3L);
        //System.out.println("\na = " + a);

        sm = fac.quotientRootCharacteristic(a);
        //System.out.println("\na   = " + a);
        //System.out.println("sm = " + sm);
        assertTrue("rootCharacteristic(a) = {t,1}", sm.size() == 1);

        Quotient<ModInteger> b = t.multiply(t).sum(qfac.getONE());
        a = Power.<Quotient<ModInteger>> positivePower(b, p);
        //System.out.println("\nb = " + b);
        //System.out.println("a = " + a);

        sm = fac.quotientRootCharacteristic(a);
        //System.out.println("\na   = " + a);
        //System.out.println("sm = " + sm);
        assertTrue("rootCharacteristic(a) = sm", fac.isCharRoot(a,sm));

        Quotient<ModInteger> c = qfac.random(3,3,el,q);
        if ( c.isONE() || c.isZERO() ) {
            c = b;
        }
        a = Power.<Quotient<ModInteger>> positivePower(c, p);
        //System.out.println("\nc = " + c);
        //System.out.println("a = " + a);

        sm = fac.quotientRootCharacteristic(a);
        //System.out.println("\na   = " + a);
        System.out.println("sm = " + sm);
        assertTrue("rootCharacteristic(a) = sm", fac.isCharRoot(a,sm));

        ComputerThreads.terminate();
    }


    /**
     * Test modular polynomial characteristic-th root.
     * 
     */
    public void xtestModularPolynomialCharRoot() {

        long p = 19L;
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        ModIntegerRing cfac = new ModIntegerRing(p,true);
        String[] qvars = new String[]{ "t" };
        GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(cfac, 1, to, qvars);
        QuotientRing<ModInteger> qfac = new QuotientRing<ModInteger>(pfac);
        GenPolynomial<ModInteger> t = pfac.generators().get(1);

        FactorQuotient<ModInteger> fac = new FactorQuotient<ModInteger>(qfac);

        int facs = 0;
        GenPolynomial<ModInteger> a = pfac.random(3, 3, el, q);
        //a = a.monic();
        //System.out.println("\na = " + a);

        SortedMap<GenPolynomial<ModInteger>, Long> sm = fac.rootCharacteristic(a);
        //System.out.println("\na   = " + a);
        //System.out.println("sm = " + sm);
        if ( sm == null ) {
            assertTrue("rootCharacteristic(a) = null", sm == null);
        }

        a = Power.<GenPolynomial<ModInteger>> positivePower(t, p*3L);
        System.out.println("\na = " + a);

        sm = fac.rootCharacteristic(a);
        //System.out.println("\na   = " + a);
        //System.out.println("sm = " + sm);
        assertTrue("rootCharacteristic(a) = sm", fac.isCharRoot(a,sm));

        GenPolynomial<ModInteger> b = t.multiply(t).multiply(pfac.fromInteger(7)).sum(pfac.getONE());
        //b = b.monic();
        a = Power.<GenPolynomial<ModInteger>> positivePower(b, p);
        System.out.println("\nb = " + b);
        System.out.println("a = " + a);

        sm = fac.rootCharacteristic(a);
        System.out.println("\na   = " + a);
        System.out.println("sm = " + sm);
        assertTrue("rootCharacteristic(a) = sm", fac.isCharRoot(a,sm));


        GenPolynomial<ModInteger> c;
        //c = pfac.random(4,5,el,q);
        //c = pfac.parse("t^2 + t + 5");
        c = pfac.parse("5 t + 10");
        if ( c.isONE() || c.isZERO() || c.isConstant() ) {
            c = b;
        }
        //c = c.monic();
        a = Power.<GenPolynomial<ModInteger>> positivePower(c, p*1L);
        //a = a.multiply( Power.<GenPolynomial<ModInteger>> positivePower(b, p*2L) );
        System.out.println("\nc = " + c);
        System.out.println("a = " + a);
        //System.out.println("c*b*b = " + c.multiply(b).multiply(b));

        sm = fac.rootCharacteristic(a);
        System.out.println("\na   = " + a);
        System.out.println("sm = " + sm);
        assertTrue("rootCharacteristic(a) = sm", fac.isCharRoot(a,sm));

        ComputerThreads.terminate();
    }


    /**
     * Test modular recursive polynomial characteristic-th root.
     * 
     */
    public void xtestModularRecursivePolynomialCharRoot() {

        long p = 19L;
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        ModIntegerRing cfac = new ModIntegerRing(p,true);
        String[] qvars = new String[]{ "t" };
        GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(cfac, 1, to, qvars);
        QuotientRing<ModInteger> qfac = new QuotientRing<ModInteger>(pfac);
        GenPolynomial<ModInteger> t = pfac.generators().get(1);

        FactorQuotient<ModInteger> fac = new FactorQuotient<ModInteger>(qfac);

        String[] vars = new String[]{ "x" };
        GenPolynomialRing<GenPolynomial<ModInteger>> rpfac = new GenPolynomialRing<GenPolynomial<ModInteger>>(pfac, 1, to, vars);
        GenPolynomial<GenPolynomial<ModInteger>> x = rpfac.generators().get(2);

        GenPolynomial<GenPolynomial<ModInteger>> a = rpfac.random(3, 3, el, q);
        if ( a.isConstant() ) {
            a = x.multiply(a).subtract(a);
        }
        System.out.println("\na  = " + a);
        GenPolynomial<GenPolynomial<ModInteger>> b = Power.<GenPolynomial<GenPolynomial<ModInteger>>> positivePower(a, p*1L);
        System.out.println("b  = " + b);

        GenPolynomial<GenPolynomial<ModInteger>> ap = fac.recursiveRootCharacteristic(a);
        System.out.println("\na  = " + a);
        System.out.println("b  = " + b);
        System.out.println("ap = " + ap);
        assertTrue("rootCharacteristic(a) = a ", null == ap);

        GenPolynomial<GenPolynomial<ModInteger>> bp = fac.recursiveRootCharacteristic(b);
        System.out.println("\na  = " + a);
        System.out.println("b  = " + b);
        System.out.println("bp = " + bp);

        //GenPolynomial<GenPolynomial<ModInteger>> r = a.remainder(bp);
        //assertTrue("rootCharacteristic(a**p) = a ", r.isZERO());

        assertTrue("rootCharacteristic(a) = bp", fac.isRecursiveCharRoot(b,bp));

        ComputerThreads.terminate();
    }

}

