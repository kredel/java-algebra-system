/*
 * $Id$
 */

package edu.jas.poly;

import edu.jas.arith.BigQuaternion;
import edu.jas.arith.Coefficient;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;


/**
 * QuatSolvableOrderedMapPolynomial Test using JUnit 
 * @author Heinz Kredel.
 */

public class QuatSolvableOrderedMapPolynomialTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>QuatSolvableOrderedMapPolynomialTest</CODE> object.
 * @param name String
 */
   public QuatSolvableOrderedMapPolynomialTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(QuatSolvableOrderedMapPolynomialTest.class);
     return suite;
   }

   private final static int bitlen = 100;

   SolvableOrderedMapPolynomial dummy;
   OrderedPolynomial a;
   OrderedPolynomial b;
   OrderedPolynomial c;
   OrderedPolynomial d;
   OrderedPolynomial e;

   int rl = 5; 
   int kl = 10;
   int ll = 5;
   int el = 3;
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
     a = new QuatSolvableOrderedMapPolynomial(table);
     assertTrue("length( a ) = 0", a.length() == 0);
     assertTrue("isZERO( a )", a.isZERO() );
     assertTrue("isONE( a )", !a.isONE() );

     b = new QuatSolvableOrderedMapPolynomial(table,rl);
     assertTrue("length( b ) = 0", b.length() == 0);
     assertTrue("isZERO( b )", b.isZERO() );
     assertTrue("isONE( b )", !b.isONE() );

     c = QuatSolvableOrderedMapPolynomial.ONE;
     assertTrue("length( c ) = 1", c.length() == 1);
     assertTrue("isZERO( c )", !c.isZERO() );
     assertTrue("isONE( c )", c.isONE() );

     d = QuatSolvableOrderedMapPolynomial.ZERO;
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
         a = QuatSolvableOrderedMapPolynomial.DIQRAS(table,rl+i, kl*(i+1), ll+2*i, el+i, q );
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

     a = QuatSolvableOrderedMapPolynomial.DIQRAS(table, rl, kl, ll, el, q );

     c = QuatSolvableOrderedMapPolynomial.DIPDIF(a,a);
     assertTrue("a-a = 0", c.isZERO() );

     b = QuatSolvableOrderedMapPolynomial.DIPSUM(a,a);
     c = QuatSolvableOrderedMapPolynomial.DIPDIF(b,a);

     assertEquals("a+a-a = a",c,a);
     assertTrue("a+a-a = a", c.equals(a) );

     b = QuatSolvableOrderedMapPolynomial.DIQRAS(table, rl, kl, ll, el, q );
     c = QuatSolvableOrderedMapPolynomial.DIPSUM(b,a);
     d = QuatSolvableOrderedMapPolynomial.DIPSUM(a,b);

     assertEquals("a+b = b+a",c,d);
     assertTrue("a+b = b+a", c.equals(d) );

     c = QuatSolvableOrderedMapPolynomial.DIQRAS(table, rl, kl, ll, el, q );
     d = QuatSolvableOrderedMapPolynomial.DIPSUM(a,QuatSolvableOrderedMapPolynomial.DIPSUM(b,c));
     e = QuatSolvableOrderedMapPolynomial.DIPSUM(QuatSolvableOrderedMapPolynomial.DIPSUM(a,b),c);

     assertEquals("a+(b+c) = (a+b)+c",d,e);
     assertTrue("a+(b+c) = (a+b)+c", d.equals(e) );

     ExpVector u = ExpVector.EVRAND(rl,el,q);
     BigQuaternion x = BigQuaternion.QRAND(kl);

     b = new QuatSolvableOrderedMapPolynomial(table, x, u);
     c = a.add(b);
     d = a.add(x,u);
     assertEquals("a+p(x,u) = a+(x,u)",c,d);

     c = a.subtract(b);
     d = a.subtract(x,u);
     assertEquals("a-p(x,u) = a-(x,u)",c,d);

     a = new QuatSolvableOrderedMapPolynomial(table);
     b = new QuatSolvableOrderedMapPolynomial(table, x, u);
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

     a = QuatSolvableOrderedMapPolynomial.DIQRAS(table, rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );
     //a = QuatSolvableOrderedMapPolynomial.DIQRAS(1, kl, 4, el, q );

     b = QuatSolvableOrderedMapPolynomial.DIQRAS(table, rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );

     c = QuatSolvableOrderedMapPolynomial.DIPPR(b,a);
     d = QuatSolvableOrderedMapPolynomial.DIPPR(a,b);
     assertTrue("not isZERO( c )", !c.isZERO() );
     assertTrue("not isZERO( d )", !d.isZERO() );

     e = QuatSolvableOrderedMapPolynomial.DIPDIF(d,c);
     assertTrue("!isZERO( a*b-b*a ) " + e, !e.isZERO() );

     //assertEquals("a*b = b*a",c,d);
     assertTrue("a*b != b*a", !c.equals(d) );

     c = QuatSolvableOrderedMapPolynomial.DIQRAS(table, rl, kl, ll, el, q );
     d = QuatSolvableOrderedMapPolynomial.DIPPR(a,QuatSolvableOrderedMapPolynomial.DIPPR(b,c));
     e = QuatSolvableOrderedMapPolynomial.DIPPR(QuatSolvableOrderedMapPolynomial.DIPPR(a,b),c);

     assertEquals("a(bc) = (ab)c",d,e);
     assertTrue("a(bc) = (ab)c", d.equals(e) );

 }


/**
 * Test object multiplication
 * 
 */
 public void testMultiplication1() {

     a = QuatSolvableOrderedMapPolynomial.DIQRAS(table, rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );
     //a = QuatSolvableOrderedMapPolynomial.DIQRAS(1, kl, 4, el, q );

     b = QuatSolvableOrderedMapPolynomial.DIQRAS(table, rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );

     c = b.multiply(a);
     d = a.multiply(b);
     assertTrue("not isZERO( c )", !c.isZERO() );
     assertTrue("not isZERO( d )", !d.isZERO() );

     e = d.subtract(c);
     assertTrue("!isZERO( a*b-b*a ) " + e, !e.isZERO() );

     //assertEquals("a*b = b*a",c,d);
     assertTrue("a*b != b*a", !c.equals(d) );

     c = QuatSolvableOrderedMapPolynomial.DIQRAS(table, rl, kl, ll, el, q );
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

     e = new QuatSolvableOrderedMapPolynomial(table,y,u);
     d = b.multiply(e);
     assertEquals("b.monic() = b(1/ldcf(b))",c,d);
 }


/**
 * Test Weyl polynomials
 * 
 */
 public void testWeyl() {
     dummy = RatSolvableOrderedMapPolynomial.DIRRAS(table/*null*/,rl,kl,ll,el,q);

     WeylRelations wl = new WeylRelations();
     int rloc = 4;
     RelationTable table = wl.generate(rloc,dummy);
     dummy = null;

     a = QuatSolvableOrderedMapPolynomial.DIQRAS(table, rloc, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );

     b = QuatSolvableOrderedMapPolynomial.DIQRAS(table, rloc, kl, ll, el, q );
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

     c = QuatSolvableOrderedMapPolynomial.DIQRAS(table, rloc, kl, ll, el, q );
     // associative
     d = a.multiply( b.multiply(c) );
     e = (a.multiply(b)).multiply(c);
     //System.out.println("d = " + d);
     //System.out.println("e = " + e);

     assertEquals("a(bc) = (ab)c",d,e);
     assertTrue("a(bc) = (ab)c", d.equals(e) );
 }

}
