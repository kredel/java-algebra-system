/*
 * $Id$
 */

package edu.jas.application;


import java.io.Serializable;
import java.util.List;

import edu.jas.poly.GenPolynomial;
import edu.jas.structure.GcdRingElem;


/**
 * Container for Ideals together with univariate polynomials.
 * @author Heinz Kredel
 */
public class IdealWithUniv<C extends GcdRingElem<C>> implements Serializable {


    /**
     * The ideal.
     */
    public final Ideal<C> ideal;


    /**
     * The list of univariate polynomials.
     */
    public final List<GenPolynomial<C>> upolys;


    /**
     * Constructor not for use.
     */
    protected IdealWithUniv() {
        throw new IllegalArgumentException("do not use this constructor");
    }


    /**
     * Constructor.
     * @param id the ideal
     * @param up the list of univaraite polynomials
     */
    protected IdealWithUniv(Ideal<C> id, List<GenPolynomial<C>> up) {
        ideal = id;
        upolys = up;
    }


    /**
     * String representation of the ideal.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ideal.toString() + "\nunivariate polynomials:\n" + upolys.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    public String toScript() {
        // Python case
        return ideal.toScript() + ",  " + upolys.toString();
    }

}
