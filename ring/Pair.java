/*
 * $Id$
 */

package edu.jas.ring;

import java.io.Serializable;

import edu.jas.structure.RingElem;

import edu.jas.poly.GenPolynomial;

    /**
     * Serializable subclass to hold pairs of polynomials
     */

public class Pair<C extends RingElem<C> > 
             implements Serializable, Comparable {

    public final GenPolynomial<C> pi;
    public final GenPolynomial<C> pj;
    public final int i;
    public final int j;
    private int n;
    private boolean toZero;


    public Pair(Object a, GenPolynomial<C> b, int i, int j) {
        this( (GenPolynomial<C>)a, b, i, j); 
    }


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
     * toString
     */

    public String toString() {
        return "pair[" + n + "](" + i + "," + j + "," + toZero + ")";
    }


    /**
     * set removed pair number
     */

    public void pairNumber(int n) {
        this.n = n;
    }


    /**
     * get removed pair number
     */

    public int getPairNumber() {
        return n;
    }


    /**
     * set zero reduction
     */

    public void setZero() {
        toZero = true;
    }


    /**
     * is reduced to zero
     */

    public boolean isZero() {
        return toZero;
    }


    /**
     * compareTo used in TreeMap // not used at moment
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
