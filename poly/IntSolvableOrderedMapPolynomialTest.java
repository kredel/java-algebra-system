/*
 * $Id$
 */

package edu.jas.poly;

import edu.jas.arith.BigInteger;
import edu.jas.arith.Coefficient;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;


/**
 * IntSolvableOrderedMapPolynomial Test using JUnit 
 * @author Heinz Kredel.
 */

public class IntSolvableOrderedMapPolynomialTest extends TestCase {

/**
 * main
 */
   public static void main (String[] args) {
          BasicConfigurator.configure();
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>IntSolvableOrderedMapPolynomialTest</CODE> object.
 * @param name String
 */
   public IntSolvableOrderedMapPolynomialTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(IntSolvableOrderedMapPolynomialTest.class);
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
     a = new IntSolvableOrderedMapPolynomial(table);
     assertTrue("length( a ) = 0", a.length() == 0);
     assertTrue("isZERO( a )", a.isZERO() );
     assertTrue("isONE( a )", !a.isONE() );

     b = new IntSolvableOrderedMapPolynomial(table,rl);
     assertTrue("length( b ) = 0", b.length() == 0);
     assertTrue("isZERO( b )", b.isZERO() );
     assertTrue("isONE( b )", !b.isONE() );

     c = IntSolvableOrderedMapPolynomial.ONE;
     assertTrue("length( c ) = 1", c.length() == 1);
     assertTrue("isZERO( c )", !c.isZERO() );
     assertTrue("isONE( c )", c.isONE() );

     d = IntSolvableOrderedMapPolynomial.ZERO;
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
         a = IntSolvableOrderedMapPolynomial.DIIRAS(table,rl+i, kl*(i+1), ll+2*i, el+i, q );
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

     a = IntSolvableOrderedMapPolynomial.DIIRAS(table, rl, kl, ll, el, q );

     c = IntSolvableOrderedMapPolynomial.DIPDIF(a,a);
     assertTrue("a-a = 0", c.isZERO() );

     b = IntSolvableOrderedMapPolynomial.DIPSUM(a,a);
     c = IntSolvableOrderedMapPolynomial.DIPDIF(b,a);

     assertEquals("a+a-a = a",c,a);
     assertTrue("a+a-a = a", c.equals(a) );

     b = IntSolvableOrderedMapPolynomial.DIIRAS(table, rl, kl, ll, el, q );
     c = IntSolvableOrderedMapPolynomial.DIPSUM(b,a);
     d = IntSolvableOrderedMapPolynomial.DIPSUM(a,b);

     assertEquals("a+b = b+a",c,d);
     assertTrue("a+b = b+a", c.equals(d) );

     c = IntSolvableOrderedMapPolynomial.DIIRAS(table, rl, kl, ll, el, q );
     d = IntSolvableOrderedMapPolynomial.DIPSUM(a,IntSolvableOrderedMapPolynomial.DIPSUM(b,c));
     e = IntSolvableOrderedMapPolynomial.DIPSUM(IntSolvableOrderedMapPolynomial.DIPSUM(a,b),c);

     assertEquals("a+(b+c) = (a+b)+c",d,e);
     assertTrue("a+(b+c) = (a+b)+c", d.equals(e) );

     ExpVector u = ExpVector.EVRAND(rl,el,q);
     BigInteger x = BigInteger.IRAND(kl);

     b = new IntSolvableOrderedMapPolynomial(table, x, u);
     c = a.add(b);
     d = a.add(x,u);
     assertEquals("a+p(x,u) = a+(x,u)",c,d);

     c = a.subtract(b);
     d = a.subtract(x,u);
     assertEquals("a-p(x,u) = a-(x,u)",c,d);

     a = new IntSolvableOrderedMapPolynomial(table);
     b = new IntSolvableOrderedMapPolynomial(table, x, u);
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

     a = IntSolvableOrderedMapPolynomial.DIIRAS(table, rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );
     //a = IntSolvableOrderedMapPolynomial.DIIRAS(1, kl, 4, el, q );

     b = IntSolvableOrderedMapPolynomial.DIIRAS(table, rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );

     c = IntSolvableOrderedMapPolynomial.DIPPR(b,a);
     d = IntSolvableOrderedMapPolynomial.DIPPR(a,b);
     assertTrue("not isZERO( c )", !c.isZERO() );
     assertTrue("not isZERO( d )", !d.isZERO() );

     e = IntSolvableOrderedMapPolynomial.DIPDIF(d,c);
     assertTrue("isZERO( a*b-b*a ) " + e, e.isZERO() );

     assertEquals("a*b = b*a",c,d);
     assertTrue("a*b = b*a", c.equals(d) );

     c = IntSolvableOrderedMapPolynomial.DIIRAS(table, rl, kl, ll, el, q );
     d = IntSolvableOrderedMapPolynomial.DIPPR(a,IntSolvableOrderedMapPolynomial.DIPPR(b,c));
     e = IntSolvableOrderedMapPolynomial.DIPPR(IntSolvableOrderedMapPolynomial.DIPPR(a,b),c);

     assertEquals("a(bc) = (ab)c",d,e);
     assertTrue("a(bc) = (ab)c", d.equals(e) );

 }


/**
 * Test object multiplication
 * 
 */
 public void testMultiplication1() {

     a = IntSolvableOrderedMapPolynomial.DIIRAS(table, rl, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );
     //a = IntSolvableOrderedMapPolynomial.DIIRAS(1, kl, 4, el, q );

     b = IntSolvableOrderedMapPolynomial.DIIRAS(table, rl, kl, ll, el, q );
     assertTrue("not isZERO( b )", !b.isZERO() );

     c = b.multiply(a);
     d = a.multiply(b);
     assertTrue("not isZERO( c )", !c.isZERO() );
     assertTrue("not isZERO( d )", !d.isZERO() );

     e = d.subtract(c);
     assertTrue("isZERO( a*b-b*a ) " + e, e.isZERO() );

     assertEquals("a*b = b*a",c,d);
     assertTrue("a*b = b*a", c.equals(d) );

     c = IntSolvableOrderedMapPolynomial.DIIRAS(table, rl, kl, ll, el, q );
     d = a.multiply( b.multiply(c) );
     e = (a.multiply(b)).multiply(c);

     assertEquals("a(bc) = (ab)c",d,e);
     assertTrue("a(bc) = (ab)c", d.equals(e) );

 }


/**
 * Test Weyl polynomials
 * 
 */
 public void testWeyl() {

     WeylRelations wl = new WeylRelations();
     int rloc = 4;
     dummy = IntSolvableOrderedMapPolynomial.DIIRAS(table/*null*/,rloc,kl,ll,el,q);
     RelationTable table = wl.generate(rloc,dummy);
     dummy = null;

     a = IntSolvableOrderedMapPolynomial.DIIRAS(table, rloc, kl, ll, el, q );
     assertTrue("not isZERO( a )", !a.isZERO() );

     b = IntSolvableOrderedMapPolynomial.DIIRAS(table, rloc, kl, ll, el, q );
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

     c = IntSolvableOrderedMapPolynomial.DIIRAS(table, rloc, kl, ll, el, q );
     // associative
     d = a.multiply( b.multiply(c) );
     e = (a.multiply(b)).multiply(c);
     //System.out.println("d = " + d);
     //System.out.println("e = " + e);

     assertEquals("a(bc) = (ab)c",d,e);
     assertTrue("a(bc) = (ab)c", d.equals(e) );
 }

}
