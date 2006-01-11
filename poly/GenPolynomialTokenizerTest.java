/*
 * $Id$
 */

package edu.jas.poly;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import java.util.List;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;

import edu.jas.arith.BigRational;
import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.arith.BigComplex;
import edu.jas.arith.BigQuaternion;

import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;

import edu.jas.poly.PolynomialList;

import edu.jas.module.ModuleList;



/**
 * GenPolynomialTokenizer Test using JUnit.
 * @author Heinz Kredel
 */

public class GenPolynomialTokenizerTest extends TestCase {

/**
 * main.
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>GenPolynomialTokenizerTest</CODE> object.
 * @param name String.
 */
   public GenPolynomialTokenizerTest(String name) {
          super(name);
   }

/**
 * suite.
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(GenPolynomialTokenizerTest.class);
     return suite;
   }

   RingFactory fac; // unused
   GenPolynomialRing pfac;
   GenSolvablePolynomialRing spfac;
   GenPolynomialTokenizer parser;
   Reader source;


   protected void setUp() {
       fac = null;
       pfac = null;
       parser = null;
       source = null;
   }

   protected void tearDown() {
       fac = null;
       pfac = null;
       parser = null;
       source = null;
   }


/**
 * Test rational polynomial.
 * 
 */
 public void testBigRational() {
     String exam = "Rat(x,y,z) L "  
                 + "( "
                 + "( 1 ), "
                 + "( 0 ), "
                 + "( 3/4 - 6/8 ), "
                 + "( 1 x + x^3 + 1/3 y z - x^3 ) "
                 + " )";
     source = new StringReader( exam );
     parser = new GenPolynomialTokenizer( source );
     PolynomialList f = null;
     try {
         f = parser.nextPolynomialSet();
     } catch(IOException e) {
         fail(""+e);
     }
     //System.out.println("f = " + f);
     assertTrue("f != null", f.list != null);
     assertTrue("length( f ) = 4", f.list.size() == 4);

     BigRational fac = new BigRational(0);
     TermOrder tord = new TermOrder(TermOrder.INVLEX);
     String[] vars = new String[]{ "x", "y", "z" };
     int nvar = vars.length;
     pfac = new GenPolynomialRing<BigRational>(fac,nvar,tord,vars);
     assertEquals("pfac == f.ring", pfac, f.ring );


     GenPolynomial a = (GenPolynomial)f.list.get(0);
     //System.out.println("a = " + a);
     assertTrue("isONE( f.get(0) )", a.isONE() );

     GenPolynomial b = (GenPolynomial)f.list.get(1);
     //System.out.println("b = " + b);
     assertTrue("isZERO( f.get(1) )", b.isZERO() );

     GenPolynomial c = (GenPolynomial)f.list.get(2);
     //System.out.println("c = " + c);
     assertTrue("isZERO( f.get(2) )", c.isZERO() );

     GenPolynomial d = (GenPolynomial)f.list.get(3);
     //System.out.println("d = " + d);
     assertEquals("f.get(3).length() == 2", 2, d.length() );
 }


/**
 * Test integer polynomial.
 * 
 */
 public void testBigInteger() {
     String exam = "Int(x,y,z) L "  
                 + "( "
                 + "( 1 ), "
                 + "( 0 ), "
                 + "( 3 2 - 6 ), "
                 + "( 1 x + x^3 + 3 y z - x^3 ) "
                 + " )";
     source = new StringReader( exam );
     parser = new GenPolynomialTokenizer( source );
     PolynomialList f = null;
     try {
         f = parser.nextPolynomialSet();
     } catch(IOException e) {
         fail(""+e);
     }
     //System.out.println("f = " + f);
     assertTrue("f != null", f.list != null);
     assertTrue("length( f ) = 4", f.list.size() == 4);

     BigInteger fac = new BigInteger(0);
     TermOrder tord = new TermOrder(TermOrder.INVLEX);
     String[] vars = new String[]{ "x", "y", "z" };
     int nvar = vars.length;
     pfac = new GenPolynomialRing<BigInteger>(fac,nvar,tord,vars);
     assertEquals("pfac == f.ring", pfac, f.ring );


     GenPolynomial a = (GenPolynomial)f.list.get(0);
     //System.out.println("a = " + a);
     assertTrue("isONE( f.get(0) )", a.isONE() );

     GenPolynomial b = (GenPolynomial)f.list.get(1);
     //System.out.println("b = " + b);
     assertTrue("isZERO( f.get(1) )", b.isZERO() );

     GenPolynomial c = (GenPolynomial)f.list.get(2);
     //System.out.println("c = " + c);
     assertTrue("isZERO( f.get(2) )", c.isZERO() );

     GenPolynomial d = (GenPolynomial)f.list.get(3);
     //System.out.println("d = " + d);
     assertEquals("f.get(3).length() == 2", 2, d.length() );
 }


/**
 * Test modular integer polynomial.
 * 
 */
 public void testModInteger() {
     String exam = "Mod 19 (x,y,z) L "  
                 + "( "
                 + "( 1 ), "
                 + "( 0 ), "
                 + "( 3 2 - 6 + 19 ), "
                 + "( 1 x + x^3 + 3 y z - x^3 ) "
                 + " )";
     source = new StringReader( exam );
     parser = new GenPolynomialTokenizer( source );
     PolynomialList f = null;
     try {
         f = parser.nextPolynomialSet();
     } catch(IOException e) {
         fail(""+e);
     }
     //System.out.println("f = " + f);
     assertTrue("f != null", f.list != null);
     assertTrue("length( f ) = 4", f.list.size() == 4);

     ModInteger fac = new ModInteger(19,0);
     TermOrder tord = new TermOrder(TermOrder.INVLEX);
     String[] vars = new String[]{ "x", "y", "z" };
     int nvar = vars.length;
     pfac = new GenPolynomialRing<ModInteger>(fac,nvar,tord,vars);
     assertEquals("pfac == f.ring", pfac, f.ring );

     GenPolynomial a = (GenPolynomial)f.list.get(0);
     //System.out.println("a = " + a);
     assertTrue("isONE( f.get(0) )", a.isONE() );

     GenPolynomial b = (GenPolynomial)f.list.get(1);
     //System.out.println("b = " + b);
     assertTrue("isZERO( f.get(1) )", b.isZERO() );

     GenPolynomial c = (GenPolynomial)f.list.get(2);
     //System.out.println("c = " + c);
     assertTrue("isZERO( f.get(2) )", c.isZERO() );

     GenPolynomial d = (GenPolynomial)f.list.get(3);
     //System.out.println("d = " + d);
     assertEquals("f.get(3).length() == 2", 2, d.length() );
 }


/**
 * Test complex polynomial.
 * 
 */
 public void testBigComplex() {
     String exam = "Complex(x,y,z) L "  
                 + "( "
                 + "( 1i0 ), "
                 + "( 0i0 ), "
                 + "( 3/4i2 - 6/8i2 ), "
                 + "( 1i0 x + x^3 + 1i3 y z - x^3 ) "
                 + " )";
     source = new StringReader( exam );
     parser = new GenPolynomialTokenizer( source );
     PolynomialList f = null;
     try {
         f = parser.nextPolynomialSet();
     } catch(IOException e) {
         fail(""+e);
     }
     //System.out.println("f = " + f);
     assertTrue("f != null", f.list != null);
     assertTrue("length( f ) = 4", f.list.size() == 4);

     BigComplex fac = new BigComplex(0);
     TermOrder tord = new TermOrder(TermOrder.INVLEX);
     String[] vars = new String[]{ "x", "y", "z" };
     int nvar = vars.length;
     pfac = new GenPolynomialRing<BigComplex>(fac,nvar,tord,vars);
     assertEquals("pfac == f.ring", pfac, f.ring );


     GenPolynomial a = (GenPolynomial)f.list.get(0);
     //System.out.println("a = " + a);
     assertTrue("isONE( f.get(0) )", a.isONE() );

     GenPolynomial b = (GenPolynomial)f.list.get(1);
     //System.out.println("b = " + b);
     assertTrue("isZERO( f.get(1) )", b.isZERO() );

     GenPolynomial c = (GenPolynomial)f.list.get(2);
     //System.out.println("c = " + c);
     assertTrue("isZERO( f.get(2) )", c.isZERO() );

     GenPolynomial d = (GenPolynomial)f.list.get(3);
     //System.out.println("d = " + d);
     assertEquals("f.get(3).length() == 2", 2, d.length() );
 }


/**
 * Test quaternion polynomial.
 * 
 */
 public void testBigQuaternion() {
     String exam = "Quat(x,y,z) L "  
                 + "( "
                 + "( 1i0j0k0 ), "
                 + "( 0i0j0k0 ), "
                 + "( 3/4i2j1k3 - 6/8i2j1k3 ), "
                 + "( 1 x + x^3 + 1i2j3k4 y z - x^3 ) "
                 + " )";
     source = new StringReader( exam );
     parser = new GenPolynomialTokenizer( source );
     PolynomialList f = null;
     try {
         f = parser.nextPolynomialSet();
     } catch(IOException e) {
         fail(""+e);
     }
     //System.out.println("f = " + f);
     assertTrue("f != null", f.list != null);
     assertTrue("length( f ) = 4", f.list.size() == 4);

     BigQuaternion fac = new BigQuaternion(0);
     TermOrder tord = new TermOrder(TermOrder.INVLEX);
     String[] vars = new String[]{ "x", "y", "z" };
     int nvar = vars.length;
     pfac = new GenPolynomialRing<BigQuaternion>(fac,nvar,tord,vars);
     assertEquals("pfac == f.ring", pfac, f.ring );


     GenPolynomial a = (GenPolynomial)f.list.get(0);
     //System.out.println("a = " + a);
     assertTrue("isONE( f.get(0) )", a.isONE() );

     GenPolynomial b = (GenPolynomial)f.list.get(1);
     //System.out.println("b = " + b);
     assertTrue("isZERO( f.get(1) )", b.isZERO() );

     GenPolynomial c = (GenPolynomial)f.list.get(2);
     //System.out.println("c = " + c);
     assertTrue("isZERO( f.get(2) )", c.isZERO() );

     GenPolynomial d = (GenPolynomial)f.list.get(3);
     //System.out.println("d = " + d);
     assertEquals("f.get(3).length() == 2", 2, d.length() );
 }


/**
 * Test rational solvable polynomial.
 * 
 */
 public void testSolvableBigRational() {
     String exam = "Rat(x,y,z) L "  
                 + "RelationTable "
                 + "( "
                 + " ( z ), ( y ), ( y z -1 ) "
                 + ") "
                 + "( "
                 + " ( 1 ), "
                 + " ( 0 ), "
                 + " ( 3/4 - 6/8 ), "
                 + " ( 1 x + x^3 + 1/3 y z - x^3 ) "
                 + " )";
     source = new StringReader( exam );
     parser = new GenPolynomialTokenizer( source );
     PolynomialList f = null;
     try {
         f = parser.nextSolvablePolynomialSet();
     } catch(IOException e) {
         fail(""+e);
     }
     //System.out.println("f = " + f);
     //System.out.println("f.ring.table = " + ((GenSolvablePolynomialRing)f.ring).table);
     assertTrue("f != null", f.list != null);
     assertTrue("length( f ) = 4", f.list.size() == 4);

     BigRational fac = new BigRational(0);
     TermOrder tord = new TermOrder(TermOrder.INVLEX);
     String[] vars = new String[]{ "x", "y", "z" };
     int nvar = vars.length;
     spfac = new GenSolvablePolynomialRing<BigRational>(fac,nvar,tord,vars);
     assertEquals("spfac == f.ring", spfac, f.ring );
     //System.out.println("spfac = " + spfac);
     //System.out.println("spfac.table = " + spfac.table);


     GenSolvablePolynomial a = (GenSolvablePolynomial)f.list.get(0);
     //System.out.println("a = " + a);
     assertTrue("isZERO( f.get(0) )", a.isZERO() );

     GenSolvablePolynomial b = (GenSolvablePolynomial)f.list.get(1);
     //System.out.println("b = " + b);
     assertTrue("isZERO( f.get(1) )", b.isZERO() );

     GenSolvablePolynomial c = (GenSolvablePolynomial)f.list.get(2);
     //System.out.println("c = " + c);
     assertTrue("isONE( f.get(2) )", c.isONE() );

     GenSolvablePolynomial d = (GenSolvablePolynomial)f.list.get(3);
     //System.out.println("d = " + d);
     assertEquals("f.get(3).length() == 2", 2, d.length() );
 }


/**
 * Test mod integer solvable polynomial.
 * 
 */
 public void testSolvableModInteger() {
     String exam = "Mod 19 (x,y,z) L "  
                 + "RelationTable "
                 + "( "
                 + " ( z ), ( y ), ( y z -1 ) "
                 + ") "
                 + "( "
                 + "( 1 ), "
                 + "( 0 ), "
                 + "( 3 2 - 6 + 19 ), "
                 + "( 1 x + x^3 + 3 y z - x^3 ) "
                 + " )";
     source = new StringReader( exam );
     parser = new GenPolynomialTokenizer( source );
     PolynomialList f = null;
     try {
         f = parser.nextSolvablePolynomialSet();
     } catch(IOException e) {
         fail(""+e);
     }
     //System.out.println("f = " + f);
     //System.out.println("f.ring.table = " + ((GenSolvablePolynomialRing)f.ring).table);
     assertTrue("f != null", f.list != null);
     assertTrue("length( f ) = 4", f.list.size() == 4);

     ModInteger fac = new ModInteger(19,0);
     TermOrder tord = new TermOrder(TermOrder.INVLEX);
     String[] vars = new String[]{ "x", "y", "z" };
     int nvar = vars.length;
     spfac = new GenSolvablePolynomialRing<ModInteger>(fac,nvar,tord,vars);
     assertEquals("spfac == f.ring", spfac, f.ring );
     //System.out.println("spfac = " + spfac);
     //System.out.println("spfac.table = " + spfac.table);


     GenSolvablePolynomial a = (GenSolvablePolynomial)f.list.get(0);
     //System.out.println("a = " + a);
     assertTrue("isZERO( f.get(0) )", a.isZERO() );

     GenSolvablePolynomial b = (GenSolvablePolynomial)f.list.get(1);
     //System.out.println("b = " + b);
     assertTrue("isZERO( f.get(1) )", b.isZERO() );

     GenSolvablePolynomial c = (GenSolvablePolynomial)f.list.get(2);
     //System.out.println("c = " + c);
     assertTrue("isONE( f.get(2) )", c.isONE() );

     GenSolvablePolynomial d = (GenSolvablePolynomial)f.list.get(3);
     //System.out.println("d = " + d);
     assertEquals("f.get(3).length() == 2", 2, d.length() );
 }


/**
 * Test integer polynomial module.
 * 
 */
 public void testBigIntegerModule() {
     String exam = "Int(x,y,z) L "  
                 + "( "
                 + " ( "
                 + "  ( 1 ), "
                 + "  ( 0 ), "
                 + "  ( 3 2 - 6 ), "
                 + "  ( 1 x + x^3 + 3 y z - x^3 ) "
                 + " ), "
                 + " ( ( 1 ), ( 0 ) ) "
                 + ")";
     source = new StringReader( exam );
     parser = new GenPolynomialTokenizer( source );
     ModuleList m = null;
     try {
         m = parser.nextSubModuleSet();
     } catch(IOException e) {
         fail(""+e);
     }
     //System.out.println("m = " + m);
     assertTrue("m != null", m.list != null);
     assertTrue("length( m ) = 2", m.list.size() == 2);
     assertTrue("length( m[0] ) = 4", ((List)m.list.get(0)).size() == 4);


     BigInteger fac = new BigInteger(0);
     TermOrder tord = new TermOrder(TermOrder.INVLEX);
     String[] vars = new String[]{ "x", "y", "z" };
     int nvar = vars.length;
     pfac = new GenPolynomialRing<BigInteger>(fac,nvar,tord,vars);
     assertEquals("pfac == m.ring", pfac, m.ring );

     List<List<GenPolynomial>> rows = m.list;
     List<GenPolynomial> f;

     f = rows.get(0);
     GenPolynomial a = (GenPolynomial)f.get(0);
     //System.out.println("a = " + a);
     assertTrue("isONE( f.get(0) )", a.isONE() );

     GenPolynomial b = (GenPolynomial)f.get(1);
     //System.out.println("b = " + b);
     assertTrue("isZERO( f.get(1) )", b.isZERO() );

     GenPolynomial c = (GenPolynomial)f.get(2);
     //System.out.println("c = " + c);
     assertTrue("isZERO( f.get(2) )", c.isZERO() );

     GenPolynomial d = (GenPolynomial)f.get(3);
     //System.out.println("d = " + d);
     assertEquals("f.get(3).length() == 2", 2, d.length() );

     f = rows.get(1);
     assertTrue("length( f ) = 4", f.size() == 4);

     a = (GenPolynomial)f.get(0);
     //System.out.println("a = " + a);
     assertTrue("isONE( f.get(0) )", a.isONE() );

     b = (GenPolynomial)f.get(1);
     //System.out.println("b = " + b);
     assertTrue("isZERO( f.get(1) )", b.isZERO() );

     c = (GenPolynomial)f.get(2);
     //System.out.println("c = " + c);
     assertTrue("isZERO( f.get(2) )", c.isZERO() );

     d = (GenPolynomial)f.get(3);
     //System.out.println("c = " + d);
     assertTrue("isZERO( f.get(3) )", d.isZERO() );
 }


/**
 * Test rational solvable polynomial module.
 * 
 */
 public void testBigRationalSolvableModule() {
     String exam = "Rat(x,y,z) L "  
                 + "RelationTable "
                 + "( "
                 + " ( z ), ( y ), ( y z -1 ) "
                 + ") "
                 + "( "
                 + " ( "
                 + "  ( 1 ), "
                 + "  ( 0 ), "
                 + "  ( 3/4 - 6/8 ), "
                 + "  ( 1 x + x^3 + 1/3 y z - x^3 ) "
                 + " ), "
                 + " ( ( x ), ( 1 ), ( 0 ) ) "
                 + " )";
     source = new StringReader( exam );
     parser = new GenPolynomialTokenizer( source );
     ModuleList m = null;
     try {
         m = parser.nextSolvableSubModuleSet();
     } catch(IOException e) {
         fail(""+e);
     }
     //System.out.println("m = " + m);
     //System.out.println("m.ring = " + m.ring);
     assertTrue("m != null", m.list != null);
     assertTrue("length( m ) = 2", m.list.size() == 2);
     assertTrue("length( m[0] ) = 4", ((List)m.list.get(0)).size() == 4);

     BigRational fac = new BigRational(0);
     TermOrder tord = new TermOrder(TermOrder.INVLEX);
     String[] vars = new String[]{ "x", "y", "z" };
     int nvar = vars.length;
     spfac = new GenSolvablePolynomialRing<BigRational>(fac,nvar,tord,vars);
     assertEquals("spfac == m.ring", spfac, m.ring );

     List<List<GenSolvablePolynomial>> rows = m.list;
     List<GenSolvablePolynomial> f;

     f = rows.get(0);
     GenSolvablePolynomial a = (GenSolvablePolynomial)f.get(0);
     //System.out.println("a = " + a);
     assertTrue("isONE( f.get(0) )", a.isONE() );

     GenSolvablePolynomial b = (GenSolvablePolynomial)f.get(1);
     //System.out.println("b = " + b);
     assertTrue("isZERO( f.get(1) )", b.isZERO() );

     GenSolvablePolynomial c = (GenSolvablePolynomial)f.get(2);
     //System.out.println("c = " + c);
     assertTrue("isZERO( f.get(2) )", c.isZERO() );

     GenSolvablePolynomial d = (GenSolvablePolynomial)f.get(3);
     //System.out.println("d = " + d);
     assertEquals("f.get(3).length() == 2", 2, d.length() );

     f = rows.get(1);
     assertTrue("length( f ) = 4", f.size() == 4);

     a = (GenSolvablePolynomial)f.get(0);
     //System.out.println("a = " + a);
     assertTrue("!isONE( f.get(0) )", !a.isONE() );

     b = (GenSolvablePolynomial)f.get(1);
     //System.out.println("b = " + b);
     assertTrue("isONE( f.get(1) )", b.isONE() );

     c = (GenSolvablePolynomial)f.get(2);
     //System.out.println("c = " + c);
     assertTrue("isZERO( f.get(2) )", c.isZERO() );

     d = (GenSolvablePolynomial)f.get(3);
     //System.out.println("d = " + d);
     assertTrue("isZERO( f.get(3) )", d.isZERO() );

 }


/**
 * Test algebraic number polynomial.
 * 
 */
 public void testAlgebraicNumber() {
     String exam = "AN[ (i) ( i^2 + 1 ) ] (x,y,z) L "  
                 + "( "
                 + "( 1 ), "
                 + "( _i_ ), "
                 + "( 0 ), "
                 + "( _i^2_ + 1 ), "
                 + "( 1 x + x^3 + _3 i_ y z - x^3 ) "
                 + " )";
     source = new StringReader( exam );
     parser = new GenPolynomialTokenizer( source );
     PolynomialList f = null;
     try {
         f = parser.nextPolynomialSet();
     } catch(IOException e) {
         fail(""+e);
     }
     //System.out.println("f = " + f);
     assertTrue("f != null", f.list != null);
     assertTrue("length( f ) = 5", f.list.size() == 5);

     AlgebraicNumber<BigRational> fac = (AlgebraicNumber<BigRational>)f.ring.coFac;
     TermOrder tord = new TermOrder(TermOrder.INVLEX);
     String[] vars = new String[]{ "x", "y", "z" };
     int nvar = vars.length;
     pfac = new GenPolynomialRing<AlgebraicNumber<BigRational>>(fac,nvar,tord,vars);
     assertEquals("pfac == f.ring", pfac, f.ring );

     GenPolynomial a = (GenPolynomial)f.list.get(0);
     //System.out.println("a = " + a);
     assertTrue("isONE( f.get(0) )", a.isONE() );

     GenPolynomial b = (GenPolynomial)f.list.get(1);
     //System.out.println("b = " + b);
     assertTrue("isUnit( f.get(1) )", b.isUnit() );

     b = b.monic();
     //System.out.println("b = " + b);
     assertTrue("isUnit( f.get(1) )", b.isONE() );

     GenPolynomial c = (GenPolynomial)f.list.get(2);
     //System.out.println("c = " + c);
     assertTrue("isZERO( f.get(1) )", c.isZERO() );

     GenPolynomial d = (GenPolynomial)f.list.get(3);
     //System.out.println("d = " + d);
     assertTrue("isZERO( f.get(2) )", d.isZERO() );

     GenPolynomial e = (GenPolynomial)f.list.get(4);
     //System.out.println("e = " + e);
     assertEquals("f.get(3).length() == 2", 2, e.length() );
 }


/**
 * Test Galois field coefficient polynomial.
 * 
 */
 public void testGaloisField() {
     String exam = "AN[ 19 (i) ( i^2 + 1 ) ] (x,y,z) L "  
                 + "( "
                 + "( 20 ), "
                 + "( _i_ ), "
                 + "( 0 ), "
                 + "( _i^2_ + 20 ), "
                 + "( 1 x + x^3 + _3 i_ y z - x^3 ) "
                 + " )";
     source = new StringReader( exam );
     parser = new GenPolynomialTokenizer( source );
     PolynomialList f = null;
     try {
         f = parser.nextPolynomialSet();
     } catch(IOException e) {
         fail(""+e);
     }
     //System.out.println("f = " + f);
     assertTrue("f != null", f.list != null);
     assertTrue("length( f ) = 5", f.list.size() == 5);

     AlgebraicNumber<ModInteger> fac = (AlgebraicNumber<ModInteger>)f.ring.coFac;
     TermOrder tord = new TermOrder(TermOrder.INVLEX);
     String[] vars = new String[]{ "x", "y", "z" };
     int nvar = vars.length;
     pfac = new GenPolynomialRing<AlgebraicNumber<ModInteger>>(fac,nvar,tord,vars);
     assertEquals("pfac == f.ring", pfac, f.ring );

     GenPolynomial a = (GenPolynomial)f.list.get(0);
     //System.out.println("a = " + a);
     assertTrue("isONE( f.get(0) )", a.isONE() );

     GenPolynomial b = (GenPolynomial)f.list.get(1);
     //System.out.println("b = " + b);
     assertTrue("isUnit( f.get(1) )", b.isUnit() );

     b = b.monic();
     //System.out.println("b = " + b);
     assertTrue("isUnit( f.get(1) )", b.isONE() );

     GenPolynomial c = (GenPolynomial)f.list.get(2);
     //System.out.println("c = " + c);
     assertTrue("isZERO( f.get(1) )", c.isZERO() );

     GenPolynomial d = (GenPolynomial)f.list.get(3);
     //System.out.println("d = " + d);
     assertTrue("isZERO( f.get(2) )", d.isZERO() );

     GenPolynomial e = (GenPolynomial)f.list.get(4);
     //System.out.println("e = " + e);
     assertEquals("f.get(3).length() == 2", 2, e.length() );
 }

}
