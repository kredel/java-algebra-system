/*
 * $Id$
 */

package edu.jas.poly;


import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;


/**
 * Word and WordFactory tests with JUnit. Tests construction and arithmetic
 * operations.
 * @author Heinz Kredel.
 */

public class WordTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
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
        TestSuite suite = new TestSuite(WordTest.class);
        return suite;
    }


    //private final static int bitlen = 100;

    Word a;


    Word b;


    Word c;


    Word d;


    @Override
    protected void setUp() {
        a = b = c = d = null;
    }


    @Override
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
        assertEquals("() = ()", a, b);
        assertEquals("length( () ) = 0", a.length(), 0);
        assertTrue("isONE( () )", a.isONE());
        assertTrue("isUnit( () )", a.isUnit());

        b = new Word(wf, "abc");
        c = wf.parse(" a b c ");
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        assertEquals("b = c: ", b, c);

        assertFalse("isONE( () )", b.isONE());
        assertFalse("isUnit( () )", b.isUnit());
        assertFalse("isONE( () )", c.isONE());
        assertFalse("isUnit( () )", c.isUnit());

        String s = b.toString();
        String t = c.toString();
        //System.out.println("s = " + s);
        //System.out.println("t = " + t);
        assertEquals("s = t: ", s, t);
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
        assertEquals("w = a", a, w);

        WordFactory wf2 = new WordFactory(w.toString());
        //System.out.println("wf2 = " + wf2);

        a = wf2.parse(w.toString());
        //System.out.println("a = " + a);
        assertEquals("w = a", a, w);

        List<Word> gens = wf.generators();
        //System.out.println("gens = " + gens);
        assertTrue("#gens == 7: ", gens.size() == 7);
        for (Word v : gens) {
            a = wf.parse(v.toString());
            assertEquals("a == v", a, v);
        }
    }


    /**
     * Test random word.
     */
    public void testRandom() {
        WordFactory wf = new WordFactory("uvw");
        //System.out.println("wf = " + wf);

        a = wf.random(5);
        b = wf.random(6);
        c = wf.random(7);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);

        assertFalse("a != (): ", a.isONE());
        assertFalse("b != (): ", b.isONE());
        assertFalse("c != (): ", c.isONE());
        assertTrue("#a == 5", a.length() == 5);
        assertTrue("#b == 6", b.length() == 6);
        assertTrue("#c == 7", c.length() == 7);

        SortedMap<String, Integer> ma = a.dependencyOnVariables();
        SortedMap<String, Integer> mb = b.dependencyOnVariables();
        SortedMap<String, Integer> mc = c.dependencyOnVariables();
        //System.out.println("ma = " + ma);
        //System.out.println("mb = " + mb);
        //System.out.println("mc = " + mc);
        assertTrue("#ma <= 3", ma.size() <= wf.length());
        assertTrue("#mb <= 3", mb.size() <= wf.length());
        assertTrue("#mc <= 3", mc.size() <= wf.length());
        assertTrue("S ma <= #a", sum(ma.values()) == a.length());
        assertTrue("S mb <= #b", sum(mb.values()) == b.length());
        assertTrue("S mc <= #c", sum(mc.values()) == c.length());
    }


    int sum(Collection<Integer> li) {
        int s = 0;
        for (Integer i : li) {
            s += i;
        }
        return s;
    }


    /**
     * Test multiplication.
     */
    public void testMultiplication() {
        WordFactory wf = new WordFactory("abcdefgx");
        a = new Word(wf, "abc");
        b = new Word(wf, "cddaa");
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
        assertEquals("d = b", d, b);

        d = c.divide(b);
        //System.out.println("d = " + d);
        assertEquals("d = a", d, a);

        d = c.divide(c);
        //System.out.println("d = " + d);
        assertTrue("isONE( () )", d.isONE());

        d = new Word(wf, "xx");
        c = a.multiply(d).multiply(b);
        //System.out.println("d = " + d);
        //System.out.println("c = " + c);

        assertTrue("divides: ", d.divides(c));
        Word[] ret = c.divideWord(d);
        //System.out.println("ret = " + ret[0] + ", " + ret[1]);

        assertEquals("prefix(c/d) = a", a, ret[0]);
        assertEquals("suffix(c/d) = b", b, ret[1]);

        Word e = ret[0].multiply(d).multiply(ret[1]);
        assertEquals("prefix(c/d) d suffix(c/d) = e", e, c);
    }


    /**
     * Test overlap.
     */
    public void testOverlap() {
        WordFactory wf = new WordFactory("abcdefg");
        a = new Word(wf, "abc");
        b = new Word(wf, "ddabca");
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        OverlapList ol = a.overlap(b);
        //System.out.println("ol = " + ol);
        assertTrue("isOverlap: ", ol.isOverlap(a,b));

        ol = b.overlap(a);
        //System.out.println("ol = " + ol);
        assertTrue("isOverlap: ", ol.isOverlap(b,a));

        a = new Word(wf,   "abcfff");
        b = new Word(wf, "ddabc");
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        ol = a.overlap(b);
        //System.out.println("ol = " + ol);
        assertTrue("isOverlap: ", ol.isOverlap(a,b));

        ol = b.overlap(a);
        //System.out.println("ol = " + ol);
        assertTrue("isOverlap: ", ol.isOverlap(b,a));

        a = new Word(wf, "fffabc");
        b = new Word(wf,    "abcdd");
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        ol = a.overlap(b);
        //System.out.println("ol = " + ol);
        assertTrue("isOverlap: ", ol.isOverlap(a,b));

        ol = b.overlap(a);
        //System.out.println("ol = " + ol);
        assertTrue("isOverlap: ", ol.isOverlap(b,a));

        a = new Word(wf, "ab");
        b = new Word(wf, "dabeabfabc");
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        ol = a.overlap(b);
        //System.out.println("ol = " + ol);
        assertTrue("isOverlap: ", ol.isOverlap(a,b));
        ol = b.overlap(a);
        //System.out.println("ol = " + ol);
        assertTrue("isOverlap: ", ol.isOverlap(b,a));

        a = new Word(wf, "abc");
        b = new Word(wf, "abceabcfabc");
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        ol = a.overlap(b);
        //System.out.println("ol = " + ol);
        assertTrue("isOverlap: ", ol.isOverlap(a,b));
        ol = b.overlap(a);
        //System.out.println("ol = " + ol);
        assertTrue("isOverlap: ", ol.isOverlap(b,a));

        a = new Word(wf, "aa");
        b = new Word(wf, "aaaaaaaaa");
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        ol = a.overlap(b);
        //System.out.println("ol = " + ol);
        assertTrue("isOverlap: ", ol.isOverlap(a,b));
        ol = b.overlap(a);
        //System.out.println("ol = " + ol);
        assertTrue("isOverlap: ", ol.isOverlap(b,a));
    }


    /**
     * Test valueOf.
     */
    public void testValueOf() {
        String[] vars = new String[] { "a", "b", "c", "d" };
        WordFactory wf = new WordFactory(vars);

        ExpVector ef = ExpVector.random(4,5L,0.5f);
        //System.out.println("ef = " + ef);

        a = wf.valueOf(ef);
        //System.out.println("a = " + a);
        assertTrue("deg(ef) == deg(a): " + ef + ", " + a, ef.degree() == a.degree() );

        String es = ef.toString(vars);
        //System.out.println("es = " + es);
        assertTrue("ef != ''" + ef, es.length() >= 0 );
    }


    /**
     * Test constructor with multi-letter Strings.
     */
    public void testMultiLetters() {
        String[] vars = new String[] {"a1", "b", " e23", "tt*", "x y" };
        WordFactory wf = new WordFactory(vars);
        //System.out.println("wf = " + wf);
        String s = wf.toString();
        assertEquals("w == vars: ", s, "\"a1,b,e23,tt,xy\"");

        Word w = wf.parse("a1 a1 b*b*b tt xy e23 tt xy");
        s = w.toString();
        String t = "\"a1 a1 b b b tt xy e23 tt xy\"";
        //System.out.println("s = " + s);
        //System.out.println("t = " + t);
        assertEquals("w == parse: ", s, t);

        Word u = wf.parse("xy e23 tt xy a1 a1 b*b*b tt");
        s = u.toString();
        String t1 = "\"xy e23 tt xy a1 a1 b b b tt\"";
        //System.out.println("s = " + s);
        //System.out.println("t = " + t1);
        assertEquals("w == parse: ", s, t1);

        Word v = u.multiply(w);
        s = v.toString();
        String t2 = t1.substring(0,t1.length()-1) + " " + t.substring(1);
        //System.out.println("s = " + s);
        //System.out.println("t = " + t2);
        assertEquals("w == parse: ", s, t2);

        v = w.multiply(u);
        s = v.toString();
        t2 = t.substring(0,t.length()-1) + " " + t1.substring(1);
        //System.out.println("s = " + s);
        //System.out.println("t = " + t2);
        assertEquals("w == parse: ", s, t2);

        w = wf.random(5);
        //System.out.println("w = " + w);
        u = wf.random(5);
        //System.out.println("u = " + u);
        v = u.multiply(w);
        //System.out.println("v = " + v);
        assertTrue("#v = #w+#u: ", v.length() == w.length()+u.length());

        List<Word> gens = wf.generators();
        //System.out.println("gens = " + gens);
        assertTrue("#gens == 5: ", gens.size() == 5);
        for (Word x : gens) {
            a = wf.parse(x.toString());
            assertEquals("a == x", a, x);
        }
    }

}
