/*
 * $Id$
 */

package edu.jas.ufd;

import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;

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

   GreatestCommonDivisorAbstract<BigInteger> ufd 
          = new GreatestCommonDivisorSimple<BigInteger>();

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
   int kl = 4;
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
         //a = ufd.basePrimitivePart(a).abs();
         //c = ufd.basePrimitivePart(c);
         ci = di.random(kl*(i+2));
         ci = ci.sum(di.getONE());

         //System.out.println("a  = " + a);
         //System.out.println("c  = " + c);
         //System.out.println("ci = " + ci);

         assertTrue("length( c"+i+" ) <> 0", c.length() > 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );
         
         b = a.multiply(c);
         //System.out.println("b  = " + b);
         d = ufd.basePseudoRemainder(b,c);
         //System.out.println("d  = " + d);

         assertTrue("rem(ac,c) == 0", d.isZERO() );

         b = a.multiply(ci);
         //System.out.println("b  = " + b);
         d = b.divide(ci); 
         //System.out.println("d  = " + d);

         assertEquals("a == ac/c", a, d );

         b = a.multiply(c);
         //System.out.println("b  = " + b);
         d = ufd.basePseudoDivide(b,c);
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

         assertTrue("length( c"+i+" ) <> 0", c.length() > 0);
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

         assertTrue("length( c"+i+" ) <> 0", c.length() > 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );
         
         a = a.multiply(c);
         b = b.multiply(c);

         d = ufd.baseGcd(a,b);
         e = ufd.basePseudoRemainder(d,c);
         //System.out.println("d  = " + d);

         assertTrue("c | gcd(ac,bc) " + e, e.isZERO() );
     }
 }


/**
 * Test base squarefree.
 * 
 */
 public void testBaseSquarefree() {

     dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),1,to);

     a = dfac.random(kl,ll,el+2,q);
     b = dfac.random(kl,ll,el+2,q);
     c = dfac.random(kl,ll,el,q);
     //System.out.println("a  = " + a);
     //System.out.println("b  = " + b);
     //System.out.println("c  = " + c);

     assertTrue("length( a ) <> 0", a.length() > 0);
     assertTrue("length( b ) <> 0", b.length() > 0);
     assertTrue("length( c ) <> 0", c.length() > 0);
     //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
     //assertTrue(" not isONE( c"+i+" )", !c.isONE() );
         
     d = a.multiply(a).multiply(b).multiply(b).multiply(b).multiply(c);
     c = a.multiply(b).multiply(c);
     //System.out.println("d  = " + d);
     //c = ufd.basePrimitivePart(c); // if  a != b != c
     c = ufd.baseSquarefreePart(c);

     d = ufd.baseSquarefreePart(d);
     //System.out.println("c  = " + c);
     //System.out.println("d  = " + d);

     e = ufd.basePseudoRemainder(d,c);
     //e = ufd.basePseudoRemainder(c,d);
     //System.out.println("e  = " + e);

     assertTrue("abc | squarefree(aabbbc) " + e, e.isZERO() );
 }


/**
 * Test base squarefree factors.
 * 
 */
 public void testBaseSquarefreeFactors() {

     dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),1,to);

     a = dfac.random(kl,ll,el+3,q);
     b = dfac.random(kl,ll,el+3,q);
     c = dfac.random(kl,ll,el+2,q);
     //System.out.println("a  = " + a);
     //System.out.println("b  = " + b);
     //System.out.println("c  = " + c);

     assertTrue("length( a ) <> 0", a.length() > 0);
         
     d = a.multiply(a).multiply(b).multiply(b).multiply(b).multiply(c);
     c = a.multiply(b).multiply(c);
     //System.out.println("d  = " + d);
     //System.out.println("c  = " + c);
     c = ufd.basePrimitivePart(c);
     d = ufd.basePrimitivePart(d);

     Map<Integer,GenPolynomial<BigInteger>> sfactors;
     sfactors = ufd.baseSquarefreeFactors(d);
     //System.out.println("sfactors = " + sfactors);

     e = dfac.getONE();
     for ( Map.Entry<Integer,GenPolynomial<BigInteger>> m : sfactors.entrySet() ) {
         GenPolynomial<BigInteger> p = m.getValue();
         int j = m.getKey();
         for ( int i = 0; i < j; i++ ) {
             e = e.multiply(p);
         }
     }
     //System.out.println("e  = " + e);
     e = ufd.basePseudoRemainder(d,e);
     assertTrue("PROD squarefreefactors(aabbbc) | aabbbc " + e, e.isZERO() );

     e = dfac.getONE();
     for ( GenPolynomial<BigInteger> p : sfactors.values() ) {
         e = e.multiply(p);
     }
     //System.out.println("e  = " + e);

     //e = ufd.basePseudoRemainder(e,c);
     e = ufd.basePseudoRemainder(c,e);
     //System.out.println("e  = " + e);

     assertTrue("squarefreefactors(aabbbc) | abc" + e, e.isZERO() );
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
         //System.out.println("cr = " + cr);

         assertTrue("length( cr"+i+" ) <> 0", cr.length() > 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );


         br = ar.multiply(cr);
         //System.out.println("br = " + br);
         dr = ufd.recursivePseudoRemainder(br,cr);
         //System.out.println("dr = " + dr);
         d = distribute(dfac,dr);
         //System.out.println("d  = " + d);

         assertTrue("rem(ac,c) == 0", d.isZERO() );

         br = ar.multiply(c);
         //System.out.println("br = " + br);
         dr = ufd.recursiveDivide(br,c);
         //System.out.println("dr = " + dr);
         d = distribute(dfac,dr);
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

         assertTrue("length( cr"+i+" ) <> 0", cr.length() > 0);
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
 * Test recursive content and primitive part, modular.
 * 
 */
 public void testRecursiveContentPPmodular() {
     ModInteger mi = new ModInteger(19,1);

     GenPolynomialRing<ModInteger> dfac;
     GenPolynomialRing<ModInteger> cfac;
     GenPolynomialRing<GenPolynomial<ModInteger>> rfac;
     dfac = new GenPolynomialRing<ModInteger>(mi,2,to);
     cfac = new GenPolynomialRing<ModInteger>(mi,2-1,to);
     rfac = new GenPolynomialRing<GenPolynomial<ModInteger>>(cfac,1,to);

     GreatestCommonDivisorAbstract<ModInteger> ufd 
          = new GreatestCommonDivisorSimple<ModInteger>();

     GenPolynomial<GenPolynomial<ModInteger>> ar, br, cr, dr, er, ac, bc;
     GenPolynomial<ModInteger> a,b,c;

     for (int i = 0; i < 1; i++) {
         a = cfac.random(kl,ll+2*i,el+i,q).monic();
         cr = rfac.random(kl*(i+2),ll+2*i,el+i,q);
         cr = monic( cr );
         //System.out.println("a  = " + a);
         //System.out.println("cr = " + cr);
           //a = ufd.basePrimitivePart(a);
           //b = distribute(dfac,cr);
           //b = ufd.basePrimitivePart(b);
           //cr = recursive(rfac,b);
           //System.out.println("a  = " + a);
           //System.out.println("cr = " + cr);

         cr = cr.multiply(a);
         //System.out.println("cr = " + cr);

         assertTrue("length( cr"+i+" ) <> 0", cr.length() > 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );
         
         c = ufd.recursiveContent(cr).monic();
         dr = ufd.recursivePrimitivePart(cr);
         dr = monic( dr );
         //System.out.println("c  = " + c);
         //System.out.println("dr = " + dr);

         //System.out.println("monic(a) = " + a.monic());
         //System.out.println("monic(c) = " + c.monic());

         ar = dr.multiply(c);
         //System.out.println("ar = " + ar);
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

         assertTrue("length( cr"+i+" ) <> 0", cr.length() > 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );
         
         ar = ar.multiply(cr);
         br = br.multiply(cr);
         //System.out.println("ar = " + ar);
         //System.out.println("br = " + br);

         dr = ufd.recursiveGcd(ar,br);
         //System.out.println("dr = " + dr);

         er = ufd.recursivePseudoRemainder(dr,cr);
         //System.out.println("er = " + er);

         assertTrue("c | gcd(ac,bc) " + er, er.isZERO() );
     }
 }


/**
 * Test recursive squarefree.
 * 
 */
 public void testRecursiveSquarefree() {
     cfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),2-1,to);
     rfac = new GenPolynomialRing<GenPolynomial<BigInteger>>(cfac,1,to);

     ar = rfac.random(kl,ll,2,q);
     br = rfac.random(kl,ll,2,q);
     cr = rfac.random(kl,ll,2,q);
     //System.out.println("ar = " + ar);
     //System.out.println("br = " + br);
     //System.out.println("cr = " + cr);

     assertTrue("length( cr ) <> 0", cr.length() > 0);
     //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
     //assertTrue(" not isONE( c"+i+" )", !c.isONE() );
         

     dr = ar.multiply(ar).multiply(br).multiply(br).multiply(cr);
     cr = ar.multiply(br).multiply(cr);
     //System.out.println("dr  = " + dr);
     cr = ufd.recursivePrimitivePart(cr);

     dr = ufd.recursiveSquarefreePart(dr);
     //System.out.println("cr  = " + cr);
     //System.out.println("dr  = " + dr);

     er = ufd.recursivePseudoRemainder(dr,cr);
     //System.out.println("er  = " + er);

     assertTrue("abc | squarefree(aabbc) " + er, er.isZERO() );
 }


/**
 * Test recursive squarefree factors.
 * 
 */
 public void testRecursiveSquarefreeFactors() {

     cfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),2-1,to);
     rfac = new GenPolynomialRing<GenPolynomial<BigInteger>>(cfac,1,to);

     ar = rfac.random(3,3,2,q);
     br = rfac.random(3,3,2,q);
     cr = rfac.random(3,3,2,q);
     //System.out.println("ar = " + ar);
     //System.out.println("br = " + br);
     //System.out.println("cr = " + cr);

     assertTrue("length( cr ) <> 0", cr.length() > 0);
         
     dr = ar.multiply(ar).multiply(br).multiply(br).multiply(cr);
     cr = ar.multiply(br).multiply(cr);
     //System.out.println("d  = " + d);
     //System.out.println("cr  = " + cr);
     cr = ufd.recursivePrimitivePart(cr);
     dr = ufd.recursivePrimitivePart(dr);

     Map<Integer,GenPolynomial<GenPolynomial<BigInteger>>> sfactors;
     sfactors = ufd.recursiveSquarefreeFactors(dr);
     //System.out.println("sfactors = " + sfactors);

     er = rfac.getONE();
     for ( Map.Entry<Integer,GenPolynomial<GenPolynomial<BigInteger>>> m : sfactors.entrySet() ) {
         GenPolynomial<GenPolynomial<BigInteger>> p = m.getValue();
         int j = m.getKey();
         for ( int i = 0; i < j; i++ ) {
             er = er.multiply(p);
         }
     }
     //System.out.println("er  = " + er);
     er = ufd.recursivePseudoRemainder(dr,er);
     assertTrue("PROD squarefreefactors(aabbbc) | aabbbc " + er, er.isZERO() );


     er = rfac.getONE();
     for ( GenPolynomial<GenPolynomial<BigInteger>> p : sfactors.values() ) {
         er = er.multiply(p);
     }
     //System.out.println("er  = " + er);

     //er = ufd.recursivePseudoRemainder(er,cr);
     er = ufd.recursivePseudoRemainder(cr,er);
     //System.out.println("er  = " + er);

     assertTrue("squarefreefactors(aabbc) | abc " + er, er.isZERO() );
 }


/**
 * Test content and primitive part.
 * 
 */
 public void testContentPP() {
     dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),3,to);

     for (int i = 0; i < 3; i++) {
         c = dfac.random(kl*(i+2),ll+2*i,el+i,q);
         //System.out.println("cr = " + cr);

         assertTrue("length( c"+i+" ) <> 0", c.length() > 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );
         
         a = ufd.content(c);
         e = a.extend(dfac,0,0L);
         b = ufd.primitivePart(c);
         //System.out.println("c  = " + c);
         //System.out.println("a  = " + a);
         //System.out.println("e  = " + e);
         //System.out.println("b  = " + b);

         d = e.multiply(b);
         assertEquals("c == cont(c)pp(c)", d, c );
     }
 }


/**
 * Test gcd 3 variables.
 * 
 */
 public void testGCD3() {
     dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),3,to);

     for (int i = 0; i < 4; i++) {
         a = dfac.random(kl,ll,el+i,q);
         b = dfac.random(kl,ll,el,q);
         c = dfac.random(kl,ll,el,q);
         //System.out.println("a = " + a);
         //System.out.println("b = " + b);
         //System.out.println("c = " + c);

         assertTrue("length( c"+i+" ) <> 0", c.length() > 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );
         
         a = a.multiply(c);
         b = b.multiply(c);
         //System.out.println("a = " + a);
         //System.out.println("b = " + b);

         d = ufd.gcd(a,b);
         //System.out.println("d = " + d);

         e = ufd.basePseudoRemainder(d,c);
         //System.out.println("e = " + e);

         assertTrue("c | gcd(ac,bc) " + e, e.isZERO() );
     }
 }


/**
 * Test gcd.
 * 
 */
 public void testGCD() {
     // dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),3,to);

     for (int i = 0; i < 2; i++) {
         a = dfac.random(kl,ll,el,q);
         b = dfac.random(kl,ll,el,q);
         c = dfac.random(kl,ll,el,q);
         //System.out.println("a = " + a);
         //System.out.println("b = " + b);
         //System.out.println("c = " + c);

         assertTrue("length( c"+i+" ) <> 0", c.length() > 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );
         
         a = a.multiply(c);
         b = b.multiply(c);
         //System.out.println("a = " + a);
         //System.out.println("b = " + b);

         d = ufd.gcd(a,b);
         //System.out.println("d = " + d);

         e = ufd.basePseudoRemainder(d,c);
         //System.out.println("e = " + e);

         assertTrue("c | gcd(ac,bc) " + e, e.isZERO() );
     }
 }


/**
 * Test gcd field coefficients.
 * 
 */
 public void testGCDfield() {
     GenPolynomialRing<BigRational> dfac;
     dfac = new GenPolynomialRing<BigRational>(new BigRational(1),3,to);

     GreatestCommonDivisorAbstract<BigRational> ufd 
          = new GreatestCommonDivisorSimple<BigRational>();

     GenPolynomial<BigRational> a, b, c, d, e;

     for (int i = 0; i < 1; i++) {
         a = dfac.random(kl,ll,el,q);
         b = dfac.random(kl,ll,el,q);
         c = dfac.random(kl,ll,el,q);
         //System.out.println("a = " + a);
         //System.out.println("b = " + b);
         //System.out.println("c = " + c);

         assertTrue("length( c"+i+" ) <> 0", c.length() > 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );
         
         a = a.multiply(c);
         b = b.multiply(c);
         //System.out.println("a = " + a);
         //System.out.println("b = " + b);

         d = ufd.gcd(a,b);
         //System.out.println("d = " + d);

         e = ufd.basePseudoRemainder(d,c);
         //System.out.println("e = " + e);

         assertTrue("c | gcd(ac,bc) " + e, e.isZERO() );
     }
 }


/**
 * Test base gcd modular coefficients. 
 * 
 */
 public void testGCDbaseMmodular() {
     ModInteger mi = new ModInteger(19,1);

     GenPolynomialRing<ModInteger> dfac;
     dfac = new GenPolynomialRing<ModInteger>(mi,1,to);

     GreatestCommonDivisorAbstract<ModInteger> ufd 
          = new GreatestCommonDivisorSimple<ModInteger>();

     GenPolynomial<ModInteger> a, b, c, d, e, ac, bc;

     for (int i = 0; i < 1; i++) {
         a = dfac.random(kl,ll,el+3+i,q).monic();
         b = dfac.random(kl,ll,el+3+i,q).monic();
         c = dfac.random(kl,ll,el+3+i,q).monic();
         //System.out.println("a = " + a);
         //System.out.println("b = " + b);
         //System.out.println("c = " + c);

         assertTrue("length( c"+i+" ) <> 0", c.length() > 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );
         
         ac = a.multiply(c);
         bc = b.multiply(c);
         //System.out.println("ac = " + ac);
         //System.out.println("bc = " + bc);

         //e = ufd.basePseudoRemainder(ac,c);
         //System.out.println("ac/c a = 0 " + e);
         //assertTrue("ac/c-a != 0 " + e, e.isZERO() );
         //e = ufd.basePseudoRemainder(bc,c);
         //System.out.println("bc/c-b = 0 " + e);
         //assertTrue("bc/c-b != 0 " + e, e.isZERO() );

         d = ufd.baseGcd(ac,bc);
         d = d.monic(); // not required
         //System.out.println("d = " + d);

         e = ufd.basePseudoRemainder(d,c);
         //System.out.println("e = " + e);

         assertTrue("c | gcd(ac,bc) " + e, e.isZERO() );
     }
 }


/**
 * Test recursive modular gcd. 
 * 
 */
 public void testRecursiveModularGCD() {
     ModInteger mi = new ModInteger(19,1);

     GenPolynomialRing<ModInteger> dfac;
     GenPolynomialRing<ModInteger> cfac;
     GenPolynomialRing<GenPolynomial<ModInteger>> rfac;
     dfac = new GenPolynomialRing<ModInteger>(mi,2,to);
     cfac = new GenPolynomialRing<ModInteger>(mi,2-1,to);
     rfac = new GenPolynomialRing<GenPolynomial<ModInteger>>(cfac,1,to);

     GreatestCommonDivisorAbstract<ModInteger> ufd 
          = new GreatestCommonDivisorSimple<ModInteger>();

     GenPolynomial<GenPolynomial<ModInteger>> ar, br, cr, dr, er, ac, bc;

     for (int i = 0; i < 1; i++) {
         ar = rfac.random(kl,2,el+2,q);
         br = rfac.random(kl,2,el+2,q);
         cr = rfac.random(kl,2,el+2,q);
         ar = monic( ar );
         br = monic( br );
         cr = monic( cr );
         //System.out.println("ar = " + ar);
         //System.out.println("br = " + br);
         //System.out.println("cr = " + cr);

         assertTrue("length( cr"+i+" ) <> 0", cr.length() > 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );
         
         ac = ar.multiply(cr);
         bc = br.multiply(cr);
         //System.out.println("ac = " + ac);
         //System.out.println("bc = " + bc);

          //er = ufd.recursivePseudoRemainder(ac,cr);
          //System.out.println("ac/c-a = 0 " + er);
          //assertTrue("ac/c-a != 0 " + er, er.isZERO() );
          //er = ufd.recursivePseudoRemainder(bc,cr);
          //System.out.println("bc/c-b = 0 " + er);
          //assertTrue("bc/c-b != 0 " + er, er.isZERO() );

         dr = ufd.recursiveGcd(ac,bc);
         dr = monic( dr );
         //System.out.println("cr = " + cr);
         //System.out.println("dr = " + dr);

         er = ufd.recursivePseudoRemainder(dr,cr);
         //System.out.println("er = " + er);

         assertTrue("c | gcd(ac,bc) " + er, er.isZERO() );
     }
 }


/**
 * Test gcd modular coefficients.
 * 
 */
 public void testGCDmodular() {
     ModInteger mi = new ModInteger(19,1);

     GenPolynomialRing<ModInteger> dfac;
     dfac = new GenPolynomialRing<ModInteger>(mi,3,to);

     GreatestCommonDivisorAbstract<ModInteger> ufd 
          = new GreatestCommonDivisorSimple<ModInteger>();

     GenPolynomial<ModInteger> a, b, c, d, e, ac, bc;

     for (int i = 0; i < 1; i++) {
         a = dfac.random(kl,ll,el+i,q).monic();
         b = dfac.random(kl,ll,el+i,q).monic();
         c = dfac.random(kl,ll,el+i,q).monic();
         //System.out.println("a = " + a);
         //System.out.println("b = " + b);
         //System.out.println("c = " + c);

         assertTrue("length( c"+i+" ) <> 0", c.length() > 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );
         
         ac = a.multiply(c);
         bc = b.multiply(c);
         //System.out.println("ac = " + ac);
         //System.out.println("bc = " + bc);

         //e = ufd.basePseudoRemainder(ac,c);
         //System.out.println("ac/c-a = 0 " + e);
         //assertTrue("ac/c-a != 0 " + e, e.isZERO() );
         //e = ufd.basePseudoRemainder(bc,c);
         //System.out.println("bc/c-b = 0 " + e);
         //assertTrue("bc/c-b != 0 " + e, e.isZERO() );

         d = ufd.gcd(ac,bc);
         //System.out.println("d = " + d);

         e = ufd.basePseudoRemainder(d,c);
         //System.out.println("e = " + e);
         //System.out.println("c = " + c);

         assertTrue("c | gcd(ac,bc) " + e, e.isZERO() );
     }
 }


/**
 * Test lcm.
 * 
 */
 public void testLCM() {
     dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),3,to);

     for (int i = 0; i < 1; i++) {
         a = dfac.random(kl,ll,el,q);
         b = dfac.random(kl,ll,el,q);
         c = dfac.random(kl,3,2,q);
         //System.out.println("a = " + a);
         //System.out.println("b = " + b);
         //System.out.println("c = " + c);

         assertTrue("length( a"+i+" ) <> 0", a.length() > 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );

         a = a.multiply(c);
         b = b.multiply(c);
         
         c = ufd.gcd(a,b);
         //System.out.println("c = " + c);

         d = ufd.lcm(a,b);
         //System.out.println("d = " + d);

         e = c.multiply(d);
         //System.out.println("e = " + e);
         c = a.multiply(b);
         //System.out.println("c = " + c);

         assertEquals("ab == gcd(a,b)lcm(ab)", c, e );
     }
 }


/**
 * Test squarefree.
 * 
 */
 public void testSquarefree() {

     dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),3,to);

     a = dfac.random(kl,ll,2,q);
     b = dfac.random(kl,ll,2,q);
     c = dfac.random(kl,ll,2,q);
     //System.out.println("a  = " + a);
     //System.out.println("b  = " + b);
     //System.out.println("c  = " + c);

     assertTrue("length( c ) <> 0", c.length() > 0);
     //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
     //assertTrue(" not isONE( c"+i+" )", !c.isONE() );
         
     d = a.multiply(a).multiply(b).multiply(b).multiply(c);
     c = a.multiply(b).multiply(c);
     //System.out.println("d  = " + d);
     c = ufd.primitivePart(c); // required here

     d = ufd.squarefreePart(d);
     //System.out.println("c  = " + c);
     //System.out.println("d  = " + d);

     e = ufd.basePseudoRemainder(d,c);
     //e = ufd.basePseudoRemainder(c,d);
     //System.out.println("e  = " + e);

     assertTrue("abc | squarefree(aabbc) " + e, e.isZERO() );
 }


/**
 * Test squarefree factors.
 * 
 */
 public void testSquarefreeFactors() {

     dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1),3,to);

     a = dfac.random(kl,3,2,q);
     b = dfac.random(kl,3,2,q);
     c = dfac.random(kl,3,2,q);
     //System.out.println("a  = " + a);
     //System.out.println("b  = " + b);
     //System.out.println("c  = " + c);

     assertTrue("length( a ) <> 0", a.length() > 0);
         
     d = a.multiply(a).multiply(b).multiply(b).multiply(b).multiply(c);
     c = a.multiply(b).multiply(c);
     //System.out.println("d  = " + d);
     //System.out.println("c  = " + c);
     c = ufd.primitivePart(c);
     d = ufd.primitivePart(d);

     Map<Integer,GenPolynomial<BigInteger>> sfactors;
     sfactors = ufd.squarefreeFactors(d);
     //System.out.println("sfactors = " + sfactors);

     e = dfac.getONE();
     for ( Map.Entry<Integer,GenPolynomial<BigInteger>> m : sfactors.entrySet() ) {
         GenPolynomial<BigInteger> p = m.getValue();
         int j = m.getKey();
         for ( int i = 0; i < j; i++ ) {
             e = e.multiply(p);
         }
     }
     //System.out.println("e  = " + e);
     e = ufd.basePseudoRemainder(d,e);
     assertTrue("PROD squarefreefactors(aabbbc) | aabbbc " + e, e.isZERO() );

     e = dfac.getONE();
     for ( GenPolynomial<BigInteger> p : sfactors.values() ) {
         e = e.multiply(p);
     }
     //System.out.println("c  = " + c);
     //System.out.println("e  = " + e);

     //e = ufd.basePseudoRemainder(e,c);
     e = ufd.basePseudoRemainder(c,e);
     //System.out.println("e  = " + e);

     assertTrue("squarefreefactors(aabbbc) | abc " + e, e.isZERO() );
 }

}
