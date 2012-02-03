/*
 * $Id$
 */

package edu.jas.application;


import java.util.SortedMap;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.root.Interval;
import edu.jas.root.RootUtil;
import edu.jas.ufd.FactorAbstract;


/**
 * Factor real algebraic tests with JUnit.
 * @author Heinz Kredel.
 */

public class FactorRealRealTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>FactorRealRealTest</CODE> object.
     * @param name String.
     */
    public FactorRealRealTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(FactorRealRealTest.class);
        return suite;
    }


    int rl = 1;


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
     * Test real real algebraic factorization.
     */
    public void testRealRealAlgebraicFactorization() {

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational bfac = new BigRational(1);

        ComplexRing<BigRational> cfac = new ComplexRing<BigRational>(bfac);
        String[] vars = new String[] { "z" };
        GenPolynomialRing<Complex<BigRational>> dfac = new GenPolynomialRing<Complex<BigRational>>(cfac, to, vars);
        GenPolynomial<Complex<BigRational>> ap = dfac.parse("z^3 - i2");

        List<Complex<RealAlgebraicNumber<BigRational>>> roots
            = RootFactory.<BigRational> complexAlgebraicNumbersComplex(ap); 
        //System.out.println("ap = " + ap);
        //System.out.println("roots = " + roots);
        assertTrue("#roots == deg(ap) ", roots.size() == ap.degree(0));

        for ( Complex<RealAlgebraicNumber<BigRational>> root : roots ) {
            RealAlgebraicRing<BigRational> rfac = root.getRe().ring;
            rfac.setField(true);
            assertTrue("isField(rfac) ", rfac.isField());
            FactorRealReal<BigRational> fac = new FactorRealReal<BigRational>(rfac);
            //FactorAbstract<RealAlgebraicNumber<BigRational>> fac = FactorFactory.<RealAlgebraicNumber<BigRational>> getImplementation(rfac);
            String[] var = new String[] { "t" };
            GenPolynomialRing<RealAlgebraicNumber<BigRational>> rpfac 
                = new GenPolynomialRing<RealAlgebraicNumber<BigRational>>(rfac, to, var); // univariate

            GenPolynomial<RealAlgebraicNumber<BigRational>> a;
            GenPolynomial<RealAlgebraicNumber<BigRational>> b = rpfac.random(2, ll, el, q);
            GenPolynomial<RealAlgebraicNumber<BigRational>> c = rpfac.random(2, ll, el, q);
            if (b.degree() == 0) {
                b = b.multiply(rpfac.univariate(0));
            }
            //b = b.monic();
            int facs = 0;
            if (c.degree() > 0) {
                facs++;
            }
            if (b.degree() > 0) {
                facs++;
            }
            a = c.multiply(b);
            //a = c;
            //a = a.monic();
            //System.out.println("\na = " + a);
            //System.out.println("b = " + b);
            //System.out.println("c = " + c);
            //System.out.println("b = " + b.monic());
            //System.out.println("c = " + c.monic());

            SortedMap<GenPolynomial<RealAlgebraicNumber<BigRational>>, Long> sm = fac.baseFactors(a);
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
            break;
        }
    }

}
