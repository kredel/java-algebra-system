/*
 * $Id$
 */

package edu.jas.ps;


import java.util.List;


import edu.jas.structure.RingElem;


/**
 * Interface for functions capable for Taylor series expansion.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public interface TaylorFunction<C extends RingElem<C>> {


    /**
     * Deriviative.
     * @return deriviative of this.
     */
    public TaylorFunction<C> deriviative();


    /**
     * Partial deriviative.
     * @param r index of the variable.
     * @return partial deriviative of this with respect to variable r.
     */
    public TaylorFunction<C> deriviative(int r);


    /**
     * Evaluate.
     * @param a element.
     * @return this(a).
     */
    public C evaluate(C a);


    /**
     * Evaluate at a given variable.
     * @param a element.
     * @param r index of the variable.
     * @return this_r(a).
     */
    public TaylorFunction<C> evaluate(C a, int r);


    /**
     * Evaluate at a tuple of elements.
     * @param a tuple of elements.
     * @return this(a).
     */
    public C evaluate(List<C> a);

}
