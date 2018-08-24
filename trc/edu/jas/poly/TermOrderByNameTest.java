/*
 * $Id$
 */

package edu.jas.poly;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;



/**
 * TermOrderByName tests with JUnit. Tests different names for TermOrders.
 * @author Heinz Kredel
 */

public class TermOrderByNameTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>TermOrderByNameTest</CODE> object.
     * @param name String.
     */
    public TermOrderByNameTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(TermOrderByNameTest.class);
        return suite;
    }


    ExpVector a, b, c, d;


    TermOrder t, s;


    @Override
    protected void setUp() {
        a = b = c = d = null;
        t = s = null;
    }


    @Override
    protected void tearDown() {
        a = b = c = d = null;
        t = s = null;
    }


    /**
     * Test constructor and toString.
     */
    public void testConstructor() {

        s = TermOrderByName.DEFAULT;
        t = TermOrderByName.DEFAULT;
        assertEquals("t = s", t, s);

        String x = t.toString();
        String y = s.toString();

        assertEquals("x = y", x, y);

        t = TermOrderByName.Lexicographic;
        x = "REVILEX";
        y = t.toString();
        boolean z = y.startsWith(x);
        assertTrue("REVILEX(.): " + y, z);

        s = TermOrderByName.DegreeLexicographic;
        t = TermOrderByName.DegreeLexicographic;
        assertEquals("t = s", t, s);
    }


    /*
     * Test constructor, split TO.
     */
    public void testConstructorSplit() {
        int r = 10;
        int sp = 5;

        ExpVector ev = ExpVectorLong.create(r);
        s = TermOrderByName.blockOrder(TermOrderByName.DegreeLexicographic,
                        TermOrderByName.DegreeLexicographic, ev, sp);
        t = TermOrderByName.blockOrder(TermOrderByName.DegreeLexicographic,
                        TermOrderByName.DegreeLexicographic, ev, sp);
        assertEquals("t = s", t, s);

        String x = t.toString();
        String y = s.toString();
        assertEquals("x = y", x, y);
        //System.out.println("s = " + s);

        s = TermOrderByName.blockOrder(TermOrderByName.DegreeLexicographic, TermOrderByName.Lexicographic,
                        ev, sp);
        t = TermOrderByName.blockOrder(TermOrderByName.DegreeLexicographic, TermOrderByName.Lexicographic,
                        ev, sp);
        assertEquals("t = s", t, s);
        //System.out.println("s = " + s);
    }


    /**
     * Test constructor weight and toString.
     */
    public void testConstructorWeight() {
        long[][] w = new long[][] { new long[] { 1l, 1l, 1l, 1l, 1l } };
        s = TermOrderByName.weightOrder(w);
        t = TermOrderByName.weightOrder(w);
        //System.out.println("s = " + s);
        //System.out.println("t = " + t);
        assertEquals("t = s", t, s);

        String x = t.toString();
        String y = s.toString();

        //System.out.println("x = " + x);
        //System.out.println("y = " + y);
        assertEquals("x = y", x, y);

        //int r = 5;
        //int sp = 3;
        //w = new long [][] { new long[] { 5l, 4l, 3l, 2l, 1l } };

        //s = new TermOrder(w,sp);
        //t = new TermOrder(w,sp);
        //assertEquals("t = s",t,s);

        //x = t.toString();
        //y = s.toString();

        //assertEquals("x = y",x,y);
        //System.out.println("s = " + s);

        x = "W(";
        boolean z = t.toString().startsWith(x);
        assertTrue("W(.)", z);
    }


    /**
     * Test compare weight.
     */
    public void testCompareWeight() {
        float q = (float) 0.9;

        a = ExpVector.random(5, 10, q);
        b = ExpVector.random(5, 10, q);
        c = a.sum(b);

        long[][] w = new long[][] { new long[] { 1l, 1l, 1l, 1l, 1l } };
        t = TermOrderByName.weightOrder(w);

        int x = ExpVector.EVIWLC(w, c, a);
        int y = ExpVector.EVIWLC(w, c, b);

        assertEquals("x = 1", 1, x);
        assertEquals("y = 1", 1, y);

        x = ExpVector.EVIWLC(w, a, c);
        y = ExpVector.EVIWLC(w, b, c);

        assertEquals("x = -1", -1, x);
        assertEquals("y = -1", -1, y);

        x = ExpVector.EVIWLC(w, a, a);
        y = ExpVector.EVIWLC(w, b, b);

        assertEquals("x = 0", 0, x);
        assertEquals("y = 0", 0, y);
    }


    /**
     * Test compare weight 2 rows.
     */
    public void testCompareWeight2() {
        float q = (float) 0.9;

        a = ExpVector.random(5, 10, q);
        b = ExpVector.random(5, 10, q);
        c = a.sum(b);

        long[][] w = new long[][] { new long[] { 1l, 1l, 1l, 1l, 1l }, new long[] { 1l, 1l, 1l, 1l, 1l } };
        t = TermOrderByName.weightOrder(w);

        int x = ExpVector.EVIWLC(w, c, a);
        int y = ExpVector.EVIWLC(w, c, b);

        assertEquals("x = 1", 1, x);
        assertEquals("y = 1", 1, y);

        x = ExpVector.EVIWLC(w, a, c);
        y = ExpVector.EVIWLC(w, b, c);

        assertEquals("x = -1", -1, x);
        assertEquals("y = -1", -1, y);

        x = ExpVector.EVIWLC(w, a, a);
        y = ExpVector.EVIWLC(w, b, b);

        assertEquals("x = 0", 0, x);
        assertEquals("y = 0", 0, y);
    }


    /**
     * Test ascend comparators.
     */
    public void testAscendComparator() {
        float q = (float) 0.9;

        a = ExpVector.random(5, 10, q);
        b = ExpVector.random(5, 10, q);
        c = a.sum(b);

        t = TermOrderByName.DegreeLexicographic;

        int x = t.getAscendComparator().compare(c, a);
        int y = t.getAscendComparator().compare(c, b);

        assertEquals("x = 1", 1, x);
        assertEquals("y = 1", 1, y);

        x = t.getAscendComparator().compare(a, c);
        y = t.getAscendComparator().compare(b, c);

        assertEquals("x = -1", -1, x);
        assertEquals("y = -1", -1, y);

        x = t.getAscendComparator().compare(a, a);
        y = t.getAscendComparator().compare(b, b);

        assertEquals("x = 0", 0, x);
        assertEquals("y = 0", 0, y);
    }


    /**
     * Test ascend comparators split.
     */
    public void testAscendComparatorSplit() {
        float q = (float) 0.9;

        int r = 10;
        int sp = 5;

        a = ExpVector.random(r, 10, q);
        b = ExpVector.random(r, 10, q);
        c = a.sum(b);

        t = TermOrderByName.blockOrder(TermOrderByName.DegreeLexicographic, TermOrderByName.Lexicographic, c,
                        sp);

        int x = t.getAscendComparator().compare(c, a);
        int y = t.getAscendComparator().compare(c, b);
        assertEquals("x = 1", 1, x);
        assertEquals("y = 1", 1, y);

        x = t.getAscendComparator().compare(a, c);
        y = t.getAscendComparator().compare(b, c);
        assertEquals("x = -1", -1, x);
        assertEquals("y = -1", -1, y);

        x = t.getAscendComparator().compare(a, a);
        y = t.getAscendComparator().compare(b, b);
        assertEquals("x = 0", 0, x);
        assertEquals("y = 0", 0, y);
    }


    /**
     * Test ascend comparators weight and split.
     */
    public void testAscendComparatorWeightSplit() {
        float q = (float) 0.9;
        int r = 8;
        //int sp = 5;

        a = ExpVector.random(r, 10, q);
        b = ExpVector.random(r, 10, q);
        c = a.sum(b);

        //long [][] w  = new long [][] { new long[] { 1l, 2l, 3l, 4l, 5l, 1l, 2l, 3l } };
        long[][] w2 = new long[][] { new long[] { 1l, 2l, 3l, 4l, 5l, 0l, 0l, 0l },
                new long[] { 0l, 0l, 0l, 0l, 0l, 1l, 2l, 3l } };
        // t = new TermOrder(w,sp);
        t = TermOrderByName.weightOrder(w2);
        TermOrder t2 = TermOrderByName.weightOrder(w2);
        assertEquals("t = t2", t, t2);

        int x = t.getAscendComparator().compare(c, a);
        int y = t.getAscendComparator().compare(c, b);
        assertEquals("x = 1", 1, x);
        assertEquals("y = 1", 1, y);

        int x2 = t2.getAscendComparator().compare(c, a);
        int y2 = t2.getAscendComparator().compare(c, b);
        assertEquals("x2 = 1", 1, x2);
        assertEquals("y2 = 1", 1, y2);

        assertEquals("x = x2", x, x2);
        assertEquals("y = y2", y, y2);


        x = t.getAscendComparator().compare(a, c);
        y = t.getAscendComparator().compare(b, c);
        assertEquals("x = -1", -1, x);
        assertEquals("y = -1", -1, y);

        x2 = t2.getAscendComparator().compare(a, c);
        y2 = t2.getAscendComparator().compare(b, c);
        assertEquals("x2 = -1", -1, x2);
        assertEquals("y2 = -1", -1, y2);

        assertEquals("x = x2", x, x2);
        assertEquals("y = y2", y, y2);


        x = t.getAscendComparator().compare(a, a);
        y = t.getAscendComparator().compare(b, b);
        assertEquals("x = 0", 0, x);
        assertEquals("y = 0", 0, y);

        x2 = t2.getAscendComparator().compare(a, a);
        y2 = t2.getAscendComparator().compare(b, b);
        assertEquals("x2 = 0", 0, x2);
        assertEquals("y2 = 0", 0, y2);

        assertEquals("x = x2", x, x2);
        assertEquals("y = y2", y, y2);
    }


    /**
     * Test descend comparators.
     */
    public void testDescendComparator() {
        float q = (float) 0.9;

        a = ExpVector.random(5, 10, q);
        b = ExpVector.random(5, 10, q);
        c = a.sum(b);

        t = TermOrderByName.DegreeLexicographic;

        int x = t.getDescendComparator().compare(c, a);
        int y = t.getDescendComparator().compare(c, b);

        assertEquals("x = -1", -1, x);
        assertEquals("y = -1", -1, y);

        x = t.getDescendComparator().compare(a, c);
        y = t.getDescendComparator().compare(b, c);

        assertEquals("x = 1", 1, x);
        assertEquals("y = 1", 1, y);

        x = t.getDescendComparator().compare(a, a);
        y = t.getDescendComparator().compare(b, b);

        assertEquals("x = 0", 0, x);
        assertEquals("y = 0", 0, y);
    }


    /**
     * Test descend comparators split.
     */
    public void testDescendComparatorSplit() {
        float q = (float) 0.9;
        int r = 10;
        int sp = 5;

        a = ExpVector.random(r, 10, q);
        b = ExpVector.random(r, 10, q);
        c = a.sum(b);

        t = TermOrderByName.blockOrder(TermOrderByName.DegreeLexicographic, TermOrderByName.Lexicographic, c,
                        sp);

        int x = t.getDescendComparator().compare(c, a);
        int y = t.getDescendComparator().compare(c, b);
        assertEquals("x = -1", -1, x);
        assertEquals("y = -1", -1, y);

        x = t.getDescendComparator().compare(a, c);
        y = t.getDescendComparator().compare(b, c);
        assertEquals("x = 1", 1, x);
        assertEquals("y = 1", 1, y);

        x = t.getDescendComparator().compare(a, a);
        y = t.getDescendComparator().compare(b, b);
        assertEquals("x = 0", 0, x);
        assertEquals("y = 0", 0, y);
    }


    /**
     * Test descend comparators weight and split.
     */
    public void testDescendComparatorWeightSplit() {
        float q = (float) 0.9;
        int r = 8;
        //int sp = 5;

        a = ExpVector.random(r, 10, q);
        b = ExpVector.random(r, 10, q);
        c = a.sum(b);

        //long [][] w  = new long [][] { new long[] { 1l, 2l, 3l, 4l, 5l, 1l, 2l, 3l } };
        long[][] w2 = new long[][] { new long[] { 1l, 2l, 3l, 4l, 5l, 0l, 0l, 0l },
                new long[] { 0l, 0l, 0l, 0l, 0l, 1l, 2l, 3l } };
        //t = new TermOrder(w,sp);
        t = TermOrderByName.weightOrder(w2);
        TermOrder t2 = TermOrderByName.weightOrder(w2);
        assertEquals("t = t2", t, t2);

        int x = t.getDescendComparator().compare(c, a);
        int y = t.getDescendComparator().compare(c, b);
        assertEquals("x = -1", -1, x);
        assertEquals("y = -1", -1, y);

        int x2 = t2.getDescendComparator().compare(c, a);
        int y2 = t2.getDescendComparator().compare(c, b);
        assertEquals("x2 = -1", -1, x2);
        assertEquals("y2 = -1", -1, y2);

        assertEquals("x = x2", x, x2);
        assertEquals("y = y2", y, y2);


        x = t.getDescendComparator().compare(a, c);
        y = t.getDescendComparator().compare(b, c);
        assertEquals("x = 1", 1, x);
        assertEquals("y = 1", 1, y);

        x2 = t2.getDescendComparator().compare(a, c);
        y2 = t2.getDescendComparator().compare(b, c);
        assertEquals("x2 = 1", 1, x2);
        assertEquals("y2 = 1", 1, y2);

        assertEquals("x = x2", x, x2);
        assertEquals("y = y2", y, y2);


        x = t.getDescendComparator().compare(a, a);
        y = t.getDescendComparator().compare(b, b);
        assertEquals("x = 0", 0, x);
        assertEquals("y = 0", 0, y);

        x2 = t2.getDescendComparator().compare(a, a);
        y2 = t2.getDescendComparator().compare(b, b);
        assertEquals("x2 = 0", 0, x2);
        assertEquals("y2 = 0", 0, y2);

        assertEquals("x = x2", x, x2);
        assertEquals("y = y2", y, y2);
    }


    /**
     * Test compare exception split.
     */
    public void testCompareExceptionSplit() {
        float q = (float) 0.9;
        int r = 10;
        int sp = 5;

        a = ExpVector.random(r, 10, q);
        b = ExpVector.random(r, 10, q);
        c = ExpVector.random(2, 10, q);

        TermOrder t2 = TermOrderByName.REVITDG;
        int x = 0;

        try {
            t = TermOrderByName.blockOrder(t2, t2, c, sp);
            x = t.getDescendComparator().compare(a, b);
            fail("IllegalArgumentException " + x);
        } catch (IllegalArgumentException e) {
            //return;
        } catch (NullPointerException e) {
            //return;
        }
    }


    /**
     * Test compare exception weight.
     */
    public void testCompareExceptionWeigth() {
        float q = (float) 0.9;
        int r = 10;

        a = ExpVector.random(r, 10, q);
        b = ExpVector.random(2, 10, q);

        int x = 0;

        try {
            t = TermOrderByName.weightOrder((long[][]) null);
            x = t.getDescendComparator().compare(a, b);
            fail("IllegalArgumentException " + x);
        } catch (IllegalArgumentException e) {
            //return;
        } catch (NullPointerException e) {
            //return;
        } catch (ArrayIndexOutOfBoundsException e) {
            //return;
        }
    }

}
