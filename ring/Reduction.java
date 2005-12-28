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
 * Polynomial Reduction interface.
 * Defines S-Polynomial, normalform, criterion 4, module criterion
 * and irreducible set.
 * @author Heinz Kredel
 */

public interface Reduction<C extends RingElem<C>>  {


    /**
     * S-Polynomial.
     * @param C coefficient type.
     * @param Ap polynomial.
     * @param Bp polynomial.
     * @return spol(Ap,Bp) the S-polynomial of Ap and Bp.
     */
    public GenPolynomial<C> SPolynomial(GenPolynomial<C> Ap, 
                                        GenPolynomial<C> Bp);


    /**
     * S-Polynomial with recording.
     * @param C coefficient type.
     * @param S recording matrix, is modified.
     * @param i index of Ap in basis list.
     * @param Ap a polynomial.
     * @param j index of Bp in basis list.
     * @param Bp a polynomial.
     * @return Spol(Ap, Bp), the S-Polynomial for Ap and Bp.
     */
    public GenPolynomial<C> 
           SPolynomial(List<GenPolynomial<C>> S,
                       int i,
                       GenPolynomial<C> Ap, 
                       int j,
                       GenPolynomial<C> Bp);


    /**
     * Module criterium.
     * @param C coefficient type.
     * @param modv number of module variables.
     * @param A polynomial.
     * @param B polynomial.
     * @return true if the module S-polynomial(i,j) is required.
     */
    public boolean moduleCriterion(int modv, 
                                   GenPolynomial<C> A, 
                                   GenPolynomial<C> B);


    /**
     * GB criterium 4.
     * Use only for commutative polynomial rings.
     * @param C coefficient type.
     * @param A polynomial.
     * @param B polynomial.
     * @param e = lcm(ht(A),ht(B))
     * @return true if the S-polynomial(i,j) is required, else false.
     */
    public boolean criterion4(GenPolynomial<C> A, 
                              GenPolynomial<C> B, 
                              ExpVector e);


    /**
     * GB criterium 4.
     * Use only for commutative polynomial rings.
     * @param C coefficient type.
     * @param A polynomial.
     * @param B polynomial.
     * @return true if the S-polynomial(i,j) is required, else false.
     */
    public boolean criterion4(GenPolynomial<C> A, 
                              GenPolynomial<C> B);


    /**
     * Normalform.
     * @param C coefficient type.
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return nf(Ap) with respect to Pp.
     */
    public GenPolynomial<C> normalform(List<GenPolynomial<C>> Pp, 
                                       GenPolynomial<C> Ap);


    /**
     * Normalform Set.
     * @param C coefficient type.
     * @param Ap polynomial list.
     * @param Pp polynomial list.
     * @return list of nf(a) with respect to Pp for all a in Ap.
     */
    public List<GenPolynomial<C>> normalform(List<GenPolynomial<C>> Pp, 
                                             List<GenPolynomial<C>> Ap);


    /**
     * Normalform with recording.
     * @param C coefficient type.
     * @param row recording matrix, is modified.
     * @param Pp a polynomial list for reduction.
     * @param Ap a polynomial.
     * @return nf(Pp,Ap), the normal form of Ap wrt. Pp.
     */
    public GenPolynomial<C> 
           normalform(List<GenPolynomial<C>> row,
                      List<GenPolynomial<C>> Pp, 
                      GenPolynomial<C> Ap);


    /**
     * Irreducible set.
     * @param C coefficient type.
     * @param Pp polynomial list.
     * @return a list P of polynomials which are in normalform wrt. P.
     */
    public List<GenPolynomial<C>> irreducibleSet(List<GenPolynomial<C>> Pp);


}
