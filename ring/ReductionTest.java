/*
 * $Id$
 */

package edu.jas.ring;

//import edu.jas.poly.RatGBase;

import java.util.List;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.poly.OrderedPolynomial;
import edu.jas.poly.RatOrderedMapPolynomial;
import edu.jas.poly.ComplexOrderedMapPolynomial;
import edu.jas.poly.PolynomialList;

/**
 * Reduction Test using JUnit 
 * @author Heinz Kredel.
 */

public class ReductionTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>ReductionTest</CODE> object.
 * @param name String
 */
   public ReductionTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(ReductionTest.class);
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
   }

   protected void tearDown() {
       a = b = c = d = e = null;
   }


/**
 * Test constants and empty list reduction
 * 
 */
 public void testRatReduction0() {
     L = new ArrayList();

     //a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     c = RatOrderedMapPolynomial.ONE;
     d = RatOrderedMapPolynomial.ZERO;

     e = Reduction.normalform( L, c );
     assertTrue("isONE( e )", e.isONE() ); 

     e = Reduction.normalform( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 


     L.add( c );
     e = Reduction.normalform( L, c );
     assertTrue("isZERO( e )", e.isZERO() ); 

     // e = Reduction.normalform( L, a );
     // assertTrue("isZERO( e )", e.isZERO() ); 

     e = Reduction.normalform( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 


     L = new ArrayList();
     L.add( d );
     e = Reduction.normalform( L, c );
     assertTrue("isONE( e )", e.isONE() ); 

     e = Reduction.normalform( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 
 }


/**
 * Test ReductionMod with constants and empty list reduction
 * 
 */
 public void testRatReduction1() {
     L = new ArrayList();

     //a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     c = RatOrderedMapPolynomial.ONE;
     d = RatOrderedMapPolynomial.ZERO;

     e = Reduction.normalformMod( L, c );
     assertTrue("isONE( e )", e.isONE() ); 

     e = Reduction.normalformMod( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 


     L.add( c );
     e = Reduction.normalformMod( L, c );
     assertTrue("isZERO( e )", e.isZERO() ); 

     // e = Reduction.normalform( L, a );
     // assertTrue("isZERO( e )", e.isZERO() ); 

     e = Reduction.normalformMod( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 


     L = new ArrayList();
     L.add( d );
     e = Reduction.normalformMod( L, c );
     assertTrue("isONE( e )", e.isONE() ); 

     e = Reduction.normalformMod( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 
 }


/**
 * Test Rat reduction
 * 
 */
 public void testRatReduction() {

     a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     b = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );

     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();
     L.add(a);

     e = Reduction.normalform( L, a );
     assertTrue("isZERO( e )", e.isZERO() );

     assertTrue("not isZERO( b )", !b.isZERO() );

     L.add(b);
     e = Reduction.normalform( L, a );
     assertTrue("isZERO( e ) some times", e.isZERO() ); 
 }


/**
 * Test Rat reduction Mod
 * 
 */
 public void testRatReductionMod() {

     a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     b = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );

     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();
     L.add(a);

     e = Reduction.normalformMod( L, a );
     assertTrue("isZERO( e )", e.isZERO() );

     assertTrue("not isZERO( b )", !b.isZERO() );

     L.add(b);
     e = Reduction.normalformMod( L, a );
     assertTrue("isZERO( e ) some times", e.isZERO() ); 
 }


/**
 * Test Complex reduction
 * 
 */
 public void testComplexReduction() {

     a = ComplexOrderedMapPolynomial.DICRAS(rl, kl, ll, el, q );
     b = ComplexOrderedMapPolynomial.DICRAS(rl, kl, ll, el, q );

     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();
     L.add(a);

     e = Reduction.normalform( L, a );
     assertTrue("isZERO( e )", e.isZERO() );

     assertTrue("not isZERO( b )", !b.isZERO() );

     L.add(b);
     e = Reduction.normalform( L, a );
     assertTrue("isZERO( e ) some times", e.isZERO() ); 
 }


}
