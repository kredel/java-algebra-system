/*
 * $Id$
 */

package edu.jas.poly;

//import edu.jas.poly.ExpVector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;

/**
 * ExpVector Test using JUnit 
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
 * @param name String
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

   private final static int bitlen = 100;

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
 * Test constructor and toString
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
 * Test random integer
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
 * Test addition
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
 * Test lcm
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
 * Test tdeg
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

}
