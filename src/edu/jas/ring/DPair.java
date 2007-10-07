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

    protected boolean useCriterion4 = true;
    protected boolean useCriterion3 = true;

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


    /**
     * toString.
     */
    public String toString() {
        return super.toString() + " useC4 = " + useCriterion4 
                                + " useC3 = " + useCriterion3;
    }


    /**
     * Set useCriterion4.
     * @param b boolean value to set.
     */
    public void setUseCriterion4(boolean c) {
        this.useCriterion4 = c;
    }


    /**
     * Get useCriterion4.
     * @return boolean value.
     */
    public boolean getUseCriterion4() {
        return this.useCriterion4;
    }


    /**
     * Set useCriterion3.
     * @param b boolean value to set.
     */
    public void setUseCriterion3(boolean c) {
        this.useCriterion3 = c;
    }


    /**
     * Get useCriterion3.
     * @return boolean value.
     */
    public boolean getUseCriterion3() {
        return this.useCriterion3;
    }

}
