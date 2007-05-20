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
import edu.jas.arith.BigComplex;

import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.TermOrder;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import static edu.jas.poly.PolyUtil.*;


/**
 * GreatestCommonDivisor Test using JUnit.
 * @author Heinz Kredel.
 */

public class GCDProxyTest extends TestCase {

/**
 * main.
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
          System.exit(0);
   }

/**
 * Constructs a <CODE>GCDProxyTest</CODE> object.
 * @param name String.
 */
   public GCDProxyTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(GCDProxyTest.class);
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
 * Test get BigInteger implementation.
 * 
 */
 public void xtestBigInteger() {
     long t;
     BigInteger bi = new BigInteger();

     GreatestCommonDivisor<BigInteger> ufd_par; 
     GreatestCommonDivisorAbstract<BigInteger> ufd; 

     ufd_par = GCDFactory.<BigInteger>getProxy(bi);
     System.out.println("ufd_par = " + ufd_par);
     assertTrue("ufd_par != null " + ufd_par, ufd_par != null);

     ufd = new GreatestCommonDivisorSubres<BigInteger>(); 

     System.out.println("ufd = " + ufd);
     assertTrue("ufd != null " + ufd, ufd != null);

     dfac = new GenPolynomialRing<BigInteger>(bi,4,to);

     for (int i = 0; i < 5; i++) {
         a = dfac.random(kl+i*20,ll+i,10+el,q);
         b = dfac.random(kl+i*20,ll+i,10+el,q);
         c = dfac.random(kl+5,ll,el,q);
         //c = dfac.getONE();
         //c = c.multiply( dfac.univariate(0) );
         c = ufd.primitivePart(c).abs();
         //System.out.println("a = " + a);
         //System.out.println("b = " + b);
         //System.out.println("c = " + c);

         if ( a.isZERO() || b.isZERO() || c.isZERO() ) {
            // skip for this turn
            continue;
         }
         assertTrue("length( c"+i+" ) <> 0", c.length() > 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );
         
         a = a.multiply(c);
         b = b.multiply(c);
         //System.out.println("a = " + a);
         //System.out.println("b = " + b);

         System.out.println("\ndegrees: a = " + a.degree() 
                                   + ", b = " + b.degree()  
                                   + ", c = " + c.degree());  

         t = System.currentTimeMillis();
         d = ufd_par.gcd(a,b);
         t = System.currentTimeMillis() - t;
         System.out.println("proxy time = " + t);
         //System.out.println("c = " + c);
         //System.out.println("d = " + d);
         //System.out.println("e = " + e);

         e = ufd.basePseudoRemainder(d,c);
         //System.out.println("e = " + e);
         assertTrue("c | gcd(ac,bc) " + e, e.isZERO() );
     }

     ((GCDProxy<BigInteger>)ufd_par).terminate();
 }


/**
 * Test get ModInteger implementation.
 * 
 */ 
 public void testModInteger() {
     long t;
     // ModInteger mi = new ModInteger(19,0,true);
     ModInteger mi = new ModInteger(536870909,0,true);

     GenPolynomial<ModInteger> a, b, c, d, e;

     GreatestCommonDivisor<ModInteger> ufd_par; 
     GreatestCommonDivisorAbstract<ModInteger> ufd; 

     ufd_par = GCDFactory.<ModInteger>getProxy(mi);
     System.out.println("ufd_par = " + ufd_par);
     assertTrue("ufd_par != null " + ufd_par, ufd_par != null);

     ufd = new GreatestCommonDivisorSubres<ModInteger>(); 

     System.out.println("ufd = " + ufd);
     assertTrue("ufd != null " + ufd, ufd != null);

     GenPolynomialRing<ModInteger> dfac;
     dfac = new GenPolynomialRing<ModInteger>(mi,4,to);

     for (int i = 0; i < 4; i++) {
         a = dfac.random(kl+i*2,ll+i,10+el,q);
         b = dfac.random(kl+i*2,ll+i,10+el,q);
         c = dfac.random(kl,ll,el,q);
         //a = dfac.random(kl,ll+i,el,q);
         //b = dfac.random(kl,ll+i,el,q);
         //c = dfac.random(kl,ll,el,q);
         //c = dfac.getONE();
         //c = c.multiply( dfac.univariate(0) );
         c = ufd.primitivePart(c).abs();
         //System.out.println("a = " + a);
         //System.out.println("b = " + b);
         //System.out.println("c = " + c);

         if ( a.isZERO() || b.isZERO() || c.isZERO() ) {
            // skip for this turn
            continue;
         }
         assertTrue("length( c"+i+" ) <> 0", c.length() > 0);
         //assertTrue(" not isZERO( c"+i+" )", !c.isZERO() );
         //assertTrue(" not isONE( c"+i+" )", !c.isONE() );
         
         a = a.multiply(c);
         b = b.multiply(c);
         //System.out.println("a = " + a);
         //System.out.println("b = " + b);

         System.out.println("\ndegrees: a = " + a.degree() 
                                   + ", b = " + b.degree()  
                                   + ", c = " + c.degree());  

         t = System.currentTimeMillis();
         d = ufd_par.gcd(a,b);
         t = System.currentTimeMillis() - t;
         System.out.println("proxy time = " + t);
         //System.out.println("c = " + c);
         //System.out.println("d = " + d);
         //System.out.println("e = " + e);

         e = ufd.basePseudoRemainder(d,c);
         //System.out.println("e = " + e);
         assertTrue("c | gcd(ac,bc) " + e, e.isZERO() );
     }

     ((GCDProxy<ModInteger>)ufd_par).terminate();
 }


/**
 * Test get BigRational implementation.
 * 
 */
 public void xtestBigRational() {
     BigRational b = new BigRational();
     GreatestCommonDivisor<BigRational> ufd; 

     ufd = GCDFactory.<BigRational>getImplementation(b);
     //System.out.println("ufd = " + ufd);
     assertTrue("ufd = Primitive " + ufd, ufd instanceof GreatestCommonDivisorPrimitive);
 }


/**
 * Test get BigComplex implementation.
 * 
 public void testBigComplex() {
     BigComplex b = new BigComplex();
     GreatestCommonDivisor<BigComplex> ufd; 

     ufd = GCDFactory.<BigComplex>getImplementation(b);
     //System.out.println("ufd = " + ufd);
     assertTrue("ufd != Simple " + ufd, ufd instanceof GreatestCommonDivisorSimple);
 }
 */


/**
 * Test get AlgebraicNumber<BigRational> implementation.
 * 
 */
 public void xtestAlgebraicNumberBigRational() {
     BigRational b = new BigRational();
     GenPolynomialRing<BigRational> fac;
     fac = new GenPolynomialRing<BigRational>( b, 1 );
     GenPolynomial<BigRational> mo = fac.random(kl,ll,el,q);
     while ( mo.isConstant() ) {
          mo = fac.random(kl,ll,el,q);
     }

     AlgebraicNumberRing<BigRational> afac;
     afac = new AlgebraicNumberRing<BigRational>( mo );

     GreatestCommonDivisor<AlgebraicNumber<BigRational>> ufd; 

     ufd = GCDFactory.<AlgebraicNumber<BigRational>>getImplementation(afac);
     //System.out.println("ufd = " + ufd);
     assertTrue("ufd = Subres " + ufd, ufd instanceof GreatestCommonDivisorSubres);


     mo = fac.univariate(0).subtract( fac.getONE() );
     afac = new AlgebraicNumberRing<BigRational>( mo, true );

     ufd = GCDFactory.<AlgebraicNumber<BigRational>>getImplementation(afac);
     //System.out.println("ufd = " + ufd);
     assertTrue("ufd = Simple " + ufd, ufd instanceof GreatestCommonDivisorSimple);

 }


/**
 * Test get AlgebraicNumber<ModInteger> implementation.
 * 
 */
 public void xtestAlgebraicNumberModInteger() {
     ModInteger b = new ModInteger(19,0,true);
     GenPolynomialRing<ModInteger> fac;
     fac = new GenPolynomialRing<ModInteger>( b, 1 );
     GenPolynomial<ModInteger> mo = fac.random(kl,ll,el,q);
     while ( mo.isConstant() ) {
          mo = fac.random(kl,ll,el,q);
     }

     AlgebraicNumberRing<ModInteger> afac;
     afac = new AlgebraicNumberRing<ModInteger>( mo );


     AlgebraicNumber<ModInteger> a = afac.getONE();
     GreatestCommonDivisor<AlgebraicNumber<ModInteger>> ufd; 

     ufd = GCDFactory.<AlgebraicNumber<ModInteger>>getImplementation(afac);
     //System.out.println("ufd = " + ufd);
     assertTrue("ufd = Subres " + ufd, ufd instanceof GreatestCommonDivisorSubres);
 }


}
