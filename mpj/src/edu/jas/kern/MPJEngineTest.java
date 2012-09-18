/*
 * $Id$
 */

package edu.jas.kern;

import java.util.Arrays;

import mpi.Comm;
import mpi.MPI;
import mpi.Status;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;


/**
 * MPJEngine tests with JUnit.
 * @author Heinz Kredel.
 */

public class MPJEngineTest extends TestCase {


    /**
     * main
     */
    public static void main (String[] args) {
        cmdline = args;
        BasicConfigurator.configure();
        junit.textui.TestRunner.run( suite() );
        MPJEngine.terminate();
    }


    static String[] cmdline;

    static mpi.Comm engine;


    /**
     * Constructs a <CODE>MPJEngineTest</CODE> object.
     * @param name String.
     */
    public MPJEngineTest(String name) {
        super(name);
    }


    /**
     * suite.
     */ 
    public static Test suite() {
        TestSuite suite= new TestSuite(MPJEngineTest.class);
        return suite;
    }


    protected void setUp() {
        if ( engine == null ) {
            engine = MPJEngine.getCommunicator(cmdline);
        }
    }


    protected void tearDown() {
        if ( engine == null ) {
            return;
        }
        engine = null;
    }


    /**
     * Test MPJEngine.
     */
    public void testMPJEngine() {
        int me = engine.Rank();
        int size = engine.Size();
        assertTrue("size > 0", size > 0);
        assertTrue("0 <= me < size", 0 <= me && me < size);
        //System.out.println("testMPJEngine(): Hello World from " + me + " of " + size);
    }


    /**
     * Test communication.
     */
    public void testCommunication() {
        int me = engine.Rank();
        int size = engine.Size();
        int tag = 13;
        int[] data = new int[5];
        if ( me == 0 ) {
            //System.out.println("testCommunication(): from " + me + " of " + size);
            for ( int i = 1; i < size; i++ ) {
                 data[0] = i;
                 engine.Send(data,0,data.length,MPI.INT,i,tag);
            }
        } else {
            Status stat = engine.Recv(data,0,data.length,MPI.INT,0,tag);
            int cnt = stat.Get_count(MPI.INT);
            int elem = stat.Get_elements(MPI.INT);
            //System.out.println("testCommunication(): status " + me + ", " + cnt + ", " + elem);
            //System.out.println("testCommunication(): received " + Arrays.toString(data));
            assertTrue("length == count", data.length == cnt);
            assertTrue("recv == me", data[0] == me);
        }
        //System.out.println("testCommunication(): done");
    }

}
