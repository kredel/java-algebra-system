
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

//import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;


/**
 * QuotientInt Test using JUnit. 
 * @author Heinz Kredel.
 */

public class QuotientIntTest extends TestCase {

/**
 * main.
 */
   public static void main (String[] args) {
       BasicConfigurator.configure();
       junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>QuotientIntTest</CODE> object.
 * @param name String.
 */
   public QuotientIntTest(String name) {
          super(name);
   }

/**
 * suite.
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(QuotientIntTest.class);
     return suite;
   }

   //private final static int bitlen = 100;

   QuotientRing<BigInteger> eFac;
   GenPolynomialRing<BigInteger> mfac;

   Quotient< BigInteger > a;
   Quotient< BigInteger > b;
   Quotient< BigInteger > c;
   Quotient< BigInteger > d;
   Quotient< BigInteger > e;

   int rl = 3; 
   int kl = 5;
   int ll = 4; //6;
   int el = 2;
   float q = 0.4f;

   protected void setUp() {
       a = b = c = d = e = null;
       TermOrder to = new TermOrder( TermOrder.INVLEX );
       mfac = new GenPolynomialRing<BigInteger>( new BigInteger(1), rl, to );
       eFac = new QuotientRing<BigInteger>( mfac );
   }

   protected void tearDown() {
       a = b = c = d = e = null;
       eFac = null;
   }


/**
 * Test constructor and toString.
 * 
 */
 public void testConstruction() {
     c = eFac.getONE();
     //System.out.println("c = " + c);
     //System.out.println("c.val = " + c.val);
     assertTrue("length( c ) = 1", c.num.length() == 1);
     assertTrue("isZERO( c )", !c.isZERO() );
     assertTrue("isONE( c )", c.isONE() );

     d = eFac.getZERO();
     //System.out.println("d = " + d);
     //System.out.println("d.val = " + d.val);
     assertTrue("length( d ) = 0", d.num.length() == 0);
     assertTrue("isZERO( d )", d.isZERO() );
     assertTrue("isONE( d )", !d.isONE() );
 }


/**
 * Test random polynomial.
 * 
 */
 public void testRandom() {
     for (int i = 0; i < 7; i++) {
         //a = eFac.random(ll+i);
         a = eFac.random(kl*(i+1), ll+2+2*i, el, q );
         //System.out.println("a = " + a);
         assertTrue("length( a"+i+" ) <> 0", a.num.length() >= 0);
         assertTrue(" not isZERO( a"+i+" )", !a.isZERO() );
         assertTrue(" not isONE( a"+i+" )", !a.isONE() );
     }
 }


/**
 * Test addition.
 * 
 */
 public void testAddition() {

     a = eFac.random(kl,ll,el,q);
     b = eFac.random(kl,ll,el,q);
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

     c = eFac.random(kl,ll,el,q);
     d = c.sum( a.sum(b) );
     e = c.sum( a ).sum(b);
     assertEquals("c+(a+b) = (c+a)+b",d,e);


     c = a.sum( eFac.getZERO() );
     d = a.subtract( eFac.getZERO() );
     assertEquals("a+0 = a-0",c,d);

     c = eFac.getZERO().sum( a );
     d = eFac.getZERO().subtract( a.negate() );
     assertEquals("0+a = 0+(-a)",c,d);

 }


/**
 * Test object multiplication.
 * 
 */
 public void testMultiplication() {

     a = eFac.random(kl,ll,el,q);
     assertTrue("not isZERO( a )", !a.isZERO() );

     b = eFac.random(kl,ll,el,q);
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

     c = eFac.random(kl,ll,el,q);
     //System.out.println("c = " + c);
     d = a.multiply( b.multiply(c) );
     e = (a.multiply(b)).multiply(c);

     //System.out.println("d = " + d);
     //System.out.println("e = " + e);

     //System.out.println("d-e = " + d.subtract(c) );

     assertEquals("a(bc) = (ab)c",d,e);
     assertTrue("a(bc) = (ab)c", d.equals(e) );

     c = a.multiply( eFac.getONE() );
     d = eFac.getONE().multiply( a );
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
