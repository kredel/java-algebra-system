/*
 * $Id$
 */

package edu.jas.poly;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.BigComplex;

import edu.jas.poly.TermOrder;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;


/**
 * PolyUtil Test using JUnit.
 * @author Heinz Kredel.
 */

public class PolyUtilTest extends TestCase {

/**
 * main.
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>PolyUtilTest</CODE> object.
 * @param name String.
 */
   public PolyUtilTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(PolyUtilTest.class);
     return suite;
   }

   //private final static int bitlen = 100;

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


   protected static java.math.BigInteger getPrime1() {
       long prime = 2; //2^60-93; // 2^30-35; //19; knuth (2,390)
       for ( int i = 1; i < 60; i++ ) {
           prime *= 2;
       }
       prime -= 93;
       //prime = 37;
       //System.out.println("p1 = " + prime);
       return new java.math.BigInteger(""+prime);
   }

   protected static java.math.BigInteger getPrime2() {
       long prime = 2; //2^60-93; // 2^30-35; //19; knuth (2,390)
       for ( int i = 1; i < 30; i++ ) {
           prime *= 2;
       }
       prime -= 35;
       //prime = 19;
       //System.out.println("p1 = " + prime);
       return new java.math.BigInteger(""+prime);
   }


/**
 * Test recursive <--> distributive conversion.
 * 
 */
 public void testConversion() {
     c = dfac.getONE();
     assertTrue("length( c ) = 1", c.length() == 1);
     assertTrue("isZERO( c )", !c.isZERO() );
     assertTrue("isONE( c )", c.isONE() );

     cr = PolyUtil.recursive(rfac,c);
     a = PolyUtil.distribute(dfac,cr);
     assertEquals("c == dist(rec(c))", c, a );

     d = dfac.getZERO();
     assertTrue("length( d ) = 0", d.length() == 0);
     assertTrue("isZERO( d )", d.isZERO() );
     assertTrue("isONE( d )", !d.isONE() );

     dr = PolyUtil.recursive(rfac,d);
     b = PolyUtil.distribute(dfac,dr);
     assertEquals("d == dist(rec(d))", d, b );
 }


/**
 * Test random recursive <--> distributive conversion.
 * 
 */
 public void testRandomConversion() {
     for (int i = 0; i < 7; i++) {
         c = dfac.random(kl*(i+2),ll+2*i,el+i,q);

         assertTrue("length( c"+i+" ) <> 0", c.length() >= 0);
         assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         assertTrue(" not isONE( c"+i+" )", !c.isONE() );

         cr = PolyUtil.recursive(rfac,c);
         a = PolyUtil.distribute(dfac,cr);
         //System.out.println("c   = " + c);
         //System.out.println("cr  = " + cr);
         //System.out.println("crd = " + a);

         assertEquals("c == dist(rec(c))", c, a );
     }
 }


/**
 * Test random rational <--> integer conversion.
 * 
 */
 public void testRationalConversion() {
     GenPolynomialRing<BigRational> rfac
         = new GenPolynomialRing<BigRational>(new BigRational(1),rl,to);

     GenPolynomial<BigRational> ar;
     GenPolynomial<BigRational> br;

     for (int i = 0; i < 3; i++) {
         c = dfac.random(kl*(i+2),ll*(i+1),el+i,q).abs();
         //c = c.multiply( new BigInteger(99) ); // fails, since not primitive
         //c = GreatestCommonDivisor.primitivePart(c);

         assertTrue("length( c"+i+" ) <> 0", c.length() >= 0);
         assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         assertTrue(" not isONE( c"+i+" )", !c.isONE() );

         ar = PolyUtil.<BigRational>fromIntegerCoefficients(rfac,c);
         br = ar.monic();
         a = PolyUtil.integerFromRationalCoefficients(dfac,br);
         //System.out.println("c   = " + c);
         //System.out.println("ar  = " + ar);
         //System.out.println("br  = " + br);
         //System.out.println("crd = " + a);

         assertEquals("c == integer(rational(c))", c, a );
     }
 }


/**
 * Test random modular <--> integer conversion.
 * 
 */
 public void testModularConversion() {
     ModInteger pm = new ModInteger(getPrime1(),1);
     GenPolynomialRing<ModInteger> mfac
         = new GenPolynomialRing<ModInteger>(pm,rl,to);

     GenPolynomial<ModInteger> ar;
     GenPolynomial<ModInteger> br;

     for (int i = 0; i < 3; i++) {
         c = dfac.random(kl*(i+2),ll*(i+1),el+i,q).abs();
         //c = c.multiply( new BigInteger(99) ); // fails, since not primitive
         //c = GreatestCommonDivisor.primitivePart(c);

         assertTrue("length( c"+i+" ) <> 0", c.length() >= 0);
         assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         assertTrue(" not isONE( c"+i+" )", !c.isONE() );

         ar = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac,c);
         a = PolyUtil.integerFromModularCoefficients(dfac,ar);
         //System.out.println("c   = " + c);
         //System.out.println("ar  = " + ar);
         //System.out.println("crd = " + a);

         assertEquals("c == integer(modular(c))", c, a );
     }
 }


/**
 * Test chinese remainder.
 * 
 */
 public void testChineseRemainder() {
     java.math.BigInteger p1 = getPrime1();
     java.math.BigInteger p2 = getPrime2();
     java.math.BigInteger p12 = p1.multiply(p2);

     ModInteger pm1 = new ModInteger(p1,1);
     GenPolynomialRing<ModInteger> mfac1
         = new GenPolynomialRing<ModInteger>(pm1,rl,to);

     ModInteger pm2 = new ModInteger(p2,1);
     GenPolynomialRing<ModInteger> mfac2
         = new GenPolynomialRing<ModInteger>(pm2,rl,to);

     ModInteger pm12 = new ModInteger(p12,1);
     GenPolynomialRing<ModInteger> mfac
         = new GenPolynomialRing<ModInteger>(pm12,rl,to);

     ModInteger di = new ModInteger(p2,p1);
     di = di.inverse();
     //System.out.println("di = " + di);

     GenPolynomial<ModInteger> am;
     GenPolynomial<ModInteger> bm;
     GenPolynomial<ModInteger> cm;

     for (int i = 0; i < 3; i++) {
         c = cfac.random( (59+29)/2, ll*(i+1), el+i, q);
         //c = c.multiply( new BigInteger(99) ); // fails, since not primitive
         //c = GreatestCommonDivisor.primitivePart(c);

         assertTrue("length( c"+i+" ) <> 0", c.length() >= 0);
         assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         assertTrue(" not isONE( c"+i+" )", !c.isONE() );

         am = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac1,c);
         bm = PolyUtil.<ModInteger>fromIntegerCoefficients(mfac2,c);

         cm = PolyUtil.chineseRemainder(mfac,am,di,bm);
         a = PolyUtil.integerFromModularCoefficients(dfac,cm);

         //System.out.println("c  = " + c);
         //System.out.println("am = " + am);
         //System.out.println("bm = " + bm);
         //System.out.println("cm = " + cm);
         //System.out.println("a  = " + a);

         assertEquals("cra(c mod p1,c mod p2) = c",c,a);
     }
 }


/**
 * Test complex conversion.
 * 
 */
 public void testComplexConversion() {
     BigRational rf = new BigRational(1);
     GenPolynomialRing<BigRational> rfac
         = new GenPolynomialRing<BigRational>(rf,rl,to);

     BigComplex cf = new BigComplex(1);
     GenPolynomialRing<BigComplex> cfac
         = new GenPolynomialRing<BigComplex>(cf,rl,to);

     BigComplex imag = BigComplex.I;

     GenPolynomial<BigRational> rp;
     GenPolynomial<BigRational> ip;
     GenPolynomial<BigComplex> crp;
     GenPolynomial<BigComplex> cip;
     GenPolynomial<BigComplex> cp;
     GenPolynomial<BigComplex> ap;

     for (int i = 0; i < 3; i++) {
         cp = cfac.random( kl+2*i, ll*(i+1), el+i, q);
 
         assertTrue("length( c"+i+" ) <> 0", cp.length() >= 0);
         assertTrue(" not isZERO( c"+i+" )", !cp.isZERO() );
         assertTrue(" not isONE( c"+i+" )", !cp.isONE() );

         rp = PolyUtil.realPart(rfac,cp);
         ip = PolyUtil.imaginaryPart(rfac,cp);

         crp = PolyUtil.complexFromRational(cfac,rp);
         cip = PolyUtil.complexFromRational(cfac,ip);

         ap = crp.sum( cip.multiply( imag ) );

         //System.out.println("cp = " + cp);
         //System.out.println("rp = " + rp);
         //System.out.println("ip = " + ip);
         //System.out.println("crp = " + crp);
         //System.out.println("cip = " + cip);
         //System.out.println("ap  = " + ap);

         assertEquals("re(c)+i*im(c) = c",cp,ap);
     }
 }

}
