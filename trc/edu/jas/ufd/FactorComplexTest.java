/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.SortedMap;

import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.poly.TermOrderByName;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Factor complex via algebraic tests with JUnit.
 * @author Heinz Kredel
 */
public class FactorComplexTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
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
     * Test dummy for empty test cases Junit.
     */
    public void xtestDummy() {
    }


    /**
     * Test complex via algebraic factorization.
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
            assertTrue("facs <= #sm", facs <= sm.length());
        }
    }


    /**
     * Test bivariate complex via algebraic factorization.
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


    /**
     * Test bivariate complex factorization. Example from issue 10:
     * https://github.com/kredel/java-algebra-system/issues/10
     */
    public void testComplexFactor() {
        ComplexRing<BigRational> cfac = new ComplexRing<BigRational>(BigRational.ZERO);
        GenPolynomialRing<Complex<BigRational>> cpfac = new GenPolynomialRing<Complex<BigRational>>(cfac,
                        new String[] { "x1", "x0" }, TermOrderByName.INVLEX);
        // GenPolynomial<Complex<BigRational>> a = cpfac.parse("x1^2 + x0^2") ; 
        // GenPolynomial<Complex<BigRational>> a = cpfac.parse("x1^4 + x0^4") ; 
        // GenPolynomial<Complex<BigRational>> a = cpfac.parse("x1^8 + x0^8") ; 
        GenPolynomial<Complex<BigRational>> a = cpfac.parse("x1^12 - x0^12");
        FactorComplex<BigRational> factorAbstract = new FactorComplex<BigRational>(cfac);
        //System.out.println("factorAbstract = " + factorAbstract);
        //System.out.println("factorFac      = " + FactorFactory.getImplementation(cfac));
        SortedMap<GenPolynomial<Complex<BigRational>>, Long> map = factorAbstract.factors(a);

        for (SortedMap.Entry<GenPolynomial<Complex<BigRational>>, Long> entry : map.entrySet()) {
            if (entry.getKey().isONE() && entry.getValue().equals(1L)) {
                continue;
            }
            assertTrue("degree <= 2 ", entry.getKey().degree() <= 2);
            // System.out.print(" ( " + entry.getKey().toScript() + " )");
            // if (!entry.getValue().equals(1L)) {
            //     System.out.print(" ^ " + entry.getValue());
            // }
            // System.out.println();
        }
        boolean t = factorAbstract.isFactorization(a, map);
        //System.out.println("t        = " + t);
        assertTrue("prod(factor(a)) = a", t);
    }

}
