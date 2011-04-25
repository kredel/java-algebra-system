/*
 * $Id$
 */

package edu.jas.application;


import java.util.List;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.kern.ComputerThreads;
import edu.jas.arith.BigRational;
import edu.jas.arith.BigDecimal;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.structure.NotInvertibleException;
import edu.jas.root.Interval;
import edu.jas.root.Rectangle;
//import edu.jas.root.RealAlgebraicNumber;
import edu.jas.root.RealRootTuple;


/**
 * RealAlgebraicNumber Test using JUnit.
 * @author Heinz Kredel.
 */

public class RealAlgebraicTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        //BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>RealAlgebraicTest</CODE> object.
     * @param name String.
     */
    public RealAlgebraicTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(RealAlgebraicTest.class);
        return suite;
    }


    //private final static int bitlen = 100;

    RealAlgebraicRing<BigRational> fac;


    GenPolynomialRing<BigRational> mfac;


    RealAlgebraicNumber<BigRational> a;


    RealAlgebraicNumber<BigRational> b;


    RealAlgebraicNumber<BigRational> c;


    RealAlgebraicNumber<BigRational> d;


    RealAlgebraicNumber<BigRational> e;


    int rl = 1;


    int kl = 10;


    int ll = 10;


    int el = ll;


    float q = 0.5f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        BigRational rfac = new BigRational();
        String[] vars = new String[] { "x", "y" };
        TermOrder tord = new TermOrder(TermOrder.INVLEX);
        mfac = new GenPolynomialRing<BigRational>(rfac, tord, vars);
        //System.out.println("mfac = " + mfac);
        // z^3 - 2 i: z    |--> x + i y
        GenPolynomial<BigRational> fr = mfac.parse("y**3 - 3 * x**2 * y  - 2");
        GenPolynomial<BigRational> fi = mfac.parse("-3 * x * y**2 + x**3");
        List<GenPolynomial<BigRational>> li = new ArrayList<GenPolynomial<BigRational>>(2);
        li.add(fr);
        li.add(fi);
        Ideal<BigRational> id = new Ideal<BigRational>(mfac,li);
        //System.out.println("id = " + id);

        List<IdealWithUniv<BigRational>> idul = id.zeroDimRootDecomposition();
        IdealWithUniv<BigRational> idu = idul.get(0);
        if ( idul.size() > 1 ) {
            //System.out.println("idul = " + idul);
            //idu = idul.get(1);
	}
        //System.out.println("idu = " + idu);
        GenPolynomial<BigRational> x = idu.ideal.list.list.remove(1);
        //System.out.println("x = " + x);
        x = x.multiply(x).subtract(mfac.fromInteger(3));
        //System.out.println("x = " + x);
        idu.ideal.list.list.add(x);
        //System.out.println("idu = " + idu);

        IdealWithRealAlgebraicRoots<BigRational,BigRational> idr = PolyUtilApp.<BigRational,BigRational> realAlgebraicRoots(idu.ideal).get(0);
        //System.out.println("idr = " + idr);
        //idr.doDecimalApproximation();
        //for ( List<BigDecimal> d : idr.decimalApproximation() ) {
        //    System.out.println("d = " + d);
	//}
        List<List<edu.jas.root.RealAlgebraicNumber<BigRational>>> ran = idr.ran;
        RealRootTuple<BigRational> root = new RealRootTuple<BigRational>(ran.get(0));
	if ( ran.size() > 1 ) {
            //System.out.println("ran = " + ran);
            root = new RealRootTuple<BigRational>(ran.get(1));
        }
        //System.out.println("root = " + root);
        fac = new RealAlgebraicRing<BigRational>(idu, root); //,not true);
        //System.out.println("fac = " + fac);
    }


    @Override
    protected void tearDown() {
        ComputerThreads.terminate();
        a = b = c = d = e = null;
        fac = null;
    }


    /**
     * Test constructor and toString.
     */
    public void testConstruction() {
        c = fac.getONE();
        //System.out.println("c = " + c);
        //System.out.println("c.getVal() = " + c.getVal());
        //assertTrue("length( c ) = 1", c.number.getVal().length() == 1);
        assertTrue("isZERO( c )", !c.isZERO());
        assertTrue("isONE( c )", c.isONE());

        d = fac.getZERO();
        //System.out.println("d = " + d);
        //System.out.println("d.getVal() = " + d.getVal());
        //assertTrue("length( d ) = 0", d.number.getVal().length() == 0);
        assertTrue("isZERO( d )", d.isZERO());
        assertTrue("isONE( d )", !d.isONE());
    }


    /**
     * Test random polynomial.
     */
    public void testRandom() {
        for (int i = 0; i < 7; i++) {
            a = fac.random(el);
            //System.out.println("a = " + a);
            if (a.isZERO() || a.isONE()) {
                continue;
            }
            // fac.random(rl+i, kl*(i+1), ll+2*i, el+i, q );
            //assertTrue("length( a" + i + " ) <> 0", a.number.getVal().length() >= 0);
            assertTrue(" not isZERO( a" + i + " )", !a.isZERO());
            assertTrue(" not isONE( a" + i + " )", !a.isONE());
        }
    }


    /**
     * Test real and imaginary.
     */
    public void testReIm() {
        //System.out.println("fac = " + fac.toScript());
        a = fac.random(ll);
        b = fac.random(ll);
        //a = fac.getZERO();
        //a = fac.getONE();
        //a = fac.parse("x");
        //a = fac.parse("y^3 + 2");
        //a = fac.parse("3 y^2");
        //b = fac.parse("y");
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        ComplexRing<RealAlgebraicNumber<BigRational>> crr;
        crr = new ComplexRing<RealAlgebraicNumber<BigRational>>(fac); 

        Complex<RealAlgebraicNumber<BigRational>> ac, bc, cc, dc, ec;
        cc = new Complex<RealAlgebraicNumber<BigRational>>(crr,a,b);
        //System.out.println("cc = " + cc);
        assertEquals("a == re(c)", a, cc.getRe());
        assertEquals("b == im(c)", b, cc.getIm());

        dc = cc.conjugate();
        ec = dc.conjugate();
        //System.out.println("dc = " + dc);
        //System.out.println("ec = " + ec);
        assertEquals("con(con(c)) = c", cc, ec);

        ac = cc.multiply(dc);
        ec = cc.norm();
        //System.out.println("ac = " + ac);
        //System.out.println("ec = " + ec);

        c = ac.getRe();
        e = ec.getRe();
        //System.out.println("c = " + c);
        //System.out.println("e = " + e);
        assertEquals("c*con(c) = norm(c)", c, e);

        c = ac.getIm();
        e = ec.getIm();
        //System.out.println("c = " + c);
        //System.out.println("e = " + e);
        assertEquals("c*con(c) = norm(c)", c, e);
    }


    /**
     * Test addition.
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
     * Test object multiplication.
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
            fail("0 invertible");
        } catch (NotInvertibleException expected) {
            //pass, return;
        }
    }


    /**
     * Test distributive law.
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
     * Test signum and magnitude.
     */
    public void testSignum() {
        a = fac.random(ll);
        //a = fac.getONE();
        System.out.println("a = " + a);

        int s = a.signum();
        System.out.println("sign(a) = " + s);
        assertTrue("isZERO( c )", (a.isZERO() && s == 0) || (!a.isZERO() && s != 0));

        BigDecimal r = a.decimalMagnitude();
        System.out.println("magnitude(a)              = " + r);

        b = a.multiply(a);
        BigDecimal rb = b.decimalMagnitude();
        System.out.println("magnitude(a*a)            = " + rb);
        BigDecimal rr = r.multiply(r);
        System.out.println("magnitude(a)*magnitude(a) = " + rr);
    }


    /**
     * Test compareTo of complex algebraic numbers.
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
