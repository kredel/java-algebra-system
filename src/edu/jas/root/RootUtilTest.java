/*
 * $Id$
 */

package edu.jas.root;


import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigRational;
import edu.jas.arith.Roots;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
import edu.jas.structure.Power;


/**
 * RootUtil tests with JUnit.
 * @author Heinz Kredel.
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


    //private final static int bitlen = 100;

    TermOrder to = new TermOrder(TermOrder.INVLEX);


    GenPolynomialRing<BigRational> dfac;


    BigRational ai;


    BigRational bi;


    BigRational ci;


    BigRational di;


    BigRational ei;


    BigRational eps;


    GenPolynomial<BigRational> a;


    GenPolynomial<BigRational> b;


    GenPolynomial<BigRational> c;


    GenPolynomial<BigRational> d;


    GenPolynomial<BigRational> e;


    int rl = 1;


    int kl = 5;


    int ll = 7;


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
     * 
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
     * Test real algebraic factory.
     * 
     */
    public void testRealAlgebraicFactory() {
        a = dfac.random(kl, ll * 2, el * 2, q);
        //a = a.multiply( dfac.univariate(0) );
        //System.out.println("a = " + a);

        List<RealAlgebraicNumber<BigRational>> lrn = RootFactory.<BigRational> realAlgebraicNumbers(a);
        //System.out.println("lrn = " + lrn);
        assertTrue("#roots >= 0 ", lrn.size() >= 0);

        if (true)
            return;

        for (RealAlgebraicNumber<BigRational> ra : lrn) {
            //System.out.println("ra = " + ra.toScript() + " in " + ra.toScriptFactory());
        }

        lrn = RootFactory.<BigRational> realAlgebraicNumbersField(a);
        //System.out.println("lrn = " + lrn);

        for (RealAlgebraicNumber<BigRational> ra : lrn) {
            //System.out.println("ra = " + ra.toScript() + " in " + ra.toScriptFactory());
        }
    }


    /**
     * Test complex algebraic factory.
     * 
     */
    public void testComplexAlgebraicFactory() {
        a = dfac.random(kl, ll, el, q);
        //a = a.multiply( dfac.univariate(0) );
        //System.out.println("a = " + a);

        ComplexRing<BigRational> cf = new ComplexRing<BigRational>(new BigRational());
        GenPolynomialRing<Complex<BigRational>> cfac = new GenPolynomialRing<Complex<BigRational>>(cf, dfac);

        GenPolynomial<Complex<BigRational>> ca = PolyUtil.<BigRational> toComplex(cfac, a);
        //System.out.println("ca = " + ca);

        List<ComplexAlgebraicNumber<BigRational>> lcn = RootFactory.<BigRational> complexAlgebraicNumbersComplex(ca);
        //System.out.println("lcn = " + lcn);
        assertTrue("#roots == deg(a) ", lcn.size() == a.degree(0));

        if (true)
            return;

        for (ComplexAlgebraicNumber<BigRational> car : lcn) {
            //System.out.println("car = " + car.toScript() + " in " + car.toScriptFactory());
            //System.out.println("car = " + car.ring.root);
            System.out.println("car = " + car.ring.root.centerApprox() + ", "
                    + (Roots.sqrt(new BigDecimal(car.ring.root.rationalLength()))) + ", " + car.ring.root);
        }
    }


    /**
     * Test complex rational factory.
     * 
     */
    public void testComplexRationalFactory() {
        a = dfac.random(kl, ll, el, q);
        //a = a.multiply( dfac.univariate(0) );
        //System.out.println("a = " + a);

        List<ComplexAlgebraicNumber<BigRational>> lcn = RootFactory.<BigRational> complexAlgebraicNumbers(a);
        //System.out.println("lcn = " + lcn);
        assertTrue("#roots == deg(a) " + lcn.size() + ", " + a.degree(0), lcn.size() == a.degree(0));

        if (true)
            return;

        for (ComplexAlgebraicNumber<BigRational> car : lcn) {
            //System.out.println("car = " + car.toScript() + " in " + car.toScriptFactory());
            //System.out.println("car = " + car.ring.root);
            System.out.println("car = " + car.ring.root.centerApprox() + ", "
                    + (Roots.sqrt(new BigDecimal(car.ring.root.rationalLength()))) + ", " + car.ring.root);
        }
    }
}
