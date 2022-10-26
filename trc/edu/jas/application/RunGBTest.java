/*
 * $Id$
 */

package edu.jas.application;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import edu.jas.kern.ComputerThreads;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * CLI tests with JUnit for GBs.
 * @author Heinz Kredel
 */

public class RunGBTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>RunGBTest</CODE> object.
     * @param name String.
     */
    public RunGBTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(RunGBTest.class);
        return suite;
    }


    @Override
    protected void setUp() {
    }


    @Override
    protected void tearDown() {
        ComputerThreads.terminate();
    }


    /**
     * Test sequential GB.
     */
    public void testSequentialGB() {
        RunGB cli = new RunGB();
        PrintStream ps = System.out;
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        PrintStream ss = new PrintStream(bs);
        try {
            System.setOut(ss);
            cli.main(new String[] { "seq", "examples/trinks7.jas", "check" });
        } finally {
            System.setOut(ps);
        }
        String sto = bs.toString();
        //System.out.println(sto);
        assertTrue("sequential", sto.contains("sequential"));
        assertTrue("G.size() = 6", sto.contains("G.size() = 6"));
        assertTrue("check isGB = true", sto.contains("check isGB = true"));
    }


    /**
     * Test parallel GB.
     */
    public void testParallelGB() {
        RunGB cli = new RunGB();
        PrintStream ps = System.out;
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        PrintStream ss = new PrintStream(bs);
        try {
            System.setOut(ss);
            cli.main(new String[] { "par", "examples/trinks7.jas", "3", "check" });
        } finally {
            System.setOut(ps);
        }
        String sto = bs.toString();
        //System.out.println(sto);
        assertTrue("parallel", sto.contains("parallel"));
        assertTrue("G.size() = 6", sto.contains("G.size() = 6"));
        assertTrue("check isGB = true", sto.contains("check isGB = true"));
    }


    /**
     * Test with "build=" GB.
     */
    public void testBuildGB() {
        RunGB cli = new RunGB();
        PrintStream ps = System.out;
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        PrintStream ss = new PrintStream(bs);
        try {
            System.setOut(ss);
            cli.main(new String[] { "build=syzygyPairlist.iterated.graded.parallel(3)",
                    "examples/trinks7.jas", "check" });
        } finally {
            System.setOut(ps);
        }
        String sto = bs.toString();
        //System.out.println(sto);
        assertTrue("iterated.graded.parallel", sto.contains("iterated.graded.parallel"));
        assertTrue("G.size() = 6", sto.contains("G.size() = 6"));
        assertTrue("check isGB = true", sto.contains("check isGB = true"));
    }

}
