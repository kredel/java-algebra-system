/*
 * $Id$
 */

package edu.jas.arith;


// import edu.jas.arith.BigRational;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * BigComplex tests with JUnit.
 * @author Heinz Kredel.
 */

public class BigDecimalComplexTest extends TestCase {


    /**
     * main
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>BigDecimalComplexTest</CODE> object.
     * @param name String.
     */
    public BigDecimalComplexTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(BigDecimalComplexTest.class);
        return suite;
    }


    BigDecimalComplex a, b, c, d, e;


    BigDecimalComplex fac;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        fac = new BigDecimalComplex();
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
    }


    /**
     * Test static initialization and constants.
     */
    public void testConstants() {
        a = BigDecimalComplex.ZERO;
        b = BigDecimalComplex.ONE;
        c = b.subtract(b);

        assertEquals("1-1 == 0", c, a);
        assertTrue("1-1 == 0", c.isZERO());
        assertTrue("1 == 1", b.isONE());

        c = a.subtract(a);

        assertEquals("0-0 == 0", c, a);
    }


    /**
     * Test constructor and toString.
     */
    public void testConstructor() {
        a = new BigDecimalComplex("6.8");
        b = new BigDecimalComplex("3.4");
        b = b.sum(b);

        assertEquals("6.8 == 3.4+3.4", a, b);

        a = new BigDecimalComplex("3.4 i 4.5");
        b = new BigDecimalComplex("-3.4 i -4.5");

        assertEquals("3.4 + i 4.5 ", a, b.negate());

        String s = "6.1111111111111111111111111111111111111111111";
        a = new BigDecimalComplex(s);
        String t = a.toString();

        assertEquals("stringConstr == toString", s, t);

        a = new BigDecimalComplex(1);
        b = new BigDecimalComplex(-1);
        c = b.sum(a);

        assertTrue("1 == 1", a.isONE());
        assertEquals("1+(-1) == 0", c, BigDecimalComplex.ZERO);
    }


    /**
     * Test random decimal.
     */
    public void testRandom() {
        a = fac.random(500);
        b = new BigDecimalComplex(a.getRe(), a.getIm());
        c = b.subtract(a);

        assertEquals("a-b == 0", c, BigDecimalComplex.ZERO);

        d = new BigDecimalComplex(b.getRe(), b.getIm());
        assertEquals("sign(a-a) == 0", 0, b.compareTo(d));
    }


    /**
     * Test addition.
     */
    public void testAddition() {
        a = fac.random(10);
        b = a.sum(a);
        c = b.subtract(a);

        assertEquals("a+a-a == a", c, a);
        assertEquals("a+a-a == a", 0, c.compareTo(a));

        d = a.sum(BigDecimalComplex.ZERO);
        assertEquals("a+0 == a", d, a);
        d = a.subtract(BigDecimalComplex.ZERO);
        assertEquals("a-0 == a", d, a);
        d = a.subtract(a);
        assertEquals("a-a == 0", d, BigDecimalComplex.ZERO);

    }


    /**
     * Test multiplication.
     */
    public void testMultiplication() {
        a = fac.random(5);
        b = a.multiply(a);
        c = b.divide(a);

        assertEquals("a*a/a == a" + c.subtract(a), c, a);
        assertEquals("a*a/a == a" + c.subtract(a), 0, c.compareTo(a));

        d = a.multiply(BigDecimalComplex.ONE);
        assertEquals("a*1 == a", d, a);
        d = a.divide(BigDecimalComplex.ONE);
        assertEquals("a/1 == a", d, a);

        a = fac.random(5);
        b = a.inverse();
        c = a.multiply(b);

        assertTrue("a*1/a == 1: " + c, c.isONE());
    }


    /**
     * Test distributive law.
     */
    public void testDistributive() {
        a = fac.random(50);
        b = fac.random(50);
        c = fac.random(50);

        d = a.multiply(b.sum(c));
        e = a.multiply(b).sum(a.multiply(c));

        assertEquals("a(b+c) == ab+ac", d, e);
    }


    /**
     * Test norm and abs.
     */
    public void testNorm() {
        a = fac.random(5);

        b = a.norm();
        c = a.abs();
        d = c.multiply(c);
        e = b.subtract(d);
        //System.out.println("e = " + e);

        assertEquals("||a|| == |a|*|a|: " + e, b, d);
    }

}
