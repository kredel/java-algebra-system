/*
 * $Id$
 */

package edu.jas.ps;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.util.CartesianProductInfinite;
import edu.jas.util.CartesianProductLong;
import edu.jas.util.LongIterable;


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

        Set<ExpVector> set = new TreeSet<ExpVector>((new TermOrder()).getDescendComparator());

        Iterable<List<Long>> ib = new CartesianProductInfinite<Long>(tlist);

        long t = 0L;
        for (List<Long> i : ib) {
            //System.out.println("i = " + i);
            ExpVector e = ExpVector.create(i);
            //System.out.println("e = " + e);
            assertFalse("e in set", set.contains(e));
            set.add(e);
            t++;
            if (t > 100L) {
                //System.out.println("i = " + i);
                break;
            }
        }
        //System.out.println("set = " + set);
        assertTrue("#set", set.size() == t);
    }


    /**
     * Test GenPolynomial iterator.
     * 
     */
    public void testGenPolynomial() {
        ModIntegerRing mi = new ModIntegerRing(5, true);
        int n = 3;
        GenPolynomialRing<ModInteger> ring = new GenPolynomialRing<ModInteger>(mi, n);

        Set<GenPolynomial<ModInteger>> set = new TreeSet<GenPolynomial<ModInteger>>();

        long t = 0;
        for (GenPolynomial<ModInteger> p : ring) {
            //System.out.println("p = " + p);
            if (set.contains(p)) {
                System.out.println("p = " + p);
                System.out.println("set = " + set);
                assertFalse("p in set ", true);
            }
            set.add(p);
            t++;
            if (t > 650L) {
                //System.out.println("i = " + i);
                break;
            }
        }
        //System.out.println("set = " + set);
        assertTrue("#set", set.size() == t);
    }


    /**
     * Test GenPolynomial monomial iterator.
     * 
     */
    public void testGenPolynomialMonomial() {
        BigInteger bi = new BigInteger(1);
        int n = 3;
        GenPolynomialRing<BigInteger> ring = new GenPolynomialRing<BigInteger>(bi, n);

        Set<GenPolynomial<BigInteger>> set = new TreeSet<GenPolynomial<BigInteger>>();

        long t = 0;
        for (GenPolynomial<BigInteger> p : ring) {
            //System.out.println("p = " + p);
            if (set.contains(p)) {
                System.out.println("p = " + p);
                //System.out.println("set = " + set);
                assertFalse("p in set ", true);
            }
            set.add(p);
            t++;
            if (t > 650L) {
                //System.out.println("i = " + i);
                break;
            }
        }
        //System.out.println("set = " + set);
        assertTrue("#set", set.size() == t);
    }


    /**
     * Test total degree ExpVector iterator.
     * 
     */
    public void testTotalDegExpVector() {
        int n = 4;

        Set<ExpVector> set = new TreeSet<ExpVector>((new TermOrder()).getDescendComparator());

        Map<Long, Set<ExpVector>> degset = new TreeMap<Long, Set<ExpVector>>();

        long t = 0L;
        for (long k = 0; k < 14; k++) {
            LongIterable li = new LongIterable();
            li.setNonNegativeIterator();
            li.setUpperBound(k);
            List<LongIterable> tlist = new ArrayList<LongIterable>(n);
            for (int i = 0; i < n; i++) {
                tlist.add(li); // can reuse li
            }
            long kdeg = k;
            //if ( kdeg > 5 ) { // kdeg < k ok but kdeg > k not allowed
            //    kdeg -= 2;
            //}
            Iterable<List<Long>> ib = new CartesianProductLong(tlist, kdeg);
            //System.out.println("kdeg = " + kdeg);
            for (List<Long> i : ib) {
                //System.out.println("i = " + i);
                ExpVector e = ExpVector.create(i);
                long tdeg = e.totalDeg();
                //System.out.println("e = " + e + ", deg = " + tdeg);
                assertTrue("tdeg == k", tdeg == kdeg);
                Set<ExpVector> es = degset.get(tdeg);
                if (es == null) {
                    es = new TreeSet<ExpVector>((new TermOrder()).getDescendComparator());
                    degset.put(tdeg, es);
                }
                es.add(e);
                //assertFalse("e in set", set.contains(e) );
                set.add(e);
                t++;
                if (t > 500000L) {
                    //System.out.println("i = " + i);
                    break;
                }
            }
        }
        //System.out.println("waste = " + w + ", of " + t);
        //System.out.println("set = " + set);
        //System.out.println("degset = " + degset);
        for (Set<ExpVector> es : degset.values()) {
            assertFalse("es != null", es == null);
            //System.out.println("#es = " + es.size() + ", es = " + es);
            //System.out.println("#es = " + es.size() + ", deg = " + es.iterator().next().totalDeg());
        }
        assertTrue("#set", set.size() == t);
    }


    /**
     * Test total degree ExpVector iterator.
     * 
     */
    public void testTotalDegExpVectorIteratorInf() {
        int n = 4;

        Set<ExpVector> set = new TreeSet<ExpVector>((new TermOrder()).getDescendComparator());

        ExpVectorIterable eiter = new ExpVectorIterable(n);
        long t = 0;
        for (ExpVector e : eiter) {
            //System.out.println("e = " + e + ", deg = " + e.totalDeg());
            t++;
            if (t > 500L) {
                //System.out.println("i = " + i);
                break;
            }
            assertFalse("e in set", set.contains(e));
            set.add(e);
        }

    }


    /**
     * Test total degree ExpVector iterator.
     * 
     */
    public void testTotalDegExpVectorIteratorFin() {
        int n = 4;

        Set<ExpVector> set = new TreeSet<ExpVector>((new TermOrder()).getDescendComparator());

        ExpVectorIterable eiter = new ExpVectorIterable(n, 5);
        long t = 0;
        for (ExpVector e : eiter) {
            //System.out.println("e = " + e + ", deg = " + e.totalDeg());
            t++;
            if (t > 500L) {
                //System.out.println("i = " + i);
                break;
            }
            assertFalse("e in set", set.contains(e));
            set.add(e);
        }

    }


    /**
     * Test total degree ExpVector iterator.
     * 
     */
    public void testTotalDegExpVectorIteratorAllFin() {
        int n = 4;

        Set<ExpVector> set = new TreeSet<ExpVector>((new TermOrder()).getDescendComparator());

        ExpVectorIterable eiter = new ExpVectorIterable(n, true, 5);
        long t = 0;
        for (ExpVector e : eiter) {
            //System.out.println("e = " + e + ", deg = " + e.totalDeg());
            t++;
            if (t > 500L) {
                //System.out.println("i = " + i);
                break;
            }
            assertFalse("e in set", set.contains(e));
            set.add(e);
        }

    }

}
