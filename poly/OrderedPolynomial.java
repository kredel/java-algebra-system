
/*
 * $Id$
 */

package edu.jas.poly;

import edu.jas.arith.Coefficient;

import java.util.Map;

/**
 * Ordered Polynomial Interface. 
 * For implementations based on Sorted Maps, 
 * e.g. java.util.TreeMap.
 * @author Heinz Kredel
 */

public interface OrderedPolynomial /* extends UnorderedPolynomial */ {

    public TermOrder getTermOrder();


    public Map getMap();

    public int length();

    public String[] getVars();

    public String[] setVars(String[] v);

    public int numberOfVariables(); 


    public String toString(); 

    public String toString(String[] vars); 

    public boolean equals( Object B ); 


    public Map.Entry leadingMonomial();

    public ExpVector leadingExpVector();

    public Coefficient leadingBaseCoefficient(); 


    public OrderedPolynomial getZERO();

    public OrderedPolynomial getZERO(TermOrder t);

    public OrderedPolynomial getONE();

    public OrderedPolynomial getONE(TermOrder t);

    public boolean isZERO();

    public boolean isONE();


    public OrderedPolynomial add(OrderedPolynomial B);

    public OrderedPolynomial add(Coefficient b, ExpVector e);

    public OrderedPolynomial subtract(OrderedPolynomial B);

    public OrderedPolynomial subtract(Coefficient b, ExpVector e);

    public OrderedPolynomial negate();


    public OrderedPolynomial monic();

    public OrderedPolynomial multiply(OrderedPolynomial B);

    public OrderedPolynomial multiply(Coefficient b);

    public OrderedPolynomial multiply(Coefficient b, ExpVector e);

    public OrderedPolynomial multiply(ExpVector e);

    public OrderedPolynomial multiply(Map.Entry m);

}
