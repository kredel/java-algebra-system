
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
 */

interface OrderedPolynomial extends UnorderedPolynomial {

    public TermOrder getTermOrder();


    public void reSort();

    public void reSort(TermOrder to);


    public Map.Entry leadingMonomial();

    public ExpVector leadingExpVector();

    public Object leadingBaseCoefficient(); 

}
