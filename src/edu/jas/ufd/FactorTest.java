/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.SortedMap;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.application.Quotient;
import edu.jas.application.QuotientRing;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.Modular;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.PrimeList;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


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
     * Test factory.
     * 
     */
    public void testFactory() {
        ModIntegerRing mi = new ModIntegerRing(19, true);
        Factorization<ModInteger> ufdm = FactorFactory.getImplementation(mi);
        //System.out.println("ufdm = " + ufdm);
        assertTrue("ufd != Modular " + ufdm, ufdm instanceof FactorModular);

        ModLongRing ml = new ModLongRing(19, true);
        Factorization<ModLong> ufdml = FactorFactory.getImplementation(ml);
        //System.out.println("ufdml = " + ufdml);
        assertTrue("ufd != Modular " + ufdml, ufdml instanceof FactorModular);

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
     * Test factory generic.
     * 
     */
    @SuppressWarnings("unchecked")
    public void testFactoryGeneric() {
        ModIntegerRing mi = new ModIntegerRing(19, true);
        Factorization<ModInteger> ufdm = FactorFactory.getImplementation((RingFactory) mi);
        //System.out.println("ufdm = " + ufdm);
        assertTrue("ufd != Modular " + ufdm, ufdm instanceof FactorModular);

        BigInteger bi = new BigInteger(1);
        Factorization<BigInteger> ufdi = FactorFactory.getImplementation((RingFactory) bi);
        //System.out.println("ufdi = " + ufdi);
        assertTrue("ufd != Integer " + ufdi, ufdi instanceof FactorInteger);

        BigRational br = new BigRational(1);
        Factorization<BigRational> ufdr = FactorFactory.getImplementation((RingFactory) br);
        //System.out.println("ufdr = " + ufdr);
        assertTrue("ufd != Rational " + ufdr, ufdr instanceof FactorRational);

        GenPolynomialRing<ModInteger> pmfac = new GenPolynomialRing<ModInteger>(mi, 1);
        GenPolynomial<ModInteger> pm = pmfac.univariate(0);
        AlgebraicNumberRing<ModInteger> am = new AlgebraicNumberRing<ModInteger>(pm, true);
        Factorization<AlgebraicNumber<ModInteger>> ufdam = FactorFactory.getImplementation((RingFactory) am);
        //System.out.println("ufdam = " + ufdam);
        assertTrue("ufd != AlgebraicNumber<ModInteger> " + ufdam, ufdam instanceof FactorAlgebraic);

        GenPolynomialRing<BigRational> prfac = new GenPolynomialRing<BigRational>(br, 1);
        GenPolynomial<BigRational> pr = prfac.univariate(0);
        AlgebraicNumberRing<BigRational> ar = new AlgebraicNumberRing<BigRational>(pr, true);
        Factorization<AlgebraicNumber<BigRational>> ufdar = FactorFactory.getImplementation((RingFactory) ar);
        //System.out.println("ufdar = " + ufdar);
        assertTrue("ufd != AlgebraicNumber<BigRational> " + ufdar, ufdar instanceof FactorAlgebraic);

        prfac = new GenPolynomialRing<BigRational>(br, 2);
        QuotientRing<BigRational> qrfac = new QuotientRing<BigRational>(prfac);
        Factorization<Quotient<BigRational>> ufdqr = FactorFactory.getImplementation((RingFactory) qrfac);
        //System.out.println("ufdqr = " + ufdqr);
        assertTrue("ufd != Quotient<BigRational> " + ufdqr, ufdqr instanceof FactorQuotient);

        pmfac = new GenPolynomialRing<ModInteger>(mi, 1);
        QuotientRing<ModInteger> qmfac = new QuotientRing<ModInteger>(pmfac);
        Factorization<Quotient<ModInteger>> ufdqm = FactorFactory.getImplementation((RingFactory) qmfac);
        //System.out.println("ufdqm = " + ufdqm);
        assertTrue("ufd != Quotient<ModInteger> " + ufdqm, ufdqm instanceof FactorQuotient);

        prfac = new GenPolynomialRing<BigRational>(br, 2);
        GenPolynomialRing<GenPolynomial<BigRational>> rrfac = new GenPolynomialRing<GenPolynomial<BigRational>>(prfac,1);
        Factorization<BigRational> ufdrr = FactorFactory.getImplementation((RingFactory) rrfac);
        //System.out.println("ufdrr = " + ufdrr);
        assertTrue("ufd != GenPolynomial<GenPolynomialBigRational>> " + ufdrr, ufdrr instanceof FactorRational);
    }


    /**
     * Test factory specific.
     * 
     */
    public void testFactorySpecific() {
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
        Factorization<AlgebraicNumber<ModInteger>> ufdam = FactorFactory.<ModInteger> getImplementation(am);
        //System.out.println("ufdam = " + ufdam);
        assertTrue("ufd != AlgebraicNumber<ModInteger> " + ufdam, ufdam instanceof FactorAlgebraic);

        GenPolynomialRing<BigRational> prfac = new GenPolynomialRing<BigRational>(br, 1);
        GenPolynomial<BigRational> pr = prfac.univariate(0);
        AlgebraicNumberRing<BigRational> ar = new AlgebraicNumberRing<BigRational>(pr, true);
        Factorization<AlgebraicNumber<BigRational>> ufdar = FactorFactory.<BigRational> getImplementation(ar);
        //System.out.println("ufdar = " + ufdar);
        assertTrue("ufd != AlgebraicNumber<BigRational> " + ufdar, ufdar instanceof FactorAlgebraic);

        prfac = new GenPolynomialRing<BigRational>(br, 2);
        QuotientRing<BigRational> qrfac = new QuotientRing<BigRational>(prfac);
        Factorization<Quotient<BigRational>> ufdqr = FactorFactory.<BigRational> getImplementation(qrfac);
        //System.out.println("ufdqr = " + ufdqr);
        assertTrue("ufd != Quotient<BigRational> " + ufdqr, ufdqr instanceof FactorQuotient);

        pmfac = new GenPolynomialRing<ModInteger>(mi, 1);
        QuotientRing<ModInteger> qmfac = new QuotientRing<ModInteger>(pmfac);
        Factorization<Quotient<ModInteger>> ufdqm = FactorFactory.<ModInteger> getImplementation(qmfac);
        //System.out.println("ufdqm = " + ufdqm);
        assertTrue("ufd != Quotient<ModInteger> " + ufdqm, ufdqm instanceof FactorQuotient);

        prfac = new GenPolynomialRing<BigRational>(br, 2);
        GenPolynomialRing<GenPolynomial<BigRational>> rrfac = new GenPolynomialRing<GenPolynomial<BigRational>>(prfac,1);
        Factorization<BigRational> ufdrr = FactorFactory.<BigRational>getImplementation(prfac);
        //System.out.println("ufdrr = " + ufdrr);
        assertTrue("ufd != GenPolynomial<GenPolynomialBigRational>> " + ufdrr, ufdrr instanceof FactorRational);
    }


    /**
     * Test rational absolute factorization, Rothstein-Trager step.
     * 
     */
    public void xtestBaseRationalAbsoluteFactorizationRoT() {

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
        //System.out.println("\npartial fraction = " + F);

        //boolean t = engine.isAbsoluteFactorization(F);
        //System.out.println("t        = " + t);
        // assertTrue("prod(factor(a)) = a", t);
    }

}
