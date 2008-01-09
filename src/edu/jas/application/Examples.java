/*
 * $Id$
 */

package edu.jas.application;

import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;

import edu.jas.structure.Product;
import edu.jas.structure.ProductRing;

import edu.jas.arith.BigRational;
import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolynomialList;

import edu.jas.ring.GroebnerBaseSeq;

/**
 * Examples for polynomials usage.
 * @author Heinz Kredel.
 */

public class Examples {

/**
 * main.
 */
   public static void main (String[] args) {
       //example1();
       example2();
   }

/**
 * example1.
 * cyclic n-th roots polynomial systems.
 *
 */
public static void example1() {
    int n = 4;

    BigInteger fac = new BigInteger();
    String[] var = ExpVector.STDVARS(n);
    GenPolynomialRing<BigInteger> ring
           = new GenPolynomialRing<BigInteger>(fac,n,var);
    System.out.println("ring = " + ring + "\n");

    List<GenPolynomial<BigInteger>> cp = new ArrayList<GenPolynomial<BigInteger>>( n ); 
    for ( int i = 1; i <= n; i++ ) {
        GenPolynomial<BigInteger> p = cyclicPoly(ring, n, i);
        cp.add( p );
        System.out.println("p["+i+"] = " +  p);
        System.out.println();
    }
    System.out.println("cp = " + cp + "\n");

    List<GenPolynomial<BigInteger>> gb;
    GroebnerBaseSeq<BigInteger> sgb = new GroebnerBaseSeq<BigInteger>();
    gb = sgb.GB( cp );
    System.out.println("gb = " + gb);

}

    static GenPolynomial<BigInteger> cyclicPoly(GenPolynomialRing<BigInteger> ring, int n, int i) {

        List<? extends GenPolynomial<BigInteger> > X 
            = /*(List<GenPolynomial<BigInteger>>)*/ ring.univariateList();

        GenPolynomial<BigInteger> p = ring.getZERO();
        for ( int j = 1; j <= n; j++ ) {
            GenPolynomial<BigInteger> pi = ring.getONE();
            for ( int k = j; k < j+i; k++ ) {
                pi = pi.multiply( X.get( k % n ) );
            }
            p = p.sum( pi );
            if ( i == n ) {
               p = p.subtract( ring.getONE() );
               break;
            }
        }
        return p;
    }


/**
 * example2.
 * abtract types: List<GenPolynomial<Product<Residue<BigRational>>>>.
 *
 */
public static void example2() {
    List<GenPolynomial<Product<Residue<BigRational>>>> L = null;
    L = new ArrayList<GenPolynomial<Product<Residue<BigRational>>>>();

    BigRational bfac = new BigRational(1);
    GenPolynomialRing<BigRational> pfac = null;
    pfac = new GenPolynomialRing<BigRational>(bfac,3);

    List<GenPolynomial<BigRational>> F = null;
    F = new ArrayList<GenPolynomial<BigRational>>();

    GenPolynomial<BigRational> p = null;
    for ( int i = 0; i < 2; i++) {
        p = pfac.random(5,4,3,0.4f);
        F.add(p);
    }

    Ideal<BigRational> id = new Ideal<BigRational>(pfac,F);
    ResidueRing<BigRational> rr = new ResidueRing<BigRational>(id);

    ProductRing<Residue<BigRational>> pr = null;
    pr = new ProductRing<Residue<BigRational>>(rr,4);

    String[] vars = new String[] { "a", "b" };
    GenPolynomialRing<Product<Residue<BigRational>>> fac;
    fac = new GenPolynomialRing<Product<Residue<BigRational>>>(pr,2,vars);

    GenPolynomial<Product<Residue<BigRational>>> pp;
    for ( int i = 0; i < 3; i++) {
        pp = fac.random(2,4,4,0.4f);
        L.add(pp);
    }

    PolynomialList<Product<Residue<BigRational>>> Lp = null;
    Lp = new PolynomialList<Product<Residue<BigRational>>>(fac,L);

    System.out.println("Lp = " + Lp);

}

}
