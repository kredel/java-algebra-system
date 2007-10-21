
/*
 * $Id$
 */

package edu.jas.structure;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Logger;

import edu.jas.arith.BigRational;
import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;

//import edu.jas.structure.RingElem;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;


/**
 * Product test with JUnit. 
 * @author Heinz Kredel.
 */

public class ProductTest extends TestCase {

/**
 * main.
 */
   public static void main (String[] args) {
       BasicConfigurator.configure();
       junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>ProductTest</CODE> object.
 * @param name String.
 */
   public ProductTest(String name) {
          super(name);
   }

/**
 * suite.
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(ProductTest.class);
     return suite;
   }

   ProductRing<BigRational> fac;
   ModIntegerRing pfac;
   ProductRing<ModInteger> mfac;

   Product< BigRational > a;
   Product< BigRational > b;
   Product< BigRational > c;
   Product< BigRational > d;
   Product< BigRational > e;

   Product< ModInteger > ap;
   Product< ModInteger > bp;
   Product< ModInteger > cp;
   Product< ModInteger > dp;
   Product< ModInteger > ep;


   int pl = 3; 
   int rl = 1; 
   int kl = 13;
   int ll = 7;
   int el = 3;
   float q = 0.9f;
   int il = 2; 
   long p = 1152921504606846883L; // 2^60-93; 

   protected void setUp() {
       a = b = c = d = e = null;
       ap = bp = cp = dp = ep = null;
       BigRational cfac = new BigRational(2,3);
       fac = new ProductRing<BigRational>( cfac, pl );
       List<RingFactory<ModInteger>> lpfac 
           = new ArrayList<RingFactory<ModInteger>>();
       pfac = new ModIntegerRing( 2 );
       lpfac.add(pfac);
       pfac = new ModIntegerRing( 3 );
       lpfac.add(pfac);
       pfac = new ModIntegerRing( 5 );
       lpfac.add(pfac);
       pfac = new ModIntegerRing( 7 );
       lpfac.add(pfac);
       mfac = new ProductRing<ModInteger>( lpfac );
   }

   protected void tearDown() {
       a = b = c = d = e = null;
       ap = bp = cp = dp = ep = null;
       fac = null;
       pfac = null;
       mfac = null;
   }


/**
 * Test constructor for integer.
 * 
 */
 public void testRatConstruction() {
     c = fac.getONE();
     //System.out.println("c = " + c);
     assertTrue("isZERO( c )", !c.isZERO() );
     assertTrue("isONE( c )", c.isONE() );

     d = fac.getZERO();
     //System.out.println("d = " + d);
     assertTrue("isZERO( d )", d.isZERO() );
     assertTrue("isONE( d )", !d.isONE() );
 }


/**
 * Test constructor for polynomial.
 * 
 */
 public void testModConstruction() {
     cp = mfac.getONE();
     //System.out.println("cp = " + cp);
     assertTrue("isZERO( cp )", !cp.isZERO() );
     assertTrue("isONE( cp )", cp.isONE() );

     dp = mfac.getZERO();
     //System.out.println("dp = " + dp);
     assertTrue("isZERO( dp )", dp.isZERO() );
     assertTrue("isONE( dp )", !dp.isONE() );
 }


/**
 * Test random integer.
 * 
 */
 public void testRatRandom() {
     for (int i = 0; i < 7; i++) {
         a = fac.random(kl*(i+1));
         if ( a.isZERO() ) {
            continue;
         }
         assertTrue(" not isZERO( a"+i+" )", !a.isZERO() );
         assertTrue(" not isONE( a"+i+" )", !a.isONE() );
         a = fac.random( kl, q );
         if ( a.isZERO() ) {
            continue;
         }
         //System.out.println("a = " + a);
         assertTrue(" not isZERO( a"+i+" )", !a.isZERO() );
         assertTrue(" not isONE( a"+i+" )", !a.isONE() );
     }
 }


/**
 * Test random polynomial.
 * 
 */
 public void testModRandom() {
     for (int i = 0; i < 7; i++) {
         ap = mfac.random(kl,q);
         if ( ap.isZERO() ) {
            continue;
         }
         //System.out.println("ap = " + ap);
         assertTrue(" not isZERO( ap"+i+" )", !ap.isZERO() );
         assertTrue(" not isONE( ap"+i+" )", !ap.isONE() );
     }
 }


/**
 * Test integer addition.
 * 
 */
 public void testRatAddition() {

     a = fac.random(kl,q);
     b = fac.random(kl,q);

     c = a.sum(b);
     d = c.subtract(b);
     assertEquals("a+b-b = a",a,d);

     //System.out.println("a = " + a);
     //System.out.println("b = " + b);
     //System.out.println("c = " + c);
     //System.out.println("d = " + d);

     c = a.sum(b);
     d = b.sum(a);
     assertEquals("a+b = b+a",c,d);

     //System.out.println("c = " + c);
     //System.out.println("d = " + d);

     c = fac.random(kl,q);
     d = c.sum( a.sum(b) );
     e = c.sum( a ).sum(b);
     assertEquals("c+(a+b) = (c+a)+b",d,e);

     //System.out.println("c = " + c);
     //System.out.println("d = " + d);
     //System.out.println("e = " + e);

     c = a.sum( fac.getZERO() );
     d = a.subtract( fac.getZERO() );
     assertEquals("a+0 = a-0",c,d);

     //System.out.println("c = " + c);
     //System.out.println("d = " + d);

     c = fac.getZERO().sum( a );
     d = fac.getZERO().subtract( a.negate() );
     assertEquals("0+a = 0+(-a)",c,d);

     //System.out.println("c = " + c);
     //System.out.println("d = " + d);
 }


/**
 * Test polynomial addition.
 * 
 */
 public void testModAddition() {

     ap = mfac.random(kl,q);
     bp = mfac.random(kl,q);
     //System.out.println("a = " + a);
     //System.out.println("b = " + b);

     cp = ap.sum(bp);
     dp = cp.subtract(bp);
     assertEquals("a+b-b = a",ap,dp);

     cp = ap.sum(bp);
     dp = bp.sum(ap);
     //System.out.println("c = " + c);
     //System.out.println("d = " + d);

     assertEquals("a+b = b+a",cp,dp);

     cp = mfac.random(kl,q);
     dp = cp.sum( ap.sum(bp) );
     ep = cp.sum( ap ).sum(bp);
     assertEquals("c+(a+b) = (c+a)+b",dp,ep);


     cp = ap.sum( mfac.getZERO() );
     dp = ap.subtract( mfac.getZERO() );
     assertEquals("a+0 = a-0",cp,dp);

     cp = mfac.getZERO().sum( ap );
     dp = mfac.getZERO().subtract( ap.negate() );
     assertEquals("0+a = 0+(-a)",cp,dp);
 }


/**
 * Test integer multiplication.
 * 
 */

 public void testRatMultiplication() {

     a = fac.random(kl);
     if ( a.isZERO() ) {
        return;
     }
     assertTrue("not isZERO( a )", !a.isZERO() );

     b = fac.random(kl,q);
     if ( b.isZERO() ) {
        return;
     }
     assertTrue("not isZERO( b )", !b.isZERO() );

     c = b.multiply(a);
     d = a.multiply(b);
     //assertTrue("not isZERO( c )", !c.isZERO() );
     //assertTrue("not isZERO( d )", !d.isZERO() );

     //System.out.println("a = " + a);
     //System.out.println("b = " + b);
     e = d.subtract(c);
     assertTrue("isZERO( a*b-b*a ) " + e, e.isZERO() );

     assertTrue("a*b = b*a", c.equals(d) );
     assertEquals("a*b = b*a",c,d);

     c = fac.random(kl,q);
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

     if ( a.isUnit() ) {
        c = a.inverse();
        d = c.multiply(a);
        e = a.idempotent();
        //System.out.println("a = " + a);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);
        assertEquals("a*1/a = 1",e,d); 
     }
 }


/**
 * Test polynomial multiplication.
 * 
 */
 public void testModMultiplication() {

     ap = mfac.random(kl,q);
     if ( ap.isZERO() ) {
        return;
     }
     assertTrue("not isZERO( a )", !ap.isZERO() );

     bp = mfac.random(kl,q);
     if ( bp.isZERO() ) {
        return;
     }
     assertTrue("not isZERO( b )", !bp.isZERO() );

     cp = bp.multiply(ap);
     dp = ap.multiply(bp);
     //assertTrue("not isZERO( c )", !cp.isZERO() );
     //assertTrue("not isZERO( d )", !dp.isZERO() );

     //System.out.println("a = " + a);
     //System.out.println("b = " + b);
     ep = dp.subtract(cp);
     assertTrue("isZERO( a*b-b*a ) " + ep, ep.isZERO() );

     assertTrue("a*b = b*a", cp.equals(dp) );
     assertEquals("a*b = b*a",cp,dp);

     cp = mfac.random(kl,q);
     //System.out.println("c = " + c);
     dp = ap.multiply( bp.multiply(cp) );
     ep = (ap.multiply(bp)).multiply(cp);

     //System.out.println("d = " + d);
     //System.out.println("e = " + e);

     //System.out.println("d-e = " + d.subtract(c) );

     assertEquals("a(bc) = (ab)c",dp,ep);
     assertTrue("a(bc) = (ab)c", dp.equals(ep) );

     cp = ap.multiply( mfac.getONE() );
     dp = mfac.getONE().multiply( ap );
     assertEquals("a*1 = 1*a",cp,dp);

     if ( ap.isUnit() ) {
        cp = ap.inverse();
        dp = cp.multiply(ap);
        ep = ap.idempotent();
        //System.out.println("ap = " + ap);
        //System.out.println("cp = " + cp);
        //System.out.println("dp = " + dp);
        //System.out.println("ep = " + ep);
        assertEquals("a*1/a = 1",ep,dp); 
     }
 }


}
