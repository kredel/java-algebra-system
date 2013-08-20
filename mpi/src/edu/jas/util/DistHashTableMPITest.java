/*
 * $Id$
 */

package edu.jas.util;


import java.io.IOException;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import mpi.Comm;
import mpi.MPIException;

import org.apache.log4j.BasicConfigurator;

import edu.jas.kern.MPIEngine;


/**
 * DistHashTableMPI test with JUnit.
 * @author Heinz Kredel
 */
public class DistHashTableMPITest extends TestCase {


    protected static Comm engine;


    /**
     * main.
     */
    public static void main(String[] args) throws MPIException {
        //long t = System.currentTimeMillis();
        BasicConfigurator.configure();
        engine = MPIEngine.getCommunicator(args);
        junit.textui.TestRunner.run(suite());
        MPIEngine.terminate();
        //t = System.currentTimeMillis() - t;
        //System.out.println("MPI runtime = " + t + " milli seconds");
    }


    /**
     * Constructs a <CODE>DistHashTableMPITest</CODE> object.
     * @param name String.
     */
    public DistHashTableMPITest(String name) {
        super(name);
    }


    /**
     * suite.
     * @return a test suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(DistHashTableMPITest.class);
        return suite;
    }


    private DistHashTableMPI<Integer, Integer> l1;


    private DistHashTableMPI<Integer, Integer> l2;


    private DistHashTableMPI<Integer, Integer> l3;


    @Override
    protected void setUp() {
    }


    @Override
    protected void tearDown() {
        if (l1 != null)
            l1.terminate();
        if (l2 != null)
            l2.terminate();
        if (l3 != null)
            l3.terminate();
        l1 = l2 = l3 = null;
        try {
            //Thread.currentThread();
            //System.out.println("tearDown: sleep = 1");
            Thread.sleep(1);
        } catch (InterruptedException e) {
        }
    }


    /**
     * Tests create and terminate DistHashTableMPI.
     */
    public void xtestDistHashTable1() throws MPIException, IOException {
        l1 = new DistHashTableMPI<Integer, Integer>(engine);
        l1.init();
        assertTrue("l1==empty", l1.isEmpty());
    }


    /**
     * Tests if the created DistHashTable has #n objects as content.
     */
    public void xtestDistHashTable2() throws MPIException, IOException {
        int me = engine.Rank();
        l1 = new DistHashTableMPI<Integer, Integer>(engine);
        l1.init();
        assertTrue("l1==empty", l1.isEmpty());
        Integer s = 0;
        if (me == 0) {
            l1.putWait(Integer.valueOf(1), Integer.valueOf(1));
        } else {
            s = l1.getWait(Integer.valueOf(1));
        }
        assertFalse("l1!=empty: ", l1.isEmpty());
        assertTrue("#l1==1: " + l1.getList(), l1.size() >= 1);
        assertEquals("s == 1: ", s, Integer.valueOf(1));
        if (me == 0) {
            l1.putWait(Integer.valueOf(2), Integer.valueOf(2));
        } else {
            s = l1.getWait(Integer.valueOf(2));
        }
        assertTrue("#l1==2: " + l1.getList(), l1.size() >= 2);
        assertEquals("s == 2: ", s, Integer.valueOf(2));
        if (me == 0) {
            l1.putWait(Integer.valueOf(3), Integer.valueOf(3));
        } else {
            s = l1.getWait(Integer.valueOf(3));
        }
        assertTrue("#l1==3: " + l1.getList(), l1.size() >= 3);
        assertEquals("s == 3: ", s, Integer.valueOf(3));

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
    public void testDistHashTable3() throws MPIException, IOException {
        int me = engine.Rank();
        l2 = new DistHashTableMPI<Integer, Integer>(engine);
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
