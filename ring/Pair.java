/*
 * $Id$
 */

package edu.jas.ring;

import java.io.Serializable;

import edu.jas.structure.RingElem;

import edu.jas.poly.GenPolynomial;


/**
 * Serializable subclass to hold pairs of polynomials.
 * @author Heinz Kredel.
 * @param C coefficient factory.
 */
public class Pair<C extends RingElem<C> > 
             implements Serializable, Comparable {

    public final GenPolynomial<C> pi;
    public final GenPolynomial<C> pj;
    public final int i;
    public final int j;
    private int n;
    private boolean toZero;


    /**
     * Pair constructor.
     * @param a polynomial i (must be castable to GenPolynomial&lt;C&gt;).
     * @param b polynomial j.
     * @param i first index.
     * @param j second index.
     */
    public Pair(Object a, GenPolynomial<C> b, int i, int j) {
        this( (GenPolynomial<C>)a, b, i, j); 
    }


    /**
     * Pair constructor.
     * @param a polynomial i.
     * @param b polynomial j.
     * @param i first index.
     * @param j second index.
     */
    public Pair(GenPolynomial<C> a, GenPolynomial<C> b, 
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
    public String toString() {
        return "pair[" + n + "](" + i + "," + j + "," + toZero + ")";
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
     * compareTo used in TreeMap // not used at moment.
     * Comparison is based on the number of the pairs.
     * @return 1 if (this &lt; o), 0 if (this == o), -1 if (this &gt; o).
     */
    public int compareTo(Object o) throws ClassCastException {
        if ( ! (o instanceof Pair) ) {
            throw new ClassCastException("Pair "+n+" o "+o);
        }
        int x = ((Pair)o).getPairNumber();
        if ( n > x ) { 
           return 1;
        }
        if ( n < x ) { 
           return -1;
        }
        return 0;
    }


    /**
     * what is this for?
     */
    public MiniPair toMiniPair() {
        return new MiniPair(i,j);
    }

}
