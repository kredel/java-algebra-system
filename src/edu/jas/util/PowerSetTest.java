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
   BigInteger bi;
   BigInteger ci;
   BigInteger di;
   BigInteger ei;

   protected void setUp() {
       ai = bi = ci = di = ei = null;
   }

   protected void tearDown() {
       ai = bi = ci = di = ei = null;
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

     PowerSet<BigInteger> ps = new PowerSet<BigInteger>( list );
     long i = 0;
     for ( List<BigInteger> subs : ps ) {
         if ( i < 0 ) {
            System.out.println("subs = " + subs);
         }
         assertTrue("size(subs) >= 0 ", subs.size() >= 0);
         i++;
     }
     long j = 1;
     for ( int k = 0; k < N; k++ ) {
         j *= 2;
     }
     assertEquals("size(ps) == 2**N ",i,j);
 }

}
