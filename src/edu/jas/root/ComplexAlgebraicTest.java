/*
 * $Id$
 */

package edu.jas.root;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigRational;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.NotInvertibleException;


/**
 * ComplexAlgebraicNumber Test using JUnit.
 * @author Heinz Kredel.
 */

public class ComplexAlgebraicTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>ComplexAlgebraicTest</CODE> object.
     * @param name String.
     */
    public ComplexAlgebraicTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(ComplexAlgebraicTest.class);
        return suite;
    }


    //private final static int bitlen = 100;

    ComplexAlgebraicRing<BigRational> fac;


    GenPolynomialRing<Complex<BigRational>> mfac;


    ComplexAlgebraicNumber<BigRational> a;


    ComplexAlgebraicNumber<BigRational> b;


    ComplexAlgebraicNumber<BigRational> c;


    ComplexAlgebraicNumber<BigRational> d;


    ComplexAlgebraicNumber<BigRational> e;


    ComplexAlgebraicNumber<BigRational> alpha;


    int rl = 1;


    int kl = 10;


    int ll = 10;


    int el = ll;


    float q = 0.5f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        ComplexRing<BigRational> cfac = new ComplexRing<BigRational>(new BigRational(1));
        Complex<BigRational> im = cfac.getIMAG();
        BigRational rfac = new BigRational();
        BigRational two = new BigRational(2);
        Complex<BigRational> nw = new Complex<BigRational>(cfac, rfac.getZERO(), two);
        Complex<BigRational> sw = new Complex<BigRational>(cfac, rfac.getZERO(), rfac.getZERO());
        Complex<BigRational> se = new Complex<BigRational>(cfac, two, rfac.getZERO());
        Complex<BigRational> ne = new Complex<BigRational>(cfac, two, two);
        Rectangle<BigRational> positiv = new Rectangle<BigRational>(nw, sw, se, ne);
        //System.out.println("postiv = " + positiv);
        String[] vars = new String[] { "alpha" };
        mfac = new GenPolynomialRing<Complex<BigRational>>(cfac, rl, vars);
        Complex<BigRational> r1 = cfac.fromInteger(1).sum(im);
        Complex<BigRational> r2 = r1.conjugate();
        GenPolynomial<Complex<BigRational>> mo = mfac.univariate(0, 1);
        mo = mo.subtract(r1).multiply(mo.subtract(r2)); // (x - (1+i))((x - (1-i))) 
        fac = new ComplexAlgebraicRing<BigRational>(mo, positiv);
        alpha = fac.getGenerator();
        //System.out.println("fac = " + fac);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
        alpha = null;
    }


    /**
     * Test constructor and toString.
     * 
     */
    public void testConstruction() {
        c = fac.getONE();
        //System.out.println("c = " + c);
        //System.out.println("c.getVal() = " + c.getVal());
        assertTrue("length( c ) = 1", c.number.getVal().length() == 1);
        assertTrue("isZERO( c )", !c.isZERO());
        assertTrue("isONE( c )", c.isONE());

        d = fac.getZERO();
        //System.out.println("d = " + d);
        //System.out.println("d.getVal() = " + d.getVal());
        assertTrue("length( d ) = 0", d.number.getVal().length() == 0);
        assertTrue("isZERO( d )", d.isZERO());
        assertTrue("isONE( d )", !d.isONE());
    }


    /**
     * Test random polynomial.
     * 
     */
    public void testRandom() {
        for (int i = 0; i < 7; i++) {
            a = fac.random(el);
            //System.out.println("a = " + a);
            if (a.isZERO() || a.isONE()) {
                continue;
            }
            // fac.random(rl+i, kl*(i+1), ll+2*i, el+i, q );
            assertTrue("length( a" + i + " ) <> 0", a.number.getVal().length() >= 0);
            assertTrue(" not isZERO( a" + i + " )", !a.isZERO());
            assertTrue(" not isONE( a" + i + " )", !a.isONE());
        }
    }


    /**
     * Test addition.
     * 
     */
    public void testAddition() {
        a = fac.random(ll);
        b = fac.random(ll);

        c = a.sum(b);
        d = c.subtract(b);
        assertEquals("a+b-b = a", a, d);

        c = a.sum(b);
        d = b.sum(a);
        assertEquals("a+b = b+a", c, d);

        c = fac.random(ll);
        d = c.sum(a.sum(b));
        e = c.sum(a).sum(b);
        assertEquals("c+(a+b) = (c+a)+b", d, e);


        c = a.sum(fac.getZERO());
        d = a.subtract(fac.getZERO());
        assertEquals("a+0 = a-0", c, d);

        c = fac.getZERO().sum(a);
        d = fac.getZERO().subtract(a.negate());
        assertEquals("0+a = 0+(-a)", c, d);
    }


    /**
     * Test real and imaginary.
     * 
     */
    public void testReIm() {
        System.out.println("fac = " + fac.toScript());
        a = fac.random(ll);
 
        RealAlgebraicNumberPart<BigRational> b = a.getRe();
        RealAlgebraicNumberPart<BigRational> c = a.getIm();
        System.out.println("a = " + a);
        System.out.println("a = " + a.magnitude());
        System.out.println("a = " + a.decimalMagnitude());
        System.out.println("b = " + b);
        System.out.println("b = " + b.magnitude());
        System.out.println("b = " + b.decimalMagnitude());
        System.out.println("c = " + c);
        System.out.println("c = " + c.magnitude());
        System.out.println("c = " + c.decimalMagnitude());

        e = fac.getIMAG();
        System.out.println("e = " + e);
        System.out.println("e = " + e.magnitude());
        System.out.println("e = " + e.decimalMagnitude());

        RealAlgebraicNumberPart<BigRational> d = e.getRe();
        System.out.println("d = " + d);
        System.out.println("d = " + d.magnitude());
        System.out.println("d = " + d.decimalMagnitude());

        RealAlgebraicNumberPart<BigRational> f = e.getIm();
        System.out.println("f = " + f);
        System.out.println("f = " + f.magnitude());
        System.out.println("f = " + f.decimalMagnitude());

        d = d.sum(f);
        System.out.println("d = " + d);

//         d = b.sum(c.multiply(e));
//         System.out.println("d = " + d);
//         System.out.println("d = " + d.magnitude());
//         System.out.println("d = " + d.decimalMagnitude());
//         assertEquals("re(a)+i*im(a) = a", a, d);

//         b = a.conjugate();
//         c = b.conjugate();
//         System.out.println("b = " + b);
//         System.out.println("b = " + b.magnitude());
//         System.out.println("b = " + b.decimalMagnitude());
//         System.out.println("c = " + c);
//         System.out.println("c = " + c.magnitude());
//         System.out.println("c = " + c.decimalMagnitude());
//         assertEquals("con(con(a)) = a", a, c);

//         c = b.multiply(a);
//         d = a.norm();
//         System.out.println("c = " + c);
//         System.out.println("c = " + c.magnitude());
//         System.out.println("c = " + c.decimalMagnitude());
//         System.out.println("d = " + d);
//         System.out.println("d = " + d.magnitude());
//         System.out.println("d = " + d.decimalMagnitude());
//         assertEquals("a*con(a) = norm(a)", c, d);
    }


    /**
     * Test object multiplication.
     * 
     */
    public void testMultiplication() {
        a = fac.random(ll);
        assertTrue("not isZERO( a )", !a.isZERO());

        b = fac.random(ll);
        assertTrue("not isZERO( b )", !b.isZERO());

        c = b.multiply(a);
        d = a.multiply(b);
        assertTrue("not isZERO( c )", !c.isZERO());
        assertTrue("not isZERO( d )", !d.isZERO());

        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        e = d.subtract(c);
        assertTrue("isZERO( a*b-b*a ) " + e, e.isZERO());

        assertTrue("a*b = b*a", c.equals(d));
        assertEquals("a*b = b*a", c, d);

        c = fac.random(ll);
        //System.out.println("c = " + c);
        d = a.multiply(b.multiply(c));
        e = (a.multiply(b)).multiply(c);

        //System.out.println("d = " + d);
        //System.out.println("e = " + e);

        //System.out.println("d-e = " + d.subtract(c) );

        assertEquals("a(bc) = (ab)c", d, e);
        assertTrue("a(bc) = (ab)c", d.equals(e));

        c = a.multiply(fac.getONE());
        d = fac.getONE().multiply(a);
        assertEquals("a*1 = 1*a", c, d);


        c = a.inverse();
        d = c.multiply(a);
        //System.out.println("a = " + a);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a*1/a = 1", fac.getONE(), d);

        try {
            a = fac.getZERO().inverse();
        } catch (NotInvertibleException expected) {
            return;
        }
        fail("0 invertible");
    }


    /**
     * Test distributive law.
     * 
     */
    public void testDistributive() {
        a = fac.random(ll);
        b = fac.random(ll);
        c = fac.random(ll);

        d = a.multiply(b.sum(c));
        e = a.multiply(b).sum(a.multiply(c));

        assertEquals("a(b+c) = ab+ac", d, e);
    }


    /**
     * Test compareTo of complex algebraic numbers.
     * 
     */
    public void testCompare() {
        a = fac.random(ll).abs();
        b = a.sum(fac.getONE());
        c = b.sum(fac.getONE());

        int ab = a.compareTo(b);
        int bc = b.compareTo(c);
        int ac = a.compareTo(c);

        assertTrue("a < a+1 ", ab < 0);
        assertTrue("a+1 < a+2 ", bc < 0);
        assertTrue("a < a+2 ", ac < 0);

        a = a.negate();
        b = a.sum(fac.getONE());
        c = b.sum(fac.getONE());

        ab = a.compareTo(b);
        bc = b.compareTo(c);
        ac = a.compareTo(c);

        assertTrue("a < a+1 ", ab < 0);
        assertTrue("a+1 < a+2 ", bc < 0);
        assertTrue("a < a+2 ", ac < 0);
    }

}
