/*
 * $Id$
 */

package edu.jas.ring;

//import edu.jas.poly.GroebnerBase;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.RatOrderedMapPolynomial;
import edu.jas.poly.OrderedPolynomial;
import edu.jas.poly.PolynomialList;

/**
 * GroebnerBase Test using JUnit 
 * @author Heinz Kredel.
 */

public class GroebnerBaseTest extends TestCase {

    private static final Logger logger = Logger.getLogger(GroebnerBaseTest.class);

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
   float q = 0.3f; //0.4f

   protected void setUp() {
       a = b = c = d = e = null;
       a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
       b = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
       c = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
       d = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
       e = d; //RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
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

     // e = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( e )", !e.isZERO() );
     L.add(e);

     L = GroebnerBase.DIRPGB( L );
     assertTrue("isDIRPGB( { a, ,b, c, d, e } )", GroebnerBase.isDIRPGB(L) );
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

     L = GroebnerBaseParallel.DIRPGB( L, threads );
     assertTrue("isDIRPGB( { a } )", GroebnerBase.isDIRPGB(L) );

     //b = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );
     L.add(b);
     //  System.out.println("L = " + L.size() );

     L = GroebnerBaseParallel.DIRPGB( L, threads );
     assertTrue("isDIRPGB( { a, b } )", GroebnerBase.isDIRPGB(L) );

     // c = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( c )", !c.isZERO() );
     L.add(c);

     L = GroebnerBaseParallel.DIRPGB( L, threads );
     assertTrue("isDIRPGB( { a, ,b, c } )", GroebnerBase.isDIRPGB(L) );

     // d = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( d )", !d.isZERO() );
     L.add(d);

     L = GroebnerBaseParallel.DIRPGB( L, threads );
     assertTrue("isDIRPGB( { a, ,b, c, d } )", GroebnerBase.isDIRPGB(L) );

     // e = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( e )", !e.isZERO() );
     L.add(e);

     if ( logger.isDebugEnabled() ) {
	 for (Iterator it = L.iterator(); it.hasNext(); ) {
             logger.debug("before Li = " + it.next() );
	 }
     }

     L = GroebnerBaseParallel.DIRPGB( L, threads );
     assertTrue("isDIRPGB( { a, ,b, c, d, e } )", GroebnerBase.isDIRPGB(L) );

     if ( logger.isDebugEnabled() ) {
	 for (Iterator it = L.iterator(); it.hasNext(); ) {
             logger.debug("after Li = " + it.next() );
	 }
     }
 }

/**
 * Test compare sequential with parallel GBase
 * 
 */
 public void testSequentialParallelGBase() {
     int threads = 2;

     ArrayList Gs, Gp;
     L = new ArrayList();
     Iterator is, ip;

     L.add(a);
     Gs = GroebnerBase.DIRPGB( L );
     Gp = GroebnerBaseParallel.DIRPGB( L, threads );
     is = Gs.iterator(); 
     ip = Gp.iterator(); 
     while ( is.hasNext() && ip.hasNext() ) {
	   assertEquals("is.equals(ip)", is.next(), ip.next() );
     }
     assertFalse("Gs = Gp = empty " , is.hasNext() || ip.hasNext() );

     L = Gs;
     L.add(b);
     Gs = GroebnerBase.DIRPGB( L );
     Gp = GroebnerBaseParallel.DIRPGB( L, threads );
     is = Gs.iterator(); 
     ip = Gp.iterator(); 
     while ( is.hasNext() && ip.hasNext() ) {
	   assertEquals("is.equals(ip)", is.next(), ip.next() );
     }
     assertFalse("Gs = Gp = empty " , is.hasNext() || ip.hasNext() );

     L = Gs;
     L.add(c);
     Gs = GroebnerBase.DIRPGB( L );
     Gp = GroebnerBaseParallel.DIRPGB( L, threads );
     is = Gs.iterator(); 
     ip = Gp.iterator(); 
     while ( is.hasNext() && ip.hasNext() ) {
	   assertEquals("is.equals(ip)", is.next(), ip.next() );
     }
     assertFalse("Gs = Gp = empty " , is.hasNext() || ip.hasNext() );

     L = Gs;
     L.add(d);
     Gs = GroebnerBase.DIRPGB( L );
     Gp = GroebnerBaseParallel.DIRPGB( L, threads );
     is = Gs.iterator(); 
     ip = Gp.iterator(); 
     while ( is.hasNext() && ip.hasNext() ) {
	   assertEquals("is.equals(ip)", is.next(), ip.next() );
     }
     assertFalse("Gs = Gp = empty " , is.hasNext() || ip.hasNext() );

     L = Gs;
     L.add(e);
     Gs = GroebnerBase.DIRPGB( L );
     Gp = GroebnerBaseParallel.DIRPGB( L, threads );
     is = Gs.iterator(); 
     ip = Gp.iterator(); 
     while ( is.hasNext() && ip.hasNext() ) {
	   assertEquals("is.equals(ip)", is.next(), ip.next() );
     }
     assertFalse("Gs = Gp = empty " , is.hasNext() || ip.hasNext() );
 }

}
