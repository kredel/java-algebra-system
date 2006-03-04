
/*
 * $Id$
 */

package edu.jas.poly;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

//import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;

import edu.jas.poly.GenPolynomial;

//import edu.jas.structure.RingElem;


/**
 * GaloisField Test using JUnit.
 * @author Heinz Kredel.
 */

public class GaloisFieldTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>GaloisFieldTest</CODE> object.
 * @param name String.
 */
   public GaloisFieldTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(GaloisFieldTest.class);
     return suite;
   }

   //private final static int bitlen = 100;

   AlgebraicNumber<ModInteger> fac;
   GenPolynomialRing<ModInteger> mfac;

   AlgebraicNumber< ModInteger > a;
   AlgebraicNumber< ModInteger > b;
   AlgebraicNumber< ModInteger > c;
   AlgebraicNumber< ModInteger > d;
   AlgebraicNumber< ModInteger > e;

   int rl = 1; 
   int kl = 10;
   int ll = 15;
   int el = ll;
   float q = 0.5f;

   protected long getPrime() {
       long prime = 2; //2^60-93; // 2^30-35; //19; knuth (2,390)
       for ( int i = 1; i < 60; i++ ) {
           prime *= 2;
       }
       prime -= 93;
       //System.out.println("prime = " + prime);
       return prime;
   }

   protected void setUp() {
       a = b = c = d = e = null;
       long prime = getPrime();
       mfac = new GenPolynomialRing<ModInteger>( new ModInteger(prime,1), 1 );
       //System.out.println("mfac = " + mfac);
       GenPolynomial<ModInteger> mo = mfac.random(kl,ll,el,q);
       while ( mo.isUnit() || mo.isZERO() ) {
          mo = mfac.random(ll);
       }
       fac = new AlgebraicNumber<ModInteger>( mo );
       //System.out.println("fac = " + fac);
   }

   protected void tearDown() {
       a = b = c = d = e = null;
       fac = null;
   }


/**
 * Test constructor and toString.
 * 
 */
 public void testConstruction() {
     c = fac.getONE();
     //System.out.println("c = " + c);
     //System.out.println("c.getVal() = " + c.getVal());
     assertTrue("length( c ) = 1", c.getVal().length() == 1);
     assertTrue("isZERO( c )", !c.getVal().isZERO() );
     assertTrue("isONE( c )", c.getVal().isONE() );

     d = fac.getZERO();
     //System.out.println("d = " + d);
     //System.out.println("d.getVal() = " + d.getVal());
     assertTrue("length( d ) = 0", d.getVal().length() == 0);
     assertTrue("isZERO( d )", d.getVal().isZERO() );
     assertTrue("isONE( d )", !d.getVal().isONE() );
 }


/**
 * Test random polynomial
 * 
 */
 public void testRandom() {
     for (int i = 0; i < 7; i++) {
         a = fac.random(ll+i);
         //System.out.println("a = " + a);

         // fac.random(rl+i, kl*(i+1), ll+2*i, el+i, q );
         assertTrue("length( a"+i+" ) <> 0", a.getVal().length() >= 0);
         assertTrue(" not isZERO( a"+i+" )", !a.getVal().isZERO() );
         assertTrue(" not isONE( a"+i+" )", !a.getVal().isONE() );
     }
 }


/**
 * Test addition.
 * 
 */
 public void testAddition() {

     a = fac.random(ll);
     b = fac.random(ll);

     c = a.sum(b);
     d = c.subtract(b);
     assertEquals("a+b-b = a",a,d);

     c = fac.random(ll);
     d = c.sum( a.sum(b) );
     e = c.sum( a ).sum(b);
     assertEquals("c+(a+b) = (c+a)+b",d,e);


     c = a.sum( fac.getZERO() );
     d = a.subtract( fac.getZERO() );
     assertEquals("a+0 = a-0",c,d);

     c = fac.getZERO().sum( a );
     d = fac.getZERO().subtract( a.negate() );
     assertEquals("0+a = 0+(-a)",c,d);

 }


/**
 * Test object multiplication.
 * 
 */

 public void testMultiplication() {

     a = fac.random(ll);
     assertTrue("not isZERO( a )", !a.isZERO() );

     b = fac.random(ll);
     assertTrue("not isZERO( b )", !b.isZERO() );

     c = b.multiply(a);
     d = a.multiply(b);
     assertTrue("not isZERO( c )", !c.isZERO() );
     assertTrue("not isZERO( d )", !d.isZERO() );

     //System.out.println("a = " + a);
     //System.out.println("b = " + b);
     e = d.subtract(c);
     assertTrue("isZERO( a*b-b*a ) " + e, e.isZERO() );

     assertTrue("a*b = b*a", c.equals(d) );
     assertEquals("a*b = b*a",c,d);

     c = fac.random(ll);
     //System.out.println("c = " + c);
     d = a.multiply( b.multiply(c) );
     e = (a.multiply(b)).multiply(c);

     //System.out.println("d = " + d);
     //System.out.println("e = " + e);

     //System.out.println("d-e = " + d.subtract(c) );

     assertEquals("a(bc) = (ab)c",d,e);
     assertTrue("a(bc) = (ab)c", d.equals(e) );

     c = a.multiply( fac.getONE() );
     d = fac.getONE().multiply( a );
     assertEquals("a*1 = 1*a",c,d);


     c = a.inverse();
     d = c.multiply(a);
     //System.out.println("a = " + a);
     //System.out.println("c = " + c);
     //System.out.println("d = " + d);
     assertEquals("a*1/a = 1",fac.getONE(),d);
 }

}
