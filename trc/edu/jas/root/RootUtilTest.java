/*
 * $Id$
 */

package edu.jas.root;


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

//import edu.jas.commons.math.Roots;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * RootUtil tests with JUnit.
 * @author Heinz Kredel
 */

public class RootUtilTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>RootUtilTest</CODE> object.
     * @param name String.
     */
    public RootUtilTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(RootUtilTest.class);
        return suite;
    }


    TermOrder to = new TermOrder(TermOrder.INVLEX);


    GenPolynomialRing<BigRational> dfac;


    BigRational ai, bi, ci, di, ei, eps;


    GenPolynomial<BigRational> a, b, c, d, e;


    int rl = 1;


    int kl = 3;


    int ll = 5;


    int el = 7;


    float q = 0.7f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        String[] vars = new String[] { "x" };
        dfac = new GenPolynomialRing<BigRational>(new BigRational(1), rl, to, vars);
        // eps = new BigRational(1L,1000000L*1000000L*1000000L);
        eps = Power.positivePower(new BigRational(1L, 10L), BigDecimal.DEFAULT_PRECISION);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        dfac = null;
        eps = null;
        ComputerThreads.terminate();
    }


    /**
     * Test sign variations.
     */
    public void testSignVar() {
        int[] li = new int[] { 1, 0, 0, -1, 2, 3, 0, 1, 0, 0, 0, -1 };

        List<BigRational> Li = new ArrayList<BigRational>();

        ai = new BigRational();

        for (int i = 0; i < li.length; i++) {
            bi = ai.fromInteger(li[i]);
            Li.add(bi);
        }
        //System.out.println("Li = " + Li);

        long v = RootUtil.<BigRational> signVar(Li);
        //System.out.println("v = " + v);

        assertEquals("varSign(Li)", v, 3);

        List<BigRational> Mi = new ArrayList<BigRational>();
        for (int i = 0; i < 7; i++) {
            bi = ai.random(kl);
            Mi.add(bi);
        }
        //System.out.println("Mi = " + Mi);

        v = RootUtil.<BigRational> signVar(Mi);
        //System.out.println("v = " + v);
        long vv = v;

        assertTrue("varSign(Mi)>=0", v >= 0);

        List<BigRational> Ni = new ArrayList<BigRational>(Mi);
        Ni.addAll(Li);
        //System.out.println("Ni = " + Ni);

        v = RootUtil.<BigRational> signVar(Ni);
        //System.out.println("v = " + v);

        assertTrue("varSign(Mi)>=3", v >= 3 + vv);

        Ni = new ArrayList<BigRational>(Ni);
        Ni.addAll(Mi);
        //System.out.println("Ni = " + Ni);

        v = RootUtil.<BigRational> signVar(Ni);
        //System.out.println("v = " + v);

        assertTrue("varSign(Mi)>=3", v >= 3 + vv);
    }


    /**
     * Test intervals.
     */
    public void testIntervals() {
        a = dfac.random(kl, ll * 2, el * 2, q);
        b = dfac.random(kl, ll * 2, el * 2, q);
        b = b.multiply(b);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        RealRootsAbstract<BigRational> rr = new RealRootsSturm<BigRational>();

        ai = rr.realRootBound(a);
        bi = rr.realRootBound(b);
        //System.out.println("ai = " + ai);
        //System.out.println("bi = " + bi);

        Interval<BigRational> v1 = new Interval<BigRational>(ai.negate(), ai);
        Interval<BigRational> v2 = new Interval<BigRational>(bi.negate(), bi.sum(BigRational.ONE));
        //System.out.println("v1 = " + v1);
        //System.out.println("v2 = " + v2);

        Interval<BigRational> v3 = v1.sum(v2);
        Interval<BigRational> v4 = v1.subtract(v2);
        Interval<BigRational> v5 = v1.multiply(v2);
        //System.out.println("v3 = " + v3);
        //System.out.println("v4 = " + v4);
        //System.out.println("v5 = " + v5);
        assertTrue("v1 in v3" , v3.contains(v1));
        assertTrue("v2 in v3" , v3.contains(v2));

        assertTrue("v1 in v4" , v4.contains(v1));
        assertTrue("v2 in v4" , v4.contains(v2));

        assertTrue("v3 in v5" , v5.contains(v3));
        assertTrue("v4 in v5" , v5.contains(v4));
    }


    /**
     * Test real algebraic factory.
     */
    public void testRealAlgebraicFactory() {
        a = dfac.random(kl, ll * 2, el * 2, q);
        //a = a.multiply( dfac.univariate(0) );
        //System.out.println("a = " + a);

        List<RealAlgebraicNumber<BigRational>> lrn = RootFactory.<BigRational> realAlgebraicNumbers(a);
        //System.out.println("lrn = " + lrn);
        //assertTrue("#roots >= 0 ", lrn.size() >= 0);
        assertTrue("#roots >= 0 ", lrn != null);
        for (RealAlgebraicNumber<BigRational> ra : lrn) {
            //System.out.println("ra = " + ra.toScript() + " in " + ra.toScriptFactory());
            assertTrue("f(r) == 0: " + ra, RootFactory.<BigRational> isRoot(a, ra));
        }

        lrn = RootFactory.<BigRational> realAlgebraicNumbersField(a);
        //System.out.println("lrn = " + lrn);
        assertTrue("#roots >= 0 ", lrn != null);
        for (RealAlgebraicNumber<BigRational> ra : lrn) {
            //System.out.println("ra = " + ra.toScript() + " in " + ra.toScriptFactory());
            assertTrue("f(r) == 0: " + ra, RootFactory.<BigRational> isRoot(a, ra));
        }
    }


    /**
     * Test complex algebraic factory.
     */
    public void testComplexAlgebraicFactory() {
        a = dfac.random(kl, ll, el, q);
        //a = a.multiply( dfac.univariate(0) );
        //System.out.println("a = " + a);
        ComplexRing<BigRational> cf = new ComplexRing<BigRational>(new BigRational());
        GenPolynomialRing<Complex<BigRational>> cfac = new GenPolynomialRing<Complex<BigRational>>(cf, dfac);

        GenPolynomial<Complex<BigRational>> ca = PolyUtil.<BigRational> toComplex(cfac, a);
        //System.out.println("ca = " + ca);
        List<ComplexAlgebraicNumber<BigRational>> lcn = RootFactory
            .<BigRational> complexAlgebraicNumbersComplex(ca);
        //System.out.println("lcn = " + lcn);
        assertTrue("#roots == deg(a): " + a, lcn.size() == a.degree(0));

        for (ComplexAlgebraicNumber<BigRational> car : lcn) {
            //System.out.println("car = " + car.toScript() + " in " + car.toScriptFactory());
            //System.out.println("car = " + car.ring.root);
            //System.out.println("car = " + car.ring.root.centerApprox() + ", "
            //        + (Roots.sqrt(new BigDecimal(car.ring.root.rationalLength()))) + ", " + car.ring.root);
            assertTrue("f(r) == 0: " + car, RootFactory.<BigRational> isRoot(a, car));
        }
    }


    /**
     * Test complex rational factory.
     */
    public void testComplexRationalFactory() {
        a = dfac.random(kl, ll, el, q);
        //a = a.multiply( dfac.univariate(0) );
        //a = dfac.parse(" 1/8 x^6 - 5/3 x^5 + 3/20 x^4 - 2 x^3 ");
        //System.out.println("a = " + a);

        List<ComplexAlgebraicNumber<BigRational>> lcn = RootFactory.<BigRational> complexAlgebraicNumbers(a);
        //System.out.println("lcn = " + lcn);
        assertTrue("#roots == deg(a): " + a, lcn.size() == a.degree(0));

        for (ComplexAlgebraicNumber<BigRational> car : lcn) {
            //System.out.println("car = " + car.toScript() + " in " + car.toScriptFactory());
            //System.out.println("car = " + car.ring.root);
            //System.out.println("car = " + car.ring.root.centerApprox() + ", "
            //        + (Roots.sqrt(new BigDecimal(car.ring.root.rationalLength()))) + ", " + car.ring.root);
            assertTrue("f(r) == 0: " + car, RootFactory.<BigRational> isRoot(a, car));
        }
    }


    /**
     * Test algebraic roots, i.e. real and complex algebraic roots.
     */
    public void testAlgebraicRoots() {
        a = dfac.random(kl, ll, el, q);
        //a = a.multiply( dfac.univariate(0) );
        //System.out.println("a = " + a);

        AlgebraicRoots<BigRational> lcn = RootFactory.<BigRational> algebraicRoots(a);
        //System.out.println("lcn = " + lcn.toScript());
        //System.out.println("lcn = " + lcn.toDecimalScript());

        long r = lcn.real.size() + lcn.complex.size();
        ////Roots<BigRational> linalg = new Roots<BigRational>();
        ////List<Complex<BigDecimal>> cd = linalg.complexRoots(a);
        ////System.out.println("cd = " + cd);

        // some real roots not detected under complex roots // todo
        assertTrue("#roots == degree(f): " + r, r >= a.degree());
    }

}
