/*
 * $Id$
 */

package edu.jas.poly;

import java.util.ArrayList;
import java.util.List;

import edu.jas.arith.BigRational;
import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;


/**
 * Examples for polynomials usage.
 * @author Heinz Kredel.
 */

public class Examples {

/**
 * main.
 */
   public static void main (String[] args) {
       /*
       example1();
       example2();
       example3();
       example4();
       example5();
       */
       //example6();
       //example7();
       //example8();
       example9();
       //example10();
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

       AlgebraicNumberRing<BigRational> fac;
          fac = new AlgebraicNumberRing<BigRational>( modul );
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
       ModIntegerRing cfac = new ModIntegerRing(prime);
       System.out.println("cfac = " + cfac);

       GenPolynomialRing<ModInteger> mfac;
          mfac = new GenPolynomialRing<ModInteger>( cfac, 1 );
       System.out.println("mfac = " + mfac);

       GenPolynomial<ModInteger> modul = mfac.random(8).monic();
          // assume !modul.isUnit()
       System.out.println("modul = " + modul);

       AlgebraicNumberRing<ModInteger> fac;
          fac = new AlgebraicNumberRing<ModInteger>( modul );
       System.out.println("fac = " + fac);

       AlgebraicNumber< ModInteger > a = fac.random(12);
       System.out.println("a = " + a);
   }


/**
 * example5.
 */
public static void example5() {
       System.out.println("\n\n example 5");

       BigRational cfac = new BigRational();
       System.out.println("cfac = " + cfac);
       GenSolvablePolynomialRing<BigRational> sfac;
                sfac = new GenSolvablePolynomialRing<BigRational>(cfac,6);
       //System.out.println("sfac = " + sfac);
       sfac.setVars( ExpVector.STDVARS(6) );
       //System.out.println("sfac = " + sfac);

       WeylRelations<BigRational> wl = new WeylRelations<BigRational>(sfac);
       wl.generate();
       System.out.println("sfac = " + sfac);

       GenSolvablePolynomial<BigRational> a = sfac.random(5);
       System.out.println("a = " + a);
       System.out.println("a = " + a.toString( sfac.vars ) );

       GenSolvablePolynomial<BigRational> b = a.multiply(a);
       System.out.println("b = " + b);
       System.out.println("b = " + b.toString( sfac.vars ) );

       System.out.println("sfac = " + sfac);
   }


/**
 * example6.
 */
public static void example6() {
       System.out.println("\n\n example 6");

       BigInteger cfac = new BigInteger();
       System.out.println("cfac = " + cfac);

       TermOrder to = new TermOrder( TermOrder.INVLEX );
       System.out.println("to   = " + to);

       GenPolynomialRing<BigInteger> fac;
       fac = new GenPolynomialRing<BigInteger>(cfac,3,to);
       System.out.println("fac = " + fac);
                fac.setVars( new String[]{ "z", "y", "x" } );
       System.out.println("fac = " + fac);

       GenPolynomial<BigInteger> x = fac.univariate(0);
       GenPolynomial<BigInteger> y = fac.univariate(1);
       GenPolynomial<BigInteger> z = fac.univariate(2);

       System.out.println("x = " + x);
       System.out.println("x = " + x.toString( fac.vars ) );
       System.out.println("y = " + y);
       System.out.println("y = " + y.toString( fac.vars ) );
       System.out.println("z = " + z);
       System.out.println("z = " + z.toString( fac.vars ) );

       GenPolynomial<BigInteger> p = x.sum(y).sum(z).sum( fac.getONE());
       BigInteger f = cfac.fromInteger(10000000001L);
       //       p = p.multiply( f );
       System.out.println("p = " + p);
       System.out.println("p = " + p.toString( fac.vars ) );

       GenPolynomial<BigInteger> q = p;
       for ( int i = 1; i < 20; i++ ) {
           q = q.multiply(p);
       }
       //System.out.println("q = " + q.toString( fac.vars ) );
       System.out.println("q = " + q.length());

       GenPolynomial<BigInteger> q1 = q.sum( fac.getONE() );

       GenPolynomial<BigInteger> q2;
       long t = System.currentTimeMillis();
       q2 = q.multiply(q1); 
       t = System.currentTimeMillis() - t;

       System.out.println("q2 = " + q2.length());
       System.out.println("time = " + t + " ms");
   }


/**
 * example7.
 */
public static void example7() {
       System.out.println("\n\n example 7");

       BigRational cfac = new BigRational();
       System.out.println("cfac = " + cfac);

       TermOrder to = new TermOrder( TermOrder.INVLEX );
       System.out.println("to   = " + to);

       GenPolynomialRing<BigRational> fac;
       fac = new GenPolynomialRing<BigRational>(cfac,3,to);
       System.out.println("fac = " + fac);
                fac.setVars( new String[]{ "z", "y", "x" } );
       System.out.println("fac = " + fac);

       long mi = 1L;
       //long mi = Integer.MAX_VALUE;
       GenPolynomial<BigRational> x = fac.univariate(0,mi);
       GenPolynomial<BigRational> y = fac.univariate(1,mi);
       GenPolynomial<BigRational> z = fac.univariate(2,mi);

       System.out.println("x = " + x);
       System.out.println("x = " + x.toString( fac.vars ) );
       System.out.println("y = " + y);
       System.out.println("y = " + y.toString( fac.vars ) );
       System.out.println("z = " + z);
       System.out.println("z = " + z.toString( fac.vars ) );

       GenPolynomial<BigRational> p = x.sum(y).sum(z).sum( fac.getONE());
       BigRational f = cfac.fromInteger(10000000001L);
       f = f.multiply( f );
       p = p.multiply( f );
       System.out.println("p = " + p);
       System.out.println("p = " + p.toString( fac.vars ) );

       GenPolynomial<BigRational> q = p;
       for ( int i = 1; i < 20; i++ ) {
           q = q.multiply(p);
       }
       //System.out.println("q = " + q.toString( fac.vars ) );
       System.out.println("q = " + q.length());

       GenPolynomial<BigRational> q1 = q.sum( fac.getONE() );

       GenPolynomial<BigRational> q2;
       long t = System.currentTimeMillis();
       q2 = q.multiply(q1); 
       t = System.currentTimeMillis() - t;

       System.out.println("q2 = " + q2.length());
       System.out.println("time = " + t + " ms");
   }

/**
 * example8.
 * Chebyshev polynomials
 *
 *  T(0) = 1
 *  T(1) = x
 *  T(n) = 2x * T(n-1) - T(n-2)
 */
public static void example8() {
    int n = 10;

    BigInteger fac = new BigInteger();
    String[] var = new String[]{ "x" };

    GenPolynomialRing<BigInteger> ring
        = new GenPolynomialRing<BigInteger>(fac,1,var);

    List<GenPolynomial<BigInteger>> T 
       = new ArrayList<GenPolynomial<BigInteger>>(n);

    GenPolynomial<BigInteger> t, one, x, x2, x2a, x2b;

    one = ring.getONE();
    x   = ring.univariate(0);
    x2  = ring.parse("2 x");
    x2a = x.multiply( fac.fromInteger(2) );
    x2b = x.multiply( new BigInteger(2) );
    x2 = x2b;

    T.add( one );
    T.add( x );
    for ( int i = 2; i < n; i++ ) {
        t = x2.multiply( T.get(i-1) ).subtract( T.get(i-2) );
        T.add( t );
    }
    for ( int i = n-2; i < n; i++ ) {
        System.out.println("T["+i+"] = " + T.get(i).toString(var) );
    }
}

/**
 * example9.
 * Legendre polynomials
 *
 *  P(0) = 1
 *  P(1) = x
 *  P(n) = 1/n [ (2n-1) * x * P(n-1) - (n-1) * P(n-2) ]
 */
 // P(n+1) = 1/(n+1) [ (2n+1) * x * P(n) - n * P(n-1) ]
public static void example9() {
    int n = 10;

    BigRational fac = new BigRational();
    String[] var = new String[]{ "x" };

    GenPolynomialRing<BigRational> ring
        = new GenPolynomialRing<BigRational>(fac,1,var);

    List<GenPolynomial<BigRational>> P 
       = new ArrayList<GenPolynomial<BigRational>>(n);

    GenPolynomial<BigRational> t, one, x, xc, xn;
    BigRational n21, nn;

    one = ring.getONE();
    x   = ring.univariate(0);

    P.add( one );
    P.add( x );
    for ( int i = 2; i < n; i++ ) {
        n21 = new BigRational( 2*i-1 );
        xc = x.multiply( n21 );
        t = xc.multiply( P.get(i-1) );
        nn = new BigRational( i-1 );
        xc = P.get(i-2).multiply( nn );
        t = t.subtract( xc );
        nn = new BigRational(1,i);
        t = t.multiply( nn );
        P.add( t );
    }
    for ( int i = 0; i < n; i++ ) {
        System.out.println("P["+i+"] = " + P.get(i).toString(var) );
        System.out.println();
    }
}


/**
 * example10.
 * Hermite polynomials
 *
 *  H(0) = 1
 *  H(1) = 2 x
 *  H(n) = 2 * x * H(n-1) - 2 * (n-1) * H(n-2)
 */
 // H(n+1) = 2 * x * H(n) - 2 * n * H(n-1)
public static void example10() {
    int n = 100;

    BigInteger fac = new BigInteger();
    String[] var = new String[]{ "x" };

    GenPolynomialRing<BigInteger> ring
        = new GenPolynomialRing<BigInteger>(fac,1,var);

    List<GenPolynomial<BigInteger>> H 
       = new ArrayList<GenPolynomial<BigInteger>>(n);

    GenPolynomial<BigInteger> t, one, x2, xc, x;
    BigInteger n2, nn;

    one = ring.getONE();
    x   = ring.univariate(0);
    n2 = new BigInteger(2);
    x2 = x.multiply( n2 );
    H.add( one );
    H.add( x2 );
    for ( int i = 2; i < n; i++ ) {
        t = x2.multiply( H.get(i-1) );
        nn = new BigInteger( 2*(i-1) );
        xc = H.get(i-2).multiply( nn );
        t = t.subtract( xc );
        H.add( t );
    }
    for ( int i = n-1; i < n; i++ ) {
        System.out.println("H["+i+"] = " + H.get(i).toString(var) );
        System.out.println();
    }
}

}
