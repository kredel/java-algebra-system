/*
 * $Id$
 */

package edu.jas.ps;


import edu.jas.structure.RingElem;


/**
 * Interface for generating functions for coefficients of power series.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public interface Coefficients<C extends RingElem<C>> {


    /**
     * Get coefficient.
     * @param index of requested coefficient.
     * @return coefficient at index.
     */
    public C get(int index);
 
}
