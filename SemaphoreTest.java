/*
 * $Id$
 */

//package edu.unima.ky.parallel;
package edu.jas;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

/**
 * Semaphore Test using JUnit 
 * @author Akitoshi Yoshida
 * @author Heinz Kredel.
 */

public class SemaphoreTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>SemaphoreTest</CODE> object.
 * @param name String
 */
   public SemaphoreTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(SemaphoreTest.class);
     return suite;
   }

   private Semaphore s1;


   protected void setUp() {
   }

   protected void tearDown() {
   }


/**
 * Tests if the created Semaphore object is positive, then calls
 * the method V().
 */
 public void testSemaphore1() {
     s1 = new Semaphore(0);
     assertEquals(s1.isPositive(),false);
     s1.V();
     assertEquals(s1.isPositive(),true);
   }

/**
 * Tests if the created Semaphore object is positive, then calls
 * the method P().
 */
   public void testSemaphore2() {
     s1 = new Semaphore(1);
     assertEquals(s1.isPositive(),true);
     try {
           s1.P();
     } catch(Exception e) {
       fail("Exception"+e);
     }
     assertEquals(s1.isPositive(),false);
   }

/**
 * Tests if the created Semaphore object is positive, then 
 */
   public void testSemaphore3() {
     s1 = new Semaphore(1);
     assertEquals(s1.isPositive(),true);
     try {
           s1.P();
     } catch(Exception e) {
       fail("Exception"+e);
     }
     assertEquals(s1.isPositive(),false);
     s1.V();
     assertEquals(s1.isPositive(),true);
   }

   public void testSemaphore4() {
     int n = 10;
     s1 = new Semaphore(n);
     assertEquals(s1.isPositive(),true);
     try {
         for (int i = 0; i < n; i++) s1.P();
     } catch(Exception e) {
       fail("Exception"+e);
     }
     assertEquals(s1.isPositive(),false);
         for (int i = 0; i < n; i++) s1.V();
     assertEquals(s1.isPositive(),true);
   }

   public void testSemaphore5() {
     int n = 1000;
     s1 = new Semaphore(n);
     assertEquals(s1.isPositive(),true);
     try {
         for (int i = 0; i < n; i++) s1.P();
     } catch(Exception e) {
       fail("Exception"+e);
     }
     assertEquals(s1.isPositive(),false);
         for (int i = 0; i < n; i++) s1.V();
     assertEquals(s1.isPositive(),true);
   }

   public void testSemaphore6() {
     int n = 10;
     s1 = new Semaphore(0);
     assertEquals(s1.isPositive(),false);
     try {
           assertEquals(s1.P(100),false);
     } catch(Exception e) {
       fail("Exception"+e);
     }
     assertEquals(s1.isPositive(),false);
   }

   public void testSemaphore7() {
     int n = 10;
     s1 = new Semaphore(n);
     assertEquals(s1.isPositive(),true);
     for (int i=0; i < n; i++) {
         try {
               assertEquals(s1.P(100),true);
         } catch(Exception e) {
           fail("Exception"+e);
         }
     }
     assertEquals(s1.isPositive(),false);
     try {
           assertEquals(s1.P(100),false);
     } catch(Exception e) {
       fail("Exception"+e);
     }
   }

}
