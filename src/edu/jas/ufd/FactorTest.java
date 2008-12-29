/*
 * $Id$
 */

package edu.jas.ufd;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.SortedMap;

import edu.jas.kern.ComputerThreads;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.BigRational;
import edu.jas.arith.BigComplex;

import edu.jas.poly.TermOrder;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.ExpVector;


/**
 * Factor tests with JUnit.
 * @author Heinz Kredel.
 */

public class FactorTest extends TestCase {

/**
 * main.
 */
   public static void main (String[] args) {
          junit.textui.TestRunner.run( suite() );
   }

/**
 * Constructs a <CODE>PolyUtilTest</CODE> object.
 * @param name String.
 */
   public FactorTest(String name) {
          super(name);
   }

/**
 */ 
 public static Test suite() {
     TestSuite suite= new TestSuite(FactorTest.class);
     return suite;
   }

   int rl = 3; 
   int kl = 5;
   int ll = 5;
   int el = 3;
   float q = 0.3f;

   protected void setUp() {
   }

   protected void tearDown() {
   }


/**
 * Test modular factorization.
 * 
 */
 public void testModularFactorization() {

     TermOrder to = new TermOrder(TermOrder.INVLEX);
     ModIntegerRing cfac = new ModIntegerRing(13);
     GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(cfac,1,to);
     FactorModular fac = new FactorModular();

     for ( int i = 0; i < 8; i++ ) {
         GenPolynomial<ModInteger> a = pfac.random(kl,ll*(i+1),el*(i+1),q);
         if ( a.isConstant() ) {
             continue;
         }
         System.out.println("\na = " + a);

         SortedMap<GenPolynomial<ModInteger>,Integer> sm = fac.baseFactors( a );
         System.out.println("sm = " + sm);

         boolean t = fac.isFactorization( a, sm );
         //System.out.println("t        = " + t);
         assertTrue("prod(factor)) = a",t);
     }
 }


/**
 * Test multivariate modular factorization.
 * 
 */
 public void testMultivariateModularFactorization() {

     TermOrder to = new TermOrder(TermOrder.INVLEX);
     ModIntegerRing cfac = new ModIntegerRing(13);
     GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(cfac,rl,to);
     FactorModular fac = new FactorModular();

     for ( int i = 0; i < 6; i++ ) {
         GenPolynomial<ModInteger> a = pfac.random(kl,ll*(i+1),el,q);
         if ( a.isConstant() ) {
             continue;
         }
         System.out.println("\na = " + a);

         SortedMap<GenPolynomial<ModInteger>,Integer> sm = fac.factors( a );
         System.out.println("sm = " + sm);

         boolean t = fac.isFactorization( a, sm );
         //System.out.println("t        = " + t);
         assertTrue("prod(factor)) = a",t);
     }
     ComputerThreads.terminate();
 }


/**
 * Test integer factorization.
 * 
 */
 public void testIntegerFactorization() {

     TermOrder to = new TermOrder(TermOrder.INVLEX);
     BigInteger cfac = new BigInteger(1);
     GenPolynomialRing<BigInteger> pfac = new GenPolynomialRing<BigInteger>(cfac,1,to);
     FactorInteger fac = new FactorInteger();

     for ( int i = 1; i < 6; i++ ) {
         GenPolynomial<BigInteger> a = pfac.random(kl,ll*(i+1),el*(i+1),q);
         if ( ! a.leadingBaseCoefficient().isUnit() ) {
             //continue;
             ExpVector e = a.leadingExpVector();
             a.doPutToMap(e,cfac.getONE());
         }
         System.out.println("\na = " + a);

         SortedMap<GenPolynomial<BigInteger>,Integer> sm = fac.baseFactors( a );
         System.out.println("sm = " + sm);

         boolean t = fac.isFactorization( a, sm );
         //System.out.println("t        = " + t);
         assertTrue("prod(factor)) = a",t);
     }
 }


/**
 * Test multivariate integer factorization.
 * 
 */
 public void xtestMultivariateIntegerFactorization() {

     TermOrder to = new TermOrder(TermOrder.INVLEX);
     BigInteger cfac = new BigInteger(1);
     GenPolynomialRing<BigInteger> pfac = new GenPolynomialRing<BigInteger>(cfac,rl,to);
     FactorInteger fac = new FactorInteger();

     for ( int i = 1; i < 6; i++ ) {
         GenPolynomial<BigInteger> a = pfac.random(kl,ll*(i+1),el,q);
         if ( ! a.leadingBaseCoefficient().isUnit() ) {
             //continue;
             ExpVector e = a.leadingExpVector();
             a.doPutToMap(e,cfac.getONE());
         }
         System.out.println("\na = " + a);

         SortedMap<GenPolynomial<BigInteger>,Integer> sm = fac.factors( a );
         System.out.println("sm = " + sm);

         boolean t = fac.isFactorization( a, sm );
         //System.out.println("t        = " + t);
         assertTrue("prod(factor)) = a",t);
     }
     ComputerThreads.terminate();
 }


/**
 * Test rational factorization.
 * 
 */
 public void xtestRationalFactorization() {

     TermOrder to = new TermOrder(TermOrder.INVLEX);
     BigRational cfac = new BigRational(1);
     GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac,1,to);
     FactorRational fac = new FactorRational();

     for ( int i = 1; i < 6; i++ ) {
         GenPolynomial<BigRational> a = pfac.random(kl,ll*(i+1),el*(i+1),q);
         if ( ! a.leadingBaseCoefficient().isONE() ) {
             //continue;
             ExpVector e = a.leadingExpVector();
             a.doPutToMap(e,cfac.getONE());
         }
         System.out.println("\na = " + a);

         SortedMap<GenPolynomial<BigRational>,Integer> sm = fac.baseFactors( a );
         System.out.println("sm = " + sm);

         boolean t = fac.isFactorization( a, sm );
         //System.out.println("t        = " + t);
         assertTrue("prod(factor)) = a",t);
     }
 }

}
