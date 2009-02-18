/*
 * $Id$
 */

package edu.jas.ufd;


import java.io.Serializable;
import java.util.List;
import java.util.SortedMap;

import edu.jas.poly.GenPolynomial;
import edu.jas.structure.GcdRingElem;


/**
 * Factorization algorithms interface.
 * @usage 
 * @see edu.jas.ufd.FactorFactory
 * @author Heinz Kredel
 * @param <C> coefficient type
 */

public interface Factorization<C extends GcdRingElem<C>> extends Serializable {


    /**
     * GenPolynomial test if is irreducible.
     * @param P GenPolynomial.
     * @return true if P is irreducible, else false.
     */
    public boolean isIrreducible(GenPolynomial<C> P);


    /**
     * GenPolynomial test if a non trivial factorization exsists.
     * @param P GenPolynomial.
     * @return true if P is reducible, else false.
     */
    public boolean isReducible(GenPolynomial<C> P);


    /**
     * GenPolynomial test if is squarefree.
     * @param P GenPolynomial.
     * @return true if P is squarefree, else false.
     */
    public boolean isSquarefree(GenPolynomial<C> P);


    /**
     * GenPolynomial factorization of a squarefree polynomial.
     * @param P squarefree and primitive! or monic! GenPolynomial.
     * @return [p_1,...,p_k] with P = prod_{i=1,...,r} p_i.
     */
    public List<GenPolynomial<C>> factorsSquarefree(GenPolynomial<C> P);


    /**
     * GenPolynomial factorization.
     * @param P GenPolynomial.
     * @return [p_1 -&gt; e_1, ..., p_k -&gt; e_k] with P = prod_{i=1,...,k} p_i**e_i.
     */
    public SortedMap<GenPolynomial<C>, Long> factors(GenPolynomial<C> P);


    /**
     * GenPolynomial greatest squarefree divisor.
     * @param P GenPolynomial.
     * @return squarefree(P).
     */
    public GenPolynomial<C> squarefreePart(GenPolynomial<C> P);


    /**
     * GenPolynomial squarefree factorization.
     * @param P GenPolynomial.
     * @return [p_1 -&gt; e_1, ..., p_k -&gt; e_k] with P = prod_{i=1,...,k} p_i^{e_i}
     *         and p_i squarefree.
     */
    public SortedMap<GenPolynomial<C>, Long> squarefreeFactors(GenPolynomial<C> P);


    /**
     * GenPolynomial is factorization.
     * @param P GenPolynomial
     * @param F = [p_1,...,p_k].
     * @return true if P = prod_{i=1,...,r} p_i, else false.
     */
    public boolean isFactorization(GenPolynomial<C> P, List<GenPolynomial<C>> F);


    /**
     * GenPolynomial is factorization.
     * @param P GenPolynomial.
     * @param F = [p_1 -&gt; e_1, ..., p_k -&gt; e_k].
     * @return true if P = prod_{i=1,...,k} p_i**e_i , else false.
     */
    public boolean isFactorization(GenPolynomial<C> P, SortedMap<GenPolynomial<C>, Long> F);

}
