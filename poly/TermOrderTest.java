/*
 * $Id$
 */

package edu.jas.poly;

//import edu.jas.poly.TermOrder;

import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

/**
 * TermOrder Test using JUnit 
 * @author Heinz Kredel.
 */

public class TermOrderTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>TermOrderTest</CODE> object.
 * @param name String
 */
   public TermOrderTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(TermOrderTest.class);
     return suite;
   }

   private final static int bitlen = 100;

   ExpVector a;
   ExpVector b;
   ExpVector c;
   ExpVector d;

   TermOrder t;
   TermOrder s;


   protected void setUp() {
       a = b = c = d = null;
       t = s = null;
   }

   protected void tearDown() {
       a = b = c = d = null;
       t = s = null;
   }


/**
 * Test constructor and toString
 * 
 */
 public void testConstructor() {

     s = new TermOrder();
     t = new TermOrder();
     assertEquals("t = s",t,s);

     String x = t.toString();
     String y = s.toString();

     assertEquals("x = y",x,y);

     t = new TermOrder(TermOrder.INVLEX);
     x = "INVLEX";
     boolean z = t.toString().startsWith(x);
     assertTrue("INVLEX(.)",z);

     s = new TermOrder(TermOrder.IGRLEX);
     t = new TermOrder(TermOrder.IGRLEX);
     assertEquals("t = s",t,s);
   }


/**
 * Test compare
 * 
 */
 public void testCompare() {
     float q = (float) 0.9;

     a = ExpVector.EVRAND(5,10,q);
     b = ExpVector.EVRAND(5,10,q);

     c = ExpVector.EVSUM(a,b);

     t = new TermOrder();

     int x = ExpVector.EVCOMP(t.getEvord(),c,a);
     int y = ExpVector.EVCOMP(t.getEvord(),c,b);

     assertEquals("x = 1",1,x);
     assertEquals("y = 1",1,y);

     x = ExpVector.EVCOMP(t.getEvord(),a,c);
     y = ExpVector.EVCOMP(t.getEvord(),b,c);

     assertEquals("x = -1",-1,x);
     assertEquals("y = -1",-1,y);

     x = ExpVector.EVCOMP(t.getEvord(),a,a);
     y = ExpVector.EVCOMP(t.getEvord(),b,b);

     assertEquals("x = 0",0,x);
     assertEquals("y = 0",0,y);
   }


/**
 * Test comparators
 * 
 */
 public void testAscendComparator() {
     float q = (float) 0.9;

     a = ExpVector.EVRAND(5,10,q);
     b = ExpVector.EVRAND(5,10,q);

     c = ExpVector.EVSUM(a,b);

     t = new TermOrder();

     int x = t.getAscendComparator().compare(c,a);
     int y = t.getAscendComparator().compare(c,b);

     assertEquals("x = 1",1,x);
     assertEquals("y = 1",1,y);

     x = t.getAscendComparator().compare(a,c);
     y = t.getAscendComparator().compare(b,c);

     assertEquals("x = -1",-1,x);
     assertEquals("y = -1",-1,y);

     x = t.getAscendComparator().compare(a,a);
     y = t.getAscendComparator().compare(b,b);

     assertEquals("x = 0",0,x);
     assertEquals("y = 0",0,y);
   }


/**
 * Test comparators
 * 
 */
 public void testDescendComparator() {
     float q = (float) 0.9;

     a = ExpVector.EVRAND(5,10,q);
     b = ExpVector.EVRAND(5,10,q);

     c = ExpVector.EVSUM(a,b);

     t = new TermOrder();

     int x = t.getDescendComparator().compare(c,a);
     int y = t.getDescendComparator().compare(c,b);

     assertEquals("x = -1",-1,x);
     assertEquals("y = -1",-1,y);

     x = t.getDescendComparator().compare(a,c);
     y = t.getDescendComparator().compare(b,c);

     assertEquals("x = 1",1,x);
     assertEquals("y = 1",1,y);

     x = t.getDescendComparator().compare(a,a);
     y = t.getDescendComparator().compare(b,b);

     assertEquals("x = 0",0,x);
     assertEquals("y = 0",0,y);
   }


/**
 * Test exception
 * 
 */
 public void testException() {
     float q = (float) 0.9;

     a = ExpVector.EVRAND(5,10,q);
     b = ExpVector.EVRAND(5,10,q);

     int wrong = 99;
     t = new TermOrder(wrong);
     int x = 0;

     try {
         x = t.getDescendComparator().compare(a,b);
     } catch (IllegalArgumentException e) {
         return;
     }
     fail("IllegalArgumentException");
   }


}
