/*
 * $Id$
 */

package edu.jas.util;


import java.util.List;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


import edu.jas.structure.RingElem;
import edu.jas.structure.UnaryFunctor;

import edu.jas.arith.Combinatoric;
import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.BigRational;
import edu.jas.arith.BigComplex;


/**
 * PowerSet tests with JUnit.
 * @author Heinz Kredel.
 */

public class PowerSetTest extends TestCase {

/**
 * main.
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>ListUtilTest</CODE> object.
 * @param name String.
 */
   public PowerSetTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(PowerSetTest.class);
     return suite;
   }

   BigInteger ai;

   protected void setUp() {
       ai = null;
   }

   protected void tearDown() {
       ai = null;
   }


/**
 * Test iterator.
 * 
 */
 public void testIterator() {
     final int N = 10;
     ai = new BigInteger();
     List<BigInteger> list = new ArrayList<BigInteger>();
     for ( int i = 0; i < N; i++ ) {
         list.add( ai.random(7) );
     }
     //System.out.println("list = " + list);

     PowerSet<BigInteger> ps = new PowerSet<BigInteger>( list );
     long i = 0;
     for ( List<BigInteger> subs : ps ) {
         if ( i < 0 ) {
            System.out.println("subs = " + subs);
         }
         if ( subs != null ) {
            assertTrue("size(subs) >= 0 ", subs.size() >= 0);
            i++;
         }
     }
     long j = 1;
     for ( int k = 0; k < N; k++ ) {
         j *= 2;
     }
     assertEquals("size(ps) == 2**N ",i,j);
 }


/**
 * Test k-subset iterator.
 * 
 */
 public void noTestKSubsetIterator() {
     final int N = 10;
     ai = new BigInteger();
     List<BigInteger> list = new ArrayList<BigInteger>();
     for ( int i = 0; i < N; i++ ) {
         list.add( ai.random(7) );
     }
     System.out.println("list = " + list);

     KsubSet<BigInteger> ks = new KsubSet<BigInteger>( list, 0 );
     long i = 0;
     for ( List<BigInteger> subs : ks ) {
         if ( i >= 0 ) {
            System.out.println("subs = " + subs);
         }
         if ( subs != null ) {
            assertTrue("size(subs) >= 0 ", subs.size() == 0);
            i++;
         }
     }
     long s = Combinatoric.binCoeff(N,0).getVal().longValue();
     assertEquals("size(ks) == "+s+" ",i,s);

     ks = new KsubSet<BigInteger>( list, 1 );
     i = 0;
     for ( List<BigInteger> subs : ks ) {
         if ( i >= 0 ) {
            System.out.println("subs = " + subs);
         }
         if ( subs != null ) {
            assertTrue("size(subs) >= 0 ", subs.size() == 1);
            i++;
         }
     }
     s = Combinatoric.binCoeff(N,1).getVal().longValue();
     assertEquals("size(ks) == "+s+" ",i,s);

     ks = new KsubSet<BigInteger>( list, 2 );
     i = 0;
     for ( List<BigInteger> subs : ks ) {
         if ( i >= 0 ) {
            System.out.println("subs = " + subs);
         }
         if ( subs != null ) {
            assertTrue("size(subs) >= 0 ", subs.size() == 2);
            i++;
         }
     }
     s = Combinatoric.binCoeff(N,2).getVal().longValue();
     assertEquals("size(ks) == "+s+" ",i,s);

     ks = new KsubSet<BigInteger>( list, 3 );
     i = 0;
     for ( List<BigInteger> subs : ks ) {
         if ( i >= 0 ) {
            System.out.println("subs = " + subs);
         }
         if ( subs != null ) {
            assertTrue("size(subs) >= 0 ", subs.size() == 3);
            i++;
         }
     }
     s = Combinatoric.binCoeff(N,3).getVal().longValue();
     assertEquals("size(ks) == "+s+" ",i,s);
 }


/**
 * Test any k-subset iterator.
 * 
 */
 public void testAnyKSubsetIterator() {
     final int N = 10;
     ai = new BigInteger();
     List<BigInteger> list = new ArrayList<BigInteger>();
     for ( int i = 0; i < N; i++ ) {
         list.add( ai.random(7) );
     }
     //System.out.println("list = " + list);

     for ( int k = 0; k <= N; k++ ) {
         KsubSet<BigInteger> ks = new KsubSet<BigInteger>( list, k );
         long i = 0;
         for ( List<BigInteger> subs : ks ) {
             if ( i < 0 ) {
                 System.out.println("subs = " + subs);
             }
             if ( subs != null ) {
                 assertTrue("size(subs) >= 0 ", subs.size() == k);
                 i++;
             }
         }
         long s = Combinatoric.binCoeff(N,k).getVal().longValue();
         assertEquals("size(ks) == "+s+" ",i,s);
     }
 }

}
