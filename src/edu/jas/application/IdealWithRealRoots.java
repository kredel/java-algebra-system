/*
 * $Id$
 */

package edu.jas.application;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.arith.Rational;
import edu.jas.arith.BigDecimal;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;


/**
 * Container for Ideals together with univariate polynomials and real roots.
 * @author Heinz Kredel
 */
public class IdealWithRealRoots<C extends GcdRingElem<C>> extends IdealWithUniv<C> implements Serializable {


    /**
     * The list of real roots.
     */
    public final List<List<BigDecimal>> rroots;


    /**
     * Constructor not for use.
     */
    protected IdealWithRealRoots() {
        throw new IllegalArgumentException("do not use this constructor");
    }


    /**
     * Constructor.
     * @param id the ideal
     * @param up the list of univaraite polynomials
     * @param rr the list of real roots
     */
    public IdealWithRealRoots(Ideal<C> id, List<GenPolynomial<C>> up, 
                              List<List<BigDecimal>> rr) {
        super(id,up);
        rroots = rr;
    }


    /**
     * Constructor.
     * @param iu the ideal with univariate polynomials
     * @param rr the list of real roots
     */
    public IdealWithRealRoots(IdealWithUniv<C> iu, List<List<BigDecimal>> rr) {
        super(iu.ideal,iu.upolys);
        rroots = rr;
    }


    /**
     * String representation of the ideal.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return super.toString() + "\nreal roots: " + rroots.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    public String toScript() {
        // Python case
        return super.toScript() +  ",  " + rroots.toString();
    }

}
