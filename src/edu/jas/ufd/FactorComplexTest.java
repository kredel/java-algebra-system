/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.SortedMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.structure.Complex;
import edu.jas.structure.ComplexRing;


/**
 * Factor complex via algebraic tests with JUnit.
 * @author Heinz Kredel.
 */

public class FactorComplexTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>FactorComplexTest</CODE> object.
     * @param name String.
     */
    public FactorComplexTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(FactorComplexTest.class);
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
     * Test complex via algebraic factorization.
     * 
     */
    public void testComplexFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational rfac = new BigRational(1);
        ComplexRing<BigRational> cfac = new ComplexRing<BigRational>(rfac);
        GenPolynomialRing<Complex<BigRational>> cpfac = new GenPolynomialRing<Complex<BigRational>>(cfac, 1,
                to);
        //System.out.println("cfac  = " + cfac);
        //System.out.println("cpfac = " + cpfac);

        FactorComplex<BigRational> fac = new FactorComplex<BigRational>(cfac);

        for (int i = 1; i < 3; i++) {
            int facs = 0;
            GenPolynomial<Complex<BigRational>> a;
            GenPolynomial<Complex<BigRational>> c = cpfac.random(2, ll + i, el + i, q);
            //a = a.monic();
            GenPolynomial<Complex<BigRational>> b = cpfac.random(2, ll + i, el + i, q);
            if (b.degree() == 0) {
                b = b.multiply(cpfac.univariate(0));
            }
            if (c.degree() > 0) {
                facs++;
            }
            b = b.multiply(b);
            if (b.degree() > 0) {
                facs++;
            }
            a = c.multiply(b);
            //a = a.monic();
            //System.out.println("\na = " + a);
            //System.out.println("b = " + b.monic());
            //System.out.println("c = " + c.monic());

            SortedMap<GenPolynomial<Complex<BigRational>>, Long> sm = fac.baseFactors(a);
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
     * Test complex absolute via algebraic factorization.
     * 
     */
    public void testComplexAbsoluteFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational rfac = new BigRational(1);
        ComplexRing<BigRational> cfac = new ComplexRing<BigRational>(rfac);
        GenPolynomialRing<Complex<BigRational>> cpfac = new GenPolynomialRing<Complex<BigRational>>(cfac, 1,
                to);
        //System.out.println("cfac  = " + cfac);
        //System.out.println("cpfac = " + cpfac);

        FactorComplex<BigRational> fac = new FactorComplex<BigRational>(cfac);

        for (int i = 1; i < 2; i++) {
            int facs = 0;
            GenPolynomial<Complex<BigRational>> a;
            GenPolynomial<Complex<BigRational>> c = cpfac.random(2, ll, el, q);
            //a = a.monic();
            GenPolynomial<Complex<BigRational>> b = cpfac.random(2, ll, el, q);
            if (b.degree() == 0) {
                b = b.multiply(cpfac.univariate(0));
            }
            if (c.degree() > 0) {
                facs++;
            }
            b = b.multiply(b);
            if (b.degree() > 0) {
                facs++;
            }
            a = c.multiply(b);
            //a = a.monic();
            //System.out.println("\na = " + a);
            //System.out.println("b = " + b.monic());
            //System.out.println("c = " + c.monic());

            FactorsMap<Complex<BigRational>> sm = fac.baseFactorsAbsolute(a);
            //System.out.println("\na   =  " + a);
            //System.out.println("sm = " + sm);
            boolean t = fac.isAbsoluteFactorization(sm);
            //System.out.println("t        = " + t);
            assertTrue("prod(factor(a)) = a", t);
        }
    }


    /**
     * Test bivariate complex via algebraic factorization.
     * 
     */
    public void testBivariateComplexFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational rfac = new BigRational(1);
        ComplexRing<BigRational> cfac = new ComplexRing<BigRational>(rfac);
        GenPolynomialRing<Complex<BigRational>> cpfac = new GenPolynomialRing<Complex<BigRational>>(cfac, 2,
                to);
        //System.out.println("cfac  = " + cfac);
        //System.out.println("cpfac = " + cpfac);

        FactorComplex<BigRational> fac = new FactorComplex<BigRational>(cfac);

        for (int i = 1; i < 2; i++) {
            int facs = 0;
            GenPolynomial<Complex<BigRational>> a;
            GenPolynomial<Complex<BigRational>> c = cpfac.random(2, ll + i, el, q);
            //a = a.monic();
            GenPolynomial<Complex<BigRational>> b = cpfac.random(2, ll + i, el, q);
            if (b.degree() == 0) {
                b = b.multiply(cpfac.univariate(0));
            }
            if (c.degree() > 0) {
                facs++;
            }
            if (b.degree() > 0) {
                facs++;
            }
            a = c.multiply(b);
            //a = a.monic();
            //System.out.println("\na = " + a);
            //System.out.println("b = " + b.monic());
            //System.out.println("c = " + c.monic());

            SortedMap<GenPolynomial<Complex<BigRational>>, Long> sm = fac.factors(a);
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

}
