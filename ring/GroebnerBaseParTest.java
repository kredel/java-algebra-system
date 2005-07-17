/*
 * $Id$
 */

package edu.jas.ring;

//import edu.jas.poly.GroebnerBase;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;

import edu.jas.ring.GroebnerBase;

/**
 * GroebnerBase Test using JUnit 
 * @author Heinz Kredel.
 */

public class GroebnerBaseParTest extends TestCase {

    private static final Logger logger = Logger.getLogger(GroebnerBaseParTest.class);

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>GroebnerBaseParTest</CODE> object.
 * @param name String
 */
   public GroebnerBaseParTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(GroebnerBaseParTest.class);
     return suite;
   }

   GenPolynomialRing<BigRational> fac;

   List<GenPolynomial<BigRational>> L;
   PolynomialList<BigRational> F;
   List<GenPolynomial<BigRational>> G;

   GenPolynomial<BigRational> a;
   GenPolynomial<BigRational> b;
   GenPolynomial<BigRational> c;
   GenPolynomial<BigRational> d;
   GenPolynomial<BigRational> e;

   int rl = 3; //4; //3; 
   int kl = 10;
   int ll = 7;
   int el = 3;
   float q = 0.2f; //0.4f

   int threads = 2;

   protected void setUp() {
       BigRational coeff = new BigRational(9);
       fac = new GenPolynomialRing<BigRational>(coeff,rl);
       a = b = c = d = e = null;
   }

   protected void tearDown() {
       a = b = c = d = e = null;
       fac = null;
   }


/**
 * Test parallel GBase
 * 
 */
 public void testParallelGBase() {

     L = new ArrayList<GenPolynomial<BigRational>>();

     a = fac.random(kl, ll, el, q );
     b = fac.random(kl, ll, el, q );
     c = fac.random(kl, ll, el, q );
     d = fac.random(kl, ll, el, q );
     e = d; //fac.random(kl, ll, el, q );

     assertTrue("not isZERO( a )", !a.isZERO() );
     L.add(a);

     L = GroebnerBaseParallel.GB( L, threads );
     assertTrue("isGB( { a } )", GroebnerBase.isGB(L) );

     assertTrue("not isZERO( b )", !b.isZERO() );
     L.add(b);
     //System.out.println("L = " + L.size() );

     L = GroebnerBaseParallel.GB( L, threads );
     assertTrue("isGB( { a, b } )", GroebnerBase.isGB(L) );

     assertTrue("not isZERO( c )", !c.isZERO() );
     L.add(c);

     L = GroebnerBaseParallel.GB( L, threads );
     assertTrue("isGB( { a, ,b, c } )", GroebnerBase.isGB(L) );

     assertTrue("not isZERO( d )", !d.isZERO() );
     L.add(d);

     L = GroebnerBaseParallel.GB( L, threads );
     assertTrue("isGB( { a, ,b, c, d } )", GroebnerBase.isGB(L) );

     assertTrue("not isZERO( e )", !e.isZERO() );
     L.add(e);

     L = GroebnerBaseParallel.GB( L, threads );
     assertTrue("isGB( { a, ,b, c, d, e } )", GroebnerBase.isGB(L) );
 }


/**
 * Test compare sequential with parallel GBase
 * 
 */
 public void testSequentialParallelGBase() {

     ArrayList<GenPolynomial<BigRational>> Gs, Gp;

     L = new ArrayList<GenPolynomial<BigRational>>();

     a = fac.random(kl, ll, el, q );
     b = fac.random(kl, ll, el, q );
     c = fac.random(kl, ll, el, q );
     d = fac.random(kl, ll, el, q );
     e = d; //fac.random(kl, ll, el, q );

     L.add(a);
     Gs = GroebnerBase.GB( L );
     Gp = GroebnerBaseParallel.GB( L, threads );

     assertTrue("Gs.containsAll(Gp)", Gs.containsAll(Gp) );
     assertTrue("Gp.containsAll(Gs)", Gp.containsAll(Gs) );

     L = Gs;
     L.add(b);
     Gs = GroebnerBase.GB( L );
     Gp = GroebnerBaseParallel.GB( L, threads );

     assertTrue("Gs.containsAll(Gp)", Gs.containsAll(Gp) );
     assertTrue("Gp.containsAll(Gs)", Gp.containsAll(Gs) );

     L = Gs;
     L.add(c);
     Gs = GroebnerBase.GB( L );
     Gp = GroebnerBaseParallel.GB( L, threads );

     assertTrue("Gs.containsAll(Gp)", Gs.containsAll(Gp) );
     assertTrue("Gp.containsAll(Gs)", Gp.containsAll(Gs) );

     L = Gs;
     L.add(d);
     Gs = GroebnerBase.GB( L );
     Gp = GroebnerBaseParallel.GB( L, threads );

     assertTrue("Gs.containsAll(Gp)", Gs.containsAll(Gp) );
     assertTrue("Gp.containsAll(Gs)", Gp.containsAll(Gs) );

     L = Gs;
     L.add(e);
     Gs = GroebnerBase.GB( L );
     Gp = GroebnerBaseParallel.GB( L, threads );

     assertTrue("Gs.containsAll(Gp)", Gs.containsAll(Gp) );
     assertTrue("Gp.containsAll(Gs)", Gp.containsAll(Gs) );
 }


/**
 * Test Trinks7 GBase
 * 
 */
 public void testTrinks7GBase() {
     String exam = "(B,S,T,Z,P,W) L "
                 + "( "  
                 + "( 45 P + 35 S - 165 B - 36 ), " 
                 + "( 35 P + 40 Z + 25 T - 27 S ), "
                 + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), "
                 + "( - 9 W + 15 T P + 20 S Z ), "
                 + "( P W + 2 T Z - 11 B**3 ), "
                 + "( 99 W - 11 B S + 3 B**2 ), "
                 + "( B**2 + 33/50 B + 2673/10000 ) "
                 + ") ";
     Reader source = new StringReader( exam );
     GenPolynomialTokenizer parser
                  = new GenPolynomialTokenizer( source );
     try {
         F = parser.nextPolynomialSet();
     } catch(IOException e) {
         fail(""+e);
     }
     //System.out.println("F = " + F);

     G = GroebnerBaseParallel.GB(F.list, threads);
     assertTrue("isGB( GB(Trinks7) )", GroebnerBase.isGB(G) );
     assertEquals("#GB(Trinks7) == 6", 6, G.size() );
     PolynomialList<BigRational> trinks 
           = new PolynomialList<BigRational>(F.ring,G);
     //System.out.println("G = " + trinks);

 }

}
