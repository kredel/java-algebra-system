/*
 * $Id$
 */

package edu.jas.poly;

//import edu.jas.poly.ComplexPolynomial;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * ComplexOrderedMapPolynomial Test using JUnit 
 * @author Heinz Kredel.
 */

public class ComplexOrderedMapPolynomialTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>ComplexOrderedMapPolynomialTest</CODE> object.
 * @param name String
 */
   public ComplexOrderedMapPolynomialTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(ComplexOrderedMapPolynomialTest.class);
     return suite;
   }

   private final static int bitlen = 100;

   OrderedPolynomial a;
   OrderedPolynomial b;
   OrderedPolynomial c;
   OrderedPolynomial d;
   OrderedPolynomial e;

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
     a = new ComplexOrderedMapPolynomial();
     assertTrue("length( a ) = 0", a.length() == 0);
     //assertTrue("isZERO( a )", a.isZERO() );
     assertTrue("isONE( a )", !a.isONE() );

     b = new ComplexOrderedMapPolynomial(rl);
     assertTrue("length( b ) = 0", b.length() == 0);
     //assertTrue("isZERO( b )", b.isZERO() );
     assertTrue("isONE( b )", !b.isONE() );

     c = ComplexOrderedMapPolynomial.ONE;
     assertTrue("length( c ) = 1", c.length() == 1);
     assertTrue("isZERO( c )", !c.isZERO() );
     assertTrue("isONE( c )", c.isONE() );

     d = ComplexOrderedMapPolynomial.ZERO;
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
         a = ComplexOrderedMapPolynomial.DICRAS(rl+i, kl*(i+1), ll+2*i, el+i, q );
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

     a = ComplexOrderedMapPolynomial.DICRAS(rl, kl, ll, el, q );

     c = ComplexOrderedMapPolynomial.DIPDIF(a,a);
     assertTrue("a-a = 0", c.isZERO() );

     b = ComplexOrderedMapPolynomial.DIPSUM(a,a);
     c = ComplexOrderedMapPolynomial.DIPDIF(b,a);

     assertEquals("a+a-a = a",c,a);
     assertTrue("a+a-a = a", c.equals(a) );

     b = ComplexOrderedMapPolynomial.DICRAS(rl, kl, ll, el, q );
     c = ComplexOrderedMapPolynomial.DIPSUM(b,a);
     d = ComplexOrderedMapPolynomial.DIPSUM(a,b);

     assertEquals("a+b = b+a",c,d);
     assertTrue("a+b = b+a", c.equals(d) );

     c = ComplexOrderedMapPolynomial.DICRAS(rl, kl, ll, el, q );
     d = ComplexOrderedMapPolynomial.DIPSUM(a,ComplexOrderedMapPolynomial.DIPSUM(b,c));
     e = ComplexOrderedMapPolynomial.DIPSUM(ComplexOrderedMapPolynomial.DIPSUM(a,b),c);

     assertEquals("a+(b+c) = (a+b)+c",d,e);
     assertTrue("a+(b+c) = (a+b)+c", d.equals(e) );

 }


/**
 * Test multiplication
 * 
 */
 public void testMultiplication() {

     a = ComplexOrderedMapPolynomial.DICRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );
     //a = ComplexOrderedMapPolynomial.DICRAS(1, kl, 4, el, q );

     b = ComplexOrderedMapPolynomial.DICRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );

     c = ComplexOrderedMapPolynomial.DIPPR(b,a);
     d = ComplexOrderedMapPolynomial.DIPPR(a,b);
     assertTrue("not isZERO( c )", !c.isZERO() );
     assertTrue("not isZERO( d )", !d.isZERO() );

     e = ComplexOrderedMapPolynomial.DIPDIF(d,c);
     assertTrue("isZERO( a*b-b*a ) " + e, e.isZERO() );

     assertEquals("a*b = b*a",c,d);
     assertTrue("a*b = b*a", c.equals(d) );

     c = ComplexOrderedMapPolynomial.DICRAS(rl, kl, ll, el, q );
     d = ComplexOrderedMapPolynomial.DIPPR(a,ComplexOrderedMapPolynomial.DIPPR(b,c));
     e = ComplexOrderedMapPolynomial.DIPPR(ComplexOrderedMapPolynomial.DIPPR(a,b),c);

     assertEquals("a(bc) = (ab)c",d,e);
     assertTrue("a(bc) = (ab)c", d.equals(e) );

 }


/**
 * Test object multiplication
 * 
 */
 public void testMultiplication1() {

     a = ComplexOrderedMapPolynomial.DICRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );
     //a = ComplexOrderedMapPolynomial.DICRAS(1, kl, 4, el, q );

     b = ComplexOrderedMapPolynomial.DICRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );

     c = b.multiply(a);
     d = a.multiply(b);
     assertTrue("not isZERO( c )", !c.isZERO() );
     assertTrue("not isZERO( d )", !d.isZERO() );

     e = d.subtract(c);
     assertTrue("isZERO( a*b-b*a ) " + e, e.isZERO() );

     assertEquals("a*b = b*a",c,d);
     assertTrue("a*b = b*a", c.equals(d) );

     c = ComplexOrderedMapPolynomial.DICRAS(rl, kl, ll, el, q );
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

     ComplexOrderedMapPolynomial a, b;

     a = ComplexOrderedMapPolynomial.DICRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );
     //a = ComplexOrderedMapPolynomial.DICRAS(1, kl, 4, el, q );

     b = ComplexOrderedMapPolynomial.DICRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );

     c = b.multiplyA(a);
     d = a.multiplyA(b);
     assertTrue("not isZERO( c )", !c.isZERO() );
     assertTrue("not isZERO( d )", !d.isZERO() );

     e = d.subtract(c);
     assertTrue("isZERO( a*b-b*a ) " + e, e.isZERO() );

     assertEquals("a*b = b*a",c,d);
     assertTrue("a*b = b*a", c.equals(d) );

     c = ComplexOrderedMapPolynomial.DICRAS(rl, kl, ll, el, q );
     d = a.multiplyA( b.multiplyA(c) );
     e = ((ComplexOrderedMapPolynomial)a.multiplyA(b)).multiplyA(c);

     assertEquals("a(bc) = (ab)c",d,e);
     assertTrue("a(bc) = (ab)c", d.equals(e) );

 }

}
