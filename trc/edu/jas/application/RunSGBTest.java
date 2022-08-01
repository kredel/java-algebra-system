/*
 * $Id$
 */

package edu.jas.application;


import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

import edu.jas.kern.ComputerThreads;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * CLI tests with JUnit for GBs.
 * @author Heinz Kredel
 */

public class RunSGBTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>RunSGBTest</CODE> object.
     * @param name String.
     */
    public RunSGBTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(RunSGBTest.class);
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
        RunSGB cli = new RunSGB();
        PrintStream ps = System.out;
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        PrintStream ss = new PrintStream(bs);
        try {
            System.setOut(ss);
            cli.main(new String[]{ "seq", "left", "examples/wa_32.jas", "check"} );
        } finally {
            System.setOut(ps);
        }
        String sto = bs.toString();
        //System.out.println(sto);
        assertTrue("sequential", sto.contains("sequential"));
        assertTrue("G.size() = 5", sto.contains("G.size() = 5"));
        assertTrue("check isGB = true", sto.contains("check isGB = true"));
    }


    /**
     * Test sequential two-sided GB.
     */
    public void testSequential2tsGB() {
        RunSGB cli = new RunSGB();
        PrintStream ps = System.out;
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        PrintStream ss = new PrintStream(bs);
        try {
            System.setOut(ss);
            cli.main(new String[]{ "seq", "two", "examples/wa_32.jas", "check"} );
        } finally {
            System.setOut(ps);
        }
        String sto = bs.toString();
        //System.out.println(sto);
        assertTrue("sequential", sto.contains("sequential"));
        assertTrue("two", sto.contains("two"));
        assertTrue("G.size() = 5", sto.contains("G.size() = 5"));
        assertTrue("check isGB = true", sto.contains("check isGB = true"));
    }


    /**
     * Test parallel left GB.
     */
    public void testParallelLeftGB() {
        RunSGB cli = new RunSGB();
        PrintStream ps = System.out;
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        PrintStream ss = new PrintStream(bs);
        try {
            System.setOut(ss);
            cli.main(new String[]{ "par", "left", "examples/wa_1.jas", "3", "check"} );
        } finally {
            System.setOut(ps);
        }
        String sto = bs.toString();
        //System.out.println(sto);
        assertTrue("parallel", sto.contains("parallel"));
        assertTrue("left", sto.contains("left"));
        assertTrue("G.size() = 8", sto.contains("G.size() = 8"));
        assertTrue("check isGB = true", sto.contains("check isGB = true"));
    }

}
