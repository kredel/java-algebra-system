/*
 * $Id$
 */

//package edu.unima.ky.parallel;
package edu.jas;

import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.Coefficient;
import edu.jas.arith.BigRational;
import edu.jas.arith.BigComplex;
import edu.jas.arith.BigQuaternion;
import edu.jas.arith.BigInteger;
import edu.jas.poly.OrderedPolynomial;
import edu.jas.poly.IntOrderedMapPolynomial;
import edu.jas.poly.RatOrderedMapPolynomial;
import edu.jas.poly.ComplexOrderedMapPolynomial;
import edu.jas.poly.QuatOrderedMapPolynomial;

/**
 * DistributedList Test using JUnit 
 * @author Heinz Kredel
 */

public class DistributedListTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>DistributedListTest</CODE> object.
 * @param name String
 */
   public DistributedListTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(DistributedListTest.class);
     return suite;
   }

   private static final String host = "localhost";

   private DistributedList l1;
   private DistributedList l2;

   private DistributedListServer dls;
   private Thread dlst;

   int rl = 7; 
   int kl = 10;
   int ll = 10;
   int el = 5;
   float q = 0.5f;

   protected void setUp() {
       dls = new DistributedListServer();
       dlst = new Thread( dls );
       dlst.start();
   }

   protected void tearDown() {
       try {
           dlst.interrupt();
           dlst.join(100);
       } catch (InterruptedException e) {
       }
       dls.terminate();
       dls = null;
       if ( l1 != null ) l1.terminate();
       if ( l2 != null ) l2.terminate();
       l1 = l2 = null;
       int k = 0;
       while ( dlst.isAlive() && k < 10 ) { 
	     k++;
             try {
		  // System.out.print("-");
                  dlst.interrupt();
                  dlst.join(100);
             } catch (InterruptedException e) {
             }
       }
       //System.out.println("tearedDown");
   }


/**
 * Tests if the created DistributedList has #n objects as content.
 */
 public void testDistributedList1() {
     l1 = new DistributedList(host);
     assertTrue("l1==empty",l1.isEmpty());
     l1.add( new Integer(1) );
     assertFalse("l1!=empty",l1.isEmpty());
     assertTrue("#l1==1", l1.size() == 1 );
     l1.add( new Integer(2) );
     assertTrue("#l1==2", l1.size() == 2 );
     l1.add( new Integer(3) );
     assertTrue("#l1==3", l1.size() == 3 );

     Iterator it = null;
     it = l1.iterator();
     int i = 0;
     while ( it.hasNext() ) {
	   Object o = it.next();
	   Integer x = new Integer( ++i );
           assertEquals("l1(i)==(i)", x, o );
     }

     l1.clear();
     assertTrue("#l1==0", l1.size() == 0 );
   }


/**
 * Tests if the two created DistributedLists have #n objects as content.
 */
 public void testDistributedList2() {
     l1 = new DistributedList(host);
     assertTrue("l1==empty",l1.isEmpty());
     l2 = new DistributedList(host);
     assertTrue("l2==empty",l2.isEmpty());

     int i = 0, loops = 10;
     while ( i < loops ) {
	   Integer x = new Integer( ++i );
           l1.add( x );
           assertTrue("#l1==i", l1.size() == i );
     }
     assertTrue("#l1==10", l1.size() == loops );

     while ( l2.size() < loops/2 ) {
         try {
	     //System.out.print(".");
             Thread.currentThread().sleep(100);
         } catch (InterruptedException e) {
         }
     }
     Iterator it = null;
     it = l2.iterator();
     i = 0;
     while ( it.hasNext() ) {
	   Object o = it.next();
	   Integer x = new Integer( ++i );
	   //System.out.println("o = " + o + " x = "+ x);
           assertEquals("l2(i)==(i)", x, o );
     }
     assertTrue("#l2==10", l2.size() == loops );
   }

/**
 * Tests DistributedLists with BigInteger, BigRational etc.
 */
 public void testDistributedList3() {
     l1 = new DistributedList(host);
     assertTrue("l1==empty",l1.isEmpty());
     l2 = new DistributedList(host);
     assertTrue("l2==empty",l2.isEmpty());

     Coefficient a = BigInteger.IRAND(kl);
     l1.add( a );
     assertTrue("#l1==i", l1.size() == 1 );

     Coefficient b = BigRational.RNRAND(kl);
     l1.add( b );
     assertTrue("#l1==i", l1.size() == 2 );

     Coefficient c = BigComplex.CRAND(kl);
     l1.add( c );
     assertTrue("#l1==i", l1.size() == 3 );

     Coefficient d = BigQuaternion.QRAND(kl);
     l1.add( d );
     assertTrue("#l1==i", l1.size() == 4 );

     while ( l2.size() < l1.size() ) {
         try {
	     //System.out.print(".");
             Thread.currentThread().sleep(100);
         } catch (InterruptedException e) {
         }
     }
     Iterator ita = null;
     ita = l1.iterator();
     Iterator itb = null;
     itb = l2.iterator();
     while ( ita.hasNext() && itb.hasNext() ) {
	   Object o = ita.next();
	   Object p = itb.next();
	   //System.out.println("o = " + o + " x = "+ x);
           assertEquals("a==b", o, p );
     }
     assertTrue("#l1==#l210", l1.size() == l2.size() );
 }


/**
 * Tests DistributedLists with Polynomials.
 */
 public void testDistributedList4() {
     l1 = new DistributedList(host);
     assertTrue("l1==empty",l1.isEmpty());
     l2 = new DistributedList(host);
     assertTrue("l2==empty",l2.isEmpty());

     OrderedPolynomial a = IntOrderedMapPolynomial.DIIRAS(rl, kl, ll, el, q);
     l1.add( a );
     assertTrue("#l1==i", l1.size() == 1 );

     OrderedPolynomial b = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q);
     l1.add( b );
     assertTrue("#l1==i", l1.size() == 2 );

     OrderedPolynomial c = ComplexOrderedMapPolynomial.DICRAS(rl, kl, ll, el, q);
     l1.add( c );
     assertTrue("#l1==i", l1.size() == 3 );

     OrderedPolynomial d = QuatOrderedMapPolynomial.DIQRAS(rl, kl, ll, el, q);
     l1.add( d );
     assertTrue("#l1==i", l1.size() == 4 );

     while ( l2.size() < l1.size() ) {
         try {
	     //System.out.print(".");
             Thread.currentThread().sleep(100);
         } catch (InterruptedException e) {
         }
     }
     Iterator ita = null;
     ita = l1.iterator();
     Iterator itb = null;
     itb = l2.iterator();
     while ( ita.hasNext() && itb.hasNext() ) {
	   Object o = ita.next();
	   Object p = itb.next();
	   //System.out.println("o = " + o + " x = "+ x);
           assertEquals("a==b", o, p );
     }
     assertTrue("#l1==#l210", l1.size() == l2.size() );
 }

}
