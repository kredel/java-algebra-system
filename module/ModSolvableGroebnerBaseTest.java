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

import edu.jas.structure.RingElem;

import edu.jas.arith.BigRational;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.TermOrder;
import edu.jas.poly.RelationTable;
import edu.jas.poly.WeylRelations;

import edu.jas.ring.Reduction;
import edu.jas.ring.GroebnerBase;

import edu.jas.module.ModuleList;


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

   BigRational cfac;
   GenSolvablePolynomialRing<BigRational> pfac;

   GenSolvablePolynomial<BigRational> a;
   GenSolvablePolynomial<BigRational> b;
   GenSolvablePolynomial<BigRational> c;
   GenSolvablePolynomial<BigRational> d;
   GenSolvablePolynomial<BigRational> e;
   String[] vars;
   GenSolvablePolynomial<BigRational> one;
   GenSolvablePolynomial<BigRational> zero;

   RelationTable<BigRational> table;

   List<List<GenSolvablePolynomial<BigRational>>> L;
   List<GenSolvablePolynomial<BigRational>> V;
   PolynomialList<BigRational> F;
   PolynomialList<BigRational> G;
   ModuleList<BigRational> M;
   ModuleList<BigRational> N;

   int rl = 4; //4; //3; 
   int kl = 8;
   int ll = 4;
   int el = 2;
   float q = 0.2f; //0.4f

   protected void setUp() {
       a = b = c = d = e = null;

       cfac = new BigRational(1);
       TermOrder tord = new TermOrder();
       String[] vars = ExpVector.STDVARS(rl);
       pfac = new GenSolvablePolynomialRing<BigRational>(cfac,rl,tord,vars);

       a = pfac.random(kl, ll, el, q );
       b = pfac.random(kl, ll, el, q );
       c = pfac.random(kl, ll, el, q );
       d = pfac.random(kl, ll, el, q );
       e = d; // = pfac.random(kl, ll, el, q );
       one =  pfac.getONE();
       zero = pfac.getZERO();
   }

   protected void tearDown() {
       a = b = c = d = e = null;
       one = null;
       zero = null;
   }


/**
 * Test sequential left GBase
 * 
 */
 public void testSequentialModSolvableGB() {

     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList<List<GenSolvablePolynomial<BigRational>>>();

     V = new ArrayList<GenSolvablePolynomial<BigRational>>();
     V.add(a); V.add(zero); V.add(one);
     L.add(V);
     M = new ModuleList<BigRational>(pfac,L);
     assertTrue("isLeftGB( { (a,0,1) } )", ModSolvableGroebnerBase.isLeftGB(M) );
     //System.out.println("M = " + M );

     N = ModSolvableGroebnerBase.leftGB( M );
     //System.out.println("N = " + N );
     assertTrue("isLeftGB( { (a,0,1) } )", ModSolvableGroebnerBase.isLeftGB(N) );

     assertTrue("not isZERO( b )", !b.isZERO() );
     V = new ArrayList<GenSolvablePolynomial<BigRational>>();
     V.add(b); V.add(one); V.add(zero);
     L.add(V);
     M = new ModuleList<BigRational>(pfac,L);
     //System.out.println("L = " + L.size() );

     N = ModSolvableGroebnerBase.leftGB( M );
     assertTrue("isLeftGB( { (a,0,1),(b,1,0) } )", ModSolvableGroebnerBase.isLeftGB(N) );
     //System.out.println("N = " + N );

     assertTrue("not isZERO( c )", !c.isZERO() );
     V = new ArrayList<GenSolvablePolynomial<BigRational>>();
     V.add(c); V.add(zero); V.add(zero);
     L.add(V);
     M = new ModuleList<BigRational>(pfac,L);
     //System.out.println("M = " + M );
     //System.out.println("L = " + L.size() );

     N = ModSolvableGroebnerBase.leftGB( M );
     assertTrue("isLeftGB( { (a,),(b,),(c,) } )", ModSolvableGroebnerBase.isLeftGB(N) );
     //System.out.println("N = " + N );

     assertTrue("not isZERO( d )", !d.isZERO() );
     V = new ArrayList<GenSolvablePolynomial<BigRational>>();
     V.add(d); V.add(zero); V.add(zero);
     L.add(V);
     M = new ModuleList<BigRational>(pfac,L);
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

     L = new ArrayList<List<GenSolvablePolynomial<BigRational>>>();

     V = new ArrayList<GenSolvablePolynomial<BigRational>>();
     V.add(a); V.add(zero); V.add(one);
     L.add(V);
     M = new ModuleList<BigRational>(pfac,L);
     assertTrue("isTwosidedGB( { (a,0,1) } )", ModSolvableGroebnerBase.isTwosidedGB(M) );

     N = ModSolvableGroebnerBase.twosidedGB( M );
     assertTrue("isTwosidedGB( { (a,0,1) } )", ModSolvableGroebnerBase.isTwosidedGB(N) );

     assertTrue("not isZERO( b )", !b.isZERO() );
     V = new ArrayList<GenSolvablePolynomial<BigRational>>();
     V.add(b); V.add(one); V.add(zero);
     L.add(V);
     M = new ModuleList<BigRational>(pfac,L);
     //System.out.println("L = " + L.size() );

     N = ModSolvableGroebnerBase.twosidedGB( M );
     assertTrue("isTwosidedGB( { (a,0,1),(b,1,0) } )", ModSolvableGroebnerBase.isTwosidedGB(N) );
     //System.out.println("N = " + N );

     assertTrue("not isZERO( c )", !c.isZERO() );
     V = new ArrayList<GenSolvablePolynomial<BigRational>>();
     V.add(c); V.add(zero); V.add(zero);
     L.add(V);
     M = new ModuleList<BigRational>(pfac,L);
     //System.out.println("L = " + L.size() );

     N = ModSolvableGroebnerBase.twosidedGB( M );
     assertTrue("isTwosidedGB( { (a,),(b,),(c,) } )", ModSolvableGroebnerBase.isTwosidedGB(N) );
     //System.out.println("N = " + N );

     assertTrue("not isZERO( d )", !d.isZERO() );
     V = new ArrayList<GenSolvablePolynomial<BigRational>>();
     V.add(d); V.add(zero); V.add(zero);
     L.add(V);
     M = new ModuleList<BigRational>(pfac,L);
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

     int rloc = 4;
     pfac = new GenSolvablePolynomialRing<BigRational>(cfac,rloc);

     WeylRelations<BigRational> wl = new WeylRelations<BigRational>(pfac);
     wl.generate();
     table = pfac.table;
     //System.out.println("table = " + table);
     //System.out.println("pfac = " + pfac);

     a = pfac.random(kl, ll, el, q );
     b = pfac.random(kl, ll, el, q );
     c = pfac.random(kl, ll, el, q );
     d = pfac.random(kl, ll, el, q );
     e = d; // = pfac.random(kl, ll, el, q );
     one =  pfac.getONE();
     zero = pfac.getZERO();

     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList<List<GenSolvablePolynomial<BigRational>>>();

     V = new ArrayList<GenSolvablePolynomial<BigRational>>();
     V.add(a); V.add(zero); V.add(one);
     L.add(V);
     M = new ModuleList<BigRational>(pfac,L);
     assertTrue("isLeftGB( { (a,0,1) } )", ModSolvableGroebnerBase.isLeftGB(M) );

     N = ModSolvableGroebnerBase.leftGB( M );
     assertTrue("isLeftGB( { (a,0,1) } )", ModSolvableGroebnerBase.isLeftGB(N) );

     assertTrue("not isZERO( b )", !b.isZERO() );
     V = new ArrayList<GenSolvablePolynomial<BigRational>>();
     V.add(b); V.add(one); V.add(zero);
     L.add(V);
     M = new ModuleList<BigRational>(pfac,L);
     //System.out.println("L = " + L.size() );

     N = ModSolvableGroebnerBase.leftGB( M );
     assertTrue("isLeftGB( { (a,0,1),(b,1,0) } )", ModSolvableGroebnerBase.isLeftGB(N) );
     //System.out.println("N = " + N );

     assertTrue("not isZERO( c )", !c.isZERO() );
     V = new ArrayList<GenSolvablePolynomial<BigRational>>();
     V.add(c); V.add(zero); V.add(zero);
     L.add(V);
     M = new ModuleList<BigRational>(pfac,L);
     //System.out.println("M = " + M );
     //System.out.println("L = " + L.size() );

     N = ModSolvableGroebnerBase.leftGB( M );
     assertTrue("isLeftGB( { (a,),(b,),(c,) } )", ModSolvableGroebnerBase.isLeftGB(N) );
     //System.out.println("N = " + N );

     assertTrue("not isZERO( d )", !d.isZERO() );
     V = new ArrayList<GenSolvablePolynomial<BigRational>>();
     V.add(d); V.add(zero); V.add(zero);
     L.add(V);
     M = new ModuleList<BigRational>(pfac,L);
     //System.out.println("M = " + M );
     //System.out.println("L = " + L.size() );

     N = ModSolvableGroebnerBase.leftGB( M );
     assertTrue("isLeftGB( { (a,b,c,d) } )", ModSolvableGroebnerBase.isLeftGB(N) );
     //System.out.println("N = " + N );

 }

}
