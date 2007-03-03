/*
 * $Id$
 */

package edu.jas.ufd;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigInteger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.TermOrder;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import static edu.jas.poly.PolyUtil.*;


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
 * Test base quotioent and remainder.
 * 
 */
 public void testBaseQR() {
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
 * Test base content and primitive part.
 * 
 */
 public void testBaseContentPP() {
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
 * Test base gcd.
 * 
 */
 public void testBaseGcd() {

     dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),1,to);

     for (int i = 0; i < 5; i++) {
         a = dfac.random(kl*(i+2),ll+2*i,el+2*i,q);
         b = dfac.random(kl*(i+2),ll+2*i,el+2*i,q);
         c = dfac.random(kl*(i+2),ll+2*i,el+2*i,q);
         //a = ufd.basePrimitivePart(a);
         //b = ufd.basePrimitivePart(b);
         //c = ufd.basePrimitivePart(c).abs();

         //System.out.println("a  = " + a);
         //System.out.println("b  = " + b);
         //System.out.println("c  = " + c);

         assertTrue("length( c"+i+" ) <> 0", c.length() >= 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );
         
         a = a.multiply(c);
         b = b.multiply(c);

         d = ufd.basePseudoGcd(a,b);
         e = ufd.basePseudoRemainder(d,c);
         //d = ufd.basePrimitivePart(d).abs();
         //System.out.println("d  = " + d);

         assertTrue("c | gcd(ac,bc)", e.isZERO() );
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

     for (int i = 0; i < 5; i++) {
         a = dfac.random(kl*(i+1),ll+i,el+i,q);
         a = ufd.basePrimitivePart(a).abs();

         c = dfac.random(kl*(i+1),ll+i,el+i,q);
         c = ufd.basePrimitivePart(a).abs();
         cr = recursive(rfac,c);

         c = cfac.random(kl*(i+1),ll+2*i,el+2*i,q);
         c = ufd.basePrimitivePart(c).abs();

         ar = recursive(rfac,a);
         //System.out.println("ar = " + ar);
         //System.out.println("a  = " + a);
         //System.out.println("c  = " + c);

         assertTrue("length( c"+i+" ) <> 0", c.length() >= 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );

         br = ar.multiply(c);
         //System.out.println("br = " + br);
         dr = ufd.recursivePseudoDivide(br,c);
         //System.out.println("dr = " + dr);
         d = distribute(dfac,dr);
         d = ufd.basePrimitivePart(d).abs();
         //System.out.println("d  = " + d);

         assertEquals("a == ac/c", a, d );

         //System.out.println("cr = " + cr);
         br = ar.multiply(cr);
         //System.out.println("br = " + br);
         dr = ufd.recursivePseudoRemainder(br,cr);
         //System.out.println("dr = " + dr);
         d = distribute(dfac,dr);
         d = ufd.basePrimitivePart(d).abs();
         //System.out.println("d  = " + d);

         assertTrue("rem(ac,c) == 0", d.isZERO() );

         br = ar.multiply(c);
         //System.out.println("br  = " + br);
         dr = ufd.recursivePseudoDivide(br,c);
         //System.out.println("dr  = " + dr);
         d = distribute(dfac,dr);
         d = ufd.basePrimitivePart(d).abs();
         //System.out.println("d  = " + d);

         assertEquals("a == ac/c", a, d );
     }
 }


/**
 * Test recursive content and primitive part.
 * 
 */
 public void testRecursiveContentPP() {
     di = new BigInteger( 1 );
     dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),2,to);
     cfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),2-1,to);
     rfac = new GenPolynomialRing<GenPolynomial<BigInteger>>(cfac,1,to);

     for (int i = 0; i < 3; i++) {
         cr = rfac.random(kl*(i+2),ll+2*i,el+i,q);
         //System.out.println("cr = " + cr);

         assertTrue("length( c"+i+" ) <> 0", cr.length() >= 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );
         
         c = ufd.recursiveContent(cr);
         dr = ufd.recursivePrimitivePart(cr);
         //System.out.println("c  = " + c);
         //System.out.println("dr = " + dr);

         ar = dr.multiply(c);
         assertEquals("c == cont(c)pp(c)", cr, ar );
     }
 }


/**
 * Test recursive gcd.
 * 
 */
 public void testRecursiveGCD() {
     di = new BigInteger( 1 );
     dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),2,to);
     cfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),2-1,to);
     rfac = new GenPolynomialRing<GenPolynomial<BigInteger>>(cfac,1,to);

     for (int i = 0; i < 2; i++) {
         ar = rfac.random(kl,ll,el+i,q);
         br = rfac.random(kl,ll,el,q);
         cr = rfac.random(kl,ll,el,q);
         //System.out.println("ar = " + ar);
         //System.out.println("br = " + br);
         //System.out.println("cr = " + cr);

         assertTrue("length( c"+i+" ) <> 0", cr.length() >= 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );
         
         ar = ar.multiply(cr);
         br = br.multiply(cr);
         //System.out.println("ar = " + ar);
         //System.out.println("br = " + br);

         dr = ufd.recursivePseudoGcd(ar,br);
         //System.out.println("dr = " + dr);

         er = ufd.recursivePseudoRemainder(dr,cr);
         //System.out.println("er = " + er);

         assertTrue("c | gcd(ac,bc)", er.isZERO() );
     }
 }


/**
 * Test content and primitive part.
 * 
 */
 public void xtestContentPP() {
     dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),2,to);

     for (int i = 0; i < 3; i++) {
         c = dfac.random(kl*(i+2),ll+2*i,el+i,q);
         //System.out.println("cr = " + cr);

         assertTrue("length( c"+i+" ) <> 0", c.length() >= 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );
         
         a = ufd.content(c);
         e = a.extend(dfac,0,0L);
         b = ufd.primitivePart(c);
         System.out.println("c  = " + c);
         System.out.println("a  = " + a);
         System.out.println("e  = " + e);
         System.out.println("b  = " + b);

         d = e.multiply(b);
         assertEquals("c == cont(c)pp(c)", d, c );
     }
 }


}
