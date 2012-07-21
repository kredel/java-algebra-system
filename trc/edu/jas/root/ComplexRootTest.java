/*
 * $Id$
 */

package edu.jas.root;


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
        String[] vars = new String[] { "x" };
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
     * Test root bound.
     * 
     */
    public void testRootBound() {
        //a = dfac.random(kl, ll, el, q);
        a = dfac.univariate(0, 2L).sum(dfac.getONE()); // x^2 + 1
        //System.out.println("a = " + a);

        ComplexRootsAbstract<BigRational> cr = new ComplexRootsSturm<BigRational>(cfac);

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

        ComplexRootsSturm<BigRational> cr = new ComplexRootsSturm<BigRational>(cfac);

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
        ComplexRootsSturm<BigRational> cr = new ComplexRootsSturm<BigRational>(cfac);

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
        ComplexRootsSturm<BigRational> cr = new ComplexRootsSturm<BigRational>(cfac);
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

        Complex<BigRational>[] corner = new Complex[4];

        corner[0] = new Complex<BigRational>(cfac, M.negate(), M); // nw
        corner[1] = new Complex<BigRational>(cfac, M.negate(), M.negate()); // sw
        corner[2] = new Complex<BigRational>(cfac, M, M.negate()); // se
        corner[3] = new Complex<BigRational>(cfac, M, M); // ne

        long v = 0;
        try {
            v = cr.windingNumber(new Rectangle<BigRational>(corner), a);
        } catch (InvalidBoundaryException e) {
            fail("" + e);
        }
        //System.out.println("winding number = " + v);
        assertTrue("wind(rect,a) == 2 ", v == 2);

        //if ( true ) return;

        corner[0] = new Complex<BigRational>(cfac, M.negate(), M); // nw
        corner[1] = new Complex<BigRational>(cfac, M.negate(), eps); // sw
        corner[2] = new Complex<BigRational>(cfac, M, eps); // se
        corner[3] = new Complex<BigRational>(cfac, M, M); // ne

        try {
            v = cr.windingNumber(new Rectangle<BigRational>(corner), a);
        } catch (InvalidBoundaryException e) {
            fail("" + e);
        }
        //System.out.println("winding number = " + v);
        assertTrue("wind(rect,a) == 1 ", v == 1);

        corner[0] = new Complex<BigRational>(cfac, eps.negate(), eps); // nw
        corner[1] = new Complex<BigRational>(cfac, eps.negate(), eps.negate()); // sw
        corner[2] = new Complex<BigRational>(cfac, eps, eps.negate()); // se
        corner[3] = new Complex<BigRational>(cfac, eps, eps); // ne

        try {
            v = cr.windingNumber(new Rectangle<BigRational>(corner), a);
        } catch (InvalidBoundaryException e) {
            fail("" + e);
        }
        //System.out.println("winding number = " + v);
        assertTrue("wind(rect,a) == 0 ", v == 0);
    }


    /**
     * Test complex roots, sqrt(-1).
     * 
     */
    @SuppressWarnings("unchecked")
    public void testComplexRootsImag() {
        ComplexRootsAbstract<BigRational> cr = new ComplexRootsSturm<BigRational>(cfac);
        Complex<BigRational> I = cfac.getIMAG();

        a = dfac.univariate(0, 2L).sum(cfac.fromInteger(1)); // x^2 + 1
        //a = dfac.univariate(0,2L).subtract(cfac.getONE());  // x^2 - 1
        //a = dfac.univariate(0,2L).subtract(I);  // x^2 - I
        //a = dfac.univariate(0,1L);  // x
        //System.out.println("a = " + a);

        Complex<BigRational> Mb = cr.rootBound(a);
        BigRational M = Mb.getRe();
        //System.out.println("M = " + M);

        Complex<BigRational>[] corner = new Complex[4];

        corner[0] = new Complex<BigRational>(cfac, M.negate(), M); // nw
        corner[1] = new Complex<BigRational>(cfac, M.negate(), M.negate()); // sw
        corner[2] = new Complex<BigRational>(cfac, M, M.negate()); // se
        corner[3] = new Complex<BigRational>(cfac, M, M); // ne

        Rectangle<BigRational> rect = new Rectangle<BigRational>(corner);

        List<Rectangle<BigRational>> roots = null;
        try {
            roots = cr.complexRoots(rect, a);
        } catch (InvalidBoundaryException e) {
            fail("" + e);
        }
        //System.out.println("roots = " + roots);
        assertTrue("#roots == deg(a) ", roots.size() == a.degree(0));
    }


    /**
     * Test complex roots.
     */
    @SuppressWarnings("unchecked")
    public void testComplexRootsRand() {
        ComplexRootsAbstract<BigRational> cr = new ComplexRootsSturm<BigRational>(cfac);
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

        Complex<BigRational>[] corner = new Complex[4];

        corner[0] = new Complex<BigRational>(cfac, M.negate(), M); // nw
        corner[1] = new Complex<BigRational>(cfac, M.negate(), M.negate()); // sw
        corner[2] = new Complex<BigRational>(cfac, M, M.negate()); // se
        corner[3] = new Complex<BigRational>(cfac, M, M); // ne

        Rectangle<BigRational> rect = new Rectangle<BigRational>(corner);


        List<Rectangle<BigRational>> roots = null;
        try {
            roots = cr.complexRoots(rect, a);
        } catch (InvalidBoundaryException e) {
            fail("" + e);
        }
        //System.out.println("a = " + a);
        //System.out.println("roots = " + roots);
        assertTrue("#roots == deg(a) ", roots.size() == a.degree(0));
    }


    /**
     * Test complex roots.
     */
    public void testComplexRoots() {
        ComplexRootsAbstract<BigRational> cr = new ComplexRootsSturm<BigRational>(cfac);

        a = dfac.random(kl, ll, el + 1, q);
        //System.out.println("a = " + a);

        List<Rectangle<BigRational>> roots = cr.complexRoots(a);
        //System.out.println("a = " + a);
        //System.out.println("roots = " + roots);
        assertTrue("#roots == deg(a) ", roots.size() == a.degree(0));
    }


    /**
     * Test complex root refinement.
     */
    public void testComplexRootRefinement() {
        ComplexRootsAbstract<BigRational> cr = new ComplexRootsSturm<BigRational>(cfac);

        a = dfac.random(kl, ll, el - 1, q);
        //a = dfac.parse("( (x-1)^3 )");
        Squarefree<Complex<BigRational>> engine = SquarefreeFactory
                .<Complex<BigRational>> getImplementation(cfac);
        //System.out.println("a = " + a);
        a = engine.squarefreePart(a);
        //System.out.println("a = " + a);

        List<Rectangle<BigRational>> roots = cr.complexRoots(a);
        //System.out.println("a = " + a);
        //System.out.println("roots = " + roots);
        assertTrue("#roots == deg(a) ", roots.size() == a.degree(0));

        BigRational len = new BigRational(1, 1000);
        //System.out.println("len = " + len);

        for (Rectangle<BigRational> root : roots) {
            try {
                Rectangle<BigRational> refine = cr.complexRootRefinement(root, a, len);
                //System.out.println("refine = " + refine);
            } catch (InvalidBoundaryException e) {
                fail("" + e);
            }
        }
    }


    /**
     * Test complex root refinement full.
     */
    public void testComplexRootRefinementFull() {
        ComplexRootsAbstract<BigRational> cr = new ComplexRootsSturm<BigRational>(cfac);

        a = dfac.random(kl, ll, el - 1, q);
        //a = dfac.parse("( (x-1)^3 )");
        //a = dfac.parse("( x^4-2 )");
        //System.out.println("a = " + a);

        BigRational len = new BigRational(1, 1000);
        //System.out.println("len = " + len);

        List<Rectangle<BigRational>> refine = cr.complexRoots(a, len);
        //System.out.println("refine = " + refine);
        assertTrue("#roots == deg(a) ", refine.size() == a.degree(0));
    }


    /**
     * Test winding number with wrong precondition.
     */
    @SuppressWarnings("unchecked")
    public void testWindingNumberWrong() {
        ComplexRootsSturm<BigRational> cr = new ComplexRootsSturm<BigRational>(cfac);
        Complex<BigRational> I = cfac.getIMAG();

        a = dfac.univariate(0, 2L).sum(cfac.fromInteger(1)); // x^2 + 1
        //a = dfac.random(kl, ll, el, q);
        //a = dfac.univariate(0,2L).subtract(cfac.getONE());  // x^2 - 1
        //a = dfac.univariate(0,2L).subtract(I);  // x^2 - I
        //a = dfac.univariate(0,1L);  // x
        //System.out.println("a = " + a);

        Complex<BigRational> Mb = cfac.fromInteger(1); //.divide(cfac.fromInteger(2)); //cr.rootBound(a);
        BigRational M = Mb.getRe();
        //System.out.println("M = " + M);
        //BigRational eps = new BigRational(1, 1000);
        //System.out.println("eps = " + eps);
        BigRational zero = new BigRational();
        //BigRational one = new BigRational(1);

        Complex<BigRational>[] corner = new Complex[4];

        corner[0] = new Complex<BigRational>(cfac, M.negate(), M); // nw
        corner[1] = new Complex<BigRational>(cfac, M.negate(), zero); // sw
        corner[2] = new Complex<BigRational>(cfac, M, zero); // se
        corner[3] = new Complex<BigRational>(cfac, M, M); // ne

        Rectangle<BigRational> rect = new Rectangle<BigRational>(corner);
        //System.out.println("rect = " + rect);

        try {
            long v = cr.windingNumber(rect, a);
            System.out.println("winding number = " + v);
            fail("wind(rect,a) must throw an exception");
        } catch (InvalidBoundaryException e) {
        }
    }


    /**
     * Test complex root approximation.
     */
    public void testComplexRootApproximation() {
        ComplexRootsAbstract<BigRational> cr = new ComplexRootsSturm<BigRational>(cfac);

        //a = dfac.random(kl, ll, el-1, q);
        //a = dfac.parse("( (x-1)*(x-2)*(x-3)*(x - { 0i1 })*(x-5) )*( x^4-2 )");
        //a = dfac.parse("( (x-1)*(x-2)*(x-3)*( x^4-2 ) )");
        //a = dfac.parse("( (x-2)*( x^4-2 ) )");
        a = dfac.parse("( ( x^4-2 ) )");
        b = dfac.parse("( (x-1)*(x-2)*(x-3) )");
        c = dfac.parse("( x^4-2 )");
        d = dfac.parse("( (x - { 0i1 })*(x-5) )");
        //a = c; // b; //.multiply(c); //.multiply(d);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        //a = b.multiply(c).multiply(d);
        //System.out.println("a = " + a);
        Squarefree<Complex<BigRational>> engine = SquarefreeFactory
                .<Complex<BigRational>> getImplementation(cfac);
        a = engine.squarefreePart(a);
        //System.out.println("a = " + a);

        eps = eps.multiply(new BigRational(1000000));
        //System.out.println("eps = " + eps);
        BigDecimal eps1 = new BigDecimal(eps);
        BigDecimal eps2 = eps1.multiply(new BigDecimal("10"));
        //System.out.println("eps1 = " + eps1);
        //System.out.println("eps2 = " + eps2);

        List<Rectangle<BigRational>> roots = cr.complexRoots(a);
        //System.out.println("a = " + a);
        //System.out.println("roots = " + roots);
        assertTrue("#roots == deg(a) ", roots.size() == a.degree(0));

        for (Rectangle<BigRational> root : roots) {
            try {
                Complex<BigDecimal> cd = cr.approximateRoot(root, a, eps);
                //System.out.println("cd = " + cd);
            } catch (NoConvergenceException e) {
                //fail("" + e);
            }
        }
    }


    /**
     * Test complex root approximation full algorithm.
     */
    public void testComplexRootApproximationFull() {
        ComplexRootsAbstract<BigRational> cr = new ComplexRootsSturm<BigRational>(cfac);

        //a = dfac.random(kl, ll, el-1, q);
        //a = dfac.parse("( (x-1)*(x-2)*(x-3)*(x - { 0i1 })*(x-5) )*( x^4-2 )");
        //a = dfac.parse("( (x-1)*(x-2)*(x-3)*( x^4-2 ) )");
        a = dfac.parse("( (x-2)*( x^4-2 ) )");
        //a = dfac.parse("( ( x^4-2 ) )");
        b = dfac.parse("( (x-1)*(x-2)*(x-3) )");
        c = dfac.parse("( x^4-2 )");
        d = dfac.parse("( (x - { 0i1 })*(x-5) )");
        //a = c; // b; //.multiply(c); //.multiply(d);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        //a = b.multiply(c).multiply(d);
        //System.out.println("a = " + a);

        eps = eps.multiply(new BigRational(1000000));
        //System.out.println("eps = " + eps);
        BigDecimal eps1 = new BigDecimal(eps);
        BigDecimal eps2 = eps1.multiply(new BigDecimal("10"));
        //System.out.println("eps1 = " + eps1);
        //System.out.println("eps2 = " + eps2);

        List<Complex<BigDecimal>> roots = cr.approximateRoots(a, eps);
        //System.out.println("a = " + a);
        //System.out.println("roots = " + roots);
        //now always true: 
        assertTrue("#roots == deg(a) ", roots.size() == a.degree(0));
    }


    /**
     * Test complex root approximation full algorithm with Wilkinson
     * polynomials. p = (x-i0)*(x-i1)*(x-i2)*(x-i3*...*(x-in)
     */
    public void testComplexRootApproximationWilkinsonFull() {
        final int N = 4;
        d = dfac.getONE();
        e = dfac.univariate(0);

        BigDecimal br = new BigDecimal();
        ComplexRing<BigDecimal> cf = new ComplexRing<BigDecimal>(br);
        Complex<BigDecimal> I = cf.getIMAG();
        Complex<BigDecimal> cc = null;
        Complex<BigRational> Ir = cfac.getIMAG();

        List<Complex<BigDecimal>> Rn = new ArrayList<Complex<BigDecimal>>(N);
        a = d;
        for (int i = 0; i < N; i++) {
            cc = cf.fromInteger(i).multiply(I);
            Rn.add(cc);
            c = dfac.fromInteger(i).multiply(Ir);
            b = e.subtract(c);
            a = a.multiply(b);
        }
        //System.out.println("a = " + a);
        Collections.reverse(Rn);
        //System.out.println("Rn = " + Rn);

        ComplexRootsAbstract<BigRational> cr = new ComplexRootsSturm<BigRational>(cfac);

        eps = eps.multiply(new BigRational(100000));
        //System.out.println("eps = " + eps);
        BigDecimal eps1 = new BigDecimal(eps);
        BigDecimal eps2 = eps1.multiply(new BigDecimal("10"));
        //System.out.println("eps1 = " + eps1);
        //System.out.println("eps2 = " + eps2);

        List<Complex<BigDecimal>> roots = cr.approximateRoots(a, eps);
        //System.out.println("a = " + a);
        //System.out.println("roots = " + roots);
        //now always true: 
        assertTrue("#roots == deg(a) ", roots.size() == a.degree(0));

        int i = 0;
        for (Complex<BigDecimal> dd : roots) {
            Complex<BigDecimal> di = Rn.get(i++);
            //System.out.print("di = " + di + ", ");
            //System.out.println("dd = " + dd);
            assertTrue("|dd - di| < eps ", dd.subtract(di).norm().getRe().compareTo(eps2) <= 0);
        }
    }


    /**
     * Test complex root approximation full algorithm with Wilkinson
     * polynomials, inverse roots. p = (x-1/i1)*(x-1/i2)*(x-1/i3*...*(x-1/in)
     */
    public void testComplexRootApproximationWilkinsonInverseFull() {
        final int N = 5;
        d = dfac.getONE();
        e = dfac.univariate(0);

        BigDecimal br = new BigDecimal();
        ComplexRing<BigDecimal> cf = new ComplexRing<BigDecimal>(br);
        Complex<BigDecimal> I = cf.getIMAG();
        Complex<BigDecimal> cc = null;
        Complex<BigRational> Ir = cfac.getIMAG();

        List<Complex<BigDecimal>> Rn = new ArrayList<Complex<BigDecimal>>(N);
        a = d;
        for (int i = 1; i < N; i++) {
            cc = cf.fromInteger(i).multiply(I);
            cc = cc.inverse();
            Rn.add(cc);
            c = dfac.fromInteger(i).multiply(Ir);
            c = d.divide(c);
            b = e.subtract(c);
            a = a.multiply(b);
        }
        //System.out.println("a = " + a);
        //Collections.sort(Rn);
        //System.out.println("Rn = " + Rn);

        ComplexRootsAbstract<BigRational> cr = new ComplexRootsSturm<BigRational>(cfac);

        eps = eps.multiply(new BigRational(100000));
        //System.out.println("eps = " + eps);
        BigDecimal eps1 = new BigDecimal(eps);
        BigDecimal eps2 = eps1.multiply(new BigDecimal("10"));
        //System.out.println("eps1 = " + eps1);
        //System.out.println("eps2 = " + eps2);

        List<Complex<BigDecimal>> roots = cr.approximateRoots(a, eps);
        //System.out.println("a = " + a);
        //System.out.println("roots = " + roots);
        //now always true: 
        assertTrue("#roots == deg(a) ", roots.size() == a.degree(0));
        //Collections.sort(roots);
        //System.out.println("roots = " + roots);

        for (Complex<BigDecimal> dd : roots) {
            //System.out.println("dd = " + dd);
            boolean t = false;
            for (Complex<BigDecimal> di : Rn) {
                //System.out.println("di = " + di);
                t = dd.subtract(di).norm().getRe().compareTo(eps2) <= 0;
                if (t) {
                    break;
                }
            }
            if (!t) {
                //assertTrue("|dd - di| < eps ", dd.subtract(di).norm().getRe().compareTo(eps2) <= 0);
                fail("|dd - di| < eps ");
            }
        }
    }


    /**
     * Test complex root invariant rectangle.
     */
    public void testComplexRootInvariant() {
        ComplexRootsAbstract<BigRational> cr = new ComplexRootsSturm<BigRational>(cfac);

        a = dfac.random(kl, ll, el - 1, q);
        b = dfac.random(kl, ll, 2, q);
        //a = dfac.parse("( (x-1)^3 )");
        //a = dfac.parse("( x^4-2 )");
        if ( a.degree() == 0 ) {
            return;
        }
        Squarefree<Complex<BigRational>> engine = SquarefreeFactory
                .<Complex<BigRational>> getImplementation(cfac);
        a = engine.squarefreePart(a);
        b = engine.squarefreePart(b);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        List<Rectangle<BigRational>> roots = cr.complexRoots(a);
        //System.out.println("roots = " + roots);
        assertTrue("#roots == deg(a) ", roots.size() == a.degree(0));

        Rectangle<BigRational> rect = roots.get(0);
        //System.out.println("rect = " + rect);
        
        try {
            Rectangle<BigRational> ref = cr.invariantRectangle(rect,a,b);
            //System.out.println("ref = " + ref);
        } catch (InvalidBoundaryException e) {
            e.printStackTrace();
        }
    }


    /**
     * Test complex root invariant magnitude rectangle.
     */
    public void testComplexRootInvariantMagnitude() {
        ComplexRootsAbstract<BigRational> cr = new ComplexRootsSturm<BigRational>(cfac);

        a = dfac.random(kl, ll, el - 1, q);
        b = dfac.random(kl, ll, 3, q);
        //a = dfac.parse("( x^2 + 1 )");
        //b = dfac.parse("( x - 0i1 )");
        if ( a.degree() == 0 ) {
            return;
        }
        Squarefree<Complex<BigRational>> engine = SquarefreeFactory
                .<Complex<BigRational>> getImplementation(cfac);
        a = engine.squarefreePart(a);
        b = engine.squarefreePart(b);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        List<Rectangle<BigRational>> roots = cr.complexRoots(a);
        //System.out.println("roots = " + roots);
        assertTrue("#roots == deg(a) ", roots.size() == a.degree(0));

        Rectangle<BigRational> rect = roots.get(0);
        //System.out.println("rect = " + rect);
        
        try {
            Rectangle<BigRational> ref = cr.invariantMagnitudeRectangle(rect,a,b,eps);
            //System.out.println("ref = " + ref);
            Complex<BigRational> mag = cr.complexRectangleMagnitude(ref,a,b);
            //System.out.println("mag  = " + mag);
            Complex<BigRational> cmag = cr.complexMagnitude(ref,a,b,eps);
            //System.out.println("cmag = " + cmag);
            assertEquals("mag == cmag: " + cmag, mag, cmag);
            BigRational rmag = cmag.getRe();
            //System.out.println("rmag = " + new BigDecimal(cmag.getRe()) + " i " + new BigDecimal(cmag.getIm()));
        } catch (InvalidBoundaryException e) {
            e.printStackTrace();
        }
    }

}
