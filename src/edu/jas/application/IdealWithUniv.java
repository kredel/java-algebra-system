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
     * The list of other useful polynomials, e.g. generators or inifinite quotients.
     */
    public final List<GenPolynomial<C>> others;


    /**
     * Constructor not for use.
     */
    protected IdealWithUniv() {
        throw new IllegalArgumentException("do not use this constructor");
    }


    /**
     * Constructor.
     * @param id the ideal
     * @param up the list of univariate polynomials
     */
    protected IdealWithUniv(Ideal<C> id, List<GenPolynomial<C>> up) {
        this(id,up,null);
    }


    /**
     * Constructor.
     * @param id the ideal
     * @param up the list of univariate polynomials
     * @param og the list of other polynomials
     */
    protected IdealWithUniv(Ideal<C> id, List<GenPolynomial<C>> up, List<GenPolynomial<C>> og) {
        ideal = id;
        upolys = up;
        others = og;
    }


    /**
     * String representation of the ideal.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String s = ideal.toString() + "\nunivariate polynomials:\n" + upolys.toString();
        if ( others == null ) {
            return s;
        } else {
            return s + "\nother polynomials:\n" + others.toString();
        }
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    public String toScript() {
        // Python case
        String s = ideal.toScript() + ",  " + upolys.toString();
        if ( others == null ) {
            return s;
        } else {
            return s + ", " + others.toString();
        }
    }

}
