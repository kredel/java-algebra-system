/*
 * $Id$
 */

package edu.jas.poly;

//import edu.jas.poly.RatGBase;

import java.util.List;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * RatGBase Test using JUnit 
 * @author Heinz Kredel.
 */

public class RatGBaseTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>RatGBaseTest</CODE> object.
 * @param name String
 */
   public RatGBaseTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(RatGBaseTest.class);
     return suite;
   }

   private final static int bitlen = 100;

   RatPolynomial a;
   RatPolynomial b;
   RatPolynomial c;
   RatPolynomial d;
   RatPolynomial e;

   List L;
   PolynomialList F;
   PolynomialList G;

   int rl = 3; 
   int kl = 10;
   int ll = 7;
   int el = 3;
   float q = 0.4f;

   protected void setUp() {
       a = b = c = d = e = null;
   }

   protected void tearDown() {
       a = b = c = d = e = null;
   }


/**
 * Test reduction
 * 
 */
 public void testReduction() {

     a = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();
     L.add(a);

     b = RatGBase.DIRPNF( L, a );
     assertTrue("isZERO( b )", b.isZERO() );

     b = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );

     L.add(b);
     c = RatGBase.DIRPNF( L, a );
     assertTrue("isZERO( c ) some times", c.isZERO() ); 

 }

/**
 * Test GBase
 * 
 */
 public void testGBase() {

     a = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();
     L.add(a);

     L = RatGBase.DIRPGB( L );
     assertTrue("isDIRPGB( { a } )", RatGBase.isDIRPGB(L) );

     b = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );
     L.add(b);
     //     System.out.println("L = " + L.size() );

     L = RatGBase.DIRPGB( L );
     assertTrue("isDIRPGB( { a, b } )", RatGBase.isDIRPGB(L) );

     c = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( c )", !c.isZERO() );
     L.add(c);

     L = RatGBase.DIRPGB( L );
     assertTrue("isDIRPGB( { a, ,b, c } )", RatGBase.isDIRPGB(L) );

     d = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( d )", !d.isZERO() );
     L.add(d);

     L = RatGBase.DIRPGB( L );
     assertTrue("isDIRPGB( { a, ,b, c } )", RatGBase.isDIRPGB(L) );

 }

/**
 * Test parallel GBase
 * 
 */
 public void testParallelGBase() {
     int threads = 2;

     a = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();
     L.add(a);

     L = RatGBase.DIRPGBparallel( L, threads );
     assertTrue("isDIRPGB( { a } )", RatGBase.isDIRPGB(L) );

     b = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );
     L.add(b);
     //     System.out.println("L = " + L.size() );

     L = RatGBase.DIRPGBparallel( L, threads );
     assertTrue("isDIRPGB( { a, b } )", RatGBase.isDIRPGB(L) );

     c = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( c )", !c.isZERO() );
     L.add(c);

     L = RatGBase.DIRPGBparallel( L, threads );
     assertTrue("isDIRPGB( { a, ,b, c } )", RatGBase.isDIRPGB(L) );

     d = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( d )", !d.isZERO() );
     L.add(d);

     L = RatGBase.DIRPGBparallel( L, threads );
     assertTrue("isDIRPGB( { a, ,b, c, d } )", RatGBase.isDIRPGB(L) );

 }

}
