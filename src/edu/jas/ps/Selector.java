/*
 * $Id$
 */

package edu.jas.ps;


import edu.jas.structure.RingElem;


/**
 * Selector interface.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public interface Selector<C extends RingElem<C>> {


    /**
     * Select.
     * @return true, if the element is selected, otherwise false.
     */
    public boolean select(C c);

}
