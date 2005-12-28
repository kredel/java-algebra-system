/*
 * $Id$
 */

package edu.jas.ring;

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
 * Reduction Test using JUnit.
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
 * suite.
 * @return a test suite.
 */
public static Test suite() {
     TestSuite suite= new TestSuite(ReductionTest.class);
     return suite;
   }

   //private final static int bitlen = 100;

   GenPolynomialRing<BigRational> fac;

   GenPolynomial<BigRational> a;
   GenPolynomial<BigRational> b;
   GenPolynomial<BigRational> c;
   GenPolynomial<BigRational> d;
   GenPolynomial<BigRational> e;

   List<GenPolynomial<BigRational>> L;
   PolynomialList<BigRational> F;
   PolynomialList<BigRational> G;

   Reduction<BigRational> red;
   Reduction<BigRational> redpar;

   int rl = 3; 
   int kl = 10;
   int ll = 7;
   int el = 3;
   float q = 0.4f;

   protected void setUp() {
       a = b = c = d = e = null;
       fac = new GenPolynomialRing<BigRational>( new BigRational(0), rl );
       red = new ReductionSeq<BigRational>();
       redpar = new ReductionPar<BigRational>();
   }

   protected void tearDown() {
       a = b = c = d = e = null;
       fac = null;
       red = null;
       redpar = null;
   }


/**
 * Test constants and empty list reduction.
 */
 public void testRatReduction0() {
     L = new ArrayList<GenPolynomial<BigRational>>();

     a = fac.random(kl, ll, el, q );
     c = fac.getONE();
     d = fac.getZERO();

     e = red.normalform( L, c );
     assertTrue("isONE( e )", e.isONE() ); 

     e = red.normalform( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 


     L.add( c );
     e = red.normalform( L, c );
     assertTrue("isZERO( e )", e.isZERO() ); 

     e = red.normalform( L, a );
     assertTrue("isZERO( e )", e.isZERO() ); 

     e = red.normalform( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 


     L = new ArrayList<GenPolynomial<BigRational>>();
     L.add( d );
     e = red.normalform( L, c );
     assertTrue("isONE( e )", e.isONE() ); 

     e = red.normalform( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 
 }


/**
 * Test ReductionMod with constants and empty list reduction.
 */
 public void testRatReduction1() {
     L = new ArrayList<GenPolynomial<BigRational>>();

     a = fac.random(kl, ll, el, q );
     c = fac.getONE();
     d = fac.getZERO();

     e = redpar.normalform( L, c );
     assertTrue("isONE( e )", e.isONE() ); 

     e = redpar.normalform( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 


     L.add( c );
     e = redpar.normalform( L, c );
     assertTrue("isZERO( e )", e.isZERO() ); 

     e = redpar.normalform( L, a );
     assertTrue("isZERO( e )", e.isZERO() ); 

     e = redpar.normalform( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 


     L = new ArrayList<GenPolynomial<BigRational>>();
     L.add( d );
     e = redpar.normalform( L, c );
     assertTrue("isONE( e )", e.isONE() ); 

     e = redpar.normalform( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 
 }


/**
 * Test Rat reduction.
 * 
 */
 public void testRatReduction() {

     a = fac.random(kl, ll, el, q );
     b = fac.random(kl, ll, el, q );

     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList<GenPolynomial<BigRational>>();
     L.add(a);

     e = red.normalform( L, a );
     assertTrue("isZERO( e )", e.isZERO() );

     assertTrue("not isZERO( b )", !b.isZERO() );

     L.add(b);
     e = red.normalform( L, a );
     assertTrue("isZERO( e ) some times", e.isZERO() ); 
 }


/**
 * Test Rat reduction Mod.
 * 
 */
 public void testRatReductionMod() {

     a = fac.random(kl, ll, el, q );
     b = fac.random(kl, ll, el, q );
     
     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList<GenPolynomial<BigRational>>();
     L.add(a);

     e = redpar.normalform( L, a );
     assertTrue("isZERO( e )", e.isZERO() );

     assertTrue("not isZERO( b )", !b.isZERO() );

     L.add(b);
     e = redpar.normalform( L, a );
     assertTrue("isZERO( e ) some times", e.isZERO() ); 
 }


/**
 * Test Complex reduction.
 * 
 */

 public void testComplexReduction() {

     GenPolynomialRing<BigComplex> fac 
          = new GenPolynomialRing<BigComplex>( new BigComplex(0), rl );

     Reduction<BigComplex> cred = new ReductionSeq<BigComplex>();

     GenPolynomial<BigComplex> a = fac.random(kl, ll, el, q );
     GenPolynomial<BigComplex> b = fac.random(kl, ll, el, q );

     assertTrue("not isZERO( a )", !a.isZERO() );

     List<GenPolynomial<BigComplex>> L 
         = new ArrayList<GenPolynomial<BigComplex>>();
     L.add(a);

     GenPolynomial<BigComplex> e 
         = cred.normalform( L, a );
     assertTrue("isZERO( e )", e.isZERO() );

     assertTrue("not isZERO( b )", !b.isZERO() );

     L.add(b);
     e = cred.normalform( L, a );
     assertTrue("isZERO( e ) some times", e.isZERO() ); 
 }


}
