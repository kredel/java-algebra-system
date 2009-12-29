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


/**
 * Factor algebraic tests with JUnit.
 * @author Heinz Kredel.
 */

public class FactorAlgebraicTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        //BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>FactorAlgebraicTest</CODE> object.
     * @param name String.
     */
    public FactorAlgebraicTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(FactorAlgebraicTest.class);
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
     * Test algebraic factorization.
     * 
     */
    public void testAlgebraicFactorization() {

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

}
