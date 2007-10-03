/*
 * $Id$
 */

package edu.jas.ring;

import java.util.List;

import java.io.Serializable;

import edu.jas.poly.ExpVector;
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
     * D-Polynomial.
     * @typeparam C coefficient type.
     * @param Ap polynomial.
     * @param Bp polynomial.
     * @return dpol(Ap,Bp) the d-polynomial of Ap and Bp.
     */
    public GenPolynomial<C> DPolynomial(GenPolynomial<C> Ap, 
                                        GenPolynomial<C> Bp);


    /**
     * D-Polynomial with recording.
     * @typeparam C coefficient type.
     * @param S recording matrix, is modified.
     * @param i index of Ap in basis list.
     * @param Ap a polynomial.
     * @param j index of Bp in basis list.
     * @param Bp a polynomial.
     * @return dpol(Ap, Bp), the d-Polynomial for Ap and Bp.
     */
    public GenPolynomial<C> 
           DPolynomial(List<GenPolynomial<C>> S,
                       int i,
                       GenPolynomial<C> Ap, 
                       int j,
                       GenPolynomial<C> Bp);

}
