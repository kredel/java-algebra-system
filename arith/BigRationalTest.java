/*
 * $Id$
 */

package edu.jas.arith;

//import edu.jas.arith.BigRational;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * BigRational Test using JUnit 
 * @author Heinz Kredel.
 */

public class BigRationalTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>BigRationalTest</CODE> object.
 * @param name String
 */
   public BigRationalTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(BigRationalTest.class);
     return suite;
   }

   BigRational a;
   BigRational b;
   BigRational c;
   BigRational d;

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
     a = BigRational.ZERO;
     b = BigRational.ONE;
     c = BigRational.RNDIF(b,b);

     assertEquals("1-1 = 0",c,a);
     assertTrue("1-1 = 0",c.isZERO());
     assertTrue("1 = 1", b.isONE() );

     a = BigRational.ZERO;
     b = BigRational.ONE;
     c = BigRational.RNDIF(b,b);

     assertEquals("1-1 = 0",c,a);
   }


/**
 * Test constructor and toString
 * 
 */
 public void testConstructor() {
     a = new BigRational( "6/8" );
     b = new BigRational( "3/4" );

     assertEquals("6/8 = 3/4",a,b);

     a = new BigRational( "3/-4" );
     b = new BigRational( "-3/4" );

     assertEquals("3/-4 = -3/4",a,b);

     String s = "6/1111111111111111111111111111111111111111111";
     a = new BigRational( s );
     String t = a.toString();

     assertEquals("stringConstr = toString",s,t);

     a = new BigRational( 1 );
     b = new BigRational( -1 );
     c = BigRational.RNSUM(b,a);

     assertTrue("1 = 1", a.isONE() );
     assertEquals("1+(-1) = 0",c,BigRational.ZERO);
   }


/**
 * Test random rationals
 * 
 */
 public void testRandom() {
     a = BigRational.RNRAND( 500 );
     b = new BigRational( "" + a );
     c = BigRational.RNDIF(b,a);

     assertEquals("a-b = 0",c,BigRational.ZERO);

     d = new BigRational( b.numerator(), b.denominator() );
     assertEquals("sign(a-a) = 0", 0, b.compareTo(d) );
 }


/**
 * Test addition
 * 
 */
 public void testAddition() {
     a = BigRational.RNRAND( 100 );
     b = BigRational.RNSUM( a, a );
     c = BigRational.RNDIF( b, a );

     assertEquals("a+a-a = a",c,a);
     assertEquals("a+a-a = a",0,BigRational.RNCOMP(c,a));

     d = BigRational.RNSUM( a, BigRational.ZERO );
     assertEquals("a+0 = a",d,a);
     d = BigRational.RNDIF( a, BigRational.ZERO );
     assertEquals("a-0 = a",d,a);
     d = BigRational.RNDIF( a, a );
     assertEquals("a-a = 0",d,BigRational.ZERO);

     a = BigRational.RNRAND( 100 );
     b = BigRational.RNABS( a );
     c = BigRational.RNNEG( b );
     d = BigRational.RNSUM( a, c );

     assertTrue("a-abs(a) = 0", d.isZERO() );
 }


/**
 * Test multiplication
 * 
 */
 public void testMultiplication() {
     a = BigRational.RNRAND( 100 );
     b = BigRational.RNPROD( a, a );
     c = BigRational.RNQ( b, a );

     assertEquals("a*a/a = a",c,a);
     assertEquals("a*a/a = a",0,BigRational.RNCOMP(c,a));

     d = BigRational.RNPROD( a, BigRational.ONE );
     assertEquals("a*1 = a",d,a);
     d = BigRational.RNQ( a, BigRational.ONE );
     assertEquals("a/1 = a",d,a);

     a = BigRational.RNRAND( 100 );
     b = BigRational.RNINV( a );
     c = BigRational.RNPROD( a, b );

     assertTrue("a*1/a = 1", c.isONE() );
 }

}
