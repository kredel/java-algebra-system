/*
 * $Id$
 */

package edu.jas.poly;

//import edu.jas.poly.ComplexPolynomial;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * ComplexMapPolynomial Test using JUnit 
 * @author Heinz Kredel.
 */

public class ComplexMapPolynomialTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>ComplexMapPolynomialTest</CODE> object.
 * @param name String
 */
   public ComplexMapPolynomialTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(ComplexMapPolynomialTest.class);
     return suite;
   }

   private final static int bitlen = 100;

   UnorderedPolynomial a;
   UnorderedPolynomial b;
   UnorderedPolynomial c;
   UnorderedPolynomial d;
   UnorderedPolynomial e;

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
     a = new ComplexMapPolynomial();
     assertTrue("length( a ) = 0", a.length() == 0);
     //assertTrue("isZERO( a )", a.isZERO() );
     assertTrue("isONE( a )", !a.isONE() );

     b = new ComplexMapPolynomial(rl);
     assertTrue("length( b ) = 0", b.length() == 0);
     //assertTrue("isZERO( b )", b.isZERO() );
     assertTrue("isONE( b )", !b.isONE() );

     c = ComplexMapPolynomial.ONE;
     assertTrue("length( c ) = 1", c.length() == 1);
     assertTrue("isZERO( c )", !c.isZERO() );
     assertTrue("isONE( c )", c.isONE() );

     d = ComplexMapPolynomial.ZERO;
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
         a = ComplexMapPolynomial.DICRAS(rl+i, kl*(i+1), ll+2*i, el+i, q );
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

     a = ComplexMapPolynomial.DICRAS(rl, kl, ll, el, q );

     c = ComplexMapPolynomial.DIPDIF(a,a);
     assertTrue("a-a = 0", c.isZERO() );

     b = ComplexMapPolynomial.DIPSUM(a,a);
     c = ComplexMapPolynomial.DIPDIF(b,a);

     assertEquals("a+a-a = a",c,a);
     assertTrue("a+a-a = a", c.equals(a) );

     b = ComplexMapPolynomial.DICRAS(rl, kl, ll, el, q );
     c = ComplexMapPolynomial.DIPSUM(b,a);
     d = ComplexMapPolynomial.DIPSUM(a,b);

     assertEquals("a+b = b+a",c,d);
     assertTrue("a+b = b+a", c.equals(d) );

     c = ComplexMapPolynomial.DICRAS(rl, kl, ll, el, q );
     d = ComplexMapPolynomial.DIPSUM(a,ComplexMapPolynomial.DIPSUM(b,c));
     e = ComplexMapPolynomial.DIPSUM(ComplexMapPolynomial.DIPSUM(a,b),c);

     assertEquals("a+(b+c) = (a+b)+c",d,e);
     assertTrue("a+(b+c) = (a+b)+c", d.equals(e) );

 }


/**
 * Test multiplication
 * 
 */
 public void testMultiplication() {

     a = ComplexMapPolynomial.DICRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );
     //a = ComplexMapPolynomial.DICRAS(1, kl, 4, el, q );

     b = ComplexMapPolynomial.DICRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );

     c = ComplexMapPolynomial.DIPPR(b,a);
     d = ComplexMapPolynomial.DIPPR(a,b);
     assertTrue("not isZERO( c )", !c.isZERO() );
     assertTrue("not isZERO( d )", !d.isZERO() );

     e = ComplexMapPolynomial.DIPDIF(d,c);
     assertTrue("isZERO( a*b-b*a ) " + e, e.isZERO() );

     assertEquals("a*b = b*a",c,d);
     assertTrue("a*b = b*a", c.equals(d) );

     c = ComplexMapPolynomial.DICRAS(rl, kl, ll, el, q );
     d = ComplexMapPolynomial.DIPPR(a,ComplexMapPolynomial.DIPPR(b,c));
     e = ComplexMapPolynomial.DIPPR(ComplexMapPolynomial.DIPPR(a,b),c);

     assertEquals("a(bc) = (ab)c",d,e);
     assertTrue("a(bc) = (ab)c", d.equals(e) );

 }

}
