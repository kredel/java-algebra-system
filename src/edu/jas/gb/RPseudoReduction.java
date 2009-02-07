/*
 * $Id$
 */

package edu.jas.gb;


import edu.jas.structure.RegularRingElem;


/**
 * Polynomial R pseudo reduction interface. Combines RReduction and
 * PseudoReduction.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public interface RPseudoReduction<C extends RegularRingElem<C>> extends RReduction<C>,
        PseudoReduction<C> {

}
