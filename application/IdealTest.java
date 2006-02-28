/*
 * $Id$
 */

package edu.jas.application;

//import edu.jas.poly.GroebnerBase;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import edu.jas.arith.BigRational;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.TermOrder;

import edu.jas.ring.GroebnerBase;
//import edu.jas.ring.Reduction;
import edu.jas.ring.GroebnerBaseSeq;
//import edu.jas.ring.ReductionSeq;


/**
 * Ideal Test using JUnit.
 * @author Heinz Kredel.
 */
public class IdealTest extends TestCase {

    private static final Logger logger = Logger.getLogger(IdealTest.class);

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>IdealTest</CODE> object.
 * @param name String.
 */
   public IdealTest(String name) {
          super(name);
   }

/**
 * suite.
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(IdealTest.class);
     return suite;
   }

   TermOrder to;
   GenPolynomialRing<BigRational> fac;

   List<GenPolynomial<BigRational>> L;
   PolynomialList<BigRational> F;
   List<GenPolynomial<BigRational>> G;

   GroebnerBase<BigRational> bb;

   GenPolynomial<BigRational> a;
   GenPolynomial<BigRational> b;
   GenPolynomial<BigRational> c;
   GenPolynomial<BigRational> d;
   GenPolynomial<BigRational> e;

   int rl = 3; //4; //3; 
   int kl = 4; //10
   int ll = 5; //7
   int el = 3;
   float q = 0.2f; //0.4f

   protected void setUp() {
       BigRational coeff = new BigRational(17,1);
       to = new TermOrder( /*TermOrder.INVLEX*/ );
       fac = new GenPolynomialRing<BigRational>(coeff,rl,to);
       bb = new GroebnerBaseSeq<BigRational>();
       a = b = c = d = e = null;
   }

   protected void tearDown() {
       a = b = c = d = e = null;
       fac = null;
       bb = null;
   }


/**
 * Test Ideal sum.
 * 
 */
 public void testIdealSum() {

     Ideal<BigRational> I;
     Ideal<BigRational> J;
     Ideal<BigRational> K;

     I = new Ideal<BigRational>(fac,L);
     assertTrue("isZERO( I )", I.isZERO() );
     assertTrue("not isONE( I )", !I.isONE() );
     assertTrue("isGB( I )", I.isGB() );

     L = new ArrayList<GenPolynomial<BigRational>>();

     a = fac.random(kl, ll, el, q );
     b = fac.random(kl, ll, el, q );
     c = fac.random(kl, ll, el, q );
     d = fac.random(kl, ll, el, q );
     e = d; //fac.random(kl, ll, el, q );

     assertTrue("not isZERO( a )", !a.isZERO() );
     L.add(a);

     I = new Ideal<BigRational>(fac,L,true);
     assertTrue("not isZERO( I )", !I.isZERO() );
     assertTrue("not isONE( I )", !I.isONE() );
     assertTrue("isGB( I )", I.isGB() );

     I = new Ideal<BigRational>(fac,L,false);
     assertTrue("not isZERO( I )", !I.isZERO() );
     assertTrue("not isONE( I )", !I.isONE() );
     assertTrue("isGB( I )", I.isGB() );


     L = bb.GB( L );
     assertTrue("isGB( { a } )", bb.isGB(L) );

     I = new Ideal<BigRational>(fac,L,true);
     assertTrue("not isZERO( I )", !I.isZERO() );
     //assertTrue("not isONE( I )", !I.isONE() );
     assertTrue("isGB( I )", I.isGB() );

     I = new Ideal<BigRational>(fac,L,false);
     assertTrue("not isZERO( I )", !I.isZERO() );
     //assertTrue("not isONE( I )", !I.isONE() );
     assertTrue("isGB( I )", I.isGB() );


     assertTrue("not isZERO( b )", !b.isZERO() );
     L.add(b);
     //System.out.println("L = " + L.size() );

     I = new Ideal<BigRational>(fac,L,false);
     assertTrue("not isZERO( I )", !I.isZERO() );
     //assertTrue("not isONE( I )", !I.isONE() );
     //assertTrue("not isGB( I )", !I.isGB() );


     L = bb.GB( L );
     assertTrue("isGB( { a, b } )", bb.isGB(L) );

     I = new Ideal<BigRational>(fac,L,true);
     assertTrue("not isZERO( I )", !I.isZERO() );
     // assertTrue("not isONE( I )", !I.isONE() );
     assertTrue("isGB( I )", I.isGB() );


     J = I;
     K = J.sum( I );
     assertTrue("not isZERO( K )", !K.isZERO() );
     assertTrue("isGB( K )", K.isGB() );
     assertTrue("equals( K, I )", K.equals(I) );


     L = new ArrayList<GenPolynomial<BigRational>>();
     assertTrue("not isZERO( c )", !c.isZERO() );
     L.add(c);
     assertTrue("isGB( { c } )", bb.isGB(L) );

     J = new Ideal<BigRational>(fac,L,true);
     K = J.sum( I );
     assertTrue("not isZERO( K )", !K.isZERO() );
     assertTrue("isGB( K )", K.isGB() );
     assertTrue("K contains(I)", K.contains(I) );
     assertTrue("K contains(J)", K.contains(J) );

     L = new ArrayList<GenPolynomial<BigRational>>();
     assertTrue("not isZERO( d )", !d.isZERO() );
     L.add(d);

     assertTrue("isGB( { d } )", bb.isGB(L) );
     J = new Ideal<BigRational>(fac,L,true);
     I = K;
     K = J.sum( I );
     assertTrue("not isZERO( K )", !K.isZERO() );
     assertTrue("isGB( K )", K.isGB() );
     assertTrue("K contains(I)", K.contains(I) );
     assertTrue("K contains(J)", K.contains(J) );


     L = new ArrayList<GenPolynomial<BigRational>>();
     assertTrue("not isZERO( e )", !e.isZERO() );
     L.add(e);

     assertTrue("isGB( { e } )", bb.isGB(L) );
     J = new Ideal<BigRational>(fac,L,true);
     I = K;
     K = J.sum( I );
     assertTrue("not isZERO( K )", !K.isZERO() );
     assertTrue("isGB( K )", K.isGB() );
     assertTrue("equals( K, I )", K.equals(I) );
     assertTrue("K contains(J)", K.contains(I) );
     assertTrue("I contains(K)", I.contains(K) );
 }


/**
 * Test Ideal product.
 * 
 */
 public void testIdealProduct() {

     Ideal<BigRational> I;
     Ideal<BigRational> J;
     Ideal<BigRational> K;
     Ideal<BigRational> H;

     I = new Ideal<BigRational>(fac,L);
     assertTrue("isZERO( I )", I.isZERO() );
     assertTrue("not isONE( I )", !I.isONE() );
     assertTrue("isGB( I )", I.isGB() );

     a = fac.random(kl, ll, el, q );
     b = fac.random(kl, ll, el, q );
     c = fac.random(kl, ll, el, q );
     d = fac.random(kl, ll, el, q );
     e = d; //fac.random(kl, ll, el, q );

     L = new ArrayList<GenPolynomial<BigRational>>();
     assertTrue("not isZERO( a )", !a.isZERO() );
     L.add(a);

     I = new Ideal<BigRational>(fac,L,true);
     assertTrue("not isZERO( I )", !I.isZERO() );
     assertTrue("not isONE( I )", !I.isONE() );
     assertTrue("isGB( I )", I.isGB() );

     L = new ArrayList<GenPolynomial<BigRational>>();
     assertTrue("not isZERO( b )", !a.isZERO() );
     L.add(b);

     J = new Ideal<BigRational>(fac,L,true);
     assertTrue("not isZERO( J )", !J.isZERO() );
     assertTrue("not isONE( J )", !J.isONE() );
     assertTrue("isGB( J )", J.isGB() );

     K = I.product( J );
     assertTrue("not isZERO( K )", !K.isZERO() );
     assertTrue("isGB( K )", K.isGB() );
     assertTrue("I contains(K)", I.contains(K) );
     assertTrue("J contains(K)", J.contains(K) );

     H = I.intersect( J );
     assertTrue("not isZERO( H )", !H.isZERO() );
     assertTrue("isGB( H )", H.isGB() );
     assertTrue("I contains(H)", I.contains(H) );
     assertTrue("J contains(H)", J.contains(H) );
     assertTrue("H contains(K)", H.contains(K) );
     if ( false /*! H.equals(K)*/ ) {
        System.out.println("I = " + I );
        System.out.println("J = " + J );
        System.out.println("K = " + K );
        System.out.println("H = " + H );
     }


     L = new ArrayList<GenPolynomial<BigRational>>();
     assertTrue("not isZERO( a )", !a.isZERO() );
     L.add(a);
     assertTrue("not isZERO( c )", !c.isZERO() );
     L.add(c);
     L = bb.GB( L );

     I = new Ideal<BigRational>(fac,L,true);
     assertTrue("not isZERO( I )", !I.isZERO() );
     //assertTrue("not isONE( I )", !I.isONE() );
     assertTrue("isGB( I )", I.isGB() );

     K = I.product( J );
     assertTrue("not isZERO( K )", !K.isZERO() );
     assertTrue("isGB( K )", K.isGB() );
     assertTrue("I contains(K)", I.contains(K) );
     assertTrue("J contains(K)", J.contains(K) );

     H = I.intersect( J );
     assertTrue("not isZERO( H )", !H.isZERO() );
     assertTrue("isGB( H )", H.isGB() );
     assertTrue("I contains(H)", I.contains(H) );
     assertTrue("J contains(H)", J.contains(H) );
     assertTrue("H contains(K)", H.contains(K) );
     if ( false /*! H.equals(K)*/ ) {
        System.out.println("I = " + I );
        System.out.println("J = " + J );
        System.out.println("K = " + K );
        System.out.println("H = " + H );
     }


     L = new ArrayList<GenPolynomial<BigRational>>();
     assertTrue("not isZERO( b )", !b.isZERO() );
     L.add(b);
     assertTrue("not isZERO( d )", !d.isZERO() );
     L.add(d);
     L = bb.GB( L );

     J = new Ideal<BigRational>(fac,L,true);
     assertTrue("not isZERO( J )", !J.isZERO() );
     //assertTrue("not isONE( J )", !J.isONE() );
     assertTrue("isGB( J )", J.isGB() );

     K = I.product( J );
     assertTrue("not isZERO( K )", !K.isZERO() );
     assertTrue("isGB( K )", K.isGB() );
     assertTrue("I contains(K)", I.contains(K) );
     assertTrue("J contains(K)", J.contains(K) );

     H = I.intersect( J );
     assertTrue("not isZERO( H )", !H.isZERO() );
     assertTrue("isGB( H )", H.isGB() );
     assertTrue("I contains(H)", I.contains(H) );
     assertTrue("J contains(H)", J.contains(H) );
     assertTrue("H contains(K)", H.contains(K) );
     if ( false /*! H.equals(K)*/ ) {
        System.out.println("I = " + I );
        System.out.println("J = " + J );
        System.out.println("K = " + K );
        System.out.println("H = " + H );
     }
 }


/**
 * Test Ideal quotient.
 * 
 */
 public void testIdealQuotient() {

     Ideal<BigRational> I;
     Ideal<BigRational> J;
     Ideal<BigRational> K;
     Ideal<BigRational> H;

     I = new Ideal<BigRational>(fac,L);
     assertTrue("isZERO( I )", I.isZERO() );
     assertTrue("not isONE( I )", !I.isONE() );
     assertTrue("isGB( I )", I.isGB() );

     a = fac.random(kl, ll, el, q );
     b = fac.random(kl, ll, el, q );
     c = fac.random(kl, ll, el, q );
     d = fac.random(kl, ll, el, q );
     e = d; //fac.random(kl, ll, el, q );

     L = new ArrayList<GenPolynomial<BigRational>>();
     assertTrue("not isZERO( a )", !a.isZERO() );
     L.add(a);
     L = bb.GB( L );

     I = new Ideal<BigRational>(fac,L,true);
     assertTrue("not isZERO( I )", !I.isZERO() );
     //assertTrue("not isONE( I )", !I.isONE() );
     assertTrue("isGB( I )", I.isGB() );


     L = new ArrayList<GenPolynomial<BigRational>>();
     assertTrue("not isZERO( b )", !a.isZERO() );
     L.add(b);
     L = bb.GB( L );

     J = new Ideal<BigRational>(fac,L,true);
     assertTrue("not isZERO( J )", !J.isZERO() );
     //assertTrue("not isONE( J )", !J.isONE() );
     assertTrue("isGB( J )", J.isGB() );

     K = I.product( J );
     assertTrue("not isZERO( K )", !K.isZERO() );
     assertTrue("isGB( K )", K.isGB() );
     assertTrue("I contains(K)", I.contains(K) );
     assertTrue("J contains(K)", J.contains(K) );

     H = K.quotient( J.getList().get(0) );
     assertTrue("not isZERO( H )", !H.isZERO() );
     assertTrue("isGB( H )", H.isGB() );
     assertTrue("equals(H,I)", H.equals(I) ); // GBs only

     H = K.quotient( J );
     assertTrue("not isZERO( H )", !H.isZERO() );
     assertTrue("isGB( H )", H.isGB() );
     assertTrue("equals(H,I)", H.equals(I) ); // GBs only


     L = new ArrayList<GenPolynomial<BigRational>>();
     assertTrue("not isZERO( b )", !b.isZERO() );
     L.add(b);
     assertTrue("not isZERO( c )", !c.isZERO() );
     L.add(c);
     L = bb.GB( L );

     J = new Ideal<BigRational>(fac,L,true);
     assertTrue("not isZERO( J )", !J.isZERO() );
     //assertTrue("not isONE( J )", !J.isONE() );
     assertTrue("isGB( J )", J.isGB() );

     K = I.product( J );
     assertTrue("not isZERO( K )", !K.isZERO() );
     assertTrue("isGB( K )", K.isGB() );
     assertTrue("I contains(K)", I.contains(K) );
     assertTrue("J contains(K)", J.contains(K) );

     H = K.quotient( J );
     assertTrue("not isZERO( H )", !H.isZERO() );
     assertTrue("isGB( H )", H.isGB() );
     assertTrue("equals(H,I)", H.equals(I) ); // GBs only


     L = new ArrayList<GenPolynomial<BigRational>>();
     assertTrue("not isZERO( a )", !a.isZERO() );
     L.add(a);
     assertTrue("not isZERO( d )", !d.isZERO() );
     L.add(d);
     L = bb.GB( L );

     I = new Ideal<BigRational>(fac,L,true);
     assertTrue("not isZERO( I )", !I.isZERO() );
     //assertTrue("not isONE( J )", !J.isONE() );
     assertTrue("isGB( I )", I.isGB() );

     K = I.product( J );
     assertTrue("not isZERO( K )", !K.isZERO() );
     assertTrue("isGB( K )", K.isGB() );
     assertTrue("I contains(K)", I.contains(K) );
     assertTrue("J contains(K)", J.contains(K) );

     H = K.quotient( J );
     assertTrue("not isZERO( H )", !H.isZERO() );
     assertTrue("isGB( H )", H.isGB() );
     assertTrue("equals(H,I)", H.equals(I) ); // GBs only

     if ( false ) {
        System.out.println("I = " + I );
        System.out.println("J = " + J );
        System.out.println("K = " + K );
        System.out.println("H = " + H );
     }
 }


/**
 * Test Ideal infinite quotient.
 * 
 */
 public void testIdealInfiniteQuotient() {

     Ideal<BigRational> I;
     Ideal<BigRational> J;

     I = new Ideal<BigRational>(fac,L);
     assertTrue("isZERO( I )", I.isZERO() );
     assertTrue("not isONE( I )", !I.isONE() );
     assertTrue("isGB( I )", I.isGB() );

     a = fac.random(kl, ll, el, q );
     b = fac.random(kl, ll, el, q );
     c = fac.random(kl, ll, el, q );
     d = fac.random(kl, ll, el, q );
     e = d; //fac.random(kl, ll, el, q );

     L = new ArrayList<GenPolynomial<BigRational>>();
     assertTrue("not isZERO( b )", !b.isZERO() );
     L.add( b );
     L = bb.GB( L );
     I = new Ideal<BigRational>(fac,L,true);
     assertTrue("not isZERO( I )", !I.isZERO() );
     //assertTrue("not isONE( I )", !I.isONE() );
     assertTrue("isGB( I )", I.isGB() );

     J = I.infiniteQuotient( a );

     assertTrue("not isZERO( c )", !c.isZERO() );
     L.add(c);
     L = bb.GB( L );
     I = new Ideal<BigRational>(fac,L,true);
     assertTrue("not isZERO( I )", !I.isZERO() );
     //assertTrue("not isONE( I )", !I.isONE() );
     assertTrue("isGB( I )", I.isGB() );

     J = I.infiniteQuotient( a );
     assertTrue("equals(J,I)", J.equals(I) ); // GBs only

     assertTrue("not isZERO( d )", !d.isZERO() );
     L.add(d);
     L = bb.GB( L );
     I = new Ideal<BigRational>(fac,L,true);
     assertTrue("not isZERO( I )", !I.isZERO() );
     //assertTrue("not isONE( I )", !I.isONE() );
     assertTrue("isGB( I )", I.isGB() );

     J = I.infiniteQuotient( a );
     assertTrue("equals(J,I)", J.equals(I) ); // GBs only

     /*
     assertTrue("not isZERO( e )", !e.isZERO() );
     L.add(e);
     L = bb.GB( L );
     I = new Ideal<BigRational>(fac,L,true);
     assertTrue("not isZERO( I )", !I.isZERO() );
     //assertTrue("not isONE( I )", !I.isONE() );
     assertTrue("isGB( I )", I.isGB() );

     J = I.infiniteQuotient( a );
     assertTrue("equals(J,I)", J.equals(I) ); // GBs only

     J = I.infiniteQuotient( b );
     assertTrue("equals(J,I)", J.equals(I) ); // GBs only
     */
 }

}
