/*
 * $Id$
 */

package edu.jas.ring;

import java.util.List;


import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;


/**
 * Polynomial D Reduction interface.
 * Defines additionally D-Polynomial.
 * @author Heinz Kredel
 */

public interface DReduction<C extends RingElem<C>> 
                 extends Reduction<C> {


    /**
     * G-Polynomial.
     * @typeparam C coefficient type.
     * @param Ap polynomial.
     * @param Bp polynomial.
     * @return gpol(Ap,Bp) the g-polynomial of Ap and Bp.
     */
    public GenPolynomial<C> GPolynomial(GenPolynomial<C> Ap, 
                                        GenPolynomial<C> Bp);


    /**
     * D-Polynomial with recording.
     * @typeparam C coefficient type.
     * @param S recording matrix, is modified.
     * @param i index of Ap in basis list.
     * @param Ap a polynomial.
     * @param j index of Bp in basis list.
     * @param Bp a polynomial.
     * @return gpol(Ap, Bp), the g-Polynomial for Ap and Bp.
     */
    public GenPolynomial<C> 
           GPolynomial(List<GenPolynomial<C>> S,
                       int i,
                       GenPolynomial<C> Ap, 
                       int j,
                       GenPolynomial<C> Bp);

}
