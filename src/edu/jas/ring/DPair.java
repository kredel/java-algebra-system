/*
 * $Id$
 */

package edu.jas.ring;

import java.io.Serializable;

import edu.jas.structure.RingElem;

import edu.jas.poly.GenPolynomial;


/**
 * Serializable subclass to hold pairs of polynomials.
 * For d-Groebner bases.
 * @author Heinz Kredel.
 * @typeparam C coefficient factory.
 */
public class DPair<C extends RingElem<C> > 
             extends Pair<C>
             /*implements Serializable, Comparable<Pair>*/ {

    /**
     * DPair constructor.
     * @param a polynomial i (must be castable to GenPolynomial&lt;C&gt;).
     * @param b polynomial j.
     * @param i first index.
     * @param j second index.
     */
    public DPair(Object a, GenPolynomial<C> b, int i, int j) {
        this( (GenPolynomial<C>)a, b, i, j); 
    }


    /**
     * DPair constructor.
     * @param a polynomial i.
     * @param b polynomial j.
     * @param i first index.
     * @param j second index.
     */
    public DPair(GenPolynomial<C> a, GenPolynomial<C> b, 
                 int i, int j) {
        super(a,b,i,j);
    }


    /**
     * DPair constructor.
     * @param p Pair.
     */
    public DPair(Pair<C> p) {
        super(p.pi, p.pj, p.i, p.j);
        pairNumber( p.getPairNumber() );
        if ( p.isZero() ) {
           setZero();
        }
    }

}
