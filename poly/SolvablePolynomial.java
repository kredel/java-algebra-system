
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

    public RelationTable getRelationTable();

    public SolvablePolynomial getZERO(RelationTable r);

    public SolvablePolynomial getZERO(RelationTable r, TermOrder t);

    public SolvablePolynomial getONE(RelationTable r);

    public SolvablePolynomial getONE(RelationTable r, TermOrder t);

    public SolvablePolynomial multiplyLeft(Coefficient b, ExpVector e);

    public SolvablePolynomial multiplyLeft(ExpVector e);

    public SolvablePolynomial multiplyLeft(Map.Entry m);

}
