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
 * Factor quotient tests with JUnit.
 * @author Heinz Kredel.
 */

public class FactorQuotientTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        //BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>FactorQuotientTest</CODE> object.
     * @param name String.
     */
    public FactorQuotientTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(FactorQuotientTest.class);
        return suite;
    }


    int rl = 1;


    int kl = 3;


    int ll = 3;


    int el = 3;


    float q = 0.4f;


    QuotientRing<BigRational> efac;


    GenPolynomialRing<BigRational> mfac;


    @Override
    protected void setUp() {
	BigRational cfac = new BigRational(1);
	TermOrder to = new TermOrder( TermOrder.INVLEX );
	mfac = new GenPolynomialRing<BigRational>( cfac, rl, to );
	efac = new QuotientRing<BigRational>( mfac );
    }


    @Override
    protected void tearDown() {
	//efac.terminate();
	efac = null;
	ComputerThreads.terminate();
    }


    /**
     * Test dummy for Junit.
     * 
     */
    public void xtestDummy() {
    }


    /**
     * Test quotient coefficient polynomial factorization.
     * 
     */
    public void testQuotientFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
 
        String[] var_x = new String[] { "x" };
        GenPolynomialRing<Quotient<BigRational>> pfac = new GenPolynomialRing<Quotient<BigRational>>(efac, 1, to, var_x);
        //System.out.println("pfac   = " + pfac.toScript());

        GenPolynomial<Quotient<BigRational>> a = pfac.random(kl,ll,el,q); // will be irreducible most times
        //System.out.println("a      = " + a);

        FactorAbstract<Quotient<BigRational>> engine = FactorFactory.getImplementation(efac);
        //System.out.println("engine = " + engine);

	SortedMap<GenPolynomial<Quotient<BigRational>>,Long> sm = engine.factors(a);
        //System.out.println("factors(a) = " + sm);

        assertTrue("#facs >= 1", sm.size() >= 1);

 	boolean t = engine.isFactorization(a, sm);
 	//System.out.println("t        = " + t);
 	assertTrue("prod(factor(a)) = a", t);
    }

}
