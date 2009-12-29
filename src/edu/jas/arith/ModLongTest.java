/*
 * $Id$
 */

package edu.jas.arith;

import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.kern.PrettyPrint;
import edu.jas.structure.NotInvertibleException;

import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.arith.PrimeList;


/**
 * ModLong and PrimeList tests with JUnit. 
 * @author Heinz Kredel.
 */

public class ModLongTest extends TestCase {

/**
 * main.
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>ModLongTest</CODE> object.
 * @param name String
 */
   public ModLongTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(ModLongTest.class);
     return suite;
   }

   ModLongRing zm;
   ModLongRing z1;
   ModLongRing z2;
   ModLong a;
   ModLong b;
   ModLong c;
   ModLong d;
   ModLong e;

   protected void setUp() {
       zm = z1 = z2 = null;
       a = b = c = d = e = null;
   }

   protected void tearDown() {
       zm = z1 = z2 = null;
       a = b = c = d = e = null;
   }


   protected static java.math.BigInteger getPrime1() {
       long prime = 2; //2^60-93; // 2^30-35; //19; knuth (2,390)
       for ( int i = 1; i < 30; i++ ) {
           prime *= 2;
       }
       //prime -= 93;
       prime -= 35;
       //System.out.println("p1 = " + prime);
       return new java.math.BigInteger(""+prime);
   }

   protected static java.math.BigInteger getPrime2() {
       long prime = 2; //2^60-93; // 2^30-35; //19; knuth (2,390)
       for ( int i = 1; i < 30; i++ ) {
           prime *= 2;
       }
       //prime -= 35;
       prime = 37;
       //System.out.println("p2 = " + prime);
       return new java.math.BigInteger(""+prime);
   }


/**
 * Test static initialization and constants.
 * 
 */
 public void testConstants() {
     zm = new ModLongRing(5);
     d = new ModLong(zm,11);
     a = zm.getZERO();
     b = zm.getONE();
     c = b.subtract(b);

     assertEquals("1-1 = 0",c,a);
     assertTrue("1-1 = 0",c.isZERO());
     assertTrue("1 = 1", b.isONE() );

   }


/**
 * Test constructor and toString.
 * 
 */
 public void testConstructor() {
     zm = new ModLongRing("5");
     a = new ModLong( zm, "64" );
     b = new ModLong( zm, "34" );

     assertEquals("64(5) = 34(5)",a,b);

     zm = new ModLongRing("7");
     a = new ModLong( zm, "-4" );
     b = new ModLong( zm, "3" );

     assertEquals("-4(7) = 3(7)",a,b);

     String s = "61111111111111111";
     zm = new ModLongRing("10");
     a = new ModLong( zm, s );
     String t = a.toString();

     if ( PrettyPrint.isTrue() ) {
        String st = "1";
        assertEquals("stringConstr = toString",st,t);
     } else {
        String st = "1 mod(10)";
        assertEquals("stringConstr = toString",st,t);
     }

     zm = new ModLongRing(7);
     a = new ModLong( zm, 1 );
     b = new ModLong( zm, -1 );
     c = b.sum(a);

     assertTrue("1 = 1", a.isONE() );
     assertTrue("1 = 1", b.isUnit() );
     assertEquals("1+(-1) = 0",c,zm.getZERO());

     zm = new ModLongRing(5);
     a = new ModLong( zm, 3 );
     b = new ModLong( zm, 0 );
     c = zm.parse( " 13 " );
     assertEquals("3(5) = 3(5)",a,c);

     StringReader sr = new StringReader("  13\n w ");
     c = zm.parse( sr );
     assertEquals("3(5) = 3(5)",a,c);
     //System.out.println("c = " + c);
  }


/**
 * Test random modular integers.
 * 
 */
 public void testRandom() {
     zm = new ModLongRing(19);
     a = zm.random( 500 );
     b = a.clone();
     c = b.subtract(a);

     assertEquals("a-b = 0",c,zm.getZERO());

     d = new ModLong( new ModLongRing(b.getModul()), b.getVal() );
     assertEquals("sign(a-a) = 0", 0, b.compareTo(d) );
 }


/**
 * Test addition.
 * 
 */
 public void testAddition() {
     zm = new ModLongRing(19);

     a = zm.random( 100 );
     b = a.sum( a );
     c = b.subtract( a );

     assertEquals("a+a-a = a",c,a);
     assertEquals("a+a-a = a",0,c.compareTo(a));

     d = a.sum( zm.getZERO() );
     assertEquals("a+0 = a",d,a);
     d = a.subtract( zm.getZERO() );
     assertEquals("a-0 = a",d,a);
     d = a.subtract( a );
     assertEquals("a-a = 0",d,zm.getZERO());

 }


/**
 * Test multiplication.
 * 
 */
 public void testMultiplication() {
     zm = new ModLongRing(5);
     d = new ModLong(zm,11);

     a = zm.random( 100 );
     if ( a.isZERO() ) {
         a = d;
     }
     b = a.multiply( a );
     c = b.divide( a );

     assertEquals("a*a/a = a",c,a);
     assertEquals("a*a/a = a",0,c.compareTo(a));

     d = a.multiply( zm.getONE() );
     assertEquals("a*1 = a",d,a);
     d = a.divide( zm.getONE() );
     assertEquals("a/1 = a",d,a);

     a = zm.random( 100 );
     if ( a.isZERO() ) {
         a = d;
     }
     b = a.inverse();
     c = a.multiply( b );

     assertTrue("a*1/a = 1", c.isONE() );

     try {
         a = zm.getZERO().inverse();
     } catch(NotInvertibleException expected) {
         return;
     }
     fail("0 invertible");
 }


/**
 * Test chinese remainder.
 * 
 */
 public void testChineseRemainder() {
     zm = new ModLongRing(19*13);
     a = zm.random( 9 );
     //System.out.println("a = " + a);
     z1 = new ModLongRing(19);
     b = new ModLong(z1,a.getVal());
     //System.out.println("b = " + b);
     z2 = new ModLongRing(13);
     c = new ModLong(z2,a.getVal());
     //System.out.println("c = " + c);
     d = new ModLong(z2,19);
     d = d.inverse();
     //System.out.println("d = " + d);

     e = zm.chineseRemainder(b,d,c);
     //System.out.println("e = " + e);

     assertEquals("cra(a mod 19,a mod 13) = a",a,e);


     java.math.BigInteger p1 = getPrime1();
     java.math.BigInteger p2 = getPrime2();
     java.math.BigInteger p1p2 = p1.multiply(p2);
     //System.out.println("p1p2 = " + p1p2);
     //System.out.println("prime p1 ? = " + p1.isProbablePrime(66));
     //System.out.println("prime p2 ? = " + p2.isProbablePrime(33));
     //System.out.println("prime p1p1 ? = " + p1p2.isProbablePrime(3));
     zm = new ModLongRing(p1p2);
     z1 = new ModLongRing(p1);
     z2 = new ModLongRing(p2);

     for ( int i = 0; i < 5; i++ ) {
         a = zm.random( (59+29)/2 ); //60+30 );
         //System.out.println("a = " + a);
         b = new ModLong(z1,a.getVal());
         //System.out.println("b = " + b);
         c = new ModLong(z2,a.getVal());
         //System.out.println("c = " + c);
         ModLong di = new ModLong(z2,p1);
         d = di.inverse();
         //System.out.println("d = " + d);

         e = zm.chineseRemainder(b,d,c);
         //System.out.println("e = " + e);

         assertEquals("cra(a mod p1,a mod p2) = a ",a,e);
     }
 }


/**
 * Test timing ModLong to ModInteger.
 * 
 */
 public void testTiming() {
     zm = new ModLongRing(getPrime1());
     a = zm.random( 9 );
     //System.out.println("a = " + a);
     b = zm.random( 9 );
     //System.out.println("b = " + b);
     c = zm.getONE();
     //System.out.println("c = " + c);

     ModIntegerRing ZM = new ModIntegerRing(zm.modul);
     ModInteger A = new ModInteger(ZM,a.getVal());
     ModInteger B = new ModInteger(ZM,b.getVal());
     ModInteger C = ZM.getONE();

     int run = 1000; //000;
     long t = System.currentTimeMillis();
     for ( int i = 0; i < run; i++ ) {
         if ( c.isZERO() ) {
             c = zm.getONE();
         }
         c = a.sum( b.divide(c) );
     }
     t = System.currentTimeMillis() - t;
     //System.out.println("long time = " + t);

     ModInteger D = new ModInteger(ZM,c.getVal());
     t = System.currentTimeMillis();
     for ( int i = 0; i < run; i++ ) {
         if ( C.isZERO() ) {
             C = ZM.getONE();
         }
         C = A.sum( B.divide(C) );
     }
     t = System.currentTimeMillis() - t;
     //System.out.println("BigInteger time = " + t);

     assertEquals("C == D ",C,D);
 }

}
