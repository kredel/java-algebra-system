/*
 * $Id$
 */

package edu.jas.kern;


import java.util.concurrent.Callable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;



/**
 * TimeStatus tests with JUnit.
 * @author Heinz Kredel
 */
public class TimeStatusTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>TimeStatusTest</CODE> object.
     * @param name String.
     */
    public TimeStatusTest(String name) {
        super(name);
    }


    /*
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(TimeStatusTest.class);
        return suite;
    }


    @Override
    protected void setUp() {
    }


    @Override
    protected void tearDown() {
        TimeStatus.setNotActive();
        TimeStatus.setLimit(Long.MAX_VALUE);
        TimeStatus.setCallBack((Callable<Boolean>) null);
    }


    /**
     * Tests checkTime.
     */
    public void testCheckTime() {
        TimeStatus.setActive();
        assertTrue("is active ", TimeStatus.isActive());
        TimeStatus.restart();
        try {
            TimeStatus.checkTime("test1");
            // succeed
        } catch (TimeExceededException e) {
            fail("test1 " + e);
        }

        TimeStatus.setLimit(0L);
        assertTrue("is active ", TimeStatus.isActive());
        try {
            Thread.sleep(10);
            TimeStatus.checkTime("test2");
            fail("test2 checkTime");
        } catch (TimeExceededException e) {
            // succeed
        } catch (InterruptedException e) {
            fail("test2 interrupt");
        }
    }


    /**
     * Tests call back.
     */
    public void testCallBack() {
        TimeStatus.setActive();
        TimeStatus.restart();
        TimeStatus.setLimit(0L);
        TimeStatus.setCallBack(new TimeStatus.TSCall(true));
        assertTrue("is active ", TimeStatus.isActive());

        try {
            Thread.sleep(10);
            TimeStatus.checkTime("test3");
            // succeed
        } catch (TimeExceededException e) {
            fail("test3 checkTime");
        } catch (InterruptedException e) {
            fail("test3 interrupt");
        }

        TimeStatus.setCallBack(new TimeStatus.TSCall(false));
        try {
            Thread.sleep(10);
            TimeStatus.checkTime("test4");
            fail("test4 checkTime");
        } catch (TimeExceededException e) {
            // succeed
        } catch (InterruptedException e) {
            fail("test4 interrupt");
        }
    }

}
