/*
 * $Id$
 */

package edu.jas.arith;


import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.jas.kern.PrettyPrint;
import edu.jas.structure.NotInvertibleException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * ModInt tests with JUnit.
 * @author Heinz Kredel
 */

public class ModIntTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>ModIntTest</CODE> object.
     * @param name String
     */
    public ModIntTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(ModIntTest.class);
        return suite;
    }


    ModIntRing zm, z1, z2;


    ModInt a, b, c, d, e;


    @Override
    protected void setUp() {
        zm = z1 = z2 = null;
        a = b = c = d = e = null;
    }


    @Override
    protected void tearDown() {
        zm = z1 = z2 = null;
        a = b = c = d = e = null;
    }


    protected static java.math.BigInteger getPrime1() {
        int prime = 2; //2^15-135; //2^60-93; // 2^30-35; //19; knuth (2,390)
        for (int i = 1; i < 15; i++) {
            prime *= 2;
        }
        prime -= 135;
        //System.out.println("p1 = " + prime);
        return new java.math.BigInteger("" + prime);
    }


    protected static java.math.BigInteger getPrime2() {
        int prime = 2; //2^60-93; // 2^30-35; //19; knuth (2,390)
        for (int i = 1; i < 30; i++) {
            prime *= 2;
        }
        prime -= 35;
        //System.out.println("p2 = " + prime);
        return new java.math.BigInteger("" + prime);
    }


    protected static java.math.BigInteger getPrime3() {
        int prime = 37;
        //System.out.println("p2 = " + prime);
        return new java.math.BigInteger("" + prime);
    }


    /**
     * Test static initialization and constants.
     */
    public void testConstants() {
        zm = new ModIntRing(5);
        d = new ModInt(zm, 11);
        a = zm.getZERO();
        b = zm.getONE();
        c = b.subtract(b);

        assertEquals("1-1 = 0", c, a);
        assertTrue("1-1 = 0", c.isZERO());
        assertTrue("1 = 1", b.isONE());
    }


    /**
     * Test bitLength.
     */
    public void testBitLength() {
        zm = new ModIntRing(163);
        a = zm.getZERO();
        b = zm.getONE();
        c = zm.random(30);
        //System.out.println("c = " + c);
        //System.out.println("len(c) = " + c.bitLength());

        assertEquals("len(0) = 1", 1L, a.bitLength());
        assertEquals("len(1) = 2", 2, b.bitLength());
        assertEquals("len(-1) = len(mod)", BigInteger.bitLength(zm.modul), b.negate().bitLength());
        assertTrue("len(random) >= 1", 1 <= c.bitLength());
    }


    /**
     * Test constructor and toString.
     */
    public void testConstructor() {
        zm = new ModIntRing("5");
        a = new ModInt(zm, "64");
        b = new ModInt(zm, "34");

        assertEquals("64(5) = 34(5)", a, b);

        zm = new ModIntRing("7");
        a = new ModInt(zm, "-4");
        b = new ModInt(zm, "3");

        assertEquals("-4(7) = 3(7)", a, b);

        String s = "61111111111111111";
        zm = new ModIntRing("10");
        try {
            a = new ModInt(zm, s);
            fail("s to large");
        } catch (NumberFormatException e) {
            // pass
        }
        s = "611111111";
        a = new ModInt(zm, s);
        String t = a.toString();

        if (PrettyPrint.isTrue()) {
            String st = "1";
            assertEquals("stringConstr = toString", st, t);
        } else {
            String st = "1 mod(10)";
            assertEquals("stringConstr = toString", st, t);
        }

        zm = new ModIntRing(7);
        a = new ModInt(zm, 1);
        b = new ModInt(zm, -1);
        c = b.sum(a);

        assertTrue("1 = 1", a.isONE());
        assertTrue("1 = 1", b.isUnit());
        assertEquals("1+(-1) = 0", c, zm.getZERO());

        zm = new ModIntRing(5);
        a = new ModInt(zm, 3);
        b = new ModInt(zm, 0);
        c = zm.parse(" 13 ");
        assertEquals("3(5) = 3(5)", a, c);

        StringReader sr = new StringReader("  13\n w ");
        c = zm.parse(sr);
        assertEquals("3(5) = 3(5)", a, c);
        //System.out.println("c = " + c);
    }


    /**
     * Test random modular integers.
     */
    public void testRandom() {
        zm = new ModIntRing(19);
        a = zm.random(500);
        b = a.copy();
        c = b.subtract(a);

        assertEquals("a-b = 0", c, zm.getZERO());

        d = new ModInt(new ModIntRing(b.getModul()), b.getVal());
        assertEquals("sign(a-a) = 0", 0, b.compareTo(d));
    }


    /**
     * Test addition.
     * 
     */
    public void testAddition() {
        zm = new ModIntRing(19);

        a = zm.random(100);
        b = a.sum(a);
        c = b.subtract(a);

        assertEquals("a+a-a = a", c, a);
        assertEquals("a+a-a = a", 0, c.compareTo(a));

        d = a.sum(zm.getZERO());
        assertEquals("a+0 = a", d, a);
        d = a.subtract(zm.getZERO());
        assertEquals("a-0 = a", d, a);
        d = a.subtract(a);
        assertEquals("a-a = 0", d, zm.getZERO());

    }


    /**
     * Test multiplication.
     */
    @SuppressWarnings("unchecked")
    public void testMultiplication() {
        zm = new ModIntRing(5);
        d = new ModInt(zm, 11);

        a = zm.random(100);
        if (a.isZERO()) {
            a = d;
        }
        b = a.multiply(a);
        c = b.divide(a);

        assertEquals("a*a/a = a", c, a);
        assertEquals("a*a/a = a", 0, c.compareTo(a));

        d = a.multiply(zm.getONE());
        assertEquals("a*1 = a", d, a);
        d = a.divide(zm.getONE());
        assertEquals("a/1 = a", d, a);

        a = zm.random(100);
        if (a.isZERO()) {
            a = d;
        }
        b = a.inverse();
        c = a.multiply(b);

        assertTrue("a*1/a = 1", c.isONE());

        try {
            a = zm.getZERO().inverse();
            fail("0 invertible");
        } catch (NotInvertibleException expected) {
            //ok
        }

        zm = new ModIntRing(5 * 3);
        a = new ModInt(zm, 5);
        assertFalse("5 !unit mod 15", a.isUnit());

        try {
            b = a.inverse();
            fail("5 invertible");
        } catch (ModularNotInvertibleException expected) {
            //ok
            //expected.printStackTrace();
            assertTrue("f  = 15 ", expected.f.equals(new BigInteger(15)));
            assertTrue("f1 =  5 ", expected.f1.equals(new BigInteger(5)));
            assertTrue("f2 =  3 ", expected.f2.equals(new BigInteger(3)));
            assertTrue("f  =  f1*f2 ", expected.f.equals(expected.f1.multiply(expected.f2)));
        } catch (NotInvertibleException e) {
            //e.printStackTrace();
            fail("wrong exception " + e);
        }
    }


    /**
     * Test chinese remainder.
     */
    public void testChineseRemainder() {
        zm = new ModIntRing(19 * 13);
        a = zm.random(9);
        //System.out.println("a = " + a);
        z1 = new ModIntRing(19);
        b = new ModInt(z1, a.getVal());
        //System.out.println("b = " + b);
        z2 = new ModIntRing(13);
        c = new ModInt(z2, a.getVal());
        //System.out.println("c = " + c);
        d = new ModInt(z2, 19);
        d = d.inverse();
        //System.out.println("d = " + d);

        e = zm.chineseRemainder(b, d, c);
        //System.out.println("e = " + e);

        assertEquals("cra(a mod 19,a mod 13) = a", a, e);

        java.math.BigInteger p1 = getPrime2();
        try {
            z1 = new ModIntRing(p1);
            fail("p1 too large");
        } catch (IllegalArgumentException e) {
            //pass
        }
        p1 = getPrime3();
        java.math.BigInteger p2 = new java.math.BigInteger("19");
        java.math.BigInteger p1p2 = p1.multiply(p2);
        //System.out.println("p1p2 = " + p1p2);
        //System.out.println("prime p1 ? = " + p1.isProbablePrime(66));
        //System.out.println("prime p2 ? = " + p2.isProbablePrime(33));
        //System.out.println("prime p1p1 ? = " + p1p2.isProbablePrime(3));
        zm = new ModIntRing(p1p2);
        z1 = new ModIntRing(p1);
        z2 = new ModIntRing(p2);

        for (int i = 0; i < 5; i++) {
            a = zm.random((59 + 29) / 2); //60+30 );
            //System.out.println("a = " + a);
            b = new ModInt(z1, a.getVal());
            //System.out.println("b = " + b);
            c = new ModInt(z2, a.getVal());
            //System.out.println("c = " + c);
            ModInt di = new ModInt(z2, p1);
            d = di.inverse();
            //System.out.println("d = " + d);

            e = zm.chineseRemainder(b, d, c);
            //System.out.println("e = " + e);

            assertEquals("cra(a mod p1,a mod p2) = a ", a, e);
        }
    }


    /**
     * Test chinese remainder of lists.
     */
    public void testChineseRemainderLists() {
        zm = new ModIntRing(19 * 13);
        z1 = new ModIntRing(19);
        z2 = new ModIntRing(13);

        List<ModInt> L1 = new ArrayList<ModInt>();
        List<ModInt> L2 = new ArrayList<ModInt>();
        List<ModInt> L;

        for (int i = 0; i < 7; i++) {
            a = zm.random(9);
            //System.out.println("a = " + a);
            b = new ModInt(z1, a.getVal());
            //System.out.println("b = " + b);
            c = new ModInt(z2, a.getVal());
            //System.out.println("c = " + c);
            L1.add(b);
            L2.add(c);
        }
        //System.out.println("L1 = " + L1);
        //System.out.println("L2 = " + L2);

        L = ModIntRing.chineseRemainder(z1.getONE(), z2.getONE(), L1, L2);
        //System.out.println("L = " + L);
        assertEquals("19 * 13) = a.modul: ", zm, L.get(0).ring);

        for (ModInt d : L) {
            b = new ModInt(z1, d.getVal());
            //System.out.println("b = " + b);
            c = new ModInt(z2, d.getVal());
            //System.out.println("c = " + c);
            assertTrue("cra(a mod 19, a mod 13) = a: ", L1.contains(b));
            assertTrue("cra(a mod 19, a mod 13) = a: ", L2.contains(c));
        }
    }


    /**
     * Test timing ModInt to ModInteger.
     */
    public void testTiming() {
        zm = new ModIntRing(getPrime1());
        a = zm.random(9);
        //System.out.println("a = " + a);
        b = zm.random(9);
        //System.out.println("b = " + b);
        c = zm.getONE();
        //System.out.println("c = " + c);

        ModIntegerRing ZM = new ModIntegerRing(zm.modul);
        ModInteger A = new ModInteger(ZM, a.getVal());
        ModInteger B = new ModInteger(ZM, b.getVal());
        ModInteger C = ZM.getONE();

        int run = 1000; //000;
        long t = System.currentTimeMillis();
        for (int i = 0; i < run; i++) {
            if (c.isZERO()) {
                c = zm.getONE();
            }
            c = a.sum(b.divide(c));
        }
        t = System.currentTimeMillis() - t;
        //System.out.println("long time = " + t);

        ModInteger D = new ModInteger(ZM, c.getVal());
        t = System.currentTimeMillis();
        for (int i = 0; i < run; i++) {
            if (C.isZERO()) {
                C = ZM.getONE();
            }
            C = A.sum(B.divide(C));
        }
        t = System.currentTimeMillis() - t;
        //System.out.println("BigInteger time = " + t);

        assertEquals("C == D ", C, D);
    }


    /**
     * Test iterator.
     */
    public void testIterator() {
        int m = 5 * 2;
        zm = new ModIntRing(m);
        ModInt j = null;
        for (ModInt i : zm) {
            //System.out.println("i = " + i);
            j = i;
        }
        ModInt end = new ModInt(zm, m - 1);
        assertTrue("j == m-1 ", j.equals(end));
    }

}
