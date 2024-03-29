/*
 * $Id$
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.List;

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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * RootUtil tests with JUnit.
 * @author Heinz Kredel
 */

public class ComplexRootTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {

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


    GenPolynomialRing<BigRational> rfac;


    GenPolynomialRing<Complex<BigRational>> dfac;


    ComplexRing<BigRational> cfac;


    BigRational eps;


    Complex<BigRational> ceps;


    BigDecimal deps;


    GenPolynomial<Complex<BigRational>> a, b, c, d, e;


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
        dfac = new GenPolynomialRing<Complex<BigRational>>(cfac, to, vars);
        rfac = new GenPolynomialRing<BigRational>(new BigRational(1), to, vars);
        eps = Power.positivePower(new BigRational(1L, 10L), BigDecimal.DEFAULT_PRECISION);
        ceps = new Complex<BigRational>(cfac, eps);
        deps = new BigDecimal(
                        Power.positivePower(new BigRational(1L, 10L), BigDecimal.DEFAULT_PRECISION - 2));
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
        //a = dfac.parse("z^3 - i2"); 
        //a = dfac.random(ll+1).monic();
        //a = dfac.parse("z^7 - 2 z");
        a = dfac.parse("z^6 - i3");
        //System.out.println("a = " + a);

        List<Complex<RealAlgebraicNumber<BigRational>>> roots;
        roots = RootFactoryApp.<BigRational> complexAlgebraicNumbersComplex(a);
        //System.out.println("a = " + a);
        //System.out.println("roots = " + roots);
        assertTrue("#roots == deg(a) ", roots.size() == a.degree(0));
        for (Complex<RealAlgebraicNumber<BigRational>> root : roots) {
            //System.out.println("root = " + root.getRe().decimalMagnitude() + " + " + root.getIm().decimalMagnitude() + " i");
            assertTrue("f(r) == 0: " + root, RootFactoryApp.<BigRational> isRoot(a, root));
        }
    }


    /*
     * Test complex roots, random polynomial.
     */
    public void testComplexRootsRand() {
        //Complex<BigRational> I = cfac.getIMAG(); 
        a = dfac.random(2, ll, el, 0.37f).monic();
        if (a.isZERO() || a.isONE()) {
            a = dfac.parse("z^6 - i3");
        }
        //a = dfac.parse("z^3 - ( 1600/6123i-280/2041 )"); // fail, todo
        //a = dfac.parse("z^3 - ( 1600/6123 )"); // fail, shows only one root
        //a = dfac.parse("z^3 - ( 16/32i-280/2041 )"); // okay, 3 roots
        Squarefree<Complex<BigRational>> sqf = SquarefreeFactory
                        .<Complex<BigRational>> getImplementation(cfac);
        a = sqf.squarefreePart(a);
        //System.out.println("a = " + a);
        List<Complex<RealAlgebraicNumber<BigRational>>> roots;
        roots = RootFactoryApp.<BigRational> complexAlgebraicNumbersComplex(a);
        //roots = RootFactoryApp.<BigRational> complexAlgebraicNumbersSquarefree(a);
        //System.out.println("a = " + a);
        //System.out.println("roots = " + roots);
        assertTrue("#roots - deg(a): " + (roots.size() - a.degree(0)) + ", a = " + a,
                        roots.size() == a.degree(0));
        for (Complex<RealAlgebraicNumber<BigRational>> root : roots) {
            //System.out.println("root = " + root.getRe().decimalMagnitude() + " + " + root.getIm().decimalMagnitude() + " i");
            assertTrue("f(r) == 0: " + root, RootFactoryApp.<BigRational> isRoot(a, root));
        }
    }


    /**
     * Test polynomial with complex roots.
     */
    public void testPolynomialComplexRoots() {
        a = dfac.parse("z^3 - 2");
        //System.out.println("a = " + a);
        List<Complex<RealAlgebraicNumber<BigRational>>> roots = RootFactoryApp
                        .<BigRational> complexAlgebraicNumbersComplex(a);
        //System.out.println("a = " + a);
        //System.out.println("roots = " + roots);
        assertTrue("#roots == deg(a) ", roots.size() == a.degree(0));
        for (Complex<RealAlgebraicNumber<BigRational>> car : roots) {
            //System.out.println("car = " + car);
            RealAlgebraicRing<BigRational> rfac = car.getRe().ring;
            rfac.setField(true); // ?? to check
            assertTrue("isField(rfac) ", rfac.isField());
            assertTrue("f(r) == 0: " + car, RootFactoryApp.<BigRational> isRoot(a, car));
        }
        Complex<RealAlgebraicNumber<BigRational>> root = roots.get(2); // 0,1,2)
        //System.out.println("a = " + a);
        //System.out.println("root = " + root.getRe().decimalMagnitude() + " + "
        //                  + root.getIm().decimalMagnitude() + " i");
        //System.out.println("root = " + root.getRe() + " + " + root.getIm() + " i");
        //String vre = root.getRe().toString().replace("{", "").replace("}", "").trim();
        //String vim = root.getIm().toString().replace("{", "").replace("}", "").trim();
        //System.out.println("vre = " + vre);
        //System.out.println("vim = " + vim);
        //String IM = root.ring.getIMAG().toString().replace("{", "").replace("}", "").replace(" ", "").trim();
        //System.out.println("IM  = " + IM);

        GenPolynomialRing<Complex<RealAlgebraicNumber<BigRational>>> cring = new GenPolynomialRing<Complex<RealAlgebraicNumber<BigRational>>>(
                        root.ring, to, new String[] { "t" });
        //List<GenPolynomial<Complex<RealAlgebraicNumber<BigRational>>>> gens = cring.generators();
        //System.out.println("gens  = " + gens);

        GenPolynomial<Complex<RealAlgebraicNumber<BigRational>>> cpol;
        //cpol = cring.random(1, 4, 4, q);

        //cpol = cring.univariate(0,3L).subtract(cring.fromInteger(2L));
        //cpol = cring.univariate(0,3L).subtract(gens.get(2));
        //cpol = cring.univariate(0,5L).subtract(cring.univariate(0,2L).multiply(root));
        //cpol = cring.univariate(0,4L).subtract(root);
        //cpol = cring.univariate(0,4L).subtract(root.multiply(root));
        //cpol = cring.univariate(0,3L).subtract(cring.univariate(0,1L).multiply(root).sum(root.multiply(root)));
        cpol = cring.univariate(0, 2L).subtract(root.multiply(root)); // okay
        //cpol = cring.univariate(0,3L).subtract(root.multiply(root)); // okay
        //cpol = cring.univariate(0,3L).subtract(root.multiply(root).multiply(root)); // not much sense r^3 = 2
        ///String vpol = vre + " + " + IM + " " + vim;
        //String vpol = " 3 + " + IM + " * 3 ";
        //String vpol = " 3i3 ";
        //String vpol = IM + " " + vim;
        //String vpol = " 2 ";// + vre; // + " " + IM;
        //String vpol = vre; // + " " + IM;
        //System.out.println("vpol = " + vpol);
        //cpol = cring.univariate(0, 3L).subtract(cring.parse(vpol));
        cpol = cpol.monic();
        //System.out.println("cpol = " + cpol);
        //long dd = cpol.degree(0);
        Squarefree<Complex<RealAlgebraicNumber<BigRational>>> sen = SquarefreeFactory
                        .<Complex<RealAlgebraicNumber<BigRational>>> getImplementation(root.ring);
        cpol = sen.squarefreePart(cpol);
        //if (cpol.degree(0) < dd) {
        //System.out.println("cpol = " + cpol);
        //}
        //System.out.println("cpol = " + cpol);

        // new version with recursion: with real factorization
        long t1 = System.currentTimeMillis();
        List<Complex<RealAlgebraicNumber<RealAlgebraicNumber<BigRational>>>> croots = RootFactoryApp
                        .<RealAlgebraicNumber<BigRational>> complexAlgebraicNumbersComplex(cpol);
        t1 = System.currentTimeMillis() - t1;
        assertTrue("nonsense " + t1, t1 >= 0L);
        //System.out.println("\na = " + a.toScript());
        //System.out.println("root = " + root.getRe().decimalMagnitude() + " + "
        //                             + root.getIm().decimalMagnitude() + " i");
        //System.out.println("a = " + a);
        //System.out.println("root = " + root.getRe().decimalMagnitude() + " + "
        //                  + root.getIm().decimalMagnitude() + " i");
        //System.out.println("root = " + root.getRe() + " + (" + root.getIm() + ") i");
        //System.out.println("root.ring = " + root.ring);
        //System.out.println("cpol      = " + cpol);
        //System.out.println("cpol.ring = " + cpol.ring);
        //System.out.println("croots = " + croots);
        for (Complex<RealAlgebraicNumber<RealAlgebraicNumber<BigRational>>> croot : croots) {
            //System.out.println("croot = " + croot);
            //System.out.println("croot = " + croot.getRe() + " + ( " + croot.getIm() + ") i");
            //System.out.println("croot.ring = " + croot.ring); //magnitude());
            //System.out.println("croot = " + croot.getRe().decimalMagnitude() + " + "
            //                              + croot.getIm().decimalMagnitude() + " i");
            assertTrue("f(r) == 0: " + croot,
                            RootFactoryApp.<RealAlgebraicNumber<BigRational>> isRoot(cpol, croot));
        }
        assertTrue("#croots == deg(cpol) " + croots.size() + " != " + cpol.degree(0),
                        croots.size() == cpol.degree(0));


        // existing version with winding number and recursion: but only one step
        long t2 = System.currentTimeMillis();
        List<edu.jas.root.ComplexAlgebraicNumber<RealAlgebraicNumber<BigRational>>> coroots = edu.jas.root.RootFactory
                        .<RealAlgebraicNumber<BigRational>> complexAlgebraicNumbersComplex(cpol);
        t2 = System.currentTimeMillis() - t2;
        assertTrue("nonsense " + t2, t2 >= 0L);
        //System.out.println("\ncpol = " + cpol);
        //System.out.println("root = " + root.getRe() + " + (" + root.getIm() + ") i");
        //System.out.println("root = " + root.getRe().decimalMagnitude() + " + "
        //                             + root.getIm().decimalMagnitude() + " i");
        for (edu.jas.root.ComplexAlgebraicNumber<RealAlgebraicNumber<BigRational>> cr2 : coroots) {
            //System.out.println("r2.ring = " + cr2.ring); //magnitude());
            assertTrue("f(r) == 0: " + cr2, edu.jas.root.RootFactory
                            .<RealAlgebraicNumber<BigRational>> isRootComplex(cpol, cr2));
        }

        // decimal for comparison
        long t3 = System.currentTimeMillis();
        for (Complex<RealAlgebraicNumber<RealAlgebraicNumber<BigRational>>> croot : croots) {
            String crs = croot.getRe().decimalMagnitude() + " + " + croot.getIm().decimalMagnitude() + " i";
            //System.out.println("croot = " + crs);
            assertFalse("crs not empty", crs.length() == 0); // java-5
        }
        t3 = System.currentTimeMillis() - t3;
        assertTrue("nonsense " + t3, t3 >= 0L);
        long t4 = System.currentTimeMillis();
        for (edu.jas.root.ComplexAlgebraicNumber<RealAlgebraicNumber<BigRational>> cr2 : coroots) {
            String crs = cr2.decimalMagnitude().toString();
            //System.out.println("r2.dec  = " + crs);
            assertFalse("crs not empty", crs.length() == 0); // java-5
        }
        t4 = System.currentTimeMillis() - t4;
        assertTrue("nonsense " + t4, t4 >= 0L);
        assertTrue("#coroots == deg(cpol) ", coroots.size() == cpol.degree(0));
        //System.out.println("time, real ideal = " + t1 + "+" + t3 + ", complex winding = " + t2 + "+" + t4 + " milliseconds");
    }


    /**
     * Test 0-dim ideal with complex roots.
     */
    public void testIdealComplexRoots() {
        String[] vars = new String[] { "x", "z" };
        rfac = new GenPolynomialRing<BigRational>(new BigRational(1), to, vars);
        GenPolynomial<BigRational> ar, br;
        ar = rfac.parse("z^3 - 2");
        br = rfac.parse("x^2 - 3");
        //System.out.println("ar = " + ar + ", deg(ar) = " + ar.degree(1));
        //System.out.println("br = " + br + ", deg(br) = " + br.degree(0));
        List<GenPolynomial<BigRational>> M;
        M = new ArrayList<GenPolynomial<BigRational>>();
        M.add(ar);
        M.add(br);
        //System.out.println("M = " + M);

        Ideal<BigRational> I = new Ideal<BigRational>(rfac, M);
        //System.out.println("I = " + I);

        List<List<Complex<BigDecimal>>> roots = PolyUtilApp.<BigRational> complexRootTuples(I, eps);
        //System.out.println("roots = " + roots);
        assertEquals("#roots == deg(a)*deg(b) ", roots.size(), ar.degree(1) * br.degree(0));

        a = PolyUtil.<BigRational> toComplex(dfac, ar);
        b = PolyUtil.<BigRational> toComplex(dfac, br);
        GenPolynomialRing<Complex<BigDecimal>> pfac;
        pfac = new GenPolynomialRing<Complex<BigDecimal>>(new ComplexRing<BigDecimal>(new BigDecimal(1)),
                        rfac);
        GenPolynomial<Complex<BigDecimal>> ac, bc;
        ac = PolyUtil.<BigRational> complexDecimalFromRational(pfac, a);
        bc = PolyUtil.<BigRational> complexDecimalFromRational(pfac, b);
        List<GenPolynomial<Complex<BigDecimal>>> Mc;
        Mc = new ArrayList<GenPolynomial<Complex<BigDecimal>>>();
        Mc.add(ac);
        Mc.add(bc);
        //System.out.println("Mc = " + Mc);
        assertTrue("isComplexRoots: " + roots, PolyUtilApp.<BigRational> isComplexRoots(Mc, roots, deps));
    }


    /**
     * Test 0-dim ideal with real roots.
     */
    public void testIdealRealRoots() {
        String[] vars = new String[] { "x", "z" };
        rfac = new GenPolynomialRing<BigRational>(new BigRational(1), to, vars);
        GenPolynomial<BigRational> ar, br;
        ar = rfac.parse("z^3 - 2");
        br = rfac.parse("x^2 - 3");
        //System.out.println("ar = " + ar + ", deg(ar) = " + ar.degree(1));
        //System.out.println("br = " + br + ", deg(br) = " + br.degree(0));

        List<GenPolynomial<BigRational>> M;
        M = new ArrayList<GenPolynomial<BigRational>>();
        M.add(ar);
        M.add(br);
        //System.out.println("M = " + M);

        Ideal<BigRational> I = new Ideal<BigRational>(rfac, M);
        //System.out.println("I = " + I);

        List<List<BigDecimal>> roots = PolyUtilApp.<BigRational> realRootTuples(I, eps);
        //System.out.println("roots = " + roots);
        assertEquals("#roots == deg(a)*deg(b) ", roots.size(), 2);

        GenPolynomialRing<BigDecimal> pfac;
        pfac = new GenPolynomialRing<BigDecimal>(new BigDecimal(1), rfac);
        List<GenPolynomial<BigDecimal>> Md;
        Md = new ArrayList<GenPolynomial<BigDecimal>>();
        Md.add(PolyUtil.<BigRational> decimalFromRational(pfac, ar));
        Md.add(PolyUtil.<BigRational> decimalFromRational(pfac, br));
        //System.out.println("Md = " + Md);
        assertTrue("isRealRoots: " + roots, PolyUtilApp.<BigRational> isRealRoots(Md, roots, deps));
    }

}
