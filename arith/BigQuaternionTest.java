/*
 * $Id$
 */

package edu.jas.arith;

//import edu.jas.arith.BigRational;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * BigQuaternion Test using JUnit 
 * @author Heinz Kredel.
 */

public class BigQuaternionTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>BigQuaternionTest</CODE> object.
 * @param name String
 */
   public BigQuaternionTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(BigQuaternionTest.class);
     return suite;
   }

   BigQuaternion a;
   BigQuaternion b;
   BigQuaternion c;
   BigQuaternion d;

   protected void setUp() {
       a = b = c = d = null;
   }

   protected void tearDown() {
       a = b = c = d = null;
   }


/**
 * Test static initialization and constants
 * 
 */
 public void testConstants() {
     a = BigQuaternion.ZERO;
     b = BigQuaternion.ONE;
     c = BigQuaternion.QDIF(b,b);

     assertEquals("1-1 = 0",c,a);
     assertTrue("1-1 = 0",c.isZERO());
     assertTrue("1 = 1", b.isONE() );

     a = BigQuaternion.ZERO;
     b = BigQuaternion.ONE;
     c = BigQuaternion.QDIF(b,b);

     assertEquals("1-1 = 0",c,a);
   }


/**
 * Test constructor and toString
 * 
 */
 public void testConstructor() {
     a = new BigQuaternion( "6/8" );
     b = new BigQuaternion( "3/4" );

     assertEquals("6/8 = 3/4",a,b);

     a = new BigQuaternion( "3/4 i 4/5" );
     b = new BigQuaternion( "-3/4 i -4/5" );

     assertEquals("3/4 + i 4/5 ",a,b.negate());

     String s = "6/1111111111111111111111111111111111111111111";
     a = new BigQuaternion( s );
     String t = a.toString();

     assertEquals("stringConstr = toString",s,t);

     a = new BigQuaternion( 1 );
     b = new BigQuaternion( -1 );
     c = BigQuaternion.QSUM(b,a);

     assertTrue("1 = 1", a.isONE() );
     assertEquals("1+(-1) = 0",c,BigQuaternion.ZERO);
   }


/**
 * Test random rationals
 * 
 */
 public void testRandom() {
     a = BigQuaternion.QRAND( 500 );
     b = new BigQuaternion( a.getRe(), a.getIm(), a.getJm(), a.getKm() );
     c = BigQuaternion.QDIF(b,a);

     assertEquals("a-b = 0",c,BigQuaternion.ZERO);

     d = new BigQuaternion( b.getRe(), b.getIm(), b.getJm(), b.getKm() );
     assertEquals("sign(a-a) = 0", 0, b.compareTo(d) );
 }


/**
 * Test addition
 * 
 */
 public void testAddition() {
     a = BigQuaternion.QRAND( 100 );
     b = BigQuaternion.QSUM( a, a );
     c = BigQuaternion.QDIF( b, a );

     assertEquals("a+a-a = a",c,a);
     assertEquals("a+a-a = a",0,c.compareTo(a));

     d = BigQuaternion.QSUM( a, BigQuaternion.ZERO );
     assertEquals("a+0 = a",d,a);
     d = BigQuaternion.QDIF( a, BigQuaternion.ZERO );
     assertEquals("a-0 = a",d,a);
     d = BigQuaternion.QDIF( a, a );
     assertEquals("a-a = 0",d,BigQuaternion.ZERO);

     a = BigQuaternion.QRAND( 100 );
     c = BigQuaternion.QNEG( a );
     d = BigQuaternion.QSUM( a, c );

     assertTrue("a-abs(a) = 0", d.isZERO() );
 }


/**
 * Test multiplication
 * 
 */
 public void testMultiplication() {
     a = BigQuaternion.QRAND( 100 );
     b = BigQuaternion.QPROD( a, a );
     c = BigQuaternion.QQ( b, a );

     assertEquals("a*a/a = a",c,a);
     assertEquals("a*a/a = a",0,c.compareTo(a));

     d = BigQuaternion.QPROD( a, BigQuaternion.ONE );
     assertEquals("a*1 = a",d,a);
     d = BigQuaternion.QQ( a, BigQuaternion.ONE );
     assertEquals("a/1 = a",d,a);

     a = BigQuaternion.QRAND( 100 );
     b = BigQuaternion.QINV( a );
     c = BigQuaternion.QPROD( a, b );

     assertTrue("a*1/a = 1", c.isONE() );
 }

}
