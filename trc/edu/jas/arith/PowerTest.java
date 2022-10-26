/*
 * $Id$
 */

package edu.jas.arith;


import java.util.List;
import java.util.LinkedList;

import edu.jas.structure.MonoidElem;
import edu.jas.structure.Power;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Basic power and structure tests with JUnit.
 * @author Heinz Kredel
 */

public class PowerTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>PowerTest</CODE> object.
     * @param name String.
     */
    public PowerTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(PowerTest.class);
        return suite;
    }


    @Override
    protected void setUp() {
        //a = b = c = d = e = null;
    }


    @Override
    protected void tearDown() {
        //a = b = c = d = e = null;
    }


    /**
     * Test addition for Integer.
     */
    public void testAddition() {
        BigInteger a, b, c;
        List<BigInteger> L = new LinkedList<BigInteger>();
        int n = 471;
        a = BigInteger.ZERO;
        for (int i = 1; i < n; i++) {
             b = BigInteger.ZERO.random(50);
             L.add(b);
             a = a.sum(b);
        }
        c = Power.<BigInteger> sum(BigInteger.ONE, L);
        assertEquals("a = c", c, a);
    }


    /**
     * Test multiplication for Integer.
     */
    public void testMultiplication() {
        BigInteger a, b, c;
        List<BigInteger> L = new LinkedList<BigInteger>();
        int n = 471;
        a = BigInteger.ONE;
        for (int i = 1; i < n; i++) {
             b = BigInteger.ZERO.random(50);
             L.add(b);
             a = a.multiply(b);
        }
        c = Power.<BigInteger> multiply(BigInteger.ONE, L);
        assertEquals("a = c", c, a);
    }


    /**
     * Test logarithm for Integer.
     */
    public void testLog() {
        long x = Power.logarithm(2, 1024);
        //System.out.println("x = " + x);
        assertEquals("x = 10", x, 10);

        BigInteger a, b;
        a = new BigInteger("2");
        b = new BigInteger("1024");
        x = Power.<BigInteger>logarithm(a, b);
        //System.out.println("x = " + x);
        assertEquals("x = 10", x, 10L);
    }


    /**
     * Test power for Rational.
     */
    public void testRationalPower() {
        BigRational a, b, c, d;
        a = BigRational.ZERO.random(100);

        // power operations
        b = Power.<BigRational> positivePower(a, 1);
        assertEquals("a^1 = a", b, a);

        Power<BigRational> pow = new Power<BigRational>(BigRational.ONE);
        b = pow.power(a, 1);
        assertEquals("a^1 = a", b, a);

        b = pow.power(a, 2);
        c = a.multiply(a);
        assertEquals("a^2 = a*a", b, c);

        d = pow.power(a, -2);
        c = b.multiply(d);
        assertTrue("a^2 * a^-2 = 1", c.isONE());

        b = pow.power(a, 3);
        c = a.multiply(a).multiply(a);
        assertEquals("a^3 = a*a*a", b, c);

        d = pow.power(a, -3);
        c = b.multiply(d);
        assertTrue("a^3 * a^-3 = 1", c.isONE());

        //Java 8:
        d = a.power(-3);
        c = b.multiply(d);
        assertTrue("a^3 * a^-3 = 1", c.isONE());

        d = a.power(0);
        c = BigRational.ONE;
        assertEquals("a^0 == 1", c, d);

        d = a.power(3);
        c = a.multiply(a).multiply(a);
        assertEquals("a^3 == a*a*a", c, d);
    }


    /**
     * Test power for Integer.
     */
    public void testIntegerPower() {
        BigInteger a, b, c, d, e;
        a = BigInteger.ZERO.random(500);

        // power operations
        b = Power.<BigInteger> positivePower(a, 1);
        assertEquals("a^1 = a", b, a);

        Power<BigInteger> pow = new Power<BigInteger>(BigInteger.ONE);
        b = pow.power(a, 1);
        assertEquals("a^1 = a", b, a);

        b = pow.power(a, 2);
        c = a.multiply(a);
        assertEquals("a^2 = a*a", b, c);

        b = pow.power(a, 3);
        c = a.multiply(a).multiply(a);
        assertEquals("a^3 = a*a*a", b, c);

        // mod power operations
        a = new BigInteger(3);
        b = Power.<BigInteger> positivePower(a, 1);
        assertEquals("a^1 = a", b, a);
        java.math.BigInteger x = java.math.BigInteger.ONE;
        b = Power.<BigInteger> positivePower(a, x);
        assertEquals("a^1 = a", b, a);

        a = new BigInteger(11);
        e = new BigInteger(2);
        c = Power.<BigInteger> modPositivePower(a, 10, e);
        assertTrue("3^n mod 2 = 1", c.isONE());

        // little fermat
        a = BigInteger.ZERO.random(500);
        b = new BigInteger(32003);
        c = Power.<BigInteger> modPositivePower(a, 32003, b);
        d = a.remainder(b);
        assertEquals("a^p = a mod p", c, d);

        c = pow.modPower(a, 32003, b);
        assertEquals("a^p = a mod p", c, d);

        //Java 8:
        a = BigInteger.ZERO.random(100);
        d = a.power(1);
        c = a;
        assertEquals("a^1 == a", c, d);

        d = a.power(0);
        c = BigInteger.ONE;
        assertEquals("a^0 == 1", c, d);

        d = a.power(3);
        c = a.multiply(a).multiply(a);
        assertEquals("a^3 == a*a*a", c, d);
    }


    /**
     * Test left/right division for Integer.
     */
    public void testIntegerLeftRight() {
        BigInteger a, b, c, d, e, f, g;
        a = BigInteger.ZERO.random(500);
        b = BigInteger.ZERO.random(50);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        c = a.divide(b);
        d = a.leftDivide(b);
        e = a.rightDivide(b);
        assertEquals("a /_l b == a/b", c, d);
        assertEquals("a /_r b == a/b", c, e);
        f = c;

        c = a.remainder(b);
        d = a.leftRemainder(b);
        e = a.rightRemainder(b);
        assertEquals("a rem_l b == a rem b", c, d);
        assertEquals("a rem_r b == a rem b", c, e);
        e = c;

        // no test since overriden in BigInteger:
        BigInteger[] qr = a.quotientRemainder(b);
        assertEquals("qr[0] == a/b", qr[0], f);
        assertEquals("qr[1] == a rem b", qr[1], e);

        MonoidElem[] lr = a.twosidedDivide(b);
        assertEquals("lr[0] == a/b", lr[0], f);
        assertTrue("lr[1] == 1", lr[1].isONE());

        // wrong: todo
        MonoidElem r = a.twosidedRemainder(b);
        assertEquals("r == e", r, e);

        c = a.gcd(b);
        d = a.leftGcd(b);
        e = a.rightGcd(b);
        assertEquals("a ggt_l b == a ggt b", c, d);
        assertEquals("a ggt_r b == a ggt b", c, e);
    }

}
