/*
 * $Id$
 */

package edu.jas.application;


import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Unit tests for Integer Programming.
 * @author Maximilian Nohr
 * @author Heinz Kredel
 */
public class IntegerProgramTest extends TestCase {


    /**
     * Execute all tests.
     * @param args
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
        // example1();
        // example2();
        // example3();
        // example4();
        // example5();
        // example6();
        // example7();
        // example8();
    }


    /**
     * Constructs a <CODE>IntegerProgramTest</CODE> object.
     * @param name String.
     */
    public IntegerProgramTest(String name) {
        super(name);
    }


    /**
     * suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(IntegerProgramTest.class);
        return suite;
    }


    @Override
    protected void setUp() {
    }


    @Override
    protected void tearDown() {
    }


    /**
     * Example p.360 CLOII
     */
    public void testExample1() {
        IntegerProgram IP = new IntegerProgram();

        long t0 = System.currentTimeMillis();

        //bsp. s.360 CLOII
        long[][] A0 = { { 4, 5, 1, 0 }, { 2, 3, 0, 1 } };
        long[] B0 = { 37, 20 };
        long[] C0 = { -11, -15, 0, 0 };

        long[] sol = IP.solve(A0, B0, C0);

        long t1 = System.currentTimeMillis();
        long t = t1 - t0;
        // System.out.println("\n" + IP);
        // System.out.println("The solution is: " + Arrays.toString(sol));
        // System.out.println("The computation needed " + t + " milliseconds.");
        assertTrue("IP.getSuccess(): " + t, IP.getSuccess());

        long[] BW = { 1, 2 }; //,3};

        sol = IP.solve(BW);

        int count = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                B0[0] = i;
                B0[1] = j;

                sol = IP.solve(A0, B0, C0);
                if (IP.getSuccess()) {
                    count++;
                }
            }
        }
        assertTrue("#IP.getSuccess(): " + count, count > 0);
        //System.out.println(count + " times successful!");
    }


    /**
     * Example p.374 CLOII 10a
     */
    public void testExample2() {
        IntegerProgram IP = new IntegerProgram();

        long t0 = System.currentTimeMillis();

        //bsp. s.374 CLOII 10a
        long[][] A = { { 3, 2, 1, 1 }, { 4, 1, 1, 0 } };
        long[] B = { 10, 5 };
        long[] C = { 2, 3, 1, 5 };

        long[] sol = IP.solve(A, B, C);

        //10b
        long[] Bb = { 20, 14 };
        sol = IP.solve(Bb);

        long t1 = System.currentTimeMillis();
        long t = t1 - t0;
        // System.out.println("\n" + IP);
        // System.out.println("The solution is: " + Arrays.toString(sol));
        // System.out.println("The computation needed " + t + " milliseconds.");
        assertTrue("IP.getSuccess(): " + t, IP.getSuccess());
    }


    /**
     * Example p.372 CLOII
     */
    public void testExample3() {
        IntegerProgram IP = new IntegerProgram();

        long t0 = System.currentTimeMillis();

        //bsp. s.372 CLOII
        long[][] A2 = { { 3, -2, 1, 0 }, { 4, 1, -1, -1 } };
        long[] B2 = { -1, 5 };
        long[] C2 = { 1, 1000, 1, 100 };

        long[] sol = IP.solve(A2, B2, C2);

        long t1 = System.currentTimeMillis();
        long t = t1 - t0;
        // System.out.println("\n" + IP);
        // System.out.println("The solution is: " + Arrays.toString(sol));
        // System.out.println("The computation needed " + t + " milliseconds.");
        assertTrue("IP.getSuccess(): " + t, IP.getSuccess());
    }


    public void testExample4() {
        IntegerProgram IP = new IntegerProgram();

        long t0 = System.currentTimeMillis();

        //bsp. s.374 10c
        long[][] A3 = { { 3, 2, 1, 1, 0, 0 }, { 1, 2, 3, 0, 1, 0 }, { 2, 1, 1, 0, 0, 1 } };
        long[] B3 = { 45, 21, 18 };
        long[] C3 = { -3, -4, -2, 0, 0, 0 };

        long[] sol = IP.solve(A3, B3, C3);

        long t1 = System.currentTimeMillis();
        long t = t1 - t0;
        // System.out.println("\n" + IP);
        // System.out.println("The solution is: " + Arrays.toString(sol));
        // System.out.println("The computation needed " + t + " milliseconds.");
        assertTrue("IP.getSuccess(): " + t, IP.getSuccess());
    }


    /**
     * Example p.138 AAECC-9
     */
    public void testExample5() {
        IntegerProgram IP = new IntegerProgram();

        long t0 = System.currentTimeMillis();

        //bsp. s.138 AAECC-9
        long[][] A4 = { { 32, 45, -41, 22 }, { -82, -13, 33, -33 }, { 23, -21, 12, -12 } };
        long[] B4 = { 214, 712, 331 }; //im Beispiel keine b genannt
        long[] C4 = { 1, 1, 1, 1 };

        long[] sol = IP.solve(A4, B4, C4);

        long t1 = System.currentTimeMillis();
        long t = t1 - t0;
        // System.out.println("\n" + IP);
        // System.out.println("The solution is: " + Arrays.toString(sol));
        // System.out.println("The computation needed " + t + " milliseconds.");
        assertTrue("IP.getSuccess(): " + t, IP.getSuccess());
    }


    /**
     * Example from M. Nohr
     */
    public void testExample6() {
        IntegerProgram IP = new IntegerProgram();

        long t0 = System.currentTimeMillis();

        //eigenes beispiel
        //System.out.println("example from mnohr:");
        long[][] A5 = { { 4, 3, 1, 0 }, { 3, 1, 0, 1 } };
        long[] B5 = { 200, 100 };
        long[] C5 = { -5, -4, 0, 0 };

        long[] sol = IP.solve(A5, B5, C5);

        long t1 = System.currentTimeMillis();
        long t = t1 - t0;
        // System.out.println("\n" + IP);
        // System.out.println("The solution is: " + Arrays.toString(sol));
        // System.out.println("The computation needed " + t + " milliseconds.");
        assertTrue("IP.getSuccess(): " + t, IP.getSuccess());
    }


    /**
     * Example unsolvable
     */
    public void testExample7() {
        IntegerProgram IP = new IntegerProgram();

        long t0 = System.currentTimeMillis();

        long[][] A9 = { { 1, 1, 1, 1 }, { -1, -1, -1, -1 } };
        long[] B9 = { 1, 1 };
        long[] C9 = { 1, 1, 0, 0 };

        long[] sol = IP.solve(A9, B9, C9);

        long t1 = System.currentTimeMillis();
        long t = t1 - t0;
        // System.out.println("\nunsolvable: " + IP);
        // System.out.println("The solution is: " + Arrays.toString(sol));
        // System.out.println("The computation needed " + t + " milliseconds.");
        assertFalse("IP.getSuccess(): " + t, IP.getSuccess());
    }


    /**
     * Example ?
     */
    public void testExample8() {
        IntegerProgram IP = new IntegerProgram();

        long t0 = System.currentTimeMillis();

        long[][] A8 = { { 4, 3, 1, 0 }, { 3, 1, 0, 1 } };
        long[] B8 = { 200, 100 };
        long[] C8 = { -5, -4, 0, 0 };

        long[] sol = IP.solve(A8, B8, C8);

        long t1 = System.currentTimeMillis();
        long t = t1 - t0;
        // System.out.println("\n" + IP);
        // System.out.println("The solution is: " + Arrays.toString(sol));
        // System.out.println("The computation needed " + t + " milliseconds.");
        assertTrue("IP.getSuccess(): " + t, IP.getSuccess());
    }

}
