
/*
 * $Id$
 */

package edu.jas.poly;


import java.util.List;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Local tests with JUnit.
 * @author Heinz Kredel
 */

public class LocalTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>LocalTest</CODE> object.
     * @param name String.
     */
    public LocalTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(LocalTest.class);
        return suite;
    }

    LocalRing<BigInteger> fac;


    GenPolynomialRing<BigRational> pfac;


    LocalRing<GenPolynomial<BigRational>> mfac;


    Local<BigInteger> a, b, c, d, e;


    Local<GenPolynomial<BigRational>> ap, bp, cp, dp, ep;


    int rl = 1;


    int kl = 13;


    int ll = 5;


    int el = 2;


    float q = 0.3f;


    int il = 2;


    long p = 1152921504606846883L; // 2^60-93; 

    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        ap = bp = cp = dp = ep = null;
        BigInteger cfac = new BigInteger(1);
        fac = new LocalRing<BigInteger>(cfac, new BigInteger(p));

        pfac = new GenPolynomialRing<BigRational>(new BigRational(1), 1);
        GenPolynomial<BigRational> mo = pfac.random(kl, ll, el, q);
        while (mo.isConstant()) {
            mo = pfac.random(kl, ll, el, q);
        }
        mfac = new LocalRing<GenPolynomial<BigRational>>(pfac, mo);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        ap = bp = cp = dp = ep = null;
        fac = null;
        pfac = null;
        mfac = null;
    }


    /**
     * Test factory for integer.
     */
    public void testIntRing() {
        assertFalse("#ring infinite", fac.isFinite());
        assertTrue("associative ring", fac.isAssociative());
        assertTrue("commutative ring", fac.isCommutative());
        assertTrue("characteristic zero", fac.characteristic().signum() == 0);
        assertFalse("no field", fac.isField());
    }


    /**
     * Test factory for polynomial.
     */
    public void testPolyRing() {
        assertFalse("#ring infinite", mfac.isFinite());
        assertTrue("associative ring", mfac.isAssociative());
        assertTrue("commutative ring", mfac.isCommutative());
        assertTrue("characteristic zero", mfac.characteristic().signum() == 0);
        assertFalse("no field", mfac.isField());
    }


    /**
     * Test constructor for integer.
     */
    public void testIntConstruction() {
        c = fac.getONE();
        //System.out.println("c = " + c);
        assertTrue("isZERO( c )", !c.isZERO());
        assertTrue("isONE( c )", c.isONE());

        d = fac.getZERO();
        //System.out.println("d = " + d);
        assertTrue("isZERO( d )", d.isZERO());
        assertTrue("isONE( d )", !d.isONE());

        List<Local<BigInteger>> gens = fac.generators();
        //System.out.println("gens = " + gens);
        assertTrue("#gens == 1: ", gens.size() == 1);
        for (Local<BigInteger> v : gens) {
            a = fac.parse(v.toString());
            assertEquals("a == v", a, v);
        }
    }


    /**
     * Test constructor for polynomial.
     */
    public void testPolyConstruction() {
        cp = mfac.getONE();
        assertTrue("isZERO( cp )", !cp.isZERO());
        assertTrue("isONE( cp )", cp.isONE());

        dp = mfac.getZERO();
        assertTrue("isZERO( dp )", dp.isZERO());
        assertTrue("isONE( dp )", !dp.isONE());

        List<Local<GenPolynomial<BigRational>>> gens = mfac.generators();
        //System.out.println("gens = " + gens);
        assertTrue("#gens == 3: ", gens.size() == 3);
        for (Local<GenPolynomial<BigRational>> v : gens) {
            ap = mfac.parse(v.toString());
            assertEquals("ap == v", ap, v);
        }
    }


    /**
     * Test random integer.
     */
    public void testRandom() {
        for (int i = 0; i < 4; i++) {
            a = fac.random(kl * (i + 1));
            //a = fac.random(kl*(i+1), ll+i, el, q );
            //System.out.println("a = " + a);
            assertTrue(" not isZERO( a" + i + " )", !a.isZERO());
            assertTrue(" not isONE( a" + i + " )", !a.isONE());
        }
    }


    /**
     * Test random polynomial.
     */
    public void testPolyRandom() {
        for (int i = 0; i < 7; i++) {
            ap = mfac.random(kl + i);
            assertTrue(" not isZERO( ap" + i + " )", !ap.isZERO());
            assertTrue(" not isONE( ap" + i + " )", !ap.isONE());
        }
    }


    /**
     * Test integer addition.
     */
    public void testIntAddition() {
        a = fac.random(kl);
        b = fac.random(kl);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        c = a.sum(b);
        d = c.subtract(b);
        assertEquals("a+b-b = a", a, d);

        c = a.sum(b);
        d = b.sum(a);
        assertEquals("a+b = b+a", c, d);

        c = fac.random(kl);
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
     * Test polynomial addition.
     */
    public void testPolyAddition() {
        ap = mfac.random(kl);
        bp = mfac.random(kl);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        cp = ap.sum(bp);
        dp = cp.subtract(bp);
        assertEquals("a+b-b = a", ap, dp);

        cp = ap.sum(bp);
        dp = bp.sum(ap);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);

        assertEquals("a+b = b+a", cp, dp);

        cp = mfac.random(kl);
        dp = cp.sum(ap.sum(bp));
        ep = cp.sum(ap).sum(bp);
        assertEquals("c+(a+b) = (c+a)+b", dp, ep);


        cp = ap.sum(mfac.getZERO());
        dp = ap.subtract(mfac.getZERO());
        assertEquals("a+0 = a-0", cp, dp);

        cp = mfac.getZERO().sum(ap);
        dp = mfac.getZERO().subtract(ap.negate());
        assertEquals("0+a = 0+(-a)", cp, dp);
    }


    /**
     * Test integer multiplication.
     */
    public void testIntMultiplication() {
        a = fac.random(kl);
        b = fac.random(kl);
        if (a.isZERO() || b.isZERO()) {
            return;
        }
        assertTrue("not isZERO( a )", !a.isZERO());
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

        c = fac.random(kl);
        d = a.multiply(b.multiply(c));
        e = a.multiply(b).multiply(c);

        assertEquals("a(bc) = (ab)c", d, e);
        assertTrue("a(bc) = (ab)c", d.equals(e));

        c = a.multiply(fac.getONE());
        d = fac.getONE().multiply(a);
        assertEquals("a*1 = 1*a", c, d);

        if (a.isUnit()) {
            c = a.inverse();
            d = c.multiply(a);
            //System.out.println("a = " + a);
            //System.out.println("c = " + c);
            //System.out.println("d = " + d);
            assertTrue("a*1/a = 1", d.isONE());
        }

        Local<BigInteger>[] qr;
        c = b.multiply(a);
        qr = c.quotientRemainder(a);
        assertEquals("b*a / a == b", qr[0], b);
        assertTrue("b*a rem a == 0", qr[1].isZERO());
    }


    /**
     * Test polynomial multiplication.
     */
    public void testPolyMultiplication() {
        ap = mfac.random(kl);
        bp = mfac.random(kl);
        if (ap.isZERO() || bp.isZERO()) {
            return;
        }

        assertTrue("not isZERO( a )", !ap.isZERO());
        assertTrue("not isZERO( b )", !bp.isZERO());

        cp = bp.multiply(ap);
        dp = ap.multiply(bp);
        assertTrue("not isZERO( c )", !cp.isZERO());
        assertTrue("not isZERO( d )", !dp.isZERO());

        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        ep = dp.subtract(cp);
        assertTrue("isZERO( a*b-b*a ) " + ep, ep.isZERO());

        assertTrue("a*b = b*a", cp.equals(dp));
        assertEquals("a*b = b*a", cp, dp);

        cp = mfac.random(kl);
        //System.out.println("c = " + c);
        dp = ap.multiply(bp.multiply(cp));
        ep = (ap.multiply(bp)).multiply(cp);

        //System.out.println("d = " + d);
        //System.out.println("e = " + e);

        //System.out.println("d-e = " + d.subtract(c) );

        assertEquals("a(bc) = (ab)c", dp, ep);
        assertTrue("a(bc) = (ab)c", dp.equals(ep));

        cp = ap.multiply(mfac.getONE());
        dp = mfac.getONE().multiply(ap);
        assertEquals("a*1 = 1*a", cp, dp);

        if (ap.isUnit()) {
            cp = ap.inverse();
            dp = cp.multiply(ap);
            //System.out.println("a = " + a);
            //System.out.println("c = " + c);
            //System.out.println("d = " + d);
            assertTrue("a*1/a = 1", dp.isONE());
        }

        Local<GenPolynomial<BigRational>>[] qr;
        cp = bp.multiply(ap);
        qr = cp.quotientRemainder(ap);
        assertEquals("b*a / a == b", qr[0], bp);
        assertTrue("b*a rem a == 0", qr[1].isZERO());
    }

}
