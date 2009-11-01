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
import edu.jas.poly.PolyUtil;
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
        example5();
        example6();
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

        // 1 / ( x^2 - 2 )
        GenPolynomial<BigRational> D = pfac.parse("x^2 - 2"); 

        GenPolynomial<BigRational> N = pfac.getONE();

        FactorRational engine = new FactorRational();

        PartialFraction<BigRational> F = engine.baseAlgebraicPartialFractionIrreducible(N,D);
        System.out.println("\npartial fraction = " + F);
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

        // 1 / ( x^3 + x )
        GenPolynomial<BigRational> D = pfac.parse("x^3 + x"); 

        GenPolynomial<BigRational> N = pfac.getONE();

        FactorRational engine = new FactorRational();

        PartialFraction<BigRational> F = engine.baseAlgebraicPartialFraction(N,D);
        System.out.println("\npartial fraction = " + F);
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

        // 1 / ( x^6 - 5 x^4 + 5 x^2 + 4 )
        GenPolynomial<BigRational> D = pfac.parse("x^6 - 5 x^4 + 5 x^2 + 4"); 

        GenPolynomial<BigRational> N = pfac.getONE();

        FactorRational engine = new FactorRational();

        PartialFraction<BigRational> F = engine.baseAlgebraicPartialFraction(N,D);
        System.out.println("\npartial fraction = " + F);
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

        // 1 / ( x^4 + 4 )
        GenPolynomial<BigRational> D = pfac.parse("x^4 + 4"); 

        GenPolynomial<BigRational> N = pfac.getONE();
 
        FactorRational engine = new FactorRational();

        PartialFraction<BigRational> F = engine.baseAlgebraicPartialFraction(N,D);
        System.out.println("\npartial fraction = " + F);
    }


    /**
     * example5.
     * Rothstein-Trager algorithm.
     */
    public static void example5() {
        System.out.println("\n\nexample 5");

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
        String[] alpha = new String[] { "alpha" };
        String[] vars = new String[] { "x" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to, vars);

        // 1 / ( x^3 - 2 )
        GenPolynomial<BigRational> D = pfac.parse("x^3 - 2"); 

        GenPolynomial<BigRational> N = pfac.getONE();

        FactorRational engine = new FactorRational();

        PartialFraction<BigRational> F = engine.baseAlgebraicPartialFraction(N,D);
        System.out.println("\npartial fraction = " + F);
    }


    /**
     * example6.
     * Partial fraction decomposition.
     */
    public static void example6() {
        System.out.println("\n\nexample 6");
        // http://www.apmaths.uwo.ca/~rcorless/AM563/NOTES/Nov_16_95/node13.html

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
        String[] alpha = new String[] { "alpha" };
        String[] vars = new String[] { "x" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to, vars);

        // ( 7 x^6 + 1 ) /  ( x^7 + x + 1 )
        GenPolynomial<BigRational> D = pfac.parse("x^7 + x + 1"); 

        GenPolynomial<BigRational> N = PolyUtil.<BigRational> baseDeriviative(D);

        FactorRational engine = new FactorRational();

        PartialFraction<BigRational> F = engine.baseAlgebraicPartialFraction(N,D);
        System.out.println("\npartial fraction = " + F);
    }

}
