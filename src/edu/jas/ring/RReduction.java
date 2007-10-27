/*
 * $Id$
 */

package edu.jas.ring;

import java.util.List;


import edu.jas.structure.RingElem;
import edu.jas.structure.RegularRingElem;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;



/**
 * Polynomial R Reduction interface.
 * Defines additionally boolean closure methods.
 * @author Heinz Kredel
 */

public interface RReduction<C extends RegularRingElem<C>> 
                 extends Reduction<C> {


    /**
     * Is boolean closed, i.e.
     * test if A == idempotent(ldcf(A)) A.
     * @typeparam C coefficient type.
     * @param A polynomial.
     * @return true if A is boolean closed, else false.
     */
     public boolean isBooleanClosed(GenPolynomial<C> A);


    /**
     * Is boolean closed, i.e.
     * test if all A in F are boolean closed.
     * @typeparam C coefficient type.
     * @param F polynomial list.
     * @return true if F is boolean closed, else false.
     */
    public boolean isBooleanClosed(List<GenPolynomial<C>> F);


    /**
     * Boolean closure, i.e.
     * compute idempotent(ldcf(A)) A.
     * @typeparam C coefficient type.
     * @param A polynomial.
     * @return bc(A).
     */
    public GenPolynomial<C> booleanClosure(GenPolynomial<C> A);


    /**
     * Boolean remainder, i.e.
     * compute idemComplement(ldcf(A)) A.
     * @typeparam C coefficient type.
     * @param A polynomial.
     * @return br(A).
     */
    public GenPolynomial<C> booleanRemainder(GenPolynomial<C> A);


    /**
     * Reduced boolean closure, i.e.
     * compute BC(A) for all A in F.
     * @typeparam C coefficient type.
     * @param F polynomial list.
     * @return red(bc(F)) = bc(red(F)).
     */
    public List<GenPolynomial<C>> reducedBooleanClosure(List<GenPolynomial<C>> F);


    /**
     * Reduced boolean closure, i.e.
     * compute BC(A) modulo F.
     * @typeparam C coefficient type.
     * @param A polynomial.
     * @param F polynomial list.
     * @return red(bc(A)).
     */
    public List<GenPolynomial<C>> reducedBooleanClosure(List<GenPolynomial<C>> F,
                                                        GenPolynomial<C> A);


}
