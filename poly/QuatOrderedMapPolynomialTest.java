/*
 * $Id$
 */

package edu.jas.poly;

//import edu.jas.poly.QuatPolynomial;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * QuatOrderedMapPolynomial Test using JUnit 
 * @author Heinz Kredel.
 */

public class QuatOrderedMapPolynomialTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>QuatOrderedMapPolynomialTest</CODE> object.
 * @param name String
 */
   public QuatOrderedMapPolynomialTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(QuatOrderedMapPolynomialTest.class);
     return suite;
   }

   private final static int bitlen = 100;

   OrderedPolynomial a;
   OrderedPolynomial b;
   OrderedPolynomial c;
   OrderedPolynomial d;
   OrderedPolynomial e;

   int rl = 6; 
   int kl = 10;
   int ll = 7;
   int el = 4;
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
     a = new QuatOrderedMapPolynomial();
     assertTrue("length( a ) = 0", a.length() == 0);
     //assertTrue("isZERO( a )", a.isZERO() );
     assertTrue("isONE( a )", !a.isONE() );

     b = new QuatOrderedMapPolynomial(rl);
     assertTrue("length( b ) = 0", b.length() == 0);
     //assertTrue("isZERO( b )", b.isZERO() );
     assertTrue("isONE( b )", !b.isONE() );

     c = QuatOrderedMapPolynomial.ONE;
     assertTrue("length( c ) = 1", c.length() == 1);
     assertTrue("isZERO( c )", !c.isZERO() );
     assertTrue("isONE( c )", c.isONE() );

     d = QuatOrderedMapPolynomial.ZERO;
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
         a = QuatOrderedMapPolynomial.DIQRAS(rl+i, kl*(i+1), ll+2*i, el+i, q );
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

     a = QuatOrderedMapPolynomial.DIQRAS(rl, kl, ll, el, q );

     c = QuatOrderedMapPolynomial.DIPDIF(a,a);
     assertTrue("a-a = 0", c.isZERO() );

     b = QuatOrderedMapPolynomial.DIPSUM(a,a);
     c = QuatOrderedMapPolynomial.DIPDIF(b,a);

     assertEquals("a+a-a = a",c,a);
     assertTrue("a+a-a = a", c.equals(a) );

     b = QuatOrderedMapPolynomial.DIQRAS(rl, kl, ll, el, q );
     c = QuatOrderedMapPolynomial.DIPSUM(b,a);
     d = QuatOrderedMapPolynomial.DIPSUM(a,b);

     assertEquals("a+b = b+a",c,d);
     assertTrue("a+b = b+a", c.equals(d) );

     c = QuatOrderedMapPolynomial.DIQRAS(rl, kl, ll, el, q );
     d = QuatOrderedMapPolynomial.DIPSUM(a,QuatOrderedMapPolynomial.DIPSUM(b,c));
     e = QuatOrderedMapPolynomial.DIPSUM(QuatOrderedMapPolynomial.DIPSUM(a,b),c);

     assertEquals("a+(b+c) = (a+b)+c",d,e);
     assertTrue("a+(b+c) = (a+b)+c", d.equals(e) );

 }


/**
 * Test multiplication
 * 
 */
 public void testMultiplication() {

     a = QuatOrderedMapPolynomial.DIQRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );
     //a = QuatOrderedMapPolynomial.DIQRAS(1, kl, 4, el, q );

     b = QuatOrderedMapPolynomial.DIQRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );

     c = QuatOrderedMapPolynomial.DIPPR(b,a);
     d = QuatOrderedMapPolynomial.DIPPR(a,b);
     assertTrue("not isZERO( c )", !c.isZERO() );
     assertTrue("not isZERO( d )", !d.isZERO() );

     e = QuatOrderedMapPolynomial.DIPDIF(d,c);
     assertTrue("!isZERO( a*b-b*a ) " + e, !e.isZERO() );

     //assertEquals("a*b != b*a",c,d);
     assertTrue("a*b != b*a", !c.equals(d) );

     c = QuatOrderedMapPolynomial.DIQRAS(rl, kl, ll, el, q );
     d = QuatOrderedMapPolynomial.DIPPR(a,QuatOrderedMapPolynomial.DIPPR(b,c));
     e = QuatOrderedMapPolynomial.DIPPR(QuatOrderedMapPolynomial.DIPPR(a,b),c);

     assertEquals("a(bc) = (ab)c",d,e);
     assertTrue("a(bc) = (ab)c", d.equals(e) );

 }


/**
 * Test object multiplication
 * 
 */
 public void testMultiplication1() {

     a = QuatOrderedMapPolynomial.DIQRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );
     //a = QuatOrderedMapPolynomial.DIQRAS(1, kl, 4, el, q );

     b = QuatOrderedMapPolynomial.DIQRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );

     c = b.multiply(a);
     d = a.multiply(b);
     assertTrue("not isZERO( c )", !c.isZERO() );
     assertTrue("not isZERO( d )", !d.isZERO() );

     e = d.subtract(c);
     assertTrue("!isZERO( a*b-b*a ) " + e, !e.isZERO() );

     //assertEquals("a*b = b*a",c,d);
     assertTrue("a*b != b*a", !c.equals(d) );

     c = QuatOrderedMapPolynomial.DIQRAS(rl, kl, ll, el, q );
     d = a.multiply( b.multiply(c) );
     e = (a.multiply(b)).multiply(c);

     assertEquals("a(bc) = (ab)c",d,e);
     assertTrue("a(bc) = (ab)c", d.equals(e) );

 }

/**
 * Test object multiplication with add
 * 
 */
 public void testMultiplication2() {

     QuatOrderedMapPolynomial a, b;

     a = QuatOrderedMapPolynomial.DIQRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );
     //a = QuatOrderedMapPolynomial.DIQRAS(1, kl, 4, el, q );

     b = QuatOrderedMapPolynomial.DIQRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );

     c = b.multiplyA(a);
     d = a.multiplyA(b);
     assertTrue("not isZERO( c )", !c.isZERO() );
     assertTrue("not isZERO( d )", !d.isZERO() );

     e = d.subtract(c);
     assertTrue("!isZERO( a*b-b*a ) " + e, !e.isZERO() );

     //assertEquals("a*b != b*a",c,d);
     assertTrue("a*b != b*a", !c.equals(d) );

     c = QuatOrderedMapPolynomial.DIQRAS(rl, kl, ll, el, q );
     d = a.multiplyA( b.multiplyA(c) );
     e = ((QuatOrderedMapPolynomial)a.multiplyA(b)).multiplyA(c);

     assertEquals("a(bc) = (ab)c",d,e);
     assertTrue("a(bc) = (ab)c", d.equals(e) );

 }

}
