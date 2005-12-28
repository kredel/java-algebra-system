/*
 * $Id$
 */

package edu.jas.ring;

import java.util.List;

import edu.jas.structure.RingElem;

import edu.jas.poly.GenPolynomial;


/**
 * Groebner Bases interface.
 * Defines methods for Groebner bases and GB test.
 * @author Heinz Kredel
 */

public interface GroebnerBase<C extends RingElem<C>>  {


    /**
     * Groebner base test.
     * @param C coefficient type.
     * @param F polynomial list.
     * @return true, if F is a Groebner base, else false.
     */
    public boolean isGB(List<GenPolynomial<C>> F);


    /**
     * Groebner base test.
     * @param C coefficient type.
     * @param modv module variable nunber.
     * @param F polynomial list.
     * @return true, if F is a Groebner base, else false.
     */
    public boolean isGB(int modv, List<GenPolynomial<C>> F);


    /**
     * Groebner base using pairlist class.
     * @param C coefficient type.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    public List<GenPolynomial<C>> 
               GB( List<GenPolynomial<C>> F );


    /**
     * Groebner base using pairlist class.
     * @param C coefficient type.
     * @param modv module variable nunber.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    public List<GenPolynomial<C>> 
               GB( int modv, 
                   List<GenPolynomial<C>> F );


    /**
     * Minimal ordered groebner basis.
     * @param C coefficient type.
     * @param Gp a Groebner base.
     * @return a reduced Groebner base of Gp.
     */
    public List<GenPolynomial<C>> 
               minimalGB(List<GenPolynomial<C>> Gp);


}
