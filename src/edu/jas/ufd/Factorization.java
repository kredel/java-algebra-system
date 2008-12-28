
/*
 * $Id$
 */

package edu.jas.ufd;

import java.io.Serializable;

import java.util.Map;
import java.util.SortedMap;
import java.util.List;

import edu.jas.structure.GcdRingElem;
import edu.jas.poly.GenPolynomial;


/**
 * Factorization algorithm interface.
 * @author Heinz Kredel
 */

public interface Factorization<C extends GcdRingElem<C> > 
                 extends Serializable {


    /**
     * GenPolynomial test if is irreducible.
     * @param P GenPolynomial<C>.
     * @return true if P is irreducible, else false.
     */
    public boolean isIrreducible( GenPolynomial<C> P );


    /**
     * GenPolynomial test if a non trivial factorization exsists.
     * @param P GenPolynomial<C>.
     * @return true if P is reducible, else false.
     */
    public boolean isReducible( GenPolynomial<C> P );


    /**
     * GenPolynomial factorization of a squarefree polynomial.
     * @param P squarefree and primitive! GenPolynomial<C>.
     * @return [p_1,...,p_k] with P = prod_{i=1,...,r} p_i.
     */
    public List<GenPolynomial<C>> factorsSquarefree( GenPolynomial<C> P );


    /**
     * GenPolynomial factorization.
     * @param P GenPolynomial<C>.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i**e_i.
     */
    public SortedMap<GenPolynomial<C>,Integer> factors( GenPolynomial<C> P );


    /**
     * GenPolynomial greatest squarefree divisor.
     * @param P GenPolynomial.
     * @return squarefree(P).
     */
    public GenPolynomial<C> squarefreePart( GenPolynomial<C> P );


    /**
     * GenPolynomial squarefree factorization.
     * @param P GenPolynomial.
     * @return squarefreeFactors(P).
     */
    public Map<Integer,GenPolynomial<C>> squarefreeFactors( GenPolynomial<C> P );


    /**
     * GenPolynomial is factorization.
     * @param P GenPolynomial<C>.
     * @param F = [p_1,...,p_k].
     * @return true if P = prod_{i=1,...,r} p_i, else false.
     */
    public boolean isFactorization( GenPolynomial<C> P, List<GenPolynomial<C>> F );


    /**
     * GenPolynomial is factorization.
     * @param P GenPolynomial<C>.
     * @param F = [p_1 -> e_1, ..., p_k -> e_k].
     * @return true if P = prod_{i=1,...,k} p_i**e_i , else false.
     */
    public boolean isFactorization( GenPolynomial<C> P, SortedMap<GenPolynomial<C>,Integer>  F );

}
