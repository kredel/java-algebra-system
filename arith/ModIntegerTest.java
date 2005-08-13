/*
 * $Id$
 */

package edu.jas.arith;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.structure.PrettyPrint;


/**
 * ModInteger Test using JUnit 
 * @author Heinz Kredel.
 */

public class ModIntegerTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>ModIntegerTest</CODE> object.
 * @param name String
 */
   public ModIntegerTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(ModIntegerTest.class);
     return suite;
   }

   ModInteger a;
   ModInteger b;
   ModInteger c;
   ModInteger d;

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
     d = new ModInteger(5,11);
     a = d.getZERO();
     b = d.getONE();
     c = ModInteger.MIDIF(b,b);

     assertEquals("1-1 = 0",c,a);
     assertTrue("1-1 = 0",c.isZERO());
     assertTrue("1 = 1", b.isONE() );

   }


/**
 * Test constructor and toString
 * 
 */
 public void testConstructor() {
     a = new ModInteger( "5", "64" );
     b = new ModInteger( "5", "34" );

     assertEquals("64(5) = 34(5)",a,b);

     a = new ModInteger( "7", "-4" );
     b = new ModInteger( "7", "3" );

     assertEquals("-4(7) = 3(7)",a,b);

     String s = "61111111111111111111111111111111111111111111";
     a = new ModInteger( "10", s );
     String t = a.toString();

     if ( PrettyPrint.isTrue() ) {
        String st = "1";
        assertEquals("stringConstr = toString",st,t);
     } else {
        String st = "1 mod(10)";
        assertEquals("stringConstr = toString",st,t);
     }

     a = new ModInteger( 7, 1 );
     b = new ModInteger( 7, -1 );
     c = ModInteger.MISUM(b,a);

     assertTrue("1 = 1", a.isONE() );
     assertTrue("1 = 1", b.isUnit() );
     assertEquals("1+(-1) = 0",c,a.getZERO());
   }


/**
 * Test random rationals
 * 
 */
 public void testRandom() {
     d = new ModInteger(19,11);
     a = d.random( 500 );
     b = a.clone();
     c = ModInteger.MIDIF(b,a);

     assertEquals("a-b = 0",c,d.getZERO());

     d = new ModInteger( b.getModul(), b.getVal() );
     assertEquals("sign(a-a) = 0", 0, b.compareTo(d) );
 }


/**
 * Test addition
 * 
 */
 public void testAddition() {
     d = new ModInteger(19,11);

     a = d.random( 100 );
     b = ModInteger.MISUM( a, a );
     c = ModInteger.MIDIF( b, a );

     assertEquals("a+a-a = a",c,a);
     assertEquals("a+a-a = a",0,ModInteger.MICOMP(c,a));

     d = ModInteger.MISUM( a, d.getZERO() );
     assertEquals("a+0 = a",d,a);
     d = ModInteger.MIDIF( a, d.getZERO() );
     assertEquals("a-0 = a",d,a);
     d = ModInteger.MIDIF( a, a );
     assertEquals("a-a = 0",d,d.getZERO());

 }


/**
 * Test multiplication
 * 
 */
 public void testMultiplication() {
     d = new ModInteger(5,11);
     //System.out.println("a = " + a);

     a = d.random( 100 );
     if ( a.isZERO() ) {
         a = d;
     }
     b = ModInteger.MIPROD( a, a );
     c = ModInteger.MIQ( b, a );

     assertEquals("a*a/a = a",c,a);
     assertEquals("a*a/a = a",0,c.compareTo(a));

     d = ModInteger.MIPROD( a, d.getONE() );
     assertEquals("a*1 = a",d,a);
     d = ModInteger.MIQ( a, d.getONE() );
     assertEquals("a/1 = a",d,a);

     a = d.random( 100 );
     if ( a.isZERO() ) {
         a = d;
     }
     b = ModInteger.MIINV( a );
     c = ModInteger.MIPROD( a, b );

     assertTrue("a*1/a = 1", c.isONE() );

     try {
         a = d.getZERO().inverse();
     } catch(ArithmeticException expected) {
         return;
     }
     fail("0 invertible");

 }

}
