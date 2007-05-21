
/*
 * $Id$
 */

package edu.jas.application;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Logger;

import edu.jas.arith.BigRational;
import edu.jas.arith.BigInteger;

//import edu.jas.structure.RingElem;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;


/**
 * QuotientInt GenPolynomial Test using JUnit. 
 * @author Heinz Kredel.
 */

public class QuotIntPolynomialTest extends TestCase {

/**
 * main.
 */
   public static void main (String[] args) {
       BasicConfigurator.configure();
       junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>QoutIntPolynomialTest</CODE> object.
 * @param name String.
 */
   public QuotIntPolynomialTest(String name) {
          super(name);
   }

/**
 * suite.
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(QuotIntPolynomialTest.class);
     return suite;
   }

   //private final static int bitlen = 100;

   QuotientRing<BigInteger> eFac;
   GenPolynomialRing<BigInteger> mfac;
   GenPolynomialRing<Quotient<BigInteger>> qfac;

   GenPolynomial< Quotient<BigInteger> > a;
   GenPolynomial< Quotient<BigInteger> > b;
   GenPolynomial< Quotient<BigInteger> > c;
   GenPolynomial< Quotient<BigInteger> > d;
   GenPolynomial< Quotient<BigInteger> > e;

   int rl = 3; 
   int kl = 2; // degree of coefficient polynomials!!!
   int ll = 4; //6;
   int el = 2;
   float q = 0.3f;

   protected void setUp() {
       a = b = c = d = e = null;
       TermOrder to = new TermOrder( TermOrder.INVLEX );
       mfac = new GenPolynomialRing<BigInteger>( new BigInteger(1), rl, to );
       eFac = new QuotientRing<BigInteger>( mfac );
       qfac = new GenPolynomialRing<Quotient<BigInteger>>( eFac, rl+1, to );
   }

   protected void tearDown() {
       a = b = c = d = e = null;
       eFac = null;
       mfac = null;
       qfac = null;
   }


/**
 * Test constructor and toString.
 * 
 */
 public void testConstruction() {
     c = qfac.getONE();
     //System.out.println("c = " + c);
     //System.out.println("c.val = " + c.val);
     assertTrue("length( c ) = 1", c.length() == 1);
     assertTrue("isZERO( c )", !c.isZERO() );
     assertTrue("isONE( c )", c.isONE() );

     d = qfac.getZERO();
     //System.out.println("d = " + d);
     //System.out.println("d.val = " + d.val);
     assertTrue("length( d ) = 0", d.length() == 0);
     assertTrue("isZERO( d )", d.isZERO() );
     assertTrue("isONE( d )", !d.isONE() );
 }


/**
 * Test random polynomial.
 * 
 */
 public void testRandom() {
     for (int i = 0; i < 4; i++) {
         //a = qfac.random(ll+i);
         a = qfac.random(kl, ll+2*i, el, q );
         //System.out.println("a["+i+"] = " + a);
         assertTrue("length( a"+i+" ) <> 0", a.length() >= 0);
         assertTrue(" not isZERO( a"+i+" )", !a.isZERO() );
         assertTrue(" not isONE( a"+i+" )", !a.isONE() );
         if ( !a.isZERO() ) {
            b = a.monic();
            //System.out.println("b["+i+"] = " + b);
            assertTrue("ldbcf( b"+i+" ) == 1", b.leadingBaseCoefficient().isONE());
         }
     }
 }


/**
 * Test addition.
 * 
 */
 public void testAddition() {

     a = qfac.random(kl,ll,el,q);
     b = qfac.random(kl,ll,el,q);
     //System.out.println("a = " + a);
     //System.out.println("b = " + b);

     c = a.sum(b);
     d = c.subtract(b);
     assertEquals("a+b-b = a",a,d);

     c = a.sum(b);
     d = b.sum(a);
     //System.out.println("c = " + c);
     //System.out.println("d = " + d);

     assertEquals("a+b = b+a",c,d);

     c = qfac.random(kl,ll,el,q);
     d = c.sum( a.sum(b) );
     e = c.sum( a ).sum(b);
     assertEquals("c+(a+b) = (c+a)+b",d,e);


     c = a.sum( qfac.getZERO() );
     d = a.subtract( qfac.getZERO() );
     assertEquals("a+0 = a-0",c,d);

     c = qfac.getZERO().sum( a );
     d = qfac.getZERO().subtract( a.negate() );
     assertEquals("0+a = 0+(-a)",c,d);

 }


/**
 * Test object multiplication.
 * 
 */
 public void testMultiplication() {

     a = qfac.random(kl,ll,el,q);
     assertTrue("not isZERO( a )", !a.isZERO() );

     b = qfac.random(kl,ll,el,q);
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

     c = qfac.random(kl,ll,el,q);
     //System.out.println("c = " + c);
     d = a.multiply( b.multiply(c) );
     e = (a.multiply(b)).multiply(c);

     //System.out.println("d = " + d);
     //System.out.println("e = " + e);

     //System.out.println("d-e = " + d.subtract(c) );

     assertEquals("a(bc) = (ab)c",d,e);
     assertTrue("a(bc) = (ab)c", d.equals(e) );

     c = a.multiply( qfac.getONE() );
     d = qfac.getONE().multiply( a );
     assertEquals("a*1 = 1*a",c,d);

     if ( a.isUnit() ) {
        c = a.inverse();
        d = c.multiply(a);
        //System.out.println("a = " + a);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertTrue("a*1/a = 1",d.isONE()); 
     }
 }

}
