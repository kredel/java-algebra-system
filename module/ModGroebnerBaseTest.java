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

/**
 * ModGroebnerBase Test using JUnit 
 * @author Heinz Kredel.
 */

public class ModGroebnerBaseTest extends TestCase {

    private static final Logger logger = Logger.getLogger(ModGroebnerBaseTest.class);

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>ModGroebnerBaseTest</CODE> object.
 * @param name String
 */
   public ModGroebnerBaseTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(ModGroebnerBaseTest.class);
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
   ModuleList M;
   ModuleList N;

   int rl = 3; //4; //3; 
   int kl = 8;
   int ll = 5;
   int el = 2;
   float q = 0.2f; //0.4f

   protected void setUp() {
       a = b = c = d = e = null;
       a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
       b = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
       c = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
       d = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
       e = d; //RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
       vars = ExpVector.STDVARS( rl );
       one = a.getONE();
       zero = a.getZERO();
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

     V = new ArrayList();
     V.add(a); V.add(zero); V.add(one);
     L.add(V);
     M = new ModuleList(vars,a.getTermOrder(),L);
     assertTrue("isGB( { (a,0,1) } )", ModGroebnerBase.isGB(M) );

     N = ModGroebnerBase.GB( M );
     assertTrue("isGB( { (a,0,1) } )", ModGroebnerBase.isGB(N) );

     assertTrue("not isZERO( b )", !b.isZERO() );
     V = new ArrayList();
     V.add(b); V.add(one); V.add(zero);
     L.add(V);
     M = new ModuleList(vars,a.getTermOrder(),L);
     //System.out.println("L = " + L.size() );

     N = ModGroebnerBase.GB( M );
     assertTrue("isDIRPGB( { (a,0,1),(b,1,0) } )", ModGroebnerBase.isGB(N) );
     //System.out.println("N = " + N );

     assertTrue("not isZERO( c )", !c.isZERO() );
     V = new ArrayList();
     V.add(c); V.add(zero); V.add(zero);
     L.add(V);
     M = new ModuleList(vars,a.getTermOrder(),L);
     //System.out.println("L = " + L.size() );

     N = ModGroebnerBase.GB( M );
     assertTrue("isDIRPGB( { (a,),(b,),(c,) } )", ModGroebnerBase.isGB(N) );
     //System.out.println("N = " + N );

     assertTrue("not isZERO( d )", !d.isZERO() );
     V = new ArrayList();
     V.add(d); V.add(zero); V.add(zero);
     L.add(V);
     M = new ModuleList(vars,a.getTermOrder(),L);
     //System.out.println("L = " + L.size() );

     N = ModGroebnerBase.GB( M );
     assertTrue("isDIRPGB( { (a,b,c,d) } )", ModGroebnerBase.isGB(N) );
     //System.out.println("N = " + N );

 }

}
