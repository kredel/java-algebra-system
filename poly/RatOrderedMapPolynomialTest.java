/*
 * $Id$
 */

package edu.jas.poly;

//import edu.jas.poly.RatPolynomial;
import edu.jas.arith.BigRational;
import edu.jas.arith.Coefficient;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * RatOrderedMapPolynomial Test using JUnit 
 * @author Heinz Kredel.
 */

public class RatOrderedMapPolynomialTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>RatOrderedMapPolynomialTest</CODE> object.
 * @param name String
 */
   public RatOrderedMapPolynomialTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(RatOrderedMapPolynomialTest.class);
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
     a = new RatOrderedMapPolynomial();
     assertTrue("length( a ) = 0", a.length() == 0);
     //assertTrue("isZERO( a )", a.isZERO() );
     assertTrue("isONE( a )", !a.isONE() );

     b = new RatOrderedMapPolynomial(rl);
     assertTrue("length( b ) = 0", b.length() == 0);
     //assertTrue("isZERO( b )", b.isZERO() );
     assertTrue("isONE( b )", !b.isONE() );

     c = RatOrderedMapPolynomial.ONE;
     assertTrue("length( c ) = 1", c.length() == 1);
     assertTrue("isZERO( c )", !c.isZERO() );
     assertTrue("isONE( c )", c.isONE() );

     d = RatOrderedMapPolynomial.ZERO;
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
         a = RatOrderedMapPolynomial.DIRRAS(rl+i, kl*(i+1), ll+2*i, el+i, q );
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

     a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );

     c = RatOrderedMapPolynomial.DIPDIF(a,a);
     assertTrue("a-a = 0", c.isZERO() );

     b = RatOrderedMapPolynomial.DIPSUM(a,a);
     c = RatOrderedMapPolynomial.DIPDIF(b,a);

     assertEquals("a+a-a = a",c,a);
     assertTrue("a+a-a = a", c.equals(a) );

     b = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     c = RatOrderedMapPolynomial.DIPSUM(b,a);
     d = RatOrderedMapPolynomial.DIPSUM(a,b);

     assertEquals("a+b = b+a",c,d);
     assertTrue("a+b = b+a", c.equals(d) );

     c = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     d = RatOrderedMapPolynomial.DIPSUM(a,RatOrderedMapPolynomial.DIPSUM(b,c));
     e = RatOrderedMapPolynomial.DIPSUM(RatOrderedMapPolynomial.DIPSUM(a,b),c);

     assertEquals("a+(b+c) = (a+b)+c",d,e);
     assertTrue("a+(b+c) = (a+b)+c", d.equals(e) );

     ExpVector u = ExpVector.EVRAND(rl,el,q);
     BigRational x = BigRational.RNRAND(kl);

     b = new RatOrderedMapPolynomial(x, u);
     c = a.add(b);
     d = a.add(x,u);
     assertEquals("a+p(x,u) = a+(x,u)",c,d);

     c = a.subtract(b);
     d = a.subtract(x,u);
     assertEquals("a-p(x,u) = a-(x,u)",c,d);

     a = new RatOrderedMapPolynomial();
     b = new RatOrderedMapPolynomial(x, u);
     c = b.add(a);
     d = a.add(x,u);
     assertEquals("a+p(x,u) = a+(x,u)",c,d);

     c = a.subtract(b);
     d = a.subtract(x,u);
     assertEquals("a-p(x,u) = a-(x,u)",c,d);
 }


/**
 * Test multiplication
 * 
 */
 public void testMultiplication() {

     a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );
     //a = RatOrderedMapPolynomial.DIRRAS(1, kl, 4, el, q );

     b = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );

     c = RatOrderedMapPolynomial.DIPPR(b,a);
     d = RatOrderedMapPolynomial.DIPPR(a,b);
     assertTrue("not isZERO( c )", !c.isZERO() );
     assertTrue("not isZERO( d )", !d.isZERO() );

     e = RatOrderedMapPolynomial.DIPDIF(d,c);
     assertTrue("isZERO( a*b-b*a ) " + e, e.isZERO() );

     assertEquals("a*b = b*a",c,d);
     assertTrue("a*b = b*a", c.equals(d) );

     c = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     d = RatOrderedMapPolynomial.DIPPR(a,RatOrderedMapPolynomial.DIPPR(b,c));
     e = RatOrderedMapPolynomial.DIPPR(RatOrderedMapPolynomial.DIPPR(a,b),c);

     assertEquals("a(bc) = (ab)c",d,e);
     assertTrue("a(bc) = (ab)c", d.equals(e) );

 }


/**
 * Test object multiplication
 * 
 */
 public void testMultiplication1() {

     a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );
     //a = RatOrderedMapPolynomial.DIRRAS(1, kl, 4, el, q );

     b = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );

     c = b.multiply(a);
     d = a.multiply(b);
     assertTrue("not isZERO( c )", !c.isZERO() );
     assertTrue("not isZERO( d )", !d.isZERO() );

     e = d.subtract(c);
     assertTrue("isZERO( a*b-b*a ) " + e, e.isZERO() );

     assertEquals("a*b = b*a",c,d);
     assertTrue("a*b = b*a", c.equals(d) );

     c = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     d = a.multiply( b.multiply(c) );
     e = (a.multiply(b)).multiply(c);

     assertEquals("a(bc) = (ab)c",d,e);
     assertTrue("a(bc) = (ab)c", d.equals(e) );

     Coefficient x = a.leadingBaseCoefficient().inverse();
     c = a.monic();
     d = a.multiply(x);
     assertEquals("a.monic() = a(1/ldcf(a))",c,d);

     ExpVector u = new ExpVector(rl);
     Coefficient y = b.leadingBaseCoefficient().inverse();
     c = b.monic();
     d = b.multiply(y,u);
     assertEquals("b.monic() = b(1/ldcf(b))",c,d);

     e = new RatOrderedMapPolynomial(y,u);
     d = b.multiply(e);
     assertEquals("b.monic() = b(1/ldcf(b))",c,d);

     d = e.multiply(b);
     assertEquals("b.monic() = (1/ldcf(b) (0))*b",c,d);
 }

/**
 * Test object multiplication with add
 * 
 */
 public void testMultiplication2() {

     RatOrderedMapPolynomial a, b;

     a = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );
     //a = RatOrderedMapPolynomial.DIRRAS(1, kl, 4, el, q );

     b = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );

     c = b.multiplyA(a);
     d = a.multiplyA(b);
     assertTrue("not isZERO( c )", !c.isZERO() );
     assertTrue("not isZERO( d )", !d.isZERO() );

     e = d.subtract(c);
     assertTrue("isZERO( a*b-b*a ) " + e, e.isZERO() );

     assertEquals("a*b = b*a",c,d);
     assertTrue("a*b = b*a", c.equals(d) );

     c = RatOrderedMapPolynomial.DIRRAS(rl, kl, ll, el, q );
     d = a.multiplyA( b.multiplyA(c) );
     e = ((RatOrderedMapPolynomial)a.multiplyA(b)).multiplyA(c);

     assertEquals("a(bc) = (ab)c",d,e);
     assertTrue("a(bc) = (ab)c", d.equals(e) );

 }

}
