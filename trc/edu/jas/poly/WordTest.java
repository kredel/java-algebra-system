/*
 * $Id$
 */

package edu.jas.poly;


import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigInteger;


/**
 * Wordr tests with JUnit.
 * Tests arithmetic operations.
 * @author Heinz Kredel.
 */

public class WordTest extends TestCase {


    /**
     * main.
     */
    public static void main (String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run( suite() );
    }


    /**
     * Constructs a <CODE>WordTest</CODE> object.
     * @param name String.
     */
    public WordTest(String name) {
        super(name);
    }

    /**
     */ 
    public static Test suite() {
        TestSuite suite= new TestSuite(WordTest.class);
        return suite;
    }

    //private final static int bitlen = 100;

    Word a;
    Word b;
    Word c;
    Word d;

    protected void setUp() {
        a = b = c = d = null;
    }

    protected void tearDown() {
        a = b = c = d = null;
    }


    /**
     * Test constructor and toString.
     */
    public void testConstructor() {
        a = new Word();
        b = a; 
        System.out.println("a = " + a);
        assertEquals("() = ()",a,b);
        assertEquals("length( () ) = 0",a.length(),0);
        assertTrue("isONE( () )",a.isONE());
        assertTrue("isUnit( () )",a.isUnit());

        b = new Word( "abc");
        c = new Word( " abc ");
        System.out.println("b = " + b);
        System.out.println("c = " + c);
        assertEquals("b = c: ",b,c);

        assertFalse("isONE( () )",b.isONE());
        assertFalse("isUnit( () )",b.isUnit());
        assertFalse("isONE( () )",c.isONE());
        assertFalse("isUnit( () )",c.isUnit());

        String s = b.toString();
        String t = c.toString();
        System.out.println("s = " + s);
        System.out.println("t = " + t);
        assertEquals("s = t: ",s,t);
    }


    /**
     * Test random integer.
     * 
     */
    public void testRandom() {
        float q = (float) 0.2;

        //         a = ExpVector.EVRAND(5,10,q);
        //         String s = a.toString();
        //         if ( s.indexOf(":") >= 0 ) {
        //             s = s.substring(0,s.indexOf(":"));
        //         }
        //         b = ExpVector.create( s );

        //         assertEquals("a == b", true, a.equals(b) );

        //         c = ExpVector.EVDIF(b,a);

        //         assertTrue("a-b = 0",c.isZERO());
    }


    /**
     * Test multiplication.
     * 
     */
    public void testMultiplication() {
        a = new Word("abc");
        b = new Word("cddaa");
        System.out.println("a = " + a);
        System.out.println("b = " + b);

        c = a.multiply(b);
        System.out.println("c = " + c);

        assertTrue("divides: ", a.divides(c));
        assertTrue("divides: ", b.divides(c));
        assertTrue("multiple: ", c.multipleOf(a));
        assertTrue("multiple: ", c.multipleOf(b));

        d = c.divide(a);
        System.out.println("d = " + d);
        assertEquals("d = b",d,b);

        d = c.divide(b);
        System.out.println("d = " + d);
        assertEquals("d = a",d,a);

        d = c.divide(c);
        System.out.println("d = " + d);
        assertTrue("isONE( () )",d.isONE());

        d = new Word("xx");
        c = a.multiply(d).multiply(b);
        System.out.println("d = " + d);
        System.out.println("c = " + c);

        assertTrue("divides: ", d.divides(c));
        Word[] ret = c.divideWord(d);
        System.out.println("ret = " + ret[0] + ", " + ret[1]);

        assertEquals("prefix(c/d) = a",a,ret[0]);
        assertEquals("suffic(c/d) = b",b,ret[1]);
    }

}
