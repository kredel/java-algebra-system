/*
 * $Id$
 */

package edu.jas.ring;

//import edu.jas.poly.RatGBase;

import java.util.List;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.poly.SolvablePolynomial;
import edu.jas.poly.OrderedPolynomial;
import edu.jas.poly.RatSolvableOrderedMapPolynomial;
import edu.jas.poly.ComplexSolvableOrderedMapPolynomial;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.RelationTable;
import edu.jas.poly.WeylRelations;

/**
 * Reduction Test using JUnit 
 * @author Heinz Kredel.
 */

public class ReductionSolvableTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>ReductionSolvableTest</CODE> object.
 * @param name String
 */
   public ReductionSolvableTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(ReductionSolvableTest.class);
     return suite;
   }

   private final static int bitlen = 100;

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

   int rl = 4; 
   int kl = 10;
   int ll = 5;
   int el = 3;
   float q = 0.4f;

   protected void setUp() {
       a = b = c = d = e = null;
       table = null;
   }

   protected void tearDown() {
       a = b = c = d = e = null;
       table = null;
   }


/**
 * Test constants and empty list reduction
 * 
 */
 public void testRatReduction0() {
     L = new ArrayList();
     table = new RelationTable();

     a = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
     //c = RatSolvableOrderedMapPolynomial.getONE(table);
     //d = RatSolvableOrderedMapPolynomial.getZERO(table);
     c = a.getONE(table);
     d = a.getZERO(table);

     e = Reduction.leftNormalform( L, c );
     assertTrue("isONE( e )", e.isONE() ); 

     e = Reduction.leftNormalform( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 


     L.add( c );
     e = Reduction.leftNormalform( L, c );
     assertTrue("isZERO( e )", e.isZERO() ); 

     // e = Reduction.leftNormalform( L, a );
     // assertTrue("isZERO( e )", e.isZERO() ); 

     e = Reduction.leftNormalform( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 


     L = new ArrayList();
     L.add( d );
     e = Reduction.leftNormalform( L, c );
     assertTrue("isONE( e )", e.isONE() ); 

     e = Reduction.leftNormalform( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 
 }

/**
 * Test constants and empty list reduction
 * 
 */
 public void testWeylRatReduction0() {
     L = new ArrayList();

     dummy = RatSolvableOrderedMapPolynomial.DIRRAS(table/*null*/,rl, kl, ll, el, q );
     table = (new WeylRelations()).generate(rl,dummy);
     dummy = null;

     a = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
     //c = RatSolvableOrderedMapPolynomial.getONE(table);
     //d = RatSolvableOrderedMapPolynomial.getZERO(table);
     c = a.getONE(table);
     d = a.getZERO(table);

     e = Reduction.leftNormalform( L, c );
     assertTrue("isONE( e )", e.isONE() ); 

     e = Reduction.leftNormalform( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 


     L.add( c );
     e = Reduction.leftNormalform( L, c );
     assertTrue("isZERO( e )", e.isZERO() ); 

     // e = Reduction.leftNormalform( L, a );
     // assertTrue("isZERO( e )", e.isZERO() ); 

     e = Reduction.leftNormalform( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 


     L = new ArrayList();
     L.add( d );
     e = Reduction.leftNormalform( L, c );
     assertTrue("isONE( e )", e.isONE() ); 

     e = Reduction.leftNormalform( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 
 }


/**
 * Test ReductionMod with constants and empty list reduction
 * 
 public void testRatReduction1() {
     L = new ArrayList();
     table = new RelationTable();

     //a = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
     c = RatSolvableOrderedMapPolynomial.ONE;
     d = RatSolvableOrderedMapPolynomial.ZERO;

     e = Reduction.leftNormalformMod( L, c );
     assertTrue("isONE( e )", e.isONE() ); 

     e = Reduction.leftNormalformMod( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 


     L.add( c );
     e = Reduction.leftNormalformMod( L, c );
     assertTrue("isZERO( e )", e.isZERO() ); 

     // e = Reduction.leftNormalform( L, a );
     // assertTrue("isZERO( e )", e.isZERO() ); 

     e = Reduction.leftNormalformMod( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 


     L = new ArrayList();
     L.add( d );
     e = Reduction.leftNormalformMod( L, c );
     assertTrue("isONE( e )", e.isONE() ); 

     e = Reduction.leftNormalformMod( L, d );
     assertTrue("isZERO( e )", e.isZERO() ); 
 }
 */


/**
 * Test Rat reduction
 * 
 */
 public void testRatReduction() {

     table = new RelationTable();

     a = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
     b = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );

     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();
     L.add(a);

     e = Reduction.leftNormalform( L, a );
     assertTrue("isZERO( e )", e.isZERO() );

     assertTrue("not isZERO( b )", !b.isZERO() );

     L.add(b);
     e = Reduction.leftNormalform( L, a );
     assertTrue("isZERO( e ) some times", e.isZERO() ); 
 }


/**
 * Test Weyl Rat reduction
 * 
 */
 public void testWeylRatReduction() {

     dummy = RatSolvableOrderedMapPolynomial.DIRRAS(table/*null*/,rl, kl, ll, el, q );
     table = (new WeylRelations()).generate(rl,dummy);
     dummy = null;

     a = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
     b = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );

     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();
     L.add(a);

     e = Reduction.leftNormalform( L, a );
     assertTrue("isZERO( e )", e.isZERO() );

     assertTrue("not isZERO( b )", !b.isZERO() );

     L.add(b);
     e = Reduction.leftNormalform( L, a );
     assertTrue("isZERO( e ) some times", e.isZERO() ); 
 }


/**
 * Test Rat reduction Mod
 * 
 public void testRatReductionMod() {

     table = new RelationTable();

     a = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
     b = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );

     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();
     L.add(a);

     e = Reduction.leftNormalformMod( L, a );
     assertTrue("isZERO( e )", e.isZERO() );

     assertTrue("not isZERO( b )", !b.isZERO() );

     L.add(b);
     e = Reduction.leftNormalformMod( L, a );
     assertTrue("isZERO( e ) some times", e.isZERO() ); 
 }
 */


/**
 * Test Complex reduction
 * 
 */
 public void testComplexReduction() {

     dummy = RatSolvableOrderedMapPolynomial.DIRRAS(table/*null*/,rl, kl, ll, el, q );
     table = (new WeylRelations()).generate(rl,dummy);
     dummy = null;

     a = ComplexSolvableOrderedMapPolynomial.DICRAS(table,rl, kl, ll, el, q );
     b = ComplexSolvableOrderedMapPolynomial.DICRAS(table,rl, kl, ll, el, q );

     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();
     L.add(a);

     e = Reduction.leftNormalform( L, a );
     assertTrue("isZERO( e )", e.isZERO() );

     assertTrue("not isZERO( b )", !b.isZERO() );

     L.add(b);
     e = Reduction.leftNormalform( L, a );
     assertTrue("isZERO( e ) some times", e.isZERO() ); 
 }


/**
 * Test Weyl Complex reduction
 * 
 */
 public void testWeylComplexReduction() {

     table = new RelationTable();

     a = ComplexSolvableOrderedMapPolynomial.DICRAS(table,rl, kl, ll, el, q );
     b = ComplexSolvableOrderedMapPolynomial.DICRAS(table,rl, kl, ll, el, q );

     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();
     L.add(a);

     e = Reduction.leftNormalform( L, a );
     assertTrue("isZERO( e )", e.isZERO() );

     assertTrue("not isZERO( b )", !b.isZERO() );

     L.add(b);
     e = Reduction.leftNormalform( L, a );
     assertTrue("isZERO( e ) some times", e.isZERO() ); 
 }


}
