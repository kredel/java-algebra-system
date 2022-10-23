/*
 * $Id$
 */

package edu.jas.kern;


import java.util.concurrent.Callable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;



/**
 * LocalTimeStatus tests with JUnit.
 * @author Heinz Kredel
 */
public class LocalTimeStatusTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>LocalTimeStatusTest</CODE> object.
     * @param name String.
     */
    public LocalTimeStatusTest(String name) {
        super(name);
    }


    /*
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(LocalTimeStatusTest.class);
        return suite;
    }


    LocalTimeStatus lt1, lt2;


    @Override
    protected void setUp() {
        lt1 = new LocalTimeStatus();
        lt2 = new LocalTimeStatus(true, 0, true);
    }


    @Override
    protected void tearDown() {
        lt1.setNotActive();
        lt1.setLimit(Long.MAX_VALUE);
        lt1.setCallBack((Callable<Boolean>) null);
        lt2 = null;
    }


    /**
     * Tests checkTime.
     */
    public void testCheckTime() {
        //System.out.println("lt1 = " + lt1);
        //System.out.println("lt2 = " + lt2);
        lt1.setActive();
        assertTrue("is active lt1", lt1.isActive());
        assertTrue("is active lt2", lt2.isActive());
        lt1.restart();
        try {
            lt1.checkTime("test1");
            lt2.checkTime("test1-2");
            // succeed
        } catch (TimeExceededException e) {
            fail("test1 " + e);
        }

        lt1.setLimit(0L);
        assertTrue("is active lt1", lt1.isActive());
        try {
            Thread.sleep(10);
            lt1.checkTime("test2");
            lt2.checkTime("test2-2");
            fail("test2 checkTime 1|2");
        } catch (TimeExceededException e) {
            // succeed
        } catch (InterruptedException e) {
            fail("test2 interrupt");
        }
        //System.out.println("lt1 = " + lt1);
        //System.out.println("lt2 = " + lt2);
    }


    /**
     * Tests call back.
     */
    public void testCallBack() {
        //System.out.println("lt1 = " + lt1);
        //System.out.println("lt2 = " + lt2);
        lt1.setActive();
        lt1.restart();
        lt1.setLimit(0L);
        lt1.setCallBack(new LocalTimeStatus.TSCall(true));
        assertTrue("is active lt1", lt1.isActive());
        assertTrue("is active lt2", lt2.isActive());

        try {
            Thread.sleep(10);
            lt1.checkTime("test3");
            lt2.checkTime("test3-2");
            // succeed
        } catch (TimeExceededException e) {
            fail("test3 checkTime");
        } catch (InterruptedException e) {
            fail("test3 interrupt");
        }

        lt1.setCallBack(new LocalTimeStatus.TSCall(false));
        try {
            Thread.sleep(10);
            lt1.checkTime("test4");
            fail("test4 checkTime");
        } catch (TimeExceededException e) {
            // succeed
        } catch (InterruptedException e) {
            fail("test4 interrupt");
        }

        lt2.setCallBack(new LocalTimeStatus.TSCall(false));
        try {
            Thread.sleep(10);
            lt2.checkTime("test4-2");
            fail("test4 checkTime");
        } catch (TimeExceededException e) {
            // succeed
        } catch (InterruptedException e) {
            fail("test4 interrupt");
        }
        //System.out.println("lt1 = " + lt1);
        //System.out.println("lt2 = " + lt2);
    }

}
