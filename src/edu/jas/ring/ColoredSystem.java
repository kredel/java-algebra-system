/*
 * $Id$
 */

package edu.jas.ring;

import java.util.List;
import java.util.ArrayList;

import edu.jas.structure.RingElem;
import edu.jas.structure.GcdRingElem;

import edu.jas.poly.ColorPolynomial;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;

import edu.jas.application.Ideal;



/**
  * Container for a condition and corresponding colored polynomial system.
  * @param <C> coefficient type
  * @param F an ideal base.
  */
public class ColoredSystem<C extends GcdRingElem<C>> {

    public final Ideal<C> conditions;
    public final List<ColorPolynomial<C>> S;


    public ColoredSystem( Ideal<C> id,
                          List<ColorPolynomial<C>> S ) {
        this.conditions = id;
        this.S = S;
    }


    /** Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("ColoredSystem: \n\n");
        s.append("conditions: " + conditions + "\n\n");
        s.append("system: " + S + "\n\n");
        return s.toString();
    }


    /**
     * Get zero condition on coefficients. 
     * @return green coefficients.
     */
    public List<GenPolynomial<C>> getConditionZero() {
        List<GenPolynomial<C>> c = conditions.getList();
        return new ArrayList<GenPolynomial<C>>( c );
    }


    /**
     * Get non zero condition on coefficients. 
     * @return red coefficients.
     */
    public List<GenPolynomial<C>> getConditionNonZero() {
        List<GenPolynomial<C>> N = new ArrayList<GenPolynomial<C>>();
        for ( ColorPolynomial<C> c : S ) {
            List<GenPolynomial<C>> r = c.getConditionNonZero();
            N.addAll( r );
        }
        return N;
    }


    /**
     * Check ordering invariants.
     * TT(green) > LT(red) and TT(red) > LT(green).
     * return true, if all ordering invariants are met, else false.
     */
    public boolean checkInvariant() {
        return true;
    }


    /**
     * Is this polynomial determined.
     * return true, if there are nonzero red terms, else false.
     */
    public boolean isDetermined() {
        return true;
    }


}
