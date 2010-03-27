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

public class FactorGenericTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        //BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>FactorGenericTest</CODE> object.
     * @param name String.
     */
    public FactorGenericTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(FactorGenericTest.class);
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
    public void xtestDummy() {
    }


    /**
     * Test generic factorization.
     * 
     */
    public void testGenericFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
 
        String[] var_w2 = new String[] { "w2" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to, var_w2);
        //System.out.println("pfac   = " + pfac.toScript());

        GenPolynomial<BigRational> w2 = pfac.parse(" w2^2 - 2 ") ;
        //System.out.println("w2     = " + w2);

        AlgebraicNumberRing<BigRational> a2fac = new AlgebraicNumberRing<BigRational>(w2, true);
        //System.out.println("a2fac  = " + a2fac.toScript());

        String[] var_x = new String[] { "x" };
        GenPolynomialRing<AlgebraicNumber<BigRational>> apfac 
            = new GenPolynomialRing<AlgebraicNumber<BigRational>>(a2fac, 1, to, var_x);
        //System.out.println("apfac  = " + apfac.toScript());

        QuotientRing<AlgebraicNumber<BigRational>> qfac 
            = new QuotientRing<AlgebraicNumber<BigRational>>(apfac);
        //System.out.println("qfac   = " + qfac.toScript());

        String[] var_wx = new String[] { "wx" };
        GenPolynomialRing<Quotient<AlgebraicNumber<BigRational>>> pqfac 
            = new GenPolynomialRing<Quotient<AlgebraicNumber<BigRational>>>(qfac,1,to,var_wx);
        //System.out.println("pqfac  = " + pqfac.toScript());

        GenPolynomial<Quotient<AlgebraicNumber<BigRational>>> wx = pqfac.parse(" wx^2 - { x } ") ;
        //System.out.println("wx     = " + wx);

        AlgebraicNumberRing<Quotient<AlgebraicNumber<BigRational>>> axfac 
            = new AlgebraicNumberRing<Quotient<AlgebraicNumber<BigRational>>>(wx, true);
        //System.out.println("axfac  = " + axfac.toScript());

        String[] var_y = new String[] { "y" };
        GenPolynomialRing<AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>>> apqfac 
            = new GenPolynomialRing<AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>>>(axfac,1,to,var_y);
        //System.out.println("apqfac = " + apqfac.toScript());

        //  ( y^2 - x ) * ( y^2 - 2 ), need {} for recursive coefficients
        GenPolynomial<AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>>> f;
        f = apqfac.parse(" ( y^2 - { { x } } ) * ( y^2 - 2 )^2 "); 
        //System.out.println("f      = " + f);

        FactorAbstract<AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>>> engine = FactorFactory.getImplementation(axfac);
        //System.out.println("engine = " + engine);

        SortedMap<GenPolynomial<AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>>>,Long> F = engine.factors(f);
        //System.out.println("factors(f) = " + F);

        assertTrue("#facs >= 4", F.size() >= 4);

        boolean t = engine.isFactorization(f, F);
        //System.out.println("t        = " + t);
        assertTrue("prod(factor(a)) = a", t);
    }

}
