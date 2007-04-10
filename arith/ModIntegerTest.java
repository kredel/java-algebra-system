/*
 * $Id$
 */

package edu.jas.arith;

import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.structure.PrettyPrint;
import edu.jas.arith.ModInteger;
import edu.jas.arith.PrimeList;


/**
 * ModInteger Test using JUnit. 
 * @author Heinz Kredel.
 */

public class ModIntegerTest extends TestCase {

/**
 * main.
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
   ModInteger e;

   protected void setUp() {
       a = b = c = d = e = null;
   }

   protected void tearDown() {
       a = b = c = d = e = null;
   }


   protected static java.math.BigInteger getPrime1() {
       long prime = 2; //2^60-93; // 2^30-35; //19; knuth (2,390)
       for ( int i = 1; i < 60; i++ ) {
           prime *= 2;
       }
       prime -= 93;
       //prime = 37;
       //System.out.println("p1 = " + prime);
       return new java.math.BigInteger(""+prime);
   }

   protected static java.math.BigInteger getPrime2() {
       long prime = 2; //2^60-93; // 2^30-35; //19; knuth (2,390)
       for ( int i = 1; i < 30; i++ ) {
           prime *= 2;
       }
       prime -= 35;
       //prime = 19;
       //System.out.println("p2 = " + prime);
       return new java.math.BigInteger(""+prime);
   }


/**
 * Test static initialization and constants.
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
 * Test constructor and toString.
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

     a = new ModInteger( 5, 3 );
     b = new ModInteger( 5, 0 );
     c = b.parse( " 13 " );
     assertEquals("3(5) = 3(5)",a,c);

     StringReader sr = new StringReader("  13\n w ");
     c = b.parse( sr );
     assertEquals("3(5) = 3(5)",a,c);
     //System.out.println("c = " + c);
  }


/**
 * Test random modular integers.
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
 * Test addition.
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
 * Test multiplication.
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


/**
 * Test chinese remainder.
 * 
 */
 public void testChineseRemainder() {
     a = new ModInteger(19*13,1);
     a = a.random( 9 );
     //System.out.println("a = " + a);
     b = new ModInteger(19,a.getVal().longValue());
     //System.out.println("b = " + b);
     c = new ModInteger(13,a.getVal().longValue());
     //System.out.println("c = " + c);
     d = new ModInteger(13,19);
     d = d.inverse();
     //System.out.println("d = " + d);

     e = a.chineseRemainder(b,d,c);
     //System.out.println("e = " + e);

     assertEquals("cra(a mod 19,a mod 13) = a",a,e);


     java.math.BigInteger p1 = getPrime1();
     java.math.BigInteger p2 = getPrime2();
     java.math.BigInteger p1p2 = p1.multiply(p2);
     //System.out.println("p1p2 = " + p1p2);
     //System.out.println("prime p1 ? = " + p1.isProbablePrime(66));
     //System.out.println("prime p2 ? = " + p2.isProbablePrime(33));
     //System.out.println("prime p1p1 ? = " + p1p2.isProbablePrime(3));
     a = new ModInteger(p1p2,1);

     for ( int i = 0; i < 5; i++ ) {
         a = a.random( (59+29)/2 ); //60+30 );
         //System.out.println("a = " + a);
         b = new ModInteger(p1,a.getVal());
         //System.out.println("b = " + b);
         c = new ModInteger(p2,a.getVal());
         //System.out.println("c = " + c);
         ModInteger di = new ModInteger(p2,p1);
         d = di.inverse();
         //System.out.println("d = " + d);

         e = a.chineseRemainder(b,d,c);
         //System.out.println("e = " + e);

         assertEquals("cra(a mod p1,a mod p2) = a",a,e);
     }
 }


/**
 * Test prime list.
 * 
 */
 public void testPrime() {
     PrimeList primes = new PrimeList();
     //System.out.println("primes = " + primes);

     //assertTrue("all primes ", primes.checkPrimes() );

     int i = 0;
     //System.out.println("primes = ");
     for ( java.math.BigInteger p : primes ) {
         //System.out.print("" + p);
         if ( i++ > 50 ) {
            break;
         }
         //System.out.print(", ");
     }
     //System.out.println();

     //System.out.println("primes = " + primes);

     assertTrue("all primes ", primes.checkPrimes() );
   }

}
