/*
 * $Id$
 */

package edu.jas.module;

//import edu.jas.poly.GroebnerBase;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import edu.jas.poly.RatOrderedMapPolynomial;
import edu.jas.poly.RatSolvableOrderedMapPolynomial;
import edu.jas.poly.SolvableOrderedMapPolynomial;
import edu.jas.poly.OrderedPolynomial;
import edu.jas.poly.SolvablePolynomial;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.ExpVector;
import edu.jas.poly.TermOrder;
import edu.jas.poly.RelationTable;
import edu.jas.poly.WeylRelations;

//import edu.jas.ring.GroebnerBase;
import edu.jas.ring.SolvableGroebnerBase;


/**
 * SolvableSyzygy Test using JUnit 
 * @author Heinz Kredel.
 */

public class SolvableSyzygyTest extends TestCase {

    private static final Logger logger = Logger.getLogger(SolvableSyzygyTest.class);

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>SolvableSyzygyTest</CODE> object.
 * @param name String
 */
   public SolvableSyzygyTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(SolvableSyzygyTest.class);
     return suite;
   }

   int port = 4711;
   String host = "localhost";

   SolvableOrderedMapPolynomial dummy;
   OrderedPolynomial a;
   OrderedPolynomial b;
   OrderedPolynomial c;
   OrderedPolynomial d;
   OrderedPolynomial e;
   String[] vars;
   OrderedPolynomial one;
   OrderedPolynomial zero;

   RelationTable table;

   List L;
   List V;
   PolynomialList F;
   PolynomialList G;
   List M;
   List N;
   List Z;

   int rl = 4; //4; //3; 
   int kl = 5;
   int ll = 9;
   int el = 2;
   float q = 0.2f; //0.4f

   protected void setUp() {
       table = new RelationTable(); // symmetric test
       TermOrder to = new TermOrder( /*TermOrder.INVLEX*/ );
       a = b = c = d = e = null;
       a = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
       a = new RatSolvableOrderedMapPolynomial(table,to,a);
       b = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
       b = new RatSolvableOrderedMapPolynomial(table,to,b);
       c = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
       c = new RatSolvableOrderedMapPolynomial(table,to,c);
       d = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
       d = new RatSolvableOrderedMapPolynomial(table,to,d);
       e = d; //RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
       vars = ExpVector.STDVARS( rl );
       one = a.getONE();
       zero = a.getZERO();
   }

   protected void tearDown() {
       table = null;
       a = b = c = d = e = null;
   }


/**
 * Test sequential SolvableSyzygy
 * 
 */
 public void testSequentialSolvableSyzygy() {

     L = new ArrayList();

     assertTrue("not isZERO( a )", !a.isZERO() );
     L.add(a);
     assertTrue("isGB( { a } )", SolvableGroebnerBase.isLeftGB(L) );
     N = SolvableSyzygy.leftZeroRelations( L );
     assertTrue("is ZR( { a } )", SolvableSyzygy.isLeftZeroRelation(N,L) );

     assertTrue("not isZERO( b )", !b.isZERO() );
     L.add(b);
     L = SolvableGroebnerBase.leftGB(L);
     assertTrue("isGB( { a, b } )", SolvableGroebnerBase.isLeftGB(L) );
     //System.out.println("\nL = " + L );
     N = SolvableSyzygy.leftZeroRelations( L );
     //System.out.println("\nN = " + N );
     assertTrue("is ZR( { a, b } )", SolvableSyzygy.isLeftZeroRelation(N,L) );

     assertTrue("not isZERO( c )", !c.isZERO() );
     L.add(c);
     L = SolvableGroebnerBase.leftGB(L);
     //System.out.println("\nL = " + L );
     assertTrue("isGB( { a, b, c } )", SolvableGroebnerBase.isLeftGB(L) );
     N = SolvableSyzygy.leftZeroRelations( L );
     //System.out.println("\nN = " + N );
     assertTrue("is ZR( { a, b, c } )", SolvableSyzygy.isLeftZeroRelation(N,L) );

     assertTrue("not isZERO( d )", !d.isZERO() );
     L.add(d);
     L = SolvableGroebnerBase.leftGB(L);
     //System.out.println("\nL = " + L );
     assertTrue("isGB( { a, b, c, d } )", SolvableGroebnerBase.isLeftGB(L) );
     N = SolvableSyzygy.leftZeroRelations( L );
     //System.out.println("\nN = " + N );
     assertTrue("is ZR( { a, b, c, d } )", SolvableSyzygy.isLeftZeroRelation(N,L) );

     //System.out.println("N = " + N );
 }


/**
 * Test sequential Weyl SolvableSyzygy
 * 
 */
 public void testSequentialWeylSolvableSyzygy() {
     dummy = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
     TermOrder to = a.getTermOrder();
     dummy = new RatSolvableOrderedMapPolynomial(table,to,dummy);

     table = (new WeylRelations()).generate(rl,dummy);
     a = new RatSolvableOrderedMapPolynomial(table,to,a);
     b = new RatSolvableOrderedMapPolynomial(table,to,b);
     c = new RatSolvableOrderedMapPolynomial(table,to,c);
     d = new RatSolvableOrderedMapPolynomial(table,to,d);
     e = d; 

     L = new ArrayList();

     assertTrue("not isZERO( a )", !a.isZERO() );
     L.add(a);
     assertTrue("isGB( { a } )", SolvableGroebnerBase.isLeftGB(L) );
     N = SolvableSyzygy.leftZeroRelations( L );
     assertTrue("is ZR( { a } )", SolvableSyzygy.isLeftZeroRelation(N,L) );

     assertTrue("not isZERO( b )", !b.isZERO() );
     L.add(b);
     L = SolvableGroebnerBase.leftGB(L);
     assertTrue("isGB( { a, b } )", SolvableGroebnerBase.isLeftGB(L) );
     System.out.println("\nL = " + L );
     N = SolvableSyzygy.leftZeroRelations( L );
     System.out.println("\nN = " + N );
     assertTrue("is ZR( { a, b } )", SolvableSyzygy.isLeftZeroRelation(N,L) );

     // useless since 1 in GB
     assertTrue("not isZERO( c )", !c.isZERO() );
     L.add(c);
     L = SolvableGroebnerBase.leftGB(L);
     System.out.println("\nL = " + L );
     assertTrue("isGB( { a, b, c } )", SolvableGroebnerBase.isLeftGB(L) );
     N = SolvableSyzygy.leftZeroRelations( L );
     System.out.println("\nN = " + N );
     assertTrue("is ZR( { a, b, c } )", SolvableSyzygy.isLeftZeroRelation(N,L) );

     // useless since 1 in GB
     assertTrue("not isZERO( d )", !d.isZERO() );
     L.add(d);
     L = SolvableGroebnerBase.leftGB(L);
     System.out.println("\nL = " + L );
     assertTrue("isGB( { a, b, c, d } )", SolvableGroebnerBase.isLeftGB(L) );
     N = SolvableSyzygy.leftZeroRelations( L );
     System.out.println("\nN = " + N );
     assertTrue("is ZR( { a, b, c, d } )", SolvableSyzygy.isLeftZeroRelation(N,L) );

     //System.out.println("N = " + N );
 }


/**
 * Test sequential module SolvableSyzygy
 * 
 */
/*
 public void testSequentialModSolvableSyzygy() {

     ModuleList M;
     ModuleList N;
     ModuleList Z;
     L = new ArrayList();

     assertTrue("not isZERO( a )", !a.isZERO() );
     V = new ArrayList();
     V.add(a); V.add(zero); V.add(one);
     L.add(V);
     M = new ModuleList(vars,a.getTermOrder(),L);
     assertTrue("isGB( { (a,0,1) } )", ModGroebnerBase.isGB(M) );

     N = ModGroebnerBase.GB( M );
     assertTrue("isGB( { (a,0,1) } )", ModGroebnerBase.isGB(N) );

     Z = SolvableSyzygy.leftZeroRelations(N);
     //System.out.println("Z = " + Z);
     assertTrue("is ZR( { a) } )", SolvableSyzygy.isLeftZeroRelation(Z,N) );

     assertTrue("not isZERO( b )", !b.isZERO() );
     V = new ArrayList();
     V.add(b); V.add(one); V.add(zero);
     L.add(V);
     M = new ModuleList(vars,a.getTermOrder(),L);
     //System.out.println("L = " + L.size() );

     N = ModGroebnerBase.GB( M );
     assertTrue("isGB( { a, b } )", ModGroebnerBase.isGB(N) );

     Z = SolvableSyzygy.leftZeroRelations(N);
     //System.out.println("Z = " + Z);
     assertTrue("is ZR( { a, b } )", SolvableSyzygy.isLeftZeroRelation(Z,N) );

     assertTrue("not isZERO( c )", !c.isZERO() );
     V = new ArrayList();
     V.add(c); V.add(one); V.add(zero);
     L.add(V);
     M = new ModuleList(vars,a.getTermOrder(),L);
     //System.out.println("L = " + L.size() );

     N = ModGroebnerBase.GB( M );
     //System.out.println("GB(M) = " + N);
     assertTrue("isGB( { a,b,c) } )", ModGroebnerBase.isGB(N) );

     Z = SolvableSyzygy.leftZeroRelations(N);
     //System.out.println("Z = " + Z);
     //boolean b = SolvableSyzygy.isLeftZeroRelation(Z,N);
     //System.out.println("boolean = " + b);
     assertTrue("is ZR( { a,b,c } )", SolvableSyzygy.isLeftZeroRelation(Z,N) );
 }
*/

}
