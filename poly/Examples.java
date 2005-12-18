/*
 * $Id$
 */

package edu.jas.poly;

import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;


/**
 * Examples.
 * @author Heinz Kredel.
 */

public class Examples {

/**
 * main.
 */
   public static void main (String[] args) {
       example1();
       example2();
       example3();
       example4();
   }

/**
 * example1.
 */
public static void example1() {
       System.out.println("\n\n example 1");

       BigRational cfac = new BigRational();
       System.out.println("cfac = " + cfac);
       GenPolynomialRing<BigRational> fac;
                fac = new GenPolynomialRing<BigRational>(cfac,7);
       //System.out.println("fac = " + fac);
       //fac.setVars( ExpVector.STDVARS(7) );
       System.out.println("fac = " + fac);

       GenPolynomial<BigRational> a = fac.random(10);
       System.out.println("a = " + a);
   }

/**
 * example2.
 */
public static void example2() {
       System.out.println("\n\n example 2");

       BigRational cfac = new BigRational();
       System.out.println("cfac = " + cfac);
       GenPolynomialRing<BigRational> fac;
                fac = new GenPolynomialRing<BigRational>(cfac,7);
       System.out.println("fac = " + fac);

       GenPolynomialRing<GenPolynomial<BigRational>> gfac;
               gfac = new GenPolynomialRing<GenPolynomial<BigRational>>(fac,3);
       System.out.println("gfac = " + gfac);

       GenPolynomial<GenPolynomial<BigRational>> a = gfac.random(10);
       System.out.println("a = " + a);
   }

/**
 * example3.
 */
public static void example3() {
       System.out.println("\n\n example 3");

       BigRational cfac = new BigRational();
       System.out.println("cfac = " + cfac);

       GenPolynomialRing<BigRational> mfac;
          mfac = new GenPolynomialRing<BigRational>( cfac, 1 );
       System.out.println("mfac = " + mfac);

       GenPolynomial<BigRational> modul = mfac.random(8).monic();
          // assume !mo.isUnit()
       System.out.println("modul = " + modul);

       AlgebraicNumber<BigRational> fac;
          fac = new AlgebraicNumber<BigRational>( modul );
       System.out.println("fac = " + fac);

       AlgebraicNumber< BigRational > a = fac.random(15);
       System.out.println("a = " + a);
   }

   protected static long getPrime() {
       long prime = 2; //2^60-93; // 2^30-35; //19; knuth (2,390)
       for ( int i = 1; i < 60; i++ ) {
           prime *= 2;
       }
       prime -= 93;
       //System.out.println("prime = " + prime);
       return prime;
   }

/**
 * example4.
 */
public static void example4() {
       System.out.println("\n\n example 4");

       long prime = getPrime();
       ModInteger cfac = new ModInteger(prime,1);
       System.out.println("cfac = " + cfac);

       GenPolynomialRing<ModInteger> mfac;
          mfac = new GenPolynomialRing<ModInteger>( cfac, 1 );
       System.out.println("mfac = " + mfac);

       GenPolynomial<ModInteger> modul = mfac.random(8).monic();
          // assume !modul.isUnit()
       System.out.println("modul = " + modul);

       AlgebraicNumber<ModInteger> fac;
          fac = new AlgebraicNumber<ModInteger>( modul );
       System.out.println("fac = " + fac);

       AlgebraicNumber< ModInteger > a = fac.random(12);
       System.out.println("a = " + a);
   }



}
