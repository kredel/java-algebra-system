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

import edu.jas.poly.ExpVector;
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
 * Test Rat reduction
 * 
 */
 public void testRatReduction() {

     a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     b = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );

     //a = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();
     L.add(a);

     e = Reduction.Normalform( L, a );
     assertTrue("isZERO( e )", e.isZERO() );

     //b = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );

     L.add(b);
     e = Reduction.Normalform( L, a );
     assertTrue("isZERO( e ) some times", e.isZERO() ); 
 }


/**
 * Test Complex reduction
 * 
 */
 public void testComplexReduction() {

     a = ComplexOrderedMapPolynomial.DICRAS(rl, kl, ll, el, q );
     b = ComplexOrderedMapPolynomial.DICRAS(rl, kl, ll, el, q );

     //a = ComplexPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();
     L.add(a);

     e = Reduction.Normalform( L, a );
     assertTrue("isZERO( e )", e.isZERO() );

     //b = ComplexPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );

     L.add(b);
     e = Reduction.Normalform( L, a );
     assertTrue("isZERO( e ) some times", e.isZERO() ); 
 }


}
