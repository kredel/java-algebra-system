/*
 * $Id$
 */

package edu.jas.poly;

//import edu.jas.poly.RatPolynomial;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * RatPolynomial Test using JUnit 
 * @author Heinz Kredel.
 */

public class RatPolynomialTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>RatPolynomialTest</CODE> object.
 * @param name String
 */
   public RatPolynomialTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(RatPolynomialTest.class);
     return suite;
   }

   private final static int bitlen = 100;

   RatPolynomial a;
   RatPolynomial b;
   RatPolynomial c;
   RatPolynomial d;
   RatPolynomial e;

   int rl = 7; 
   int kl = 10;
   int ll = 10;
   int el = 5;
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
     a = new RatPolynomial();
     assertTrue("length( a ) = 0", a.length() == 0);
     //assertTrue("isZERO( a )", a.isZERO() );
     assertTrue("isONE( a )", !a.isONE() );

     b = new RatPolynomial(rl);
     assertTrue("length( b ) = 0", b.length() == 0);
     //assertTrue("isZERO( b )", b.isZERO() );
     assertTrue("isONE( b )", !b.isONE() );

     c = RatPolynomial.ONE;
     assertTrue("length( c ) = 1", c.length() == 1);
     assertTrue("isZERO( c )", !c.isZERO() );
     assertTrue("isONE( c )", c.isONE() );

     d = RatPolynomial.ZERO;
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
         a = RatPolynomial.DIRRAS(rl+i, kl*(i+1), ll+2*i, el+i, q );
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

     a = RatPolynomial.DIRRAS(rl, kl, ll, el, q );

     b = RatPolynomial.DIRPSM(a,a);
     c = RatPolynomial.DIRPDF(b,a);

     assertEquals("a+a-a = a",c,a);
     assertTrue("a+a-a = a", c.equals(a) );

     b = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     c = RatPolynomial.DIRPSM(b,a);
     d = RatPolynomial.DIRPSM(a,b);

     assertEquals("a+b = b+a",c,d);
     assertTrue("a+b = b+a", c.equals(d) );

     c = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     d = RatPolynomial.DIRPSM(a,RatPolynomial.DIRPSM(b,c));
     e = RatPolynomial.DIRPSM(RatPolynomial.DIRPSM(a,b),c);

     assertEquals("a+(b+c) = (a+b)+c",d,e);
     assertTrue("a+(b+c) = (a+b)+c", d.equals(e) );
 }


/**
 * Test multiplication
 * 
 */
 public void testMultiplication() {

     a = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );
     //a = RatPolynomial.DIRRAS(1, kl, 4, el, q );

     // does not work
     //c = RatPolynomial.DIPPR(a,RatPolynomial.ONE);
     //assertEquals("a*1 = a",c,a);

     // does not work
     //d = RatPolynomial.DIPPR(a,RatPolynomial.ZERO);
     //assertEquals("a*0 = 0",d,RatPolynomial.ZERO);
     //assertTrue("isZERO( a*0 )", d.isZERO() );


     b = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );
     //b = RatPolynomial.DIRRAS(1, kl, 4, el, q );

     //     System.out.println("\na = "+a);
     //System.out.println("b = "+b);

     c = RatPolynomial.DIRPPR(b,a);
     d = RatPolynomial.DIRPPR(a,b);
     assertTrue("not isZERO( c )", !c.isZERO() );
     assertTrue("not isZERO( d )", !d.isZERO() );

     //System.out.println("ba = "+c);
     //System.out.println("ab = "+d);

     e = RatPolynomial.DIRPDF(d,c);
     //System.out.println("a*b-b*a = "+e);
     assertTrue("isZERO( a*b-b*a )", e.isZERO() );

     assertEquals("a*b = b*a",c,d);
     assertTrue("a*b = b*a", c.equals(d) );

     c = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
     d = RatPolynomial.DIRPPR(a,RatPolynomial.DIRPPR(b,c));
     e = RatPolynomial.DIRPPR(RatPolynomial.DIRPPR(a,b),c);

     assertEquals("a+(b+c) = (a+b)+c",d,e);
     assertTrue("a+(b+c) = (a+b)+c", d.equals(e) );
 }

}
