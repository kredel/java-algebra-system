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
import edu.jas.poly.OrderedPolynomial;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.ExpVector;
import edu.jas.poly.TermOrder;

import edu.jas.ring.GroebnerBase;


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

   OrderedPolynomial a;
   OrderedPolynomial b;
   OrderedPolynomial c;
   OrderedPolynomial d;
   OrderedPolynomial e;
   String[] vars;
   OrderedPolynomial one;
   OrderedPolynomial zero;

   List L;
   List V;
   PolynomialList F;
   PolynomialList G;
   List M;
   List N;
   List Z;

   int rl = 4; //4; //3; 
   int kl = 7;
   int ll = 9;
   int el = 2;
   float q = 0.2f; //0.4f

   protected void setUp() {
       TermOrder to = new TermOrder( TermOrder.INVLEX );
       a = b = c = d = e = null;
       a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
       a = new RatOrderedMapPolynomial(to,a);
       b = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
       b = new RatOrderedMapPolynomial(to,b);
       c = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
       c = new RatOrderedMapPolynomial(to,c);
       d = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
       d = new RatOrderedMapPolynomial(to,d);
       e = d; //RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
       vars = ExpVector.STDVARS( rl );
       one = a.getONE();
       zero = a.getZERO();
   }

   protected void tearDown() {
       a = b = c = d = e = null;
   }


/**
 * Test sequential Syzygy
 * 
 */
 public void testSequentialSyzygy() {

     L = new ArrayList();

     assertTrue("not isZERO( a )", !a.isZERO() );
     L.add(a);
     assertTrue("isGB( { a } )", GroebnerBase.isDIRPGB(L) );
     N = Syzygy.zeroRelations( L );
     assertTrue("is ZR( { a } )", Syzygy.isZeroRelation(N,L) );

     assertTrue("not isZERO( b )", !b.isZERO() );
     L.add(b);
     L = GroebnerBase.DIRPGB(L);
     assertTrue("isGB( { a, b } )", GroebnerBase.isDIRPGB(L) );
     //System.out.println("\nL = " + L );
     N = Syzygy.zeroRelations( L );
     //System.out.println("\nN = " + N );
     assertTrue("is ZR( { a, b } )", Syzygy.isZeroRelation(N,L) );

     assertTrue("not isZERO( c )", !c.isZERO() );
     L.add(c);
     L = GroebnerBase.DIRPGB(L);
     //System.out.println("\nL = " + L );
     assertTrue("isGB( { a, b, c } )", GroebnerBase.isDIRPGB(L) );
     N = Syzygy.zeroRelations( L );
     //System.out.println("\nN = " + N );
     assertTrue("is ZR( { a, b, c } )", Syzygy.isZeroRelation(N,L) );

     assertTrue("not isZERO( d )", !d.isZERO() );
     L.add(d);
     L = GroebnerBase.DIRPGB(L);
     //System.out.println("\nL = " + L );
     assertTrue("isGB( { a, b, c, d } )", GroebnerBase.isDIRPGB(L) );
     N = Syzygy.zeroRelations( L );
     //System.out.println("\nN = " + N );
     assertTrue("is ZR( { a, b, c, d } )", Syzygy.isZeroRelation(N,L) );

     //System.out.println("N = " + N );
     /*
     */
 }


/**
 * Test sequential module Syzygy
 * 
 */
 public void testSequentialModSyzygy() {

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

     Z = Syzygy.zeroRelations(N);
     //System.out.println("Z = " + Z);
     assertTrue("is ZR( { a) } )", Syzygy.isZeroRelation(Z,N) );

     assertTrue("not isZERO( b )", !b.isZERO() );
     V = new ArrayList();
     V.add(b); V.add(one); V.add(zero);
     L.add(V);
     M = new ModuleList(vars,a.getTermOrder(),L);
     //System.out.println("L = " + L.size() );

     N = ModGroebnerBase.GB( M );
     assertTrue("isGB( { a, b } )", ModGroebnerBase.isGB(N) );

     Z = Syzygy.zeroRelations(N);
     //System.out.println("Z = " + Z);
     assertTrue("is ZR( { a, b } )", Syzygy.isZeroRelation(Z,N) );

     assertTrue("not isZERO( c )", !c.isZERO() );
     V = new ArrayList();
     V.add(c); V.add(one); V.add(zero);
     L.add(V);
     M = new ModuleList(vars,a.getTermOrder(),L);
     //System.out.println("L = " + L.size() );

     N = ModGroebnerBase.GB( M );
     //System.out.println("GB(M) = " + N);
     assertTrue("isGB( { a,b,c) } )", ModGroebnerBase.isGB(N) );

     Z = Syzygy.zeroRelations(N);
     //System.out.println("Z = " + Z);
     //boolean b = Syzygy.isZeroRelation(Z,N);
     //System.out.println("boolean = " + b);
     assertTrue("is ZR( { a,b,c } )", Syzygy.isZeroRelation(Z,N) );

 }

}
