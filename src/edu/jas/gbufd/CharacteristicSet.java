/*
 * $Id$
 */

package edu.jas.gbufd;

import java.util.List;
import java.io.Serializable;

import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingElem;
import edu.jas.poly.GenPolynomial;


/**
 * Characteristic Set interface.
 * Defines methods for Characteristic Sets and tests.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */
public interface CharacteristicSet<C extends GcdRingElem<C>> extends Serializable {


    /**
     * Characteristic set.
     * According to Wu's algorithm with rereduction of leading coefficients.
     * @param A list of generic polynomials.
     * @return charSetWu(A).
     */
    public List<GenPolynomial<C>> characteristicSet(List<GenPolynomial<C>> A);


    /**
     * Characteristic set test.
     * @param A list of generic polynomials.
     * @return true, if A is a characteristic set, else false.
     */
    public boolean isCharacteristicSet(List<GenPolynomial<C>> A);


    /**
     * Characteristic set reduction.
     * Pseudo remainder wrt. the main variabe with further pseudo reduction of the leading coefficient.
     * @param P generic polynomial.
     * @param A list of generic polynomials as characteristic set.
     * @return characteristicSetReductionCoeff(characteristicSetRemainder(A,P),Ap).
     */
    public GenPolynomial<C> characteristicSetReduction(List<GenPolynomial<C>> A, GenPolynomial<C> P);

}
