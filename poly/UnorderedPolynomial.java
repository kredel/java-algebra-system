
/*
 * $Id$
 */

package edu.jas.poly;

import edu.jas.arith.Coefficient;
import edu.jas.poly.ExpVector;

import java.util.Map;

/**
 * Unordered Polynomial Interface. 
 * For implementations based on Maps, 
 * e.g. java.util.LinkedHashMap.
 */

interface UnorderedPolynomial {


    public Map getMap();

    public int length();

    public String[] getVars();

    public String[] setVars(String[] v);

    public int numberOfVariables(); 


    public String toString(); 

    public String toString(String[] vars); 

    public boolean equals( Object B ); 


    public UnorderedPolynomial getZERO();

    public UnorderedPolynomial getONE();

    public boolean isZERO();

    public boolean isONE();


    public UnorderedPolynomial add(UnorderedPolynomial B);

    public UnorderedPolynomial subtract(UnorderedPolynomial B);

    public UnorderedPolynomial negate();


    public UnorderedPolynomial multiply(UnorderedPolynomial B);

    public UnorderedPolynomial multiply(Coefficient b);

    public UnorderedPolynomial multiply(Coefficient b, ExpVector e);

    public UnorderedPolynomial multiply(Map.Entry m);

}
