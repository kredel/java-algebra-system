/*
 * $Id$
 */

package edu.jas.poly;

//import edu.jas.poly.ExpVector;

import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

/**
 * ExpVector Test using JUnit.
 * @author Heinz Kredel.
 */

public class ExpVectorTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>ExpVectorTest</CODE> object.
 * @param name String.
 */
   public ExpVectorTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(ExpVectorTest.class);
     return suite;
   }

   //private final static int bitlen = 100;

   ExpVector a;
   ExpVector b;
   ExpVector c;
   ExpVector d;

   protected void setUp() {
       a = b = c = d = null;
   }

   protected void tearDown() {
       a = b = c = d = null;
   }


/**
 * Test constructor and toString.
 * 
 */
 public void testConstructor() {
     a = new ExpVector( 0 );
     b = new ExpVector( 0 );
     assertEquals("() = ()",a,b);
     assertEquals("length( () ) = 0",a.length(),0);
     assertTrue("isZERO( () )",a.isZERO());

     a = new ExpVector( 10 );
     b = new ExpVector( 10 );
     assertEquals("10e = 10e",a,b);
     assertEquals("length( 10e ) = 10",a.length(),10);
     assertTrue("isZERO( ( 10e ) )",a.isZERO());

     String s = "(0,0,0,0,0,0,0,0,0,0)";
     a = new ExpVector( s );
     String t = a.toString();

     assertEquals("stringConstr = toString",s,t);
     assertTrue("isZERO( ( 10e ) )",a.isZERO());
   }


/**
 * Test random integer.
 * 
 */
 public void testRandom() {
     float q = (float) 0.2;

     a = ExpVector.EVRAND(5,10,q);
     b = new ExpVector( "" + a );

     assertEquals("a == b", true, a.equals(b) );

     c = ExpVector.EVDIF(b,a);

     assertTrue("a-b = 0",c.isZERO());
 }


/**
 * Test addition.
 * 
 */
 public void testAddition() {
     float q = (float) 0.2;

     a = ExpVector.EVRAND(5,10,q);

     b = ExpVector.EVSUM(a,a);
     c = ExpVector.EVDIF(b,a);

     assertEquals("a+a-a = a",c,a);
     assertTrue("a+a-a = a", c.equals(a) );

     boolean t;
     t = ExpVector.EVMT(b,a);
     assertTrue("a | a+a", t );

     a = ExpVector.EVRAND(5,10,q);
     b = ExpVector.EVRAND(5,10,q);

     c = ExpVector.EVSUM(a,b);
     d = ExpVector.EVSUM(b,a);
     assertTrue("a+b = b+a", c.equals(d) );
 }


/**
 * Test lcm.
 * 
 */
 public void testLcm() {
     float q = (float) 0.2;

     a = ExpVector.EVRAND(5,10,q);
     b = ExpVector.EVRAND(5,10,q);
     c = ExpVector.EVLCM(a,b);
     d = ExpVector.EVLCM(b,a);

     assertTrue("lcm(a,b) = lcm(b,a)", c.equals(d) );

     assertTrue("a | lcm(a,b)", ExpVector.EVMT(c,a) );
     assertTrue("b | lcm(a,b)", ExpVector.EVMT(c,b) );

     d = ExpVector.EVDIF(c,a);
     assertTrue("sign(lcm(a,b)-a) >= 0", ExpVector.EVSIGN(d) >= 0 );
     d = ExpVector.EVDIF(c,b);
     assertTrue("sign(lcm(a,b)-b) >= 0", ExpVector.EVSIGN(d) >= 0 );
 }

/**
 * Test tdeg.
 * 
 */
 public void testTdeg() {
     a = new ExpVector( 100 );
     assertTrue("tdeg(a) = 0", ExpVector.EVTDEG(a) == 0 );

     float q = (float) 0.2;

     a = ExpVector.EVRAND(5,10,q);
     b = ExpVector.EVRAND(5,10,q);

     assertTrue("tdeg(a) >= 0", ExpVector.EVTDEG(a) >= 0 );
     assertTrue("tdeg(b) >= 0", ExpVector.EVTDEG(b) >= 0 );

     c = ExpVector.EVSUM(a,b);
     assertTrue("tdeg(a+b) >= tdeg(a)", 
                   ExpVector.EVTDEG(c) >=
                   ExpVector.EVTDEG(a) );
     assertTrue("tdeg(a+b) >= tdeg(b)", 
                   ExpVector.EVTDEG(c) >=
                   ExpVector.EVTDEG(b) );

     c = ExpVector.EVLCM(a,b);
     assertTrue("tdeg(lcm(a,b)) >= tdeg(a)", 
                   ExpVector.EVTDEG(c) >=
                   ExpVector.EVTDEG(a) );
     assertTrue("tdeg(lcm(a,b)) >= tdeg(b)", 
                   ExpVector.EVTDEG(c) >=
                   ExpVector.EVTDEG(b) );
 }

/**
 * Test weighted.
 * 
 */
 public void testWeightdeg() {
     a = new ExpVector( 100 );
     assertTrue("tdeg(a) = 0", ExpVector.EVTDEG(a) == 0 );
     assertTrue("wdeg(a) = 0", ExpVector.EVWDEG(null,a) == 0 );

     float q = (float) 0.2;

     a = ExpVector.EVRAND(5,10,q);
     b = ExpVector.EVRAND(5,10,q);
     long [][] w = new long [][] { new long[] { 1l, 1l, 1l, 1l, 1l } };

     assertTrue("tdeg(a) >= 0", ExpVector.EVTDEG(a) >= 0 );
     assertTrue("tdeg(b) >= 0", ExpVector.EVTDEG(b) >= 0 );

     assertTrue("wdeg(a) >= 0", ExpVector.EVWDEG(w,a) >= 0 );
     assertTrue("wdeg(b) >= 0", ExpVector.EVWDEG(w,b) >= 0 );

     assertEquals("tdeg(a) == wdeg(a)", ExpVector.EVTDEG(a), ExpVector.EVWDEG(w,a) );
     assertEquals("tdeg(b) == wdeg(b)", ExpVector.EVTDEG(b), ExpVector.EVWDEG(w,b) );

     c = ExpVector.EVSUM(a,b);
     assertTrue("wdeg(a+b) >= wdeg(a)", 
                   ExpVector.EVWDEG(w,c) >=
                   ExpVector.EVWDEG(w,a) );
     assertTrue("wdeg(a+b) >= wdeg(b)", 
                   ExpVector.EVWDEG(w,c) >=
                   ExpVector.EVWDEG(w,b) );

     c = ExpVector.EVLCM(a,b);
     assertTrue("wdeg(lcm(a,b)) >= wdeg(a)", 
                   ExpVector.EVWDEG(w,c) >=
                   ExpVector.EVWDEG(w,a) );
     assertTrue("wdeg(lcm(a,b)) >= wdeg(b)", 
                   ExpVector.EVWDEG(w,c) >=
                   ExpVector.EVWDEG(w,b) );


     w = new long [][] { new long[] { 10l, 1l, 3l, 9l, 100l } };

     assertTrue("tdeg(a) >= 0", ExpVector.EVTDEG(a) >= 0 );
     assertTrue("tdeg(b) >= 0", ExpVector.EVTDEG(b) >= 0 );

     assertTrue("wdeg(a) >= 0", ExpVector.EVWDEG(w,a) >= 0 );
     assertTrue("wdeg(b) >= 0", ExpVector.EVWDEG(w,b) >= 0 );

     assertTrue("tdeg(a) <= wdeg(a)", ExpVector.EVTDEG(a) <= ExpVector.EVWDEG(w,a) );
     assertTrue("tdeg(b) <= wdeg(b)", ExpVector.EVTDEG(b) <= ExpVector.EVWDEG(w,b) );

     c = ExpVector.EVSUM(a,b);
     assertTrue("wdeg(a+b) >= wdeg(a)", 
                   ExpVector.EVWDEG(w,c) >=
                   ExpVector.EVWDEG(w,a) );
     assertTrue("wdeg(a+b) >= wdeg(b)", 
                   ExpVector.EVWDEG(w,c) >=
                   ExpVector.EVWDEG(w,b) );

     c = ExpVector.EVLCM(a,b);
     assertTrue("wdeg(lcm(a,b)) >= wdeg(a)", 
                   ExpVector.EVWDEG(w,c) >=
                   ExpVector.EVWDEG(w,a) );
     assertTrue("wdeg(lcm(a,b)) >= wdeg(b)", 
                   ExpVector.EVWDEG(w,c) >=
                   ExpVector.EVWDEG(w,b) );


     w = new long [][] { new long[] { 10l, 1l, 3l, 9l, 100l },
                         new long[] { 1l,  1l, 1l, 1l,   1l }  };

     assertTrue("tdeg(a) >= 0", ExpVector.EVTDEG(a) >= 0 );
     assertTrue("tdeg(b) >= 0", ExpVector.EVTDEG(b) >= 0 );

     assertTrue("wdeg(a) >= 0", ExpVector.EVWDEG(w,a) >= 0 );
     assertTrue("wdeg(b) >= 0", ExpVector.EVWDEG(w,b) >= 0 );

     assertTrue("tdeg(a) <= wdeg(a)", ExpVector.EVTDEG(a) <= ExpVector.EVWDEG(w,a) );
     assertTrue("tdeg(b) <= wdeg(b)", ExpVector.EVTDEG(b) <= ExpVector.EVWDEG(w,b) );

     c = ExpVector.EVSUM(a,b);
     assertTrue("wdeg(a+b) >= wdeg(a)", 
                   ExpVector.EVWDEG(w,c) >=
                   ExpVector.EVWDEG(w,a) );
     assertTrue("wdeg(a+b) >= wdeg(b)", 
                   ExpVector.EVWDEG(w,c) >=
                   ExpVector.EVWDEG(w,b) );

     c = ExpVector.EVLCM(a,b);
     assertTrue("wdeg(lcm(a,b)) >= wdeg(a)", 
                   ExpVector.EVWDEG(w,c) >=
                   ExpVector.EVWDEG(w,a) );
     assertTrue("wdeg(lcm(a,b)) >= wdeg(b)", 
                   ExpVector.EVWDEG(w,c) >=
                   ExpVector.EVWDEG(w,b) );

 }


/**
 * Test dependency on variables.
 * 
 */
 public void testDependency() {
     int[] exp;
     int[] dep;

     a = new ExpVector(10,5,2l);
     exp = new int[] { 5 };
     dep = ExpVector.EVDOV(a);
     assertTrue("[5] = [5]",Arrays.equals(exp,dep));

     b = new ExpVector(10,3,9l);
     exp = new int[] { 3 };
     dep = ExpVector.EVDOV(b);
     assertTrue("[3] = [3]",Arrays.equals(exp,dep));

     c = ExpVector.EVSUM(a,b);
     exp = new int[] { 3, 5 };
     dep = ExpVector.EVDOV(c);
     assertTrue("[3,5] = [3,5]",Arrays.equals(exp,dep));

     b = new ExpVector(10);
     exp = new int[] { };
     dep = ExpVector.EVDOV(b);
     assertTrue("[] = []",Arrays.equals(exp,dep));

     b = new ExpVector(0);
     exp = new int[] { };
     dep = ExpVector.EVDOV(b);
     assertTrue("[] = []",Arrays.equals(exp,dep));

     b = new ExpVector(1,0,1l);
     exp = new int[] { 0 };
     dep = ExpVector.EVDOV(b);
     assertTrue("[0] = [0]",Arrays.equals(exp,dep));
 }

}
