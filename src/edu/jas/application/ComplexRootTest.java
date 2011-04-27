/*
 * $Id$
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Logger;

import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
import edu.jas.structure.Power;
import edu.jas.ufd.Squarefree;
import edu.jas.ufd.SquarefreeFactory;
import edu.jas.root.ComplexRootsSturm;
import edu.jas.root.ComplexRoots;
import edu.jas.root.Rectangle;


/**
 * RootUtil tests with JUnit.
 * @author Heinz Kredel.
 */

public class ComplexRootTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>ComplexRootTest</CODE> object.
     * @param name String.
     */
    public ComplexRootTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(ComplexRootTest.class);
        return suite;
    }


    TermOrder to = new TermOrder(TermOrder.INVLEX);


    GenPolynomialRing<Complex<BigRational>> dfac;


    ComplexRing<BigRational> cfac;


    BigRational eps;


    Complex<BigRational> ceps;


    GenPolynomial<Complex<BigRational>> a;


    GenPolynomial<Complex<BigRational>> b;


    GenPolynomial<Complex<BigRational>> c;


    GenPolynomial<Complex<BigRational>> d;


    GenPolynomial<Complex<BigRational>> e;


    int rl = 1;


    int kl = 3;


    int ll = 3;


    int el = 5;


    float q = 0.7f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        cfac = new ComplexRing<BigRational>(new BigRational(1));
        String[] vars = new String[] { "z" };
        dfac = new GenPolynomialRing<Complex<BigRational>>(cfac, rl, to, vars);
        eps = Power.positivePower(new BigRational(1L, 10L), BigDecimal.DEFAULT_PRECISION);
        ceps = new Complex<BigRational>(cfac,eps);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        dfac = null;
        cfac = null;
        eps = null;
    }


    /**
     * Test complex roots, imaginary.
     */
    public void testComplexRootsImag() {
        //Complex<BigRational> I = cfac.getIMAG(); 
        a = dfac.parse("z^3 - i2");
        a = dfac.random(ll+1).monic();
        a = dfac.parse("z^7 - 2 z");
        a = dfac.parse("z^6 - i2");
        //System.out.println("a = " + a);

        List<Complex<RealAlgebraicNumber<BigRational>>> roots;
        roots = RootFactory.<BigRational> complexAlgebraicNumbersComplex(a); 
        //System.out.println("a = " + a);
        //System.out.println("roots = " + roots);
        assertTrue("#roots == deg(a) ", roots.size() == a.degree(0));
        for ( Complex<RealAlgebraicNumber<BigRational>> root : roots ) {
            //System.out.println("root = " + root.getRe().decimalMagnitude() + " + " + root.getIm().decimalMagnitude() + " i");
        }
    }


    /*
     * Test complex roots, random polynomial.
     */
    public void testComplexRootsRand() {
        //Complex<BigRational> I = cfac.getIMAG(); 
        a = dfac.random(ll+1).monic();
        if ( a.isZERO() || a.isONE() ) {
             a = dfac.parse("z^6 - i2");
        }
        //System.out.println("a = " + a);
        List<Complex<RealAlgebraicNumber<BigRational>>> roots;
        roots = RootFactory.<BigRational> complexAlgebraicNumbersComplex(a); 
        //System.out.println("a = " + a);
        //System.out.println("roots = " + roots);
        assertTrue("#roots == deg(a) ", roots.size() == a.degree(0));
        for ( Complex<RealAlgebraicNumber<BigRational>> root : roots ) {
            //System.out.println("root = " + root.getRe().decimalMagnitude() + " + " + root.getIm().decimalMagnitude() + " i");
        }
    }


    /**
     * Test polynomial with complex roots.
     */
    public void testPolynomialComplexRoots() {
        a = dfac.parse("z^3 - 2");
        //System.out.println("a = " + a);
        List<Complex<RealAlgebraicNumber<BigRational>>> roots;
        roots = RootFactory.<BigRational> complexAlgebraicNumbersComplex(a); 
        //System.out.println("a = " + a);
        //System.out.println("roots = " + roots);
        assertTrue("#roots == deg(a) ", roots.size() == a.degree(0));
        Complex<RealAlgebraicNumber<BigRational>> root = roots.get(1);
        System.out.println("a = " + a);
        System.out.println("root = " + root.getRe().decimalMagnitude() + " + " + root.getIm().decimalMagnitude() + " i");

        GenPolynomialRing<Complex<RealAlgebraicNumber<BigRational>>> cring 
	    = new GenPolynomialRing<Complex<RealAlgebraicNumber<BigRational>>>(root.ring,1);

        GenPolynomial<Complex<RealAlgebraicNumber<BigRational>>> cpol;
        cpol = cring.random(3);
        cpol = cring.univariate(0,3L).subtract(cring.fromInteger(2L));
        cpol = cring.univariate(0,1L).subtract(cring.parse( root.getRe().toString() ));
        //cpol = cring.univariate(0,1L).subtract(cring.parse( "x3^2 + x4" ));
        System.out.println("cpol = " + cpol);

        ComplexRoots<RealAlgebraicNumber<BigRational>> crs 
            = new ComplexRootsSturm<RealAlgebraicNumber<BigRational>>(root.ring);

        // existing version using winding numbers
        List<Rectangle<RealAlgebraicNumber<BigRational>>> r2roots = crs.complexRoots(cpol);
        System.out.println("r2roots = " + r2roots);
        assertTrue("#r2roots == deg(cpol) ", r2roots.size() == cpol.degree(0));
        for ( Rectangle<RealAlgebraicNumber<BigRational>> r2 : r2roots ) {
            System.out.println("r2 = " + r2.getDecimalCenter());
        }

        // new version with recursion: not yet because of missing real factorization
        //List<Complex<RealAlgebraicNumber<RealAlgebraicNumber<BigRational>>>> croots;
        //croots = RootFactory.<RealAlgebraicNumber<BigRational>> complexAlgebraicNumbersComplex(cpol); 
        //System.out.println("croots = " + croots);
        //assertTrue("#croots == deg(cpol) ", croots.size() == cpol.degree(0));

        // old version with recursion: but only one step
        List<edu.jas.root.ComplexAlgebraicNumber<RealAlgebraicNumber<BigRational>>> coroots;
        coroots = edu.jas.root.RootFactory.<RealAlgebraicNumber<BigRational>> complexAlgebraicNumbersComplex(cpol); 
        System.out.println("coroots = " + coroots);
        assertTrue("#coroots == deg(cpol) ", coroots.size() == cpol.degree(0));
        for ( edu.jas.root.ComplexAlgebraicNumber<RealAlgebraicNumber<BigRational>> cr2 : coroots ) {
            System.out.println("r2 = " + cr2.decimalMagnitude());
        }
    }

}
