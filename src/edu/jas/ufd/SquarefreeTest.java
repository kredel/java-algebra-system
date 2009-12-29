/*
 * $Id$
 */

package edu.jas.ufd;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.application.Quotient;
import edu.jas.application.QuotientRing;
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
import edu.jas.structure.RingFactory;


/**
 * Squarefree Factor tests with JUnit.
 * @author Heinz Kredel.
 */

public class SquarefreeTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        //BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>SquarefreeTest</CODE> object.
     * @param name String.
     */
    public SquarefreeTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(SquarefreeTest.class);
        return suite;
    }


    @Override
    protected void setUp() {
    }


    @Override
    protected void tearDown() {
        ComputerThreads.terminate();
    }


    /**
     * Test factory specific.
     * 
     */
    public void testFactorySpecific() {
        ModIntegerRing mi = new ModIntegerRing(19, true);
        Squarefree<ModInteger> sqfm = SquarefreeFactory.getImplementation(mi);
        //System.out.println("sqfm = " + sqfm);
        assertTrue("sqf != Modular " + sqfm, sqfm instanceof SquarefreeFiniteFieldCharP);

        ModLongRing ml = new ModLongRing(19, true);
        Squarefree<ModLong> sqfml = SquarefreeFactory.getImplementation(ml);
        //System.out.println("sqfml = " + sqfml);
        assertTrue("sqf != Modular " + sqfml, sqfml instanceof SquarefreeFiniteFieldCharP);

        BigInteger bi = new BigInteger(1);
        Squarefree<BigInteger> sqfi = SquarefreeFactory.getImplementation(bi);
        //System.out.println("sqfi = " + sqfi);
        assertTrue("sqf != Integer " + sqfi, sqfi instanceof SquarefreeRingChar0);

        BigRational br = new BigRational(1);
        Squarefree<BigRational> sqfr = SquarefreeFactory.getImplementation(br);
        //System.out.println("sqfr = " + sqfr);
        assertTrue("sqf != Rational " + sqfr, sqfr instanceof SquarefreeFieldChar0);

        GenPolynomialRing<ModInteger> pmfac = new GenPolynomialRing<ModInteger>(mi, 1);
        GenPolynomial<ModInteger> pm = pmfac.univariate(0);
        AlgebraicNumberRing<ModInteger> am = new AlgebraicNumberRing<ModInteger>(pm, true);
        Squarefree<AlgebraicNumber<ModInteger>> sqfam = SquarefreeFactory.<ModInteger> getImplementation(am);
        //System.out.println("sqfam = " + sqfam);
        assertTrue("sqf != AlgebraicNumber<ModInteger> " + sqfam, sqfam instanceof SquarefreeFiniteFieldCharP);

        GenPolynomialRing<BigRational> prfac = new GenPolynomialRing<BigRational>(br, 1);
        GenPolynomial<BigRational> pr = prfac.univariate(0);
        AlgebraicNumberRing<BigRational> ar = new AlgebraicNumberRing<BigRational>(pr, true);
        Squarefree<AlgebraicNumber<BigRational>> sqfar = SquarefreeFactory
                .<BigRational> getImplementation(ar);
        //System.out.println("sqfar = " + sqfar);
        assertTrue("sqf != AlgebraicNumber<BigRational> " + sqfar, sqfar instanceof SquarefreeFieldChar0);

        prfac = new GenPolynomialRing<BigRational>(br, 2);
        QuotientRing<BigRational> qrfac = new QuotientRing<BigRational>(prfac);
        Squarefree<Quotient<BigRational>> sqfqr = SquarefreeFactory.<BigRational> getImplementation(qrfac);
        //System.out.println("sqfqr = " + sqfqr);
        assertTrue("sqf != Quotient<BigRational> " + sqfqr, sqfqr instanceof SquarefreeFieldChar0);

        pmfac = new GenPolynomialRing<ModInteger>(mi, 1);
        QuotientRing<ModInteger> qmfac = new QuotientRing<ModInteger>(pmfac);
        Squarefree<Quotient<ModInteger>> sqfqm = SquarefreeFactory.<ModInteger> getImplementation(qmfac);
        //System.out.println("sqfqm = " + sqfqm);
        assertTrue("sqf != Quotient<ModInteger> " + sqfqm, sqfqm instanceof SquarefreeInfiniteFieldCharP);
    }


    /**
     * Test factory generic.
     * 
     */
    @SuppressWarnings("unchecked")
    public void testFactoryGeneric() {
        ModIntegerRing mi = new ModIntegerRing(19, true);
        Squarefree<ModInteger> sqfm = SquarefreeFactory.getImplementation((RingFactory) mi);
        //System.out.println("sqfm = " + sqfm);
        assertTrue("sqf != Modular " + sqfm, sqfm instanceof SquarefreeFiniteFieldCharP);

        ModLongRing ml = new ModLongRing(19, true);
        Squarefree<ModLong> sqfml = SquarefreeFactory.getImplementation((RingFactory)ml);
        //System.out.println("sqfml = " + sqfml);
        assertTrue("sqf != Modular " + sqfml, sqfml instanceof SquarefreeFiniteFieldCharP);

        BigInteger bi = new BigInteger(1);
        Squarefree<BigInteger> sqfi = SquarefreeFactory.getImplementation((RingFactory) bi);
        //System.out.println("sqfi = " + sqfi);
        assertTrue("sqf != Integer " + sqfi, sqfi instanceof SquarefreeRingChar0);

        BigRational br = new BigRational(1);
        Squarefree<BigRational> sqfr = SquarefreeFactory.getImplementation((RingFactory) br);
        //System.out.println("sqfr = " + sqfr);
        assertTrue("sqf != Rational " + sqfr, sqfr instanceof SquarefreeFieldChar0);

        GenPolynomialRing<ModInteger> pmfac = new GenPolynomialRing<ModInteger>(mi, 1);
        GenPolynomial<ModInteger> pm = pmfac.univariate(0);
        AlgebraicNumberRing<ModInteger> am = new AlgebraicNumberRing<ModInteger>(pm, true);
        Squarefree<AlgebraicNumber<ModInteger>> sqfam = SquarefreeFactory.getImplementation((RingFactory) am);
        //System.out.println("sqfam = " + sqfam);
        assertTrue("sqf != AlgebraicNumber<ModInteger> " + sqfam, sqfam instanceof SquarefreeFiniteFieldCharP);

        GenPolynomialRing<BigRational> prfac = new GenPolynomialRing<BigRational>(br, 1);
        GenPolynomial<BigRational> pr = prfac.univariate(0);
        AlgebraicNumberRing<BigRational> ar = new AlgebraicNumberRing<BigRational>(pr, true);
        Squarefree<AlgebraicNumber<BigRational>> sqfar = SquarefreeFactory
                .getImplementation((RingFactory) ar);
        //System.out.println("sqfar = " + sqfar);
        assertTrue("sqf != AlgebraicNumber<BigRational> " + sqfar, sqfar instanceof SquarefreeFieldChar0);

        prfac = new GenPolynomialRing<BigRational>(br, 2);
        QuotientRing<BigRational> qrfac = new QuotientRing<BigRational>(prfac);
        Squarefree<Quotient<BigRational>> sqfqr = SquarefreeFactory.getImplementation((RingFactory) qrfac);
        //System.out.println("sqfqr = " + sqfqr);
        assertTrue("sqf != Quotient<BigRational> " + sqfqr, sqfqr instanceof SquarefreeFieldChar0);

        pmfac = new GenPolynomialRing<ModInteger>(mi, 1);
        QuotientRing<ModInteger> qmfac = new QuotientRing<ModInteger>(pmfac);
        Squarefree<Quotient<ModInteger>> sqfqm = SquarefreeFactory.getImplementation((RingFactory) qmfac);
        //System.out.println("sqfqm = " + sqfqm);
        assertTrue("sqf != Quotient<ModInteger> " + sqfqm, sqfqm instanceof SquarefreeInfiniteFieldCharP);
    }

}
