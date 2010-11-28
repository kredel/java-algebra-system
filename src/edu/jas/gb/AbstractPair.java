/*
 * $Id$
 */

package edu.jas.gb;

import java.io.Serializable;

import edu.jas.structure.RingElem;

import edu.jas.poly.GenPolynomial;


/**
 * Serializable abstract subclass to hold pairs of polynomials.
 * @param <C> coefficient type
 * @author Heinz Kredel.
 */
public abstract class AbstractPair<C extends RingElem<C> > 
                      implements Serializable {

    public final GenPolynomial<C> pi;
    public final GenPolynomial<C> pj;
    public final int i;
    public final int j;


    /**
     * AbstarctPair constructor.
     * @param a polynomial i.
     * @param b polynomial j.
     * @param i first index.
     * @param j second index.
     */
    public AbstractPair(GenPolynomial<C> a, GenPolynomial<C> b, 
                        int i, int j) {
        pi = a; 
        pj = b; 
        this.i = i; 
        this.j = j;
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        return "pair(" + i + "{" + pi.length() + "}," 
                       + j + "{" + pj.length() + "}"
                           + ")";
    }

}
