/*
 * $Id$
 */

package edu.jas.ring;

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

import edu.jas.poly.ExpVector;
import edu.jas.poly.RatOrderedMapPolynomial;
import edu.jas.poly.OrderedPolynomial;
import edu.jas.poly.PolynomialList;

/**
 * GroebnerBase Test using JUnit 
 * @author Heinz Kredel.
 */

public class GroebnerBaseTest extends TestCase {

    private static final Logger logger = Logger.getLogger(GroebnerBaseTest.class);

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>GroebnerBaseTest</CODE> object.
 * @param name String
 */
   public GroebnerBaseTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(GroebnerBaseTest.class);
     return suite;
   }

   int port = 4711;
   String host = "localhost";

   OrderedPolynomial a;
   OrderedPolynomial b;
   OrderedPolynomial c;
   OrderedPolynomial d;
   OrderedPolynomial e;

   List L;
   PolynomialList F;
   PolynomialList G;

   int rl = 3; 
   int kl = 10;
   int ll = 7;
   int el = 3;
   float q = 0.3f; //0.4f

   protected void setUp() {
       a = b = c = d = e = null;
       a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
       b = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
       c = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
       d = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
       e = d; //RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
   }

   protected void tearDown() {
       a = b = c = d = e = null;
   }


/**
 * Test GBase
 * 
 */
 public void testGBase() {

     // a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();
     L.add(a);

     L = GroebnerBase.DIRPGB( L );
     assertTrue("isDIRPGB( { a } )", GroebnerBase.isDIRPGB(L) );

     // b = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );
     L.add(b);
     //System.out.println("L = " + L.size() );

     L = GroebnerBase.DIRPGB( L );
     assertTrue("isDIRPGB( { a, b } )", GroebnerBase.isDIRPGB(L) );

     // c = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( c )", !c.isZERO() );
     L.add(c);

     L = GroebnerBase.DIRPGB( L );
     assertTrue("isDIRPGB( { a, ,b, c } )", GroebnerBase.isDIRPGB(L) );

     // d = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( d )", !d.isZERO() );
     L.add(d);

     L = GroebnerBase.DIRPGB( L );
     assertTrue("isDIRPGB( { a, ,b, c, d } )", GroebnerBase.isDIRPGB(L) );

     // e = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( e )", !e.isZERO() );
     L.add(e);

     L = GroebnerBase.DIRPGB( L );
     assertTrue("isDIRPGB( { a, ,b, c, d, e } )", GroebnerBase.isDIRPGB(L) );
 }

/**
 * Test parallel GBase
 * 
 */
 public void testParallelGBase() {
     int threads = 2;

     //a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );

     L = new ArrayList();
     L.add(a);

     L = GroebnerBaseParallel.DIRPGB( L, threads );
     assertTrue("isDIRPGB( { a } )", GroebnerBase.isDIRPGB(L) );

     //b = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );
     L.add(b);
     //  System.out.println("L = " + L.size() );

     L = GroebnerBaseParallel.DIRPGB( L, threads );
     assertTrue("isDIRPGB( { a, b } )", GroebnerBase.isDIRPGB(L) );

     // c = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( c )", !c.isZERO() );
     L.add(c);

     L = GroebnerBaseParallel.DIRPGB( L, threads );
     assertTrue("isDIRPGB( { a, ,b, c } )", GroebnerBase.isDIRPGB(L) );

     // d = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( d )", !d.isZERO() );
     L.add(d);

     L = GroebnerBaseParallel.DIRPGB( L, threads );
     assertTrue("isDIRPGB( { a, ,b, c, d } )", GroebnerBase.isDIRPGB(L) );

     // e = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( e )", !e.isZERO() );
     L.add(e);

     if ( logger.isDebugEnabled() ) {
	 for (Iterator it = L.iterator(); it.hasNext(); ) {
             logger.debug("before Li = " + it.next() );
	 }
     }

     L = GroebnerBaseParallel.DIRPGB( L, threads );
     assertTrue("isDIRPGB( { a, ,b, c, d, e } )", GroebnerBase.isDIRPGB(L) );

     if ( logger.isDebugEnabled() ) {
	 for (Iterator it = L.iterator(); it.hasNext(); ) {
             logger.debug("after Li = " + it.next() );
	 }
     }
 }

/**
 * Test compare sequential with parallel GBase
 * 
 */
 public void xtestSequentialParallelGBase() {
     int threads = 2;

     ArrayList Gs, Gp;
     L = new ArrayList();
     Iterator is, ip;

     L.add(a);
     Gs = GroebnerBase.DIRPGB( L );
     Gp = GroebnerBaseParallel.DIRPGB( L, threads );

     assertTrue("Gs.containsAll(Gp)", Gs.containsAll(Gp) );
     assertTrue("Gp.containsAll(Gs)", Gp.containsAll(Gs) );

     L = Gs;
     L.add(b);
     Gs = GroebnerBase.DIRPGB( L );
     Gp = GroebnerBaseParallel.DIRPGB( L, threads );

     assertTrue("Gs.containsAll(Gp)", Gs.containsAll(Gp) );
     assertTrue("Gp.containsAll(Gs)", Gp.containsAll(Gs) );

     L = Gs;
     L.add(c);
     Gs = GroebnerBase.DIRPGB( L );
     Gp = GroebnerBaseParallel.DIRPGB( L, threads );

     assertTrue("Gs.containsAll(Gp)", Gs.containsAll(Gp) );
     assertTrue("Gp.containsAll(Gs)", Gp.containsAll(Gs) );

     L = Gs;
     L.add(d);
     Gs = GroebnerBase.DIRPGB( L );
     Gp = GroebnerBaseParallel.DIRPGB( L, threads );

     assertTrue("Gs.containsAll(Gp)", Gs.containsAll(Gp) );
     assertTrue("Gp.containsAll(Gs)", Gp.containsAll(Gs) );

     L = Gs;
     L.add(e);
     Gs = GroebnerBase.DIRPGB( L );
     Gp = GroebnerBaseParallel.DIRPGB( L, threads );

     assertTrue("Gs.containsAll(Gp)", Gs.containsAll(Gp) );
     assertTrue("Gp.containsAll(Gs)", Gp.containsAll(Gs) );
 }

/**
 * Test compare sequential with distributed GBase
 * 
 */
 public void testSequentialDistributedGBase() {
     int threads = 2;

     ArrayList Gs, Gp;
     L = new ArrayList();
     Iterator is, ip;
     Gs = Gp = null;

     L.add(a);
     Gs = GroebnerBase.DIRPGB( L );
     Thread[] clients = new Thread[threads];
     for (int t = 0; t < threads; t++) {
	 clients[t] = new Thread( new Clients(host,port) );
	 clients[t].start();
     }
     try {
         Gp = GroebnerBaseDistributed.DIRPGBServer( L, threads, port );
     } catch (IOException e) {
     }
     for (int t = 0; t < threads; t++) {
	 try {
             clients[t].join();
	 } catch (InterruptedException e) {
	 }
     }
     //System.out.println("Gs = "+Gs);
     //System.out.println("Gp = "+Gp);
     assertTrue("Gs.containsAll(Gp)", Gs.containsAll(Gp) );
     assertTrue("Gp.containsAll(Gs)", Gp.containsAll(Gs) );

     L = Gs;
     L.add(b);
     Gs = GroebnerBase.DIRPGB( L );
     for (int t = 0; t < threads; t++) {
	 clients[t] = new Thread( new Clients(host,port) );
	 clients[t].start();
     }
     try {
         Gp = GroebnerBaseDistributed.DIRPGBServer( L, threads, port );
     } catch (IOException e) {
     }
     for (int t = 0; t < threads; t++) {
	 try {
             clients[t].join();
	 } catch (InterruptedException e) {
	 }
     }
     assertTrue("Gs.containsAll(Gp)", Gs.containsAll(Gp) );
     assertTrue("Gp.containsAll(Gs)", Gp.containsAll(Gs) );

     L = Gs;
     L.add(c);
     Gs = GroebnerBase.DIRPGB( L );
     if ( Gs.size() > 1 ) {
     for (int t = 0; t < threads; t++) {
	 clients[t] = new Thread( new Clients(host,port) );
	 clients[t].start();
     }
     try {
         Gp = GroebnerBaseDistributed.DIRPGBServer( L, threads, port );
     } catch (IOException e) {
     }
     for (int t = 0; t < threads; t++) {
	 try {
             clients[t].join();
	 } catch (InterruptedException e) {
	 }
     }
     assertTrue("Gs.containsAll(Gp)", Gs.containsAll(Gp) );
     assertTrue("Gp.containsAll(Gs)", Gp.containsAll(Gs) );
     }

     L = Gs;
     L.add(d);
     Gs = GroebnerBase.DIRPGB( L );
     if ( Gs.size() > 1 ) {
     for (int t = 0; t < threads; t++) {
	 clients[t] = new Thread( new Clients(host,port) );
	 clients[t].start();
     }
     try {
         Gp = GroebnerBaseDistributed.DIRPGBServer( L, threads, port );
     } catch (IOException e) {
     }
     for (int t = 0; t < threads; t++) {
	 try {
             clients[t].join();
	 } catch (InterruptedException e) {
	 }
     }
     assertTrue("Gs.containsAll(Gp)", Gs.containsAll(Gp) );
     assertTrue("Gp.containsAll(Gs)", Gp.containsAll(Gs) );
     }

     L = Gs;
     L.add(e);
     Gs = GroebnerBase.DIRPGB( L );
     if ( Gs.size() > 1 ) {
     for (int t = 0; t < threads; t++) {
	 clients[t] = new Thread( new Clients(host,port) );
	 clients[t].start();
     }
     try {
         Gp = GroebnerBaseDistributed.DIRPGBServer( L, threads, port );
     } catch (IOException e) {
     }
     for (int t = 0; t < threads; t++) {
	 try {
             clients[t].join();
	 } catch (InterruptedException e) {
	 }
     }
     assertTrue("Gs.containsAll(Gp)", Gs.containsAll(Gp) );
     assertTrue("Gp.containsAll(Gs)", Gp.containsAll(Gs) );
     }
 }

}


class Clients implements Runnable {
    private final String host;
    private final int port;

    Clients(String host, int port) {
	this.host = host;
	this.port = port;
    }

    public void run() {
	try {
            GroebnerBaseDistributed.DIRPGBClient(host,port);
	} catch (IOException e) {
	}
	return;
    }

}
