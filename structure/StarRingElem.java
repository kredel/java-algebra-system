/*
 * $Id: $
 */

package edu.jas.structure;


/**
 * StarRingElement interface for use with the basic arithmetic classes.
 * Adds norm and conjugation.
 * @author Heinz Kredel
 */

public interface StarRingElem<C extends StarRingElem<C>> 
                 extends RingElem<C> {

    /**
     * Conjugate of this.
     * @return conj(this).
     */
    public C conjugate();


    /**
     * Norm of this.
     * @return norm(this).
     */
    public C norm();

}
