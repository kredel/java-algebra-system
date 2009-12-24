/*
 * $Id$
 */

package edu.jas.root;


import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
import edu.jas.structure.Complex;
import edu.jas.structure.ComplexRing;
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
        //BasicConfigurator.configure();
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


    //private final static int bitlen = 100;

    TermOrder to = new TermOrder(TermOrder.INVLEX);


    GenPolynomialRing<Complex<BigRational>> dfac;


    ComplexRing<BigRational> cfac;


    Complex<BigRational> ai;


    Complex<BigRational> bi;


    Complex<BigRational> ci;


    Complex<BigRational> di;


    Complex<BigRational> ei;


    BigRational eps;


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
        ai = bi = ci = di = ei = null;
        cfac = new ComplexRing<BigRational>(new BigRational(1));
        String[] vars = new String[] { "x" };
        dfac = new GenPolynomialRing<Complex<BigRational>>(cfac, rl, to, vars);
        eps = Power.positivePower(new BigRational(1L, 10L), BigDecimal.DEFAULT_PRECISION);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        dfac = null;
        cfac = null;
        eps = null;
    }


    /**
     * Test root bound.
     * 
     */
    public void testRootBound() {
        //a = dfac.random(kl, ll, el, q);
        a = dfac.univariate(0, 2L).sum(dfac.getONE()); // x^2 + 1
        //System.out.println("a = " + a);

        ComplexRootAbstract<BigRational> cr = new ComplexRootsSturm<BigRational>();

        Complex<BigRational> Mb = cr.rootBound(a);
        BigRational M = Mb.getRe();

        //System.out.println("M = " + M);
        assertTrue("M >= 1 ", M.compareTo(BigRational.ONE) >= 0);

        //a = a.monic();
        a = a.multiply(dfac.fromInteger(5));
        //System.out.println("a = " + a);
        M = cr.rootBound(a).getRe();

        //System.out.println("M = " + M);
        assertTrue("M >= 1 ", M.compareTo(BigRational.ONE) >= 0);
    }


    /**
     * Test Cauchy index.
     * 
     */
    public void testCauchyIndex() {
        a = dfac.random(kl, ll, el, q);
        b = dfac.random(kl, ll, el, q);
        //a = dfac.univariate(0,2L).sum(dfac.getONE());  // x^2 + 1
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        BigRational l = new BigRational(0);
        BigRational r = new BigRational(1);
        GenPolynomialRing<BigRational> fac = new GenPolynomialRing<BigRational>(l, dfac);

        ComplexRootsSturm<BigRational> cr = new ComplexRootsSturm<BigRational>();

        GenPolynomial<BigRational> f = PolyUtil.<BigRational> realPartFromComplex(fac, a);
        GenPolynomial<BigRational> g = PolyUtil.<BigRational> imaginaryPartFromComplex(fac, b);
        //System.out.println("re(a) = " + f);
        //System.out.println("im(b) = " + g);

        long ci = cr.indexOfCauchy(l, r, g, f);
        //System.out.println("ci = " + ci);

        assertTrue("ci >= 0 ", ci >= -a.degree(0));
    }


    /**
     * Test Routh.
     * 
     */
    public void testRouth() {
        ComplexRootsSturm<BigRational> cr = new ComplexRootsSturm<BigRational>();

        //a = dfac.random(kl, ll, el, q);
        //b = dfac.random(kl, ll, el, q);
        Complex<BigRational> I = cfac.getIMAG();
        GenPolynomial<Complex<BigRational>> X = dfac.univariate(0);
        //System.out.println("I = " + I);
        //System.out.println("X = " + X);

        //a = dfac.univariate(0,2L).sum( dfac.getONE().multiply(I) );  // x^2 + i

        //b = X.subtract( dfac.getONE().multiply( I ) ); // x - i
        b = X.subtract(dfac.getONE().multiply(I.negate())); // x + i
        c = X.subtract(dfac.getONE().multiply(I.multiply(cfac.fromInteger(3)))); // x - 3i
        d = X.subtract(dfac.getONE().multiply(I.multiply(cfac.fromInteger(4)))); // x - 4i
        e = X.subtract(dfac.getONE().multiply(I.multiply(cfac.fromInteger(5)))); // x - 5i

        a = b.multiply(c).multiply(d).multiply(e);
        //System.out.println("a = " + a.toScript());
        //System.out.println("i = " + cfac.getIMAG());

        Complex<BigRational> Mb = cr.rootBound(a);
        BigRational M = Mb.getRe();
        //System.out.println("M = " + M);

        BigRational minf = M.negate(); // - infinity
        BigRational pinf = M; // + infinity
        GenPolynomialRing<BigRational> fac = new GenPolynomialRing<BigRational>(pinf, dfac);

        GenPolynomial<BigRational> f = PolyUtil.<BigRational> realPartFromComplex(fac, a);
        GenPolynomial<BigRational> g = PolyUtil.<BigRational> imaginaryPartFromComplex(fac, a);
        //System.out.println("re(a) = " + f.toScript());
        //System.out.println("im(a) = " + g.toScript());

        long[] ri = cr.indexOfRouth(minf, pinf, f, g);
        //System.out.println("ri = [" + ri[0] + ", " + ri[1] + " ]");
        long deg = ri[0] + ri[1];
        assertTrue("sum(ri) == deg(a) ", deg >= a.degree(0));
    }


    /**
     * Test winding number.
     * 
     */
    @SuppressWarnings("unchecked")
    public void testWindingNumber() {
        ComplexRootsSturm<BigRational> cr = new ComplexRootsSturm<BigRational>();
        Complex<BigRational> I = cfac.getIMAG();

        a = dfac.univariate(0, 2L).sum(cfac.fromInteger(1)); // x^2 + 1
        //a = dfac.random(kl, ll, el, q);
        //a = dfac.univariate(0,2L).subtract(cfac.getONE());  // x^2 - 1
        //a = dfac.univariate(0,2L).subtract(I);  // x^2 - I
        //a = dfac.univariate(0,1L);  // x
        //System.out.println("a = " + a);

        Complex<BigRational> Mb = cr.rootBound(a);
        BigRational M = Mb.getRe();
        //System.out.println("M = " + M);
        BigRational eps = new BigRational(1, 1000);
        //System.out.println("eps = " + eps);

        Complex<BigRational>[] corner = (Complex<BigRational>[]) new Complex[4];

        corner[0] = new Complex<BigRational>(cfac, M.negate(), M); // nw
        corner[1] = new Complex<BigRational>(cfac, M.negate(), M.negate()); // sw
        corner[2] = new Complex<BigRational>(cfac, M, M.negate()); // se
        corner[3] = new Complex<BigRational>(cfac, M, M); // ne

        long v = cr.windingNumber(new Rectangle<BigRational>(corner), a);
        //System.out.println("winding number = " + v);
        assertTrue("wind(rect,a) == 2 ", v == 2);

        //if ( true ) return;

        corner[0] = new Complex<BigRational>(cfac, M.negate(), M); // nw
        corner[1] = new Complex<BigRational>(cfac, M.negate(), eps); // sw
        corner[2] = new Complex<BigRational>(cfac, M, eps); // se
        corner[3] = new Complex<BigRational>(cfac, M, M); // ne

        v = cr.windingNumber(new Rectangle<BigRational>(corner), a);
        //System.out.println("winding number = " + v);
        assertTrue("wind(rect,a) == 1 ", v == 1);

        corner[0] = new Complex<BigRational>(cfac, eps.negate(), eps); // nw
        corner[1] = new Complex<BigRational>(cfac, eps.negate(), eps.negate()); // sw
        corner[2] = new Complex<BigRational>(cfac, eps, eps.negate()); // se
        corner[3] = new Complex<BigRational>(cfac, eps, eps); // ne

        v = cr.windingNumber(new Rectangle<BigRational>(corner), a);
        //System.out.println("winding number = " + v);
        assertTrue("wind(rect,a) == 0 ", v == 0);
    }


    /**
     * Test complex roots, sqrt(-1).
     * 
     */
    @SuppressWarnings("unchecked")
    public void testComplexRootsImag() {
        ComplexRootAbstract<BigRational> cr = new ComplexRootsSturm<BigRational>();
        Complex<BigRational> I = cfac.getIMAG();

        a = dfac.univariate(0, 2L).sum(cfac.fromInteger(1)); // x^2 + 1
        //a = dfac.univariate(0,2L).subtract(cfac.getONE());  // x^2 - 1
        //a = dfac.univariate(0,2L).subtract(I);  // x^2 - I
        //a = dfac.univariate(0,1L);  // x
        //System.out.println("a = " + a);

        Complex<BigRational> Mb = cr.rootBound(a);
        BigRational M = Mb.getRe();
        //System.out.println("M = " + M);

        Complex<BigRational>[] corner = (Complex<BigRational>[]) new Complex[4];

        corner[0] = new Complex<BigRational>(cfac, M.negate(), M); // nw
        corner[1] = new Complex<BigRational>(cfac, M.negate(), M.negate()); // sw
        corner[2] = new Complex<BigRational>(cfac, M, M.negate()); // se
        corner[3] = new Complex<BigRational>(cfac, M, M); // ne

        Rectangle<BigRational> rect = new Rectangle<BigRational>(corner);

        List<Rectangle<BigRational>> roots = cr.complexRoots(rect, a);
        //System.out.println("roots = " + roots);
        assertTrue("#roots == deg(a) ", roots.size() == a.degree(0));
    }


    /**
     * Test complex roots.
     * 
     */
    @SuppressWarnings("unchecked")
    public void testComplexRootsRand() {
        ComplexRootAbstract<BigRational> cr = new ComplexRootsSturm<BigRational>();
        Complex<BigRational> I = cfac.getIMAG();

        a = dfac.random(kl, ll, el, q);
        Squarefree<Complex<BigRational>> engine = SquarefreeFactory
                .<Complex<BigRational>> getImplementation(cfac);
        a = engine.squarefreePart(a);

        //a = dfac.univariate(0,2L).subtract(cfac.getONE());  // x^2 - 1
        //a = dfac.univariate(0,2L).sum(cfac.fromInteger(1));  // x^2 + 1
        //a = dfac.univariate(0,2L).subtract(I);  // x^2 - I
        //a = dfac.univariate(0,1L);  // x
        //System.out.println("a = " + a);

        Complex<BigRational> Mb = cr.rootBound(a);
        BigRational M = Mb.getRe();
        //System.out.println("M = " + M);

        Complex<BigRational>[] corner = (Complex<BigRational>[]) new Complex[4];

        corner[0] = new Complex<BigRational>(cfac, M.negate(), M); // nw
        corner[1] = new Complex<BigRational>(cfac, M.negate(), M.negate()); // sw
        corner[2] = new Complex<BigRational>(cfac, M, M.negate()); // se
        corner[3] = new Complex<BigRational>(cfac, M, M); // ne

        Rectangle<BigRational> rect = new Rectangle<BigRational>(corner);

        List<Rectangle<BigRational>> roots = cr.complexRoots(rect, a);
        //System.out.println("a = " + a);
        //System.out.println("roots = " + roots);
        assertTrue("#roots == deg(a) ", roots.size() == a.degree(0));
    }


    /**
     * Test complex roots.
     * 
     */
    public void testComplexRoots() {
        ComplexRootAbstract<BigRational> cr = new ComplexRootsSturm<BigRational>();
 
        a = dfac.random(kl, ll, el + 1, q);
        //System.out.println("a = " + a);

        List<Rectangle<BigRational>> roots = cr.complexRoots(a);
        //System.out.println("a = " + a);
        //System.out.println("roots = " + roots);
        assertTrue("#roots == deg(a) ", roots.size() == a.degree(0));
    }


    /**
     * Test complex root refinement.
     * 
     */
    public void testComplexRootRefinement() {
        ComplexRootAbstract<BigRational> cr = new ComplexRootsSturm<BigRational>();

        a = dfac.random(kl, ll, el + 1, q);
        Squarefree<Complex<BigRational>> engine = SquarefreeFactory
                .<Complex<BigRational>> getImplementation(cfac);
        a = engine.squarefreePart(a);
        //System.out.println("a = " + a);

        List<Rectangle<BigRational>> roots = cr.complexRoots(a);
        //System.out.println("a = " + a);
        //System.out.println("roots = " + roots);
        assertTrue("#roots == deg(a) ", roots.size() == a.degree(0));

        BigRational len = new BigRational(1, 1000000);
        //System.out.println("len = " + len);

        for (Rectangle<BigRational> root : roots) {
            Rectangle<BigRational> refine = cr.complexRootRefinement(root, a, len);
            //System.out.println("refine = " + refine);
        }
    }
}
