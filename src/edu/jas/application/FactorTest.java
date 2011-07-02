/*
 * $Id$
 */

package edu.jas.application;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.FactorRational;
import edu.jas.ufd.Factorization;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;
import edu.jas.ufd.FactorAlgebraic;
import edu.jas.ufd.FactorQuotient;
import edu.jas.ufd.FactorModular;
import edu.jas.ufd.FactorInteger;


/**
 * Factor tests with JUnit.
 * @see edu.jas.ufd.FactorTest
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
     */
    public void testDummy() {
    }


    /**
     * Test factory.
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
        GenPolynomialRing<GenPolynomial<BigRational>> rrfac = new GenPolynomialRing<GenPolynomial<BigRational>>(
                prfac, 1);
        Factorization<BigRational> ufdrr = FactorFactory.getImplementation((RingFactory) rrfac);
        //System.out.println("ufdrr = " + ufdrr);
        assertTrue("ufd != GenPolynomial<GenPolynomialBigRational>> " + ufdrr,
                ufdrr instanceof FactorRational);
    }


    /**
     * Test factory specific.
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
        GenPolynomialRing<GenPolynomial<BigRational>> rrfac = new GenPolynomialRing<GenPolynomial<BigRational>>(
                prfac, 1);
        Factorization<BigRational> ufdrr = FactorFactory.<BigRational> getImplementation(prfac);
        //System.out.println("ufdrr = " + ufdrr);
        assertTrue("ufd != GenPolynomial<GenPolynomialBigRational>> " + ufdrr,
                ufdrr instanceof FactorRational);
    }

}
