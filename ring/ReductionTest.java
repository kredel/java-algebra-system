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

import edu.jas.arith.BigRational;
import edu.jas.arith.BigComplex;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
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

   GenPolynomialRing<BigRational> fac;

   GenPolynomial<BigRational> a;
   GenPolynomial<BigRational> b;
   GenPolynomial<BigRational> c;
   GenPolynomial<BigRational> d;
   GenPolynomial<BigRational> e;

   List<GenPolynomial<BigRational>> L;
   PolynomialList<BigRational> F;
   PolynomialList<BigRational> G;

   int rl = 3; 
   int kl = 10;
   int ll = 7;
   int el = 3;
   float q = 0.4f;

   protected void setUp() {
       a = b = c = d = e = null;
       fac = new GenPolynomialRing<BigRational>( new BigRational(0), rl );
   }

   protected void tearDown() {
       a = b = c = d = e = null;
       fac = null;
   }


/**
 * Test constants and empty list reduction
 * 
 */
 public void testRatReduction0() {
     L = new ArrayList<GenPolynomial<BigRational>>();

     //a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     c = fac.getONE();
     d = fac.getZERO();

     e = Reduction.<BigRational>normalform( L, c );
     assertTrue("isONE( e )", e.isONE() ); 

     e = Reduction.<BigRational>normalform( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 


     L.add( c );
     e = Reduction.<BigRational>normalform( L, c );
     assertTrue("isZERO( e )", e.isZERO() ); 

     // e = Reduction.normalform( L, a );
     // assertTrue("isZERO( e )", e.isZERO() ); 

     e = Reduction.<BigRational>normalform( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 


     L = new ArrayList<GenPolynomial<BigRational>>();
     L.add( d );
     e = Reduction.<BigRational>normalform( L, c );
     assertTrue("isONE( e )", e.isONE() ); 

     e = Reduction.<BigRational>normalform( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 
 }


/**
 * Test ReductionMod with constants and empty list reduction
 * 
 */
/*
 public void testRatReduction1() {
     L = new ArrayList<GenPolynomial<BigRational>>();

     //a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     c = fac.getONE();
     d = fac.getZERO();

     e = Reduction.<BigRational>normalformMod( L, c );
     assertTrue("isONE( e )", e.isONE() ); 

     e = Reduction.<BigRational>normalformMod( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 


     L.add( c );
     e = Reduction.<BigRational>normalformMod( L, c );
     assertTrue("isZERO( e )", e.isZERO() ); 

     // e = Reduction.normalform( L, a );
     // assertTrue("isZERO( e )", e.isZERO() ); 

     e = Reduction.<BigRational>normalformMod( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 


     L = new ArrayList();
     L.add( d );
     e = Reduction.<BigRational>normalformMod( L, c );
     assertTrue("isONE( e )", e.isONE() ); 

     e = Reduction.<BigRational>normalformMod( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 
 }
*/


/**
 * Test Rat reduction
 * 
 */
 public void testRatReduction() {

     a = fac.random(kl, ll, el, q );
     b = fac.random(kl, ll, el, q );

     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList<GenPolynomial<BigRational>>();
     L.add(a);

     e = Reduction.<BigRational>normalform( L, a );
     assertTrue("isZERO( e )", e.isZERO() );

     assertTrue("not isZERO( b )", !b.isZERO() );

     L.add(b);
     e = Reduction.<BigRational>normalform( L, a );
     assertTrue("isZERO( e ) some times", e.isZERO() ); 
 }


/**
 * Test Rat reduction Mod
 * 
 */
/*
 public void testRatReductionMod() {

     a = fac.random(kl, ll, el, q );
     b = fac.random(kl, ll, el, q );
     
     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList<GenPolynomial<BigRational>>();
     L.add(a);

     e = Reduction.<BigRational>normalformMod( L, a );
     assertTrue("isZERO( e )", e.isZERO() );

     assertTrue("not isZERO( b )", !b.isZERO() );

     L.add(b);
     e = Reduction.<BigRational>normalformMod( L, a );
     assertTrue("isZERO( e ) some times", e.isZERO() ); 
 }
*/

/**
 * Test Complex reduction
 * 
 */

 public void testComplexReduction() {

     GenPolynomialRing<BigComplex> fac 
          = new GenPolynomialRing<BigComplex>( new BigComplex(0), rl );

     GenPolynomial<BigComplex> a = fac.random(kl, ll, el, q );
     GenPolynomial<BigComplex> b = fac.random(kl, ll, el, q );

     assertTrue("not isZERO( a )", !a.isZERO() );

     List<GenPolynomial<BigComplex>> L 
         = new ArrayList<GenPolynomial<BigComplex>>();
     L.add(a);

     GenPolynomial<BigComplex> e 
         = Reduction.<BigComplex>normalform( L, a );
     assertTrue("isZERO( e )", e.isZERO() );

     assertTrue("not isZERO( b )", !b.isZERO() );

     L.add(b);
     e = Reduction.<BigComplex>normalform( L, a );
     assertTrue("isZERO( e ) some times", e.isZERO() ); 
 }


}
