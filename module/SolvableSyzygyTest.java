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
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.ExpVector;
import edu.jas.poly.TermOrder;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.WeylRelations;
import edu.jas.poly.RelationTable;

import edu.jas.ring.Reduction;
import edu.jas.ring.GroebnerBase;
import edu.jas.ring.SolvableGroebnerBase;

import edu.jas.module.ModuleList;
import edu.jas.module.Syzygy;
import edu.jas.module.SolvableSyzygy;


/**
 * SolvableSyzygy Test using JUnit. 
 * @author Heinz Kredel.
 */

public class SolvableSyzygyTest extends TestCase {

    private static final Logger logger = Logger.getLogger(SolvableSyzygyTest.class);

/**
 * main.
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>SolvableSyzygyTest</CODE> object.
 * @param name String.
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

   BigRational cfac;
   GenSolvablePolynomialRing<BigRational> fac;

   PolynomialList<BigRational> F;
   List<GenSolvablePolynomial<BigRational>> G;

   GenSolvablePolynomial<BigRational> a;
   GenSolvablePolynomial<BigRational> b;
   GenSolvablePolynomial<BigRational> c;
   GenSolvablePolynomial<BigRational> d;
   GenSolvablePolynomial<BigRational> e;
   GenSolvablePolynomial<BigRational> zero;
   GenSolvablePolynomial<BigRational> one;

   String[] vars;
   TermOrder tord;
   RelationTable table;

   List<GenSolvablePolynomial<BigRational>> L;
   List<List<GenSolvablePolynomial<BigRational>>> K;
   List<GenSolvablePolynomial<BigRational>> V;
   List<List<GenSolvablePolynomial<BigRational>>> W;
   ModuleList<BigRational> M;
   ModuleList<BigRational> N;
   ModuleList<BigRational> Z;

   int rl = 4; //4; //3; 
   int kl = 5;
   int ll = 9;
   int el = 2;
   float q = 0.3f; //0.4f

   protected void setUp() {
       cfac = new BigRational(1);
       vars = ExpVector.STDVARS( rl );
       tord = new TermOrder();
       fac = new GenSolvablePolynomialRing<BigRational>(cfac,rl,tord,vars);
       table = fac.table; 
       a = b = c = d = e = null;
       L = null;
       K = null;
       V = null;

       a = fac.random(kl, ll, el, q );
       b = fac.random(kl, ll, el, q );
       c = fac.random(kl, ll, el, q );
       d = fac.random(kl, ll, el, q );
       e = d; //fac.random(kl, ll, el, q );

       one = fac.getONE();
       zero = fac.getZERO();
   }

   protected void tearDown() {
       a = b = c = d = e = null;
       L = null;
       K = null;
       V = null;
       fac = null;
       vars = null;
       tord = null;
       table = null;
   }


/**
 * Test sequential SolvableSyzygy.
 * 
 */
 public void testSequentialSolvableSyzygy() {

     L = new ArrayList<GenSolvablePolynomial<BigRational>>();

     assertTrue("not isZERO( a )", !a.isZERO() );
     L.add(a);
     assertTrue("isGB( { a } )", SolvableGroebnerBase.<BigRational>isLeftGB(L) );
     K = SolvableSyzygy.<BigRational>leftZeroRelations( L );
     assertTrue("is ZR( { a } )", SolvableSyzygy.<BigRational>isLeftZeroRelation(K,L) );

     assertTrue("not isZERO( b )", !b.isZERO() );
     L.add(b);
     L = SolvableGroebnerBase.<BigRational>leftGB(L);
     assertTrue("isGB( { a, b } )", SolvableGroebnerBase.<BigRational>isLeftGB(L) );
     //System.out.println("\nL = " + L );
     K = SolvableSyzygy.<BigRational>leftZeroRelations( L );
     //System.out.println("\nK = " + K );
     assertTrue("is ZR( { a, b } )", SolvableSyzygy.<BigRational>isLeftZeroRelation(K,L) );

     assertTrue("not isZERO( c )", !c.isZERO() );
     L.add(c);
     L = SolvableGroebnerBase.<BigRational>leftGB(L);
     //System.out.println("\nL = " + L );
     assertTrue("isGB( { a, b, c } )", SolvableGroebnerBase.<BigRational>isLeftGB(L) );
     K = SolvableSyzygy.<BigRational>leftZeroRelations( L );
     //System.out.println("\nK = " + K );
     assertTrue("is ZR( { a, b, c } )", SolvableSyzygy.<BigRational>isLeftZeroRelation(K,L) );

     assertTrue("not isZERO( d )", !d.isZERO() );
     L.add(d);
     L = SolvableGroebnerBase.<BigRational>leftGB(L);
     //System.out.println("\nL = " + L );
     assertTrue("isGB( { a, b, c, d } )", SolvableGroebnerBase.<BigRational>isLeftGB(L) );
     K = SolvableSyzygy.<BigRational>leftZeroRelations( L );
     //System.out.println("\nK = " + K );
     assertTrue("is ZR( { a, b, c, d } )", SolvableSyzygy.<BigRational>isLeftZeroRelation(K,L) );

     //System.out.println("K = " + K );
 }


/**
 * Test sequential Weyl SolvableSyzygy.
 * 
 */
 public void testSequentialWeylSolvableSyzygy() {

     int rloc = 4;
     fac = new GenSolvablePolynomialRing<BigRational>(cfac,rloc);

     WeylRelations<BigRational> wl = new WeylRelations<BigRational>(fac);
     wl.generate();
     table = fac.table;

     a = fac.random(kl, ll, el, q );
     b = fac.random(kl, ll, el, q );
     c = fac.random(kl, ll, el, q );
     d = fac.random(kl, ll, el, q );
     e = d; //fac.random(kl, ll, el, q );

     L = new ArrayList<GenSolvablePolynomial<BigRational>>();

     assertTrue("not isZERO( a )", !a.isZERO() );
     L.add(a);
     assertTrue("isGB( { a } )", SolvableGroebnerBase.<BigRational>isLeftGB(L) );
     K = SolvableSyzygy.<BigRational>leftZeroRelations( L );
     assertTrue("is ZR( { a } )", SolvableSyzygy.<BigRational>isLeftZeroRelation(K,L) );

     assertTrue("not isZERO( b )", !b.isZERO() );
     L.add(b);
     L = SolvableGroebnerBase.<BigRational>leftGB(L);
     assertTrue("isGB( { a, b } )", SolvableGroebnerBase.<BigRational>isLeftGB(L) );
     //System.out.println("\nL = " + L );
     K = SolvableSyzygy.<BigRational>leftZeroRelations( L );
     //System.out.println("\nK = " + K );
     assertTrue("is ZR( { a, b } )", SolvableSyzygy.<BigRational>isLeftZeroRelation(K,L) );

     // useless since 1 in GB
     assertTrue("not isZERO( c )", !c.isZERO() );
     L.add(c);
     L = SolvableGroebnerBase.<BigRational>leftGB(L);
     //System.out.println("\nL = " + L );
     assertTrue("isGB( { a, b, c } )", SolvableGroebnerBase.<BigRational>isLeftGB(L) );
     K = SolvableSyzygy.<BigRational>leftZeroRelations( L );
     //System.out.println("\nK = " + K );
     assertTrue("is ZR( { a, b, c } )", SolvableSyzygy.<BigRational>isLeftZeroRelation(K,L) );

     // useless since 1 in GB
     assertTrue("not isZERO( d )", !d.isZERO() );
     L.add(d);
     L = SolvableGroebnerBase.<BigRational>leftGB(L);
     //System.out.println("\nL = " + L );
     assertTrue("isGB( { a, b, c, d } )", SolvableGroebnerBase.<BigRational>isLeftGB(L) );
     K = SolvableSyzygy.<BigRational>leftZeroRelations( L );
     //System.out.println("\nK = " + K );
     assertTrue("is ZR( { a, b, c, d } )", SolvableSyzygy.<BigRational>isLeftZeroRelation(K,L) );

     //System.out.println("K = " + K );
 }


/**
 * Test sequential module SolvableSyzygy.
 * 
 */
 public void testSequentialModSolvableSyzygy() {

     W = new ArrayList<List<GenSolvablePolynomial<BigRational>>>();

     assertTrue("not isZERO( a )", !a.isZERO() );
     V = new ArrayList<GenSolvablePolynomial<BigRational>>();
     V.add(a); V.add(zero); V.add(one);
     W.add(V);
     M = new ModuleList<BigRational>(fac,W);
     assertTrue("isGB( { (a,0,1) } )", ModSolvableGroebnerBase.<BigRational>isLeftGB(M) );

     N = ModSolvableGroebnerBase.<BigRational>leftGB( M );
     assertTrue("isGB( { (a,0,1) } )", ModSolvableGroebnerBase.<BigRational>isLeftGB(N) );

     Z = SolvableSyzygy.<BigRational>leftZeroRelations(N);
     //System.out.println("Z = " + Z);
     assertTrue("is ZR( { a) } )", SolvableSyzygy.<BigRational>isLeftZeroRelation(Z,N) );

     assertTrue("not isZERO( b )", !b.isZERO() );
     V = new ArrayList<GenSolvablePolynomial<BigRational>>();
     V.add(b); V.add(one); V.add(zero);
     W.add(V);
     M = new ModuleList<BigRational>(fac,W);
     //System.out.println("W = " + W.size() );

     N = ModSolvableGroebnerBase.<BigRational>leftGB( M );
     assertTrue("isGB( { a, b } )", ModSolvableGroebnerBase.<BigRational>isLeftGB(N) );

     Z = SolvableSyzygy.<BigRational>leftZeroRelations(N);
     //System.out.println("Z = " + Z);
     assertTrue("is ZR( { a, b } )", SolvableSyzygy.<BigRational>isLeftZeroRelation(Z,N) );

     assertTrue("not isZERO( c )", !c.isZERO() );
     V = new ArrayList<GenSolvablePolynomial<BigRational>>();
     V.add(c); V.add(one); V.add(zero);
     W.add(V);
     M = new ModuleList<BigRational>(fac,W);
     //System.out.println("W = " + W.size() );

     N = ModSolvableGroebnerBase.<BigRational>leftGB( M );
     //System.out.println("GB(M) = " + N);
     assertTrue("isGB( { a,b,c) } )", ModSolvableGroebnerBase.<BigRational>isLeftGB(N) );

     Z = SolvableSyzygy.<BigRational>leftZeroRelations(N);
     //System.out.println("Z = " + Z);
     //boolean b = SolvableSyzygy.<BigRational>isLeftZeroRelation(Z,N);
     //System.out.println("boolean = " + b);
     assertTrue("is ZR( { a,b,c } )", SolvableSyzygy.<BigRational>isLeftZeroRelation(Z,N) );
 }

}
