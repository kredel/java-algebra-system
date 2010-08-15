/*
 * $Id$
 */

package edu.jas.util;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigInteger;
import edu.jas.poly.ExpVector;
import edu.jas.poly.TermOrder;


/**
 * Iterator tests with JUnit.
 * @author Heinz Kredel.
 */

public class IteratorsTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>ListUtilTest</CODE> object.
     * @param name String.
     */
    public IteratorsTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(IteratorsTest.class);
        return suite;
    }


    @Override
    protected void setUp() {
    }


    @Override
    protected void tearDown() {
    }


    /**
     * Test cartesian product.
     * 
     */
    public void testCartesianProduct() {
        BigInteger ai = new BigInteger();
        int s1 = 5;
        int s2 = 3;
        int s = 1;
        for (int i = 0; i < s1; i++) {
            s *= s2;
        }
        //System.out.println("s = " + s);
        List<List<BigInteger>> tlist = new ArrayList<List<BigInteger>>(s1);
        for (int i = 0; i < s1; i++) {
            List<BigInteger> list = new ArrayList<BigInteger>(s2);
            for (int j = 0; j < s2; j++) {
                list.add(ai.fromInteger(j));
            }
            tlist.add(list);
        }
        //System.out.println("tlist = " + tlist);
        int t = 0;
        for (List<BigInteger> tuple : new CartesianProduct<BigInteger>(tlist)) {
            t++;
            //System.out.println("tuple = " + tuple);
            assertTrue("|tuple| == " + s1 + " ", s1 == tuple.size());
        }
        assertTrue("#tuple == " + s + " == " + t + " ", t == s);
    }


    /**
     * Test power set.
     * 
     */
    public void testPowerSet() {
        BigInteger ai = new BigInteger();
        int s1 = 5;
        int s = 1;
        for (int i = 0; i < s1; i++) {
            s *= 2;
        }
        //System.out.println("s = " + s);
        List<BigInteger> tlist = new ArrayList<BigInteger>(s1);
        for (int j = 0; j < s1; j++) {
            tlist.add(ai.random(7));
        }
        //System.out.println("tlist = " + tlist);
        int t = 0;
        for (List<BigInteger> tuple : new PowerSet<BigInteger>(tlist)) {
            t++;
            //System.out.println("tuple = " + tuple);
        }
        assertTrue("#tuple == " + s + " == " + t + " ", t == s);
    }


    /**
     * Test k-subset set.
     * 
     */
    public void testKsubSet() {
        BigInteger ai = new BigInteger();
        int s1 = 5;
        int s = 1;
        for (int i = 0; i < s1; i++) {
            s *= 2;
        }
        //System.out.println("s = " + s);
        List<BigInteger> tlist = new ArrayList<BigInteger>(s1);
        for (int j = 0; j < s1; j++) {
            tlist.add(ai.random(7));
        }
        //System.out.println("tlist = " + tlist);
        int t = 0;
        for (int k = 0; k <= s1; k++) {
            for (List<BigInteger> tuple : new KsubSet<BigInteger>(tlist, k)) {
                t++;
                //System.out.println("tuple = " + tuple);
                assertTrue("|tuple| == " + k + " ", k == tuple.size());
            }
        }
        assertTrue("#tuple == " + s + " == " + t + " ", t == s);
    }


    /**
     * Test infinite cartesian product.
     * 
     */
    public void testInfiniteCartesianProductTwoList() {
        BigInteger ai = new BigInteger();
        ai.setNonNegativeIterator();
        int s1 = 2;
        //System.out.println("s1 = " + s1);
        List<Iterable<BigInteger>> tlist = new ArrayList<Iterable<BigInteger>>(s1);
        for (int i = 0; i < s1; i++) {
            tlist.add(ai);
        }
        //System.out.println("tlist = " + tlist);
        Set<List<BigInteger>> set = new HashSet<List<BigInteger>>();

        int s2 = 5;
        int s = 1;
        for (int i = 0; i < s1; i++) {
            s *= s2;
        }
        //System.out.println("s = " + s);
        List<List<BigInteger>> ftlist = new ArrayList<List<BigInteger>>(s1);
        for (int i = 0; i < s1; i++) {
            List<BigInteger> list = new ArrayList<BigInteger>(s2);
            for (int j = 0; j < s2; j++) {
                list.add(ai.fromInteger(j));
            }
            ftlist.add(list);
        }
        //System.out.println("tlist = " + tlist);
        int r = 0;
        for (List<BigInteger> tuple : new CartesianProduct<BigInteger>(ftlist)) {
            r++;
            set.add(tuple); 
        }
        //System.out.println("set = " + set.size());
        //System.out.println("set = " + r);

        int t = 0;
        int h = 0;
        Iterable<List<BigInteger>> ib = new CartesianProductInfinite<BigInteger>(tlist);
        Iterator<List<BigInteger>> iter = ib.iterator();
        while ( iter.hasNext() ) {
            List<BigInteger> tuple = iter.next();
            t++;
            //System.out.println("tuple = " + tuple);
            //assertTrue("|tuple| == " + s1 + " ", s1 == tuple.size());
            if ( set.contains(tuple) ) {
                h++;
            }
            if ( h >= r ) {
                break;
            }
            assertTrue("#tuple <= 125 " + t, t <= 125);
        }
        //System.out.println("#tuple = " + t + ", #set = " + r);
    }


    /**
     * Test infinite cartesian product.
     * 
     */
    public void testInfiniteCartesianProduct() {
        BigInteger ai = new BigInteger();
        ai.setNonNegativeIterator();
        int s1 = 4;
        //System.out.println("s1 = " + s1);
        List<Iterable<BigInteger>> tlist = new ArrayList<Iterable<BigInteger>>(s1);
        for (int i = 0; i < s1; i++) {
            tlist.add(ai);
        }
        //System.out.println("tlist = " + tlist);
        Set<List<BigInteger>> set = new HashSet<List<BigInteger>>();

        int s2 = 5;
        int s = 1;
        for (int i = 0; i < s1; i++) {
            s *= s2;
        }
        //System.out.println("s = " + s);
        List<List<BigInteger>> ftlist = new ArrayList<List<BigInteger>>(s1);
        for (int i = 0; i < s1; i++) {
            List<BigInteger> list = new ArrayList<BigInteger>(s2);
            for (int j = 0; j < s2; j++) {
                list.add(ai.fromInteger(j));
            }
            ftlist.add(list);
        }
        //System.out.println("tlist = " + tlist);
        int r = 0;
        for (List<BigInteger> tuple : new CartesianProduct<BigInteger>(ftlist)) {
            r++;
            set.add(tuple); 
        }
        //System.out.println("set = " + set.size());
        //System.out.println("set = " + r);

        int t = 0;
        int h = 0;
        Iterable<List<BigInteger>> ib = new CartesianProductInfinite<BigInteger>(tlist);
        Iterator<List<BigInteger>> iter = ib.iterator();
        while ( iter.hasNext() ) {
            List<BigInteger> tuple = iter.next();
            t++;
            //System.out.println("tuple = " + tuple);
            //assertTrue("|tuple| == " + s1 + " ", s1 == tuple.size());
            if ( set.contains(tuple) ) {
                h++;
            }
            if ( h >= r ) {
                break;
            }
            assertTrue("#tuple <= 3281 " + t, t <= 3281);
        }
        //System.out.println("#tuple = " + t + ", #set = " + r);
    }


    /**
     * Test Long iterator.
     * 
     */
    public void testLong() {
        LongIterable li = new LongIterable();
        li.setNonNegativeIterator();
        long s = 0L;
        long t = 0L;
        for ( Long i : li ) {
            //System.out.println("i = " + i);
            s = i;
            assertTrue("t == i", t == i );
            t++;
            if ( t > 1000L ) { //% 100000000L == 0L ) {
	        //System.out.println("i = " + i);
                break;
 	    }
	}
        //System.out.println("t = " + t);
        assertTrue("i == 1000", s == 1000L );

        li.setAllIterator();
        s = 0L;
        t = 0L;
        for ( Long i : li ) {
            //System.out.println("i = " + i);
            s = i;
            //assertTrue("t == i", t == i );
            t++;
            if ( t >= 1000L ) { //% 100000000L == 0L ) {
	        //System.out.println("i = " + i);
                break;
 	    }
	}
        //System.out.println("t = " + t);
        assertTrue("i == 500", s == 500L );
    }


    /**
     * Test ExpVector iterator.
     * 
     */
    public void testExpVector() {
        int n = 5;
        LongIterable li = new LongIterable();
        li.setNonNegativeIterator();

        List<Iterable<Long>> tlist = new ArrayList<Iterable<Long>>(n);
        for (int i = 0; i < n; i++) {
            tlist.add(li);
        }
        //System.out.println("tlist = " + tlist);

        Set<ExpVector> set = new TreeSet<ExpVector>( (new TermOrder()).getDescendComparator() );

        Iterable<List<Long>> ib = new CartesianProductInfinite<Long>(tlist);

        long t = 0L;
        for ( List<Long> i : ib ) {
            //System.out.println("i = " + i);
            ExpVector e = ExpVector.create(i);
            //System.out.println("e = " + e);
            assertFalse("e in set", set.contains(e) );
            set.add(e);
            t++;
            if ( t > 100L ) { 
	        //System.out.println("i = " + i);
                break;
 	    }
	}
        //System.out.println("set = " + set);
        assertTrue("#set", set.size() == t );
    }

}
