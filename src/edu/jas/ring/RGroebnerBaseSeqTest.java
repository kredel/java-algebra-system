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


import edu.jas.structure.Product;
import edu.jas.structure.ProductRing;

import edu.jas.arith.BigRational;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;

import edu.jas.ring.GroebnerBase;

/**
 * R-Groebner base sequential tests with JUnit.
 * @author Heinz Kredel.
 */

public class RGroebnerBaseSeqTest extends TestCase {

    //private static final Logger logger = Logger.getLogger(RGroebnerBaseSeqTest.class);

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>RGroebnerBaseSeqTest</CODE> object.
 * @param name String.
 */
   public RGroebnerBaseSeqTest(String name) {
          super(name);
   }

/**
 * suite.
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(RGroebnerBaseSeqTest.class);
     return suite;
   }

   ProductRing<BigRational> pfac;
   GenPolynomialRing<Product<BigRational>> fac;

   List<GenPolynomial<Product<BigRational>>> L;
   PolynomialList<Product<BigRational>> F;
   List<GenPolynomial<Product<BigRational>>> G;

   GroebnerBase<Product<BigRational>> bb;

   GenPolynomial<Product<BigRational>> a;
   GenPolynomial<Product<BigRational>> b;
   GenPolynomial<Product<BigRational>> c;
   GenPolynomial<Product<BigRational>> d;
   GenPolynomial<Product<BigRational>> e;

   int rl = 3; //4; //3; 
   int kl = 10;
   int ll = 7;
   int el = 3;
   float q = 0.2f; //0.4f

   protected void setUp() {
       BigRational coeff = new BigRational(9);
       pfac = new ProductRing<BigRational>(coeff,4);
       fac = new GenPolynomialRing<Product<BigRational>>(pfac,rl);
       a = b = c = d = e = null;
       bb = new RGroebnerBaseSeq<Product<BigRational>>();
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
 public void testSequentialGBase() {

     L = new ArrayList<GenPolynomial<Product<BigRational>>>();

     a = fac.random(kl, ll, el, q );
     b = fac.random(kl, ll, el, q );
     c = fac.random(kl, ll, el, q );
     d = fac.random(kl, ll, el, q );
     e = d; //fac.random(kl, ll, el, q );

     if ( a.isZERO() || b.isZERO() || c.isZERO() || d.isZERO() ) {
        return;
     }

     assertTrue("not isZERO( a )", !a.isZERO() );
     L.add(a);

     L = bb.GB( L );
     assertTrue("isGB( { a } )", bb.isGB(L) );
     System.out.println("L = " + L );

     assertTrue("not isZERO( b )", !b.isZERO() );
     L.add(b);
     //System.out.println("L = " + L.size() );

     L = bb.GB( L );
     assertTrue("isGB( { a, b } )", bb.isGB(L) );
     System.out.println("L = " + L );

     assertTrue("not isZERO( c )", !c.isZERO() );
     L.add(c);

     L = bb.GB( L );
     assertTrue("isGB( { a, ,b, c } )", bb.isGB(L) );
     System.out.println("L = " + L );

     assertTrue("not isZERO( d )", !d.isZERO() );
     L.add(d);

     L = bb.GB( L );
     assertTrue("isGB( { a, ,b, c, d } )", bb.isGB(L) );
     System.out.println("L = " + L );

     assertTrue("not isZERO( e )", !e.isZERO() );
     L.add(e);

     L = bb.GB( L );
     assertTrue("isGB( { a, ,b, c, d, e } )", bb.isGB(L) );
     System.out.println("L = " + L );
 }


}
