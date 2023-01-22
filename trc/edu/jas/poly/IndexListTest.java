/*
 * $Id$
 */

package edu.jas.poly;


import java.util.Arrays;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * IndexList tests with JUnit. Tests construction and arithmetic operations.
 * @author Heinz Kredel
 */

public class IndexListTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>IndexListTest</CODE> object.
     * @param name String.
     */
    public IndexListTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(IndexListTest.class);
        return suite;
    }


    IndexList a, b, c, d, e;


    IndexFactory idf;


    @Override
    protected void setUp() {
        idf = new IndexFactory(11);
        a = b = c = d = null;
    }


    @Override
    protected void tearDown() {
        a = b = c = d = null;
    }


    float q = 0.5f;


    /**
     * Test constructor and toString.
     */
    public void testConstructor() {
        a = idf.random(0, q);
        b = a;
        //System.out.println("a = " + a);
        assertEquals("() = ()", a, b);
        assertEquals("length( () ) = 0", a.length(), 0);
        assertFalse("isZERO( () )", a.isZERO());
        assertTrue("isONE( () )", a.isONE());
        assertTrue("isUnit( () )", a.isUnit());

        b = idf.random(10, q);
        //System.out.println("b = " + b);
        assertTrue("length( () ) = 0", b.length() >= 0);
        assertFalse("isZERO( () )", b.isZERO());
        assertFalse("isONE( () )", b.isONE());
        assertFalse("isUnit( () )", b.isUnit());

        c = new IndexList(idf);
        //System.out.println("c = " + c);
        //assertNotEquals("() = ()", a, c);
        assertTrue("length( 0 ) = -1", c.length() < 0);
        assertTrue("isZERO( () )", c.isZERO());
        assertFalse("isONE( () )", c.isONE());
        assertFalse("isUnit( () )", c.isUnit());

        String s = b.toString();
        String t = b.toScript();
        //System.out.println("s = " + s);
        //System.out.println("t = " + t);
        assertEquals("s == t: ", s, t);
    }


    /**
     * Test random IndexList.
     */
    public void testRandom() {
        a = idf.random(5);
        b = idf.random(7);
        c = idf.random(9);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);

        assertFalse("a != (): ", a.isZERO());
        assertFalse("b != (): ", b.isZERO());
        assertFalse("c != (): ", c.isZERO());

        assertFalse("a != b: ", a.equals(b));
        assertFalse("a != c: ", a.equals(c));
        assertFalse("c != b: ", c.equals(b));

        d = c.abs();
        //System.out.println("d = " + d);
        assertTrue("sign(d) > 0: ", d.signum() > 0);

        if (d.degree() > 1) {
            assertTrue("minDeg < maxDeg: ", d.minDeg() < d.maxDeg());
        }
    }


    /**
     * Test multiplication.
     */
    public void testMultiplication() {
        a = idf.random(9, 0.2f);
        b = idf.random(7, 0.3f);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        c = a.multiply(b);
        d = b.multiply(a);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        //assertTrue("ab = -ba: " + c + " ==? " + d, c.isZERO() || d.isZERO() || a.isONE() || b.isONE() || c.equals(d.negate()));

        if (c.isZERO()) {
            return;
        }

        boolean div = a.divides(c);
        assertTrue("a | c: ", div);
        div = b.divides(c);
        assertTrue("b | c: ", div);
        //System.out.println("div = " + div);

        IndexList ca, cb;
        ca = a.innerRightProduct(c);
        cb = b.innerRightProduct(c);
        //System.out.println("ca = " + ca);
        //System.out.println("cb = " + cb);

        assertEquals("a == cb: ", a.abs(), cb.abs());
        assertEquals("b == ca: ", b.abs(), ca.abs());

        ca = c.innerLeftProduct(a);
        cb = c.innerLeftProduct(b);
        //System.out.println("ca_l = " + ca);
        //System.out.println("cb_l = " + cb);
    }


    /**
     * Test sequence IndexList.
     */
    public void testSequence() {
        a = idf.sequence(0, 4);
        b = idf.sequence(4, 3);
        c = idf.sequence(0, 7);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);

        assertFalse("a != (): ", a.isZERO());
        assertFalse("b != (): ", b.isZERO());
        assertFalse("c != (): ", c.isZERO());

        assertFalse("a != b: ", a.equals(b));
        assertFalse("a != c: ", a.equals(c));
        assertFalse("c != b: ", c.equals(b));

        d = a.multiply(b);
        e = b.multiply(a);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);
        assertEquals("c == ab: ", c, d);

        IndexList ca, cb;
        ca = a.innerRightProduct(d);
        cb = b.innerRightProduct(d);
        //System.out.println("ca = " + ca);
        //System.out.println("cb = " + cb);

        assertEquals("a == cb: ", a.abs(), cb.abs());
        assertEquals("b == ca: ", b.abs(), ca.abs());
    }


    /**
     * Test valueOf.
     */
    public void testValueOf() {
        ExpVector ef = ExpVector.random(4, 2L, 0.8f);
        //System.out.println("ef = " + ef);

        a = idf.valueOf(ef);
        //System.out.println("a = " + a);
        if (!a.isZERO()) {
            assertTrue("depend(ef) == deg(a): " + ef + ", " + a, ef.dependentVariables() == a.degree());
        }

        String as = a.toString() + " = " + a.toScript();
        //System.out.println("as = " + as);
        assertTrue("as != ''" + as, as.length() >= 0);

        List<Integer> W = Arrays.<Integer> asList(1, 4, 7, 8, 13, 17);
        //System.out.println("W = " + W);

        a = idf.valueOf(W);
        //System.out.println("a = " + a);
        assertTrue("deg(a) == #W " + W + ", " + a, W.size() == a.degree());

        int[] w = new int[] { 1, 4, 7, 8, 13, 17, 4, 1 };
        //System.out.println("w = " + w);

        a = idf.valueOf(w);
        //System.out.println("a = " + a);
        assertTrue("a == 0: " + a, a.isZERO());
        a = idf.valueOf((int[]) null);
        //System.out.println("a = " + a);
        assertTrue("a == 0: " + a, a.isZERO());

        w = new int[] { 7, 8, 13, 17, 4, 1 };
        //System.out.println("w = " + w);

        a = idf.valueOf(w);
        //System.out.println("a = " + a);
        assertTrue("deg(a) == #w: " + a, a.degree() == w.length);
        assertTrue("sign(a) < 0: " + a, a.signum() < 0);
        assertTrue("check(a) == true: " + a, a.isConformant());

        b = new IndexList(idf, w);
        //System.out.println("b = " + b);
        assertFalse("check(b) == false: " + b, b.isConformant());

        c = idf.valueOf(b);
        //System.out.println("c = " + c);
        assertTrue("check(b) == true: " + c, c.isConformant());
    }

}
