
/*
 * $Id$
 */

package edu.jas.poly;

import edu.jas.arith.Coefficient;

import java.util.Map;

/**
 * Solvable Polynomial Interface. 
 * Adds some left multiplication methods to OrderedPolynomial.
 * @author Heinz Kredel
 */

public interface SolvablePolynomial extends OrderedPolynomial {

    public OrderedPolynomial multiplyLeft(Coefficient b, ExpVector e);

    public OrderedPolynomial multiplyLeft(ExpVector e);

    public OrderedPolynomial multiplyLeft(Map.Entry m);

}
