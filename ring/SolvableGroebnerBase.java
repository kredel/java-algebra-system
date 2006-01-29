/*
 * $Id$
 */

package edu.jas.ring;


import java.util.List;

import edu.jas.poly.GenSolvablePolynomial;

import edu.jas.structure.RingElem;


/**
 * Solvable Groebner Bases interface.
 * Defines methods for left and twosided Groebner bases 
 * and left and twosided GB tests.
 * @author Heinz Kredel.
 */

public interface SolvableGroebnerBase<C extends RingElem<C>> {


    /**
     * Left Groebner base test.
     * @param C coefficient type.
     * @param F solvable polynomial list.
     * @return true, if F is a left Groebner base, else false.
     */
    public boolean isLeftGB(List<GenSolvablePolynomial<C>> F);


    /**
     * Left Groebner base test.
     * @param C coefficient type.
     * @param modv number of module variables.
     * @param F solvable polynomial list.
     * @return true, if F is a left Groebner base, else false.
     */
    public boolean isLeftGB(int modv, List<GenSolvablePolynomial<C>> F);


    /**
     * Twosided Groebner base test.
     * @param C coefficient type.
     * @param Fp solvable polynomial list.
     * @return true, if Fp is a two-sided Groebner base, else false.
     */
    public boolean isTwosidedGB(List<GenSolvablePolynomial<C>> Fp);


    /**
     * Twosided Groebner base test.
     * @param C coefficient type.
     * @param modv number of module variables.
     * @param Fp solvable polynomial list.
     * @return true, if Fp is a two-sided Groebner base, else false.
     */
    public boolean isTwosidedGB(int modv, 
                                List<GenSolvablePolynomial<C>> Fp);


    /**
     * Left Groebner base using pairlist class.
     * @param C coefficient type.
     * @param F solvable polynomial list.
     * @return leftGB(F) a left Groebner base of F.
     */
    public List<GenSolvablePolynomial<C>> 
               leftGB(List<GenSolvablePolynomial<C>> F);


    /**
     * Left Groebner base using pairlist class.
     * @param C coefficient type.
     * @param modv number of module variables.
     * @param F solvable polynomial list.
     * @return leftGB(F) a left Groebner base of F.
     */
    public List<GenSolvablePolynomial<C>> 
               leftGB(int modv, 
                      List<GenSolvablePolynomial<C>> F);


    /**
     * Left minimal ordered groebner basis.
     * @param C coefficient type.
     * @param Gp a left Groebner base.
     * @return leftGBmi(F) a minimal left Groebner base of Gp.
     */
    public List<GenSolvablePolynomial<C>> 
               leftMinimalGB(List<GenSolvablePolynomial<C>> Gp);


    /**
     * Twosided Groebner base using pairlist class.
     * @param C coefficient type.
     * @param Fp solvable polynomial list.
     * @return tsGB(Fp) a twosided Groebner base of Fp.
     */
    public List<GenSolvablePolynomial<C>> 
               twosidedGB(List<GenSolvablePolynomial<C>> Fp);

    /**
     * Twosided Groebner base using pairlist class.
     * @param C coefficient type.
     * @param modv number of module variables.
     * @param Fp solvable polynomial list.
     * @return tsGB(Fp) a twosided Groebner base of Fp.
     */
    public List<GenSolvablePolynomial<C>> 
               twosidedGB(int modv, 
                          List<GenSolvablePolynomial<C>> Fp);

}
