/*
 * $Id$
 */

package edu.jas.arith;

import java.util.List;
import java.util.ArrayList;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * BigQuaternion tests with JUnit.
 * @author Heinz Kredel.
 */

public class BigQuaternionTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>BigQuaternionTest</CODE> object.
     * @param name String.
     */
    public BigQuaternionTest(String name) {
        super(name);
    }


    /**
 */
    /**
     * @return suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(BigQuaternionTest.class);
        return suite;
    }


    BigQuaternion a, b, c, d, e, f;


    BigQuaternion fac;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        fac = new BigQuaternion();
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
        a = BigQuaternion.ZERO;
        b = BigQuaternion.ONE;
        c = b.subtract(b);
        assertEquals("1-1 = 0", c, a);
        assertTrue("1-1 = 0", c.isZERO());
        assertTrue("1 = 1", b.isONE());

        a = BigQuaternion.ZERO;
        b = BigQuaternion.ONE;
        c = b.subtract(b);
        assertEquals("1-1 = 0", c, a);
    }


    /**
     * Test bitLength.
     */
    public void testBitLength() {
        a = BigQuaternion.ZERO;
        b = BigQuaternion.ONE;
        c = b.random(100);
        //System.out.println("c = " + c);
        //System.out.println("len(c) = " + c.bitLength());

        assertEquals("len(0) = 12", 12, a.bitLength());
        assertEquals("len(1) = 13", 13, b.bitLength());
        assertEquals("len(-1) = 13", 13, b.negate().bitLength());
        assertTrue("len(random) >= 12", 12 <= c.bitLength());

        d = BigQuaternion.I;
        assertEquals("len(i) = 13", 13, d.bitLength());
        assertEquals("len(-i) = 13", 13, d.negate().bitLength());

        d = BigQuaternion.J;
        assertEquals("len(j) = 13", 13, d.bitLength());
        assertEquals("len(-j) = 13", 13, d.negate().bitLength());

        d = BigQuaternion.K;
        assertEquals("len(k) = 13", 13, d.bitLength());
        assertEquals("len(-k) = 13", 13, d.negate().bitLength());
    }


    /**
     * Test constructor and toString.
     */
    public void testConstructor() {
        a = new BigQuaternion("6/8");
        b = new BigQuaternion("3/4");

        assertEquals("6/8 = 3/4", a, b);

        a = new BigQuaternion("3/4 i 4/5 j 1/5 k 2/5");
        b = new BigQuaternion("-3/4 i -4/5 j -1/5 k -2/5");
        assertEquals("3/4 + i 4/5 + j 1/5 + k 2/5", a, b.negate());

        String s = "6/1111111111111111111111111111111111111111111";
        a = new BigQuaternion(s);
        String t = a.toString();

        assertEquals("stringConstr = toString", s, t);

        a = new BigQuaternion(1);
        b = new BigQuaternion(-1);
        c = b.sum(a);

        assertTrue("1 = 1", a.isONE());
        assertEquals("1+(-1) = 0", c, BigQuaternion.ZERO);
    }


    /**
     * Test random rationals.
     */
    public void testRandom() {
        a = fac.random(50);
        b = new BigQuaternion(a.getRe(), a.getIm(), a.getJm(), a.getKm());
        c = b.subtract(a);

        assertEquals("a-b = 0", BigQuaternion.ZERO, c);

        d = new BigQuaternion(b.getRe(), b.getIm(), b.getJm(), b.getKm());
        assertEquals("sign(a-a) = 0", 0, b.compareTo(d));
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

        d = a.sum(BigQuaternion.ZERO);
        assertEquals("a+0 = a", d, a);
        d = a.subtract(BigQuaternion.ZERO);
        assertEquals("a-0 = a", d, a);
        d = a.subtract(a);
        assertEquals("a-a = 0", d, BigQuaternion.ZERO);

    }


    /**
     * Test multiplication.
     */
    public void testMultiplication() {
        a = fac.random(10);
        b = a.multiply(a);
        c = b.divide(a);
        assertEquals("a*a/a = a", c, a);
        assertEquals("a*a/a = a", 0, c.compareTo(a));

        d = a.multiply(BigQuaternion.ONE);
        assertEquals("a*1 = a", d, a);
        d = a.divide(BigQuaternion.ONE);
        assertEquals("a/1 = a", d, a);

        a = fac.random(10);
        b = a.inverse();
        c = a.multiply(b);
        assertTrue("a*1/a = 1", c.isONE());
        c = b.multiply(a);
        assertTrue("1/a*a = 1", c.isONE());

        b = a.abs();
        c = b.inverse();
        d = b.multiply(c);
        assertTrue("abs(a)*1/abs(a) = 1", d.isONE());

        b = a.abs();
        c = a.conjugate();
        d = a.multiply(c);
        assertEquals("abs(a)^2 = a a^", b, d);

        b = fac.random(10);
        c = a.inverse();
        d = c.multiply(b);
        e = a.multiply(d);
        assertEquals("a*(1/a)*b = b", b, e);

        d = b.multiply(c);
        e = d.multiply(a);
        assertEquals("b*(1/a)*a = b", b, e);
    }


    /**
     * Test multiplication axioms.
     */
    public void testMultiplicationAxioms() {
        a = fac.random(10);
        b = fac.random(10);

        c = a.multiply(b);
        d = b.multiply(a);
        assertTrue("a*b != b*a", !c.equals(d));

        c = fac.random(10);

        d = a.multiply(b.multiply(c));
        e = a.multiply(b).multiply(c);
        assertTrue("a(bc) = (ab)c", e.equals(d));
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
    }


    /**
     * Test entier elements.
     */
    public void testEntier() {
        a = fac.getONE();
        b = fac.getZERO();
        assertTrue("1 is entier", a.isEntier());
        assertTrue("0 is entier", b.isEntier());

        a = new BigQuaternion("3 i 4 j 5 k 2");
        assertTrue("a is entier", a.isEntier());
        System.out.println("a = " + a);

        b = new BigQuaternion("-3/2 i -5/2 j -1/2 k -7/2");
        assertTrue("b is entier", b.isEntier());
        System.out.println("b = " + b);

        c = a.multiply(a);
        System.out.println("c = " + c); 
        assertTrue("c is entier", c.isEntier());

        c = b.multiply(b);
        System.out.println("c = " + c);
        assertTrue("c is entier", c.isEntier());

        c = a.multiply(b);
        System.out.println("c = " + c);
        assertTrue("c is entier", c.isEntier());

        c = b.multiply(a);
        System.out.println("c = " + c);
        assertTrue("c is entier", c.isEntier());

        d = a.norm();
        System.out.println("norm(a) = " + d);
        assertTrue("d is entier", d.isEntier());

        d = b.norm();
        System.out.println("norm(b) = " + d);
        assertTrue("d is entier", d.isEntier());

        // quotient and remainder
        System.out.println("a = " + a.toScript());
        System.out.println("b = " + b.toScript());
        BigQuaternion[] qr = a.leftQuotientAndRemainder(b);
        c = qr[0];
        d = qr[1];
        System.out.println("q = " + c.toScript());
        System.out.println("d = " + d.toScript());
        assertTrue("c is entier", c.isEntier());
        assertTrue("d is entier", d.isEntier());
        System.out.println("norm(b) = " + b.norm());
        System.out.println("norm(r) = " + d.norm());

        qr = a.rightQuotientAndRemainder(b);
        c = qr[0];
        d = qr[1];
        System.out.println("q = " + c.toScript());
        System.out.println("d = " + d.toScript());
        assertTrue("c is entier", c.isEntier());
        assertTrue("d is entier", d.isEntier());
        System.out.println("norm(b) = " + b.norm());
        System.out.println("norm(r) = " + d.norm());

        // gcds
        BigQuaternion g = a.leftGcd(b);
        System.out.println("g = " + g.toScript());
        System.out.println("norm(g) = " + g.norm());
        assertTrue("g is entier", g.isEntier());

        BigQuaternion h = a.rightGcd(b);
        System.out.println("h = " + h.toScript());
        System.out.println("norm(h) = " + h.norm());
        assertTrue("h is entier", h.isEntier());

        c = g.inverse();
        e = a.multiply(c);
        f = c.multiply(a);
        System.out.println("e = " + e.toScript());
        System.out.println("f = " + f.toScript());
        e = e.multiply(g);
        f = g.multiply(f);
        assertEquals("e == f", f, e);
        assertEquals("a == e", a, e);

        e = b.multiply(c);
        f = c.multiply(b);
        System.out.println("e = " + e.toScript());
        System.out.println("f = " + f.toScript());
        e = e.multiply(g);
        f = g.multiply(f);
        assertEquals("e == f", f, e);
        assertEquals("b == e", b, e);

        d = h.inverse();
        e = d.multiply(a);
        f = a.multiply(d);
        System.out.println("e = " + e.toScript());
        System.out.println("f = " + f.toScript());
        e = h.multiply(e);
        f = f.multiply(h);
        assertEquals("e == f", f, e);
        assertEquals("a == e", a, e);
        d = h.inverse();

        e = d.multiply(b);
        f = b.multiply(d);
        System.out.println("e = " + e.toScript());
        System.out.println("f = " + f.toScript());
        e = h.multiply(e);
        f = f.multiply(h);
        assertEquals("e == f", f, e);
        assertEquals("b == e", b, e);
    }

}
