/*
 * $Id$
 */

package edu.jas.poly;

//import edu.jas.poly.QuatPolynomial;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * QuatMapPolynomial Test using JUnit 
 * @author Heinz Kredel.
 */

public class QuatMapPolynomialTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>QuatMapPolynomialTest</CODE> object.
 * @param name String
 */
   public QuatMapPolynomialTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(QuatMapPolynomialTest.class);
     return suite;
   }

   private final static int bitlen = 100;

   UnorderedPolynomial a;
   UnorderedPolynomial b;
   UnorderedPolynomial c;
   UnorderedPolynomial d;
   UnorderedPolynomial e;
   UnorderedPolynomial f;

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
     a = new QuatMapPolynomial();
     assertTrue("length( a ) = 0", a.length() == 0);
     //assertTrue("isZERO( a )", a.isZERO() );
     assertTrue("isONE( a )", !a.isONE() );

     b = new QuatMapPolynomial(rl);
     assertTrue("length( b ) = 0", b.length() == 0);
     //assertTrue("isZERO( b )", b.isZERO() );
     assertTrue("isONE( b )", !b.isONE() );

     c = QuatMapPolynomial.ONE;
     assertTrue("length( c ) = 1", c.length() == 1);
     assertTrue("isZERO( c )", !c.isZERO() );
     assertTrue("isONE( c )", c.isONE() );

     d = QuatMapPolynomial.ZERO;
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
         a = QuatMapPolynomial.DIQRAS(rl+i, kl*(i+1), ll+2*i, el+i, q );
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

     a = QuatMapPolynomial.DIQRAS(rl, kl, ll, el, q );

     c = QuatMapPolynomial.DIPDIF(a,a);
     assertTrue("a-a = 0", c.isZERO() );

     b = QuatMapPolynomial.DIPSUM(a,a);
     c = QuatMapPolynomial.DIPDIF(b,a);

     assertEquals("a+a-a = a",c,a);
     assertTrue("a+a-a = a", c.equals(a) );

     b = QuatMapPolynomial.DIQRAS(rl, kl, ll, el, q );
     c = QuatMapPolynomial.DIPSUM(b,a);
     d = QuatMapPolynomial.DIPSUM(a,b);

     assertEquals("a+b = b+a",c,d);
     assertTrue("a+b = b+a", c.equals(d) );

     c = QuatMapPolynomial.DIQRAS(rl, kl, ll, el, q );
     d = QuatMapPolynomial.DIPSUM(a,QuatMapPolynomial.DIPSUM(b,c));
     e = QuatMapPolynomial.DIPSUM(QuatMapPolynomial.DIPSUM(a,b),c);

     assertEquals("a+(b+c) = (a+b)+c",d,e);
     assertTrue("a+(b+c) = (a+b)+c", d.equals(e) );

 }


/**
 * Test multiplication
 * 
 */
 public void testMultiplication() {

     a = QuatMapPolynomial.DIQRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );

     b = QuatMapPolynomial.DIQRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );

     c = QuatMapPolynomial.DIPPR(b,a);
     d = QuatMapPolynomial.DIPPR(a,b);
     assertTrue("not isZERO( c )", !c.isZERO() );
     assertTrue("not isZERO( d )", !d.isZERO() );

     e = QuatMapPolynomial.DIPDIF(d,c);
     assertTrue("!isZERO( a*b-b*a ) " + e, !e.isZERO() );

     assertTrue("a*b != b*a", !c.equals(d) );

     c = QuatMapPolynomial.DIQRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( c )", !c.isZERO() );

     d = QuatMapPolynomial.DIPPR(a,QuatMapPolynomial.DIPPR(b,c));
     e = QuatMapPolynomial.DIPPR(QuatMapPolynomial.DIPPR(a,b),c);

     f = QuatMapPolynomial.DIPDIF(d,e);
     assertEquals("a(bc) = (ab)c",d,e);
     assertTrue("a(bc) = (ab)c "+f, f.isZERO() );

 }

}
