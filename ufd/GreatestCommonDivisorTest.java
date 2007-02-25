/*
 * $Id$
 */

package edu.jas.ufd;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.poly.ExpVector;
import edu.jas.poly.TermOrder;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.arith.BigInteger;


/**
 * GreatestCommonDivisor Test using JUnit.
 * @author Heinz Kredel.
 */

public class GreatestCommonDivisorTest extends TestCase {

/**
 * main.
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>GreatestCommonDivisorTest</CODE> object.
 * @param name String.
 */
   public GreatestCommonDivisorTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(GreatestCommonDivisorTest.class);
     return suite;
   }

   //private final static int bitlen = 100;

   GreatestCommonDivisor<BigInteger> ufd 
          = new GreatestCommonDivisor<BigInteger>();

   TermOrder to = new TermOrder( TermOrder.INVLEX );

   GenPolynomialRing<BigInteger> dfac;
   GenPolynomialRing<BigInteger> cfac;
   GenPolynomialRing<GenPolynomial<BigInteger>> rfac;

   BigInteger ai;
   BigInteger bi;
   BigInteger ci;
   BigInteger di;
   BigInteger ei;

   GenPolynomial<BigInteger> a;
   GenPolynomial<BigInteger> b;
   GenPolynomial<BigInteger> c;
   GenPolynomial<BigInteger> d;
   GenPolynomial<BigInteger> e;

   GenPolynomial<GenPolynomial<BigInteger>> ar;
   GenPolynomial<GenPolynomial<BigInteger>> br;
   GenPolynomial<GenPolynomial<BigInteger>> cr;
   GenPolynomial<GenPolynomial<BigInteger>> dr;
   GenPolynomial<GenPolynomial<BigInteger>> er;

   int rl = 5; 
   int kl = 5;
   int ll = 5;
   int el = 3;
   float q = 0.3f;

   protected void setUp() {
       a = b = c = d = e = null;
       ai = bi = ci = di = ei = null;
       ar = br = cr = dr = er = null;
       dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),rl,to);
       cfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),rl-1,to);
       rfac = new GenPolynomialRing<GenPolynomial<BigInteger>>(cfac,1,to);
   }

   protected void tearDown() {
       a = b = c = d = e = null;
       ai = bi = ci = di = ei = null;
       ar = br = cr = dr = er = null;
       dfac = null;
       cfac = null;
       rfac = null;
   }


/**
 * Test recursive <--> distributive conversion.
 * 
 */
 public void ytestConversion() {
     c = dfac.getONE();
     assertTrue("length( c ) = 1", c.length() == 1);
     assertTrue("isZERO( c )", !c.isZERO() );
     assertTrue("isONE( c )", c.isONE() );

     cr = ufd.recursive(rfac,c);
     a = ufd.distribute(dfac,cr);
     assertEquals("c == dist(rec(c))", c, a );

     d = dfac.getZERO();
     assertTrue("length( d ) = 0", d.length() == 0);
     assertTrue("isZERO( d )", d.isZERO() );
     assertTrue("isONE( d )", !d.isONE() );

     dr = ufd.recursive(rfac,d);
     b = ufd.distribute(dfac,dr);
     assertEquals("d == dist(rec(d))", d, b );
 }


/**
 * Test random recursive <--> distributive conversion.
 * 
 */
 public void ytestRandomConversion() {
     for (int i = 0; i < 7; i++) {
         c = dfac.random(kl*(i+2),ll+2*i,el+i,q);

         assertTrue("length( c"+i+" ) <> 0", c.length() >= 0);
         assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         assertTrue(" not isONE( c"+i+" )", !c.isONE() );

         cr = ufd.recursive(rfac,c);
         a = ufd.distribute(dfac,cr);
         //System.out.println("c   = " + c);
         //System.out.println("cr  = " + cr);
         //System.out.println("crd = " + a);

         assertEquals("c == dist(rec(c))", c, a );
     }
 }


/**
 * Test base content and primitive part.
 * 
 */
 public void ytestBaseContentPP() {
     di = new BigInteger( 1 );

     for (int i = 0; i < 13; i++) {
         c = dfac.random(kl*(i+2),ll+2*i,el+i,q);
         c = c.multiply( di.random(kl*(i+2)) );

         assertTrue("length( c"+i+" ) <> 0", c.length() >= 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );
         
         ci = ufd.baseContent(c);
         d = ufd.basePrimitivePart(c);
         //System.out.println("c  = " + c);
         //System.out.println("ci = " + ci);
         //System.out.println("d  = " + d);

         a = d.multiply(ci);
         assertEquals("c == cont(c)pp(c)", c, a );
     }
 }


/**
 * Test base quotioent and remainder.
 * 
 */
 public void ytestBaseQR() {
     di = new BigInteger( 1 );
     dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),1,to);

     for (int i = 0; i < 5; i++) {
         a = dfac.random(kl*(i+2),ll+2*i,el+2*i,q);
         c = dfac.random(kl*(i+2),ll+2*i,el+2*i,q);
         a = ufd.basePrimitivePart(a).abs();
         c = ufd.basePrimitivePart(c);
         ci = di.random(kl*(i+2));

         //System.out.println("a  = " + a);
         //System.out.println("c  = " + c);
         //System.out.println("ci = " + ci);

         assertTrue("length( c"+i+" ) <> 0", c.length() >= 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );
         
         b = a.multiply(ci);
         //System.out.println("b  = " + b);
         d = ufd.basePseudoDivide(b,ci);
         //d = ufd.basePrimitivePart(d).abs();
         //System.out.println("d  = " + d);

         assertEquals("a == ac/c", a, d );

         b = a.multiply(c);
         //System.out.println("b  = " + b);
         d = ufd.basePseudoRemainder(b,c);
         //d = ufd.basePrimitivePart(d).abs();
         //System.out.println("d  = " + d);

         assertTrue("rem(ac,c) == 0", d.isZERO() );

         b = a.multiply(c);
         //System.out.println("b  = " + b);
         d = ufd.basePseudoDivide(b,c);
         //System.out.println("d  = " + d);
         d = ufd.basePrimitivePart(d).abs();
         //System.out.println("d  = " + d);

         assertEquals("a == ac/c", a, d );
     }
 }


/**
 * Test base gcd.
 * 
 */
 public void ytestBaseGcd() {

     dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),1,to);

     for (int i = 0; i < 5; i++) {
         a = dfac.random(kl*(i+2),ll+2*i,el+2*i,q);
         b = dfac.random(kl*(i+2),ll+2*i,el+2*i,q);
         c = dfac.random(kl*(i+2),ll+2*i,el+2*i,q);
         a = ufd.basePrimitivePart(a);
         b = ufd.basePrimitivePart(b);
         c = ufd.basePrimitivePart(c).abs();

         //System.out.println("a  = " + a);
         //System.out.println("b  = " + b);
         //System.out.println("c  = " + c);

         assertTrue("length( c"+i+" ) <> 0", c.length() >= 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );
         
         a = a.multiply(c);
         b = b.multiply(c);

         d = ufd.basePseudoGcd(a,b);
         d = ufd.basePrimitivePart(d).abs();
         //System.out.println("d  = " + d);

         assertEquals("c == gcd(ac,bc)", c, d );
     }
 }


/**
 * Test recursive quotioent and remainder.
 * 
 */
 public void testRecursiveQR() {
     di = new BigInteger( 1 );
     dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),2,to);
     cfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),2-1,to);
     rfac = new GenPolynomialRing<GenPolynomial<BigInteger>>(cfac,1,to);

     for (int i = 0; i < 1; i++) {
         a = dfac.random(kl*(i+1),ll+i,el+i,q);
         a = ufd.basePrimitivePart(a).abs();

         c = dfac.random(kl*(i+1),ll+i,el+i,q);
         c = ufd.basePrimitivePart(a).abs();
         cr = ufd.recursive(rfac,c);

         c = cfac.random(kl*(i+1),ll+2*i,el+2*i,q);
         c = ufd.basePrimitivePart(c).abs();

         ar = ufd.recursive(rfac,a);
         System.out.println("ar = " + ar);
         System.out.println("a  = " + a);
         System.out.println("c  = " + c);

         assertTrue("length( c"+i+" ) <> 0", c.length() >= 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );

         br = ar.multiply(c);
         System.out.println("br = " + br);
         dr = ufd.recursivePseudoDivide(br,c);
         System.out.println("dr = " + dr);
         d = ufd.distribute(dfac,dr);
         d = ufd.basePrimitivePart(d).abs();
         System.out.println("d  = " + d);

         assertEquals("a == ac/c", a, d );

         System.out.println("cr = " + cr);
         br = ar.multiply(cr);
         System.out.println("br = " + br);
         dr = ufd.pseudoRemainder(br,cr);
         //d = ufd.basePrimitivePart(d).abs();
         System.out.println("dr = " + dr);
         d = ufd.distribute(dfac,dr);
         d = ufd.basePrimitivePart(d).abs();
         System.out.println("d  = " + d);

         assertTrue("rem(ac,c) == 0", d.isZERO() );

         /*
         b = a.multiply(c);
         //System.out.println("b  = " + b);
         d = ufd.basePseudoDivide(b,c);
         //System.out.println("d  = " + d);
         d = ufd.basePrimitivePart(d).abs();
         //System.out.println("d  = " + d);

         assertEquals("a == ac/c", a, d );
         */
     }
 }


/**
 * Test univariate quotient and remainder.
 * 
 */

 public void xtestUnivQuotRem() {

     dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),1,to);

     a = dfac.random(kl,ll,ll,q).monic();
     assertTrue("not isZERO( a )", !a.isZERO() );

     b = dfac.random(kl,ll,ll,q).monic();
     assertTrue("not isZERO( b )", !b.isZERO() );

     GenPolynomial<BigInteger> h = a;
     GenPolynomial<BigInteger> g = dfac.random(ll); //.monic();
     assertTrue("not isZERO( g )", !g.isZERO() );
     a = a.multiply(g);
     b = b.multiply(g);
     //System.out.println("a = " + a);
     //System.out.println("b = " + b);
     //System.out.println("g = " + g);

     GenPolynomial<BigInteger> r;
     r = ufd.basePseudoRemainder(b,a);
     //System.out.println("r = " + r);

     // not nice
     c = b;
     BigInteger lbc = a.leadingBaseCoefficient();
     while ( ! ufd.basePseudoRemainder(c.subtract(r),a).isZERO() ) {
         c = c.multiply( lbc );
     }
     d = c.subtract(r);
     //System.out.println("d = " + d);
     e = ufd.basePseudoRemainder(d,a);
     //System.out.println("e = " + e);
     assertTrue("b-r = q a", e.isZERO() );


     // pseudo gcd tests -------------------------------
     c = ufd.basePseudoGcd(a,b);
     //System.out.println("pseudoGcd = " + c);
     //System.out.println("pseudoGcd = " + ufd.basePseudoRemainder(c,g));
     assertTrue("a mod pseudoGcd(a,b) = 0", ufd.basePseudoRemainder(a,c).isZERO() );
     assertTrue("b mod pseudoGcd(a,b) = 0", ufd.basePseudoRemainder(b,c).isZERO() );
     assertTrue("g = pseudoGcd(a,b)", ufd.basePseudoRemainder(c,g).isZERO() );
 }



/**
 * Test bivariate quotient and remainder.
 * 
 */

 public void xtestQuotRem() {

     dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),2,to);

     a = dfac.random(kl,ll,ll,q).monic();
     assertTrue("not isZERO( a )", !a.isZERO() );

     b = dfac.random(kl,ll,ll,q).monic();
     assertTrue("not isZERO( b )", !b.isZERO() );

     GenPolynomial<BigInteger> h = a;
     GenPolynomial<BigInteger> g = dfac.random(ll); //.monic();
     assertTrue("not isZERO( g )", !g.isZERO() );

     System.out.println("a = " + a);
     System.out.println("b = " + b);
     System.out.println("g = " + g);
     a = a.multiply(g);
     b = b.multiply(g);
     System.out.println("ag = " + a);
     System.out.println("bg = " + b);

     BigInteger i = ufd.baseContent(a);
     System.out.println("bc a = " + i);
     i = ufd.baseContent(b);
     System.out.println("bc b = " + i);
     i = ufd.baseContent(g);
     System.out.println("bc g = " + i);

     a = ufd.basePrimitivePart(a);
     b = ufd.basePrimitivePart(b);
     g = ufd.basePrimitivePart(g);
     System.out.println("bp a = " + a);
     System.out.println("bp b = " + b);
     System.out.println("bp g = " + g);


     // pseudo gcd tests -------------------------------
     c = ufd.pseudoGcd(a,b);
     System.out.println("pseudoGcd = " + c);

     c = ufd.basePrimitivePart(c);
     System.out.println("bp c = " + c);

     // c = ufd.primitivePart(c);
     //System.out.println("pp pseudoGcd = " + c);
     //System.out.println("pseudoGcd = " + ufd.basePseudoRemainder(c,g));

     assertEquals("g == pseudoGcd(a,b)", g.abs(), c.abs() );
 }


}
