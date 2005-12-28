/*
 * $Id$
 */

package edu.jas.ring;

import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;

import edu.jas.poly.GenSolvablePolynomial;


/**
 * Solvable Polynomial Reduction interface.
 * Defines S-Polynomial, normalform
 * and irreducible set.
 * @author Heinz Kredel
 */

public interface SolvableReduction<C extends RingElem<C>>  {


    /**
     * Left S-Polynomial.
     * @param C coefficient type.
     * @param Ap solvable polynomial.
     * @param Bp solvable polynomial.
     * @return left-spol(Ap,Bp) the left S-polynomial of Ap and Bp.
     */
    public GenSolvablePolynomial<C> 
           leftSPolynomial(GenSolvablePolynomial<C> Ap, 
                           GenSolvablePolynomial<C> Bp);


    /**
     * S-Polynomial with recording.
     * @param C coefficient type.
     * @param S recording matrix, is modified.
     * @param i index of Ap in basis list.
     * @param Ap a polynomial.
     * @param j index of Bp in basis list.
     * @param Bp a polynomial.
     * @return leftSpol(Ap, Bp), the left S-Polynomial for Ap and Bp.
     */
    public GenSolvablePolynomial<C> 
           leftSPolynomial(List<GenSolvablePolynomial<C>> S,
                           int i,
                           GenSolvablePolynomial<C> Ap, 
                           int j,
                           GenSolvablePolynomial<C> Bp);


    /**
     * Left Normalform.
     * @param C coefficient type.
     * @param Ap solvable polynomial.
     * @param Pp solvable polynomial list.
     * @return left-nf(Ap) with respect to Pp.
     */
    public GenSolvablePolynomial<C> 
           leftNormalform(List<GenSolvablePolynomial<C>> Pp, 
                          GenSolvablePolynomial<C> Ap);


    /**
     * LeftNormalform with recording.
     * @param C coefficient type.
     * @param row recording matrix, is modified.
     * @param Pp a polynomial list for reduction.
     * @param Ap a polynomial.
     * @return nf(Pp,Ap), the left normal form of Ap wrt. Pp.
     */
    public GenSolvablePolynomial<C> 
           leftNormalform(List<GenSolvablePolynomial<C>> row,
                          List<GenSolvablePolynomial<C>> Pp, 
                          GenSolvablePolynomial<C> Ap);


    /**
     * Left Normalform Set.
     * @param C coefficient type.
     * @param Ap solvable polynomial list.
     * @param Pp solvable polynomial list.
     * @return list of left-nf(a) with respect to Pp for all a in Ap.
     */
    public List<GenSolvablePolynomial<C>> 
           leftNormalform(List<GenSolvablePolynomial<C>> Pp, 
                          List<GenSolvablePolynomial<C>> Ap);


    /**
     * Left irreducible set.
     * @param C coefficient type.
     * @param Pp solvable polynomial list.
     * @return a list P of solvable polynomials which are in normalform wrt. P.
     */
    public List<GenSolvablePolynomial<C>> 
           leftIrreducibleSet(List<GenSolvablePolynomial<C>> Pp); 


}
