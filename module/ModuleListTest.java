/*
 * $Id$
 */

package edu.jas.module;

//import edu.jas.poly.RatPolynomial;
import edu.jas.arith.BigRational;
import edu.jas.arith.Coefficient;

import edu.jas.poly.PolynomialList;
import edu.jas.poly.OrderedPolynomial;
import edu.jas.poly.TermOrder;
import edu.jas.poly.ExpVector;
import edu.jas.poly.RatOrderedMapPolynomial;

import edu.jas.module.ModuleList;

import java.util.List;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * ModuleList Test using JUnit 
 * @author Heinz Kredel.
 */

public class ModuleListTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>ModuleListTest</CODE> object.
 * @param name String
 */
   public ModuleListTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(ModuleListTest.class);
     return suite;
   }

   private final static int bitlen = 100;

   ModuleList m;
   PolynomialList p;

   OrderedPolynomial a;
   OrderedPolynomial b;
   OrderedPolynomial c;
   OrderedPolynomial d;
   OrderedPolynomial e;

   int rl = 4; 
   int kl = 4;
   int ll = 4;
   int el = 5;
   float q = 0.5f;

   protected void setUp() {
       a = b = c = d = e = null;
       m = null;
       p = null;
   }

   protected void tearDown() {
       a = b = c = d = e = null;
       m = null;
       p = null;
   }


/**
 * Test constructor and toString
 * 
 */
 public void testConstructor() {
     p = new PolynomialList((String[])null,(TermOrder)null,(List)null);
     assertTrue("p = 0", p.list == null);

     m = new ModuleList((String[])null,(TermOrder)null,(List)null);
     assertTrue("m = 0", m.list == null);
 }


/**
 * Test polynomial list
 * 
 */
 public void testPolynomialList() {
     List l = new ArrayList();
     for (int i = 0; i < 7; i++) {
         a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll+i, el, q );
         assertTrue("length( a"+i+" ) <> 0", a.length() >= 0);
         assertTrue(" not isZERO( a"+i+" )", !a.isZERO() );
         assertTrue(" not isONE( a"+i+" )", !a.isONE() );
         l.add( a );
     }
     p = new PolynomialList(ExpVector.STDVARS(rl),a.getTermOrder(),l);
     //System.out.println("p = "+p);

     assertTrue("p == p", p.equals(p) );
     assertEquals("p.length", 7, p.list.size() );
 }


/**
 * Test module list
 * 
 */
 public void testModuleList() {
     List l = new ArrayList();
     for (int i = 0; i < 4; i++) {
         List r = new ArrayList();
         for ( int j = 0; j < 3; j++ ) {
             a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
             assertTrue("length( a"+i+" ) <> 0", a.length() >= 0);
             assertTrue(" not isZERO( a"+i+" )", !a.isZERO() );
             assertTrue(" not isONE( a"+i+" )", !a.isONE() );
             r.add( a );
         }
         l.add( r );
     }
     m = new ModuleList(ExpVector.STDVARS(rl),a.getTermOrder(),l);
     //System.out.println("m = "+m);
     assertTrue("m == m", m.equals(m) );
     assertEquals("m.length", 4, m.list.size() );
 }


/**
 * Test module and polynomial list
 * 
 */
 public void testModulePolynomialList() {
     List l = new ArrayList();
     for (int i = 0; i < 4; i++) {
         List r = new ArrayList();
         for ( int j = 0; j < 3; j++ ) {
             a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
             assertTrue("length( a"+i+" ) <> 0", a.length() >= 0);
             assertTrue(" not isZERO( a"+i+" )", !a.isZERO() );
             assertTrue(" not isONE( a"+i+" )", !a.isONE() );
             r.add( a );
         }
         l.add( r );
     }
     m = new ModuleList(ExpVector.STDVARS(rl),a.getTermOrder(),l);
     //System.out.println("m = "+m);
     assertTrue("m == m", m.equals(m) );
     assertEquals("m.length", 4, m.list.size() );

     p = m.getPolynomialList();
     //System.out.println("p = "+p);
     assertTrue("p == p", p.equals(p) );
     assertEquals("p.length", 4, p.list.size() );
 }

/**
 * Test module and polynomial and module list
 * 
 */
 public void testModulePolynomialModuleList() {
     List l = new ArrayList();
     for (int i = 0; i < 4; i++) {
         List r = new ArrayList();
         for ( int j = 0; j < 3; j++ ) {
             a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
             assertTrue("length( a"+i+" ) <> 0", a.length() >= 0);
             assertTrue(" not isZERO( a"+i+" )", !a.isZERO() );
             assertTrue(" not isONE( a"+i+" )", !a.isONE() );
             r.add( a );
         }
         l.add( r );
     }
     m = new ModuleList(ExpVector.STDVARS(rl),a.getTermOrder(),l);
     //System.out.println("m = "+m);
     assertTrue("m == m", m.equals(m) );
     assertEquals("m.length", 4, m.list.size() );

     p = m.getPolynomialList();
     //System.out.println("p = "+p);
     assertTrue("p == p", p.equals(p) );
     assertEquals("p.length", 4, p.list.size() );

     ModuleList m2 = null;
     m2 = ModuleList.getModuleList( 3, p );
     //System.out.println("m2 = "+m2);
     assertTrue("m2 == m2", m2.equals(m2) );
     assertEquals("m2.length", 4, m2.list.size() );

     assertTrue("m == m2", m.equals(m2) );
 }


/**
 * Test module and polynomial and module and polynomial list
 * 
 */
 public void testModulePolynomialModuleListPolynomial() {
     List l = new ArrayList();
     for (int i = 0; i < 4; i++) {
         List r = new ArrayList();
         for ( int j = 0; j < 3; j++ ) {
             a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
             assertTrue("length( a"+i+" ) <> 0", a.length() >= 0);
             assertTrue(" not isZERO( a"+i+" )", !a.isZERO() );
             assertTrue(" not isONE( a"+i+" )", !a.isONE() );
             r.add( a );
         }
         l.add( r );
     }
     m = new ModuleList(ExpVector.STDVARS(rl),a.getTermOrder(),l);
     //System.out.println("m = "+m);
     assertTrue("m == m", m.equals(m) );
     assertEquals("m.length", 4, m.list.size() );

     p = m.getPolynomialList();
     //System.out.println("p = "+p);
     assertTrue("p == p", p.equals(p) );
     assertEquals("p.length", 4, p.list.size() );

     ModuleList m2 = null;
     m2 = ModuleList.getModuleList( 3, p );
     //System.out.println("m2 = "+m2);
     assertTrue("m2 == m2", m2.equals(m2) );
     assertEquals("m2.length", 4, m2.list.size() );

     assertTrue("m == m2", m.equals(m2) );

     PolynomialList p2 = null;
     p2 = m2.getPolynomialList();
     //System.out.println("p2 = "+p2);
     assertTrue("p2 == p2", p2.equals(p2) );
     assertEquals("p2.length", 4, p2.list.size() );

     assertTrue("p == p2", p.equals(p2) );
 }

}
