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

import edu.jas.poly.RatSolvableOrderedMapPolynomial;
//import edu.jas.poly.OrderedPolynomial;
import edu.jas.poly.SolvablePolynomial;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.ExpVector;
import edu.jas.poly.RelationTable;
import edu.jas.poly.WeylRelations;

/**
 * ModSolvableGroebnerBase Test using JUnit 
 * @author Heinz Kredel.
 */

public class ModSolvableGroebnerBaseTest extends TestCase {

    private static final Logger logger = Logger.getLogger(ModSolvableGroebnerBaseTest.class);

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>ModSolvableGroebnerBaseTest</CODE> object.
 * @param name String
 */
   public ModSolvableGroebnerBaseTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(ModSolvableGroebnerBaseTest.class);
     return suite;
   }

   int port = 4711;
   String host = "localhost";

   SolvablePolynomial a;
   SolvablePolynomial b;
   SolvablePolynomial c;
   SolvablePolynomial d;
   SolvablePolynomial e;
   String[] vars;
   SolvablePolynomial one;
   SolvablePolynomial zero;
   SolvablePolynomial dummy;

   RelationTable table;

   List L;
   List V;
   PolynomialList F;
   PolynomialList G;
   ModuleList M;
   ModuleList N;

   int rl = 4; //4; //3; 
   int kl = 8;
   int ll = 5;
   int el = 2;
   float q = 0.2f; //0.4f

   protected void setUp() {
       table = new RelationTable();
       a = b = c = d = e = null;
       a = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
       b = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
       c = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
       d = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
       e = d; //RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
       vars = ExpVector.STDVARS( rl );
       one =  (SolvablePolynomial)a.getONE();
       zero = (SolvablePolynomial)a.getZERO();
   }

   protected void tearDown() {
       table = null;
       a = b = c = d = e = null;
   }


/**
 * Test sequential left GBase
 * 
 */
 public void testSequentialModSolvableGB() {

     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();

     V = new ArrayList();
     V.add(a); V.add(zero); V.add(one);
     L.add(V);
     M = new ModuleList(vars,a.getTermOrder(),L,table);
     assertTrue("isLeftGB( { (a,0,1) } )", ModSolvableGroebnerBase.isLeftGB(M) );
     //System.out.println("M = " + M );

     N = ModSolvableGroebnerBase.leftGB( M );
     //System.out.println("N = " + N );
     assertTrue("isLeftGB( { (a,0,1) } )", ModSolvableGroebnerBase.isLeftGB(N) );

     assertTrue("not isZERO( b )", !b.isZERO() );
     V = new ArrayList();
     V.add(b); V.add(one); V.add(zero);
     L.add(V);
     M = new ModuleList(vars,a.getTermOrder(),L,table);
     //System.out.println("L = " + L.size() );

     N = ModSolvableGroebnerBase.leftGB( M );
     assertTrue("isLeftGB( { (a,0,1),(b,1,0) } )", ModSolvableGroebnerBase.isLeftGB(N) );
     //System.out.println("N = " + N );

     assertTrue("not isZERO( c )", !c.isZERO() );
     V = new ArrayList();
     V.add(c); V.add(zero); V.add(zero);
     L.add(V);
     M = new ModuleList(vars,a.getTermOrder(),L,table);
     //System.out.println("M = " + M );
     //System.out.println("L = " + L.size() );

     N = ModSolvableGroebnerBase.leftGB( M );
     assertTrue("isLeftGB( { (a,),(b,),(c,) } )", ModSolvableGroebnerBase.isLeftGB(N) );
     //System.out.println("N = " + N );

     assertTrue("not isZERO( d )", !d.isZERO() );
     V = new ArrayList();
     V.add(d); V.add(zero); V.add(zero);
     L.add(V);
     M = new ModuleList(vars,a.getTermOrder(),L,table);
     //System.out.println("M = " + M );
     //System.out.println("L = " + L.size() );

     N = ModSolvableGroebnerBase.leftGB( M );
     assertTrue("isLeftGB( { (a,b,c,d) } )", ModSolvableGroebnerBase.isLeftGB(N) );
     //System.out.println("N = " + N );

 }


/**
 * Test sequential twosided GBase
 * 
 */
 public void testSequentialModTSSolvableGB() {

     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();

     V = new ArrayList();
     V.add(a); V.add(zero); V.add(one);
     L.add(V);
     M = new ModuleList(vars,a.getTermOrder(),L,table);
     assertTrue("isTwosidedGB( { (a,0,1) } )", ModSolvableGroebnerBase.isTwosidedGB(M) );

     N = ModSolvableGroebnerBase.twosidedGB( M );
     assertTrue("isTwosidedGB( { (a,0,1) } )", ModSolvableGroebnerBase.isTwosidedGB(N) );

     assertTrue("not isZERO( b )", !b.isZERO() );
     V = new ArrayList();
     V.add(b); V.add(one); V.add(zero);
     L.add(V);
     M = new ModuleList(vars,a.getTermOrder(),L,table);
     //System.out.println("L = " + L.size() );

     N = ModSolvableGroebnerBase.twosidedGB( M );
     assertTrue("isTwosidedGB( { (a,0,1),(b,1,0) } )", ModSolvableGroebnerBase.isTwosidedGB(N) );
     //System.out.println("N = " + N );

     assertTrue("not isZERO( c )", !c.isZERO() );
     V = new ArrayList();
     V.add(c); V.add(zero); V.add(zero);
     L.add(V);
     M = new ModuleList(vars,a.getTermOrder(),L,table);
     //System.out.println("L = " + L.size() );

     N = ModSolvableGroebnerBase.twosidedGB( M );
     assertTrue("isTwosidedGB( { (a,),(b,),(c,) } )", ModSolvableGroebnerBase.isTwosidedGB(N) );
     //System.out.println("N = " + N );

     assertTrue("not isZERO( d )", !d.isZERO() );
     V = new ArrayList();
     V.add(d); V.add(zero); V.add(zero);
     L.add(V);
     M = new ModuleList(vars,a.getTermOrder(),L,table);
     //System.out.println("L = " + L.size() );

     N = ModSolvableGroebnerBase.twosidedGB( M );
     assertTrue("isTwosidedGB( { (a,b,c,d) } )", ModSolvableGroebnerBase.isTwosidedGB(N) );
     //System.out.println("N = " + N );

 }


/**
 * Test sequential Weyl GBase
 * 
 */
 public void testSequentialModSolvableWeylGB() {
     dummy = RatSolvableOrderedMapPolynomial.DIRRAS(table/*null*/,rl, kl, ll, el, q );
     table = (new WeylRelations()).generate(rl,dummy);
     dummy = null;

     a = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
     b = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
     c = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
     d = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
     e = d; //RatSolvableOrderedMapPolynomial.DIRRAS(table,rl, kl, ll, el, q );
     one =  (SolvablePolynomial)a.getONE(table);
     zero = (SolvablePolynomial)a.getZERO(table);

     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();

     V = new ArrayList();
     V.add(a); V.add(zero); V.add(one);
     L.add(V);
     M = new ModuleList(vars,a.getTermOrder(),L,table);
     assertTrue("isLeftGB( { (a,0,1) } )", ModSolvableGroebnerBase.isLeftGB(M) );

     N = ModSolvableGroebnerBase.leftGB( M );
     assertTrue("isLeftGB( { (a,0,1) } )", ModSolvableGroebnerBase.isLeftGB(N) );

     assertTrue("not isZERO( b )", !b.isZERO() );
     V = new ArrayList();
     V.add(b); V.add(one); V.add(zero);
     L.add(V);
     M = new ModuleList(vars,a.getTermOrder(),L,table);
     //System.out.println("L = " + L.size() );

     N = ModSolvableGroebnerBase.leftGB( M );
     assertTrue("isLeftGB( { (a,0,1),(b,1,0) } )", ModSolvableGroebnerBase.isLeftGB(N) );
     //System.out.println("N = " + N );

     assertTrue("not isZERO( c )", !c.isZERO() );
     V = new ArrayList();
     V.add(c); V.add(zero); V.add(zero);
     L.add(V);
     M = new ModuleList(vars,a.getTermOrder(),L,table);
     //System.out.println("M = " + M );
     //System.out.println("L = " + L.size() );

     N = ModSolvableGroebnerBase.leftGB( M );
     assertTrue("isLeftGB( { (a,),(b,),(c,) } )", ModSolvableGroebnerBase.isLeftGB(N) );
     //System.out.println("N = " + N );

     assertTrue("not isZERO( d )", !d.isZERO() );
     V = new ArrayList();
     V.add(d); V.add(zero); V.add(zero);
     L.add(V);
     M = new ModuleList(vars,a.getTermOrder(),L,table);
     //System.out.println("M = " + M );
     //System.out.println("L = " + L.size() );

     N = ModSolvableGroebnerBase.leftGB( M );
     assertTrue("isLeftGB( { (a,b,c,d) } )", ModSolvableGroebnerBase.isLeftGB(N) );
     //System.out.println("N = " + N );

 }

}
