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
 * Factor rational tests with JUnit.
 * @author Heinz Kredel.
 */

public class FactorRationalTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        //BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>FactorRationalTest</CODE> object.
     * @param name String.
     */
    public FactorRationalTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(FactorRationalTest.class);
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
     * Test rational factorization.
     * 
     */
    public void testRationalFactorization() {

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
            if ( b.isZERO() || c.isZERO() ) {
                continue;
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
                assertTrue("#facs < " + facs + ", b = " + b + ", c = " + c + ", sm = " + sm, sf >= facs);
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
    public void testBaseRationalAbsoluteFactorization() {

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
     * Test rational absolute factorization.
     * 
     */
    public void testRationalAbsoluteFactorization() {

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

}
