/*
 * $Id$
 */

package edu.jas.arith;


import java.util.SortedMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * BigQuaternionInteger tests with JUnit.
 * @author Heinz Kredel
 */

public class BigQuaternionIntegerTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>BigQuaternionIntegerTest</CODE> object.
     * @param name String.
     */
    public BigQuaternionIntegerTest(String name) {
        super(name);
    }


    /**
     * @return suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(BigQuaternionIntegerTest.class);
        return suite;
    }


    BigQuaternion a, b, c, d, e, f;


    BigQuaternionRing fac;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        fac = new BigQuaternionRing(true);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        fac = null;
    }


    /**
     * Test static initialization and constants.
     */
    public void testConstants() {
        a = fac.getZERO();
        b = fac.getONE();
        c = b.subtract(b);
        assertEquals("1-1 = 0", c, a);
        assertTrue("1-1 = 0", c.isZERO());
        assertTrue("1 = 1", b.isONE());

        assertTrue("isEntier(0)", a.isEntier());
        assertTrue("isEntier(1)", b.isEntier());
        assertTrue("isEntier(c)", c.isEntier());
    }


    /**
     * Test bitLength.
     */
    public void testBitLength() {
        a = fac.ZERO;
        b = fac.ONE;
        c = fac.random(100);
        //System.out.println("c = " + c);
        //System.out.println("len(c) = " + c.bitLength());

        assertEquals("len(0) = 12", 12, a.bitLength());
        assertEquals("len(1) = 13", 13, b.bitLength());
        assertEquals("len(-1) = 13", 13, b.negate().bitLength());
        assertTrue("len(random) >= 12", 12 <= c.bitLength());

        d = fac.I;
        assertEquals("len(i) = 13", 13, d.bitLength());
        assertEquals("len(-i) = 13", 13, d.negate().bitLength());

        d = fac.J;
        assertEquals("len(j) = 13", 13, d.bitLength());
        assertEquals("len(-j) = 13", 13, d.negate().bitLength());

        d = fac.K;
        assertEquals("len(k) = 13", 13, d.bitLength());
        assertEquals("len(-k) = 13", 13, d.negate().bitLength());
    }


    /**
     * Test constructor and toString.
     */
    public void testConstructor() {
        a = new BigQuaternionInteger(fac, "6/8");
        b = new BigQuaternionInteger(fac, "3/4");

        assertEquals("6/8 = 3/4", a, b);
        assertFalse("isEntier(a)", a.isEntier());
        assertFalse("isEntier(b)", b.isEntier());

        a = new BigQuaternionInteger(fac, "3/4 i 4/5 j 1/5 k 2/5");
        b = new BigQuaternionInteger(fac, "-3/4 i -4/5 j -1/5 k -2/5");
        assertEquals("3/4 + i 4/5 + j 1/5 + k 2/5", a, b.negate());
        assertFalse("isEntier(a)", a.isEntier());
        assertFalse("isEntier(b)", b.isEntier());

        String s = "6/1111111111111111111111111111111111111111111";
        a = new BigQuaternionInteger(fac, s);
        String t = a.toString();

        assertFalse("isEntier(a)", a.isEntier());
        assertEquals("stringConstr = toString", s, t);

        a = new BigQuaternionInteger(fac, 1);
        b = new BigQuaternionInteger(fac, -1);
        c = b.sum(a);
        assertTrue("isEntier(a)", a.isEntier());
        assertTrue("isEntier(b)", b.isEntier());
        assertTrue("isEntier(c)", c.isEntier());

        assertTrue("1 = 1", a.isONE());
        assertEquals("1+(-1) = 0", c, fac.ZERO);
    }


    /**
     * Test random rationals.
     */
    public void testRandom() {
        a = fac.random(50);
        b = new BigQuaternionInteger(fac, a.getRe(), a.getIm(), a.getJm(), a.getKm());
        c = b.subtract(a);
        assertTrue("isEntier(a)", a.isEntier());
        assertTrue("isEntier(b)", b.isEntier());
        assertTrue("isEntier(c)", c.isEntier());

        assertEquals("a-b = 0", fac.ZERO, c);

        d = new BigQuaternionInteger(fac, b);
        assertEquals("sign(a-a) = 0", 0, b.compareTo(d));
        assertTrue("isEntier(d)", d.isEntier());
    }


    /**
     * Test addition.
     */
    public void testAddition() {
        a = fac.random(10);
        b = a.sum(a);
        c = b.subtract(a);
        assertEquals("a+a-a = a", c, a);
        assertEquals("a+a-a = a", 0, c.compareTo(a));

        assertTrue("isEntier(a)", a.isEntier());
        assertTrue("isEntier(b)", b.isEntier());
        assertTrue("isEntier(c)", c.isEntier());

        d = a.sum(fac.ZERO);
        assertEquals("a+0 = a", d, a);

        d = a.subtract(fac.ZERO);
        assertEquals("a-0 = a", d, a);

        d = a.subtract(a);
        assertEquals("a-a = 0", d, fac.ZERO);
        assertTrue("isEntier(d)", d.isEntier());
    }


    /**
     * Test multiplication.
     */
    public void testMultiplication() {
        a = fac.random(10);
        b = a.multiply(a);
        c = b.leftDivide(a);
        BigQuaternionInteger bi = new BigQuaternionInteger(fac, b);
        d = bi.leftDivide(a);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertTrue("isEntier(a)", a.isEntier());
        assertTrue("isEntier(b)", b.isEntier());
        assertTrue("isEntier(c)", c.isEntier());
        assertTrue("isEntier(d)", d.isEntier());

        //if (! d.equals(a)) {
        //    e = a.multiply(d);
        //    System.out.println("e = " + e + ", e==b: " + e.equals(bi));
        //    e = d.multiply(a);
        //    System.out.println("e = " + e + ", e==b: " + e.equals(bi));
        //} 
        assertEquals("a*a/a = a", d, a); // || !d.isZERO());

        d = a.multiply(fac.ONE);
        assertEquals("a*1 = a", d, a);
        d = a.divide(fac.ONE);
        assertEquals("a/1 = a", d, a);

        //a = fac.random(10);
        //b = a.inverse(); // not entier
        //c = a.multiply(b);
        //assertTrue("a*1/a = 1", c.isONE());
        //c = b.multiply(a);
        //assertTrue("1/a*a = 1", c.isONE());
        //assertTrue("isEntier(a)", a.isEntier());
        //assertTrue("isEntier(b)", b.isEntier());
        //assertTrue("isEntier(c)", c.isEntier());

        //b = a.abs();
        //c = a.norm();
        //assertEquals("abs() == norm(): " + b, b, c); // wrong
        
        //c = b.inverse(); // not entier
        //d = b.multiply(c);
        //assertTrue("abs(a)*1/abs(a) = 1", d.isONE());
        //assertTrue("isEntier(a)", a.isEntier());

        b = a.norm();
        c = a.conjugate();
        d = a.multiply(c);
        assertTrue("isEntier(a)", a.isEntier());
        assertEquals("abs(a)^2 = a a^", b, d);
        assertTrue("isEntier(b)", b.isEntier());
        assertTrue("isEntier(c)", c.isEntier());
        assertTrue("isEntier(d)", d.isEntier());
    }


    /**
     * Test multiplication axioms.
     */
    public void testMultiplicationAxioms() {
        a = fac.random(10);
        b = fac.random(10);

        c = a.multiply(b);
        d = b.multiply(a);
        assertFalse("a*b != b*a", c.equals(d));

        assertTrue("isEntier(a)", a.isEntier());
        assertTrue("isEntier(b)", b.isEntier());
        assertTrue("isEntier(c)", c.isEntier());
        assertTrue("isEntier(d)", d.isEntier());

        c = fac.random(10);

        d = a.multiply(b.multiply(c));
        e = a.multiply(b).multiply(c);
        assertTrue("a(bc) = (ab)c", e.equals(d));
        assertTrue("isEntier(c)", c.isEntier());
        assertTrue("isEntier(d)", d.isEntier());
        assertTrue("isEntier(e)", e.isEntier());
    }


    /**
     * Test distributive law.
     */
    public void testDistributive() {
        a = fac.random(20);
        b = fac.random(20);
        c = fac.random(20);

        d = a.multiply(b.sum(c));
        e = a.multiply(b).sum(a.multiply(c));
        assertEquals("a(b+c) = ab+ac", d, e);

        assertTrue("isEntier(a)", a.isEntier());
        assertTrue("isEntier(b)", b.isEntier());
        assertTrue("isEntier(c)", c.isEntier());
        assertTrue("isEntier(d)", d.isEntier());
        assertTrue("isEntier(e)", e.isEntier());
    }


    /**
     * Test divide entier elements.
     */
    public void testDivideEntier() {
        a = new BigQuaternionInteger(fac, "3 i 4 j 5 k 2");
        assertTrue("a is entier", a.isEntier());
        //System.out.println("a = " + a);

        b = new BigQuaternionInteger(fac, "-3/2 i -5/2 j -1/2 k -7/2");
        assertTrue("b is entier", b.isEntier());
        //System.out.println("b = " + b);

        c = a.multiply(a);
        //System.out.println("c = " + c); 
        assertTrue("c is entier", c.isEntier());

        c = b.multiply(b);
        //System.out.println("c = " + c);
        assertTrue("c is entier", c.isEntier());

        c = a.multiply(b);
        //System.out.println("c = " + c);
        assertTrue("c is entier", c.isEntier());

        c = b.multiply(a);
        //System.out.println("c = " + c);
        assertTrue("c is entier", c.isEntier());

        d = a.norm();
        //System.out.println("norm(a) = " + d);
        assertTrue("d is entier", d.isEntier());

        d = b.norm();
        //System.out.println("norm(b) = " + d);
        assertTrue("d is entier", d.isEntier());

        BigQuaternionInteger ai, bi;
        ai = new BigQuaternionInteger(fac, a);
        bi = new BigQuaternionInteger(fac, b);
        // quotient and remainder
        //System.out.println("ai = " + ai.toScript());
        //System.out.println("bi = " + bi.toScript());
        BigQuaternion[] qr = ai.leftQuotientAndRemainder(bi);
        c = qr[0];
        d = qr[1];
        //System.out.println("q = " + c.toScript());
        //System.out.println("d = " + d.toScript());
        assertTrue("c is entier", c.isEntier());
        assertTrue("d is entier", d.isEntier());
        //System.out.println("norm(b) = " + b.norm());
        //System.out.println("norm(r) = " + d.norm());
        assertEquals("a == b * q + r: ", a, b.multiply(c).sum(d));
        assertTrue("norm(r) < norm(b): ", d.norm().re.compareTo(b.norm().re) < 0);

        qr = ai.rightQuotientAndRemainder(bi);
        c = qr[0];
        d = qr[1];
        //System.out.println("q = " + c.toScript());
        //System.out.println("d = " + d.toScript());
        assertTrue("c is entier", c.isEntier());
        assertTrue("d is entier", d.isEntier());
        //System.out.println("norm(b) = " + b.norm());
        //System.out.println("norm(r) = " + d.norm());
        assertEquals("a == q * b + r: ", a, c.multiply(b).sum(d));
        assertTrue("norm(r) < norm(b): ", d.norm().re.compareTo(b.norm().re) < 0);
    }


    /**
     * Test gcd entier elements.
     */
    public void testGcdEntier() {
        a = fac.random(10);
        b = fac.random(10);
        BigQuaternionInteger ai, bi;
        ai = new BigQuaternionInteger(fac, a);
        bi = new BigQuaternionInteger(fac, b);

        BigQuaternion g = ai.leftGcd(bi);
        //System.out.println("g = " + g.toScript());
        //System.out.println("norm(g) = " + g.norm());
        assertTrue("g is entier", g.isEntier());
        BigQuaternion r = ai.leftQuotientAndRemainder(g)[1];
        //System.out.println("r = " + r.toScript());
        assertTrue("r == 0: ", r.isZERO());
        r = bi.leftQuotientAndRemainder(g)[1];
        //System.out.println("r = " + r.toScript());
        assertTrue("r == 0: " + r, r.isZERO());

        BigQuaternion h = ai.rightGcd(bi);
        //System.out.println("h = " + h.toScript());
        //System.out.println("norm(h) = " + h.norm());
        assertTrue("h is entier", h.isEntier());
        r = ai.rightQuotientAndRemainder(h)[1];
        //System.out.println("r = " + r.toScript());
        assertTrue("r == 0: ", r.isZERO());
        r = bi.rightQuotientAndRemainder(h)[1];
        //System.out.println("r = " + r.toScript());
        assertTrue("r == 0: ", r.isZERO());

        // round to entier and factor norm
        a = fac.random(20).roundToLipschitzian();
        //a = fac.random(20).roundToHurwitzian();
        //System.out.println("a = " + a.toScript());
        b = a.norm();
        //System.out.println("b = " + b.toScript());
        java.math.BigInteger pp = b.re.floor();
        //System.out.println("pp = " + pp);
        long pl = pp.longValue();

        SortedMap<Long, Integer> P = PrimeInteger.factors(pl);
        //System.out.println("P = " + P);
        for (Long p : P.keySet()) {
            c = new BigQuaternion(fac, new BigRational(p));
            //System.out.println("c = " + c);
            d = a.leftGcd(c);
            //System.out.println("d = " + d.toScript());
            e = d.norm();
            //System.out.println("e = " + e);
            assertTrue("norm(gcd) == c: " + c + " : " + e,
                            c.equals(e) || c.equals(e.power(2)) || c.power(2).equals(e));
        }
    }

}
