/*
 * $Id$
 * log: RatGBaseTest.java,v 1.3 2003/12/25 kredel Exp 
 */

package edu.jas.ring;

//import edu.jas.poly.RatGBase;

import java.util.List;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.poly.ExpVector;
import edu.jas.poly.RatPolynomial;
import edu.jas.poly.PolynomialList;

/**
 * RatGBase Test using JUnit 
 * @author Heinz Kredel.
 */

public class RatGBaseTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
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
       a = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
       b = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
       c = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
       d = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
   }

   protected void tearDown() {
       a = b = c = d = e = null;
   }


/**
 * Test reduction
 * 
 */
 public void testReduction() {

     //a = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();
     L.add(a);

     e = RatGBase.DIRPNF( L, a );
     assertTrue("isZERO( e )", e.isZERO() );

     //b = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );

     L.add(b);
     e = RatGBase.DIRPNF( L, a );
     assertTrue("isZERO( e ) some times", e.isZERO() ); 
 }

/**
 * Test GBase
 * 
 */
 public void testGBase() {

     // a = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();
     L.add(a);

     L = RatGBase.DIRPGB( L );
     assertTrue("isDIRPGB( { a } )", RatGBase.isDIRPGB(L) );

     // b = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );
     L.add(b);
     // System.out.println("L = " + L.size() );

     L = RatGBase.DIRPGB( L );
     assertTrue("isDIRPGB( { a, b } )", RatGBase.isDIRPGB(L) );

     // c = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( c )", !c.isZERO() );
     L.add(c);

     L = RatGBase.DIRPGB( L );
     assertTrue("isDIRPGB( { a, ,b, c } )", RatGBase.isDIRPGB(L) );

     // d = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( d )", !d.isZERO() );
     L.add(d);

     L = RatGBase.DIRPGB( L );
     assertTrue("isDIRPGB( { a, ,b, c, d } )", RatGBase.isDIRPGB(L) );
 }

/**
 * Test parallel GBase
 * 
 */
 public void testParallelGBase() {
     int threads = 2;

     //a = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();
     L.add(a);

     L = RatGBase.DIRPGBparallel( L, threads );
     assertTrue("isDIRPGB( { a } )", RatGBase.isDIRPGB(L) );

     //b = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );
     L.add(b);
     //  System.out.println("L = " + L.size() );

     L = RatGBase.DIRPGBparallel( L, threads );
     assertTrue("isDIRPGB( { a, b } )", RatGBase.isDIRPGB(L) );

     // c = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( c )", !c.isZERO() );
     L.add(c);

     L = RatGBase.DIRPGBparallel( L, threads );
     assertTrue("isDIRPGB( { a, ,b, c } )", RatGBase.isDIRPGB(L) );

     /*
     // d = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( d )", !d.isZERO() );
     L.add(d);

     L = RatGBase.DIRPGBparallel( L, threads );
     assertTrue("isDIRPGB( { a, ,b, c, d } )", RatGBase.isDIRPGB(L) );
     */
 }

}
