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

import org.apache.log4j.BasicConfigurator;


/**
 * RatSolvableOrderedMapPolynomial Test using JUnit 
 * @author Heinz Kredel.
 */

public class RatSolvableOrderedMapPolynomialTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>RatSolvableOrderedMapPolynomialTest</CODE> object.
 * @param name String
 */
   public RatSolvableOrderedMapPolynomialTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(RatSolvableOrderedMapPolynomialTest.class);
     return suite;
   }

   private final static int bitlen = 100;

   OrderedPolynomial a;
   OrderedPolynomial b;
   OrderedPolynomial c;
   OrderedPolynomial d;
   OrderedPolynomial e;

   int rl = 5; 
   int kl = 10;
   int ll = 7;
   int el = 4;
   float q = 0.5f;

   RelationTable table;

   protected void setUp() {
       table = new RelationTable(); // symmetric test
       a = b = c = d = e = null;
   }

   protected void tearDown() {
       table = null;
       a = b = c = d = e = null;
   }


/**
 * Test constructor and toString
 * 
 */
 public void testConstructor() {
     a = new RatSolvableOrderedMapPolynomial(table);
     assertTrue("length( a ) = 0", a.length() == 0);
     assertTrue("isZERO( a )", a.isZERO() );
     assertTrue("isONE( a )", !a.isONE() );

     b = new RatSolvableOrderedMapPolynomial(table,rl);
     assertTrue("length( b ) = 0", b.length() == 0);
     assertTrue("isZERO( b )", b.isZERO() );
     assertTrue("isONE( b )", !b.isONE() );

     c = RatSolvableOrderedMapPolynomial.ONE;
     assertTrue("length( c ) = 1", c.length() == 1);
     assertTrue("isZERO( c )", !c.isZERO() );
     assertTrue("isONE( c )", c.isONE() );

     d = RatSolvableOrderedMapPolynomial.ZERO;
     assertTrue("length( d ) = 0", d.length() == 0);
     assertTrue("isZERO( d )", d.isZERO() );
     assertTrue("isONE( d )", !d.isONE() );
 }


/**
 * Test random polynomial
 * 
 */
 public void testRandom() {
     for (int i = 0; i < 2; i++) {
         a = RatSolvableOrderedMapPolynomial.DIRRAS(table,rl+i, kl*(i+1), ll+2*i, el+i, q );
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

     a = RatSolvableOrderedMapPolynomial.DIRRAS(table, rl, kl, ll, el, q );

     c = RatSolvableOrderedMapPolynomial.DIPDIF(a,a);
     assertTrue("a-a = 0", c.isZERO() );

     b = RatSolvableOrderedMapPolynomial.DIPSUM(a,a);
     c = RatSolvableOrderedMapPolynomial.DIPDIF(b,a);

     assertEquals("a+a-a = a",c,a);
     assertTrue("a+a-a = a", c.equals(a) );

     b = RatSolvableOrderedMapPolynomial.DIRRAS(table, rl, kl, ll, el, q );
     c = RatSolvableOrderedMapPolynomial.DIPSUM(b,a);
     d = RatSolvableOrderedMapPolynomial.DIPSUM(a,b);

     assertEquals("a+b = b+a",c,d);
     assertTrue("a+b = b+a", c.equals(d) );

     c = RatSolvableOrderedMapPolynomial.DIRRAS(table, rl, kl, ll, el, q );
     d = RatSolvableOrderedMapPolynomial.DIPSUM(a,RatSolvableOrderedMapPolynomial.DIPSUM(b,c));
     e = RatSolvableOrderedMapPolynomial.DIPSUM(RatSolvableOrderedMapPolynomial.DIPSUM(a,b),c);

     assertEquals("a+(b+c) = (a+b)+c",d,e);
     assertTrue("a+(b+c) = (a+b)+c", d.equals(e) );

     ExpVector u = ExpVector.EVRAND(rl,el,q);
     BigRational x = BigRational.RNRAND(kl);

     b = new RatSolvableOrderedMapPolynomial(table, x, u);
     c = a.add(b);
     d = a.add(x,u);
     assertEquals("a+p(x,u) = a+(x,u)",c,d);

     c = a.subtract(b);
     d = a.subtract(x,u);
     assertEquals("a-p(x,u) = a-(x,u)",c,d);

     a = new RatSolvableOrderedMapPolynomial(table);
     b = new RatSolvableOrderedMapPolynomial(table, x, u);
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

     a = RatSolvableOrderedMapPolynomial.DIRRAS(table, rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );
     //a = RatSolvableOrderedMapPolynomial.DIRRAS(1, kl, 4, el, q );

     b = RatSolvableOrderedMapPolynomial.DIRRAS(table, rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );

     c = RatSolvableOrderedMapPolynomial.DIPPR(b,a);
     d = RatSolvableOrderedMapPolynomial.DIPPR(a,b);
     assertTrue("not isZERO( c )", !c.isZERO() );
     assertTrue("not isZERO( d )", !d.isZERO() );

     e = RatSolvableOrderedMapPolynomial.DIPDIF(d,c);
     assertTrue("isZERO( a*b-b*a ) " + e, e.isZERO() );

     assertEquals("a*b = b*a",c,d);
     assertTrue("a*b = b*a", c.equals(d) );

     c = RatSolvableOrderedMapPolynomial.DIRRAS(table, rl, kl, ll, el, q );
     d = RatSolvableOrderedMapPolynomial.DIPPR(a,RatSolvableOrderedMapPolynomial.DIPPR(b,c));
     e = RatSolvableOrderedMapPolynomial.DIPPR(RatSolvableOrderedMapPolynomial.DIPPR(a,b),c);

     assertEquals("a(bc) = (ab)c",d,e);
     assertTrue("a(bc) = (ab)c", d.equals(e) );

 }


/**
 * Test object multiplication
 * 
 */
 public void testMultiplication1() {

     a = RatSolvableOrderedMapPolynomial.DIRRAS(table, rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );
     //a = RatSolvableOrderedMapPolynomial.DIRRAS(1, kl, 4, el, q );

     b = RatSolvableOrderedMapPolynomial.DIRRAS(table, rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );

     c = b.multiply(a);
     d = a.multiply(b);
     assertTrue("not isZERO( c )", !c.isZERO() );
     assertTrue("not isZERO( d )", !d.isZERO() );

     e = d.subtract(c);
     assertTrue("isZERO( a*b-b*a ) " + e, e.isZERO() );

     assertEquals("a*b = b*a",c,d);
     assertTrue("a*b = b*a", c.equals(d) );

     c = RatSolvableOrderedMapPolynomial.DIRRAS(table, rl, kl, ll, el, q );
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

     e = new RatSolvableOrderedMapPolynomial(table,y,u);
     d = b.multiply(e);
     assertEquals("b.monic() = b(1/ldcf(b))",c,d);

     d = e.multiply(b);
     assertEquals("b.monic() = (1/ldcf(b) (0))*b",c,d);
 }


/**
 * Test Weyl polynomials
 * 
 */
 public void testWeyl() {
     WeylRelations wl = new WeylRelations();
     int rloc = 4;
     RelationTable table = wl.generate(rloc);

     a = RatSolvableOrderedMapPolynomial.DIRRAS(table, rloc, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );

     b = RatSolvableOrderedMapPolynomial.DIRRAS(table, rloc, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );

     // non commutative
     c = b.multiply(a);
     d = a.multiply(b);
     //System.out.println("c = " + c);
     //System.out.println("d = " + d);
     assertTrue("not isZERO( c )", !c.isZERO() );
     assertTrue("not isZERO( d )", !d.isZERO() );

     e = d.subtract(c);
     assertTrue("!isZERO( a*b-b*a ) " + e, !e.isZERO() );
     assertTrue("a*b != b*a", !c.equals(d) );

     c = RatSolvableOrderedMapPolynomial.DIRRAS(table, rloc, kl, ll, el, q );
     // associative
     d = a.multiply( b.multiply(c) );
     e = (a.multiply(b)).multiply(c);
     //System.out.println("d = " + d);
     //System.out.println("e = " + e);

     assertEquals("a(bc) = (ab)c",d,e);
     assertTrue("a(bc) = (ab)c", d.equals(e) );
 }

}
