/*
 * $Id$
 */

package edu.jas.poly;

//import edu.jas.poly.IntPolynomial;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * IntPolynomial Test using JUnit 
 * @author Heinz Kredel.
 */

public class IntPolynomialTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>IntPolynomialTest</CODE> object.
 * @param name String
 */
   public IntPolynomialTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(IntPolynomialTest.class);
     return suite;
   }

   private final static int bitlen = 100;

   Polynomial a;
   Polynomial b;
   Polynomial c;
   Polynomial d;
   Polynomial e;

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
     a = new IntPolynomial();
     assertTrue("length( a ) = 0", a.length() == 0);
     //assertTrue("isZERO( a )", a.isZERO() );
     assertTrue("isONE( a )", !a.isONE() );

     b = new IntPolynomial(rl);
     assertTrue("length( b ) = 0", b.length() == 0);
     //assertTrue("isZERO( b )", b.isZERO() );
     assertTrue("isONE( b )", !b.isONE() );

     c = IntPolynomial.ONE;
     assertTrue("length( c ) = 1", c.length() == 1);
     assertTrue("isZERO( c )", !c.isZERO() );
     assertTrue("isONE( c )", c.isONE() );

     d = IntPolynomial.ZERO;
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
         a = IntPolynomial.DIIRAS(rl+i, kl*(i+1), ll+2*i, el+i, q );
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

     a = IntPolynomial.DIIRAS(rl, kl, ll, el, q );

     b = IntPolynomial.DIPSUM(a,a);
     c = IntPolynomial.DIPDIF(b,a);

     assertEquals("a+a-a = a",c,a);
     assertTrue("a+a-a = a", c.equals(a) );

     b = IntPolynomial.DIIRAS(rl, kl, ll, el, q );
     c = IntPolynomial.DIPSUM(b,a);
     d = IntPolynomial.DIPSUM(a,b);

     assertEquals("a+b = b+a",c,d);
     assertTrue("a+b = b+a", c.equals(d) );

     c = IntPolynomial.DIIRAS(rl, kl, ll, el, q );
     d = TreePolynomial.DIPSUM(a,TreePolynomial.DIPSUM(b,c));
     e = TreePolynomial.DIPSUM(TreePolynomial.DIPSUM(a,b),c);

     assertEquals("a+(b+c) = (a+b)+c",d,e);
     assertTrue("a+(b+c) = (a+b)+c", d.equals(e) );
 }


/**
 * Test multiplication
 * 
 */
 public void testMultiplication() {

     a = IntPolynomial.DIIRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );

     // does not work
     //c = IntPolynomial.DIPPR(a,IntPolynomial.ONE);
     //assertEquals("a*1 = a",c,a);

     // does not work
     //d = IntPolynomial.DIPPR(a,IntPolynomial.ZERO);
     //assertEquals("a*0 = 0",d,IntPolynomial.ZERO);
     //assertTrue("isZERO( a*0 )", d.isZERO() );


     b = IntPolynomial.DIIRAS(rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );

     c = IntPolynomial.DIPPR(b,a);
     d = IntPolynomial.DIPPR(a,b);
     assertTrue("not isZERO( c )", !c.isZERO() );
     assertTrue("not isZERO( d )", !d.isZERO() );

     e = IntPolynomial.DIPDIF(d,c);
     assertTrue("isZERO( a*b-b*a )", e.isZERO() );

     assertEquals("a*b = b*a",c,d);
     assertTrue("a*b = b*a", c.equals(d) );

     c = IntPolynomial.DIIRAS(rl, kl, ll, el, q );
     d = TreePolynomial.DIPPR(a,TreePolynomial.DIPPR(b,c));
     e = TreePolynomial.DIPPR(TreePolynomial.DIPPR(a,b),c);

     assertEquals("a(bc) = (ab)c",d,e);
     assertTrue("a(bc) = (ab)c", d.equals(e) );
 }

}
