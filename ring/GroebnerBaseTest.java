/*
 * $Id$
 */

package edu.jas.ring;

//import edu.jas.poly.GroebnerBase;

import java.util.List;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.poly.ExpVector;
import edu.jas.poly.RatOrderedMapPolynomial;
import edu.jas.poly.OrderedPolynomial;
import edu.jas.poly.PolynomialList;

/**
 * GroebnerBase Test using JUnit 
 * @author Heinz Kredel.
 */

public class GroebnerBaseTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>GroebnerBaseTest</CODE> object.
 * @param name String
 */
   public GroebnerBaseTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(GroebnerBaseTest.class);
     return suite;
   }

   private final static int bitlen = 100;

   OrderedPolynomial a;
   OrderedPolynomial b;
   OrderedPolynomial c;
   OrderedPolynomial d;
   OrderedPolynomial e;

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
       a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
       b = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
       c = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
       d = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
   }

   protected void tearDown() {
       a = b = c = d = e = null;
   }


/**
 * Test GBase
 * 
 */
 public void testGBase() {

     // a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();
     L.add(a);

     L = GroebnerBase.DIRPGB( L );
     assertTrue("isDIRPGB( { a } )", GroebnerBase.isDIRPGB(L) );

     // b = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );
     L.add(b);
     //System.out.println("L = " + L.size() );

     L = GroebnerBase.DIRPGB( L );
     assertTrue("isDIRPGB( { a, b } )", GroebnerBase.isDIRPGB(L) );

     // c = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( c )", !c.isZERO() );
     L.add(c);

     L = GroebnerBase.DIRPGB( L );
     assertTrue("isDIRPGB( { a, ,b, c } )", GroebnerBase.isDIRPGB(L) );

     // d = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( d )", !d.isZERO() );
     L.add(d);

     L = GroebnerBase.DIRPGB( L );
     assertTrue("isDIRPGB( { a, ,b, c, d } )", GroebnerBase.isDIRPGB(L) );
 }

/**
 * Test parallel GBase
 * 
 */
 public void testParallelGBase() {
     int threads = 2;

     //a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();
     L.add(a);

     L = GroebnerBase.DIRPGBparallel( L, threads );
     assertTrue("isDIRPGB( { a } )", GroebnerBase.isDIRPGB(L) );

     //b = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );
     L.add(b);
     //  System.out.println("L = " + L.size() );

     L = GroebnerBase.DIRPGBparallel( L, threads );
     assertTrue("isDIRPGB( { a, b } )", GroebnerBase.isDIRPGB(L) );

     // c = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( c )", !c.isZERO() );
     L.add(c);

     L = GroebnerBase.DIRPGBparallel( L, threads );
     assertTrue("isDIRPGB( { a, ,b, c } )", GroebnerBase.isDIRPGB(L) );

     // d = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( d )", !d.isZERO() );
     L.add(d);

     L = GroebnerBase.DIRPGBparallel( L, threads );
     assertTrue("isDIRPGB( { a, ,b, c, d } )", GroebnerBase.isDIRPGB(L) );
 }

}
