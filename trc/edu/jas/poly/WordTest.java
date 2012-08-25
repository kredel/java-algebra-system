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
 * Word and WordFactory tests with JUnit.
 * Tests construction and arithmetic operations.
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
        WordFactory wf = new WordFactory("abcdefg");
        a = new Word(wf);
        b = a; 
        //System.out.println("a = " + a);
        assertEquals("() = ()",a,b);
        assertEquals("length( () ) = 0",a.length(),0);
        assertTrue("isONE( () )",a.isONE());
        assertTrue("isUnit( () )",a.isUnit());

        b = new Word(wf, "abc");
        c = wf.parse(" a b c ");
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        assertEquals("b = c: ",b,c);

        assertFalse("isONE( () )",b.isONE());
        assertFalse("isUnit( () )",b.isUnit());
        assertFalse("isONE( () )",c.isONE());
        assertFalse("isUnit( () )",c.isUnit());

        String s = b.toString();
        String t = c.toString();
        //System.out.println("s = " + s);
        //System.out.println("t = " + t);
        assertEquals("s = t: ",s,t);
    }


    /**
     * Test word factory.
     */
    public void testFactory() {
        WordFactory wf = new WordFactory("abcdefg");
        //System.out.println("wf = " + wf);
        Word w = wf.getONE();
        //System.out.println("w = " + w);
        assertTrue("w == (): ", w.isONE());

        w = wf.parse("aaabbbcccaaa");
        //System.out.println("w = " + w);
        assertFalse("w != (): ", w.isONE());

        a = wf.parse(w.toString());
        //System.out.println("a = " + a);
        assertEquals("w = a",a,w);

        WordFactory wf2 = new WordFactory(w.toString());
        //System.out.println("wf2 = " + wf2);

        a = wf2.parse(w.toString());
        //System.out.println("a = " + a);
        assertEquals("w = a",a,w);

        List<Word> gens = wf.generators();
        //System.out.println("gens = " + gens);
        assertTrue("#gens == 8: ", gens.size() == 8);
        for ( Word v : gens ) {
	    a = wf.parse(v.toString());
            assertEquals("a == v", a,v);
        }
    }


    /**
     * Test random word.
     */
    public void testRandom() {
        WordFactory wf = new WordFactory("uvwxyz");
        //System.out.println("wf = " + wf);

        a = wf.random(3);
        b = wf.random(4);
        c = wf.random(5);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);

        assertFalse("a != (): ", a.isONE());
        assertFalse("b != (): ", b.isONE());
        assertFalse("c != (): ", c.isONE());
        assertTrue("#a == 3", a.length() == 3);
        assertTrue("#b == 4", b.length() == 4);
        assertTrue("#c == 5", c.length() == 5);
    }


    /**
     * Test multiplication.
     */
    public void testMultiplication() {
        WordFactory wf = new WordFactory("abcdefgx");
        a = new Word(wf,"abc");
        b = new Word(wf,"cddaa");
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        c = a.multiply(b);
        //System.out.println("c = " + c);

        assertTrue("divides: ", a.divides(c));
        assertTrue("divides: ", b.divides(c));
        assertTrue("multiple: ", c.multipleOf(a));
        assertTrue("multiple: ", c.multipleOf(b));

        d = c.divide(a);
        //System.out.println("d = " + d);
        assertEquals("d = b",d,b);

        d = c.divide(b);
        //System.out.println("d = " + d);
        assertEquals("d = a",d,a);

        d = c.divide(c);
        //System.out.println("d = " + d);
        assertTrue("isONE( () )",d.isONE());

        d = new Word(wf,"xx");
        c = a.multiply(d).multiply(b);
        //System.out.println("d = " + d);
        //System.out.println("c = " + c);

        assertTrue("divides: ", d.divides(c));
        Word[] ret = c.divideWord(d);
        //System.out.println("ret = " + ret[0] + ", " + ret[1]);

        assertEquals("prefix(c/d) = a",a,ret[0]);
        assertEquals("suffix(c/d) = b",b,ret[1]);

        Word e = ret[0].multiply(d).multiply(ret[1]);
        assertEquals("prefix(c/d) d suffix(c/d) = e",e,c);
    }

}
