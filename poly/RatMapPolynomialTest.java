/*
 * $Id$
 */

package edu.jas.poly;

//import edu.jas.poly.RatPolynomial;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * RatMapPolynomial Test using JUnit 
 * @author Heinz Kredel.
 */

public class RatMapPolynomialTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>RatMapPolynomialTest</CODE> object.
 * @param name String
 */
   public RatMapPolynomialTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(RatMapPolynomialTest.class);
     return suite;
   }

   private final static int bitlen = 100;

   UnorderedPolynomial a;
   UnorderedPolynomial b;
   UnorderedPolynomial c;
   UnorderedPolynomial d;
   UnorderedPolynomial e;

   int rl = 2; //7; 
   int kl = 5; //10;
   int ll = 3; //10;
   int el = 4; //5;
   float q = 0.5f;

   protected void setUp() {
       a = b = c = d = e = null;
   }

   protected void tearDown() {
       a = b = c = d = e = null;
   }


/**
 * Test constructor and toString
 * 
 */
 public void testConstructor() {
     a = new RatMapPolynomial();
     assertTrue("length( a ) = 0", a.length() == 0);
     //assertTrue("isZERO( a )", a.isZERO() );
     assertTrue("isONE( a )", !a.isONE() );

     b = new RatMapPolynomial(rl);
     assertTrue("length( b ) = 0", b.length() == 0);
     //assertTrue("isZERO( b )", b.isZERO() );
     assertTrue("isONE( b )", !b.isONE() );

     c = RatMapPolynomial.ONE;
     assertTrue("length( c ) = 1", c.length() == 1);
     assertTrue("isZERO( c )", !c.isZERO() );
     assertTrue("isONE( c )", c.isONE() );

     d = RatMapPolynomial.ZERO;
     assertTrue("length( d ) = 0", d.length() == 0);
     assertTrue("isZERO( d )", d.isZERO() );
     assertTrue("isONE( d )", !d.isONE() );
 }


/**
 * Test random polynomial
 * 
 */
 public void testRandom() {
     for (int i = 0; i < 7; i++) {
         a = RatMapPolynomial.DIRRAS(rl+i, kl*(i+1), ll+2*i, el+i, q );
         assertTrue("length( a"+i+" ) <> 0", a.length() >= 0);
         assertTrue(" not isZERO( a"+i+" )", !a.isZERO() );
         assertTrue(" not isONE( a"+i+" )", !a.isONE() );
     }
 }


/**
 * Test addition
 * 
 */
 public void testAddition() {

     a = RatMapPolynomial.DIRRAS(rl, kl, ll, el, q );

     c = RatMapPolynomial.DIPDIF(a,a);
     assertTrue("a-a = 0", c.isZERO() );

     b = RatMapPolynomial.DIPSUM(a,a);
     c = RatMapPolynomial.DIPDIF(b,a);

     assertEquals("a+a-a = a",c,a);
     assertTrue("a+a-a = a", c.equals(a) );

     b = RatMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     c = RatMapPolynomial.DIPSUM(b,a);
     d = RatMapPolynomial.DIPSUM(a,b);

     assertEquals("a+b = b+a",c,d);
     assertTrue("a+b = b+a", c.equals(d) );

     c = RatMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     d = RatMapPolynomial.DIPSUM(a,RatMapPolynomial.DIPSUM(b,c));
     e = RatMapPolynomial.DIPSUM(RatMapPolynomial.DIPSUM(a,b),c);

     assertEquals("a+(b+c) = (a+b)+c",d,e);
     assertTrue("a+(b+c) = (a+b)+c", d.equals(e) );

 }


/**
 * Test multiplication
 * 
 */
 public void testMultiplication() {

     a = RatMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );
     //a = RatMapPolynomial.DIRRAS(1, kl, 4, el, q );

     b = RatMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );

     c = RatMapPolynomial.DIPPR(b,a);
     d = RatMapPolynomial.DIPPR(a,b);
     assertTrue("not isZERO( c )", !c.isZERO() );
     assertTrue("not isZERO( d )", !d.isZERO() );

     e = RatMapPolynomial.DIPDIF(d,c);
     assertTrue("isZERO( a*b-b*a ) " + e, e.isZERO() );

     assertEquals("a*b = b*a",c,d);
     assertTrue("a*b = b*a", c.equals(d) );

     c = RatMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     d = RatMapPolynomial.DIPPR(a,RatMapPolynomial.DIPPR(b,c));
     e = RatMapPolynomial.DIPPR(RatMapPolynomial.DIPPR(a,b),c);

     assertEquals("a(bc) = (ab)c",d,e);
     assertTrue("a(bc) = (ab)c", d.equals(e) );

 }

}
