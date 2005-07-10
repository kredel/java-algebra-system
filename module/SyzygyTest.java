/*
 * $Id$
 */

package edu.jas.module;

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

import edu.jas.ring.Reduction;
import edu.jas.ring.GroebnerBase;

import edu.jas.module.ModuleList;
import edu.jas.module.Syzygy;


/**
 * Syzygy Test using JUnit 
 * @author Heinz Kredel.
 */

public class SyzygyTest extends TestCase {

    private static final Logger logger = Logger.getLogger(SyzygyTest.class);

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>SyzygyTest</CODE> object.
 * @param name String
 */
   public SyzygyTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(SyzygyTest.class);
     return suite;
   }

   int port = 4711;
   String host = "localhost";

   GenPolynomialRing<BigRational> fac;

   PolynomialList<BigRational> F;
   List<GenPolynomial<BigRational>> G;

   GenPolynomial<BigRational> a;
   GenPolynomial<BigRational> b;
   GenPolynomial<BigRational> c;
   GenPolynomial<BigRational> d;
   GenPolynomial<BigRational> e;
   GenPolynomial<BigRational> zero;
   GenPolynomial<BigRational> one;

   String[] vars;
   TermOrder tord;

   List<GenPolynomial<BigRational>> L;
   List<List<GenPolynomial<BigRational>>> K;
   List<GenPolynomial<BigRational>> V;
   List<List<GenPolynomial<BigRational>>> W;
   ModuleList<BigRational> M;
   ModuleList<BigRational> N;
   ModuleList<BigRational> Z;

   int rl = 4; //4; //3; 
   int kl = 7;
   int ll = 9;
   int el = 2;
   float q = 0.2f; //0.4f

   protected void setUp() {
       BigRational coeff = new BigRational(9);
       vars = ExpVector.STDVARS( rl );
       tord = new TermOrder();
       fac = new GenPolynomialRing<BigRational>(coeff,rl,tord,vars);
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
   }


/**
 * Test sequential Syzygy
 * 
 */
 public void testSequentialSyzygy() {

     L = new ArrayList<GenPolynomial<BigRational>>();

     assertTrue("not isZERO( a )", !a.isZERO() );
     L.add(a);
     assertTrue("isGB( { a } )", GroebnerBase.<BigRational>isGB(L) );
     K = Syzygy.<BigRational>zeroRelations( L );
     assertTrue("is ZR( { a } )", Syzygy.<BigRational>isZeroRelation(K,L) );

     assertTrue("not isZERO( b )", !b.isZERO() );
     L.add(b);
     L = GroebnerBase.<BigRational>GB(L);
     assertTrue("isGB( { a, b } )", GroebnerBase.<BigRational>isGB(L) );
     //System.out.println("\nL = " + L );
     K = Syzygy.<BigRational>zeroRelations( L );
     //System.out.println("\nN = " + N );
     assertTrue("is ZR( { a, b } )", Syzygy.<BigRational>isZeroRelation(K,L) );

     assertTrue("not isZERO( c )", !c.isZERO() );
     L.add(c);
     L = GroebnerBase.<BigRational>GB(L);
     //System.out.println("\nL = " + L );
     assertTrue("isGB( { a, b, c } )", GroebnerBase.<BigRational>isGB(L) );
     K = Syzygy.<BigRational>zeroRelations( L );
     //System.out.println("\nN = " + N );
     assertTrue("is ZR( { a, b, c } )", Syzygy.<BigRational>isZeroRelation(K,L) );

     assertTrue("not isZERO( d )", !d.isZERO() );
     L.add(d);
     L = GroebnerBase.<BigRational>GB(L);
     //System.out.println("\nL = " + L );
     assertTrue("isGB( { a, b, c, d } )", GroebnerBase.<BigRational>isGB(L) );
     K = Syzygy.<BigRational>zeroRelations( L );
     //System.out.println("\nN = " + N );
     assertTrue("is ZR( { a, b, c, d } )", Syzygy.<BigRational>isZeroRelation(K,L) );

     //System.out.println("N = " + N );
     /*
     */
 }


/**
 * Test sequential module Syzygy
 * 
 */
 public void testSequentialModSyzygy() {

     W = new ArrayList<List<GenPolynomial<BigRational>>>();

     assertTrue("not isZERO( a )", !a.isZERO() );
     V = new ArrayList<GenPolynomial<BigRational>>();
     V.add(a); V.add(zero); V.add(one);
     W.add(V);
     M = new ModuleList<BigRational>(fac,W);
     assertTrue("isGB( { (a,0,1) } )", ModGroebnerBase.<BigRational>isGB(M) );

     N = ModGroebnerBase.<BigRational>GB( M );
     assertTrue("isGB( { (a,0,1) } )", ModGroebnerBase.<BigRational>isGB(N) );

     Z = Syzygy.zeroRelations(N);
     //System.out.println("Z = " + Z);
     assertTrue("is ZR( { a) } )", Syzygy.<BigRational>isZeroRelation(Z,N) );

     assertTrue("not isZERO( b )", !b.isZERO() );
     V = new ArrayList<GenPolynomial<BigRational>>();
     V.add(b); V.add(one); V.add(zero);
     W.add(V);
     M = new ModuleList<BigRational>(fac,W);
     //System.out.println("W = " + W.size() );

     N = ModGroebnerBase.<BigRational>GB( M );
     assertTrue("isGB( { a, b } )", ModGroebnerBase.<BigRational>isGB(N) );

     Z = Syzygy.<BigRational>zeroRelations(N);
     //System.out.println("Z = " + Z);
     assertTrue("is ZR( { a, b } )", Syzygy.<BigRational>isZeroRelation(Z,N) );

     assertTrue("not isZERO( c )", !c.isZERO() );
     V = new ArrayList<GenPolynomial<BigRational>>();
     V.add(c); V.add(one); V.add(zero);
     W.add(V);
     M = new ModuleList<BigRational>(fac,W);
     //System.out.println("W = " + W.size() );

     N = ModGroebnerBase.<BigRational>GB( M );
     //System.out.println("GB(M) = " + N);
     assertTrue("isGB( { a,b,c) } )", ModGroebnerBase.<BigRational>isGB(N) );

     Z = Syzygy.<BigRational>zeroRelations(N);
     //System.out.println("Z = " + Z);
     //boolean b = Syzygy.isZeroRelation(Z,N);
     //System.out.println("boolean = " + b);
     assertTrue("is ZR( { a,b,c } )", Syzygy.<BigRational>isZeroRelation(Z,N) );

 }

}
