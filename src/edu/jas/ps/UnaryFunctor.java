/*
 * $Id$
 */

package edu.jas.ps;


import edu.jas.structure.RingElem;


/**
 * Unary functor interface.
 * @param <C> ring element type
 * @param <D> ring element type
 * @author Heinz Kredel
 */

public interface UnaryFunctor< C extends RingElem<C>, D extends RingElem<D> > {


    /**
     * Evaluate.
     * @return evaluated element.
     */
    public D eval(C c);

}
