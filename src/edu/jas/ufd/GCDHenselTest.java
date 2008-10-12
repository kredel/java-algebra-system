/*
 * $Id$
 */

package edu.jas.ufd;

//import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigInteger;
//import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.PrimeList;

//import edu.jas.poly.ExpVector;
import edu.jas.poly.TermOrder;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;


/**
 * GCD Hensel algorithm tests with JUnit.
 * @author Heinz Kredel.
 */

public class GCDHenselTest extends TestCase {

/**
 * main.
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>GCDHenselTest</CODE> object.
 * @param name String.
 */
   public GCDHenselTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(GCDHenselTest.class);
     return suite;
   }

   //private final static int bitlen = 100;

   GreatestCommonDivisorAbstract<BigInteger> ufd; 

   TermOrder to = new TermOrder( TermOrder.INVLEX );

   GenPolynomialRing<BigInteger> dfac;
   GenPolynomialRing<BigInteger> cfac;
   GenPolynomialRing<GenPolynomial<BigInteger>> rfac;

   //PrimeList primes = new PrimeList();

   BigInteger cofac;

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
   GenPolynomial<BigInteger> ac;
   GenPolynomial<BigInteger> bc;

   GenPolynomial<GenPolynomial<BigInteger>> ar;
   GenPolynomial<GenPolynomial<BigInteger>> br;
   GenPolynomial<GenPolynomial<BigInteger>> cr;
   GenPolynomial<GenPolynomial<BigInteger>> dr;
   GenPolynomial<GenPolynomial<BigInteger>> er;
   GenPolynomial<GenPolynomial<BigInteger>> arc;
   GenPolynomial<GenPolynomial<BigInteger>> brc;

   int rl = 3; 
   int kl = 64;
   int ll = 5;
   int el = 5; //3;
   float q = 0.3f;

   protected void setUp() {
       a = b = c = d = e = null;
       ai = bi = ci = di = ei = null;
       ar = br = cr = dr = er = null;
       cofac = new BigInteger();
       ufd = new GreatestCommonDivisorHensel/*<BigInteger>*/();
       dfac = new GenPolynomialRing<BigInteger>(cofac,rl,to);
       cfac = new GenPolynomialRing<BigInteger>(cofac,rl-1,to);
       rfac = new GenPolynomialRing<GenPolynomial<BigInteger>>(cfac,1,to);
   }

   protected void tearDown() {
       a = b = c = d = e = null;
       ai = bi = ci = di = ei = null;
       ar = br = cr = dr = er = null;
       ufd = null;
       dfac = null;
       cfac = null;
       rfac = null;
   }


/**
 * Test univariate Hensel algorithm gcd with subres PRS recursive algorithm.
 * 
 */
 public void testHenselSubresGcd() {

     GenPolynomial<BigInteger> a;
     GenPolynomial<BigInteger> b;
     GenPolynomial<BigInteger> c;
     GenPolynomial<BigInteger> d;
     GenPolynomial<BigInteger> e;

     GenPolynomialRing<BigInteger> dfac 
         = new GenPolynomialRing<BigInteger>(cofac,rl,to);

     for (int i = 0; i < 1; i++) {
         a = dfac.random(kl,ll+i,el+i,q);
         b = dfac.random(kl,ll+i,el+i,q);
         c = dfac.random(kl,ll+i,el+i,q);
         c = c.multiply( dfac.univariate(0) );
         //a = ufd.basePrimitivePart(a);
         //b = ufd.basePrimitivePart(b);

         if ( a.isZERO() || b.isZERO() || c.isZERO() ) {
            // skip for this turn
            continue;
         }
         assertTrue("length( c"+i+" ) <> 0", c.length() > 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );

         System.out.println("a  = " + a);
         System.out.println("b  = " + b);
         System.out.println("c  = " + c);
         
         a = a.multiply(c); //.multiply(c);
         b = b.multiply(c);

         System.out.println("a c = " + a);
         System.out.println("b c = " + b);

         d = ufd.gcd(a,b);
         //d = ufd.baseGcd(a,b);

         c = ufd.basePrimitivePart(c).abs();
         e = PolyUtil.<BigInteger>basePseudoRemainder(d,c);
         System.out.println("c  = " + c);
         System.out.println("d  = " + d);
         //System.out.println("e  = " + e);

         assertTrue("c | gcd(ac,bc): " + e, e.isZERO() );

         e = PolyUtil.<BigInteger>basePseudoRemainder(a,d);
         //System.out.println("e  = " + e);
         assertTrue("gcd(a,b) | a: " + e, e.isZERO() );

         e = PolyUtil.<BigInteger>basePseudoRemainder(b,d);
         //System.out.println("e  = " + e);
         assertTrue("gcd(a,b) | b: " + e, e.isZERO() );
     }
 }

}
