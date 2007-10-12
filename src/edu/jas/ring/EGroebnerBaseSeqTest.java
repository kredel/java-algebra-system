/*
 * $Id$
 */

package edu.jas.ring;


import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Logger;

import edu.jas.kern.ComputerThreads;

import edu.jas.arith.BigRational;
import edu.jas.arith.BigInteger;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;

import edu.jas.ring.GroebnerBase;

import edu.jas.application.Quotient;

/**
 * EGroebner base sequential tests with JUnit.
 * @author Heinz Kredel.
 */

public class EGroebnerBaseSeqTest extends TestCase {

    //private static final Logger logger = Logger.getLogger(EGroebnerBaseSeqTest.class);

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>EGroebnerBaseSeqTest</CODE> object.
 * @param name String.
 */
   public EGroebnerBaseSeqTest(String name) {
          super(name);
   }

/**
 * suite.
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(EGroebnerBaseSeqTest.class);
     return suite;
   }

   GenPolynomialRing<BigInteger> fac;

   List<GenPolynomial<BigInteger>> L;
   PolynomialList<BigInteger> F;
   List<GenPolynomial<BigInteger>> G;

   GroebnerBase<BigInteger> bb;

   GenPolynomial<BigInteger> a;
   GenPolynomial<BigInteger> b;
   GenPolynomial<BigInteger> c;
   GenPolynomial<BigInteger> d;
   GenPolynomial<BigInteger> e;

   int rl = 3; //4; //3; 
   int kl = 4; //4; 10
   int ll = 4;
   int el = 3;
   float q = 0.2f; //0.4f

   protected void setUp() {
       BigInteger coeff = new BigInteger(9);
       fac = new GenPolynomialRing<BigInteger>(coeff,rl);
       a = b = c = d = e = null;
       bb = new EGroebnerBaseSeq<BigInteger>();
   }

   protected void tearDown() {
       a = b = c = d = e = null;
       fac = null;
       bb = null;
   }


/**
 * Test sequential GBase.
 * 
 */
 public void xtestSequentialGBase() {

     L = new ArrayList<GenPolynomial<BigInteger>>();

     a = fac.random(kl, ll, el, q ).abs();
     b = fac.random(kl, ll, el, q ).abs();
     c = fac.random(kl, ll/2, el, q ).abs();
     d = fac.random(kl, ll/2, el, q ).abs();
     e = d; //fac.random(kl, ll, el, q );

     if ( a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO() ) {
        return;
     }

     L.add(a);
     //System.out.println("    L  = " + L );
     L = bb.GB( L );
     //System.out.println("eGB(L) = " + L );
     assertTrue("isGB( { a } )", bb.isGB(L) );

     L.add(b);
     //System.out.println("    L  = " + L );
     L = bb.GB( L );
     //System.out.println("eGB(L) = " + L );
     assertTrue("isGB( { a, b } )", bb.isGB(L) );

     L.add(c);
     //System.out.println("    L  = " + L );
     L = bb.GB( L );
     //System.out.println("eGB(L) = " + L );
     assertTrue("isGB( { a, ,b, c } )", bb.isGB(L) );

     L.add(d);
     //System.out.println("    L  = " + L );
     L = bb.GB( L );
     //System.out.println("eGB(L) = " + L );
     assertTrue("isGB( { a, ,b, c, d } )", bb.isGB(L) );

     L.add(e);
     //System.out.println("    L  = " + L );
     L = bb.GB( L );
     //System.out.println("eGB(L) = " + L );
     assertTrue("isGB( { a, ,b, c, d, e } )", bb.isGB(L) );
 }

/**
 * Test Trinks7 GBase over Z.
 * 
 */ 
 @SuppressWarnings("unchecked") // not jet working
 public void testTrinks7GBaseZ() {
     String exam = "Z(B,S,T,Z,P,W) G "
                 + "( "  
                 + "( 45 P + 35 S - 165 B - 36 ), " 
                 + "( 35 P + 40 Z + 25 T - 27 S ), "
                 + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), "
                 + "( - 9 W + 15 T P + 20 S Z ), "
                 + "( P W + 2 T Z - 11 B**3 ), "
                 + "( 99 W - 11 B S + 3 B**2 ), "
                 + "( 10000 B**2 + 6600 B + 2673 ) "
                 + ") ";
     Reader source = new StringReader( exam );
     GenPolynomialTokenizer parser
                  = new GenPolynomialTokenizer( source );
     try {
         F = (PolynomialList<BigInteger>) parser.nextPolynomialSet();
     } catch(ClassCastException e) {
         fail(""+e);
     } catch(IOException e) {
         fail(""+e);
     }
     System.out.println("F = " + F);

     G = bb.GB(F.list);
     assertTrue("isGB( GB(Trinks7) )", bb.isGB(G) );
     assertEquals("#GB(Trinks7) == 6", 6, G.size() );
     PolynomialList<BigInteger> trinks 
           = new PolynomialList<BigInteger>(F.ring,G);
     System.out.println("G = " + trinks);

 }


/**
 * Test Trinks7 GBase over Q(B).
 * 
 */ 
 @SuppressWarnings("unchecked") // not jet working
 public void xtestTrinks7GBaseQ() {
     String exam = "RatFunc{ B } (S,T,Z,P,W) G "
                 + "( "  
                 + "( 45 P + 35 S - { 165 B } - { 36 } ), " 
                 + "( 35 P + 40 Z + 25 T - 27 S ), "
                 + "( 15 W + 25 S P + 30 Z - 18 T - { 165 B**2 } ), "
                 + "( - 9 W + 15 T P + 20 S Z ), "
                 + "( P W + 2 T Z - { 11 B**3 } ), "
                 + "( 99 W - { 11 B } S + { 3 B**2 } ), "
                 + "( { 10000 B**2 + 6600 B + 2673 } ) "
                 + ") ";
     Reader source = new StringReader( exam );
     GenPolynomialTokenizer parser
                  = new GenPolynomialTokenizer( source );
     EGroebnerBaseSeq<Quotient<BigInteger>> bb 
         = new EGroebnerBaseSeq<Quotient<BigInteger>>();
     PolynomialList<Quotient<BigInteger>> F = null;
     List<GenPolynomial<Quotient<BigInteger>>> G = null;
     try {
         F = (PolynomialList<Quotient<BigInteger>>) parser.nextPolynomialSet();
     } catch(ClassCastException e) {
         fail(""+e);
     } catch(IOException e) {
         fail(""+e);
     }
     System.out.println("F = " + F);

     G = bb.GB(F.list);
     assertTrue("isGB( GB(Trinks7) )", bb.isGB(G) );
     assertEquals("#GB(Trinks7) == 1", 1, G.size() );
     PolynomialList<Quotient<BigInteger>> trinks 
           = new PolynomialList<Quotient<BigInteger>>(F.ring,G);
     System.out.println("G = " + trinks);
     ComputerThreads.terminate();
 }


/**
 * Test Trinks7 GBase over Z(B).
 * 
 */ 
 @SuppressWarnings("unchecked") // not working
 public void ytestTrinks7GBaseZ() {
     String exam = "IntFunc{ B } (S,T,Z,P,W) G "
                 + "( "  
                 + "( 45 P + 35 S - { 165 B } - { 36 } ), " 
                 + "( 35 P + 40 Z + 25 T - 27 S ), "
                 + "( 15 W + 25 S P + 30 Z - 18 T - { 165 B**2 } ), "
                 + "( - 9 W + 15 T P + 20 S Z ), "
                 + "( P W + 2 T Z - { 11 B**3 } ), "
                 + "( 99 W - { 11 B } S + { 3 B**2 } ), "
                 + "( { 10000 B**2 + 6600 B + 2673 } ) "
                 + ") ";
     Reader source = new StringReader( exam );
     GenPolynomialTokenizer parser
                  = new GenPolynomialTokenizer( source );
     EGroebnerBaseSeq<GenPolynomial<BigInteger>> bb 
         = new EGroebnerBaseSeq<GenPolynomial<BigInteger>>();
     PolynomialList<GenPolynomial<BigInteger>> F = null;
     List<GenPolynomial<GenPolynomial<BigInteger>>> G = null;
     try {
         F = (PolynomialList<GenPolynomial<BigInteger>>) parser.nextPolynomialSet();
     } catch(ClassCastException e) {
         fail(""+e);
     } catch(IOException e) {
         fail(""+e);
     }
     System.out.println("F = " + F);

     G = bb.GB(F.list);
     assertTrue("isGB( GB(Trinks7) )", bb.isGB(G) );
     assertEquals("#GB(Trinks7) == 1", 1, G.size() );
     PolynomialList<GenPolynomial<BigInteger>> trinks 
           = new PolynomialList<GenPolynomial<BigInteger>>(F.ring,G);
     System.out.println("G = " + trinks);
     ComputerThreads.terminate();
 }

}
