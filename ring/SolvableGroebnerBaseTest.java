/*
 * $Id$
 */

package edu.jas.ring;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import edu.jas.poly.RatSolvableOrderedMapPolynomial;
import edu.jas.poly.OrderedPolynomial;
import edu.jas.poly.SolvablePolynomial;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.RelationTable;
import edu.jas.poly.WeylRelations;

/**
 * SolvableGroebnerBase Test using JUnit 
 * @author Heinz Kredel.
 */

public class SolvableGroebnerBaseTest extends TestCase {

    private static final Logger logger = Logger.getLogger(SolvableGroebnerBaseTest.class);

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>SolvableGroebnerBaseTest</CODE> object.
 * @param name String
 */
   public SolvableGroebnerBaseTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(SolvableGroebnerBaseTest.class);
     return suite;
   }

   int port = 4711;
   String host = "localhost";

   RelationTable table;

   SolvablePolynomial dummy;
   SolvablePolynomial a;
   SolvablePolynomial b;
   SolvablePolynomial c;
   SolvablePolynomial d;
   SolvablePolynomial e;

   List L;
   PolynomialList F;
   PolynomialList G;

   int rl = 4; //4; //3; 
   int kl = 10;
   int ll = 3;
   int el = 2;
   float q = 0.2f; //0.4f

   protected void setUp() {
       table = new RelationTable();
       a = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
       b = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
       c = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
       d = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
       e = d; //RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
   }

   protected void tearDown() {
       a = b = c = d = e = null;
   }


/**
 * Test sequential GBase
 * 
 */
 public void testSequentialGBase() {

     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();
     L.add(a);

     L = SolvableGroebnerBase.leftDIRPGB( L );
     assertTrue("isLeftDIRPGB( { a } )", SolvableGroebnerBase.isLeftDIRPGB(L) );

     assertTrue("not isZERO( b )", !b.isZERO() );
     L.add(b);
     //System.out.println("L = " + L.size() );

     L = SolvableGroebnerBase.leftDIRPGB( L );
     assertTrue("isLeftDIRPGB( { a, b } )", SolvableGroebnerBase.isLeftDIRPGB(L) );

     assertTrue("not isZERO( c )", !c.isZERO() );
     L.add(c);

     L = SolvableGroebnerBase.leftDIRPGB( L );
     assertTrue("isLeftDIRPGB( { a, ,b, c } )", SolvableGroebnerBase.isLeftDIRPGB(L) );

     assertTrue("not isZERO( d )", !d.isZERO() );
     L.add(d);

     L = SolvableGroebnerBase.leftDIRPGB( L );
     assertTrue("isLeftDIRPGB( { a, ,b, c, d } )", SolvableGroebnerBase.isLeftDIRPGB(L) );

     assertTrue("not isZERO( e )", !e.isZERO() );
     L.add(e);

     L = SolvableGroebnerBase.leftDIRPGB( L );
     assertTrue("isLeftDIRPGB( { a, ,b, c, d, e } )", SolvableGroebnerBase.isLeftDIRPGB(L) );
 }


/**
 * Test Weyl sequential GBase
 * 
 */
 public void testWeylSequentialGBase() {

     dummy = RatSolvableOrderedMapPolynomial.DIRRAS(table/*null*/,rl, kl, ll, el, q );
     table = (new WeylRelations()).generate(rl,dummy);
     dummy = null;

     a = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
     b = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
     c = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
     d = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
     e = d; //RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );

     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();
     L.add(a);

     L = SolvableGroebnerBase.leftDIRPGB( L );
     assertTrue("isLeftDIRPGB( { a } )", SolvableGroebnerBase.isLeftDIRPGB(L) );

     assertTrue("not isZERO( b )", !b.isZERO() );
     L.add(b);
     //System.out.println("L = " + L.size() );

     L = SolvableGroebnerBase.leftDIRPGB( L );
     assertTrue("isLeftDIRPGB( { a, b } )", SolvableGroebnerBase.isLeftDIRPGB(L) );

     assertTrue("not isZERO( c )", !c.isZERO() );
     L.add(c);

     L = SolvableGroebnerBase.leftDIRPGB( L );
     assertTrue("isLeftDIRPGB( { a, ,b, c } )", SolvableGroebnerBase.isLeftDIRPGB(L) );

     assertTrue("not isZERO( d )", !d.isZERO() );
     L.add(d);

     L = SolvableGroebnerBase.leftDIRPGB( L );
     assertTrue("isLeftDIRPGB( { a, ,b, c, d } )", SolvableGroebnerBase.isLeftDIRPGB(L) );

     assertTrue("not isZERO( e )", !e.isZERO() );
     L.add(e);

     L = SolvableGroebnerBase.leftDIRPGB( L );
     assertTrue("isLeftDIRPGB( { a, ,b, c, d, e } )", SolvableGroebnerBase.isLeftDIRPGB(L) );
 }

}
