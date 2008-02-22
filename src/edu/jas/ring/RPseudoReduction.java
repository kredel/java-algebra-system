/*
 * $Id$
 */

package edu.jas.ring;


import edu.jas.structure.RegularRingElem;


/**
 * Polynomial R pseudo reduction interface.
 * Combines RReduction and PseudoReduction.
 * @author Heinz Kredel
 */

public interface RPseudoReduction<C extends RegularRingElem<C>> 
       extends RReduction<C>, PseudoReduction<C> {

}
