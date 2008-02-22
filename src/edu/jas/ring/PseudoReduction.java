/*
 * $Id$
 */

package edu.jas.ring;

import java.util.List;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;


/**
 * Polynomial pseudo reduction interface.
 * Defines additionaly normalformFactor.
 * @author Heinz Kredel
 */

public interface PseudoReduction<C extends RingElem<C>> 
                 extends Reduction<C> {


    /**
     * Normalform.
     * @typeparam C coefficient type.
     * @param Pp polynomial list.
     * @param Ap polynomial.
     * @return ( nf(Ap), mf ) with respect to Pp and 
               mf as multiplication factor for Ap.
     */
    public PseudoReductionEntry<C> 
           normalformFactor( List<GenPolynomial<C>> Pp, 
                             GenPolynomial<C> Ap );

}
