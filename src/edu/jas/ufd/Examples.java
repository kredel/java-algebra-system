/*
 * $Id$
 */

package edu.jas.ufd;

import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;

import edu.jas.kern.ComputerThreads;
import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;


/**
 * Examples for ufd usage.
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
        ComputerThreads.terminate();
    }


    /**
     * example1.
     * Rothstein-Trager algorithm.
     */
    public static void example1() {
        System.out.println("\n\nexample 1");

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
        String[] alpha = new String[] { "alpha" };
        String[] vars = new String[] { "x" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to, vars);
        GenPolynomial<BigRational> D;

        GenPolynomial<BigRational> x2 = pfac.univariate(0, 2);
        // x^2 - 2
        D = x2.subtract(pfac.fromInteger(2)); 

        GenPolynomial<BigRational> N = pfac.getONE();
        //GenPolynomial<BigRational> N = pfac.univariate(0);
        //GenPolynomial<BigRational> N = pfac.fromInteger(5);

        FactorRational engine = new FactorRational();

        FactorsList<BigRational> F = engine.baseAlgebraicPartialFraction(N,D);
        System.out.println("\nN     = " + N);
        System.out.println("D     = " + D);
        System.out.println("F     = " + F);

    }


    /**
     * example2.
     * Rothstein-Trager algorithm.
     */
    public static void example2() {
        System.out.println("\n\nexample 2");

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
        String[] alpha = new String[] { "alpha" };
        String[] vars = new String[] { "x" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to, vars);
        GenPolynomial<BigRational> D;

        GenPolynomial<BigRational> x3 = pfac.univariate(0, 3);
        GenPolynomial<BigRational> x = pfac.univariate(0);
        // x^3 + x
        D = x3.sum(x); 

        GenPolynomial<BigRational> N = pfac.getONE();
        //GenPolynomial<BigRational> N = pfac.univariate(0);
        //GenPolynomial<BigRational> N = pfac.fromInteger(5);

        FactorRational engine = new FactorRational();

        FactorsList<BigRational> F = engine.baseAlgebraicPartialFraction(N,D);
        System.out.println("\nN     = " + N);
        System.out.println("D     = " + D);
        System.out.println("F     = " + F);

    }


    /**
     * example3.
     * Rothstein-Trager algorithm.
     */
    public static void example3() {
        System.out.println("\n\nexample 3");

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
        String[] alpha = new String[] { "alpha" };
        String[] vars = new String[] { "x" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to, vars);
        GenPolynomial<BigRational> D;

        GenPolynomial<BigRational> x6 = pfac.univariate(0, 6);
        GenPolynomial<BigRational> x4 = pfac.univariate(0, 4);
        GenPolynomial<BigRational> x2 = pfac.univariate(0, 2);
        // x^6 - 5 x^4 + 5 x^2 + 4
        D = x6.subtract(x4.multiply(pfac.fromInteger(5))); 
        D = D.sum(x2.multiply(pfac.fromInteger(5))); 
        D = D.sum(pfac.fromInteger(4)); 

        GenPolynomial<BigRational> N = pfac.getONE();
        //GenPolynomial<BigRational> N = pfac.univariate(0);
        //GenPolynomial<BigRational> N = pfac.fromInteger(5);

        FactorRational engine = new FactorRational();

        FactorsList<BigRational> F = engine.baseAlgebraicPartialFraction(N,D);
        System.out.println("\nN     = " + N);
        System.out.println("D     = " + D);
        System.out.println("F     = " + F);
    }


    /**
     * example4.
     * Rothstein-Trager algorithm.
     */
    public static void example4() {
        System.out.println("\n\nexample 4");

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
        String[] alpha = new String[] { "alpha" };
        String[] vars = new String[] { "x" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to, vars);
        GenPolynomial<BigRational> D = pfac.univariate(0, 4);
        D = D.sum(pfac.fromInteger(4)); // x^4 + 4

        GenPolynomial<BigRational> N = pfac.getONE();
        //GenPolynomial<BigRational> N = pfac.univariate(0);
        //GenPolynomial<BigRational> N = pfac.fromInteger(5);

        FactorRational engine = new FactorRational();

        FactorsList<BigRational> F = engine.baseAlgebraicPartialFraction(N,D);
        System.out.println("\nN     = " + N);
        System.out.println("D     = " + D);
        System.out.println("F     = " + F);
    }


    /**
     * example5.
     * Partial fraction decomposition.
     */
    public static void example5() {
        System.out.println("\n\nexample 5");
    }

}
