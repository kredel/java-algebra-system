/*
 * $Id$
 */

package edu.jas.application;

import java.util.List;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.BigRational;
import edu.jas.arith.BigComplex;

import edu.jas.poly.TermOrder;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;

import edu.jas.structure.Product;
import edu.jas.structure.ProductRing;
import edu.jas.structure.RingElem;

import edu.jas.application.PolyUtilApp;


/**
 * PolyUtilApp tests with JUnit.
 * @author Heinz Kredel.
 */

public class PolyUtilAppTest extends TestCase {

/**
 * main.
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>PolyUtilAppTest</CODE> object.
 * @param name String.
 */
   public PolyUtilAppTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(PolyUtilAppTest.class);
     return suite;
   }

   //private final static int bitlen = 100;

   TermOrder to = new TermOrder( TermOrder.INVLEX );

   GenPolynomialRing<BigRational> dfac;
   GenPolynomialRing<BigRational> cfac;
   GenPolynomialRing<GenPolynomial<BigRational>> rfac;

   BigRational ai;
   BigRational bi;
   BigRational ci;
   BigRational di;
   BigRational ei;

   GenPolynomial<BigRational> a;
   GenPolynomial<BigRational> b;
   GenPolynomial<BigRational> c;
   GenPolynomial<BigRational> d;
   GenPolynomial<BigRational> e;

   GenPolynomial<GenPolynomial<BigRational>> ar;
   GenPolynomial<GenPolynomial<BigRational>> br;
   GenPolynomial<GenPolynomial<BigRational>> cr;
   GenPolynomial<GenPolynomial<BigRational>> dr;
   GenPolynomial<GenPolynomial<BigRational>> er;

   int rl = 5; 
   int kl = 5;
   int ll = 5;
   int el = 5;
   float q = 0.6f;

   protected void setUp() {
       a = b = c = d = e = null;
       ai = bi = ci = di = ei = null;
       ar = br = cr = dr = er = null;
       dfac = new GenPolynomialRing<BigRational>(new BigRational(1),rl,to);
       cfac = new GenPolynomialRing<BigRational>(new BigRational(1),rl-1,to);
       rfac = new GenPolynomialRing<GenPolynomial<BigRational>>(cfac,1,to);
   }

   protected void tearDown() {
       a = b = c = d = e = null;
       ai = bi = ci = di = ei = null;
       ar = br = cr = dr = er = null;
       dfac = null;
       cfac = null;
       rfac = null;
   }


/**
 * Test Product represenation conversion, rational numbers.
 * 
 */
 public void xtestProductConversionRN() {
     GenPolynomialRing<BigRational> ufac;
     ufac = new GenPolynomialRing<BigRational>(new BigRational(1),1);

     ProductRing<GenPolynomial<BigRational>> pfac;
     pfac = new ProductRing<GenPolynomial<BigRational>>( ufac, rl );

     Product<GenPolynomial<BigRational>> cp;

     c = dfac.getONE();
     //System.out.println("c = " + c);

     cp = PolyUtilApp.toProduct(pfac,c);
     //System.out.println("cp = " + cp);
     assertTrue("isZERO( cp )", cp.isZERO() );

     c = dfac.random(kl,ll,el,q);
     //System.out.println("c = " + c);

     cp = PolyUtilApp.toProduct(pfac,c);
     //System.out.println("cp = " + cp);
     assertTrue("!isONE( cp )", !cp.isONE() );
 }


/**
 * Test Product represenation conversion, algebraic numbers.
 * 
 */
 public void xtestProductConversionAN() {
     GenPolynomialRing<BigRational> ufac;
     ufac = new GenPolynomialRing<BigRational>(new BigRational(1),1);

     GenPolynomial<BigRational> m;
     m = ufac.univariate(0,2);
     m = m.subtract( ufac.univariate(0,1) );
     //System.out.println("m = " + m);

     AlgebraicNumberRing<BigRational> afac;
     afac = new AlgebraicNumberRing<BigRational>(m);
     //System.out.println("afac = " + afac);

     ProductRing<AlgebraicNumber<BigRational>> pfac;
     pfac = new ProductRing<AlgebraicNumber<BigRational>>( afac, rl );

     Product<AlgebraicNumber<BigRational>> cp;

     c = dfac.getONE();
     //System.out.println("c = " + c);

     cp = PolyUtilApp.toANProduct(pfac,c);
     //System.out.println("cp = " + cp);
     assertTrue("isZERO( cp )", cp.isZERO() );
     
     c = dfac.random(kl,ll,el,q);
     //System.out.println("c = " + c);

     cp = PolyUtilApp.toANProduct(pfac,c);
     //System.out.println("cp = " + cp);
     assertTrue("!isONE( cp )", !cp.isONE() );
 }


/**
 * Test Product represenation conversion, algebraic numbers, coefficients.
 * 
 */
 public void testProductConversionANCoeff() {
     GenPolynomialRing<BigRational> ufac;
     ufac = new GenPolynomialRing<BigRational>(new BigRational(1),1);

     GenPolynomial<BigRational> m;
     m = ufac.univariate(0,2);
     m = m.subtract( ufac.univariate(0,1) );
     //System.out.println("m = " + m);

     AlgebraicNumberRing<BigRational> afac;
     afac = new AlgebraicNumberRing<BigRational>(m);
     //System.out.println("afac = " + afac);

     ProductRing<AlgebraicNumber<BigRational>> pfac;
     pfac = new ProductRing<AlgebraicNumber<BigRational>>( afac, rl );
     //System.out.println("pfac = " + pfac);

     GenPolynomialRing<Product<AlgebraicNumber<BigRational>>> fac 
         = new GenPolynomialRing<Product<AlgebraicNumber<BigRational>>>(pfac,4); 
     //System.out.println("fac = " + fac);


     rfac = new GenPolynomialRing<GenPolynomial<BigRational>>(dfac,4);

     GenPolynomial<Product<AlgebraicNumber<BigRational>>> cp;
     // Product<AlgebraicNumber<BigRational>> cp;

     cr = rfac.getONE();
     //System.out.println("cr = " + cr);

     cp = PolyUtilApp.toANProductCoeff(fac,cr);
     //System.out.println("cp = " + cp);
     assertTrue("isZERO( cp )", cp.isZERO() );
     
     cr = rfac.random(kl,ll,el,q);
     //System.out.println("cr = " + cr);

     cp = PolyUtilApp.toANProductCoeff(fac,cr);
     //System.out.println("cp = " + cp);
     assertTrue("!isONE( cp )", !cp.isONE() );

     br = rfac.random(kl,ll,el,q);
     //System.out.println("br = " + br);

     List<GenPolynomial<GenPolynomial<BigRational>>> list
         = new ArrayList<GenPolynomial<GenPolynomial<BigRational>>>();
     list.add(cr);
     list.add(br);

     List<GenPolynomial<Product<AlgebraicNumber<BigRational>>>> res 
         = new ArrayList<GenPolynomial<Product<AlgebraicNumber<BigRational>>>>(); 
     //System.out.println("list = " + list);

     res = PolyUtilApp.toANProductCoeff(fac,list);
     //System.out.println("res = " + res);
 }

}
