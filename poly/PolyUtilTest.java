/*
 * $Id$
 */

package edu.jas.poly;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.poly.TermOrder;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.arith.BigInteger;


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

}
