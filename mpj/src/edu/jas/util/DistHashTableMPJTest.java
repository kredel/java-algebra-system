/*
 * $Id$
 */

package edu.jas.util;


import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import mpi.Comm;

import edu.jas.kern.MPJEngine;


/**
 * DistHashTableMPJ test with JUnit.
 * @author Heinz Kredel
 */
public class DistHashTableMPJTest extends TestCase {


    protected static Comm engine;


    /**
     * main.
     */
    public static void main(String[] args) {
        //long t = System.currentTimeMillis();
        BasicConfigurator.configure();
        engine = MPJEngine.getCommunicator(args);
        junit.textui.TestRunner.run(suite());
        engine.Barrier();
        MPJEngine.terminate();
        //t = System.currentTimeMillis() - t;
        //System.out.println("MPJ runtime = " + t + " milli seconds");
    }


    /**
     * Constructs a <CODE>DistHashTableMPJTest</CODE> object.
     * @param name String.
     */
    public DistHashTableMPJTest(String name) {
        super(name);
    }


    /**
     * suite.
     * @return a test suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(DistHashTableMPJTest.class);
        return suite;
    }


    private DistHashTableMPJ<Integer, Integer> l1;


    private DistHashTableMPJ<Integer, Integer> l2;


    private DistHashTableMPJ<Integer, Integer> l3;


    protected void setUp() {
        engine.Barrier();
    }


    protected void tearDown() {
        engine.Barrier();
        if (l1 != null)
            l1.terminate();
        if (l2 != null)
            l2.terminate();
        if (l3 != null)
            l3.terminate();
        l1 = l2 = l3 = null;
        try {
            //System.out.println("tearDown: sleep = 1");
            Thread.currentThread().sleep(1);
        } catch (InterruptedException e) {
        }
        engine.Barrier();
    }


    /**
     * Tests create and terminate DistHashTableMPJ.
     */
    public void xtestDistHashTable1() {
        l1 = new DistHashTableMPJ<Integer, Integer>(engine);
        l1.init();
        assertTrue("l1==empty", l1.isEmpty());
    }


    /**
     * Tests if the created DistHashTable has #n objects as content.
     */
    public void xtestDistHashTable2() {
        int me = engine.Rank();
        l1 = new DistHashTableMPJ<Integer, Integer>(engine);
        l1.init();
        assertTrue("l1==empty", l1.isEmpty());
        if (me == 0) {
            l1.putWait(Integer.valueOf(1), Integer.valueOf(1));
        } else {
            Integer s = l1.getWait(Integer.valueOf(1));
        }
        assertFalse("l1!=empty: ", l1.isEmpty());
        assertTrue("#l1==1: " + l1.getList(), l1.size() >= 1);
        if (me == 0) {
            l1.putWait(Integer.valueOf(2), Integer.valueOf(2));
        } else {
            Integer s = l1.getWait(Integer.valueOf(2));
        }
        assertTrue("#l1==2: " + l1.getList(), l1.size() >= 2);
        if (me == 0) {
            l1.putWait(Integer.valueOf(3), Integer.valueOf(3));
        } else {
            Integer s = l1.getWait(Integer.valueOf(3));
        }
        assertTrue("#l1==3: " + l1.getList(), l1.size() >= 3);

        Iterator it = null;
        it = l1.iterator();
        int i = 0;
        while (it.hasNext()) {
            Object k = it.next();
            Object o = l1.get(k);
            Integer x = Integer.valueOf(++i);
            assertEquals("l1(i)==v(i)", x, o);
            assertEquals("l1(i)==k(i)", x, k);
        }
        l1.clear();
        assertTrue("#l1==0", l1.size() == 0);
    }


    /**
     * Tests if the two created DistHashTables have #n objects as content.
     */
    public void testDistHashTable3() {
        int me = engine.Rank();
        l2 = new DistHashTableMPJ<Integer, Integer>(engine);
        l2.init();
        //System.out.println("test3: me = " + me + ", l2 = "+ l2);
        assertTrue("l2==empty", l2.isEmpty());

        int i = 0, loops = 10;
        while (i < loops) {
            Integer x = Integer.valueOf(++i);
            //System.out.println("me = " + me + ", x = "+ x);
            if (me == 0) {
                l2.putWait(x, x);
            } else {
                Integer s = l2.getWait(x);
                assertEquals("s = x: " + s + ", " + x, s, x);
            }
            assertTrue("#l1==i: " + i + ", #l1 = " + l2.size(), l2.size() >= i);
        }
        assertTrue("#l2==" + loops, l2.size() == loops);

        Iterator it = l2.iterator();
        i = 0;
        while (it.hasNext()) {
            Object k = it.next();
            Object o = l2.get(k);
            Integer x = Integer.valueOf(++i);
            //System.out.println("me = " + me + ", o = " + o + ", x = "+ x);
            assertEquals("l2(i)==k(i)", x, k);
            assertEquals("l2(i)==v(i)", x, o);
        }
    }

}
