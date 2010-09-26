/*
 * $Id$
 */

package edu.jas.ps;


import java.util.List;

import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.RingElem;


/**
 * Polynomial functions capable for Taylor series expansion.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public class PolynomialTaylorFunction<C extends RingElem<C>> implements TaylorFunction<C> {


    final GenPolynomial<C> pol;


    public PolynomialTaylorFunction(GenPolynomial<C> p) {
	pol = p;
    }


    /**
     * To String.
     * @return string representation of this.
     */
    @Override
    public String toString() {
        return pol.toString();
    }


    /**
     * Deriviative.
     * @return deriviative of this.
     */
    @Override
    public TaylorFunction<C> deriviative() {
	return new PolynomialTaylorFunction<C>(PolyUtil. <C> baseDeriviative(pol)) { 
	};
    }


    /**
     * Partial deriviative.
     * @param r index of the variable.
     * @return partial deriviative of this with respect to variable r.
     */
    public TaylorFunction<C> deriviative(int r) {
	throw new UnsupportedOperationException("not implemented");
    }


    /**
     * Evaluate.
     * @param a element.
     * @return this(a).
     */
    @Override
    public C evaluate(C a) {
	return PolyUtil. <C> evaluateMain(pol.ring.coFac,pol,a);
    }


    /**
     * Evaluate at a given variable.
     * @param a element.
     * @param r index of the variable.
     * @return this_r(a).
     */
    public TaylorFunction<C> evaluate(C a, int r) {
	throw new UnsupportedOperationException("not implemented");
    }


    /**
     * Evaluate at a tuple of elements.
     * @param a tuple of elements.
     * @return this(a).
     */
    public C evaluate(List<C> a) {
	throw new UnsupportedOperationException("not implemented");
    }

}
