/*
 * $Id$
 */

package edu.jas.application;

import java.io.Serializable;

import edu.jas.structure.RingElem;


/**
 * Serializable subclass to hold pairs of colored polynomials.
 * @param <C> coefficient type
 * @author Heinz Kredel.
 */
public class CPair<C extends RingElem<C> > 
             implements Serializable, Comparable<CPair> {

    public final ColorPolynomial<C> pi;
    public final ColorPolynomial<C> pj;
    public final int i;
    public final int j;
    protected int n;
    protected boolean toZero = false;
    protected boolean useCriterion4 = true;
    protected boolean useCriterion3 = true;


    /**
     * Pair constructor.
     * @param a polynomial i.
     * @param b polynomial j.
     * @param i first index.
     * @param j second index.
     */
    public CPair(ColorPolynomial<C> a, ColorPolynomial<C> b, 
                 int i, int j) {
        pi = a; 
        pj = b; 
        this.i = i; 
        this.j = j;
        this.n = 0;
        toZero = false; // ok
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        return "pair[" + n + "](" + i + "{" + pi.length() + "}," 
                           + j + "{" + pj.length() + "}"
                           + ", r0=" + toZero  
                           + ", c4=" + useCriterion4  
                           + ", c3=" + useCriterion3 
                           + ")";
    }


    /**
     * Set removed pair number.
     * @param n number of this pair generated in OrderedPairlist.
     */
    public void pairNumber(int n) {
        this.n = n;
    }


    /**
     * Get removed pair number.
     * @return n number of this pair generated in OrderedPairlist.
     */
    public int getPairNumber() {
        return n;
    }


    /**
     * Set zero reduction.
     * The S-polynomial of this Pair was reduced to zero.
     */
    public void setZero() {
        toZero = true;
    }


    /**
     * Is reduced to zero.
     * @return true if the S-polynomial of this Pair was reduced to zero, else false.
     */
    public boolean isZero() {
        return toZero;
    }


    /**
     * equals.
     * @param ob an Object.
     * @return true if this is equal to o, else false.
     */
    @Override
    public boolean equals(Object ob) {
        if ( ! (ob instanceof CPair) ) {
           return false;
           // throw new ClassCastException("Pair "+n+" o "+o);
        }
        return 0 == compareTo( (CPair)ob );
    }


    /**
     * compareTo used in TreeMap // not used at moment.
     * Comparison is based on the number of the pairs.
     * @param p a Pair.
     * @return 1 if (this &lt; o), 0 if (this == o), -1 if (this &gt; o).
     */
    @Override
    public int compareTo(CPair p) {
        int x = p.getPairNumber();
        if ( n > x ) { 
           return 1;
        }
        if ( n < x ) { 
           return -1;
        }
        return 0;
    }


    /** Hash code for this pair.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() { 
        int h;
        h = getPairNumber();
        return h;
    }


    /**
     * Set useCriterion4.
     * @param c boolean value to set.
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
     * @param c boolean value to set.
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
