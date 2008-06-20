/*
 * $Id$
 */

package edu.mas.kern;


import static edu.mas.kern.LIST.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Basic list processing tests with JUnit.
 * @author Heinz Kredel.
 */

public class LISTTest extends TestCase {

/**
 * main.
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>LISTTest</CODE> object.
 * @param name String.
 */
   public LISTTest(String name) {
          super(name);
   }

/**
 * suite.
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(LISTTest.class);
     return suite;
   }

   protected void setUp() {
       //a = b = c = d = e = null;
   }

   protected void tearDown() {
       //a = b = c = d = e = null;
   }


/**
 * Test static initialization LIST.
 */
 public void testLISTinit() {
     LIST<Object> a = null;
     assertTrue("a == () ", isEmpty(a) );
     assertEquals("len(a) == 0 ", LENGTH(a), 0 );
     a = new LIST<Object>();
     assertTrue("a == () ", isEmpty(a) );
     assertEquals("len(a) == 0 ", LENGTH(a), 0 );
     //System.out.println("a = " + a);
     a = new LIST<Object>( a.list );
     assertTrue("a == () ", isEmpty(a) );
     assertEquals("len(a) == 0 ", LENGTH(a), 0 );
 }


/**
 * Test static LIST creation.
 */
 public void testLISTcreate() {
     Object five = 5;
     LIST<Object> a = null;
     assertTrue("a == () ", isEmpty(a) );
     assertEquals("len(a) == 0 ", LENGTH(a), 0 );
     //System.out.println("a = " + a);
     a = LIST1( five );
     assertFalse("a != () ", isEmpty(a) );
     assertEquals("len(a) == 1 ", LENGTH(a), 1 );
     //System.out.println("a = " + a);
     a = COMP( five, a );
     assertFalse("a != () ", isEmpty(a) );
     assertEquals("len(a) == 2 ", LENGTH(a), 2 );
     //System.out.println("a = " + a);

     LIST<Object> b = LIST2(five,five);
     assertFalse("b != () ", isEmpty(b) );
     assertEquals("len(a) == 2 ", LENGTH(a), 2 );
     assertTrue("a == b ", EQUAL(a,b) );
 }


/**
 * Test static LIST operations.
 */
 public void testLISToper() {
     Object five = 5;
     LIST<Object> a = LIST1( five );
     assertFalse("a != () ", isEmpty(a) );
     assertEquals("len(a) == 1 ", LENGTH(a), 1 );
     a = COMP( five, a );
     assertFalse("a != () ", isEmpty(a) );
     assertEquals("len(a) == 2 ", LENGTH(a), 2 );

     LIST<Object> b = CINV(a);
     assertFalse("b != () ", isEmpty(b) );
     assertEquals("len(a) == 2 ", LENGTH(a), 2 );
     assertTrue("a == b ", EQUAL(a,b) );

     LIST<Object> c = INV(a);
     assertFalse("c != () ", isEmpty(c) );
     assertEquals("len(c) == 2 ", LENGTH(c), 2 );
     assertTrue("a == c ", EQUAL(a,c) );
     //System.out.println("a = " + a);
     //System.out.println("c = " + c);
 }


/**
 * Test static LIST many elements.
 */
 public void testLISTelems() {
     int max = 100;
     Object n;
     LIST<Object> a = null;
     for ( int i = 0; i < max; i++ ) {
         n = i;
         a = COMP( n, a );
     }
     assertFalse("a != () ", isEmpty(a) );
     assertEquals("len(a) == "+max+" ", LENGTH(a), max );
     //System.out.println("a = " + a);

     LIST<Object> b = CINV(a);
     assertFalse("b != () ", isEmpty(b) );
     assertEquals("len(b) == "+max+" ", LENGTH(b), max );
     //System.out.println("b = " + b);

     b = INV( b );
     assertFalse("b != () ", isEmpty(b) );
     assertEquals("len(b) == "+max+" ", LENGTH(b), max );
     //System.out.println("b = " + b);
     assertTrue("a == INV(CINV(a)) ", EQUAL(a,b) );
 }


/**
 * Test static LIST destructive operations.
 */
 public void testLISTdestruct() {
     Object n = 5; 
     LIST<Object> a = LIST1( n );
     LIST<Object> b = LIST1( n );
     assertEquals("len(a) == 1 ", LENGTH(a), 1 );
     assertEquals("len(b) == 1 ", LENGTH(b), 1 );
     SRED(a,b);
     assertFalse("a != () ", isEmpty(a) );
     assertFalse("b != () ", isEmpty(b) );
     assertEquals("len(b) == 1 ", LENGTH(b), 1 );
     assertEquals("len(a) == 2 ", LENGTH(a), 2 );
     //System.out.println("a = " + a);

     n = 7;
     SFIRST(a,n);
     //System.out.println("a = " + a);
     assertEquals("len(a) == 2 ", LENGTH(a), 2 );
     LIST<Object> c = COMP( n, b );
     assertEquals("len(c) == 2 ", LENGTH(c), 2 );
     assertTrue("a == c ", EQUAL(a,c) );
 }

}