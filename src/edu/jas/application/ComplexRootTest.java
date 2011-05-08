/*
 * $Id$
 */

package edu.jas.application;


import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.root.ComplexRoots;
import edu.jas.root.ComplexRootsSturm;
import edu.jas.root.Rectangle;
import edu.jas.structure.Power;
import edu.jas.ufd.Squarefree;
import edu.jas.ufd.SquarefreeFactory;


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
        ceps = new Complex<BigRational>(cfac, eps);
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
    public void xtestComplexRootsImag() {
        //Complex<BigRational> I = cfac.getIMAG(); 
        //a = dfac.parse("z^3 - i2");
        //a = dfac.random(ll+1).monic();
        //a = dfac.parse("z^7 - 2 z");
        a = dfac.parse("z^6 - i2");
        //System.out.println("a = " + a);

        List<Complex<RealAlgebraicNumber<BigRational>>> roots;
        roots = RootFactory.<BigRational> complexAlgebraicNumbersComplex(a);
        //System.out.println("a = " + a);
        //System.out.println("roots = " + roots);
        assertTrue("#roots == deg(a) ", roots.size() == a.degree(0));
        for (Complex<RealAlgebraicNumber<BigRational>> root : roots) {
            //System.out.println("root = " + root.getRe().decimalMagnitude() + " + " + root.getIm().decimalMagnitude() + " i");
        }
    }


    /*
     * Test complex roots, random polynomial.
     */
    public void xtestComplexRootsRand() {
        //Complex<BigRational> I = cfac.getIMAG(); 
        a = dfac.random(ll + 1).monic();
        if (a.isZERO() || a.isONE()) {
            a = dfac.parse("z^6 - i2");
        }
        Squarefree<Complex<BigRational>> sqf = SquarefreeFactory
                        .<Complex<BigRational>> getImplementation(cfac);
        a = sqf.squarefreePart(a);
        //System.out.println("a = " + a);
        List<Complex<RealAlgebraicNumber<BigRational>>> roots;
        roots = RootFactory.<BigRational> complexAlgebraicNumbersComplex(a);
        //System.out.println("a = " + a);
        //System.out.println("roots = " + roots);
        assertTrue("#roots == deg(a): " + (roots.size() - a.degree(0)) + ", a = " + a,
                        roots.size() == a.degree(0));
        for (Complex<RealAlgebraicNumber<BigRational>> root : roots) {
            //System.out.println("root = " + root.getRe().decimalMagnitude() + " + " + root.getIm().decimalMagnitude() + " i");
        }
    }


    /**
     * Test polynomial with complex roots.
     */
    public void testPolynomialComplexRoots() {
        a = dfac.parse("z^3 - 3");
        //System.out.println("a = " + a);
        List<Complex<RealAlgebraicNumber<BigRational>>> roots = RootFactory
                        .<BigRational> complexAlgebraicNumbersComplex(a);
        //System.out.println("a = " + a);
        //System.out.println("roots = " + roots);
        assertTrue("#roots == deg(a) ", roots.size() == a.degree(0));
        Complex<RealAlgebraicNumber<BigRational>> root = roots.get(2); // 1)
        //System.out.println("a = " + a);
        //System.out.println("root = " + root.getRe().decimalMagnitude() + " + "
        //                + root.getIm().decimalMagnitude() + " i");
        //System.out.println("root = " + root.getRe() + " + " + root.getIm() + " i");
        String vre = root.getRe().toString().replace("{", "").replace("}", "").trim();
        String vim = root.getIm().toString().replace("{", "").replace("}", "").trim();
        //System.out.println("vre = " + vre);
        //System.out.println("vim = " + vim);
        String IM = root.ring.getIMAG().toString().replace("{", "").replace("}", "").replace(" ", "").trim();
        //System.out.println("IM  = " + IM);

        GenPolynomialRing<Complex<RealAlgebraicNumber<BigRational>>> cring 
           = new GenPolynomialRing<Complex<RealAlgebraicNumber<BigRational>>>(root.ring, new String[] { "t" });

        GenPolynomial<Complex<RealAlgebraicNumber<BigRational>>> cpol;
        //cpol = cring.random(2, 3, 3, q);
        //cpol = cring.univariate(0,3L).subtract(cring.fromInteger(2L));
        //String vpol = vre + " + " + IM + " " + vim;
        String vpol = IM + " " + vim;
        //String vpol = " 3 ";// + vre; // + " " + IM;
        //System.out.println("vpol = " + vpol);
        cpol = cring.univariate(0, 2L).subtract(cring.parse(vpol));
        cpol = cpol.monic();
        //System.out.println("cpol = " + cpol);

        // new version with recursion: now possible with real factorization
        List<Complex<RealAlgebraicNumber<RealAlgebraicNumber<BigRational>>>> croots = RootFactory
                        .<RealAlgebraicNumber<BigRational>> complexAlgebraicNumbersComplex(cpol);
        //System.out.println("\na = " + a);
        //System.out.println("root = " + root.getRe().decimalMagnitude() + " + "
        //               + root.getIm().decimalMagnitude() + " i");
        //System.out.println("cpol = " + cpol);
        //System.out.println("croots = " + croots);
        for (Complex<RealAlgebraicNumber<RealAlgebraicNumber<BigRational>>> croot : croots) {
            //System.out.println("croot = " + croot);
            Complex<RealAlgebraicNumber<RealAlgebraicNumber<BigRational>>> croot2 = croot.multiply(croot);
            //System.out.println("croot^2 = " + croot2);
            //System.out.println("croot = " + croot.ring);
            //System.out.println("croot = " + croot.getRe() + " + " + croot.getIm() + " i");
            System.out.println("croot = " + croot.getRe().decimalMagnitude() + " + "
                            + croot.getIm().decimalMagnitude() + " i");
            //System.out.println("croot^2 = " + croot2.getRe().decimalMagnitude() + " + "
            //                + croot2.getIm().decimalMagnitude() + " i");
        }

        for (Complex<RealAlgebraicNumber<BigRational>> roo : roots) {
            //System.out.println("root = " + roo.getRe().decimalMagnitude() + " + "
            //                + roo.getIm().decimalMagnitude() + " i");
        }

        assertTrue("#croots == deg(cpol) ", croots.size() == cpol.degree(0));

        if (true)
            return;

        ComplexRoots<RealAlgebraicNumber<BigRational>> crs = new ComplexRootsSturm<RealAlgebraicNumber<BigRational>>(
                        root.ring);

        // existing version using winding numbers
        List<Rectangle<RealAlgebraicNumber<BigRational>>> r2roots = crs.complexRoots(cpol);
        System.out.println("cpol = " + cpol);
        System.out.println("r2roots = " + r2roots);
        assertTrue("#r2roots == deg(cpol) ", r2roots.size() == cpol.degree(0));
        for (Rectangle<RealAlgebraicNumber<BigRational>> r2 : r2roots) {
            System.out.println("r2 = " + r2.getDecimalCenter());
        }

        // old version with winding number and recursion: but only one step
        List<edu.jas.root.ComplexAlgebraicNumber<RealAlgebraicNumber<BigRational>>> coroots;
        coroots = edu.jas.root.RootFactory
                        .<RealAlgebraicNumber<BigRational>> complexAlgebraicNumbersComplex(cpol);
        System.out.println("cpol = " + cpol);
        System.out.println("coroots = " + coroots);
        assertTrue("#coroots == deg(cpol) ", coroots.size() == cpol.degree(0));
        for (edu.jas.root.ComplexAlgebraicNumber<RealAlgebraicNumber<BigRational>> cr2 : coroots) {
            System.out.println("r2.ring = " + cr2.ring); //magnitude());
            //System.out.println("r2.mag  = " + cr2.magnitude());
            //System.out.println("r2.dec  = " + cr2.decimalMagnitude());
        }
    }

}
