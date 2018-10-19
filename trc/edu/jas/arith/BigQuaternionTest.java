/*
 * $Id$
 */

package edu.jas.arith;


import java.util.SortedMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * BigQuaternion tests with JUnit.
 * @author Heinz Kredel
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
     * @return suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(BigQuaternionTest.class);
        return suite;
    }


    BigQuaternion a, b, c, d, e, f;


    BigQuaternionRing fac;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        fac = new BigQuaternionRing();
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

        a = fac.getZERO();
        b = fac.getONE();
        c = b.subtract(b);
        assertEquals("1-1 = 0", c, a);
    }


    /**
     * Test bitLength.
     */
    public void testBitLength() {
        a = fac.getZERO();
        b = fac.getONE();
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
        a = new BigQuaternion(fac, "6/8");
        b = new BigQuaternion(fac, "3/4");

        assertEquals("6/8 = 3/4", a, b);

        a = new BigQuaternion(fac, "3/4 i 4/5 j 1/5 k 2/5");
        b = new BigQuaternion(fac, "-3/4 i -4/5 j -1/5 k -2/5");
        assertEquals("3/4 + i 4/5 + j 1/5 + k 2/5", a, b.negate());

        String s = "6/1111111111111111111111111111111111111111111";
        a = new BigQuaternion(fac, s);
        String t = a.toString();

        assertEquals("stringConstr = toString", s, t);

        a = new BigQuaternion(fac, 1);
        b = new BigQuaternion(fac, -1);
        c = b.sum(a);

        assertTrue("1 = 1", a.isONE());
        assertEquals("1+(-1) = 0", c, fac.getZERO());
    }


    /**
     * Test random rationals.
     */
    public void testRandom() {
        a = fac.random(50);
        b = new BigQuaternion(fac, a.getRe(), a.getIm(), a.getJm(), a.getKm());
        c = b.subtract(a);

        assertEquals("a-b = 0", fac.getZERO(), c);

        d = new BigQuaternion(fac, b.getRe(), b.getIm(), b.getJm(), b.getKm());
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

        d = a.sum(fac.getZERO());
        assertEquals("a+0 = a", d, a);
        d = a.subtract(fac.getZERO());
        assertEquals("a-0 = a", d, a);
        d = a.subtract(a);
        assertEquals("a-a = 0", d, fac.getZERO());
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

        d = a.multiply(fac.getONE());
        assertEquals("a*1 = a", d, a);
        d = a.divide(fac.getONE());
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

        b = a.norm();
        d = a.multiply(a.conjugate());
        assertEquals("norm() = a a^", b, d);

        //b = a.norm();
        d = a.abs();
        e = d.multiply(d);
        BigRational dd = e.re.subtract(b.re).abs().divide(e.re.abs().sum(b.re.abs()));
        //System.out.println("dd = " + dd + ", d = " + d + ", e = " + e);
        BigRational eps = new BigRational(1,10).power(BigDecimal.DEFAULT_PRECISION-1);
        assertTrue("abs()*abs() == norm(): " + dd, dd.compareTo(eps) <= 0);
        
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
        assertFalse("a*b != b*a", c.equals(d));

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

}
